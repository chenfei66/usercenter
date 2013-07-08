/*
 * Copyright 2010-2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2010-2013 UnboundID Corp.
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

import com.hwlcn.ldap.ldap.protocol.SearchResultReferenceProtocolOp;
import com.hwlcn.ldap.ldap.sdk.SearchResultEntry;
import com.hwlcn.ldap.ldap.sdk.SearchResultListener;
import com.hwlcn.ldap.ldap.sdk.SearchResultReference;
import com.hwlcn.ldap.util.Debug;



/**
 * This class provides an implementation of a search result listener that will
 * be used by the {@link com.hwlcn.ldap.ldap.listener.ProxyRequestHandler} class in the course of returning
 * entries to the client.
 */
final class ProxySearchResultListener
      implements SearchResultListener
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = -1581507251328572490L;



  // The message ID for the associated search request.
  private final int messageID;

  // The client connection that will be used to return the results.
  private final LDAPListenerClientConnection clientConnection;



  /**
   * Creates a new proxy search result listener with the provided information.
   *
   * @param  clientConnection  The client connection to which the results will
   *                           be sent.
   * @param  messageID         The message ID that will be used for any response
   *                           messages returned to the client.
   */
  ProxySearchResultListener(final LDAPListenerClientConnection clientConnection,
                            final int messageID)
  {
    this.clientConnection = clientConnection;
    this.messageID        = messageID;
  }



  /**
   * {@inheritDoc}
   */
  public void searchEntryReturned(final SearchResultEntry searchEntry)
  {
    try
    {
      clientConnection.sendSearchResultEntry(messageID, searchEntry,
           searchEntry.getControls());
    }
    catch (final Exception e)
    {
      Debug.debugException(e);
    }
  }



  /**
   * {@inheritDoc}
   */
  public void searchReferenceReturned(
                   final SearchResultReference searchReference)
  {
    try
    {
      final SearchResultReferenceProtocolOp searchResultReferenceProtocolOp =
           new SearchResultReferenceProtocolOp(Arrays.asList(
                searchReference.getReferralURLs()));

      clientConnection.sendSearchResultReference(messageID,
           searchResultReferenceProtocolOp, searchReference.getControls());
    }
    catch (final Exception e)
    {
      Debug.debugException(e);
    }
  }
}
