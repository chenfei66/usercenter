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



import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.ldap.protocol.BindRequestProtocolOp;
import com.hwlcn.ldap.ldap.protocol.LDAPMessage;
import com.hwlcn.ldap.ldap.protocol.LDAPResponse;
import com.hwlcn.core.annotation.Extensible;
import com.hwlcn.core.annotation.InternalUseOnly;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides an API that should be used to represent an LDAPv3 SASL
 * bind request.  A SASL bind includes a SASL mechanism name and an optional set
 * of credentials.
 * <BR><BR>
 * See <A HREF="http://www.ietf.org/rfc/rfc4422.txt">RFC 4422</A> for more
 * information about the Simple Authentication and Security Layer.
 */
@Extensible()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class SASLBindRequest
       extends BindRequest
       implements ResponseAcceptor
{
  /**
   * The BER type to use for the credentials element in a simple bind request
   * protocol op.
   */
  protected static final byte CRED_TYPE_SASL = (byte) 0xA3;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -5842126553864908312L;



  // The message ID to use for LDAP messages used in bind processing.
  private int messageID;

  // The queue used to receive responses from the server.
  private final LinkedBlockingQueue<LDAPResponse> responseQueue;



  /**
   * Creates a new SASL bind request with the provided controls.
   *
   * @param  controls  The set of controls to include in this SASL bind request.
   */
  protected SASLBindRequest(final Control[] controls)
  {
    super(controls);

    messageID     = -1;
    responseQueue = new LinkedBlockingQueue<LDAPResponse>();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getBindType()
  {
    return getSASLMechanismName();
  }



  /**
   * Retrieves the name of the SASL mechanism used in this SASL bind request.
   *
   * @return  The name of the SASL mechanism used in this SASL bind request.
   */
  public abstract String getSASLMechanismName();



  /**
   * {@inheritDoc}
   */
  @Override()
  public int getLastMessageID()
  {
    return messageID;
  }



  /**
   * Sends an LDAP message to the directory server and waits for the response.
   *
   * @param  connection       The connection to the directory server.
   * @param  bindDN           The bind DN to use for the request.  It should be
   *                          {@code null} for most types of SASL bind requests.
   * @param  saslCredentials  The SASL credentials to use for the bind request.
   *                          It may be {@code null} if no credentials are
   *                          required.
   * @param  controls         The set of controls to include in the request.  It
   *                          may be {@code null} if no controls are required.
   * @param  timeoutMillis   The maximum length of time in milliseconds to wait
   *                         for a response, or zero if it should wait forever.
   *
   * @return  The bind response message returned by the directory server.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while sending the request or
   *                         reading the response, or if a timeout occurred
   *                         while waiting for the response.
   */
  protected final BindResult sendBindRequest(final LDAPConnection connection,
                                  final String bindDN,
                                  final ASN1OctetString saslCredentials,
                                  final Control[] controls,
                                  final long timeoutMillis)
            throws LDAPException
  {
    if (messageID == -1)
    {
      messageID = connection.nextMessageID();
    }

    final BindRequestProtocolOp protocolOp =
         new BindRequestProtocolOp(bindDN, getSASLMechanismName(),
                                   saslCredentials);

    final LDAPMessage requestMessage =
         new LDAPMessage(messageID, protocolOp, controls);
    return sendMessage(connection, requestMessage, timeoutMillis);
  }



  /**
   * Sends an LDAP message to the directory server and waits for the response.
   *
   * @param  connection      The connection to the directory server.
   * @param  requestMessage  The LDAP message to send to the directory server.
   * @param  timeoutMillis   The maximum length of time in milliseconds to wait
   *                         for a response, or zero if it should wait forever.
   *
   * @return  The response message received from the server.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while sending the request or
   *                         reading the response, or if a timeout occurred
   *                         while waiting for the response.
   */
  protected final BindResult sendMessage(final LDAPConnection connection,
                                         final LDAPMessage requestMessage,
                                         final long timeoutMillis)
            throws LDAPException
  {
    if (connection.synchronousMode())
    {
      return sendMessageSync(connection, requestMessage, timeoutMillis);
    }

    final int msgID = requestMessage.getMessageID();
    connection.registerResponseAcceptor(msgID, this);
    try
    {
      final long requestTime = System.nanoTime();
      connection.getConnectionStatistics().incrementNumBindRequests();
      connection.sendMessage(requestMessage);

      // Wait for and process the response.
      final LDAPResponse response;
      try
      {
        if (timeoutMillis > 0)
        {
          response = responseQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
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
             ERR_BIND_INTERRUPTED.get(connection.getHostPort()), ie);
      }

      return handleResponse(connection, response, requestTime);
    }
    finally
    {
      connection.deregisterResponseAcceptor(msgID);
    }
  }



  /**
   * Sends an LDAP message to the directory server and waits for the response.
   * This should only be used when the connection is operating in synchronous
   * mode.
   *
   * @param  connection      The connection to the directory server.
   * @param  requestMessage  The LDAP message to send to the directory server.
   * @param  timeoutMillis   The maximum length of time in milliseconds to wait
   *                         for a response, or zero if it should wait forever.
   *
   * @return  The response message received from the server.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while sending the request or
   *                         reading the response, or if a timeout occurred
   *                         while waiting for the response.
   */
  private BindResult sendMessageSync(final LDAPConnection connection,
                                     final LDAPMessage requestMessage,
                                     final long timeoutMillis)
            throws LDAPException
  {
    // Set the appropriate timeout on the socket.
    try
    {
      connection.getConnectionInternals(true).getSocket().setSoTimeout(
           (int) timeoutMillis);
    }
    catch (Exception e)
    {
      debugException(e);
    }


    final int msgID = requestMessage.getMessageID();
    final long requestTime = System.nanoTime();
    connection.getConnectionStatistics().incrementNumBindRequests();
    connection.sendMessage(requestMessage);

    while (true)
    {
      final LDAPResponse response = connection.readResponse(messageID);
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
        return handleResponse(connection, response, requestTime);
      }
    }
  }



  /**
   * Performs the necessary processing for handling a response.
   *
   * @param  connection   The connection used to read the response.
   * @param  response     The response to be processed.
   * @param  requestTime  The time the request was sent to the server.
   *
   * @return  The bind result.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs.
   */
  private BindResult handleResponse(final LDAPConnection connection,
                                    final LDAPResponse response,
                                    final long requestTime)
          throws LDAPException
  {
    if (response == null)
    {
      final long waitTime = nanosToMillis(System.nanoTime() - requestTime);
      throw new LDAPException(ResultCode.TIMEOUT,
           ERR_BIND_CLIENT_TIMEOUT.get(waitTime, connection.getHostPort()));
    }

    if (response instanceof ConnectionClosedResponse)
    {
      final ConnectionClosedResponse ccr = (ConnectionClosedResponse) response;
      final String message = ccr.getMessage();
      if (message == null)
      {
        // The connection was closed while waiting for the response.
        throw new LDAPException(ccr.getResultCode(),
             ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE.get(
                  connection.getHostPort(), toString()));
      }
      else
      {
        // The connection was closed while waiting for the response.
        throw new LDAPException(ccr.getResultCode(),
             ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE_WITH_MESSAGE.get(
                  connection.getHostPort(), toString(), message));
      }
    }

    connection.getConnectionStatistics().incrementNumBindResponses(
         System.nanoTime() - requestTime);
    return (BindResult) response;
  }



  /**
   * {@inheritDoc}
   */
  @InternalUseOnly()
  public final void responseReceived(final LDAPResponse response)
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
}
