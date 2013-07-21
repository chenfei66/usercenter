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
package com.hwlcn.ldap.ldap.protocol;



import com.hwlcn.ldap.asn1.ASN1Buffer;
import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1StreamReader;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.DeleteRequest;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.InternalUseOnly;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.protocol.ProtocolMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class provides an implementation of an LDAP delete request protocol op.
 */
@InternalUseOnly()
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeleteRequestProtocolOp
       implements ProtocolOp
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 1577020640104649789L;



  // The entry DN for this delete request.
  private final String dn;



  /**
   * Creates a new delete request protocol op with the provided information.
   *
   * @param  dn  The entry DN for this delete request.
   */
  public DeleteRequestProtocolOp(final String dn)
  {
    this.dn = dn;
  }



  /**
   * Creates a new delete request protocol op from the provided delete request
   * object.
   *
   * @param  request  The delete request object to use to create this protocol
   *                  op.
   */
  public DeleteRequestProtocolOp(final DeleteRequest request)
  {
    dn = request.getDN();
  }



  /**
   * Creates a new delete request protocol op read from the provided ASN.1
   * stream reader.
   *
   * @param  reader  The ASN.1 stream reader from which to read the delete
   *                 request protocol op.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or parsing the
   *                         delete request.
   */
  DeleteRequestProtocolOp(final ASN1StreamReader reader)
       throws LDAPException
  {
    try
    {
      dn = reader.readString();
      ensureNotNull(dn);
    }
    catch (Exception e)
    {
      debugException(e);

      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_DELETE_REQUEST_CANNOT_DECODE.get(getExceptionMessage(e)), e);
    }
  }



  /**
   * Retrieves the target entry DN for this delete request.
   *
   * @return  The target entry DN for this delete request.
   */
  public String getDN()
  {
    return dn;
  }



  /**
   * {@inheritDoc}
   */
  public byte getProtocolOpType()
  {
    return LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST;
  }



  /**
   * {@inheritDoc}
   */
  public ASN1Element encodeProtocolOp()
  {
    return new ASN1OctetString(LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST, dn);
  }



  /**
   * Decodes the provided ASN.1 element as a delete request protocol op.
   *
   * @param  element  The ASN.1 element to be decoded.
   *
   * @return  The decoded delete request protocol op.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided ASN.1 element cannot be decoded as
   *                         a delete request protocol op.
   */
  public static DeleteRequestProtocolOp decodeProtocolOp(
                                             final ASN1Element element)
         throws LDAPException
  {
    try
    {
      return new DeleteRequestProtocolOp(
           ASN1OctetString.decodeAsOctetString(element).stringValue());
    }
    catch (final Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_DELETE_REQUEST_CANNOT_DECODE.get(getExceptionMessage(e)),
           e);
    }
  }



  /**
   * {@inheritDoc}
   */
  public void writeTo(final ASN1Buffer buffer)
  {
    buffer.addOctetString(LDAPMessage.PROTOCOL_OP_TYPE_DELETE_REQUEST, dn);
  }



  /**
   * Creates a delete request from this protocol op.
   *
   * @param  controls  The set of controls to include in the delete request.
   *                   It may be empty or {@code null} if no controls should be
   *                   included.
   *
   * @return  The delete request that was created.
   */
  public DeleteRequest toDeleteRequest(final Control... controls)
  {
    return new DeleteRequest(dn, controls);
  }



  /**
   * Retrieves a string representation of this protocol op.
   *
   * @return  A string representation of this protocol op.
   */
  @Override()
  public String toString()
  {
    final StringBuilder buffer = new StringBuilder();
    toString(buffer);
    return buffer.toString();
  }



  /**
   * {@inheritDoc}
   */
  public void toString(final StringBuilder buffer)
  {
    buffer.append("DeleteRequestProtocolOp(dn='");
    buffer.append(dn);
    buffer.append("')");
  }
}
