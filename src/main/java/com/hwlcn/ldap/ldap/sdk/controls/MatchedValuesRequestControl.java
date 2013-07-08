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
package com.hwlcn.ldap.ldap.sdk.controls;



import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1Sequence;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.controls.ControlMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class provides an implementation of the matched values request control
 * as defined in <A HREF="http://www.ietf.org/rfc/rfc3876.txt">RFC 3876</A>.  It
 * should only be used with a search request, in which case it indicates that
 * only attribute values matching at least one of the provided
 * {@link com.hwlcn.ldap.ldap.sdk.controls.MatchedValuesFilter}s should be included in matching entries.  That
 * is, this control may be used to restrict the set of values included in the
 * entries that are returned.  This is particularly useful for multivalued
 * attributes with a large number of values when only a small number of values
 * are of interest to the client.
 * <BR><BR>
 * There are no corresponding response controls included in the search result
 * entry, search result reference, or search result done messages returned for
 * the associated search request.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the use of the matched values request
 * control.  It will cause only values of the "{@code myIntValues}" attribute
 * to be returned in which those values are greater than or equal to five:
 * <PRE>
 *   SearchRequest searchRequest =
 *        new SearchRequest("uid=john.doe,ou=People,dc=example,dc=com",
 *                          SearchScope.BASE, "(objectClass=*)", "myIntValues");
 *   searchRequest.addControl(new MatchedValuesRequestControl(
 *        MatchedValuesFilter.createGreaterOrEqualFilter("myIntValues", "5"));
 *   SearchResult result = connection.search(searchRequest);
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MatchedValuesRequestControl
       extends Control
{
  /**
   * The OID (1.2.826.0.1.3344810.2.3) for the matched values request control.
   */
  public static final String MATCHED_VALUES_REQUEST_OID =
       "1.2.826.0.1.3344810.2.3";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 6799850686547208774L;



  // The set of matched values filters for this control.
  private final MatchedValuesFilter[] filters;



  /**
   * Creates a new matched values request control with the provided set of
   * filters.  It will not be be marked as critical.
   *
   * @param  filters  The set of filters to use for this control.  At least one
   *                  filter must be provided.
   */
  public MatchedValuesRequestControl(final MatchedValuesFilter... filters)
  {
    this(false, filters);
  }



  /**
   * Creates a new matched values request control with the provided criticality
   * and set of filters.
   *
   * @param  isCritical  Indicates whether this control should be marked
   *                     critical.
   * @param  filters     The set of filters to use for this control.  At least
   *                     one filter must be provided.
   */
  public MatchedValuesRequestControl(final boolean isCritical,
                                     final MatchedValuesFilter... filters)
  {
    super(MATCHED_VALUES_REQUEST_OID, isCritical,  encodeValue(filters));

    this.filters = filters;
  }



  /**
   * Creates a new matched values request control which is decoded from the
   * provided generic control.
   *
   * @param  control  The generic control to be decoded as a matched values
   *                  request control.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         matched values request control.
   */
  public MatchedValuesRequestControl(final Control control)
         throws LDAPException
  {
    super(control);

    final ASN1OctetString value = control.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_MV_REQUEST_NO_VALUE.get());
    }

    try
    {
      final ASN1Element valueElement = ASN1Element.decode(value.getValue());
      final ASN1Element[] filterElements =
           ASN1Sequence.decodeAsSequence(valueElement).elements();
      filters = new MatchedValuesFilter[filterElements.length];
      for (int i=0; i < filterElements.length; i++)
      {
        filters[i] = MatchedValuesFilter.decode(filterElements[i]);
      }
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_MV_REQUEST_CANNOT_DECODE.get(e), e);
    }
  }



  /**
   * Encodes the provided set of filters into a value appropriate for use with
   * the matched values control.
   *
   * @param  filters  The set of filters to include in the value.  It must not
   *                  be {@code null} or empty.
   *
   * @return  The ASN.1 octet string containing the encoded control value.
   */
  private static ASN1OctetString encodeValue(
                                      final MatchedValuesFilter[] filters)
  {
    ensureNotNull(filters);
    ensureTrue(filters.length > 0,
               "MatchedValuesRequestControl.filters must not be empty.");

    final ASN1Element[] elements = new ASN1Element[filters.length];
    for (int i=0; i < filters.length; i++)
    {
      elements[i] = filters[i].encode();
    }

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * Retrieves the set of filters for this matched values request control.
   *
   * @return  The set of filters for this matched values request control.
   */
  public MatchedValuesFilter[] getFilters()
  {
    return filters;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_MATCHED_VALUES_REQUEST.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("MatchedValuesRequestControl(filters={");

    for (int i=0; i < filters.length; i++)
    {
      if (i > 0)
      {
        buffer.append(", ");
      }

      buffer.append('\'');
      filters[i].toString(buffer);
      buffer.append('\'');
    }

    buffer.append("}, isCritical=");
    buffer.append(isCritical());
    buffer.append(')');
  }
}
