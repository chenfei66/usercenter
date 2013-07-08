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



import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.controls.ControlMessages.*;



/**
 * This class provides an implementation of the ManageDsaIT control as described
 * in <A HREF="http://www.ietf.org/rfc/rfc3296.txt">RFC 3296</A>.  This control
 * may be used to request that the directory server treat all entries as if they
 * were regular entries.
 * <BR><BR>
 * One of the most common uses of the ManageDsaIT control is to request that the
 * directory server to treat an entry containing the "{@code referral}" object
 * class as a regular entry rather than a smart referral.  Normally, when the
 * server encounters an entry with the {@code referral} object class, it sends
 * a referral with the URLs contained in the {@code ref} attribute of that
 * entry.  However, if the ManageDsaIT control is included then the operation
 * will attempt to operate on the referral definition itself rather than sending
 * that referral to the client.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the use of the ManageDsaIT control to
 * delete an entry that may or may not be a referral:
 * <PRE>
 *   DeleteRequest deleteRequest =
 *     new DeleteRequest("uid=john.doe,ou=People,dc=example,dc=com");
 *   deleteRequest.addControl(new ManageDsaITRequestControl());
 *   LDAPResult deleteResult = connection.delete(deleteRequest);
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ManageDsaITRequestControl
       extends Control
{
  /**
   * The OID (2.16.840.1.113730.3.4.2) for the ManageDsaIT request control.
   */
  public static final String MANAGE_DSA_IT_REQUEST_OID =
       "2.16.840.1.113730.3.4.2";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -4540943247829123783L;



  /**
   * Creates a new ManageDsaIT request control.  The control will not be marked
   * critical.
   */
  public ManageDsaITRequestControl()
  {
    super(MANAGE_DSA_IT_REQUEST_OID, false, null);
  }



  /**
   * Creates a new ManageDsaIT request control.
   *
   * @param  isCritical  Indicates whether the control should be marked
   *                     critical.
   */
  public ManageDsaITRequestControl(final boolean isCritical)
  {
    super(MANAGE_DSA_IT_REQUEST_OID, isCritical, null);
  }



  /**
   * Creates a new ManageDsaIT request control which is decoded from the
   * provided generic control.
   *
   * @param  control  The generic control to be decoded as a ManageDsaIT request
   *                  control.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         ManageDsaIT request control.
   */
  public ManageDsaITRequestControl(final Control control)
         throws LDAPException
  {
    super(control);

    if (control.hasValue())
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_MANAGE_DSA_IT_HAS_VALUE.get());
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_MANAGE_DSAIT_REQUEST.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("ManageDsaITRequestControl(isCritical=");
    buffer.append(isCritical());
    buffer.append(')');
  }
}
