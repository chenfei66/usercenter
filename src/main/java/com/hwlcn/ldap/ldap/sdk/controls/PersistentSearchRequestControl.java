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
package com.hwlcn.ldap.ldap.sdk.controls;



import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import com.hwlcn.ldap.asn1.ASN1Boolean;
import com.hwlcn.ldap.asn1.ASN1Element;
import com.hwlcn.ldap.asn1.ASN1Integer;
import com.hwlcn.ldap.asn1.ASN1OctetString;
import com.hwlcn.ldap.asn1.ASN1Sequence;
import com.hwlcn.ldap.ldap.sdk.Control;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.sdk.controls.ControlMessages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class provides an implementation of the persistent search request
 * control as defined in draft-ietf-ldapext-psearch.  It may be included in a
 * search request to request notification for changes to entries that match the
 * associated set of search criteria.  It can provide a basic mechanism for
 * clients to request to be notified whenever entries matching the associated
 * search criteria are altered.
 * <BR><BR>
 * A persistent search request control may include the following elements:
 * <UL>
 *   <LI>{@code changeTypes} -- Specifies the set of change types for which to
 *       receive notification.  This may be any combination of one or more of
 *       the {@link com.hwlcn.ldap.ldap.sdk.controls.PersistentSearchChangeType} values.</LI>
 *   <LI>{@code changesOnly} -- Indicates whether to only return updated entries
 *       that match the associated search criteria.  If this is {@code false},
 *       then the server will first return all existing entries in the server
 *       that match the search criteria, and will then begin returning entries
 *       that are updated in an operation associated with one of the
 *       registered {@code changeTypes}.  If this is {@code true}, then the
 *       server will not return all matching entries that already exist in the
 *       server but will only return entries in response to changes that
 *       occur.</LI>
 *   <LI>{@code returnECs} -- Indicates whether search result entries returned
 *       as a result of a change to the directory data should include the
 *       {@link com.hwlcn.ldap.ldap.sdk.controls.EntryChangeNotificationControl} to provide information about
 *       the type of operation that occurred.  If {@code changesOnly} is
 *       {@code false}, then entry change notification controls will not be
 *       included in existing entries that match the search criteria, but only
 *       in entries that are updated by an operation with one of the registered
 *       {@code changeTypes}.</LI>
 * </UL>
 * Note that when an entry is returned in response to a persistent search
 * request, the content of the entry that is returned will reflect the updated
 * entry in the server (except in the case of a delete operation, in which case
 * it will be the entry as it appeared before it was removed).  Other than the
 * information included in the entry change notification control, the search
 * result entry will not contain any information about what actually changed in
 * the entry.
 * <BR><BR>
 * Many servers do not enforce time limit or size limit restrictions on the
 * persistent search control, and because there is no defined "end" to the
 * search, it may remain active until the client abandons or cancels the search
 * or until the connection is closed.  Because of this, it is strongly
 * recommended that clients only use the persistent search request control in
 * conjunction with asynchronous search operations invoked using the
 * {@link com.hwlcn.ldap.ldap.sdk.LDAPConnection#asyncSearch} method.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the process for beginning an asynchronous
 * search that includes the persistent search control in order to notify the
 * client of all changes to entries within the "dc=example,dc=com" subtree.
 * <PRE>
 *   SearchRequest searchRequest =
 *        new SearchRequest(myAsyncSearchListener, "dc=example,dc=com",
 *                          SearchScope.SUB, "(objectClass=*)");
 *   searchRequest.addControl(new PersistentSearchRequestControl(
 *        PersistentSearchChangeType.allChangeTypes(), true, true));
 *   AsyncRequestID asyncRequestID = connection.asyncSearch(searchRequest);
 * </PRE>
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PersistentSearchRequestControl
       extends Control
{
  /**
   * The OID (2.16.840.1.113730.3.4.3) for the persistent search request
   * control.
   */
  public static final String PERSISTENT_SEARCH_REQUEST_OID =
       "2.16.840.1.113730.3.4.3";



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 3532762682521779027L;



  // Indicates whether the search should only return search result entries for
  // changes made to entries matching the search criteria, or if existing
  // entries already in the server should be returned as well.
  private final boolean changesOnly;

  // Indicates whether search result entries returned as part of this persistent
  // search should include the entry change notification control.
  private final boolean returnECs;

  // The set of change types for which this persistent search control is
  // registered.
  private final EnumSet<PersistentSearchChangeType> changeTypes;



  /**
   * Creates a new persistent search control with the provided information.  It
   * will be marked critical.
   *
   * @param  changeType   The change type for which to register.  It must not be
   *                      {@code null}.
   * @param  changesOnly  Indicates whether the search should only return search
   *                      result entries for changes made to entries matching
   *                      the search criteria, or if existing matching entries
   *                      in the server should be returned as well.
   * @param  returnECs    Indicates whether the search result entries returned
   *                      as part of this persistent search should include the
   *                      entry change notification control.
   */
  public PersistentSearchRequestControl(
              final PersistentSearchChangeType changeType,
              final boolean changesOnly, final boolean returnECs)
  {
    super(PERSISTENT_SEARCH_REQUEST_OID, true,
          encodeValue(changeType, changesOnly, returnECs));

    changeTypes = EnumSet.of(changeType);

    this.changesOnly = changesOnly;
    this.returnECs   = returnECs;
  }



  /**
   * Creates a new persistent search control with the provided information.  It
   * will be marked critical.
   *
   * @param  changeTypes  The set of change types for which to register.  It
   *                      must not be {@code null} or empty.
   * @param  changesOnly  Indicates whether the search should only return search
   *                      result entries for changes made to entries matching
   *                      the search criteria, or if existing matching entries
   *                      in the server should be returned as well.
   * @param  returnECs    Indicates whether the search result entries returned
   *                      as part of this persistent search should include the
   *                      entry change notification control.
   */
  public PersistentSearchRequestControl(
              final Set<PersistentSearchChangeType> changeTypes,
              final boolean changesOnly, final boolean returnECs)
  {
    super(PERSISTENT_SEARCH_REQUEST_OID, true,
          encodeValue(changeTypes, changesOnly, returnECs));

    this.changeTypes = EnumSet.copyOf(changeTypes);
    this.changesOnly = changesOnly;
    this.returnECs   = returnECs;
  }



  /**
   * Creates a new persistent search control with the provided information.
   *
   * @param  changeType   The change type for which to register.  It must not be
   *                      {@code null}.
   * @param  changesOnly  Indicates whether the search should only return search
   *                      result entries for changes made to entries matching
   *                      the search criteria, or if existing matching entries
   *                      in the server should be returned as well.
   * @param  returnECs    Indicates whether the search result entries returned
   *                      as part of this persistent search should include the
   *                      entry change notification control.
   * @param  isCritical   Indicates whether the control should be marked
   *                      critical.
   */
  public PersistentSearchRequestControl(
              final PersistentSearchChangeType changeType,
              final boolean changesOnly, final boolean returnECs,
              final boolean isCritical)
  {
    super(PERSISTENT_SEARCH_REQUEST_OID, isCritical,
          encodeValue(changeType, changesOnly, returnECs));

    changeTypes = EnumSet.of(changeType);

    this.changesOnly = changesOnly;
    this.returnECs   = returnECs;
  }



  /**
   * Creates a new persistent search control with the provided information.
   *
   * @param  changeTypes  The set of change types for which to register.  It
   *                      must not be {@code null} or empty.
   * @param  changesOnly  Indicates whether the search should only return search
   *                      result entries for changes made to entries matching
   *                      the search criteria, or if existing matching entries
   *                      in the server should be returned as well.
   * @param  returnECs    Indicates whether the search result entries returned
   *                      as part of this persistent search should include the
   *                      entry change notification control.
   * @param  isCritical   Indicates whether the control should be marked
   *                      critical.
   */
  public PersistentSearchRequestControl(
              final Set<PersistentSearchChangeType> changeTypes,
              final boolean changesOnly, final boolean returnECs,
              final boolean isCritical)
  {
    super(PERSISTENT_SEARCH_REQUEST_OID, isCritical,
          encodeValue(changeTypes, changesOnly, returnECs));

    this.changeTypes = EnumSet.copyOf(changeTypes);
    this.changesOnly = changesOnly;
    this.returnECs   = returnECs;
  }



  /**
   * Creates a new persistent search request control which is decoded from the
   * provided generic control.
   *
   * @param  control  The generic control to be decoded as a persistent search
   *                  request control.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         persistent search request control.
   */
  public PersistentSearchRequestControl(final Control control)
         throws LDAPException
  {
    super(control);

    final ASN1OctetString value = control.getValue();
    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_PSEARCH_NO_VALUE.get());
    }

    try
    {
      final ASN1Element valueElement = ASN1Element.decode(value.getValue());
      final ASN1Element[] elements =
           ASN1Sequence.decodeAsSequence(valueElement).elements();

      changeTypes =
           EnumSet.copyOf(PersistentSearchChangeType.decodeChangeTypes(
                          ASN1Integer.decodeAsInteger(elements[0]).intValue()));
      changesOnly = ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();
      returnECs   = ASN1Boolean.decodeAsBoolean(elements[2]).booleanValue();
    }
    catch (Exception e)
    {
      debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
                              ERR_PSEARCH_CANNOT_DECODE.get(e), e);
    }
  }



  /**
   * Encodes the provided information into an octet string that can be used as
   * the value for this control.
   *
   * @param  changeType   The change type for which to register.  It must not be
   *                      {@code null}.
   * @param  changesOnly  Indicates whether the search should only return search
   *                      result entries for changes made to entries matching
   *                      the search criteria, or if existing matching entries
   *                      in the server should be returned as well.
   * @param  returnECs    Indicates whether the search result entries returned
   *                      as part of this persistent search should include the
   *                      entry change notification control.
   *
   * @return  An ASN.1 octet string that can be used as the value for this
   *          control.
   */
  private static ASN1OctetString encodeValue(
               final PersistentSearchChangeType changeType,
               final boolean changesOnly, final boolean returnECs)
  {
    ensureNotNull(changeType);

    final ASN1Element[] elements =
    {
      new ASN1Integer(changeType.intValue()),
      new ASN1Boolean(changesOnly),
      new ASN1Boolean(returnECs)
    };

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * Encodes the provided information into an octet string that can be used as
   * the value for this control.
   *
   * @param  changeTypes  The set of change types for which to register.  It
   *                      must not be {@code null} or empty.
   * @param  changesOnly  Indicates whether the search should only return search
   *                      result entries for changes made to entries matching
   *                      the search criteria, or if existing matching entries
   *                      in the server should be returned as well.
   * @param  returnECs    Indicates whether the search result entries returned
   *                      as part of this persistent search should include the
   *                      entry change notification control.
   *
   * @return  An ASN.1 octet string that can be used as the value for this
   *          control.
   */
  private static ASN1OctetString encodeValue(
               final Set<PersistentSearchChangeType> changeTypes,
               final boolean changesOnly, final boolean returnECs)
  {
    ensureNotNull(changeTypes);
    ensureFalse(changeTypes.isEmpty(),
         "PersistentSearchRequestControl.changeTypes must not be empty.");

    final ASN1Element[] elements =
    {
      new ASN1Integer(
               PersistentSearchChangeType.encodeChangeTypes(changeTypes)),
      new ASN1Boolean(changesOnly),
      new ASN1Boolean(returnECs)
    };

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * Retrieves the set of change types for this persistent search request
   * control.
   *
   * @return  The set of change types for this persistent search request
   *          control.
   */
  public Set<PersistentSearchChangeType> getChangeTypes()
  {
    return changeTypes;
  }



  /**
   * Indicates whether the search should only return search result entries for
   * changes made to entries matching the search criteria, or if existing
   * matching entries should be returned as well.
   *
   * @return  {@code true} if the search should only return search result
   *          entries for changes matching the search criteria, or {@code false}
   *          if it should also return existing entries that match the search
   *          criteria.
   */
  public boolean changesOnly()
  {
    return changesOnly;
  }



  /**
   * Indicates whether the search result entries returned as part of this
   * persistent search should include the entry change notification control.
   *
   * @return  {@code true} if search result entries returned as part of this
   *          persistent search should include the entry change notification
   *          control, or {@code false} if not.
   */
  public boolean returnECs()
  {
    return returnECs;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_PSEARCH_REQUEST.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("PersistentSearchRequestControl(changeTypes={");

    final Iterator<PersistentSearchChangeType> iterator =
         changeTypes.iterator();
    while (iterator.hasNext())
    {
      buffer.append(iterator.next().getName());
      if (iterator.hasNext())
      {
        buffer.append(", ");
      }
    }

    buffer.append("}, changesOnly=");
    buffer.append(changesOnly);
    buffer.append(", returnECs=");
    buffer.append(returnECs);
    buffer.append(", isCritical=");
    buffer.append(isCritical());
    buffer.append(')');
  }
}
