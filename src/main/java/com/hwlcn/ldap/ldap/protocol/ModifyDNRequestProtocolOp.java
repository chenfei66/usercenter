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



import com.hwlcn.ldap.asn1.ASN1Boolean;
import com.hwlcn.ldap.asn1.ASN1Buffer;
import com.hwlcn.ldap.asn1.ASN1BufferSequence;
import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1Sequence;
import com.hwlcn.ldap.asn1.ASN1StreamReader;
import com.hwlcn.ldap.asn1.ASN1StreamReaderSequence;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ModifyDNRequest;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.util.NotMutable;
import com.hwlcn.ldap.util.InternalUseOnly;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.protocol.ProtocolMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides an implementation of an LDAP modify DN request protocol
 * op.
 */
@InternalUseOnly()
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyDNRequestProtocolOp
       implements ProtocolOp
{
  /**
   * The BER type for the newSuperior element.
   */
  public static final byte TYPE_NEW_SUPERIOR = (byte) 0x80;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 7514385089303489375L;



  // The deleteOldRDN flag for this modify DN request.
  private final boolean deleteOldRDN;

  // The entry DN for this modify DN request.
  private final String dn;

  // The new RDN for this modify DN request.
  private final String newRDN;

  // The new superior DN for this modify DN request.
  private final String newSuperiorDN;



  /**
   * Creates a new modify DN request protocol op with the provided information.
   *
   * @param  dn             The entry DN for this modify DN request.
   * @param  newRDN         The new RDN for this modify DN request.
   * @param  deleteOldRDN   Indicates whether to delete the old RDN values.
   * @param  newSuperiorDN  The new superior DN for this modify DN request, or
   *                        {@code null} if there is none.
   */
  public ModifyDNRequestProtocolOp(final String dn, final String newRDN,
                                   final boolean deleteOldRDN,
                                   final String newSuperiorDN)
  {
    this.dn            = dn;
    this.newRDN        = newRDN;
    this.deleteOldRDN  = deleteOldRDN;
    this.newSuperiorDN = newSuperiorDN;
  }



  /**
   * Creates a new modify DN request protocol op from the provided modify DN
   * request object.
   *
   * @param  request  The modify DN request object to use to create this
   *                  protocol op.
   */
  public ModifyDNRequestProtocolOp(final ModifyDNRequest request)
  {
    dn            = request.getDN();
    newRDN        = request.getNewRDN();
    deleteOldRDN  = request.deleteOldRDN();
    newSuperiorDN = request.getNewSuperiorDN();
  }



  /**
   * Creates a new modify DN request protocol op read from the provided ASN.1
   * stream reader.
   *
   * @param  reader  The ASN.1 stream reader from which to read the modify DN
   *                 request protocol op.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or parsing the
   *                         modify DN request.
   */
  ModifyDNRequestProtocolOp(final ASN1StreamReader reader)
       throws LDAPException
  {
    try
    {
      final ASN1StreamReaderSequence opSequence = reader.beginSequence();

      dn           = reader.readString();
      newRDN       = reader.readString();
      deleteOldRDN = reader.readBoolean();

      if (opSequence.hasMoreElements())
      {
        newSuperiorDN = reader.readString();
      }
      else
      {
        newSuperiorDN = null;
      }
    }
    catch (Exception e)
    {
      debugException(e);

      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_MODIFY_DN_REQUEST_CANNOT_DECODE.get(getExceptionMessage(e)), e);
    }
  }



  /**
   * Retrieves the target entry DN for this modify DN request.
   *
   * @return  The target entry DN for this modify DN request.
   */
  public String getDN()
  {
    return dn;
  }



  /**
   * Retrieves the new RDN for this modify DN request.
   *
   * @return  The new RDN for this modify DN request.
   */
  public String getNewRDN()
  {
    return newRDN;
  }



  /**
   * Indicates whether to delete the old RDN values from the target entry.
   *
   * @return  {@code true} if the old RDN values should be removed from the
   *          entry, or {@code false} if not.
   */
  public boolean deleteOldRDN()
  {
    return deleteOldRDN;
  }



  /**
   * Retrieves the new superior DN for this modify DN request, if any.
   *
   * @return  The new superior DN for this modify DN request, or {@code null} if
   *          there is none.
   */
  public String getNewSuperiorDN()
  {
    return newSuperiorDN;
  }



  /**
   * {@inheritDoc}
   */
  public byte getProtocolOpType()
  {
    return LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST;
  }



  /**
   * {@inheritDoc}
   */
  public ASN1Element encodeProtocolOp()
  {
    if (newSuperiorDN == null)
    {
      return new ASN1Sequence(LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST,
           new ASN1OctetString(dn),
           new ASN1OctetString(newRDN),
           new ASN1Boolean(deleteOldRDN));
    }
    else
    {
      return new ASN1Sequence(LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST,
           new ASN1OctetString(dn),
           new ASN1OctetString(newRDN),
           new ASN1Boolean(deleteOldRDN),
           new ASN1OctetString(TYPE_NEW_SUPERIOR, newSuperiorDN));
    }
  }



  /**
   * Decodes the provided ASN.1 element as a modify DN request protocol op.
   *
   * @param  element  The ASN.1 element to be decoded.
   *
   * @return  The decoded modify DN request protocol op.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided ASN.1 element cannot be decoded as
   *                         a modify DN request protocol op.
   */
  public static ModifyDNRequestProtocolOp decodeProtocolOp(
                                               final ASN1Element element)
         throws LDAPException
  {
    try
    {
      final ASN1Element[] elements =
           ASN1Sequence.decodeAsSequence(element).elements();
      final String dn =
           ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
      final String newRDN =
           ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
      final boolean deleteOldRDN =
           ASN1Boolean.decodeAsBoolean(elements[2]).booleanValue();

      final String newSuperiorDN;
      if (elements.length > 3)
      {
        newSuperiorDN =
             ASN1OctetString.decodeAsOctetString(elements[3]).stringValue();
      }
      else
      {
        newSuperiorDN = null;
      }

      return new ModifyDNRequestProtocolOp(dn, newRDN, deleteOldRDN,
           newSuperiorDN);
    }
    catch (final Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_MODIFY_DN_REQUEST_CANNOT_DECODE.get(getExceptionMessage(e)),
           e);
    }
  }



  /**
   * {@inheritDoc}
   */
  public void writeTo(final ASN1Buffer buffer)
  {
    final ASN1BufferSequence opSequence =
         buffer.beginSequence(LDAPMessage.PROTOCOL_OP_TYPE_MODIFY_DN_REQUEST);
    buffer.addOctetString(dn);
    buffer.addOctetString(newRDN);
    buffer.addBoolean(deleteOldRDN);

    if (newSuperiorDN != null)
    {
      buffer.addOctetString(TYPE_NEW_SUPERIOR, newSuperiorDN);
    }
    opSequence.end();
  }



  /**
   * Creates a modify DN request from this protocol op.
   *
   * @param  controls  The set of controls to include in the modify DN request.
   *                   It may be empty or {@code null} if no controls should be
   *                   included.
   *
   * @return  The modify DN request that was created.
   */
  public ModifyDNRequest toModifyDNRequest(final Control... controls)
  {
    return new ModifyDNRequest(dn, newRDN, deleteOldRDN, newSuperiorDN,
         controls);
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
    buffer.append("ModifyDNRequestProtocolOp(dn='");
    buffer.append(dn);
    buffer.append("', newRDN='");
    buffer.append(newRDN);
    buffer.append("', deleteOldRDN=");
    buffer.append(deleteOldRDN);

    if (newSuperiorDN != null)
    {
      buffer.append(", newSuperiorDN='");
      buffer.append(newSuperiorDN);
      buffer.append('\'');
    }

    buffer.append(')');
  }
}
