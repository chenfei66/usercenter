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
package com.hwlcn.ldap.ldap.sdk.controls;



import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.controls.ControlMessages.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class provides an implementation of the proxied authorization V2
 * request control, as defined in
 * <A HREF="http://www.ietf.org/rfc/rfc4370.txt">RFC 4370</A>.  It may be used
 * to request that the associated operation be performed as if it has been
 * requested by some other user.
 * <BR><BR>
 * The target authorization identity for this control is specified as an
 * "authzId" value as described in section 5.2.1.8 of
 * <A HREF="http://www.ietf.org/rfc/rfc4513.txt">RFC 4513</A>.  That is, it
 * should be either "dn:" followed by the distinguished name of the target user,
 * or "u:" followed by the username.  If the "u:" form is used, then the
 * mechanism used to resolve the provided username to an entry may vary from
 * server to server.
 * <BR><BR>
 * This control may be used in conjunction with add, delete, compare, delete,
 * extended, modify, modify DN, and search requests.  In that case, the
 * associated operation will be processed under the authority of the specified
 * authorization identity rather than the identity associated with the client
 * connection (i.e., the user as whom that connection is bound).  Note that
 * because of the inherent security risks associated with the use of the proxied
 * authorization control, most directory servers which support its use enforce
 * strict restrictions on the users that are allowed to request this control.
 * If a user attempts to use the proxied authorization V2 request control and
 * does not have sufficient permission to do so, then the server will return a
 * failure response with the {@link ResultCode#AUTHORIZATION_DENIED} result
 * code.
 * <BR><BR>
 * There is no corresponding response control for this request control.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the use of the proxied authorization V2
 * control to delete an entry under the authority of the user with DN
 * "uid=john.doe,ou=People,dc=example,dc=com":
 * <PRE>
 *   DeleteRequest deleteRequest =
 *        new DeleteRequest("cn=no longer needed,dc=example,dc=com");
 *   deleteRequest.addControl(new ProxiedAuthorizationV2RequestControl(
 *        "dn:uid=john.doe,ou=People,dc=example,dc=com"));
 *
 *   try
 *   {
 *     LDAPResult deleteResult = connection.delete(deleteRequest);
 *     // If we got here, then the delete was successful.
 *   }
 *   catch (LDAPException le)
 *   {
 *     if (le.getResultCode() == ResultCode.AUTHORIZATION_DENIED)
 *     {
 *       // The delete failed because the authenticated user does not have
 *       // permission to use the proxied authorization V2 control.
 *     }
 *     else
 *     {
 *       // The delete failed for some other reason.
 *     }
 *   }
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ProxiedAuthorizationV2RequestControl
       extends Control
{
  /**
   * The OID (2.16.840.1.113730.3.4.18) for the proxied authorization v2 request
   * control.
   */
  public static final String PROXIED_AUTHORIZATION_V2_REQUEST_OID =
       "2.16.840.1.113730.3.4.18";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 1054244283964851067L;



  // The authorization ID string that may be used to identify the user under
  // whose authorization the associated operation should be performed.
  private final String authorizationID;



  /**
   * Creates a new proxied authorization V2 request control that will proxy as
   * the specified user.
   *
   * @param  authorizationID  The authorization ID string that will be used to
   *                          identify the user under whose authorization the
   *                          associated operation should be performed.  It may
   *                          take one of three forms:  it can be an empty
   *                          string (to indicate that the operation should use
   *                          anonymous authorization), a string that begins
   *                          with "dn:" and is followed by the DN of the target
   *                          user, or a string that begins with "u:" and is
   *                          followed by the username for the target user
   *                          (where the process of mapping the provided
   *                          username to the corresponding entry will depend on
   *                          the server configuration).  It must not be
   *                          {@code null}.
   */
  public ProxiedAuthorizationV2RequestControl(final String authorizationID)
  {
    super(PROXIED_AUTHORIZATION_V2_REQUEST_OID, true,
          new ASN1OctetString(authorizationID));

    ensureNotNull(authorizationID);

    this.authorizationID = authorizationID;
  }



  /**
   * Creates a new proxied authorization v2 request control which is decoded
   * from the provided generic control.
   *
   * @param  control  The generic control to be decoded as a proxied
   *                  authorization v2 request control.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         proxied authorization v2 request control.
   */
  public ProxiedAuthorizationV2RequestControl(final Control control)
         throws LDAPException
  {
    super(control);

    final ASN1OctetString value = control.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_PROXY_V2_NO_VALUE.get());
    }

    authorizationID = value.stringValue();
  }



  /**
   * Retrieves the authorization ID string that will be used to identify the
   * user under whose authorization the associated operation should be
   * performed.
   *
   * @return  The authorization ID string that will be used to identify the user
   *          under whose authorization the associated operation should be
   *          performed.
   */
  public String getAuthorizationID()
  {
    return authorizationID;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_PROXIED_AUTHZ_V2_REQUEST.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("ProxiedAuthorizationV2RequestControl(authorizationID='");
    buffer.append(authorizationID);
    buffer.append("')");
  }
}
