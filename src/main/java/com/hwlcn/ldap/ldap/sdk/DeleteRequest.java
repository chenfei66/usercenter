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



import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hwlcn.ldap.asn1.ASN1Buffer;
import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.ldap.protocol.LDAPMessage;
import com.hwlcn.ldap.ldap.protocol.LDAPResponse;
import com.hwlcn.ldap.ldap.protocol.ProtocolOp;
import com.hwlcn.ldap.ldif.LDIFDeleteChangeRecord;
import com.hwlcn.ldap.util.InternalUseOnly;
import com.hwlcn.ldap.util.Mutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class implements the processing necessary to perform an LDAPv3 delete
 * operation, which removes an entry from the directory.  A delete request
 * contains the DN of the entry to remove.  It may also include a set of
 * controls to send to the server.
 * {@code DeleteRequest} objects are mutable and therefore can be altered and
 * re-used for multiple requests.  Note, however, that {@code DeleteRequest}
 * objects are not threadsafe and therefore a single {@code DeleteRequest}
 * object instance should not be used to process multiple requests at the same
 * time.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the process for performing a delete
 * operation:
 * <PRE>
 *   DeleteRequest deleteRequest =
 *        new DeleteRequest("cn=entry to delete,dc=example,dc=com");
 *
 *   try
 *   {
 *     LDAPResult deleteResult = connection.delete(deleteRequest);
 *
 *     System.out.println("The entry was successfully deleted.");
 *   }
 *   catch (LDAPException le)
 *   {
 *     System.err.println("The delete operation failed.");
 *   }
 * </PRE>
 */
@Mutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class DeleteRequest
       extends UpdatableLDAPRequest
       implements ReadOnlyDeleteRequest, ResponseAcceptor, ProtocolOp
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -6126029442850884239L;



  // The message ID from the last LDAP message sent from this request.
  private int messageID = -1;

  // The queue that will be used to receive response messages from the server.
  private final LinkedBlockingQueue<LDAPResponse> responseQueue =
       new LinkedBlockingQueue<LDAPResponse>();

  // The DN of the entry to delete.
  private String dn;



  /**
   * Creates a new delete request with the provided DN.
   *
   * @param  dn  The DN of the entry to delete.  It must not be {@code null}.
   */
  public DeleteRequest(final String dn)
  {
    super(null);

    ensureNotNull(dn);

    this.dn = dn;
  }



  /**
   * Creates a new delete request with the provided DN.
   *
   * @param  dn        The DN of the entry to delete.  It must not be
   *                   {@code null}.
   * @param  controls  The set of controls to include in the request.
   */
  public DeleteRequest(final String dn, final Control[] controls)
  {
    super(controls);

    ensureNotNull(dn);

    this.dn = dn;
  }



  /**
   * Creates a new delete request with the provided DN.
   *
   * @param  dn  The DN of the entry to delete.  It must not be {@code null}.
   */
  public DeleteRequest(final DN dn)
  {
    super(null);

    ensureNotNull(dn);

    this.dn = dn.toString();
  }



  /**
   * Creates a new delete request with the provided DN.
   *
   * @param  dn        The DN of the entry to delete.  It must not be
   *                   {@code null}.
   * @param  controls  The set of controls to include in the request.
   */
  public DeleteRequest(final DN dn, final Control[] controls)
  {
    super(controls);

    ensureNotNull(dn);

    this.dn = dn.toString();
  }



  /**
   * {@inheritDoc}
   */
  public String getDN()
  {
    return dn;
  }



  /**
   * Specifies the DN of the entry to delete.
   *
   * @param  dn  The DN of the entry to delete.  It must not be {@code null}.
   */
  public void setDN(final String dn)
  {
    ensureNotNull(dn);

    this.dn = dn;
  }



  /**
   * Specifies the DN of the entry to delete.
   *
   * @param  dn  The DN of the entry to delete.  It must not be {@code null}.
   */
  public void setDN(final DN dn)
  {
    ensureNotNull(dn);

    this.dn = dn.toString();
  }



  /**
   * {@inheritDoc}
   */
  public byte getProtocolOpType()
  {
    return LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST;
  }



  /**
   * {@inheritDoc}
   */
  public void writeTo(final ASN1Buffer buffer)
  {
    buffer.addOctetString(LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST, dn);
  }



  /**
   * Encodes the delete request protocol op to an ASN.1 element.
   *
   * @return  The ASN.1 element with the encoded delete request protocol op.
   */
  public ASN1Element encodeProtocolOp()
  {
    return new ASN1OctetString(LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST, dn);
  }



  /**
   * Sends this delete request to the directory server over the provided
   * connection and returns the associated response.
   *
   * @param  connection  The connection to use to communicate with the directory
   *                     server.
   * @param  depth       The current referral depth for this request.  It should
   *                     always be one for the initial request, and should only
   *                     be incremented when following referrals.
   *
   * @return  An LDAP result object that provides information about the result
   *          of the delete processing.
   *
   * @throws  LDAPException  If a problem occurs while sending the request or
   *                         reading the response.
   */
  @Override()
  protected LDAPResult process(final LDAPConnection connection, final int depth)
            throws LDAPException
  {
    if (connection.synchronousMode())
    {
      return processSync(connection, depth,
           connection.getConnectionOptions().autoReconnect());
    }

    final long requestTime = System.nanoTime();
    processAsync(connection, null);

    try
    {
      // Wait for and process the response.
      final LDAPResponse response;
      try
      {
        final long responseTimeout = getResponseTimeoutMillis(connection);
        if (responseTimeout > 0)
        {
          response = responseQueue.poll(responseTimeout, TimeUnit.MILLISECONDS);
        }
        else
        {
          response = responseQueue.take();
        }
      }
      catch (InterruptedException ie)
      {
        debugException(ie);
        throw new LDAPException(ResultCode.LOCAL_ERROR,
             ERR_DELETE_INTERRUPTED.get(connection.getHostPort()), ie);
      }

      return handleResponse(connection, response,  requestTime, depth, false);
    }
    finally
    {
      connection.deregisterResponseAcceptor(messageID);
    }
  }



  /**
   * Sends this delete request to the directory server over the provided
   * connection and returns the message ID for the request.
   *
   * @param  connection      The connection to use to communicate with the
   *                         directory server.
   * @param  resultListener  The async result listener that is to be notified
   *                         when the response is received.  It may be
   *                         {@code null} only if the result is to be processed
   *                         by this class.
   *
   * @return  The async request ID created for the operation, or {@code null} if
   *          the provided {@code resultListener} is {@code null} and the
   *          operation will not actually be processed asynchronously.
   *
   * @throws  LDAPException  If a problem occurs while sending the request.
   */
  AsyncRequestID processAsync(final LDAPConnection connection,
                              final AsyncResultListener resultListener)
                 throws LDAPException
  {
    // Create the LDAP message.
    messageID = connection.nextMessageID();
    final LDAPMessage message = new LDAPMessage(messageID, this, getControls());


    // If the provided async result listener is {@code null}, then we'll use
    // this class as the message acceptor.  Otherwise, create an async helper
    // and use it as the message acceptor.
    final AsyncRequestID asyncRequestID;
    if (resultListener == null)
    {
      asyncRequestID = null;
      connection.registerResponseAcceptor(messageID, this);
    }
    else
    {
      final AsyncHelper helper = new AsyncHelper(connection,
           OperationType.DELETE, messageID, resultListener,
           getIntermediateResponseListener());
      connection.registerResponseAcceptor(messageID, helper);
      asyncRequestID = helper.getAsyncRequestID();

      final long timeout = getResponseTimeoutMillis(connection);
      if (timeout > 0L)
      {
        final Timer timer = connection.getTimer();
        final AsyncTimeoutTimerTask timerTask =
             new AsyncTimeoutTimerTask(helper);
        timer.schedule(timerTask, timeout);
        asyncRequestID.setTimerTask(timerTask);
      }
    }


    // Send the request to the server.
    try
    {
      debugLDAPRequest(this);
      connection.getConnectionStatistics().incrementNumDeleteRequests();
      connection.sendMessage(message);
      return asyncRequestID;
    }
    catch (LDAPException le)
    {
      debugException(le);

      connection.deregisterResponseAcceptor(messageID);
      throw le;
    }
  }



  /**
   * Processes this delete operation in synchronous mode, in which the same
   * thread will send the request and read the response.
   *
   * @param  connection  The connection to use to communicate with the directory
   *                     server.
   * @param  depth       The current referral depth for this request.  It should
   *                     always be one for the initial request, and should only
   *                     be incremented when following referrals.
   * @param  allowRetry  Indicates whether the request may be re-tried on a
   *                     re-established connection if the initial attempt fails
   *                     in a way that indicates the connection is no longer
   *                     valid and autoReconnect is true.
   *
   * @return  An LDAP result object that provides information about the result
   *          of the delete processing.
   *
   * @throws  LDAPException  If a problem occurs while sending the request or
   *                         reading the response.
   */
  private LDAPResult processSync(final LDAPConnection connection,
                                 final int depth, final boolean allowRetry)
          throws LDAPException
  {
    // Create the LDAP message.
    messageID = connection.nextMessageID();
    final LDAPMessage message =
         new LDAPMessage(messageID,  this, getControls());


    // Set the appropriate timeout on the socket.
    try
    {
      connection.getConnectionInternals(true).getSocket().setSoTimeout(
           (int) getResponseTimeoutMillis(connection));
    }
    catch (Exception e)
    {
      debugException(e);
    }


    // Send the request to the server.
    final long requestTime = System.nanoTime();
    debugLDAPRequest(this);
    connection.getConnectionStatistics().incrementNumDeleteRequests();
    try
    {
      connection.sendMessage(message);
    }
    catch (final LDAPException le)
    {
      debugException(le);

      if (allowRetry)
      {
        final LDAPResult retryResult = reconnectAndRetry(connection, depth,
             le.getResultCode());
        if (retryResult != null)
        {
          return retryResult;
        }
      }

      throw le;
    }

    while (true)
    {
      final LDAPResponse response;
      try
      {
        response = connection.readResponse(messageID);
      }
      catch (final LDAPException le)
      {
        debugException(le);

        if ((le.getResultCode() == ResultCode.TIMEOUT) &&
            connection.getConnectionOptions().abandonOnTimeout())
        {
          connection.abandon(messageID);
        }

        if (allowRetry)
        {
          final LDAPResult retryResult = reconnectAndRetry(connection, depth,
               le.getResultCode());
          if (retryResult != null)
          {
            return retryResult;
          }
        }

        throw le;
      }

      if (response instanceof IntermediateResponse)
      {
        final IntermediateResponseListener listener =
             getIntermediateResponseListener();
        if (listener != null)
        {
          listener.intermediateResponseReturned(
               (IntermediateResponse) response);
        }
      }
      else
      {
        return handleResponse(connection, response, requestTime, depth,
             allowRetry);
      }
    }
  }



  /**
   * Performs the necessary processing for handling a response.
   *
   * @param  connection   The connection used to read the response.
   * @param  response     The response to be processed.
   * @param  requestTime  The time the request was sent to the server.
   * @param  depth        The current referral depth for this request.  It
   *                      should always be one for the initial request, and
   *                      should only be incremented when following referrals.
   * @param  allowRetry   Indicates whether the request may be re-tried on a
   *                      re-established connection if the initial attempt fails
   *                      in a way that indicates the connection is no longer
   *                      valid and autoReconnect is true.
   *
   * @return  The delete result.
   *
   * @throws  LDAPException  If a problem occurs.
   */
  private LDAPResult handleResponse(final LDAPConnection connection,
                                    final LDAPResponse response,
                                    final long requestTime, final int depth,
                                    final boolean allowRetry)
          throws LDAPException
  {
    if (response == null)
    {
      final long waitTime = nanosToMillis(System.nanoTime() - requestTime);
      if (connection.getConnectionOptions().abandonOnTimeout())
      {
        connection.abandon(messageID);
      }

      throw new LDAPException(ResultCode.TIMEOUT,
           ERR_DELETE_CLIENT_TIMEOUT.get(waitTime, connection.getHostPort()));
    }

    connection.getConnectionStatistics().incrementNumDeleteResponses(
         System.nanoTime() - requestTime);
    if (response instanceof ConnectionClosedResponse)
    {
      // The connection was closed while waiting for the response.
      if (allowRetry)
      {
        final LDAPResult retryResult = reconnectAndRetry(connection, depth,
             ResultCode.SERVER_DOWN);
        if (retryResult != null)
        {
          return retryResult;
        }
      }

      final ConnectionClosedResponse ccr = (ConnectionClosedResponse) response;
      final String message = ccr.getMessage();
      if (message == null)
      {
        throw new LDAPException(ccr.getResultCode(),
             ERR_CONN_CLOSED_WAITING_FOR_DELETE_RESPONSE.get(
                  connection.getHostPort(), toString()));
      }
      else
      {
        throw new LDAPException(ccr.getResultCode(),
             ERR_CONN_CLOSED_WAITING_FOR_DELETE_RESPONSE_WITH_MESSAGE.get(
                  connection.getHostPort(), toString(), message));
      }
    }

    final LDAPResult result = (LDAPResult) response;
    if ((result.getResultCode().equals(ResultCode.REFERRAL)) &&
        followReferrals(connection))
    {
      if (depth >= connection.getConnectionOptions().getReferralHopLimit())
      {
        return new LDAPResult(messageID, ResultCode.REFERRAL_LIMIT_EXCEEDED,
                              ERR_TOO_MANY_REFERRALS.get(),
                              result.getMatchedDN(), result.getReferralURLs(),
                              result.getResponseControls());
      }

      return followReferral(result, connection, depth);
    }
    else
    {
      if (allowRetry)
      {
        final LDAPResult retryResult = reconnectAndRetry(connection, depth,
             result.getResultCode());
        if (retryResult != null)
        {
          return retryResult;
        }
      }

      return result;
    }
  }



  /**
   * Attempts to re-establish the connection and retry processing this request
   * on it.
   *
   * @param  connection  The connection to be re-established.
   * @param  depth       The current referral depth for this request.  It should
   *                     always be one for the initial request, and should only
   *                     be incremented when following referrals.
   * @param  resultCode  The result code for the previous operation attempt.
   *
   * @return  The result from re-trying the add, or {@code null} if it could not
   *          be re-tried.
   */
  private LDAPResult reconnectAndRetry(final LDAPConnection connection,
                                       final int depth,
                                       final ResultCode resultCode)
  {
    try
    {
      // We will only want to retry for certain result codes that indicate a
      // connection problem.
      switch (resultCode.intValue())
      {
        case ResultCode.SERVER_DOWN_INT_VALUE:
        case ResultCode.DECODING_ERROR_INT_VALUE:
        case ResultCode.CONNECT_ERROR_INT_VALUE:
          connection.reconnect();
          return processSync(connection, depth, false);
      }
    }
    catch (final Exception e)
    {
      debugException(e);
    }

    return null;
  }



  /**
   * Attempts to follow a referral to perform a delete operation in the target
   * server.
   *
   * @param  referralResult  The LDAP result object containing information about
   *                         the referral to follow.
   * @param  connection      The connection on which the referral was received.
   * @param  depth           The number of referrals followed in the course of
   *                         processing this request.
   *
   * @return  The result of attempting to process the delete operation by
   *          following the referral.
   *
   * @throws  LDAPException  If a problem occurs while attempting to establish
   *                         the referral connection, sending the request, or
   *                         reading the result.
   */
  private LDAPResult followReferral(final LDAPResult referralResult,
                                    final LDAPConnection connection,
                                    final int depth)
          throws LDAPException
  {
    for (final String urlString : referralResult.getReferralURLs())
    {
      try
      {
        final LDAPURL referralURL = new LDAPURL(urlString);
        final String host = referralURL.getHost();

        if (host == null)
        {
          // We can't handle a referral in which there is no host.
          continue;
        }

        final DeleteRequest deleteRequest;
        if (referralURL.baseDNProvided())
        {
          deleteRequest = new DeleteRequest(referralURL.getBaseDN(),
                                            getControls());
        }
        else
        {
          deleteRequest = this;
        }

        final LDAPConnection referralConn = connection.getReferralConnector().
             getReferralConnection(referralURL, connection);
        try
        {
          return deleteRequest.process(referralConn, depth+1);
        }
        finally
        {
          referralConn.setDisconnectInfo(DisconnectType.REFERRAL, null, null);
          referralConn.close();
        }
      }
      catch (LDAPException le)
      {
        debugException(le);
      }
    }

    // If we've gotten here, then we could not follow any of the referral URLs,
    // so we'll just return the original referral result.
    return referralResult;
  }



  /**
   * {@inheritDoc}
   */
  @InternalUseOnly()
  public void responseReceived(final LDAPResponse response)
         throws LDAPException
  {
    try
    {
      responseQueue.put(response);
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.LOCAL_ERROR,
           ERR_EXCEPTION_HANDLING_RESPONSE.get(getExceptionMessage(e)), e);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public int getLastMessageID()
  {
    return messageID;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public OperationType getOperationType()
  {
    return OperationType.DELETE;
  }



  /**
   * {@inheritDoc}
   */
  public DeleteRequest duplicate()
  {
    return duplicate(getControls());
  }



  /**
   * {@inheritDoc}
   */
  public DeleteRequest duplicate(final Control[] controls)
  {
    final DeleteRequest r = new DeleteRequest(dn, controls);

    if (followReferralsInternal() != null)
    {
      r.setFollowReferrals(followReferralsInternal());
    }

    r.setResponseTimeoutMillis(getResponseTimeoutMillis(null));

    return r;
  }



  /**
   * {@inheritDoc}
   */
  public LDIFDeleteChangeRecord toLDIFChangeRecord()
  {
    return new LDIFDeleteChangeRecord(this);
  }



  /**
   * {@inheritDoc}
   */
  public String[] toLDIF()
  {
    return toLDIFChangeRecord().toLDIF();
  }



  /**
   * {@inheritDoc}
   */
  public String toLDIFString()
  {
    return toLDIFChangeRecord().toLDIFString();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("DeleteRequest(dn='");
    buffer.append(dn);
    buffer.append('\'');

    final Control[] controls = getControls();
    if (controls.length > 0)
    {
      buffer.append(", controls={");
      for (int i=0; i < controls.length; i++)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }

        buffer.append(controls[i]);
      }
      buffer.append('}');
    }

    buffer.append(')');
  }
}
