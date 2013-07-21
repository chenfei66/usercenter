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



import com.hwlcn.core.annotation.Extensible;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This interface defines a method that may be invoked if an unsolicited
 * notification is received from the directory server.  An unsolicited
 * notification handler should be defined in the {@link com.hwlcn.ldap.ldap.sdk.LDAPConnectionOptions}
 * for an {@link com.hwlcn.ldap.ldap.sdk.LDAPConnection} to be called whenever an unsolicited
 * notification is received for that connection.
 * <BR><BR>
 * An unsolicited notification is a type of extended response that is sent from
 * the server to the client without a corresponding request, and it may be used
 * to alert the client of a significant server-side event.  For example,
 * section 4.4.1 of <A HREF="http://www.ietf.org/rfc/rfc4511.txt">RFC 4511</A>
 * defines a notice of disconnection unsolicited notification that can be used
 *  by the server to inform the client that it is about to close the connection.
 * <BR><BR>
 * Implementations of this interface should be threadsafe to ensure that
 * multiple connections will be able to safely use the same
 * {@code UnsolicitedNotificationHandler} instance.
 */
@Extensible()
@ThreadSafety(level=ThreadSafetyLevel.INTERFACE_THREADSAFE)
public interface UnsolicitedNotificationHandler
{
  /**
   * Performs any processing that may be necessary in response to the provided
   * unsolicited notification that has been received from the server.
   *
   * @param  connection    The connection on which the unsolicited notification
   *                       was received.
   * @param  notification  The unsolicited notification that has been received
   *                       from the server.
   */
  void handleUnsolicitedNotification(final LDAPConnection connection,
                                     final ExtendedResult notification);
}
