/*
 * Copyright 2008-2013 UnboundID Corp.
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
package com.hwlcn.ldap.ldap.matchingrules;



import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.util.Extensible;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.util.Debug.*;



/**
 * This class provides a common matching rule framework that may be extended by
 * matching rule implementations in which equality, ordering, and substring
 * matching can all be made based on byte-for-byte comparisons of the normalized
 * value, and any value is acceptable.
 */
@Extensible()
@ThreadSafety(level=ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class AcceptAllSimpleMatchingRule
       extends SimpleMatchingRule
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -7450007924568660003L;



  /**
   * {@inheritDoc}
   */
  @Override()
  public boolean valuesMatch(final ASN1OctetString value1,
                             final ASN1OctetString value2)
  {
    return normalize(value1).equals(normalize(value2));
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public boolean matchesSubstring(final ASN1OctetString value,
                                  final ASN1OctetString subInitial,
                                  final ASN1OctetString[] subAny,
                                  final ASN1OctetString subFinal)
  {
    try
    {
      return super.matchesSubstring(value, subInitial, subAny, subFinal);
    }
    catch (LDAPException le)
    {
      debugException(le);

      // This should never happen, as the only reason the superclass version of
      // this method will throw an exception is if an exception is thrown by
      // normalize or normalizeSubstring.
      return false;
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public int compareValues(final ASN1OctetString value1,
                           final ASN1OctetString value2)
  {
    try
    {
      return super.compareValues(value1, value2);
    }
    catch (LDAPException le)
    {
      debugException(le);

      // This should never happen, as the only reason the superclass version of
      // this method will throw an exception is if an exception is thrown by
      // normalize or normalizeSubstring.
      return 0;
    }
  }



  /**
   * {@inheritDoc}  This variant of the {@code normalize} method is not allowed
   * to throw exceptions.
   */
  @Override()
  public abstract ASN1OctetString normalize(final ASN1OctetString value);



  /**
   * {@inheritDoc}  This variant of the {@code normalizeSubstring} method is not
   * allowed to throw exceptions.
   */
  @Override()
  public abstract ASN1OctetString normalizeSubstring(
                                       final ASN1OctetString value,
                                       final byte substringType);
}
