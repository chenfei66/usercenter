/*
 * Copyright 2008-2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2013 UnboundID Corp.
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



import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.SocketFactory;

import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class provides a server set implementation that will attempt to
 * establish connections to servers in the order they are provided.  If the
 * first server is unavailable, then it will attempt to connect to the second,
 * then to the third, etc.  Note that this implementation also makes it possible
 * to use failover between distinct server sets, which means that it will first
 * attempt to obtain a connection from the first server set and if all attempts
 * fail, it will proceed to the second set, and so on.  This can provide a
 * significant degree of flexibility in complex environments (e.g., first use a
 * round robin server set containing servers in the local data center, but if
 * none of those are available then fail over to a server set with servers in a
 * remote data center).
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the process for creating a failover server
 * set with information about individual servers.  It will first try to connect
 * to ds1.example.com:389, but if that fails then it will try connecting to
 * ds2.example.com:389:
 * <PRE>
 *   String[] addresses =
 *   {
 *     "ds1.example.com",
 *     "ds2.example.com"
 *   };
 *   int[] ports =
 *   {
 *     389,
 *     389
 *   };
 *   FailoverServerSet failoverSet = new FailoverServerSet(addresses, ports);
 * </PRE>
 * This second example demonstrates the process for creating a failover server
 * set which actually fails over between two different data centers (east and
 * west), with each data center containing two servers that will be accessed in
 * a round-robin manner.  It will first try to connect to one of the servers in
 * the east data center, and if that attempt fails then it will try to connect
 * to the other server in the east data center.  If both of them fail, then it
 * will try to connect to one of the servers in the west data center, and
 * finally as a last resort the other server in the west data center:
 * <PRE>
 *   String[] eastAddresses =
 *   {
 *     "ds-east-1.example.com",
 *     "ds-east-2.example.com",
 *   };
 *   int[] eastPorts =
 *   {
 *     389,
 *     389
 *   }
 *   RoundRobinServerSet eastSet =
 *        new RoundRobinServerSet(eastAddresses, eastPorts);
 *
 *   String[] westAddresses =
 *   {
 *     "ds-west-1.example.com",
 *     "ds-west-2.example.com",
 *   };
 *   int[] westPorts =
 *   {
 *     389,
 *     389
 *   }
 *   RoundRobinServerSet westSet =
 *        new RoundRobinServerSet(westAddresses, westPorts);
 *
 *   FailoverServerSet failoverSet = new FailoverServerSet(eastSet, westSet);
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FailoverServerSet
       extends ServerSet
{
  // Indicates whether to re-order the server set list if failover occurs.
  private final AtomicBoolean reOrderOnFailover;

  // The server sets for which we will allow failover.
  private final ServerSet[] serverSets;



  /**
   * Creates a new failover server set with the specified set of directory
   * server addresses and port numbers.  It will use the default socket factory
   * provided by the JVM to create the underlying sockets.
   *
   * @param  addresses  The addresses of the directory servers to which the
   *                    connections should be established.  It must not be
   *                    {@code null} or empty.
   * @param  ports      The ports of the directory servers to which the
   *                    connections should be established.  It must not be
   *                    {@code null}, and it must have the same number of
   *                    elements as the {@code addresses} array.  The order of
   *                    elements in the {@code addresses} array must correspond
   *                    to the order of elements in the {@code ports} array.
   */
  public FailoverServerSet(final String[] addresses, final int[] ports)
  {
    this(addresses, ports, null, null);
  }



  /**
   * Creates a new failover server set with the specified set of directory
   * server addresses and port numbers.  It will use the default socket factory
   * provided by the JVM to create the underlying sockets.
   *
   * @param  addresses          The addresses of the directory servers to which
   *                            the connections should be established.  It must
   *                            not be {@code null} or empty.
   * @param  ports              The ports of the directory servers to which the
   *                            connections should be established.  It must not
   *                            be {@code null}, and it must have the same
   *                            number of elements as the {@code addresses}
   *                            array.  The order of elements in the
   *                            {@code addresses} array must correspond to the
   *                            order of elements in the {@code ports} array.
   * @param  connectionOptions  The set of connection options to use for the
   *                            underlying connections.
   */
  public FailoverServerSet(final String[] addresses, final int[] ports,
                           final LDAPConnectionOptions connectionOptions)
  {
    this(addresses, ports, null, connectionOptions);
  }



  /**
   * Creates a new failover server set with the specified set of directory
   * server addresses and port numbers.  It will use the provided socket factory
   * to create the underlying sockets.
   *
   * @param  addresses      The addresses of the directory servers to which the
   *                        connections should be established.  It must not be
   *                        {@code null} or empty.
   * @param  ports          The ports of the directory servers to which the
   *                        connections should be established.  It must not be
   *                        {@code null}, and it must have the same number of
   *                        elements as the {@code addresses} array.  The order
   *                        of elements in the {@code addresses} array must
   *                        correspond to the order of elements in the
   *                        {@code ports} array.
   * @param  socketFactory  The socket factory to use to create the underlying
   *                        connections.
   */
  public FailoverServerSet(final String[] addresses, final int[] ports,
                           final SocketFactory socketFactory)
  {
    this(addresses, ports, socketFactory, null);
  }



  /**
   * Creates a new failover server set with the specified set of directory
   * server addresses and port numbers.  It will use the provided socket factory
   * to create the underlying sockets.
   *
   * @param  addresses          The addresses of the directory servers to which
   *                            the connections should be established.  It must
   *                            not be {@code null} or empty.
   * @param  ports              The ports of the directory servers to which the
   *                            connections should be established.  It must not
   *                            be {@code null}, and it must have the same
   *                            number of elements as the {@code addresses}
   *                            array.  The order of elements in the
   *                            {@code addresses} array must correspond to the
   *                            order of elements in the {@code ports} array.
   * @param  socketFactory      The socket factory to use to create the
   *                            underlying connections.
   * @param  connectionOptions  The set of connection options to use for the
   *                            underlying connections.
   */
  public FailoverServerSet(final String[] addresses, final int[] ports,
                           final SocketFactory socketFactory,
                           final LDAPConnectionOptions connectionOptions)
  {
    ensureNotNull(addresses, ports);
    ensureTrue(addresses.length > 0,
               "FailoverServerSet.addresses must not be empty.");
    ensureTrue(addresses.length == ports.length,
         "FailoverServerSet addresses and ports arrays must be the same size.");

    reOrderOnFailover = new AtomicBoolean(false);

    final SocketFactory sf;
    if (socketFactory == null)
    {
      sf = SocketFactory.getDefault();
    }
    else
    {
      sf = socketFactory;
    }

    final LDAPConnectionOptions co;
    if (connectionOptions == null)
    {
      co = new LDAPConnectionOptions();
    }
    else
    {
      co = connectionOptions;
    }


    serverSets = new ServerSet[addresses.length];
    for (int i=0; i < serverSets.length; i++)
    {
      serverSets[i] = new SingleServerSet(addresses[i], ports[i], sf, co);
    }
  }



  /**
   * Creates a new failover server set that will fail over between the provided
   * server sets.
   *
   * @param  serverSets  The server sets between which failover should occur.
   *                     It must not be {@code null} or empty.
   */
  public FailoverServerSet(final ServerSet... serverSets)
  {
    ensureNotNull(serverSets);
    ensureFalse(serverSets.length == 0,
                "FailoverServerSet.serverSets must not be empty.");

    this.serverSets = serverSets;

    reOrderOnFailover = new AtomicBoolean(false);
  }



  /**
   * Creates a new failover server set that will fail over between the provided
   * server sets.
   *
   * @param  serverSets  The server sets between which failover should occur.
   *                     It must not be {@code null} or empty.
   */
  public FailoverServerSet(final List<ServerSet> serverSets)
  {
    ensureNotNull(serverSets);
    ensureFalse(serverSets.isEmpty(),
                "FailoverServerSet.serverSets must not be empty.");

    this.serverSets = new ServerSet[serverSets.size()];
    serverSets.toArray(this.serverSets);

    reOrderOnFailover = new AtomicBoolean(false);
  }



  /**
   * Retrieves the server sets over which failover will occur.  If this failover
   * server set was created from individual servers rather than server sets,
   * then the elements contained in the returned array will be
   * {@code SingleServerSet} instances.
   *
   * @return  The server sets over which failover will occur.
   */
  public ServerSet[] getServerSets()
  {
    return serverSets;
  }



  /**
   * Indicates whether the list of servers or server sets used by this failover
   * server set should be re-ordered in the event that a failure is encountered
   * while attempting to establish a connection.  If {@code true}, then any
   * failed attempt to establish a connection to a server set at the beginning
   * of the list may cause that server/set to be moved to the end of the list so
   * that it will be the last one tried on the next attempt.
   *
   * @return  {@code true} if the order of elements in the associated list of
   *          servers or server sets should be updated if a failure occurs while
   *          attempting to establish a connection, or {@code false} if the
   *          original order should be preserved.
   */
  public boolean reOrderOnFailover()
  {
    return reOrderOnFailover.get();
  }



  /**
   * Specifies whether the list of servers or server sets used by this failover
   * server set should be re-ordered in the event that a failure is encountered
   * while attempting to establish a connection.  By default, the original
   * order will be preserved, but if this method is called with a value of
   * {@code true}, then a failed attempt to establish a connection to the server
   * or server set at the beginning of the list may cause that server to be
   * moved to the end of the list so that it will be the last server/set tried
   * on the next attempt.
   *
   * @param  reOrderOnFailover  Indicates whether the list of servers or server
   *                            sets should be re-ordered in the event that a
   *                            failure is encountered while attempting to
   *                            establish a connection.
   */
  public void setReOrderOnFailover(final boolean reOrderOnFailover)
  {
    this.reOrderOnFailover.set(reOrderOnFailover);
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public LDAPConnection getConnection()
         throws LDAPException
  {
    return getConnection(null);
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public LDAPConnection getConnection(
                             final LDAPConnectionPoolHealthCheck healthCheck)
         throws LDAPException
  {
    if (reOrderOnFailover.get() && (serverSets.length > 1))
    {
      synchronized (this)
      {
        // First, try to get a connection using the first set in the list.  If
        // this succeeds, then we don't need to go any further.
        try
        {
          return serverSets[0].getConnection(healthCheck);
        }
        catch (final LDAPException le)
        {
          debugException(le);
        }

        // If we've gotten here, then we will need to re-order the list unless
        // all other attempts fail.
        int successfulPos = -1;
        LDAPConnection conn = null;
        LDAPException lastException = null;
        for (int i=1; i < serverSets.length; i++)
        {
          try
          {
            conn = serverSets[i].getConnection(healthCheck);
            successfulPos = i;
            break;
          }
          catch (final LDAPException le)
          {
            debugException(le);
            lastException = le;
          }
        }

        if (successfulPos > 0)
        {
          int pos = 0;
          final ServerSet[] setCopy = new ServerSet[serverSets.length];
          for (int i=successfulPos; i < serverSets.length; i++)
          {
            setCopy[pos++] = serverSets[i];
          }

          for (int i=0; i < successfulPos; i++)
          {
            setCopy[pos++] = serverSets[i];
          }

          System.arraycopy(setCopy, 0, serverSets, 0, setCopy.length);
          return conn;
        }
        else
        {
          throw lastException;
        }
      }
    }
    else
    {
      LDAPException lastException = null;

      for (final ServerSet s : serverSets)
      {
        try
        {
          return s.getConnection(healthCheck);
        }
        catch (LDAPException le)
        {
          debugException(le);
          lastException = le;
        }
      }

      throw lastException;
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("FailoverServerSet(serverSets={");

    for (int i=0; i < serverSets.length; i++)
    {
      if (i > 0)
      {
        buffer.append(", ");
      }

      serverSets[i].toString(buffer);
    }

    buffer.append("})");
  }
}
