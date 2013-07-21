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



import java.util.ArrayList;

import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1StreamReader;
import com.hwlcn.ldap.asn1.ASN1StreamReaderSequence;
import com.hwlcn.core.annotation.Extensible;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides a data structure for holding information about the result
 * of processing a bind operation.  It provides generic bind response elements
 * as described in the {@link LDAPResult} class, but may be overridden to
 * provide more detailed information for specific types of bind requests.
 */
@Extensible()
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class BindResult
       extends LDAPResult
{
  /**
   * The BER type for the server SASL credentials element in the bind result.
   */
  private static final byte TYPE_SERVER_SASL_CREDENTIALS = (byte) 0x87;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 2211625049303605730L;



  // The server SASL credentials from the response, if available.
  private final ASN1OctetString serverSASLCredentials;



  /**
   * Creates a new bind result with the provided information.
   *
   * @param  messageID          The message ID for the LDAP message that is
   *                            associated with this bind result.
   * @param  resultCode         The result code from the response.
   * @param  diagnosticMessage  The diagnostic message from the response, if
   *                            available.
   * @param  matchedDN          The matched DN from the response, if available.
   * @param  referralURLs       The set of referral URLs from the response, if
   *                            available.
   * @param  responseControls   The set of controls from the response, if
   *                            available.
   */
  public BindResult(final int messageID, final ResultCode resultCode,
                    final String diagnosticMessage, final String matchedDN,
                    final String[] referralURLs,
                    final Control[] responseControls)
  {
    this(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs,
         responseControls, null);
  }



  /**
   * Creates a new bind result with the provided information.
   *
   * @param  messageID              The message ID for the LDAP message that is
   *                                associated with this bind result.
   * @param  resultCode             The result code from the response.
   * @param  diagnosticMessage      The diagnostic message from the response, if
   *                                available.
   * @param  matchedDN              The matched DN from the response, if
   *                                available.
   * @param  referralURLs           The set of referral URLs from the response,
   *                                if available.
   * @param  responseControls       The set of controls from the response, if
   *                                available.
   * @param  serverSASLCredentials  The server SASL credentials from the
   *                                response, if available.
   */
  public BindResult(final int messageID, final ResultCode resultCode,
                    final String diagnosticMessage, final String matchedDN,
                    final String[] referralURLs,
                    final Control[] responseControls,
                    final ASN1OctetString serverSASLCredentials)
  {
    super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs,
          responseControls);

    this.serverSASLCredentials = serverSASLCredentials;
  }



  /**
   * Creates a new bind result from the provided generic LDAP result.
   *
   * @param  ldapResult  The LDAP result to use to create this bind result.
   */
  public BindResult(final LDAPResult ldapResult)
  {
    super(ldapResult);

    serverSASLCredentials = null;
  }



  /**
   * Creates a new bind result from the provided bind result.  This constructor
   * may be used in creating custom subclasses.
   *
   * @param  bindResult  The bind result to use to create this bind result.
   */
  protected BindResult(final BindResult bindResult)
  {
    super(bindResult);

    serverSASLCredentials = bindResult.serverSASLCredentials;
  }



  /**
   * Creates a new bind result object with the provided message ID and with the
   * protocol op and controls read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this bind result.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded bind result.
   *
   * @throws  LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  static BindResult readBindResultFrom(final int messageID,
                         final ASN1StreamReaderSequence messageSequence,
                         final ASN1StreamReader reader)
         throws LDAPException
  {
    try
    {
      final ASN1StreamReaderSequence protocolOpSequence =
           reader.beginSequence();
      final ResultCode resultCode = ResultCode.valueOf(reader.readEnumerated());

      String matchedDN = reader.readString();
      if (matchedDN.length() == 0)
      {
        matchedDN = null;
      }

      String diagnosticMessage = reader.readString();
      if (diagnosticMessage.length() == 0)
      {
        diagnosticMessage = null;
      }

      String[] referralURLs = null;
      ASN1OctetString serverSASLCredentials = null;
      while (protocolOpSequence.hasMoreElements())
      {
        final byte type = (byte) reader.peek();
        switch (type)
        {
          case TYPE_REFERRAL_URLS:
            final ArrayList<String> refList = new ArrayList<String>(1);
            final ASN1StreamReaderSequence refSequence = reader.beginSequence();
            while (refSequence.hasMoreElements())
            {
              refList.add(reader.readString());
            }
            referralURLs = new String[refList.size()];
            refList.toArray(referralURLs);
            break;

          case TYPE_SERVER_SASL_CREDENTIALS:
            serverSASLCredentials =
                 new ASN1OctetString(type, reader.readBytes());
            break;

          default:
            throw new LDAPException(ResultCode.DECODING_ERROR,
                 ERR_BIND_RESULT_INVALID_ELEMENT.get(toHex(type)));
        }
      }

      Control[] controls = NO_CONTROLS;
      if (messageSequence.hasMoreElements())
      {
        final ArrayList<Control> controlList = new ArrayList<Control>(1);
        final ASN1StreamReaderSequence controlSequence = reader.beginSequence();
        while (controlSequence.hasMoreElements())
        {
          controlList.add(Control.readFrom(reader));
        }

        controls = new Control[controlList.size()];
        controlList.toArray(controls);
      }

      return new BindResult(messageID, resultCode, diagnosticMessage, matchedDN,
                            referralURLs, controls, serverSASLCredentials);
    }
    catch (LDAPException le)
    {
      debugException(le);
      throw le;
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_BIND_RESULT_CANNOT_DECODE.get(getExceptionMessage(e)), e);
    }
  }



  /**
   * Retrieves the server SASL credentials from the bind result, if available.
   *
   * @return  The server SASL credentials from the bind response, or
   *          {@code null} if none were provided.
   */
  public ASN1OctetString getServerSASLCredentials()
  {
    return serverSASLCredentials;
  }
}
