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



import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class provides a SASL ANONYMOUS bind request implementation as described
 * in <A HREF="http://www.ietf.org/rfc/rfc4505.txt">RFC 4505</A>.  Binding with
 * The ANONYMOUS SASL mechanism is essentially equivalent to using an anonymous
 * simple bind (i.e., a simple bind with an empty password), although the SASL
 * ANONYMOUS mechanism does provide the ability to include additional trace
 * information with the request that may be logged or otherwise handled by
 * the server.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the process for performing an ANONYMOUS
 * bind, including a trace string of "Hello, world!" against a directory server:
 * <PRE>
 *   ANONYMOUSBindRequest bindRequest =
 *        new ANONYMOUSBindRequest("Hello, world!");
 *   try
 *   {
 *     BindResult bindResult = connection.bind(bindRequest);
 *     // If we get here, then the bind was successful.
 *   }
 *   catch (LDAPException le)
 *   {
 *     // The bind failed for some reason.
 *   }
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class ANONYMOUSBindRequest
       extends SASLBindRequest
{
  /**
   * The name for the ANONYMOUS SASL mechanism.
   */
  public static final String ANONYMOUS_MECHANISM_NAME = "ANONYMOUS";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 4259102841471750866L;



  // The trace string that should be included in the bind request, if available.
  private final String traceString;



  /**
   * Creates a new SASL ANONYMOUS bind request with no trace string and no
   * controls.
   */
  public ANONYMOUSBindRequest()
  {
    this(null, NO_CONTROLS);
  }



  /**
   * Creates a new SASL ANONYMOUS bind request with the provided trace string
   * and no controls.
   *
   * @param  traceString  The trace string to include in the bind request, or
   *                      {@code null} if no trace string is to be provided.
   */
  public ANONYMOUSBindRequest(final String traceString)
  {
    this(traceString, NO_CONTROLS);
  }



  /**
   * Creates a new SASL ANONYMOUS bind request with the provided set of controls
   * and no trace string.
   *
   * @param  controls     The set of controls to include in the request.
   */
  public ANONYMOUSBindRequest(final Control... controls)
  {
    this(null, controls);
  }



  /**
   * Creates a new SASL ANONYMOUS bind request with the provided trace string
   * and controls.
   *
   * @param  traceString  The trace string to include in the bind request, or
   *                      {@code null} if no trace string is to be provided.
   * @param  controls     The set of controls to include in the request.
   */
  public ANONYMOUSBindRequest(final String traceString,
                              final Control... controls)
  {
    super(controls);

    this.traceString = traceString;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getSASLMechanismName()
  {
    return ANONYMOUS_MECHANISM_NAME;
  }



  /**
   * Retrieves the trace string that will be included with the bind request.
   *
   * @return  The trace string that will be included with the bind request, or
   *          {@code null} if there is to be no trace string.
   */
  public String getTraceString()
  {
    return traceString;
  }



  /**
   * Sends this bind request to the target server over the provided connection
   * and returns the corresponding response.
   *
   * @param  connection  The connection to use to send this bind request to the
   *                     server and read the associated response.
   * @param  depth       The current referral depth for this request.  It should
   *                     always be one for the initial request, and should only
   *                     be incremented when following referrals.
   *
   * @return  The bind response read from the server.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while sending the request or
   *                         reading the response.
   */
  @Override()
  protected BindResult process(final LDAPConnection connection, final int depth)
            throws LDAPException
  {
    ASN1OctetString credentials = null;
    if ((traceString == null) || (traceString.length() == 0))
    {
      credentials = new ASN1OctetString(traceString);
    }

    return sendBindRequest(connection, null, credentials, getControls(),
                           getResponseTimeoutMillis(connection));
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public ANONYMOUSBindRequest getRebindRequest(final String host,
                                               final int port)
  {
    return new ANONYMOUSBindRequest(traceString, getControls());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public ANONYMOUSBindRequest duplicate()
  {
    return duplicate(getControls());
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public ANONYMOUSBindRequest duplicate(final Control[] controls)
  {
    final ANONYMOUSBindRequest bindRequest =
         new ANONYMOUSBindRequest(traceString, controls);
    bindRequest.setResponseTimeoutMillis(getResponseTimeoutMillis(null));
    return bindRequest;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("ANONYMOUSBindRequest(");
    if (traceString != null)
    {
      buffer.append(", trace='");
      buffer.append(traceString);
      buffer.append('\'');
    }

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
