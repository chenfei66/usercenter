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



import com.hwlcn.ldap.ldap.sdk.DN;
import com.hwlcn.ldap.ldap.sdk.RDN;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides a set of utility methods for working with LDAP DNs.
 * <BR><BR>
 * This class is primarily intended to be used in the process of updating
 * applications which use the Netscape Directory SDK for Java to switch to or
 * coexist with the UnboundID LDAP SDK for Java.  For applications not written
 * using the Netscape Directory SDK for Java, the {@link com.hwlcn.ldap.ldap.sdk.DN} class should be
 * used instead.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPDN
{
  /**
   * Prevent this class from being instantiated.
   */
  private LDAPDN()
  {
    // No implementation required.
  }



  /**
   * Retrieves a normalized representation of the provided DN.  If the provided
   * string does not represent a valid distinguished name, then the value
   * returned by this method will not be reliable.
   *
   * @param  dn  The string representation of the DN to be normalized.
   *
   * @return  A normalized representation of the provided DN.
   */
  public static String normalize(final String dn)
  {
    try
    {
      return DN.normalize(dn);
    }
    catch (Exception e)
    {
      debugException(e);
      return toLowerCase(dn.trim());
    }
  }



  /**
   * Explodes the provided DN into individual RDN components.  If the provided
   * string does not represent a valid distinguished name, then the value
   * returned by this method will not be reliable.
   *
   * @param  dn       The DN to be exploded into its RDN components.
   * @param  noTypes  Indicates whether to exclude the attribute names and
   *                  equal signs and only include the values of the RDN
   *                  components.
   *
   * @return  An exploded representation of the provided DN.
   */
  public static String[] explodeDN(final String dn, final boolean noTypes)
  {
    try
    {
      final RDN[] rdns = new DN(dn).getRDNs();
      final String[] rdnStrings = new String[rdns.length];
      for (int i=0; i < rdns.length; i++)
      {
        if (noTypes)
        {
          final StringBuilder buffer = new StringBuilder();
          for (final String s : rdns[i].getAttributeValues())
          {
            if (buffer.length() > 0)
            {
              buffer.append('+');
            }
            buffer.append(s);
          }
          rdnStrings[i] = buffer.toString();
        }
        else
        {
          rdnStrings[i] = rdns[i].toString();
        }
      }
      return rdnStrings;
    }
    catch (Exception e)
    {
      debugException(e);
      return new String[] { dn };
    }
  }



  /**
   * Explodes the provided RDN into individual name-value pairs.  If the
   * provided string does not represent a valid relative distinguished name,
   * then the value returned by this method will not be reliable.
   *
   * @param  rdn      The RDN to be exploded into its name-value pairs.
   * @param  noTypes  Indicates whether to exclude the attribute names and
   *                  equal signs and only include the values of the components.
   *
   * @return  An exploded representation of the provided DN.
   */
  public static String[] explodeRDN(final String rdn, final boolean noTypes)
  {
    try
    {
      final RDN      rdnObject  = new RDN(rdn);

      final String[] values = rdnObject.getAttributeValues();
      if (noTypes)
      {
        return values;
      }

      final String[] names      = rdnObject.getAttributeNames();
      final String[] returnStrs = new String[names.length];

      for (int i=0; i < names.length; i++)
      {
        returnStrs[i] = names[i] + '=' + values[i];
      }

      return returnStrs;
    }
    catch (Exception e)
    {
      debugException(e);
      return new String[] { rdn };
    }
  }



  /**
   * Indicates whether the provided strings represent the same distinguished
   * name.
   *
   * @param  dn1  The first DN to be compared.
   * @param  dn2  The second DN to be compared.
   *
   * @return  {@code true} if the provided strings represent the same
   *          distinguished name, or {@code false} if not or if either of the
   *          values cannot be parsed as a valid DN.
   */
  public static boolean equals(final String dn1, final String dn2)
  {
    try
    {
      return DN.equals(dn1, dn2);
    }
    catch (Exception e)
    {
      debugException(e);
      return false;
    }
  }
}
