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
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.controls.ControlMessages.*;



/**
 * This class provides an implementation of the subtree delete request control
 * as defined in draft-armijo-ldap-treedelete.  This can be used to delete an
 * entry and all subordinate entries in a single operation.
 * <BR><BR>
 * Normally, if an entry has one or more subordinates, a directory server will
 * refuse to delete it by rejecting the request with a
 * {@link ResultCode#NOT_ALLOWED_ON_NONLEAF} result.  In such cases, it is
 * necessary to first recursively remove all of its subordinates before the
 * target entry can be deleted.  However, this subtree delete request control
 * can be used to request that the server remove the entry and all subordinates
 * as a single operation.  For servers that support this control, it is
 * generally much more efficient and convenient than removing all of the
 * subordinate entries one at a time.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the use of the subtree delete control:
 * <PRE>
 *   DeleteRequest deleteRequest =
 *        new DeleteRequest("cn=small subtree,dc=example,dc=com");
 *   deleteRequest.addControl(new SubtreeDeleteRequestControl());
 *   LDAPResult deleteResult = connection.delete(deleteRequest);
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SubtreeDeleteRequestControl
       extends Control
{
  /**
   * The OID (1.2.840.113556.1.4.805) for the subtree delete request control.
   */
  public static final String SUBTREE_DELETE_REQUEST_OID =
       "1.2.840.113556.1.4.805";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 3748121547717081961L;



  /**
   * Creates a new subtree delete request control.  The control will not be
   * marked critical.
   */
  public SubtreeDeleteRequestControl()
  {
    super(SUBTREE_DELETE_REQUEST_OID, false, null);
  }



  /**
   * Creates a new subtree delete request control.
   *
   * @param  isCritical  Indicates whether the control should be marked
   *                     critical.
   */
  public SubtreeDeleteRequestControl(final boolean isCritical)
  {
    super(SUBTREE_DELETE_REQUEST_OID, isCritical, null);
  }



  /**
   * Creates a new subtree delete request control which is decoded from the
   * provided generic control.
   *
   * @param  control  The generic control to be decoded as a subtree delete
   *                  request control.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         subtree delete request control.
   */
  public SubtreeDeleteRequestControl(final Control control)
         throws LDAPException
  {
    super(control);

    if (control.hasValue())
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_SUBTREE_DELETE_HAS_VALUE.get());
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_SUBTREE_DELETE_REQUEST.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("SubtreeDeleteRequestControl(isCritical=");
    buffer.append(isCritical());
    buffer.append(')');
  }
}
