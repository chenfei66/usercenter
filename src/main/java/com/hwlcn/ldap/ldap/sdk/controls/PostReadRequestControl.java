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



import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1Sequence;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.ldap.util.StaticUtils;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.controls.ControlMessages.*;
import static com.hwlcn.ldap.util.Debug.*;



/**
 * This class provides an implementation of the LDAP post-read request control
 * as defined in <A HREF="http://www.ietf.org/rfc/rfc4527.txt">RFC 4527</A>.  It
 * may be used to request that the server retrieve a copy of the target entry as
 * it appeared immediately after processing an add, modify, or modify DN
 * operation.
 * <BR><BR>
 * If this control is included in an add, modify, or modify DN request, then the
 * corresponding response may include a {@link PostReadResponseControl}
 * containing a version of the entry as it appeared after applying that change.
 * Note that this response control will only be included if the operation was
 * successful, so it will not be provided if the operation failed for some
 * reason (e.g., if the change would have violated the server schema, or if the
 * requester did not have sufficient permission to perform that operation).
 * <BR><BR>
 * The value of this control should contain a set of requested attributes to
 * include in the entry that is returned.  The server should treat this set of
 * requested attributes exactly as it treats the requested attributes from a
 * {@link com.hwlcn.ldap.ldap.sdk.SearchRequest}.  As is the case with a search
 * request, if no attributes are specified, then all user attributes will be
 * included.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the use of the post-read controls.  It
 * will modify an entry to increment the value of the {@code testCounter}
 * attribute by one, and will use the post-read controls to determine what the
 * new value is:
 * <PRE>
 *   Modification mod =
 *        new Modification(ModificationType.INCREMENT, "testCounter", "1");
 *   ModifyRequest modifyRequest =
 *        new ModifyRequest("uid=john.doe,ou=People,dc=example,dc=com", mod);
 *   modifyRequest.addControl(new PostReadRequestControl("testCounter"));
 *   LDAPResult modifyResult = connection.modify(modifyRequest);
 *
 *   Integer newValue = null;
 *   PostReadResponseControl c = PostReadResponseControl.get(modifyResult);
 *   if (c != null)
 *   {
 *     newValue = c.getEntry().getAttributeValueAsInteger("testCounter");
 *   }
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PostReadRequestControl
       extends Control
{
  /**
   * The OID (1.3.6.1.1.13.2) for the post-read request control.
   */
  public static final String POST_READ_REQUEST_OID = "1.3.6.1.1.13.2";



  /**
   * The set of requested attributes that will be used if none are provided.
   */
  private static final String[] NO_ATTRIBUTES = StaticUtils.NO_STRINGS;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -4210061989410209462L;



  // The set of requested attributes to retrieve from the target entry.
  private final String[] attributes;



  /**
   * Creates a new post-read request control that will retrieve the specified
   * set of attributes from the target entry.  It will be marked critical.
   *
   * @param  attributes  The set of attributes to retrieve from the target
   *                     entry.  It behaves in the same way as the set of
   *                     requested attributes for a search operation.  If this
   *                     is empty or {@code null}, then all user attributes
   *                     will be returned.
   */
  public PostReadRequestControl(final String... attributes)
  {
    this(true, attributes);
  }



  /**
   * Creates a new post-read request control that will retrieve the specified
   * set of attributes from the target entry.
   *
   * @param  isCritical  Indicates whether this control should be marked
   *                     critical.
   * @param  attributes  The set of attributes to retrieve from the target
   *                     entry.  It behaves in the same way as the set of
   *                     requested attributes for a search operation.  If this
   *                     is empty or {@code null}, then all user attributes
   *                     will be returned.
   */
  public PostReadRequestControl(final boolean isCritical,
                                final String... attributes)
  {
    super(POST_READ_REQUEST_OID, isCritical, encodeValue(attributes));

    if (attributes == null)
    {
      this.attributes = NO_ATTRIBUTES;
    }
    else
    {
      this.attributes = attributes;
    }
  }



  /**
   * Creates a new post-read request control which is decoded from the provided
   * generic control.
   *
   * @param  control  The generic control to be decoded as a post-read request
   *                  control.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         post-read request control.
   */
  public PostReadRequestControl(final Control control)
         throws LDAPException
  {
    super(control);

    final ASN1OctetString value = control.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_POST_READ_REQUEST_NO_VALUE.get());
    }

    try
    {
      final ASN1Element valueElement = ASN1Element.decode(value.getValue());
      final ASN1Element[] attrElements =
           ASN1Sequence.decodeAsSequence(valueElement).elements();
      attributes = new String[attrElements.length];
      for (int i=0; i < attrElements.length; i++)
      {
        attributes[i] =
             ASN1OctetString.decodeAsOctetString(attrElements[i]).stringValue();
      }
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_POST_READ_REQUEST_CANNOT_DECODE.get(e), e);
    }
  }



  /**
   * Encodes the provided information into an octet string that can be used as
   * the value for this control.
   *
   * @param  attributes  The set of attributes to retrieve from the target
   *                     entry.  It behaves in the same way as the set of
   *                     requested attributes for a search operation.  If this
   *                     is empty or {@code null}, then all user attributes
   *                     will be returned.
   *
   * @return  An ASN.1 octet string that can be used as the value for this
   *          control.
   */
  private static ASN1OctetString encodeValue(final String[] attributes)
  {
    if ((attributes == null) || (attributes.length == 0))
    {
      return new ASN1OctetString(new ASN1Sequence().encode());
    }

    final ASN1OctetString[] elements = new ASN1OctetString[attributes.length];
    for (int i=0; i < attributes.length; i++)
    {
      elements[i] = new ASN1OctetString(attributes[i]);
    }

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * Retrieves the set of attributes that will be requested for inclusion in the
   * entry returned in the response control.
   *
   * @return  The set of attributes that will be requested for inclusion in the
   *          entry returned in the response control, or an empty array if all
   *          user attributes should be returned.
   */
  public String[] getAttributes()
  {
    return attributes;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_POST_READ_REQUEST.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("PostReadRequestControl(attributes={");
    for (int i=0; i < attributes.length; i++)
    {
      if (i > 0)
      {
        buffer.append(", ");
      }
      buffer.append('\'');
      buffer.append(attributes[i]);
      buffer.append('\'');
    }
    buffer.append("}, isCritical=");
    buffer.append(isCritical());
    buffer.append(')');
  }
}
