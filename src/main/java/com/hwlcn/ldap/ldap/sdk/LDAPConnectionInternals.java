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



import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

import com.hwlcn.ldap.asn1.ASN1Buffer;
import com.hwlcn.ldap.ldap.protocol.LDAPMessage;
import com.hwlcn.ldap.util.DebugType;
import com.hwlcn.ldap.util.InternalUseOnly;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class is used to hold references to the elements involved in network
 * communication for an LDAP connection.
 */
@InternalUseOnly()
final class LDAPConnectionInternals
{
  // The counter that will be used to obtain the next message ID to use when
  // sending requests to the server.
  private final AtomicInteger nextMessageID;

  // Indicates whether to operate in synchronous mode.
  private final boolean synchronousMode;

  // The port of the server to which the connection is established.
  private final int port;

  // The time that this connection was established.
  private final long connectTime;

  // The LDAP connection with which this connection internals is associated.
  private final LDAPConnection connection;

  // The LDAP connection reader with which this connection internals is
  // associated.
  private final LDAPConnectionReader connectionReader;

  // The output stream used to send requests to the server.
  private volatile OutputStream outputStream;

  // The socket used to communicate with the directory server.
  private final Socket socket;

  // The address of the server to which the connection is established.
  private final String host;

  // The thread-local ASN.1 buffer used for writing elements.
  private static final ThreadLocal<ASN1Buffer> asn1Buffers =
       new ThreadLocal<ASN1Buffer>();



  /**
   * Creates a new instance of this object.
   *
   * @param  connection     The LDAP connection created with this connection
   *                        internals object.
   * @param  options        The set of options for the connection.
   * @param  socketFactory  The socket factory to use to create the socket.
   * @param  host           The address of the server to which the connection
   *                        should be established.
   * @param  port           The port of the server to which the connection
   *                        should be established.
   * @param  timeout        The maximum length of time in milliseconds to wait
   *                        for the connection to be established before failing,
   *                        or zero to indicate that no timeout should be
   *                        enforced (although if the attempt stalls long
   *                        enough, then the underlying operating system may
   *                        cause it to timeout).
   *
   * @throws  java.io.IOException  If a problem occurs while establishing the
   *                       connection.
   */
  LDAPConnectionInternals(final LDAPConnection connection,
                          final LDAPConnectionOptions options,
                          final SocketFactory socketFactory, final String host,
                          final int port, final int timeout)
       throws IOException

  {
    this.connection = connection;
    this.host       = host;
    this.port       = port;

    if (options.captureConnectStackTrace())
    {
      connection.setConnectStackTrace(Thread.currentThread().getStackTrace());
    }

    connectTime               = System.currentTimeMillis();
    nextMessageID             = new AtomicInteger(0);
    synchronousMode           = options.useSynchronousMode();

    try
    {
      final ConnectThread connectThread =
           new ConnectThread(socketFactory, host, port);
      connectThread.start();
      socket = connectThread.getConnectedSocket(timeout);
    }
    catch (LDAPException le)
    {
      debugException(le);
      throw new IOException(le.getMessage());
    }

    if (options.getReceiveBufferSize() > 0)
    {
      socket.setReceiveBufferSize(options.getReceiveBufferSize());
    }

    if (options.getSendBufferSize() > 0)
    {
      socket.setSendBufferSize(options.getSendBufferSize());
    }

    try
    {
      debugConnect(host, port, connection);
      socket.setKeepAlive(options.useKeepAlive());
      socket.setReuseAddress(options.useReuseAddress());
      socket.setSoLinger(options.useLinger(),
                         options.getLingerTimeoutSeconds());
      socket.setTcpNoDelay(options.useTCPNoDelay());

      outputStream     = socket.getOutputStream();
      connectionReader = new LDAPConnectionReader(connection, this);
    }
    catch (IOException ioe)
    {
      debugException(ioe);
      try
      {
        socket.close();
      }
      catch (Exception e)
      {
        debugException(e);
      }

      throw ioe;
    }
  }



  /**
   * Starts the connection reader for this connection internals.  This will
   * have no effect if the connection is operating in synchronous mode.
   */
  void startConnectionReader()
  {
    if (! synchronousMode)
    {
      connectionReader.start();
    }
  }



  /**
   * Retrieves the LDAP connection with which this connection internals object
   * is associated.
   *
   * @return  The LDAP connection with which this connection internals object is
   *          associated.
   */
  LDAPConnection getConnection()
  {
    return connection;
  }



  /**
   * Retrieves the LDAP connection reader used to read responses from the
   * server.
   *
   * @return  The LDAP connection reader used to read responses from the server,
   *          or {@code null} if the connection is operating in synchronous mode
   *          and is not using a connection reader.
   */
  LDAPConnectionReader getConnectionReader()
  {
    return connectionReader;
  }



  /**
   * Retrieves the address of the server to which this connection is
   * established.
   *
   * @return  The address of the server to which this connection is established.
   */
  String getHost()
  {
    return host;
  }



  /**
   * Retrieves the port of the server to which this connection is established.
   *
   * @return  The port of the server to which this connection is established.
   */
  int getPort()
  {
    return port;
  }



  /**
   * Retrieves the socket used to communicate with the directory server.
   *
   * @return  The socket used to communicate with the directory server.
   */
  Socket getSocket()
  {
    return socket;
  }



  /**
   * Retrieves the output stream used to send requests to the server.
   *
   * @return  The output stream used to send requests to the server.
   */
  OutputStream getOutputStream()
  {
    return outputStream;
  }



  /**
   * Indicates whether the socket is currently connected.
   *
   * @return  {@code true} if the socket is currently connected, or
   *          {@code false} if not.
   */
  boolean isConnected()
  {
    return socket.isConnected();
  }



  /**
   * Indicates whether this connection is operating in synchronous mode.
   *
   * @return  {@code true} if this connection is operating in synchronous mode,
   *          or {@code false} if not.
   */
  boolean synchronousMode()
  {
    return synchronousMode;
  }



  /**
   * Converts this clear-text connection to one that encrypts all communication
   * using Transport Layer Security.  This method is intended for use as a
   * helper for processing in the course of the StartTLS extended operation and
   * should not be used for other purposes.
   *
   * @param  sslContext  The SSL context to use when performing the negotiation.
   *                     It must not be {@code null}.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while converting this
   *                         connection to use TLS.
   */
  void convertToTLS(final SSLContext sslContext)
       throws LDAPException
  {
    outputStream = connectionReader.doStartTLS(sslContext);
  }


  /**
   * Retrieves the message ID that should be used for the next message to send
   * to the directory server.
   *
   * @return  The message ID that should be used for the next message to send to
   *          the directory server.
   */
  int nextMessageID()
  {
    int msgID = nextMessageID.incrementAndGet();
    if (msgID > 0)
    {
      return msgID;
    }

    while (true)
    {
      if (nextMessageID.compareAndSet(msgID, 1))
      {
        return 1;
      }

      msgID = nextMessageID.incrementAndGet();
      if (msgID > 0)
      {
        return msgID;
      }
    }
  }



  /**
   * Registers the provided response acceptor with the connection reader.
   *
   * @param  messageID         The message ID for which the acceptor is to be
   *                           registered.
   * @param  responseAcceptor  The response acceptor to register.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If another response acceptor is already registered
   *                         with the provided message ID.
   */
  void registerResponseAcceptor(final int messageID,
                                final ResponseAcceptor responseAcceptor)
       throws LDAPException
  {
    if (! isConnected())
    {
      final LDAPConnectionOptions connectionOptions =
           connection.getConnectionOptions();
      final boolean closeRequested = connection.closeRequested();
      if (connectionOptions.autoReconnect() && (! closeRequested))
      {
        connection.reconnect();
        connection.registerResponseAcceptor(messageID,  responseAcceptor);
      }
      else
      {
        throw new LDAPException(ResultCode.SERVER_DOWN,
                                ERR_CONN_NOT_ESTABLISHED.get());
      }
    }

    connectionReader.registerResponseAcceptor(messageID, responseAcceptor);
  }



  /**
   * Deregisters the response acceptor associated with the provided message ID.
   *
   * @param  messageID  The message ID for which to deregister the associated
   *                    response acceptor.
   */
  void deregisterResponseAcceptor(final int messageID)
  {
    connectionReader.deregisterResponseAcceptor(messageID);
  }



  /**
   * Sends the provided LDAP message to the directory server.
   *
   * @param  message     The LDAP message to be sent.
   * @param  allowRetry  Indicates whether to allow retrying the send after a
   *                     reconnect.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while sending the message.
   */
  void sendMessage(final LDAPMessage message, final boolean allowRetry)
       throws LDAPException
  {
    if (! isConnected())
    {
      throw new LDAPException(ResultCode.SERVER_DOWN,
                              ERR_CONN_NOT_ESTABLISHED.get());
    }

    ASN1Buffer buffer = asn1Buffers.get();
    if (buffer == null)
    {
      buffer = new ASN1Buffer();
      asn1Buffers.set(buffer);
    }

    buffer.clear();
    try
    {
      message.writeTo(buffer);
    }
    catch (final LDAPRuntimeException lre)
    {
      debugException(lre);
      lre.throwLDAPException();
    }

    try
    {
      final OutputStream os = outputStream;
      buffer.writeTo(os);
      os.flush();
    }
    catch (IOException ioe)
    {
      debugException(ioe);

      // If the message was an unbind request, then we don't care that it
      // didn't get sent.  Otherwise, fail the send attempt but try to reconnect
      // first if appropriate.
      if (message.getProtocolOpType() ==
          LDAPMessage.PROTOCOL_OP_TYPE_UNBIND_REQUEST)
      {
        return;
      }

      final LDAPConnectionOptions connectionOptions =
           connection.getConnectionOptions();
      final boolean closeRequested = connection.closeRequested();
      if (allowRetry && (! closeRequested) && (! connection.synchronousMode()))
      {
        connection.reconnect();

        try
        {
          sendMessage(message, false);
          return;
        }
        catch (final Exception e)
        {
          debugException(e);
        }
      }

      throw new LDAPException(ResultCode.SERVER_DOWN,
           ERR_CONN_SEND_ERROR.get(host + ':' + port, getExceptionMessage(ioe)),
           ioe);
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.LOCAL_ERROR,
           ERR_CONN_ENCODE_ERROR.get(host + ':' + port, getExceptionMessage(e)),
           e);
    }
    finally
    {
      if (buffer.zeroBufferOnClear())
      {
        buffer.clear();
      }
    }
  }



  /**
   * Closes the connection associated with this connection internals.
   */
  void close()
  {
    DisconnectInfo disconnectInfo = connection.getDisconnectInfo();
    if (disconnectInfo == null)
    {
      disconnectInfo = connection.setDisconnectInfo(
           new DisconnectInfo(connection, DisconnectType.UNKNOWN, null, null));
    }

    // Determine if this connection was closed by a finalizer.
    final boolean closedByFinalizer =
         ((disconnectInfo.getType() == DisconnectType.CLOSED_BY_FINALIZER) &&
          socket.isConnected());


    // Make sure that the connection reader is no longer running.
    try
    {
      connectionReader.close(false);
    }
    catch (Exception e)
    {
      debugException(e);
    }

    try
    {
      outputStream.close();
    }
    catch (Exception e)
    {
      debugException(e);
    }

    try
    {
      socket.close();
    }
    catch (Exception e)
    {
      debugException(e);
    }

    debugDisconnect(host, port, connection, disconnectInfo.getType(),
         disconnectInfo.getMessage(), disconnectInfo.getCause());
    if (closedByFinalizer && debugEnabled(DebugType.LDAP))
    {
      debug(Level.WARNING, DebugType.LDAP,
            "Connection closed by LDAP SDK finalizer:  " + toString());
    }
    disconnectInfo.notifyDisconnectHandler();
  }



  /**
   * Retrieves the time that the connection was established.
   *
   * @return  The time that the connection was established, or -1 if the
   *          connection is not established.
   */
  public long getConnectTime()
  {
    if (isConnected())
    {
      return connectTime;
    }
    else
    {
      return -1L;
    }
  }



  /**
   * Retrieves a string representation of this connection internals object.
   *
   * @return  A string representation of this connection internals object.
   */
  @Override()
  public String toString()
  {
    final StringBuilder buffer = new StringBuilder();
    toString(buffer);
    return buffer.toString();
  }



  /**
   * Appends a string representation of this connection internals object to the
   * provided buffer.
   *
   * @param  buffer  The buffer to which the information should be appended.
   */
  public void toString(final StringBuilder buffer)
  {
    buffer.append("LDAPConnectionInternals(host='");
    buffer.append(host);
    buffer.append("', port=");
    buffer.append(port);
    buffer.append(", connected=");
    buffer.append(socket.isConnected());
    buffer.append(", nextMessageID=");
    buffer.append(nextMessageID.get());
    buffer.append(')');
  }
}
