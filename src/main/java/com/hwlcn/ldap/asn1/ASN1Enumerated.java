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



import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.asn1.ASN1Constants.*;
import static com.hwlcn.ldap.asn1.ASN1Messages.*;
import static com.hwlcn.ldap.util.Debug.*;



/**
 * This class provides an ASN.1 enumerated element.  Enumerated elements are
 * very similar to integer elements, and the only real difference between them
 * is that the individual values of an enumerated element have a symbolic
 * significance (i.e., each value is associated with a particular meaning),
 * although this does not impact its encoding other than through the use of a
 * different default BER type.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Enumerated
       extends ASN1Element
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -5915912036130847725L;



  // The int value for this element.
  private final int intValue;



  /**
   * Creates a new ASN.1 enumerated element with the default BER type and the
   * provided int value.
   *
   * @param  intValue  The int value to use for this element.
   */
  public ASN1Enumerated(final int intValue)
  {
    super(UNIVERSAL_ENUMERATED_TYPE, ASN1Integer.encodeIntValue(intValue));

    this.intValue = intValue;
  }



  /**
   * Creates a new ASN.1 enumerated element with the specified BER type and the
   * provided int value.
   *
   * @param  type      The BER type to use for this element.
   * @param  intValue  The int value to use for this element.
   */
  public ASN1Enumerated(final byte type, final int intValue)
  {
    super(type, ASN1Integer.encodeIntValue(intValue));

    this.intValue = intValue;
  }



  /**
   * Creates a new ASN.1 enumerated element with the specified BER type and the
   * provided int and pre-encoded values.
   *
   * @param  type      The BER type to use for this element.
   * @param  intValue  The int value to use for this element.
   * @param  value     The pre-encoded value to use for this element.
   */
  private ASN1Enumerated(final byte type, final int intValue,
                         final byte[] value)
  {
    super(type, value);

    this.intValue = intValue;
  }



  /**
   * Retrieves the int value for this element.
   *
   * @return  The int value for this element.
   */
  public int intValue()
  {
    return intValue;
  }



  /**
   * Decodes the contents of the provided byte array as an enumerated element.
   *
   * @param  elementBytes  The byte array to decode as an ASN.1 enumerated
   *                       element.
   *
   * @return  The decoded ASN.1 enumerated element.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the provided array cannot be decoded as an
   *                         enumerated element.
   */
  public static ASN1Enumerated decodeAsEnumerated(final byte[] elementBytes)
         throws ASN1Exception
  {
    try
    {
      int valueStartPos = 2;
      int length = (elementBytes[1] & 0x7F);
      if (length != elementBytes[1])
      {
        final int numLengthBytes = length;

        length = 0;
        for (int i=0; i < numLengthBytes; i++)
        {
          length <<= 8;
          length |= (elementBytes[valueStartPos++] & 0xFF);
        }
      }

      if ((elementBytes.length - valueStartPos) != length)
      {
        throw new ASN1Exception(ERR_ELEMENT_LENGTH_MISMATCH.get(length,
                                     (elementBytes.length - valueStartPos)));
      }

      final byte[] value = new byte[length];
      System.arraycopy(elementBytes, valueStartPos, value, 0, length);

      int intValue;
      switch (value.length)
      {
        case 1:
          intValue = (value[0] & 0xFF);
          if ((value[0] & 0x80) != 0x00)
          {
            intValue |= 0xFFFFFF00;
          }
          break;

        case 2:
          intValue = ((value[0] & 0xFF) << 8) | (value[1] & 0xFF);
          if ((value[0] & 0x80) != 0x00)
          {
            intValue |= 0xFFFF0000;
          }
          break;

        case 3:
          intValue = ((value[0] & 0xFF) << 16) | ((value[1] & 0xFF) << 8) |
                     (value[2] & 0xFF);
          if ((value[0] & 0x80) != 0x00)
          {
            intValue |= 0xFF000000;
          }
          break;

        case 4:
          intValue = ((value[0] & 0xFF) << 24) | ((value[1] & 0xFF) << 16) |
                     ((value[2] & 0xFF) << 8) | (value[3] & 0xFF);
          break;

        default:
          throw new ASN1Exception(ERR_ENUMERATED_INVALID_LENGTH.get(
                                       value.length));
      }

      return new ASN1Enumerated(elementBytes[0], intValue, value);
    }
    catch (final ASN1Exception ae)
    {
      debugException(ae);
      throw ae;
    }
    catch (final Exception e)
    {
      debugException(e);
      throw new ASN1Exception(ERR_ELEMENT_DECODE_EXCEPTION.get(e), e);
    }
  }



  /**
   * Decodes the provided ASN.1 element as an enumerated element.
   *
   * @param  element  The ASN.1 element to be decoded.
   *
   * @return  The decoded ASN.1 enumerated element.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the provided element cannot be decoded as an
   *                         enumerated element.
   */
  public static ASN1Enumerated decodeAsEnumerated(final ASN1Element element)
         throws ASN1Exception
  {
    int intValue;
    final byte[] value = element.getValue();
    switch (value.length)
    {
      case 1:
        intValue = (value[0] & 0xFF);
        if ((value[0] & 0x80) != 0x00)
        {
          intValue |= 0xFFFFFF00;
        }
        break;

      case 2:
        intValue = ((value[0] & 0xFF) << 8) | (value[1] & 0xFF);
        if ((value[0] & 0x80) != 0x00)
        {
          intValue |= 0xFFFF0000;
        }
        break;

      case 3:
        intValue = ((value[0] & 0xFF) << 16) | ((value[1] & 0xFF) << 8) |
                   (value[2] & 0xFF);
        if ((value[0] & 0x80) != 0x00)
        {
          intValue |= 0xFF000000;
        }
        break;

      case 4:
        intValue = ((value[0] & 0xFF) << 24) | ((value[1] & 0xFF) << 16) |
                   ((value[2] & 0xFF) << 8) | (value[3] & 0xFF);
        break;

      default:
        throw new ASN1Exception(ERR_ENUMERATED_INVALID_LENGTH.get(
                                     value.length));
    }

    return new ASN1Enumerated(element.getType(), intValue, value);
  }



  /**
   * Appends a string representation of this ASN.1 element to the provided
   * buffer.
   *
   * @param  buffer  The buffer to which to append the information.
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append(intValue);
  }
}
