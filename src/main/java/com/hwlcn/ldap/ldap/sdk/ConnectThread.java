/*
 * Copyright 2009-2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009-2013 UnboundID Corp.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.hwlcn.ldap.ldap.sdk;



import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.SocketFactory;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides a thread that may be used to create an establish a
 * socket using a provided socket factory with a specified timeout.  This
 * provides a more reliable mechanism for attempting to establish a connection
 * with a timeout than using the {@code Socket.connect} method that takes a
 * timeout because this method cannot be used with some socket factories (like
 * SSL socket factories), and that method is also not reliable for hung servers
 * which are listening for connections but are not responsive.  The
 * {@link #getConnectedSocket} method should be called immediately after
 * starting the thread to wait for the connection to be established, or to fail
 * if it cannot be successfully established within the given timeout period.
 */
final class ConnectThread
      extends Thread
{
  // Indicates whether the connection has been successfully established.
  private final AtomicBoolean connected;

  // Indicates whether the thread has been started.
  private final AtomicBoolean started;

  // The socket used for the connection.
  private final AtomicReference<Socket> socket;

  // The thread being used to establish the connection.
  private final AtomicReference<Thread> thread;

  // The exception caught while trying to establish the connection.
  private final AtomicReference<Throwable> exception;

  // The port to which the connection should be established.
  private final int port;

  // The socket factory that will be used to create the connection.
  private final SocketFactory socketFactory;

  // The address to which the connection should be established.
  private final String address;



  /**
   * Creates a new instance of this connect thread with the provided
   * information.
   *
   * @param  socketFactory  The socket factory to use to create the socket.
   * @param  address        The address to which the connection should be
   *                        established.
   * @param  port           The port to which the connection should be
   *                        established.
   */
  ConnectThread(final SocketFactory socketFactory, final String address,
                final int port)
  {
    super("Background connect thread for " + address + ':' + port);
    setDaemon(true);

    this.socketFactory = socketFactory;
    this.address       = address;
    this.port          = port;

    connected = new AtomicBoolean(false);
    started   = new AtomicBoolean(false);
    socket    = new AtomicReference<Socket>();
    thread    = new AtomicReference<Thread>();
    exception = new AtomicReference<Throwable>();
  }



  /**
   * Attempts to establish the connection.
   */
  @Override()
  public void run()
  {
    thread.set(Thread.currentThread());
    started.set(true);

    try
    {
      socket.set(socketFactory.createSocket(address, port));
      connected.set(true);
    }
    catch (final Throwable t)
    {
      debugException(t);
      exception.set(t);
    }
    finally
    {
      thread.set(null);
    }
  }



  /**
   * Gets the connection after it has been established.  This should be called
   * immediately after starting the thread.
   *
   * @param  timeoutMillis  The maximum length of time in milliseconds to wait
   *                        for the connection to be established.  It may be
   *                        zero if no timeout should be used.
   *
   * @return  The socket that has been connected to the target server.
   *
   * @throws  LDAPException  If a problem occurs while attempting to establish
   *                         the connection, or if it cannot be established
   *                         within the specified time limit.
   */
  Socket getConnectedSocket(final long timeoutMillis)
         throws LDAPException
  {
    while (! started.get())
    {
      Thread.yield();
    }

    final Thread t = thread.get();
    if (t != null)
    {
      try
      {
        t.join(timeoutMillis);
      }
      catch (Exception e)
      {
        debugException(e);
      }
    }

    if (connected.get())
    {
      return socket.get();
    }

    try
    {
      if (t != null)
      {
        t.interrupt();
      }
    }
    catch (final Exception e)
    {
      debugException(e);
    }

    try
    {
      final Socket s = socket.get();
      if (s != null)
      {
        s.close();
      }
    }
    catch (final Exception e)
    {
      debugException(e);
    }

    final Throwable cause = exception.get();
    if (cause == null)
    {
      throw new LDAPException(ResultCode.CONNECT_ERROR,
           ERR_CONNECT_THREAD_TIMEOUT.get(address, port, timeoutMillis));
    }
    else
    {
      throw new LDAPException(ResultCode.CONNECT_ERROR,
           ERR_CONNECT_THREAD_EXCEPTION.get(address, port,
                getExceptionMessage(cause)), cause);
    }
  }
}
