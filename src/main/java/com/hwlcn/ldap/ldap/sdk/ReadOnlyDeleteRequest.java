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



import com.hwlcn.ldap.ldif.LDIFDeleteChangeRecord;
import com.hwlcn.ldap.util.NotExtensible;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This interface defines a set of methods that may be safely called in an LDAP
 * delete request without altering its contents.  This interface must not be
 * implemented by any class other than {@link com.hwlcn.ldap.ldap.sdk.DeleteRequest}.
 * <BR><BR>
 * This interface does not inherently provide the assurance of thread safety for
 * the methods that it exposes, because it is still possible for a thread
 * referencing the object which implements this interface to alter the request
 * using methods not included in this interface.  However, if it can be
 * guaranteed that no thread will alter the underlying object, then the methods
 * exposed by this interface can be safely invoked concurrently by any number of
 * threads.
 */
@NotExtensible()
@ThreadSafety(level=ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface ReadOnlyDeleteRequest
       extends ReadOnlyLDAPRequest
{
  /**
   * Retrieves the DN of the entry to delete.
   *
   * @return  The DN of the entry to delete.
   */
  String getDN();



  /**
   * {@inheritDoc}
   */
  DeleteRequest duplicate();



  /**
   * {@inheritDoc}
   */
  DeleteRequest duplicate(final Control[] controls);



  /**
   * Retrieves an LDIF delete change record with the contents of this delete
   * request.
   *
   * @return  An LDIF delete change record with the contents of this delete
   *          request.
   */
  LDIFDeleteChangeRecord toLDIFChangeRecord();



  /**
   * Retrieves a string array whose lines contain an LDIF representation of the
   * corresponding delete change record.
   *
   * @return  A string array whose lines contain an LDIF representation of the
   *          corresponding delete change record.
   */
  String[] toLDIF();



  /**
   * Retrieves an LDIF string representation of this delete request.
   *
   * @return  An LDIF string representation of this delete request.
   */
  String toLDIFString();
}
