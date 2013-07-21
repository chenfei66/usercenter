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



import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import com.hwlcn.ldap.ldap.protocol.LDAPResponse;
import com.hwlcn.ldap.util.DebugType;
import com.hwlcn.core.annotation.InternalUseOnly;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;



/**
 * This class provides a helper class used for processing asynchronous add,
 * delete, modify, and modify DN operations.
 */
@InternalUseOnly()
final class AsyncHelper
      implements CommonAsyncHelper, IntermediateResponseListener
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 7186731025240177443L;



  // The async request ID created for the associated operation.
  private final AsyncRequestID asyncRequestID;

  // The async result listener to be notified when the response arrives.
  private final AsyncResultListener resultListener;

  // Indicates whether the final response has been returned.
  private final AtomicBoolean responseReturned;

  // The BER type for the operation with which this helper is associated.
  private final OperationType operationType;

  // The intermediate response listener to be notified of any intermediate
  // response messages received.
  private final IntermediateResponseListener intermediateResponseListener;

  // The connection with which this async helper is associated.
  private final LDAPConnection connection;

  // The time that this async helper was created.
  private final long createTime;



  /**
   * Creates a new instance of this async helper that will be used to forward
   * decoded results to the provided async result listener.
   *
   * @param  connection                    The connection with which this async
   *                                       helper is associated.
   * @param  operationType                 The operation type for the associated
   *                                       operation.
   * @param  messageID                     The message ID for the associated
   *                                       operation.
   * @param  resultListener                The async result listener to be
   *                                       notified when the response arrives.
   * @param  intermediateResponseListener  The intermediate response listener to
   *                                       be notified of any intermediate
   *                                       response messages received.
   */
  @InternalUseOnly()
  AsyncHelper(final LDAPConnection connection,
              final OperationType operationType, final int messageID,
              final AsyncResultListener resultListener,
              final IntermediateResponseListener intermediateResponseListener)
  {
    this.connection                   = connection;
    this.operationType                = operationType;
    this.resultListener               = resultListener;
    this.intermediateResponseListener = intermediateResponseListener;

    asyncRequestID   = new AsyncRequestID(messageID, connection);
    responseReturned = new AtomicBoolean(false);
    createTime       = System.nanoTime();
  }



  /**
   * {@inheritDoc}
   */
  public AsyncRequestID getAsyncRequestID()
  {
    return asyncRequestID;
  }



  /**
   * {@inheritDoc}
   */
  public LDAPConnection getConnection()
  {
    return connection;
  }



  /**
   * {@inheritDoc}
   */
  public long getCreateTimeNanos()
  {
    return createTime;
  }



  /**
   * {@inheritDoc}
   */
  public OperationType getOperationType()
  {
    return operationType;
  }



  /**
   * {@inheritDoc}
   */
  @InternalUseOnly()
  public void responseReceived(final LDAPResponse response)
         throws LDAPException
  {
    if (! responseReturned.compareAndSet(false, true))
    {
      return;
    }

    final long responseTime = System.nanoTime() - createTime;
    if (response instanceof ConnectionClosedResponse)
    {
      final ConnectionClosedResponse ccr = (ConnectionClosedResponse) response;
      final String message = ccr.getMessage();
      if (message == null)
      {
        throw new LDAPException(ccr.getResultCode(),
             ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE.get());
      }
      else
      {
        throw new LDAPException(ccr.getResultCode(),
             ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE_WITH_MESSAGE.get(
                  message));
      }

    }

    switch (operationType)
    {
      case ADD:
        connection.getConnectionStatistics().incrementNumAddResponses(
             responseTime);
        break;
      case DELETE:
        connection.getConnectionStatistics().incrementNumDeleteResponses(
             responseTime);
        break;
      case MODIFY:
        connection.getConnectionStatistics().incrementNumModifyResponses(
             responseTime);
        break;
      case MODIFY_DN:
        connection.getConnectionStatistics().incrementNumModifyDNResponses(
             responseTime);
        break;
    }

    final LDAPResult result = (LDAPResult) response;
    resultListener.ldapResultReceived(asyncRequestID, result);
    asyncRequestID.setResult(result);
  }



  /**
   * {@inheritDoc}
   */
  @InternalUseOnly()
  public void intermediateResponseReturned(
                   final IntermediateResponse intermediateResponse)
  {
    if (intermediateResponseListener == null)
    {
      debug(Level.WARNING, DebugType.LDAP,
            WARN_INTERMEDIATE_RESPONSE_WITH_NO_LISTENER.get(
                 String.valueOf(intermediateResponse)));
    }
    else
    {
      intermediateResponseListener.intermediateResponseReturned(
           intermediateResponse);
    }
  }
}
