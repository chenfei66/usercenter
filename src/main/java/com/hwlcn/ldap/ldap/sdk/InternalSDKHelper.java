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



import javax.net.ssl.SSLContext;

import com.hwlcn.ldap.asn1.ASN1StreamReader;
import com.hwlcn.ldap.asn1.ASN1StreamReaderSequence;
import com.hwlcn.ldap.ldap.protocol.LDAPMessage;
import com.hwlcn.ldap.ldap.sdk.extensions.CancelExtendedRequest;
import com.hwlcn.ldap.ldap.sdk.schema.Schema;
import com.hwlcn.core.annotation.InternalUseOnly;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class serves as a proxy that provides access to selected package-private
 * methods in classes in the {@code com.hwlcn.ldap.ldap.sdk} package so that they
 * may be called by code in other packages within the LDAP SDK.  Neither this
 * class nor the methods it contains may be used outside of the LDAP SDK.
 */
@InternalUseOnly()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class InternalSDKHelper
{
  /**
   * Prevent this class from being instantiated.
   */
  private InternalSDKHelper()
  {
    // No implementation is required.
  }



  /**
   * Sets the SO_TIMEOUT socket option on the socket associated with this
   * connection, which will take effect for the next blocking operation that is
   * performed on the socket.  This will have no effect for connections that are
   * operating in synchronous mode.
   *
   * @param  connection  The connection for which the SO_TIMEOUT option will be
   *                     set.
   * @param  soTimeout   The SO_TIMEOUT value (in milliseconds) that should be
   *                     used for the connection.  It must be greater than or
   *                     equal to zero, with a timeout of zero indicating an
   *                     unlimited timeout.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem is encountered while attempting to
   *                         set the SO_TIMEOUT value.
   */
  @InternalUseOnly()
  public static void setSoTimeout(final LDAPConnection connection,
                                  final int soTimeout)
         throws LDAPException
  {
    final LDAPConnectionReader connectionReader =
         connection.getConnectionInternals(true).getConnectionReader();
    if (connectionReader != null)
    {
      connectionReader.setSoTimeout(soTimeout);
    }
  }



  /**
   * Converts the provided clear-text connection to one that encrypts all
   * communication using Transport Layer Security.  This method is intended for
   * use as a helper for processing in the course of the StartTLS extended
   * operation and should not be used for other purposes.
   *
   * @param  connection  The LDAP connection to be converted to use TLS.
   * @param  sslContext  The SSL context to use when performing the negotiation.
   *                     It must not be {@code null}.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while converting the provided
   *                         connection to use TLS.
   */
  @InternalUseOnly()
  public static void convertToTLS(final LDAPConnection connection,
                                  final SSLContext sslContext)
         throws LDAPException
  {
    connection.convertToTLS(sslContext);
  }



  /**
   * Creates a new asynchronous request ID with the specified LDAP message ID.
   *
   * @param  targetMessageID  The message ID to use for the asynchronous request
   *                          ID.
   * @param  connection       The connection on which the associated request has
   *                          been sent.
   *
   * @return  The new asynchronous request ID.
   */
  @InternalUseOnly()
  public static AsyncRequestID createAsyncRequestID(final int targetMessageID,
                                    final LDAPConnection connection)
  {
    return new AsyncRequestID(targetMessageID, connection);
  }



  /**
   * Sends an LDAP cancel extended request to the server over the provided
   * connection without waiting for the response.  This is intended for use when
   * it is necessary to send a cancel request over a connection operating in
   * synchronous mode.
   *
   * @param  connection       The connection over which to send the cancel
   *                          request.
   * @param  targetMessageID  The message ID of the request to cancel.
   * @param  controls         The set of controls to include in the request.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while sending the cancel
   *                         request.
   */
  @InternalUseOnly()
  public static void cancel(final LDAPConnection connection,
                            final int targetMessageID,
                            final Control... controls)
         throws LDAPException
  {
    final CancelExtendedRequest cancelRequest =
         new CancelExtendedRequest(targetMessageID);
    connection.sendMessage(new LDAPMessage(connection.nextMessageID(),
         new ExtendedRequest(cancelRequest), controls));
  }



  /**
   * Creates a new LDAP result object with the provided message ID and with the
   * protocol op and controls read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this LDAP result.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded LDAP result object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static LDAPResult readLDAPResultFrom(final int messageID,
                                final ASN1StreamReaderSequence messageSequence,
                                final ASN1StreamReader reader)
         throws LDAPException
  {
    return LDAPResult.readLDAPResultFrom(messageID, messageSequence, reader);
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
   * @return  The decoded bind result object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static BindResult readBindResultFrom(final int messageID,
                                final ASN1StreamReaderSequence messageSequence,
                                final ASN1StreamReader reader)
         throws LDAPException
  {
    return BindResult.readBindResultFrom(messageID, messageSequence, reader);
  }



  /**
   * Creates a new compare result object with the provided message ID and with
   * the protocol op and controls read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this compare result.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded compare result object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static CompareResult readCompareResultFrom(final int messageID,
                     final ASN1StreamReaderSequence messageSequence,
                     final ASN1StreamReader reader)
         throws LDAPException
  {
    return CompareResult.readCompareResultFrom(messageID, messageSequence,
                                               reader);
  }



  /**
   * Creates a new extended result object with the provided message ID and with
   * the protocol op and controls read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this extended result.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded extended result object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static ExtendedResult readExtendedResultFrom(final int messageID,
                     final ASN1StreamReaderSequence messageSequence,
                     final ASN1StreamReader reader)
         throws LDAPException
  {
    return ExtendedResult.readExtendedResultFrom(messageID, messageSequence,
                                                 reader);
  }



  /**
   * Creates a new search result entry object with the protocol op and controls
   * read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this search result entry.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   * @param  schema           The schema to use to select the appropriate
   *                          matching rule to use for each attribute.  It may
   *                          be {@code null} if the default matching rule
   *                          should always be used.
   *
   * @return  The decoded search result entry object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static SearchResultEntry readSearchResultEntryFrom(final int messageID,
                     final ASN1StreamReaderSequence messageSequence,
                     final ASN1StreamReader reader, final Schema schema)
         throws LDAPException
  {
    return SearchResultEntry.readSearchEntryFrom(messageID, messageSequence,
                                                 reader, schema);
  }



  /**
   * Creates a new search result reference object with the protocol op and
   * controls read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this search result entry.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded search result reference object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static SearchResultReference readSearchResultReferenceFrom(
                     final int messageID,
                     final ASN1StreamReaderSequence messageSequence,
                     final ASN1StreamReader reader)
         throws LDAPException
  {
    return SearchResultReference.readSearchReferenceFrom(messageID,
                messageSequence, reader);
  }



  /**
   * Creates a new search result object with the provided message ID and with
   * the protocol op and controls read from the given ASN.1 stream reader.  It
   * will be necessary for the caller to ensure that the entry and reference
   * details are updated.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this search result.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded search result object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static SearchResult readSearchResultFrom(final int messageID,
                     final ASN1StreamReaderSequence messageSequence,
                     final ASN1StreamReader reader)
         throws LDAPException
  {
    return SearchResult.readSearchResultFrom(messageID, messageSequence,
                                             reader);
  }



  /**
   * Creates a new intermediate response object with the provided message ID and
   * with the protocol op and controls read from the given ASN.1 stream reader.
   *
   * @param  messageID        The LDAP message ID for the LDAP message that is
   *                          associated with this intermediate response.
   * @param  messageSequence  The ASN.1 stream reader sequence used in the
   *                          course of reading the LDAP message elements.
   * @param  reader           The ASN.1 stream reader from which to read the
   *                          protocol op and controls.
   *
   * @return  The decoded intermediate response object.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If a problem occurs while reading or decoding data
   *                         from the ASN.1 stream reader.
   */
  @InternalUseOnly()
  public static IntermediateResponse readIntermediateResponseFrom(
                     final int messageID,
                     final ASN1StreamReaderSequence messageSequence,
                     final ASN1StreamReader reader)
         throws LDAPException
  {
    return IntermediateResponse.readFrom(messageID, messageSequence, reader);
  }



  /**
   * Indicates whether automatic referral following is enabled for the provided
   * request.
   *
   * @param  request  The request for which to make the determination.
   *
   * @return  {@code Boolean.TRUE} if automatic referral following is enabled
   *          for this request, {@code Boolean.FALSE} if not, or {@code null} if
   *          a per-request behavior is not specified.
   */
  @InternalUseOnly()
  public static Boolean followReferralsInternal(final LDAPRequest request)
  {
    return request.followReferralsInternal();
  }



  /**
   * Retrieves the message ID that should be used for the next request sent
   * over the provided connection.
   *
   * @param  connection  The LDAP connection for which to obtain the next
   *                     request message ID.
   *
   * @return  The message ID that should be used for the next request sent over
   *          the provided connection, or -1 if the connection is not
   *          established.
   */
  @InternalUseOnly()
  public static int nextMessageID(final LDAPConnection connection)
  {
    return connection.nextMessageID();
  }



  /**
   * Retrieves the last successful bind request processed on the provided
   * connection.
   *
   * @param  connection  The LDAP connection for which to obtain the last
   *                     successful bind request.
   *
   * @return  The last successful bind request processed on the provided
   *          connection.  It may be {@code null} if no bind has been performed
   *          on the connection, or if the last bind attempt was not successful.
   */
  @InternalUseOnly()
  public static BindRequest getLastBindRequest(final LDAPConnection connection)
  {
    return connection.getLastBindRequest();
  }
}
