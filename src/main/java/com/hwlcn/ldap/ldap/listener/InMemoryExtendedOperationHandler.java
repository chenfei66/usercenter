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

import com.hwlcn.ldap.ldap.sdk.ExtendedRequest;
import com.hwlcn.ldap.ldap.sdk.ExtendedResult;
import com.hwlcn.core.annotation.Extensible;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class defines an API that may be used to provide support for one or
 * more types of extended operations in the in-memory directory server.
 */
@Extensible()
@ThreadSafety(level=ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class InMemoryExtendedOperationHandler
{
  /**
   * Retrieves the name that should be used for this extended operation handler.
   *
   * @return  The name that should be used for this extended operation handler.
   */
  public abstract String getExtendedOperationHandlerName();



  /**
   * Retrieves a list of the extended request OIDs supported by this extended
   * operation handler.
   *
   * @return  A list of the extended request OIDs supported by this extended
   *          operation handler.
   */
  public abstract List<String> getSupportedExtendedRequestOIDs();



  /**
   * Performs the appropriate processing for the provided extended request.
   * This method is completely responsible for any controls associated with the
   * provided request.
   *
   * @param  handler    The in-memory request handler that accepted the extended
   *                    request.
   * @param  messageID  The message ID for the LDAP message that the client used
   *                    to send the request.
   * @param  request    The extended request to process, which will have a
   *                    request OID which matches one of the OIDs in the list
   *                    returned byt the
   *                    {@link #getSupportedExtendedRequestOIDs()} method.
   *
   * @return  The result that should be returned to the client in response to
   *          the provided request.
   */
  public abstract ExtendedResult processExtendedOperation(
                                      final InMemoryRequestHandler handler,
                                      final int messageID,
                                      final ExtendedRequest request);



  /**
   * Retrieves a string representation of this extended operation handler.
   *
   * @return  A string representation of this extended operation handler.
   */
  @Override()
  public String toString()
  {
    return getExtendedOperationHandlerName();
  }
}
