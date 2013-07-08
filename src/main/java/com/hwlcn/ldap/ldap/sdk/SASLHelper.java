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
package com.hwlcn.ldap.ldap.sdk;



import javax.security.sasl.SaslClient;

import com.hwlcn.ldap.asn1.ASN1OctetString;

import static com.hwlcn.ldap.ldap.sdk.LDAPMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides a mechanism for authenticating to an LDAP directory
 * server using the Java SASL client library.  It is intended for internal use
 * only.
 */
final class SASLHelper
{
  // The set of controls to include in the request.
  private final Control[] controls;

  // The message ID used when communicating with the directory server.
  private final int messageID;

  // The connection to use to communicate with the Directory Server.
  private final LDAPConnection connection;

  // The maximum length of time in milliseconds to wait for a response from the
  // server.
  private final long responseTimeoutMillis;

  // The SASL bind request being processed.
  private final SASLBindRequest bindRequest;

  // The SASL client to use to perform the processing.
  private final SaslClient saslClient;

  // The name of the SASL mechanism to use.
  private final String mechanism;



  /**
   * Creates a new SASL client with the provided information.
   *
   * @param  bindRequest            The SASL bind request being processed.
   * @param  connection             The connection to use to communicate with
   *                                the directory server.
   * @param  mechanism              The name of the SASL mechanism to use.
   * @param  saslClient             The Java SASL client instance to use to
   *                                perform the processing.
   * @param  controls               The set of controls to include in the
   *                                request.
   * @param  responseTimeoutMillis  The maximum length of time in milliseconds
   *                                to wait for a response from the server.
   */
  SASLHelper(final SASLBindRequest bindRequest, final LDAPConnection connection,
             final String mechanism, final SaslClient saslClient,
             final Control[] controls, final long responseTimeoutMillis)
  {
    this.bindRequest           = bindRequest;
    this.connection            = connection;
    this.mechanism             = mechanism;
    this.saslClient            = saslClient;
    this.controls              = controls;
    this.responseTimeoutMillis = responseTimeoutMillis;

    messageID = -1;
  }



  /**
   * Performs a SASL bind against an LDAP directory server.
   *
   * @return  The result of the bind operation processing.
   *
   * @throws  LDAPException  If a problem occurs while processing the bind.
   */
  BindResult processSASLBind()
         throws LDAPException
  {
    try
    {
      // Get the SASL credentials for the initial request.
      byte[] credBytes = null;
      try
      {
        if (saslClient.hasInitialResponse())
        {
          credBytes = saslClient.evaluateChallenge(new byte[0]);
        }
      }
      catch (Exception e)
      {
        debugException(e);
        throw new LDAPException(ResultCode.LOCAL_ERROR,
             ERR_SASL_CANNOT_CREATE_INITIAL_REQUEST.get(mechanism,
                  getExceptionMessage(e)), e);
      }

      ASN1OctetString saslCredentials;
      if ((credBytes == null) || (credBytes.length == 0))
      {
        saslCredentials = null;
      }
      else
      {
        saslCredentials = new ASN1OctetString(credBytes);
      }

      BindResult bindResult = bindRequest.sendBindRequest(connection, "",
           saslCredentials, controls, responseTimeoutMillis);

      if (! bindResult.getResultCode().equals(ResultCode.SASL_BIND_IN_PROGRESS))
      {
        return bindResult;
      }

      byte[] serverCredBytes = bindResult.getServerSASLCredentials().getValue();

      while (true)
      {
        try
        {
          credBytes = saslClient.evaluateChallenge(serverCredBytes);
        }
        catch (Exception e)
        {
          debugException(e);
          throw new LDAPException(ResultCode.LOCAL_ERROR,
               ERR_SASL_CANNOT_CREATE_SUBSEQUENT_REQUEST.get(mechanism,
                    getExceptionMessage(e)), e);
        }

        // Create the bind request protocol op.
        if ((credBytes == null) || (credBytes.length == 0))
        {
          saslCredentials = null;
        }
        else
        {
          saslCredentials = new ASN1OctetString(credBytes);
        }

        bindResult = bindRequest.sendBindRequest(connection, "",
             saslCredentials, controls, responseTimeoutMillis);
        if (! bindResult.getResultCode().equals(
                   ResultCode.SASL_BIND_IN_PROGRESS))
        {
          return bindResult;
        }

        serverCredBytes = bindResult.getServerSASLCredentials().getValue();
      }
    }
    finally
    {
      try
      {
        saslClient.dispose();
      }
      catch (Exception e)
      {
        debugException(e);
      }
    }
  }



  /**
   * Retrieves the message ID used when communicating with the directory server.
   *
   * @return  The message ID used when communicating with the directory server.
   */
  int getMessageID()
  {
    return messageID;
  }
}
