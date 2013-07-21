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
package com.hwlcn.ldap.asn1;



import com.hwlcn.ldap.util.LDAPSDKException;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class defines an exception that can be thrown if a problem occurs while
 * interacting with ASN.1 BER elements.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Exception
       extends LDAPSDKException
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 3234714599495723483L;



  /**
   * Creates a new ASN.1 exception with the provided message.
   *
   * @param  message  A message explaining the problem that occurred.
   */
  public ASN1Exception(final String message)
  {
    super(message);
  }



  /**
   * Creates a new ASN.1 exception with the provided message and cause.
   *
   * @param  message  A message explaining the problem that occurred.
   * @param  cause    The underlying cause for this exception.
   */
  public ASN1Exception(final String message, final Throwable cause)
  {
    super(message, cause);
  }
}
