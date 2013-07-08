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
package com.hwlcn.ldap.ldap.sdk.extensions;



import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1Integer;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1Sequence;
import com.hwlcn.ldap.ldap.sdk.AsyncRequestID;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.ExtendedRequest;
import com.hwlcn.ldap.ldap.sdk.ExtendedResult;
import com.hwlcn.ldap.ldap.sdk.LDAPConnection;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.extensions.ExtOpMessages.*;
import static com.hwlcn.ldap.util.Debug.*;



/**
 * This class provides an implementation of the LDAP cancel extended request as
 * defined in <A HREF="http://www.ietf.org/rfc/rfc3909.txt">RFC 3909</A>.  It
 * may be used to request that the server interrupt processing on another
 * operation in progress on the same connection.  It behaves much like the
 * abandon operation, with the exception that both the cancel request and the
 * operation that is canceled will receive responses, whereas an abandon request
 * never returns a response, and the operation that is abandoned will also not
 * receive a response if the abandon is successful.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example initiates an asynchronous modify operation and then
 * attempts to cancel it:
 * <PRE>
 *   Modification mod = new Modification(ModificationType.REPLACE,
 *        "description", "This is the new description.");
 *   ModifyRequest modifyRequest =
 *        new ModifyRequest("dc=example,dc=com", mod);
 *
 *   AsyncRequestID asyncRequestID =
 *        connection.asyncModify(modifyRequest, myAsyncResultListener);
 *
 *   // Assume that we've waited a reasonable amount of time but the modify
 *   // hasn't completed yet so we'll try to cancel it.
 *
 *   CancelExtendedRequest cancelRequest =
 *        new CancelExtendedRequest(asyncRequestID);
 *
 *   // NOTE:  The processExtendedOperation method will only throw an exception
 *   // if a problem occurs while trying to send the request or read the
 *   // response.  It will not throw an exception because of a non-success
 *   // response.  That's good for us in this case because the cancel result
 *   // should never be "SUCCESS".
 *   ExtendedResult cancelResult =
 *        connection.processExtendedOperation(cancelRequest);
 *   switch (cancelResult.getResultCode())
 *   {
 *     case ResultCode.CANCELED:
 *       System.out.println("The operation was successfully canceled.");
 *       break;
 *     case ResultCode.NO_SUCH_OPERATION:
 *       System.out.println("The server didn't know anything about the " +
 *                          "operation.  Maybe it's already completed.");
 *       break;
 *     case ResultCode.TOO_LATE:
 *       System.out.println("It was too late in the operation processing " +
 *                          "to cancel the operation.");
 *       break;
 *     case ResultCode.CANNOT_CANCEL:
 *       System.out.println("The target operation is not one that could be " +
 *                          "canceled.");
 *       break;
 *     default:
 *       System.err.println("An error occurred while processing the cancel " +
 *                          "request.");
 *       break;
 *   }
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class CancelExtendedRequest
       extends ExtendedRequest
{
  /**
   * The OID (1.3.6.1.1.8) for the cancel extended request.
   */
  public static final String CANCEL_REQUEST_OID = "1.3.6.1.1.8";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -7170687636394194183L;



  // The message ID of the request to cancel.
  private final int targetMessageID;



  /**
   * Creates a new cancel extended request that will cancel the request with the
   * specified async request ID.
   *
   * @param  requestID  The async request ID of the request to cancel.  It must
   *                    not be {@code null}.
   */
  public CancelExtendedRequest(final AsyncRequestID requestID)
  {
    this(requestID.getMessageID(), null);
  }



  /**
   * Creates a new cancel extended request that will cancel the request with the
   * specified message ID.
   *
   * @param  targetMessageID  The message ID of the request to cancel.
   */
  public CancelExtendedRequest(final int targetMessageID)
  {
    this(targetMessageID, null);
  }



  /**
   * Creates a new cancel extended request that will cancel the request with the
   * specified request ID.
   *
   * @param  requestID  The async request ID of the request to cancel.  It must
   *                    not be {@code null}.
   * @param  controls   The set of controls to include in the request.
   */
  public CancelExtendedRequest(final AsyncRequestID requestID,
                               final Control[] controls)
  {
    this(requestID.getMessageID(), controls);
  }



  /**
   * Creates a new cancel extended request that will cancel the request with the
   * specified message ID.
   *
   * @param  targetMessageID  The message ID of the request to cancel.
   * @param  controls         The set of controls to include in the request.
   */
  public CancelExtendedRequest(final int targetMessageID,
                               final Control[] controls)
  {
    super(CANCEL_REQUEST_OID, encodeValue(targetMessageID), controls);

    this.targetMessageID = targetMessageID;
  }



  /**
   * Creates a new cancel extended request from the provided generic extended
   * request.
   *
   * @param  extendedRequest  The generic extended request to use to create this
   *                          cancel extended request.
   *
   * @throws  LDAPException  If a problem occurs while decoding the request.
   */
  public CancelExtendedRequest(final ExtendedRequest extendedRequest)
         throws LDAPException
  {
    super(extendedRequest);

    final ASN1OctetString value = extendedRequest.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_CANCEL_REQUEST_NO_VALUE.get());
    }

    try
    {
      final ASN1Element valueElement = ASN1Element.decode(value.getValue());
      final ASN1Element[] elements =
           ASN1Sequence.decodeAsSequence(valueElement).elements();
      targetMessageID = ASN1Integer.decodeAsInteger(elements[0]).intValue();
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_CANCEL_REQUEST_CANNOT_DECODE.get(e), e);
    }
  }



  /**
   * Generates a properly-encoded request value for this cancel extended
   * request.
   *
   * @param  targetMessageID  The message ID of the request to cancel.
   *
   * @return  An ASN.1 octet string containing the encoded request value.
   */
  private static ASN1OctetString encodeValue(final int targetMessageID)
  {
    final ASN1Element[] sequenceValues =
    {
      new ASN1Integer(targetMessageID)
    };

    return new ASN1OctetString(new ASN1Sequence(sequenceValues).encode());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  protected ExtendedResult process(final LDAPConnection connection,
                                   final int depth)
            throws LDAPException
  {
    if (connection.synchronousMode())
    {
      throw new LDAPException(ResultCode.NOT_SUPPORTED,
           ERR_CANCEL_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE.get());
    }

    return super.process(connection, depth);
  }



  /**
   * Retrieves the message ID of the request to cancel.
   *
   * @return  The message ID of the request to cancel.
   */
  public int getTargetMessageID()
  {
    return targetMessageID;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public CancelExtendedRequest duplicate()
  {
    return duplicate(getControls());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public CancelExtendedRequest duplicate(final Control[] controls)
  {
    final CancelExtendedRequest cancelRequest =
         new CancelExtendedRequest(targetMessageID, controls);
    cancelRequest.setResponseTimeoutMillis(getResponseTimeoutMillis(null));
    return cancelRequest;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getExtendedRequestName()
  {
    return INFO_EXTENDED_REQUEST_NAME_CANCEL.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("CancelExtendedRequest(targetMessageID=");
    buffer.append(targetMessageID);

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
