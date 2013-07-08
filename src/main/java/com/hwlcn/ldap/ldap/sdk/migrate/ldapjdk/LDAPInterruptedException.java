/*
 * Copyright 2009-2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009-2013 UnboundID Corp.
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
package com.hwlcn.ldap.ldap.sdk.migrate.ldapjdk;



import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.util.NotExtensible;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class provides an exception that may be returned if an operation in
 * progress is interrupted.
 * <BR><BR>
 * This class is primarily intended to be used in the process of updating
 * applications which use the Netscape Directory SDK for Java to switch to or
 * coexist with the UnboundID LDAP SDK for Java.  For applications not written
 * using the Netscape Directory SDK for Java, the
 * {@link com.hwlcn.ldap.ldap.sdk.LDAPException} class should be used instead.
 */
@NotExtensible()
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPInterruptedException
       extends LDAPException
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 7867903105944011998L;



  /**
   * Creates a new LDAP interrupted exception.
   */
  LDAPInterruptedException()
  {
    super(null, ResultCode.USER_CANCELED_INT_VALUE);
  }



  /**
   * Creates a new LDAP interrupted exception from the provided
   * {@link com.hwlcn.ldap.ldap.sdk.LDAPException} object.
   *
   * @param  ldapException  The {@code LDAPException} object to use for this
   *                        LDAP interrupted exception.
   */
  LDAPInterruptedException(
       final com.hwlcn.ldap.ldap.sdk.LDAPException ldapException)
  {
    super(ldapException);
  }
}
