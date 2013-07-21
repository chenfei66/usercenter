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



import java.util.List;

import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.ldap.sdk.BindResult;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.DN;
import com.hwlcn.core.annotation.Extensible;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class defines an API that may be used to provide support for a specified
 * SASL mechanism in the in-memory directory server.
 */
@Extensible()
@ThreadSafety(level=ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class InMemorySASLBindHandler
{
  /**
   * Retrieves the name of the SASL mechanism supported by this bind handler.
   *
   * @return  The name of the SASL mechanism supported by this bind handler.
   */
  public abstract String getSASLMechanismName();



  /**
   * Performs the appropriate processing for a SASL bind request with the
   * provided information.
   * <BR><BR>
   * If the bind processing is successful, then this method should also call
   * {@link com.hwlcn.ldap.ldap.listener.InMemoryRequestHandler#setAuthenticatedDN(com.hwlcn.ldap.ldap.sdk.DN)} on the provided
   * request handler instance to set the identity of the authenticated user.
   * <BR><BR>
   * If the associated SASL mechanism requires multiple stages of processing
   * and it is necessary to store and retrieve state information to use in other
   * stages of the bind processing, then the map returned by the
   * {@link com.hwlcn.ldap.ldap.listener.InMemoryRequestHandler#getConnectionState()} method should be used
   * for this purpose.
   *
   * @param  handler      The in-memory request handler that accepted the bind
   *                      request.
   * @param  messageID    The message ID for the LDAP message that the client
   *                      used to send the request.
   * @param  bindDN       The bind DN provided by the client.
   * @param  credentials  The SASL credentials provided by the client, or
   *                      {@code null} if there were none.
   * @param  controls     The request controls provided by the client.
   *
   * @return  The result that should be returned to the client in response to
   *          the provided request.
   */
  public abstract BindResult processSASLBind(
                                  final InMemoryRequestHandler handler,
                                  final int messageID, final DN bindDN,
                                  final ASN1OctetString credentials,
                                  final List<Control> controls);



  /**
   * Retrieves a string representation of this SASL bind handler.
   *
   * @return  A string representation of this SASL bind handler.
   */
  @Override()
  public String toString()
  {
    return "InMemorySASLBindHandler(mechanismName='" + getSASLMechanismName() +
         ')';
  }
}
