/*
 * Copyright 2007-2013 UnboundID Corp.
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



import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.hwlcn.ldap.ldap.protocol.LDAPResponse;
import com.hwlcn.ldap.ldap.sdk.schema.Schema;
import com.hwlcn.ldap.util.ObjectPair;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class provides an implementation of an LDAP connection pool, which is a
 * structure that can hold multiple connections established to a given server
 * that can be reused for multiple operations rather than creating and
 * destroying connections for each operation.  This connection pool
 * implementation provides traditional methods for checking out and releasing
 * connections, but it also provides wrapper methods that make it easy to
 * perform operations using pooled connections without the need to explicitly
 * check out or release the connections.
 * <BR><BR>
 * Note that both the {@code LDAPConnectionPool} class and the
 * {@link com.hwlcn.ldap.ldap.sdk.LDAPConnection} class implement the {@link com.hwlcn.ldap.ldap.sdk.LDAPInterface} interface.
 * This is a common interface that defines a number of common methods for
 * processing LDAP requests.  This means that in many cases, an application can
 * use an object of type {@link com.hwlcn.ldap.ldap.sdk.LDAPInterface} rather than
 * {@link com.hwlcn.ldap.ldap.sdk.LDAPConnection}, which makes it possible to work with either a single
 * standalone connection or with a connection pool.
 * <BR><BR>
 * <H2>Creating a Connection Pool</H2>
 * An LDAP connection pool can be created from either a single
 * {@link com.hwlcn.ldap.ldap.sdk.LDAPConnection} (for which an appropriate number of copies will be
 * created to fill out the pool) or using a {@link com.hwlcn.ldap.ldap.sdk.ServerSet} to create
 * connections that may span multiple servers.  For example:
 * <BR><BR>
 * <PRE>
 *   // Create a new LDAP connection pool with ten connections established and
 *   // authenticated to the same server:
 *   LDAPConnection connection = new LDAPConnection(address, port);
 *   BindResult bindResult = connection.bind(bindDN, password);
 *   LDAPConnectionPool connectionPool = new LDAPConnectionPool(connection, 10);
 *
 *   // Create a new LDAP connection pool with 10 connections spanning multiple
 *   // servers using a server set.
 *   RoundRobinServerSet serverSet = new RoundRobinServerSet(addresses, ports);
 *   SimpleBindRequest bindRequest = new SimpleBindRequest(bindDN, password);
 *   LDAPConnectionPool connectionPool =
 *        new LDAPConnectionPool(serverSet, bindRequest, 10);
 * </PRE>
 * Note that in some cases, such as when using StartTLS, it may be necessary to
 * perform some additional processing when a new connection is created for use
 * in the connection pool.  In this case, a {@link PostConnectProcessor} should
 * be provided to accomplish this.  See the documentation for the
 * {@link StartTLSPostConnectProcessor} class for an example that demonstrates
 * its use for creating a connection pool with connections secured using
 * StartTLS.
 * <BR><BR>
 * <H2>Processing Operations with a Connection Pool</H2>
 * If a single operation is to be processed using a connection from the
 * connection pool, then it can be used without the need to check out or release
 * a connection or perform any validity checking on the connection.  This can
 * be accomplished via the {@link com.hwlcn.ldap.ldap.sdk.LDAPInterface} interface that allows a
 * connection pool to be treated like a single connection.  For example, to
 * perform a search using a pooled connection:
 * <PRE>
 *   SearchResult searchResult =
 *        connectionPool.search("dc=example,dc=com", SearchScope.SUB,
 *                              "(uid=john.doe)");
 * </PRE>
 * If an application needs to process multiple operations using a single
 * connection, then it may be beneficial to obtain a connection from the pool
 * to use for processing those operations and then return it back to the pool
 * when it is no longer needed.  This can be done using the
 * {@link #getConnection} and {@link #releaseConnection} methods.  If during
 * processing it is determined that the connection is no longer valid, then the
 * connection should be released back to the pool using the
 * {@link #releaseDefunctConnection} method, which will ensure that the
 * connection is closed and a new connection will be established to take its
 * place in the pool.
 * <BR><BR>
 * Note that it is also possible to process multiple operations on a single
 * connection using the {@link #processRequests} method.  This may be useful if
 * a fixed set of operations should be processed over the same connection and
 * none of the subsequent requests depend upon the results of the earlier
 * operations.
 * <BR><BR>
 * Connection pools should generally not be used when performing operations that
 * may change the state of the underlying connections.  This is particularly
 * true for bind operations and the StartTLS extended operation, but it may
 * apply to other types of operations as well.
 * <BR><BR>
 * Performing a bind operation using a connection from the pool will invalidate
 * any previous authentication on that connection, and if that connection is
 * released back to the pool without first being re-authenticated as the
 * original user, then subsequent operation attempts may fail or be processed in
 * an incorrect manner.  Bind operations should only be performed in a
 * connection pool if the pool is to be used exclusively for processing binds,
 * if the bind request is specially crafted so that it will not change the
 * identity of the associated connection (e.g., by including the retain identity
 * request control in the bind request if using the Commercial Edition of the
 * LDAP SDK with an UnboundID Directory Server), or if the code using the
 * connection pool makes sure to re-authenticate the connection as the
 * appropriate user whenever its identity has been changed.
 * <BR><BR>
 * The StartTLS extended operation should never be invoked on a connection which
 * is part of a connection pool.  It is acceptable for the pool to maintain
 * connections which have been configured with StartTLS security prior to being
 * added to the pool (via the use of the {@link StartTLSPostConnectProcessor}).
 */
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPConnectionPool
       extends AbstractConnectionPool
{
  /**
   * The default health check interval for this connection pool, which is set to
   * 60000 milliseconds (60 seconds).
   */
  private static final long DEFAULT_HEALTH_CHECK_INTERVAL = 60000L;



  // A counter used to keep track of the number of times that the pool failed to
  // replace a defunct connection.  It may also be initialized to the difference
  // between the initial and maximum number of connections that should be
  // included in the pool.
  private final AtomicInteger failedReplaceCount;

  // The types of operations that should be retried if they fail in a manner
  // that may be the result of a connection that is no longer valid.
  private final AtomicReference<Set<OperationType>> retryOperationTypes;

  // Indicates whether this connection pool has been closed.
  private volatile boolean closed;

  // Indicates whether to create a new connection if necessary rather than
  // waiting for a connection to become available.
  private boolean createIfNecessary;

  // Indicates whether health check processing for connections in synchronous
  // mode should include attempting to read with a very short timeout to attempt
  // to detect closures and unsolicited notifications in a more timely manner.
  private volatile boolean trySynchronousReadDuringHealthCheck;

  // The bind request to use to perform authentication whenever a new connection
  // is established.
  private final BindRequest bindRequest;

  // The number of connections to be held in this pool.
  private final int numConnections;

  // The health check implementation that should be used for this connection
  // pool.
  private LDAPConnectionPoolHealthCheck healthCheck;

  // The thread that will be used to perform periodic background health checks
  // for this connection pool.
  private final LDAPConnectionPoolHealthCheckThread healthCheckThread;

  // The statistics for this connection pool.
  private final LDAPConnectionPoolStatistics poolStatistics;

  // The set of connections that are currently available for use.
  private final LinkedBlockingQueue<LDAPConnection> availableConnections;

  // The length of time in milliseconds between periodic health checks against
  // the available connections in this pool.
  private volatile long healthCheckInterval;

  // The time that the last expired connection was closed.
  private volatile long lastExpiredDisconnectTime;

  // The maximum length of time in milliseconds that a connection should be
  // allowed to be established before terminating and re-establishing the
  // connection.
  private volatile long maxConnectionAge;

  // The maximum length of time in milliseconds to wait for a connection to be
  // available.
  private long maxWaitTime;

  // The minimum length of time in milliseconds that must pass between
  // disconnects of connections that have exceeded the maximum connection age.
  private volatile long minDisconnectInterval;

  // The schema that should be shared for connections in this pool, along with
  // its expiration time.
  private volatile ObjectPair<Long,Schema> pooledSchema;

  // The post-connect processor for this connection pool, if any.
  private final PostConnectProcessor postConnectProcessor;

  // The server set to use for establishing connections for use by this pool.
  private final ServerSet serverSet;

  // The user-friendly name assigned to this connection pool.
  private String connectionPoolName;




  /**
   * Creates a new LDAP connection pool with up to the specified number of
   * connections, created as clones of the provided connection.  Initially, only
   * the provided connection will be included in the pool, but additional
   * connections will be created as needed until the pool has reached its full
   * capacity, at which point the create if necessary and max wait time settings
   * will be used to determine how to behave if a connection is requested but
   * none are available.
   *
   * @param  connection      The connection to use to provide the template for
   *                         the other connections to be created.  This
   *                         connection will be included in the pool.  It must
   *                         not be {@code null}, and it must be established to
   *                         the target server.  It does not necessarily need to
   *                         be authenticated if all connections in the pool are
   *                         to be unauthenticated.
   * @param  numConnections  The total number of connections that should be
   *                         created in the pool.  It must be greater than or
   *                         equal to one.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided connection cannot be used to
   *                         initialize the pool, or if a problem occurs while
   *                         attempting to establish any of the connections.  If
   *                         this is thrown, then all connections associated
   *                         with the pool (including the one provided as an
   *                         argument) will be closed.
   */
  public LDAPConnectionPool(final LDAPConnection connection,
                            final int numConnections)
         throws LDAPException
  {
    this(connection, 1, numConnections, null);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created as clones of the provided connection.
   *
   * @param  connection          The connection to use to provide the template
   *                             for the other connections to be created.  This
   *                             connection will be included in the pool.  It
   *                             must not be {@code null}, and it must be
   *                             established to the target server.  It does not
   *                             necessarily need to be authenticated if all
   *                             connections in the pool are to be
   *                             unauthenticated.
   * @param  initialConnections  The number of connections to initially
   *                             establish when the pool is created.  It must be
   *                             greater than or equal to one.
   * @param  maxConnections      The maximum number of connections that should
   *                             be maintained in the pool.  It must be greater
   *                             than or equal to the initial number of
   *                             connections.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided connection cannot be used to
   *                         initialize the pool, or if a problem occurs while
   *                         attempting to establish any of the connections.  If
   *                         this is thrown, then all connections associated
   *                         with the pool (including the one provided as an
   *                         argument) will be closed.
   */
  public LDAPConnectionPool(final LDAPConnection connection,
                            final int initialConnections,
                            final int maxConnections)
         throws LDAPException
  {
    this(connection, initialConnections, maxConnections, null);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created as clones of the provided connection.
   *
   * @param  connection            The connection to use to provide the template
   *                               for the other connections to be created.
   *                               This connection will be included in the pool.
   *                               It must not be {@code null}, and it must be
   *                               established to the target server.  It does
   *                               not necessarily need to be authenticated if
   *                               all connections in the pool are to be
   *                               unauthenticated.
   * @param  initialConnections    The number of connections to initially
   *                               establish when the pool is created.  It must
   *                               be greater than or equal to one.
   * @param  maxConnections        The maximum number of connections that should
   *                               be maintained in the pool.  It must be
   *                               greater than or equal to the initial number
   *                               of connections.
   * @param  postConnectProcessor  A processor that should be used to perform
   *                               any post-connect processing for connections
   *                               in this pool.  It may be {@code null} if no
   *                               special processing is needed.  Note that this
   *                               processing will not be invoked on the
   *                               provided connection that will be used as the
   *                               first connection in the pool.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided connection cannot be used to
   *                         initialize the pool, or if a problem occurs while
   *                         attempting to establish any of the connections.  If
   *                         this is thrown, then all connections associated
   *                         with the pool (including the one provided as an
   *                         argument) will be closed.
   */
  public LDAPConnectionPool(final LDAPConnection connection,
                            final int initialConnections,
                            final int maxConnections,
                            final PostConnectProcessor postConnectProcessor)
         throws LDAPException
  {
    this(connection, initialConnections, maxConnections,  postConnectProcessor,
         true);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created as clones of the provided connection.
   *
   * @param  connection             The connection to use to provide the
   *                                template for the other connections to be
   *                                created.  This connection will be included
   *                                in the pool.  It must not be {@code null},
   *                                and it must be established to the target
   *                                server.  It does not necessarily need to be
   *                                authenticated if all connections in the pool
   *                                are to be unauthenticated.
   * @param  initialConnections     The number of connections to initially
   *                                establish when the pool is created.  It must
   *                                be greater than or equal to one.
   * @param  maxConnections         The maximum number of connections that
   *                                should be maintained in the pool.  It must
   *                                be greater than or equal to the initial
   *                                number of connections.
   * @param  postConnectProcessor   A processor that should be used to perform
   *                                any post-connect processing for connections
   *                                in this pool.  It may be {@code null} if no
   *                                special processing is needed.  Note that
   *                                this processing will not be invoked on the
   *                                provided connection that will be used as the
   *                                first connection in the pool.
   * @param  throwOnConnectFailure  If an exception should be thrown if a
   *                                problem is encountered while attempting to
   *                                create the specified initial number of
   *                                connections.  If {@code true}, then the
   *                                attempt to create the pool will fail.if any
   *                                connection cannot be established.  If
   *                                {@code false}, then the pool will be created
   *                                but may have fewer than the initial number
   *                                of connections (or possibly no connections).
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided connection cannot be used to
   *                         initialize the pool, or if a problem occurs while
   *                         attempting to establish any of the connections.  If
   *                         this is thrown, then all connections associated
   *                         with the pool (including the one provided as an
   *                         argument) will be closed.
   */
  public LDAPConnectionPool(final LDAPConnection connection,
                            final int initialConnections,
                            final int maxConnections,
                            final PostConnectProcessor postConnectProcessor,
                            final boolean throwOnConnectFailure)
         throws LDAPException
  {
    this(connection, initialConnections, maxConnections, 1,
         postConnectProcessor, throwOnConnectFailure);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created as clones of the provided connection.
   *
   * @param  connection             The connection to use to provide the
   *                                template for the other connections to be
   *                                created.  This connection will be included
   *                                in the pool.  It must not be {@code null},
   *                                and it must be established to the target
   *                                server.  It does not necessarily need to be
   *                                authenticated if all connections in the pool
   *                                are to be unauthenticated.
   * @param  initialConnections     The number of connections to initially
   *                                establish when the pool is created.  It must
   *                                be greater than or equal to one.
   * @param  maxConnections         The maximum number of connections that
   *                                should be maintained in the pool.  It must
   *                                be greater than or equal to the initial
   *                                number of connections.
   * @param  initialConnectThreads  The number of concurrent threads to use to
   *                                establish the initial set of connections.
   *                                A value greater than one indicates that the
   *                                attempt to establish connections should be
   *                                parallelized.
   * @param  postConnectProcessor   A processor that should be used to perform
   *                                any post-connect processing for connections
   *                                in this pool.  It may be {@code null} if no
   *                                special processing is needed.  Note that
   *                                this processing will not be invoked on the
   *                                provided connection that will be used as the
   *                                first connection in the pool.
   * @param  throwOnConnectFailure  If an exception should be thrown if a
   *                                problem is encountered while attempting to
   *                                create the specified initial number of
   *                                connections.  If {@code true}, then the
   *                                attempt to create the pool will fail.if any
   *                                connection cannot be established.  If
   *                                {@code false}, then the pool will be created
   *                                but may have fewer than the initial number
   *                                of connections (or possibly no connections).
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided connection cannot be used to
   *                         initialize the pool, or if a problem occurs while
   *                         attempting to establish any of the connections.  If
   *                         this is thrown, then all connections associated
   *                         with the pool (including the one provided as an
   *                         argument) will be closed.
   */
  public LDAPConnectionPool(final LDAPConnection connection,
                            final int initialConnections,
                            final int maxConnections,
                            final int initialConnectThreads,
                            final PostConnectProcessor postConnectProcessor,
                            final boolean throwOnConnectFailure)
         throws LDAPException
  {
    ensureNotNull(connection);
    ensureTrue(initialConnections >= 1,
               "LDAPConnectionPool.initialConnections must be at least 1.");
    ensureTrue(maxConnections >= initialConnections,
               "LDAPConnectionPool.initialConnections must not be greater " +
                    "than maxConnections.");

    this.postConnectProcessor = postConnectProcessor;

    trySynchronousReadDuringHealthCheck = true;
    healthCheck               = new LDAPConnectionPoolHealthCheck();
    healthCheckInterval       = DEFAULT_HEALTH_CHECK_INTERVAL;
    poolStatistics            = new LDAPConnectionPoolStatistics(this);
    pooledSchema              = null;
    connectionPoolName        = null;
    retryOperationTypes       = new AtomicReference<Set<OperationType>>(
         Collections.unmodifiableSet(EnumSet.noneOf(OperationType.class)));
    numConnections            = maxConnections;
    availableConnections      =
         new LinkedBlockingQueue<LDAPConnection>(numConnections);

    if (! connection.isConnected())
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
                              ERR_POOL_CONN_NOT_ESTABLISHED.get());
    }


    serverSet = new SingleServerSet(connection.getConnectedAddress(),
                                    connection.getConnectedPort(),
                                    connection.getLastUsedSocketFactory(),
                                    connection.getConnectionOptions());
    bindRequest = connection.getLastBindRequest();

    final LDAPConnectionOptions opts = connection.getConnectionOptions();
    if (opts.usePooledSchema())
    {
      try
      {
        final Schema schema = connection.getSchema();
        if (schema != null)
        {
          connection.setCachedSchema(schema);

          final long currentTime = System.currentTimeMillis();
          final long timeout = opts.getPooledSchemaTimeoutMillis();
          if ((timeout <= 0L) || (timeout+currentTime <= 0L))
          {
            pooledSchema = new ObjectPair<Long,Schema>(Long.MAX_VALUE, schema);
          }
          else
          {
            pooledSchema =
                 new ObjectPair<Long,Schema>(timeout+currentTime, schema);
          }
        }
      }
      catch (final Exception e)
      {
        debugException(e);
      }
    }

    final List<LDAPConnection> connList;
    if (initialConnectThreads > 1)
    {
      connList = Collections.synchronizedList(
           new ArrayList<LDAPConnection>(initialConnections));
      final ParallelPoolConnector connector = new ParallelPoolConnector(this,
           connList, initialConnections, initialConnectThreads,
           throwOnConnectFailure);
      connector.establishConnections();
    }
    else
    {
      connList = new ArrayList<LDAPConnection>(initialConnections);
      connection.setConnectionName(null);
      connection.setConnectionPool(this);
      connList.add(connection);
      for (int i=1; i < initialConnections; i++)
      {
        try
        {
          connList.add(createConnection());
        }
        catch (LDAPException le)
        {
          debugException(le);

          if (throwOnConnectFailure)
          {
            for (final LDAPConnection c : connList)
            {
              try
              {
                c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null,
                     le);
                c.terminate(null);
              }
              catch (Exception e)
              {
                debugException(e);
              }
            }

            throw le;
          }
        }
      }
    }

    availableConnections.addAll(connList);

    failedReplaceCount        =
         new AtomicInteger(maxConnections - availableConnections.size());
    createIfNecessary         = true;
    maxConnectionAge          = 0L;
    minDisconnectInterval     = 0L;
    lastExpiredDisconnectTime = 0L;
    maxWaitTime               = 5000L;
    closed                    = false;

    healthCheckThread = new LDAPConnectionPoolHealthCheckThread(this);
    healthCheckThread.start();
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created using the provided server set.  Initially, only
   * one will be created and included in the pool, but additional connections
   * will be created as needed until the pool has reached its full capacity, at
   * which point the create if necessary and max wait time settings will be used
   * to determine how to behave if a connection is requested but none are
   * available.
   *
   * @param  serverSet       The server set to use to create the connections.
   *                         It is acceptable for the server set to create the
   *                         connections across multiple servers.
   * @param  bindRequest     The bind request to use to authenticate the
   *                         connections that are established.  It may be
   *                         {@code null} if no authentication should be
   *                         performed on the connections.
   * @param  numConnections  The total number of connections that should be
   *                         created in the pool.  It must be greater than or
   *                         equal to one.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while attempting to establish
   *                         any of the connections.  If this is thrown, then
   *                         all connections associated with the pool will be
   *                         closed.
   */
  public LDAPConnectionPool(final ServerSet serverSet,
                            final BindRequest bindRequest,
                            final int numConnections)
         throws LDAPException
  {
    this(serverSet, bindRequest, 1, numConnections, null);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created using the provided server set.
   *
   * @param  serverSet           The server set to use to create the
   *                             connections.  It is acceptable for the server
   *                             set to create the connections across multiple
   *                             servers.
   * @param  bindRequest         The bind request to use to authenticate the
   *                             connections that are established.  It may be
   *                             {@code null} if no authentication should be
   *                             performed on the connections.
   * @param  initialConnections  The number of connections to initially
   *                             establish when the pool is created.  It must be
   *                             greater than or equal to zero.
   * @param  maxConnections      The maximum number of connections that should
   *                             be maintained in the pool.  It must be greater
   *                             than or equal to the initial number of
   *                             connections, and must not be zero.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while attempting to establish
   *                         any of the connections.  If this is thrown, then
   *                         all connections associated with the pool will be
   *                         closed.
   */
  public LDAPConnectionPool(final ServerSet serverSet,
                            final BindRequest bindRequest,
                            final int initialConnections,
                            final int maxConnections)
         throws LDAPException
  {
    this(serverSet, bindRequest, initialConnections, maxConnections, null);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created using the provided server set.
   *
   * @param  serverSet             The server set to use to create the
   *                               connections.  It is acceptable for the server
   *                               set to create the connections across multiple
   *                               servers.
   * @param  bindRequest           The bind request to use to authenticate the
   *                               connections that are established.  It may be
   *                               {@code null} if no authentication should be
   *                               performed on the connections.
   * @param  initialConnections    The number of connections to initially
   *                               establish when the pool is created.  It must
   *                               be greater than or equal to zero.
   * @param  maxConnections        The maximum number of connections that should
   *                               be maintained in the pool.  It must be
   *                               greater than or equal to the initial number
   *                               of connections, and must not be zero.
   * @param  postConnectProcessor  A processor that should be used to perform
   *                               any post-connect processing for connections
   *                               in this pool.  It may be {@code null} if no
   *                               special processing is needed.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while attempting to establish
   *                         any of the connections.  If this is thrown, then
   *                         all connections associated with the pool will be
   *                         closed.
   */
  public LDAPConnectionPool(final ServerSet serverSet,
                            final BindRequest bindRequest,
                            final int initialConnections,
                            final int maxConnections,
                            final PostConnectProcessor postConnectProcessor)
         throws LDAPException
  {
    this(serverSet, bindRequest, initialConnections, maxConnections,
         postConnectProcessor, true);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created using the provided server set.
   *
   * @param  serverSet              The server set to use to create the
   *                                connections.  It is acceptable for the
   *                                server set to create the connections across
   *                                multiple servers.
   * @param  bindRequest            The bind request to use to authenticate the
   *                                connections that are established.  It may be
   *                                {@code null} if no authentication should be
   *                                performed on the connections.
   * @param  initialConnections     The number of connections to initially
   *                                establish when the pool is created.  It must
   *                                be greater than or equal to zero.
   * @param  maxConnections         The maximum number of connections that
   *                                should be maintained in the pool.  It must
   *                                be greater than or equal to the initial
   *                                number of connections, and must not be zero.
   * @param  postConnectProcessor   A processor that should be used to perform
   *                                any post-connect processing for connections
   *                                in this pool.  It may be {@code null} if no
   *                                special processing is needed.
   * @param  throwOnConnectFailure  If an exception should be thrown if a
   *                                problem is encountered while attempting to
   *                                create the specified initial number of
   *                                connections.  If {@code true}, then the
   *                                attempt to create the pool will fail.if any
   *                                connection cannot be established.  If
   *                                {@code false}, then the pool will be created
   *                                but may have fewer than the initial number
   *                                of connections (or possibly no connections).
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while attempting to establish
   *                         any of the connections and
   *                         {@code throwOnConnectFailure} is true.  If this is
   *                         thrown, then all connections associated with the
   *                         pool will be closed.
   */
  public LDAPConnectionPool(final ServerSet serverSet,
                            final BindRequest bindRequest,
                            final int initialConnections,
                            final int maxConnections,
                            final PostConnectProcessor postConnectProcessor,
                            final boolean throwOnConnectFailure)
         throws LDAPException
  {
    this(serverSet, bindRequest, initialConnections, maxConnections, 1,
         postConnectProcessor, throwOnConnectFailure);
  }



  /**
   * Creates a new LDAP connection pool with the specified number of
   * connections, created using the provided server set.
   *
   * @param  serverSet              The server set to use to create the
   *                                connections.  It is acceptable for the
   *                                server set to create the connections across
   *                                multiple servers.
   * @param  bindRequest            The bind request to use to authenticate the
   *                                connections that are established.  It may be
   *                                {@code null} if no authentication should be
   *                                performed on the connections.
   * @param  initialConnections     The number of connections to initially
   *                                establish when the pool is created.  It must
   *                                be greater than or equal to zero.
   * @param  maxConnections         The maximum number of connections that
   *                                should be maintained in the pool.  It must
   *                                be greater than or equal to the initial
   *                                number of connections, and must not be zero.
   * @param  initialConnectThreads  The number of concurrent threads to use to
   *                                establish the initial set of connections.
   *                                A value greater than one indicates that the
   *                                attempt to establish connections should be
   *                                parallelized.
   * @param  postConnectProcessor   A processor that should be used to perform
   *                                any post-connect processing for connections
   *                                in this pool.  It may be {@code null} if no
   *                                special processing is needed.
   * @param  throwOnConnectFailure  If an exception should be thrown if a
   *                                problem is encountered while attempting to
   *                                create the specified initial number of
   *                                connections.  If {@code true}, then the
   *                                attempt to create the pool will fail.if any
   *                                connection cannot be established.  If
   *                                {@code false}, then the pool will be created
   *                                but may have fewer than the initial number
   *                                of connections (or possibly no connections).
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while attempting to establish
   *                         any of the connections and
   *                         {@code throwOnConnectFailure} is true.  If this is
   *                         thrown, then all connections associated with the
   *                         pool will be closed.
   */
  public LDAPConnectionPool(final ServerSet serverSet,
                            final BindRequest bindRequest,
                            final int initialConnections,
                            final int maxConnections,
                            final int initialConnectThreads,
                            final PostConnectProcessor postConnectProcessor,
                            final boolean throwOnConnectFailure)
         throws LDAPException
  {
    ensureNotNull(serverSet);
    ensureTrue(initialConnections >= 0,
               "LDAPConnectionPool.initialConnections must be greater than " +
                    "or equal to 0.");
    ensureTrue(maxConnections > 0,
               "LDAPConnectionPool.maxConnections must be greater than 0.");
    ensureTrue(maxConnections >= initialConnections,
               "LDAPConnectionPool.initialConnections must not be greater " +
                    "than maxConnections.");

    this.serverSet            = serverSet;
    this.bindRequest          = bindRequest;
    this.postConnectProcessor = postConnectProcessor;

    healthCheck               = new LDAPConnectionPoolHealthCheck();
    healthCheckInterval       = DEFAULT_HEALTH_CHECK_INTERVAL;
    poolStatistics            = new LDAPConnectionPoolStatistics(this);
    connectionPoolName        = null;
    retryOperationTypes       = new AtomicReference<Set<OperationType>>(
         Collections.unmodifiableSet(EnumSet.noneOf(OperationType.class)));

    final List<LDAPConnection> connList;
    if (initialConnectThreads > 1)
    {
      connList = Collections.synchronizedList(
           new ArrayList<LDAPConnection>(initialConnections));
      final ParallelPoolConnector connector = new ParallelPoolConnector(this,
           connList, initialConnections, initialConnectThreads,
           throwOnConnectFailure);
      connector.establishConnections();
    }
    else
    {
      connList = new ArrayList<LDAPConnection>(initialConnections);
      for (int i=0; i < initialConnections; i++)
      {
        try
        {
          connList.add(createConnection());
        }
        catch (LDAPException le)
        {
          debugException(le);

          if (throwOnConnectFailure)
          {
            for (final LDAPConnection c : connList)
            {
              try
              {
                c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null,
                     le);
                c.terminate(null);
              } catch (Exception e)
              {
                debugException(e);
              }
            }

            throw le;
          }
        }
      }
    }

    numConnections = maxConnections;

    availableConnections =
         new LinkedBlockingQueue<LDAPConnection>(numConnections);
    availableConnections.addAll(connList);

    failedReplaceCount        =
         new AtomicInteger(maxConnections - availableConnections.size());
    createIfNecessary         = true;
    maxConnectionAge          = 0L;
    minDisconnectInterval     = 0L;
    lastExpiredDisconnectTime = 0L;
    maxWaitTime               = 5000L;
    closed                    = false;

    healthCheckThread = new LDAPConnectionPoolHealthCheckThread(this);
    healthCheckThread.start();
  }



  /**
   * Creates a new LDAP connection for use in this pool.
   *
   * @return  A new connection created for use in this pool.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while attempting to establish
   *                         the connection.  If a connection had been created,
   *                         it will be closed.
   */
  LDAPConnection createConnection()
                 throws LDAPException
  {
    final LDAPConnection c = serverSet.getConnection(healthCheck);
    c.setConnectionPool(this);

    // Auto-reconnect must be disabled for pooled connections, so turn it off
    // if the associated connection options have it enabled for some reason.
    LDAPConnectionOptions opts = c.getConnectionOptions();
    if (opts.autoReconnect())
    {
      opts = opts.duplicate();
      opts.setAutoReconnect(false);
      c.setConnectionOptions(opts);
    }

    if (postConnectProcessor != null)
    {
      try
      {
        postConnectProcessor.processPreAuthenticatedConnection(c);
      }
      catch (Exception e)
      {
        debugException(e);

        try
        {
          poolStatistics.incrementNumFailedConnectionAttempts();
          c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null, e);
          c.terminate(null);
        }
        catch (Exception e2)
        {
          debugException(e2);
        }

        if (e instanceof LDAPException)
        {
          throw ((LDAPException) e);
        }
        else
        {
          throw new LDAPException(ResultCode.CONNECT_ERROR,
               ERR_POOL_POST_CONNECT_ERROR.get(getExceptionMessage(e)), e);
        }
      }
    }

    try
    {
      if (bindRequest != null)
      {
        c.bind(bindRequest.duplicate());
      }
    }
    catch (Exception e)
    {
      debugException(e);
      try
      {
        poolStatistics.incrementNumFailedConnectionAttempts();
        c.setDisconnectInfo(DisconnectType.BIND_FAILED, null, e);
        c.terminate(null);
      }
      catch (Exception e2)
      {
        debugException(e2);
      }

      if (e instanceof LDAPException)
      {
        throw ((LDAPException) e);
      }
      else
      {
        throw new LDAPException(ResultCode.CONNECT_ERROR,
             ERR_POOL_CONNECT_ERROR.get(getExceptionMessage(e)), e);
      }
    }

    if (postConnectProcessor != null)
    {
      try
      {
        postConnectProcessor.processPostAuthenticatedConnection(c);
      }
      catch (Exception e)
      {
        debugException(e);
        try
        {
          poolStatistics.incrementNumFailedConnectionAttempts();
          c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null, e);
          c.terminate(null);
        }
        catch (Exception e2)
        {
          debugException(e2);
        }

        if (e instanceof LDAPException)
        {
          throw ((LDAPException) e);
        }
        else
        {
          throw new LDAPException(ResultCode.CONNECT_ERROR,
               ERR_POOL_POST_CONNECT_ERROR.get(getExceptionMessage(e)), e);
        }
      }
    }

    if (opts.usePooledSchema())
    {
      final long currentTime = System.currentTimeMillis();
      if ((pooledSchema == null) || (currentTime > pooledSchema.getFirst()))
      {
        try
        {
          final Schema schema = c.getSchema();
          if (schema != null)
          {
            c.setCachedSchema(schema);

            final long timeout = opts.getPooledSchemaTimeoutMillis();
            if ((timeout <= 0L) || (currentTime + timeout <= 0L))
            {
              pooledSchema =
                   new ObjectPair<Long,Schema>(Long.MAX_VALUE, schema);
            }
            else
            {
              pooledSchema =
                   new ObjectPair<Long,Schema>((currentTime+timeout), schema);
            }
          }
        }
        catch (final Exception e)
        {
          debugException(e);

          // There was a problem retrieving the schema from the server, but if
          // we have an earlier copy then we can assume it's still valid.
          if (pooledSchema != null)
          {
            c.setCachedSchema(pooledSchema.getSecond());
          }
        }
      }
      else
      {
        c.setCachedSchema(pooledSchema.getSecond());
      }
    }

    c.setConnectionPoolName(connectionPoolName);
    poolStatistics.incrementNumSuccessfulConnectionAttempts();

    return c;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void close()
  {
    close(true, 1);
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void close(final boolean unbind, final int numThreads)
  {
    closed = true;
    healthCheckThread.stopRunning();

    if (numThreads > 1)
    {
      final ArrayList<LDAPConnection> connList =
           new ArrayList<LDAPConnection>(availableConnections.size());
      availableConnections.drainTo(connList);

      final ParallelPoolCloser closer =
           new ParallelPoolCloser(connList, unbind, numThreads);
      closer.closeConnections();
    }
    else
    {
      while (true)
      {
        final LDAPConnection conn = availableConnections.poll();
        if (conn == null)
        {
          return;
        }
        else
        {
          poolStatistics.incrementNumConnectionsClosedUnneeded();
          conn.setDisconnectInfo(DisconnectType.POOL_CLOSED, null, null);
          if (unbind)
          {
            conn.terminate(null);
          }
          else
          {
            conn.setClosed();
          }
        }
      }
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public boolean isClosed()
  {
    return closed;
  }



  /**
   * Processes a simple bind using a connection from this connection pool, and
   * then reverts that authentication by re-binding as the same user used to
   * authenticate new connections.  If new connections are unauthenticated, then
   * the subsequent bind will be an anonymous simple bind.  This method attempts
   * to ensure that processing the provided bind operation does not have a
   * lasting impact the authentication state of the connection used to process
   * it.
   * <BR><BR>
   * If the second bind attempt (the one used to restore the authentication
   * identity) fails, the connection will be closed as defunct so that a new
   * connection will be created to take its place.
   *
   * @param  bindDN    The bind DN for the simple bind request.
   * @param  password  The password for the simple bind request.
   * @param  controls  The optional set of controls for the simple bind request.
   *
   * @return  The result of processing the provided bind operation.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the server rejects the bind request, or if a
   *                         problem occurs while sending the request or reading
   *                         the response.
   */
  public BindResult bindAndRevertAuthentication(final String bindDN,
                                                final String password,
                                                final Control... controls)
         throws LDAPException
  {
    return bindAndRevertAuthentication(
         new SimpleBindRequest(bindDN, password, controls));
  }



  /**
   * Processes the provided bind request using a connection from this connection
   * pool, and then reverts that authentication by re-binding as the same user
   * used to authenticate new connections.  If new connections are
   * unauthenticated, then the subsequent bind will be an anonymous simple bind.
   * This method attempts to ensure that processing the provided bind operation
   * does not have a lasting impact the authentication state of the connection
   * used to process it.
   * <BR><BR>
   * If the second bind attempt (the one used to restore the authentication
   * identity) fails, the connection will be closed as defunct so that a new
   * connection will be created to take its place.
   *
   * @param  bindRequest  The bind request to be processed.  It must not be
   *                      {@code null}.
   *
   * @return  The result of processing the provided bind operation.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the server rejects the bind request, or if a
   *                         problem occurs while sending the request or reading
   *                         the response.
   */
  public BindResult bindAndRevertAuthentication(final BindRequest bindRequest)
         throws LDAPException
  {
    LDAPConnection conn = getConnection();

    try
    {
      final BindResult result = conn.bind(bindRequest);
      releaseAndReAuthenticateConnection(conn);
      return result;
    }
    catch (final Throwable t)
    {
      debugException(t);

      if (t instanceof LDAPException)
      {
        final LDAPException le = (LDAPException) t;

        boolean shouldThrow;
        try
        {
          healthCheck.ensureConnectionValidAfterException(conn, le);

          // The above call will throw an exception if the connection doesn't
          // seem to be valid, so if we've gotten here then we should assume
          // that it is valid and we will pass the exception onto the client
          // without retrying the operation.
          releaseAndReAuthenticateConnection(conn);
          shouldThrow = true;
        }
        catch (final Exception e)
        {
          debugException(e);

          // This implies that the connection is not valid.  If the pool is
          // configured to re-try bind operations on a newly-established
          // connection, then that will be done later in this method.
          // Otherwise, release the connection as defunct and pass the bind
          // exception onto the client.
          if (! getOperationTypesToRetryDueToInvalidConnections().contains(
                     OperationType.BIND))
          {
            releaseDefunctConnection(conn);
            shouldThrow = true;
          }
          else
          {
            shouldThrow = false;
          }
        }

        if (shouldThrow)
        {
          throw le;
        }
      }
      else
      {
        releaseDefunctConnection(conn);
        throw new LDAPException(ResultCode.LOCAL_ERROR,
             ERR_POOL_OP_EXCEPTION.get(getExceptionMessage(t)), t);
      }
    }


    // If we've gotten here, then the bind operation should be re-tried on a
    // newly-established connection.
    conn = replaceDefunctConnection(conn);

    try
    {
      final BindResult result = conn.bind(bindRequest);
      releaseAndReAuthenticateConnection(conn);
      return result;
    }
    catch (final Throwable t)
    {
      debugException(t);

      if (t instanceof LDAPException)
      {
        final LDAPException le = (LDAPException) t;

        try
        {
          healthCheck.ensureConnectionValidAfterException(conn, le);
          releaseAndReAuthenticateConnection(conn);
        }
        catch (final Exception e)
        {
          debugException(e);
          releaseDefunctConnection(conn);
        }

        throw le;
      }
      else
      {
        releaseDefunctConnection(conn);
        throw new LDAPException(ResultCode.LOCAL_ERROR,
             ERR_POOL_OP_EXCEPTION.get(getExceptionMessage(t)), t);
      }
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public LDAPConnection getConnection()
         throws LDAPException
  {
    if (closed)
    {
      poolStatistics.incrementNumFailedCheckouts();
      throw new LDAPException(ResultCode.CONNECT_ERROR,
                              ERR_POOL_CLOSED.get());
    }

    LDAPConnection conn = availableConnections.poll();
    if (conn != null)
    {
      if (conn.isConnected())
      {
        try
        {
          healthCheck.ensureConnectionValidForCheckout(conn);
          poolStatistics.incrementNumSuccessfulCheckoutsWithoutWaiting();
          return conn;
        }
        catch (LDAPException le)
        {
          debugException(le);
        }
      }

      handleDefunctConnection(conn);
      for (int i=0; i < numConnections; i++)
      {
        conn = availableConnections.poll();
        if (conn == null)
        {
          break;
        }
        else if (conn.isConnected())
        {
          try
          {
            healthCheck.ensureConnectionValidForCheckout(conn);
            poolStatistics.incrementNumSuccessfulCheckoutsWithoutWaiting();
            return conn;
          }
          catch (LDAPException le)
          {
            debugException(le);
            handleDefunctConnection(conn);
          }
        }
        else
        {
          handleDefunctConnection(conn);
        }
      }
    }

    if (failedReplaceCount.get() > 0)
    {
      final int newReplaceCount = failedReplaceCount.getAndDecrement();
      if (newReplaceCount > 0)
      {
        try
        {
          conn = createConnection();
          poolStatistics.incrementNumSuccessfulCheckoutsNewConnection();
          return conn;
        }
        catch (LDAPException le)
        {
          debugException(le);
          failedReplaceCount.incrementAndGet();
          poolStatistics.incrementNumFailedCheckouts();
          throw le;
        }
      }
      else
      {
        failedReplaceCount.incrementAndGet();
        poolStatistics.incrementNumFailedCheckouts();
        throw new LDAPException(ResultCode.CONNECT_ERROR,
                                ERR_POOL_NO_CONNECTIONS.get());
      }
    }

    if (maxWaitTime > 0)
    {
      try
      {
        conn = availableConnections.poll(maxWaitTime, TimeUnit.MILLISECONDS);
        if (conn != null)
        {
          try
          {
            healthCheck.ensureConnectionValidForCheckout(conn);
            poolStatistics.incrementNumSuccessfulCheckoutsAfterWaiting();
            return conn;
          }
          catch (LDAPException le)
          {
            debugException(le);
            handleDefunctConnection(conn);
          }
        }
      }
      catch (InterruptedException ie)
      {
        debugException(ie);
      }
    }

    if (createIfNecessary)
    {
      try
      {
        conn = createConnection();
        poolStatistics.incrementNumSuccessfulCheckoutsNewConnection();
        return conn;
      }
      catch (LDAPException le)
      {
        debugException(le);
        poolStatistics.incrementNumFailedCheckouts();
        throw le;
      }
    }
    else
    {
      poolStatistics.incrementNumFailedCheckouts();
      throw new LDAPException(ResultCode.CONNECT_ERROR,
                              ERR_POOL_NO_CONNECTIONS.get());
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void releaseConnection(final LDAPConnection connection)
  {
    if (connection == null)
    {
      return;
    }

    connection.setConnectionPoolName(connectionPoolName);
    if (connectionIsExpired(connection))
    {
      try
      {
        final LDAPConnection newConnection = createConnection();
        if (availableConnections.offer(newConnection))
        {
          connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_EXPIRED,
               null, null);
          connection.terminate(null);
          poolStatistics.incrementNumConnectionsClosedExpired();
          lastExpiredDisconnectTime = System.currentTimeMillis();
          return;
        }
        else
        {
          newConnection.setDisconnectInfo(
               DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
          newConnection.terminate(null);
          poolStatistics.incrementNumConnectionsClosedUnneeded();
        }
      }
      catch (final LDAPException le)
      {
        debugException(le);
      }
    }

    try
    {
      healthCheck.ensureConnectionValidForRelease(connection);
    }
    catch (LDAPException le)
    {
      releaseDefunctConnection(connection);
      return;
    }

    if (availableConnections.offer(connection))
    {
      poolStatistics.incrementNumReleasedValid();
    }
    else
    {
      // This means that the connection pool is full, which can happen if the
      // pool was empty when a request came in to retrieve a connection and
      // createIfNecessary was true.  In this case, we'll just close the
      // connection since we don't need it any more.
      connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED,
                                   null, null);
      poolStatistics.incrementNumConnectionsClosedUnneeded();
      connection.terminate(null);
      return;
    }

    if (closed)
    {
      close();
    }
  }



  /**
   * Performs a bind on the provided connection before releasing it back to the
   * pool, so that it will be authenticated as the same user as
   * newly-established connections.  If newly-established connections are
   * unauthenticated, then this method will perform an anonymous simple bind to
   * ensure that the resulting connection is unauthenticated.
   *
   * Releases the provided connection back to this pool.
   *
   * @param  connection  The connection to be released back to the pool after
   *                     being re-authenticated.
   */
  public void releaseAndReAuthenticateConnection(
                   final LDAPConnection connection)
  {
    if (connection == null)
    {
      return;
    }

    try
    {
      if (bindRequest == null)
      {
        connection.bind("", "");
      }
      else
      {
        connection.bind(bindRequest);
      }

      releaseConnection(connection);
    }
    catch (final Exception e)
    {
      debugException(e);
      releaseDefunctConnection(connection);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void releaseDefunctConnection(final LDAPConnection connection)
  {
    if (connection == null)
    {
      return;
    }

    connection.setConnectionPoolName(connectionPoolName);
    poolStatistics.incrementNumConnectionsClosedDefunct();
    handleDefunctConnection(connection);
  }



  /**
   * Performs the real work of terminating a defunct connection and replacing it
   * with a new connection if possible.
   *
   * @param  connection  The defunct connection to be replaced.
   *
   * @return  The new connection created to take the place of the defunct
   *          connection, or {@code null} if no new connection was created.
   *          Note that if a connection is returned, it will have already been
   *          made available and the caller must not rely on it being unused for
   *          any other purpose.
   */
  private LDAPConnection handleDefunctConnection(
                              final LDAPConnection connection)
  {
    connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null,
                                 null);
    connection.terminate(null);

    if (closed)
    {
      return null;
    }

    if (createIfNecessary && (availableConnections.remainingCapacity() <= 0))
    {
      return null;
    }

    try
    {
      final LDAPConnection conn = createConnection();
      if (! availableConnections.offer(conn))
      {
        conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED,
                               null, null);
        conn.terminate(null);
        return null;
      }

      return conn;
    }
    catch (LDAPException le)
    {
      debugException(le);
      failedReplaceCount.incrementAndGet();
      return null;
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public LDAPConnection replaceDefunctConnection(
                             final LDAPConnection connection)
         throws LDAPException
  {
    connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null,
                                 null);
    connection.terminate(null);

    if (closed)
    {
      throw new LDAPException(ResultCode.CONNECT_ERROR, ERR_POOL_CLOSED.get());
    }

    return createConnection();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public Set<OperationType> getOperationTypesToRetryDueToInvalidConnections()
  {
    return retryOperationTypes.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void setRetryFailedOperationsDueToInvalidConnections(
                   final Set<OperationType> operationTypes)
  {
    if ((operationTypes == null) || operationTypes.isEmpty())
    {
      retryOperationTypes.set(
           Collections.unmodifiableSet(EnumSet.noneOf(OperationType.class)));
    }
    else
    {
      final EnumSet<OperationType> s = EnumSet.noneOf(OperationType.class);
      s.addAll(operationTypes);
      retryOperationTypes.set(Collections.unmodifiableSet(s));
    }
  }



  /**
   * Indicates whether the provided connection should be considered expired.
   *
   * @param  connection  The connection for which to make the determination.
   *
   * @return  {@code true} if the provided connection should be considered
   *          expired, or {@code false} if not.
   */
  private boolean connectionIsExpired(final LDAPConnection connection)
  {
    // If connection expiration is not enabled, then there is nothing to do.
    if (maxConnectionAge <= 0L)
    {
      return false;
    }

    // If there is a minimum disconnect interval, then make sure that we have
    // not closed another expired connection too recently.
    final long currentTime = System.currentTimeMillis();
    if ((currentTime - lastExpiredDisconnectTime) < minDisconnectInterval)
    {
      return false;
    }

    // Get the age of the connection and see if it is expired.
    final long connectionAge = currentTime - connection.getConnectTime();
    return (connectionAge > maxConnectionAge);
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getConnectionPoolName()
  {
    return connectionPoolName;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void setConnectionPoolName(final String connectionPoolName)
  {
    this.connectionPoolName = connectionPoolName;
    for (final LDAPConnection c : availableConnections)
    {
      c.setConnectionPoolName(connectionPoolName);
    }
  }



  /**
   * Indicates whether the connection pool should create a new connection if one
   * is requested when there are none available.
   *
   * @return  {@code true} if a new connection should be created if none are
   *          available when a request is received, or {@code false} if an
   *          exception should be thrown to indicate that no connection is
   *          available.
   */
  public boolean getCreateIfNecessary()
  {
    return createIfNecessary;
  }



  /**
   * Specifies whether the connection pool should create a new connection if one
   * is requested when there are none available.
   *
   * @param  createIfNecessary  Specifies whether the connection pool should
   *                            create a new connection if one is requested when
   *                            there are none available.
   */
  public void setCreateIfNecessary(final boolean createIfNecessary)
  {
    this.createIfNecessary = createIfNecessary;
  }



  /**
   * Retrieves the maximum length of time in milliseconds to wait for a
   * connection to become available when trying to obtain a connection from the
   * pool.
   *
   * @return  The maximum length of time in milliseconds to wait for a
   *          connection to become available when trying to obtain a connection
   *          from the pool, or zero to indicate that the pool should not block
   *          at all if no connections are available and that it should either
   *          create a new connection or throw an exception.
   */
  public long getMaxWaitTimeMillis()
  {
    return maxWaitTime;
  }



  /**
   * Specifies the maximum length of time in milliseconds to wait for a
   * connection to become available when trying to obtain a connection from the
   * pool.
   *
   * @param  maxWaitTime  The maximum length of time in milliseconds to wait for
   *                      a connection to become available when trying to obtain
   *                      a connection from the pool.  A value of zero should be
   *                      used to indicate that the pool should not block at all
   *                      if no connections are available and that it should
   *                      either create a new connection or throw an exception.
   */
  public void setMaxWaitTimeMillis(final long maxWaitTime)
  {
    if (maxWaitTime > 0L)
    {
      this.maxWaitTime = maxWaitTime;
    }
    else
    {
      this.maxWaitTime = 0L;
    }
  }



  /**
   * Retrieves the maximum length of time in milliseconds that a connection in
   * this pool may be established before it is closed and replaced with another
   * connection.
   *
   * @return  The maximum length of time in milliseconds that a connection in
   *          this pool may be established before it is closed and replaced with
   *          another connection, or {@code 0L} if no maximum age should be
   *          enforced.
   */
  public long getMaxConnectionAgeMillis()
  {
    return maxConnectionAge;
  }



  /**
   * Specifies the maximum length of time in milliseconds that a connection in
   * this pool may be established before it should be closed and replaced with
   * another connection.
   *
   * @param  maxConnectionAge  The maximum length of time in milliseconds that a
   *                           connection in this pool may be established before
   *                           it should be closed and replaced with another
   *                           connection.  A value of zero indicates that no
   *                           maximum age should be enforced.
   */
  public void setMaxConnectionAgeMillis(final long maxConnectionAge)
  {
    if (maxConnectionAge > 0L)
    {
      this.maxConnectionAge = maxConnectionAge;
    }
    else
    {
      this.maxConnectionAge = 0L;
    }
  }



  /**
   * Retrieves the minimum length of time in milliseconds that should pass
   * between connections closed because they have been established for longer
   * than the maximum connection age.
   *
   * @return  The minimum length of time in milliseconds that should pass
   *          between connections closed because they have been established for
   *          longer than the maximum connection age, or {@code 0L} if expired
   *          connections may be closed as quickly as they are identified.
   */
  public long getMinDisconnectIntervalMillis()
  {
    return minDisconnectInterval;
  }



  /**
   * Specifies the minimum length of time in milliseconds that should pass
   * between connections closed because they have been established for longer
   * than the maximum connection age.
   *
   * @param  minDisconnectInterval  The minimum length of time in milliseconds
   *                                that should pass between connections closed
   *                                because they have been established for
   *                                longer than the maximum connection age.  A
   *                                value less than or equal to zero indicates
   *                                that no minimum time should be enforced.
   */
  public void setMinDisconnectIntervalMillis(final long minDisconnectInterval)
  {
    if (minDisconnectInterval > 0)
    {
      this.minDisconnectInterval = minDisconnectInterval;
    }
    else
    {
      this.minDisconnectInterval = 0L;
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public LDAPConnectionPoolHealthCheck getHealthCheck()
  {
    return healthCheck;
  }



  /**
   * Sets the health check implementation for this connection pool.
   *
   * @param  healthCheck  The health check implementation for this connection
   *                      pool.  It must not be {@code null}.
   */
  public void setHealthCheck(final LDAPConnectionPoolHealthCheck healthCheck)
  {
    ensureNotNull(healthCheck);
    this.healthCheck = healthCheck;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public long getHealthCheckIntervalMillis()
  {
    return healthCheckInterval;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void setHealthCheckIntervalMillis(final long healthCheckInterval)
  {
    ensureTrue(healthCheckInterval > 0L,
         "LDAPConnectionPool.healthCheckInterval must be greater than 0.");
    this.healthCheckInterval = healthCheckInterval;
    healthCheckThread.wakeUp();
  }



  /**
   * Indicates whether health check processing for connections operating in
   * synchronous mode should include attempting to perform a read from each
   * connection with a very short timeout.  This can help detect unsolicited
   * responses and unexpected connection closures in a more timely manner.  This
   * will be ignored for connections not operating in synchronous mode.
   *
   * @return  {@code true} if health check processing for connections operating
   *          in synchronous mode should include a read attempt with a very
   *          short timeout, or {@code false} if not.
   */
  public boolean trySynchronousReadDuringHealthCheck()
  {
    return trySynchronousReadDuringHealthCheck;
  }



  /**
   * Specifies whether health check processing for connections operating in
   * synchronous mode should include attempting to perform a read from each
   * connection with a very short timeout.
   *
   * @param  trySynchronousReadDuringHealthCheck  Indicates whether health check
   *                                              processing for connections
   *                                              operating in synchronous mode
   *                                              should include attempting to
   *                                              perform a read from each
   *                                              connection with a very short
   *                                              timeout.
   */
  public void setTrySynchronousReadDuringHealthCheck(
                   final boolean trySynchronousReadDuringHealthCheck)
  {
    this.trySynchronousReadDuringHealthCheck =
         trySynchronousReadDuringHealthCheck;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  protected void doHealthCheck()
  {
    // Create a set used to hold connections that we've already examined.  If we
    // encounter the same connection twice, then we know that we don't need to
    // do any more work.
    final HashSet<LDAPConnection> examinedConnections =
         new HashSet<LDAPConnection>(numConnections);

    for (int i=0; i < numConnections; i++)
    {
      LDAPConnection conn = availableConnections.poll();
      if (conn == null)
      {
        break;
      }
      else if (examinedConnections.contains(conn))
      {
        if (! availableConnections.offer(conn))
        {
          conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED,
                                 null, null);
          poolStatistics.incrementNumConnectionsClosedUnneeded();
          conn.terminate(null);
        }
        break;
      }

      if (! conn.isConnected())
      {
        conn = handleDefunctConnection(conn);
        if (conn != null)
        {
          examinedConnections.add(conn);
        }
      }
      else
      {
        if (connectionIsExpired(conn))
        {
          try
          {
            final LDAPConnection newConnection = createConnection();
            if (availableConnections.offer(newConnection))
            {
              examinedConnections.add(newConnection);
              conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_EXPIRED,
                   null, null);
              conn.terminate(null);
              poolStatistics.incrementNumConnectionsClosedExpired();
              lastExpiredDisconnectTime = System.currentTimeMillis();
              continue;
            }
            else
            {
              newConnection.setDisconnectInfo(
                   DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
              newConnection.terminate(null);
              poolStatistics.incrementNumConnectionsClosedUnneeded();
            }
          }
          catch (final LDAPException le)
          {
            debugException(le);
          }
        }


        // If the connection is operating in synchronous mode, then try to read
        // a message on it using an extremely short timeout.  This can help
        // detect a connection closure or unsolicited notification in a more
        // timely manner than if we had to wait for the client code to try to
        // use the connection.
        if (trySynchronousReadDuringHealthCheck && conn.synchronousMode())
        {
          int previousTimeout = Integer.MIN_VALUE;
          Socket s = null;
          try
          {
            s = conn.getConnectionInternals(true).getSocket();
            previousTimeout = s.getSoTimeout();
            s.setSoTimeout(1);

            final LDAPResponse response = conn.readResponse(0);
            if (response instanceof ConnectionClosedResponse)
            {
              conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT,
                   ERR_POOL_HEALTH_CHECK_CONN_CLOSED.get(), null);
              poolStatistics.incrementNumConnectionsClosedDefunct();
              conn = handleDefunctConnection(conn);
              if (conn != null)
              {
                examinedConnections.add(conn);
              }
              continue;
            }
            else if (response instanceof ExtendedResult)
            {
              // This means we got an unsolicited response.  It could be a
              // notice of disconnection, or it could be something else, but in
              // any case we'll send it to the connection's unsolicited
              // notification handler (if one is defined).
              final UnsolicitedNotificationHandler h = conn.
                   getConnectionOptions().getUnsolicitedNotificationHandler();
              if (h != null)
              {
                h.handleUnsolicitedNotification(conn,
                     (ExtendedResult) response);
              }
            }
            else if (response instanceof LDAPResult)
            {
              final LDAPResult r = (LDAPResult) response;
              if (r.getResultCode() == ResultCode.SERVER_DOWN)
              {
                conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT,
                     ERR_POOL_HEALTH_CHECK_CONN_CLOSED.get(), null);
                poolStatistics.incrementNumConnectionsClosedDefunct();
                conn = handleDefunctConnection(conn);
                if (conn != null)
                {
                  examinedConnections.add(conn);
                }
                continue;
              }
            }
          }
          catch (final LDAPException le)
          {
            if (le.getResultCode() == ResultCode.TIMEOUT)
            {
              debugException(Level.FINEST, le);
            }
            else
            {
              debugException(le);
              conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT,
                   ERR_POOL_HEALTH_CHECK_READ_FAILURE.get(
                        getExceptionMessage(le)), le);
              poolStatistics.incrementNumConnectionsClosedDefunct();
              conn = handleDefunctConnection(conn);
              if (conn != null)
              {
                examinedConnections.add(conn);
              }
              continue;
            }
          }
          catch (final Exception e)
          {
            debugException(e);
            conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT,
                 ERR_POOL_HEALTH_CHECK_READ_FAILURE.get(getExceptionMessage(e)),
                 e);
            poolStatistics.incrementNumConnectionsClosedDefunct();
            conn = handleDefunctConnection(conn);
            if (conn != null)
            {
              examinedConnections.add(conn);
            }
            continue;
          }
          finally
          {
            if (previousTimeout != Integer.MIN_VALUE)
            {
              try
              {
                s.setSoTimeout(previousTimeout);
              }
              catch (final Exception e)
              {
                debugException(e);
                conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT,
                     null, e);
                poolStatistics.incrementNumConnectionsClosedDefunct();
                conn = handleDefunctConnection(conn);
                if (conn != null)
                {
                  examinedConnections.add(conn);
                }
                continue;
              }
            }
          }
        }

        try
        {
          healthCheck.ensureConnectionValidForContinuedUse(conn);
          if (availableConnections.offer(conn))
          {
            examinedConnections.add(conn);
          }
          else
          {
            conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED,
                                   null, null);
            poolStatistics.incrementNumConnectionsClosedUnneeded();
            conn.terminate(null);
          }
        }
        catch (Exception e)
        {
          debugException(e);
          conn = handleDefunctConnection(conn);
          if (conn != null)
          {
            examinedConnections.add(conn);
          }
        }
      }
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public int getCurrentAvailableConnections()
  {
    return availableConnections.size();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public int getMaximumAvailableConnections()
  {
    return numConnections;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public LDAPConnectionPoolStatistics getConnectionPoolStatistics()
  {
    return poolStatistics;
  }



  /**
   * Closes this connection pool in the event that it becomes unreferenced.
   *
   * @throws  Throwable  If an unexpected problem occurs.
   */
  @Override()
  protected void finalize()
            throws Throwable
  {
    super.finalize();

    close();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("LDAPConnectionPool(");

    final String name = connectionPoolName;
    if (name != null)
    {
      buffer.append("name='");
      buffer.append(name);
      buffer.append("', ");
    }

    buffer.append("serverSet=");
    serverSet.toString(buffer);
    buffer.append(", maxConnections=");
    buffer.append(numConnections);
    buffer.append(')');
  }
}
