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
package com.hwlcn.ldap.ldap.listener;



import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.ldap.protocol.LDAPMessage;
import com.hwlcn.ldap.ldap.sdk.BindResult;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.DN;
import com.hwlcn.ldap.ldap.sdk.Entry;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.hwlcn.ldap.ldap.sdk.controls.AuthorizationIdentityResponseControl;
import com.hwlcn.ldap.util.Debug;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.ldap.util.StaticUtils;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.listener.ListenerMessages.*;



/**
 * This class defines a SASL bind handler which may be used to provide support
 * for the SASL PLAIN mechanism (as defined in RFC 4616) in the in-memory
 * directory server.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PLAINBindHandler
       extends InMemorySASLBindHandler
{
  /**
   * Creates a new instance of this SASL bind handler.
   */
  public PLAINBindHandler()
  {
    // No initialization is required.
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getSASLMechanismName()
  {
    return "PLAIN";
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public BindResult processSASLBind(final InMemoryRequestHandler handler,
                                    final int messageID, final DN bindDN,
                                    final ASN1OctetString credentials,
                                    final List<Control> controls)
  {
    // Process the provided request controls.
    final Map<String,Control> controlMap;
    try
    {
      controlMap = RequestControlPreProcessor.processControls(
           LDAPMessage.PROTOCOL_OP_TYPE_BIND_REQUEST, controls);
    }
    catch (final LDAPException le)
    {
      Debug.debugException(le);
      return  new BindResult(messageID, le.getResultCode(),
           le.getMessage(), le.getMatchedDN(), le.getReferralURLs(),
           le.getResponseControls());
    }


    // Parse the credentials, which should be in the form:
    //      [authzid] UTF8NUL authcid UTF8NUL passwd
    if (credentials == null)
    {
      return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS,
           ERR_PLAIN_BIND_NO_CREDENTIALS.get(), null, null, null);
    }

    int firstNullPos  = -1;
    int secondNullPos = -1;
    final byte[] credBytes = credentials.getValue();
    for (int i=0; i < credBytes.length; i++)
    {
      if (credBytes[i] == 0x00)
      {
        if (firstNullPos < 0)
        {
          firstNullPos = i;
        }
        else
        {
          secondNullPos = i;
          break;
        }
      }
    }

    if (secondNullPos < 0)
    {
      return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS,
           ERR_PLAIN_BIND_MALFORMED_CREDENTIALS.get(), null, null, null);
    }


    // There must have been at least an authentication identity.  Verify that it
    // is valid.
    final String authzID;
    final String authcID = StaticUtils.toUTF8String(credBytes, (firstNullPos+1),
         (secondNullPos-firstNullPos-1));
    if (firstNullPos == 0)
    {
      authzID = null;
    }
    else
    {
      authzID = StaticUtils.toUTF8String(credBytes, 0, firstNullPos);
    }

    DN authDN;
    try
    {
      authDN = handler.getDNForAuthzID(authcID);
    }
    catch (final LDAPException le)
    {
      Debug.debugException(le);
      return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS,
           le.getMessage(), le.getMatchedDN(), le.getReferralURLs(),
           le.getResponseControls());
    }


    // Verify that the password is correct.
    final byte[] bindPWBytes = new byte[credBytes.length - secondNullPos - 1];
    System.arraycopy(credBytes, secondNullPos+1, bindPWBytes, 0,
         bindPWBytes.length);

    final boolean passwordValid;
    if (authDN.isNullDN())
    {
      // For an anonymous bind, the password must be empty, and no authorization
      // ID may have been provided.
      passwordValid = ((bindPWBytes.length == 0) && (authzID == null));
    }
    else
    {
      // Determine the password for the target user, which may be an actual
      // entry or be included in the additional bind credentials.
      final byte[] userPWBytes;
      final Entry authEntry = handler.getEntry(authDN);
      if (authEntry == null)
      {
        userPWBytes = handler.getAdditionalBindCredentials(authDN);
      }
      else
      {
        userPWBytes = authEntry.getAttributeValueBytes("userPassword");
      }

      passwordValid =  Arrays.equals(bindPWBytes, userPWBytes);
    }

    if (! passwordValid)
    {
      return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS,
           null, null, null, null);
    }


    // The server doesn't really distinguish between authID and authzID, so
    // if an authzID was provided then we'll just behave as if the user
    // specified as the authzID had bound.
    String authID = authcID;
    if (authzID != null)
    {
      try
      {
        authID = authzID;
        authDN = handler.getDNForAuthzID(authzID);
      }
      catch (final LDAPException le)
      {
        Debug.debugException(le);
        return new BindResult(messageID, ResultCode.INVALID_CREDENTIALS,
             le.getMessage(), le.getMatchedDN(), le.getReferralURLs(),
             le.getResponseControls());
      }
    }

    handler.setAuthenticatedDN(authDN);
    final Control[] responseControls;
    if (controlMap.containsKey(AuthorizationIdentityRequestControl.
             AUTHORIZATION_IDENTITY_REQUEST_OID))
    {
      if (authDN == null)
      {
        responseControls = new Control[]
        {
          new AuthorizationIdentityResponseControl("")
        };
      }
      else
      {
        responseControls = new Control[]
        {
          new AuthorizationIdentityResponseControl("dn:" + authDN.toString())
        };
      }
    }
    else
    {
      responseControls = null;
    }

    return new BindResult(messageID, ResultCode.SUCCESS, null, null, null,
         responseControls);
  }
}
