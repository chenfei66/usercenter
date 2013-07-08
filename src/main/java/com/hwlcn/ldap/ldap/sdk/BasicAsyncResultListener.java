/*
 * Copyright 2011-2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2011-2013 UnboundID Corp.
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



import java.io.Serializable;

import com.hwlcn.ldap.util.InternalUseOnly;
import com.hwlcn.ldap.util.Mutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class provides a basic implementation of the {@link com.hwlcn.ldap.ldap.sdk.AsyncResultListener}
 * interface that will merely set the result object to a local variable that can
 * be accessed through a getter method.  It provides a listener that may be
 * easily used when processing an asynchronous operation using the
 * {@link com.hwlcn.ldap.ldap.sdk.AsyncRequestID} as a {@code java.util.concurrent.Future} object.
 */
@Mutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class BasicAsyncResultListener
       implements AsyncResultListener, Serializable
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -2701328904233458257L;



  // The result that has been received for the associated operation.
  private volatile LDAPResult ldapResult;



  /**
   * Creates a new instance of this class for use in processing a single add,
   * delete, modify, or modify DN operation.  A single basic async result
   * listener object may not be used for multiple operations.
   */
  public BasicAsyncResultListener()
  {
    ldapResult = null;
  }



  /**
   * {@inheritDoc}
   */
  @InternalUseOnly()
  public void ldapResultReceived(final AsyncRequestID requestID,
                                 final LDAPResult ldapResult)
  {
    this.ldapResult = ldapResult;
  }



  /**
   * Retrieves the result that has been received for the associated asynchronous
   * operation, if it has been received.
   *
   * @return  The result that has been received for the associated asynchronous
   *          operation, or {@code null} if no response has been received yet.
   */
  public LDAPResult getLDAPResult()
  {
    return ldapResult;
  }
}
