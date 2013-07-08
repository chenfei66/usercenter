/*
 * Copyright 2013 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2013 UnboundID Corp.
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



import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;



/**
 * This enum defines a set of message keys for messages in the
 * com.hwlcn.ldap.ldap.sdk package, which correspond to messages in the
 * ldap-ldapsdk-ldap.properties properties file.
 * <BR><BR>
 * This source file was generated from the properties file.
 * Do not edit it directly.
 */
enum LDAPMessages
{
  /**
   * Abandon operations are not supported on connections operating in synchronous mode.
   */
  ERR_ABANDON_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE("Abandon operations are not supported on connections operating in synchronous mode."),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for an add response from server {1}.
   */
  ERR_ADD_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for an add response from server {1}."),



  /**
   * Add processing was interrupted while waiting for a response from server {0}.
   */
  ERR_ADD_INTERRUPTED("Add processing was interrupted while waiting for a response from server {0}."),



  /**
   * Asynchronous operations are not supported on connections operating in synchronous mode.
   */
  ERR_ASYNC_NOT_SUPPORTED_IN_SYNCHRONOUS_MODE("Asynchronous operations are not supported on connections operating in synchronous mode."),



  /**
   * The search result listener for the provided search request is not an AsyncSearchResultListener.
   */
  ERR_ASYNC_SEARCH_INVALID_LISTENER("The search result listener for the provided search request is not an AsyncSearchResultListener."),



  /**
   * The provided search request is not configured with a search result listener.
   */
  ERR_ASYNC_SEARCH_NO_LISTENER("The provided search request is not configured with a search result listener."),



  /**
   * Unable to read or decode an LDAP attribute:  {0}
   */
  ERR_ATTR_CANNOT_DECODE("Unable to read or decode an LDAP attribute:  {0}"),



  /**
   * An LDAP attribute sequence must contain exactly two elements, but the provided sequence had {0,number,0} elements.
   */
  ERR_ATTR_DECODE_INVALID_COUNT("An LDAP attribute sequence must contain exactly two elements, but the provided sequence had {0,number,0} elements."),



  /**
   * Unable to decode the second element of the LDAP attribute sequence as the value set:  {0}
   */
  ERR_ATTR_DECODE_VALUE_SET("Unable to decode the second element of the LDAP attribute sequence as the value set:  {0}"),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for a bind response from server {1}.
   */
  ERR_BIND_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for a bind response from server {1}."),



  /**
   * Bind processing was interrupted while waiting for a response from server {0}.
   */
  ERR_BIND_INTERRUPTED("Bind processing was interrupted while waiting for a response from server {0}."),



  /**
   * Unable to read or decode a bind result:  {0}
   */
  ERR_BIND_RESULT_CANNOT_DECODE("Unable to read or decode a bind result:  {0}"),



  /**
   * Invalid element type {0} found in a bind result sequence.
   */
  ERR_BIND_RESULT_INVALID_ELEMENT("Invalid element type {0} found in a bind result sequence."),



  /**
   * Unable to parse the value of attribute ''{0}'' as a set of attributes:  {1}
   */
  ERR_CHANGELOG_CANNOT_PARSE_ATTR_LIST("Unable to parse the value of attribute ''{0}'' as a set of attributes:  {1}"),



  /**
   * Unable to decode the value of the {0} attribute in the changelog entry as a set of attributes:  {0}
   */
  ERR_CHANGELOG_CANNOT_PARSE_DELENTRYATTRS("Unable to decode the value of the {0} attribute in the changelog entry as a set of attributes:  {0}"),



  /**
   * Unable to parse the value of attribute ''{0}'' as a set of modifications:  {1}
   */
  ERR_CHANGELOG_CANNOT_PARSE_MOD_LIST("Unable to parse the value of attribute ''{0}'' as a set of modifications:  {1}"),



  /**
   * The value ''{0}'' is not a valid change number
   */
  ERR_CHANGELOG_INVALID_CHANGE_NUMBER("The value ''{0}'' is not a valid change number"),



  /**
   * The value ''{0}'' is not a valid change type.
   */
  ERR_CHANGELOG_INVALID_CHANGE_TYPE("The value ''{0}'' is not a valid change type."),



  /**
   * Unable to decode the value of the {0} attribute in the changelog entry as a set of modifications:  {1}
   */
  ERR_CHANGELOG_INVALID_DELENTRYATTRS_MODS("Unable to decode the value of the {0} attribute in the changelog entry as a set of modifications:  {1}"),



  /**
   * Unable to decode the value of the {0} attribute in the changelog entry because it was structured as a set of modifications but not all of the modifications were delete modifications.
   */
  ERR_CHANGELOG_INVALID_DELENTRYATTRS_MOD_TYPE("Unable to decode the value of the {0} attribute in the changelog entry because it was structured as a set of modifications but not all of the modifications were delete modifications."),



  /**
   * The provided changelog entry does not contain a value for the changes attribute.
   */
  ERR_CHANGELOG_MISSING_CHANGES("The provided changelog entry does not contain a value for the changes attribute."),



  /**
   * The provided modify DN changelog entry does not contain a value for the deleteOldRDN attribute.
   */
  ERR_CHANGELOG_MISSING_DELETE_OLD_RDN("The provided modify DN changelog entry does not contain a value for the deleteOldRDN attribute."),



  /**
   * The provided modify DN changelog entry does not contain a value for the newRDN attribute.
   */
  ERR_CHANGELOG_MISSING_NEW_RDN("The provided modify DN changelog entry does not contain a value for the newRDN attribute."),



  /**
   * The provided entry does not contain a value for the required changeNumber attribute.
   */
  ERR_CHANGELOG_NO_CHANGE_NUMBER("The provided entry does not contain a value for the required changeNumber attribute."),



  /**
   * The provided entry does not contain a value for the required changeType attribute.
   */
  ERR_CHANGELOG_NO_CHANGE_TYPE("The provided entry does not contain a value for the required changeType attribute."),



  /**
   * The provided entry does not contain a value for the required targetDN attribute.
   */
  ERR_CHANGELOG_NO_TARGET_DN("The provided entry does not contain a value for the required targetDN attribute."),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for a compare response from server {1}.
   */
  ERR_COMPARE_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for a compare response from server {1}."),



  /**
   * Compare processing was interrupted while waiting for a response from server {0}.
   */
  ERR_COMPARE_INTERRUPTED("Compare processing was interrupted while waiting for a response from server {0}."),



  /**
   * An error occurred while attempting to establish a connection to server {0}:{1,number,0}:  {2}
   */
  ERR_CONNECT_THREAD_EXCEPTION("An error occurred while attempting to establish a connection to server {0}:{1,number,0}:  {2}"),



  /**
   * Unable to establish a connection to server {0}:{1,number,0} within the configured timeout of {2,number,0} milliseconds.
   */
  ERR_CONNECT_THREAD_TIMEOUT("Unable to establish a connection to server {0}:{1,number,0} within the configured timeout of {2,number,0} milliseconds."),



  /**
   * The specified message ID is already registered with another response acceptor.
   */
  ERR_CONNREADER_MSGID_IN_USE("The specified message ID is already registered with another response acceptor."),



  /**
   * The connection reader was unable to successfully complete TLS negotiation:  {0}
   */
  ERR_CONNREADER_STARTTLS_FAILED("The connection reader was unable to successfully complete TLS negotiation:  {0}"),



  /**
   * The connection reader was unable to successfully complete TLS negotiation, but no additional information is available to explain the reason for the failure.
   */
  ERR_CONNREADER_STARTTLS_FAILED_NO_EXCEPTION("The connection reader was unable to successfully complete TLS negotiation, but no additional information is available to explain the reason for the failure."),



  /**
   * Unable to authenticate to remote system {0}:{1,number,0} because the authentication method used does not support rebinding.
   */
  ERR_CONN_CANNOT_AUTHENTICATE_FOR_REFERRAL("Unable to authenticate to remote system {0}:{1,number,0} because the authentication method used does not support rebinding."),



  /**
   * The connection to directory server {0} was lost and the authentication method chosen does not provide the ability to automatically re-authenticate.
   */
  ERR_CONN_CANNOT_REAUTHENTICATE("The connection to directory server {0} was lost and the authentication method chosen does not provide the ability to automatically re-authenticate."),



  /**
   * The connection was closed through an unexpected call path that did not first set the disconnect reason (stack trace:  {0}).
   */
  ERR_CONN_CLOSED_BY_UNEXPECTED_CALL_PATH("The connection was closed through an unexpected call path that did not first set the disconnect reason (stack trace:  {0})."),



  /**
   * The connection to server {0} was closed while waiting for a response to an add request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_ADD_RESPONSE("The connection to server {0} was closed while waiting for a response to an add request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to an add request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_ADD_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to an add request {1}:  {2}"),



  /**
   * The connection to the directory server was closed while waiting for a response to an asynchronous request.
   */
  ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE("The connection to the directory server was closed while waiting for a response to an asynchronous request."),



  /**
   * The connection to the directory server was closed while waiting for a response to an asynchronous request:  {0}
   */
  ERR_CONN_CLOSED_WAITING_FOR_ASYNC_RESPONSE_WITH_MESSAGE("The connection to the directory server was closed while waiting for a response to an asynchronous request:  {0}"),



  /**
   * The connection to server {0} was closed while waiting for a response to a bind request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE("The connection to server {0} was closed while waiting for a response to a bind request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to a bind request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_BIND_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to a bind request {1}:  {2}"),



  /**
   * The connection to server {0} was closed while waiting for a response to a compare request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_COMPARE_RESPONSE("The connection to server {0} was closed while waiting for a response to a compare request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to a compare request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_COMPARE_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to a compare request {1}:  {2}"),



  /**
   * The connection to server {0} was closed while waiting for a response to a delete request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_DELETE_RESPONSE("The connection to server {0} was closed while waiting for a response to a delete request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to a delete request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_DELETE_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to a delete request {1}:  {2}"),



  /**
   * The connection to server {0} was closed while waiting for a response to an extended request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_EXTENDED_RESPONSE("The connection to server {0} was closed while waiting for a response to an extended request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to an extended request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_EXTENDED_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to an extended request {1}:  {2}"),



  /**
   * The connection to server {0} was closed while waiting for a response to a modify DN request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_MODIFY_DN_RESPONSE("The connection to server {0} was closed while waiting for a response to a modify DN request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to a modify DN request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_MODIFY_DN_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to a modify DN request {1}:  {2}"),



  /**
   * The connection to server {0} was closed while waiting for a response to a modify request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_MODIFY_RESPONSE("The connection to server {0} was closed while waiting for a response to a modify request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to a modify request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_MODIFY_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to a modify request {1}:  {2}"),



  /**
   * The connection to server {0} was closed while waiting for a response to search request {1}.
   */
  ERR_CONN_CLOSED_WAITING_FOR_SEARCH_RESPONSE("The connection to server {0} was closed while waiting for a response to search request {1}."),



  /**
   * The connection to server {0} was closed while waiting for a response to search request {1}:  {2}
   */
  ERR_CONN_CLOSED_WAITING_FOR_SEARCH_RESPONSE_WITH_MESSAGE("The connection to server {0} was closed while waiting for a response to search request {1}:  {2}"),



  /**
   * An error occurred while attempting to connect to server {0}:  {1}
   */
  ERR_CONN_CONNECT_ERROR("An error occurred while attempting to connect to server {0}:  {1}"),



  /**
   * An error occurred while encoding the LDAP message or sending it to server {0}:  {1}
   */
  ERR_CONN_ENCODE_ERROR("An error occurred while encoding the LDAP message or sending it to server {0}:  {1}"),



  /**
   * Multiple connect failures in less than one second.
   */
  ERR_CONN_MULTIPLE_FAILURES("Multiple connect failures in less than one second."),



  /**
   * The connection is not established.
   */
  ERR_CONN_NOT_ESTABLISHED("The connection is not established."),



  /**
   * An attempt was made to read a response on a connection that is not established.
   */
  ERR_CONN_READ_RESPONSE_NOT_ESTABLISHED("An attempt was made to read a response on a connection that is not established."),



  /**
   * An error occurred while attempting to send the LDAP message to server {0}:  {1}
   */
  ERR_CONN_SEND_ERROR("An error occurred while attempting to send the LDAP message to server {0}:  {1}"),



  /**
   * Unable to decode the provided sequence as a set of controls because one of the elements could not be decoded as a control sequence:  {0}
   */
  ERR_CONTROLS_DECODE_ELEMENT_NOT_SEQUENCE("Unable to decode the provided sequence as a set of controls because one of the elements could not be decoded as a control sequence:  {0}"),



  /**
   * Unable to read or decode an LDAP control:  {0}
   */
  ERR_CONTROL_CANNOT_DECODE("Unable to read or decode an LDAP control:  {0}"),



  /**
   * Unable to decode the second element of the control sequence as a Boolean criticality:  {0}
   */
  ERR_CONTROL_DECODE_CRITICALITY("Unable to decode the second element of the control sequence as a Boolean criticality:  {0}"),



  /**
   * The control sequence contained an invalid number of elements (expected between 1 and 3, got {0,number,0}).
   */
  ERR_CONTROL_DECODE_INVALID_ELEMENT_COUNT("The control sequence contained an invalid number of elements (expected between 1 and 3, got {0,number,0})."),



  /**
   * The second element of the control sequence contained an invalid BER type of {0}.
   */
  ERR_CONTROL_INVALID_TYPE("The second element of the control sequence contained an invalid BER type of {0}."),



  /**
   * Unable to create a CRAM-MD5 SASL client:  {0}
   */
  ERR_CRAMMD5_CANNOT_CREATE_SASL_CLIENT("Unable to create a CRAM-MD5 SASL client:  {0}"),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for a delete response from server {1}.
   */
  ERR_DELETE_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for a delete response from server {1}."),



  /**
   * Delete processing was interrupted while waiting for a response from server {0}.
   */
  ERR_DELETE_INTERRUPTED("Delete processing was interrupted while waiting for a response from server {0}."),



  /**
   * Unable to create a DIGEST-MD5 SASL client:  {0}
   */
  ERR_DIGESTMD5_CANNOT_CREATE_SASL_CLIENT("Unable to create a DIGEST-MD5 SASL client:  {0}"),



  /**
   * Unexpected comma or semicolon found at the end of the DN string.
   */
  ERR_DN_ENDS_WITH_COMMA("Unexpected comma or semicolon found at the end of the DN string."),



  /**
   * The provided DN string ends with a plus sign that is not followed by a name-value pair.
   */
  ERR_DN_ENDS_WITH_PLUS("The provided DN string ends with a plus sign that is not followed by a name-value pair."),



  /**
   * An error occurred while attempting to retrieve entry ''{0}'':  {1}
   */
  ERR_DN_ENTRY_SOURCE_ERR_RETRIEVING_ENTRY("An error occurred while attempting to retrieve entry ''{0}'':  {1}"),



  /**
   * Entry ''{0}'' does not exist or is not readable.
   */
  ERR_DN_ENTRY_SOURCE_NO_SUCH_ENTRY("Entry ''{0}'' does not exist or is not readable."),



  /**
   * Unable to determine whether DN ''{0}'' matches the specified base and scope because the provided scope ''{1}'' is not supported for this determination.
   */
  ERR_DN_MATCHES_UNSUPPORTED_SCOPE("Unable to determine whether DN ''{0}'' matches the specified base and scope because the provided scope ''{1}'' is not supported for this determination."),



  /**
   * The provided DN string does not contain an attribute name in an RDN component.
   */
  ERR_DN_NO_ATTR_IN_RDN("The provided DN string does not contain an attribute name in an RDN component."),



  /**
   * The provided string could not be decoded as a DN because no equal sign was found after the RDN attribute ''{0}''.
   */
  ERR_DN_NO_EQUAL_SIGN("The provided string could not be decoded as a DN because no equal sign was found after the RDN attribute ''{0}''."),



  /**
   * The provided DN string does not have a value for attribute {0}.
   */
  ERR_DN_NO_VALUE_FOR_ATTR("The provided DN string does not have a value for attribute {0}."),



  /**
   * Unexpected character {0} found at position {1,number,0} in the DN string.
   */
  ERR_DN_UNEXPECTED_CHAR("Unexpected character {0} found at position {1,number,0} in the DN string."),



  /**
   * Unexpected comma or semicolon encountered at position {0,number,0} of the DN string.
   */
  ERR_DN_UNEXPECTED_COMMA("Unexpected comma or semicolon encountered at position {0,number,0} of the DN string."),



  /**
   * Unable to add value ''{0}'' for attribute {1} because that value already exists in the entry.
   */
  ERR_ENTRY_APPLY_MODS_ADD_EXISTING("Unable to add value ''{0}'' for attribute {1} because that value already exists in the entry."),



  /**
   * Unable to perform the add for attribute {0} because there were no values contained in the provided modification.
   */
  ERR_ENTRY_APPLY_MODS_ADD_NO_VALUES("Unable to perform the add for attribute {0} because there were no values contained in the provided modification."),



  /**
   * An error occurred while attempting to determine the RDN for entry ''{0}'' to ensure that none of the modifications targets an RDN attribute:  {1}
   */
  ERR_ENTRY_APPLY_MODS_CANNOT_DETERMINE_RDN("An error occurred while attempting to determine the RDN for entry ''{0}'' to ensure that none of the modifications targets an RDN attribute:  {1}"),



  /**
   * Unable to remove attribute {0} because it does not exist in the entry.
   */
  ERR_ENTRY_APPLY_MODS_DELETE_NONEXISTENT_ATTR("Unable to remove attribute {0} because it does not exist in the entry."),



  /**
   * Unable to remove value ''{0}'' for attribute {1} because it does not exist in the entry.
   */
  ERR_ENTRY_APPLY_MODS_DELETE_NONEXISTENT_VALUE("Unable to remove value ''{0}'' for attribute {1} because it does not exist in the entry."),



  /**
   * Unable to update entry {0}:  {1}
   */
  ERR_ENTRY_APPLY_MODS_FAILURE("Unable to update entry {0}:  {1}"),



  /**
   * Unable to increment the value for attribute {0} because the value ''{1}'' for that attribute in the entry cannot be parsed as an integer.
   */
  ERR_ENTRY_APPLY_MODS_INCREMENT_ENTRY_VALUE_NOT_INTEGER("Unable to increment the value for attribute {0} because the value ''{1}'' for that attribute in the entry cannot be parsed as an integer."),



  /**
   * Unable to increment the value for attribute {0} because the provided increment value ''{1}'' cannot be parsed as an integer.
   */
  ERR_ENTRY_APPLY_MODS_INCREMENT_MOD_VALUE_NOT_INTEGER("Unable to increment the value for attribute {0} because the provided increment value ''{1}'' cannot be parsed as an integer."),



  /**
   * Unable to increment the value for attribute {0} because the increment modification had multiple values.
   */
  ERR_ENTRY_APPLY_MODS_INCREMENT_MULTIPLE_MOD_VALUES("Unable to increment the value for attribute {0} because the increment modification had multiple values."),



  /**
   * Unable to increment the value for attribute {0} because the entry has multiple values for that attribute.
   */
  ERR_ENTRY_APPLY_MODS_INCREMENT_NOT_SINGLE_VALUED("Unable to increment the value for attribute {0} because the entry has multiple values for that attribute."),



  /**
   * Unable to increment the value for attribute {0} because the increment modification did not include any values.
   */
  ERR_ENTRY_APPLY_MODS_INCREMENT_NO_MOD_VALUES("Unable to increment the value for attribute {0} because the increment modification did not include any values."),



  /**
   * Unable to increment the value for attribute {0} because that attribute does not exist in the entry.
   */
  ERR_ENTRY_APPLY_MODS_INCREMENT_NO_SUCH_ATTR("Unable to increment the value for attribute {0} because that attribute does not exist in the entry."),



  /**
   * Unable to apply the modification to entry ''{0}'' because the modifications would have impacted the entry RDN.
   */
  ERR_ENTRY_APPLY_MODS_TARGETS_RDN("Unable to apply the modification to entry ''{0}'' because the modifications would have impacted the entry RDN."),



  /**
   * Unknown modification type {0}.
   */
  ERR_ENTRY_APPLY_MODS_UNKNOWN_TYPE("Unknown modification type {0}."),



  /**
   * An error occurred while attempting to handle the response message:  {0}
   */
  ERR_EXCEPTION_HANDLING_RESPONSE("An error occurred while attempting to handle the response message:  {0}"),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for an extended response from server {1}.
   */
  ERR_EXTENDED_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for an extended response from server {1}."),



  /**
   * Unable to read or decode an extended result:  {0}
   */
  ERR_EXTENDED_RESULT_CANNOT_DECODE("Unable to read or decode an extended result:  {0}"),



  /**
   * Invalid element type {0} found in an extended result sequence.
   */
  ERR_EXTENDED_RESULT_INVALID_ELEMENT("Invalid element type {0} found in an extended result sequence."),



  /**
   * Extended operation processing was interrupted while waiting for a response from server {0}.
   */
  ERR_EXTOP_INTERRUPTED("Extended operation processing was interrupted while waiting for a response from server {0}."),



  /**
   * Unable to establish a connection to any server in the fastest connect set because connection attempts failed in all servers.
   */
  ERR_FASTEST_CONNECT_SET_ALL_FAILED("Unable to establish a connection to any server in the fastest connect set because connection attempts failed in all servers."),



  /**
   * Unable to establish a connection to any server in the fastest connect set:  {0}
   */
  ERR_FASTEST_CONNECT_SET_CONNECT_EXCEPTION("Unable to establish a connection to any server in the fastest connect set:  {0}"),



  /**
   * Unable to establish a connection to any server in the fastest connect set within the connect timeout of {0,number,0}ms.
   */
  ERR_FASTEST_CONNECT_SET_CONNECT_TIMEOUT("Unable to establish a connection to any server in the fastest connect set within the connect timeout of {0,number,0}ms."),



  /**
   * The connection options to be used for the fastest connect set do not allow concurrent socket factory use.
   */
  ERR_FASTEST_CONNECT_SET_OPTIONS_NOT_PARALLEL("The connection options to be used for the fastest connect set do not allow concurrent socket factory use."),



  /**
   * An empty password was read from file ''{0}''.
   */
  ERR_FILE_PW_PROVIDER_EMPTY_PW("An empty password was read from file ''{0}''."),



  /**
   * An error occurred while attempting to read a password from file ''{0}'':  {1}
   */
  ERR_FILE_PW_PROVIDER_ERROR_READING_PW("An error occurred while attempting to read a password from file ''{0}'':  {1}"),



  /**
   * Approximate matching is not supported when attempting to determine whether a given entry matches a search filter.
   */
  ERR_FILTER_APPROXIMATE_MATCHING_NOT_SUPPORTED("Approximate matching is not supported when attempting to determine whether a given entry matches a search filter."),



  /**
   * Unable to read or decode a search filter:  {0}
   */
  ERR_FILTER_CANNOT_DECODE("Unable to read or decode a search filter:  {0}"),



  /**
   * Unable to decode the value of the filter element as an attribute value assertion:  {0}
   */
  ERR_FILTER_CANNOT_DECODE_AVA("Unable to decode the value of the filter element as an attribute value assertion:  {0}"),



  /**
   * Unable to decode the provided filter element as a set of components for an AND or OR filter:  {0}
   */
  ERR_FILTER_CANNOT_DECODE_COMPS("Unable to decode the provided filter element as a set of components for an AND or OR filter:  {0}"),



  /**
   * Unable to decode the provided filter element as an extensible match component:  {0}
   */
  ERR_FILTER_CANNOT_DECODE_EXTMATCH("Unable to decode the provided filter element as an extensible match component:  {0}"),



  /**
   * Unable to decode the provided filter element as the component for a NOT filter:  {0}
   */
  ERR_FILTER_CANNOT_DECODE_NOT_COMP("Unable to decode the provided filter element as the component for a NOT filter:  {0}"),



  /**
   * Unable to decode the value of the filter element as a substring filter sequence:  {0}
   */
  ERR_FILTER_CANNOT_DECODE_SUBSTRING("Unable to decode the value of the filter element as a substring filter sequence:  {0}"),



  /**
   * Unable to parse the matching rule ID and/or dnAttributes flag from an extensible match filter component beginning at position {0,number,0}.
   */
  ERR_FILTER_CANNOT_PARSE_MRID("Unable to parse the matching rule ID and/or dnAttributes flag from an extensible match filter component beginning at position {0,number,0}."),



  /**
   * The provided ASN.1 element had an invalid BER type ({0}) for an LDAP filter component.
   */
  ERR_FILTER_ELEMENT_INVALID_TYPE("The provided ASN.1 element had an invalid BER type ({0}) for an LDAP filter component."),



  /**
   * Zero-length attribute name found in the filter component starting at position {0,number,0}.
   */
  ERR_FILTER_EMPTY_ATTR_NAME("Zero-length attribute name found in the filter component starting at position {0,number,0}."),



  /**
   * Unable to parse an extensible match filter component starting at position {0,number,0} because the matching rule ID string was empty.
   */
  ERR_FILTER_EMPTY_MRID("Unable to parse an extensible match filter component starting at position {0,number,0} because the matching rule ID string was empty."),



  /**
   * Unexpected end of filter encountered after the greater than sign in the filter component starting at position {0,number,0}.
   */
  ERR_FILTER_END_AFTER_GT("Unexpected end of filter encountered after the greater than sign in the filter component starting at position {0,number,0}."),



  /**
   * Unexpected end of filter encountered after the less than sign in the filter component starting at position {0,number,0}.
   */
  ERR_FILTER_END_AFTER_LT("Unexpected end of filter encountered after the less than sign in the filter component starting at position {0,number,0}."),



  /**
   * Unexpected end of filter encountered after the tilde in the filter component starting at position {0,number,0}.
   */
  ERR_FILTER_END_AFTER_TILDE("Unexpected end of filter encountered after the tilde in the filter component starting at position {0,number,0}."),



  /**
   * Expected a closing parenthesis in the provided filter string at position {0,number,0}.
   */
  ERR_FILTER_EXPECTED_CLOSE_PAREN("Expected a closing parenthesis in the provided filter string at position {0,number,0}."),



  /**
   * Expected an opening parenthesis in the provided filter string at position {0,number,0}.
   */
  ERR_FILTER_EXPECTED_OPEN_PAREN("Expected an opening parenthesis in the provided filter string at position {0,number,0}."),



  /**
   * Extensible matching is not supported when attempting to determine whether a given entry matches a search filter.
   */
  ERR_FILTER_EXTENSIBLE_MATCHING_NOT_SUPPORTED("Extensible matching is not supported when attempting to determine whether a given entry matches a search filter."),



  /**
   * Unable to decode the provided extensible match filter because the dnAttributes flag element could not be decoded as a Boolean:  {0}
   */
  ERR_FILTER_EXTMATCH_DNATTRS_NOT_BOOLEAN("Unable to decode the provided extensible match filter because the dnAttributes flag element could not be decoded as a Boolean:  {0}"),



  /**
   * Unable to decode the provided extensible match filter because the filter sequence contained an element with an invalid type ({0}).
   */
  ERR_FILTER_EXTMATCH_INVALID_TYPE("Unable to decode the provided extensible match filter because the filter sequence contained an element with an invalid type ({0})."),



  /**
   * Unable to decode the provided extensible match filter because it included multiple attribute name elements.
   */
  ERR_FILTER_EXTMATCH_MULTIPLE_ATTRS("Unable to decode the provided extensible match filter because it included multiple attribute name elements."),



  /**
   * Unable to decode the provided extensible match filter because it included multiple dnAttributes elements.
   */
  ERR_FILTER_EXTMATCH_MULTIPLE_DNATTRS("Unable to decode the provided extensible match filter because it included multiple dnAttributes elements."),



  /**
   * Unable to decode the provided extensible match filter because it included multiple matching rule ID elements.
   */
  ERR_FILTER_EXTMATCH_MULTIPLE_MRIDS("Unable to decode the provided extensible match filter because it included multiple matching rule ID elements."),



  /**
   * Unable to decode the provided extensible match filter because it included multiple match value elements.
   */
  ERR_FILTER_EXTMATCH_MULTIPLE_VALUES("Unable to decode the provided extensible match filter because it included multiple match value elements."),



  /**
   * Unable to decode the provided extensible match filter because it did not contain either an attribute name or a matching rule ID, but at least one of them must be given.
   */
  ERR_FILTER_EXTMATCH_NO_ATTR_OR_MRID("Unable to decode the provided extensible match filter because it did not contain either an attribute name or a matching rule ID, but at least one of them must be given."),



  /**
   * Unable to decode the provided extensible match filter because it did not contain a match value, which is a required element.
   */
  ERR_FILTER_EXTMATCH_NO_VALUE("Unable to decode the provided extensible match filter because it did not contain a match value, which is a required element."),



  /**
   * Invalid number of elements in the attribute value assertion sequence (expected 2, got {0,number,0}).
   */
  ERR_FILTER_INVALID_AVA_ELEMENT_COUNT("Invalid number of elements in the attribute value assertion sequence (expected 2, got {0,number,0})."),



  /**
   * Invalid escaped character ''{0}'' encountered at the end of the filter string.  Expected two hex digits, but only found one.
   */
  ERR_FILTER_INVALID_ESCAPED_END_CHAR("Invalid escaped character ''{0}'' encountered at the end of the filter string.  Expected two hex digits, but only found one."),



  /**
   * Invalid hex character ''{0}'' encountered at position {1,number,0}.
   */
  ERR_FILTER_INVALID_HEX_CHAR("Invalid hex character ''{0}'' encountered at position {1,number,0}."),



  /**
   * Unable to decode the value of the filter element as a substring filter because the filter sequence had an invalid number of elements (expected 2, got {0,number,0}).
   */
  ERR_FILTER_INVALID_SUBSTR_ASSERTION_COUNT("Unable to decode the value of the filter element as a substring filter because the filter sequence had an invalid number of elements (expected 2, got {0,number,0})."),



  /**
   * Invalid substring component type {0}.
   */
  ERR_FILTER_INVALID_SUBSTR_TYPE("Invalid substring component type {0}."),



  /**
   * Invalid filter type {0}.
   */
  ERR_FILTER_INVALID_TYPE("Invalid filter type {0}."),



  /**
   * Mismatched number of parentheses encountered in the provided search filter string between positions {0,number,0} and {1,number,0}.
   */
  ERR_FILTER_MISMATCHED_PARENS("Mismatched number of parentheses encountered in the provided search filter string between positions {0,number,0} and {1,number,0}."),



  /**
   * Expected parentheses around the filter component ''{0}''.
   */
  ERR_FILTER_MISSING_PARENTHESES("Expected parentheses around the filter component ''{0}''."),



  /**
   * The substring filter included multiple subFinal elements.
   */
  ERR_FILTER_MULTIPLE_SUBFINAL("The substring filter included multiple subFinal elements."),



  /**
   * The substring filter included multiple subInitial elements.
   */
  ERR_FILTER_MULTIPLE_SUBINITIAL("The substring filter included multiple subInitial elements."),



  /**
   * Unable to parse an extensible match filter component starting at position {0,number,0} because the end of the component was reached before a colon was found to mark the end of the matching rule ID.
   */
  ERR_FILTER_NO_COLON_AFTER_MRID("Unable to parse an extensible match filter component starting at position {0,number,0} because the end of the component was reached before a colon was found to mark the end of the matching rule ID."),



  /**
   * Encountered the end of the filter component starting at position {0,number,0} without finding an equal sign to indicate the start of the assertion value.
   */
  ERR_FILTER_NO_EQUALS("Encountered the end of the filter component starting at position {0,number,0} without finding an equal sign to indicate the start of the assertion value."),



  /**
   * Unable to parse an extensible match filter component starting at position {0,number,0} because the colon following the matching rule ID was not followed by an equal sign.
   */
  ERR_FILTER_NO_EQUAL_AFTER_MRID("Unable to parse an extensible match filter component starting at position {0,number,0} because the colon following the matching rule ID was not followed by an equal sign."),



  /**
   * Opening parenthesis found at position {0,number,0} without the expected closing parenthesis at position {1,number,0}.
   */
  ERR_FILTER_OPEN_WITHOUT_CLOSE("Opening parenthesis found at position {0,number,0} without the expected closing parenthesis at position {1,number,0}."),



  /**
   * The provided search filter string was nested too deeply to be parsed.
   */
  ERR_FILTER_TOO_DEEP("The provided search filter string was nested too deeply to be parsed."),



  /**
   * The provided filter string is too short to be a valid search filter.
   */
  ERR_FILTER_TOO_SHORT("The provided filter string is too short to be a valid search filter."),



  /**
   * Unexpected unescaped asterisk encountered in the non-presence, non-substring filter component starting at position {0,number,0}.
   */
  ERR_FILTER_UNEXPECTED_ASTERISK("Unexpected unescaped asterisk encountered in the non-presence, non-substring filter component starting at position {0,number,0}."),



  /**
   * Unexpected character following the greater than sign in the filter component starting at position {0,number,0} (expected ''='', got ''{1}'').
   */
  ERR_FILTER_UNEXPECTED_CHAR_AFTER_GT("Unexpected character following the greater than sign in the filter component starting at position {0,number,0} (expected ''='', got ''{1}'')."),



  /**
   * Unexpected character following the less than sign in the filter component starting at position {0,number,0} (expected ''='', got ''{1}'').
   */
  ERR_FILTER_UNEXPECTED_CHAR_AFTER_LT("Unexpected character following the less than sign in the filter component starting at position {0,number,0} (expected ''='', got ''{1}'')."),



  /**
   * Unexpected character {0} after the colon following the matching rule ID in the extensible match filter starting at position {1,number,0}.
   */
  ERR_FILTER_UNEXPECTED_CHAR_AFTER_MRID("Unexpected character {0} after the colon following the matching rule ID in the extensible match filter starting at position {1,number,0}."),



  /**
   * Unexpected character following the tilde in the filter component starting at position {0,number,0} (expected ''='', got ''{1}'').
   */
  ERR_FILTER_UNEXPECTED_CHAR_AFTER_TILDE("Unexpected character following the tilde in the filter component starting at position {0,number,0} (expected ''='', got ''{1}'')."),



  /**
   * Unexpected character ''{0}'' found as the only character in the assertion value in the filter component starting at position {1,number,0}.
   */
  ERR_FILTER_UNEXPECTED_CHAR_IN_AV("Unexpected character ''{0}'' found as the only character in the assertion value in the filter component starting at position {1,number,0}."),



  /**
   * Unexpected closing parenthesis found at position {0,number,0} of the filter string.
   */
  ERR_FILTER_UNEXPECTED_CLOSE_PAREN("Unexpected closing parenthesis found at position {0,number,0} of the filter string."),



  /**
   * Unexpected double asterisk encountered in the substring filter component starting at position {0,number,0}.
   */
  ERR_FILTER_UNEXPECTED_DOUBLE_ASTERISK("Unexpected double asterisk encountered in the substring filter component starting at position {0,number,0}."),



  /**
   * Unexpected opening parenthesis found at position {0,number,0} of the filter string.
   */
  ERR_FILTER_UNEXPECTED_OPEN_PAREN("Unexpected opening parenthesis found at position {0,number,0} of the filter string."),



  /**
   * Unable to retrieve entry {0} in the course of performing the health check:  {1}
   */
  ERR_GET_ENTRY_HEALTH_CHECK_FAILURE("Unable to retrieve entry {0} in the course of performing the health check:  {1}"),



  /**
   * No entry was returned from the search.
   */
  ERR_GET_ENTRY_HEALTH_CHECK_NO_ENTRY_RETURNED("No entry was returned from the search."),



  /**
   * The GSSAPI authentication attempt failed:  {0}
   */
  ERR_GSSAPI_AUTHENTICATION_FAILED("The GSSAPI authentication attempt failed:  {0}"),



  /**
   * An error occurred while attempting to create the JAAS configuration file to use for GSSAPI authentication:  {0}
   */
  ERR_GSSAPI_CANNOT_CREATE_JAAS_CONFIG("An error occurred while attempting to create the JAAS configuration file to use for GSSAPI authentication:  {0}"),



  /**
   * Unable to create a GSSAPI SASL client:  {0}
   */
  ERR_GSSAPI_CANNOT_CREATE_SASL_CLIENT("Unable to create a GSSAPI SASL client:  {0}"),



  /**
   * An error occurred while attempting to initialize the JAAS login context for GSSAPI authentication:  {0}
   */
  ERR_GSSAPI_CANNOT_INITIALIZE_JAAS_CONTEXT("An error occurred while attempting to initialize the JAAS login context for GSSAPI authentication:  {0}"),



  /**
   * The same GSSAPI bind request object cannot be used by multiple threads attempting to authenticate at the same time.
   */
  ERR_GSSAPI_MULTIPLE_CONCURRENT_REQUESTS("The same GSSAPI bind request object cannot be used by multiple threads attempting to authenticate at the same time."),



  /**
   * No password was provided for the GSSAPI bind request, and no existing Kerberos session is available.
   */
  ERR_GSSAPI_NO_PASSWORD_AVAILABLE("No password was provided for the GSSAPI bind request, and no existing Kerberos session is available."),



  /**
   * No supported JAAS module could be identified for the current VM.
   */
  ERR_GSSAPI_NO_SUPPORTED_JAAS_MODULE("No supported JAAS module could be identified for the current VM."),



  /**
   * Unable to read or decode an intermediate response:  {0}
   */
  ERR_INTERMEDIATE_RESPONSE_CANNOT_DECODE("Unable to read or decode an intermediate response:  {0}"),



  /**
   * Empty attribute name found in the attribute list.
   */
  ERR_LDAPURL_ATTRLIST_EMPTY_ATTRIBUTE("Empty attribute name found in the attribute list."),



  /**
   * The attribute list ended with a comma.
   */
  ERR_LDAPURL_ATTRLIST_ENDS_WITH_COMMA("The attribute list ended with a comma."),



  /**
   * Unable to percent-decode string ''{0}'' from the URL because it included a percent character that was not followed by two hexadecimal digits.
   */
  ERR_LDAPURL_HEX_STRING_TOO_SHORT("Unable to percent-decode string ''{0}'' from the URL because it included a percent character that was not followed by two hexadecimal digits."),



  /**
   * Invalid non-hexadecimal character ''{0}'' found following a percent sign in the URL string.
   */
  ERR_LDAPURL_INVALID_HEX_CHAR("Invalid non-hexadecimal character ''{0}'' found following a percent sign in the URL string."),



  /**
   * Invalid port value {0,number,0} found in the LDAP URL (port values must be between 1 and 65535).
   */
  ERR_LDAPURL_INVALID_PORT("Invalid port value {0,number,0} found in the LDAP URL (port values must be between 1 and 65535)."),



  /**
   * The provided URL string cannot be parsed as an LDAP URL because it had an invalid scheme of ''{0}''.  The only accepted scheme values are ''ldap'' and ''ldaps''.
   */
  ERR_LDAPURL_INVALID_SCHEME("The provided URL string cannot be parsed as an LDAP URL because it had an invalid scheme of ''{0}''.  The only accepted scheme values are ''ldap'' and ''ldaps''."),



  /**
   * Invalid scope string ''{0}''.
   */
  ERR_LDAPURL_INVALID_SCOPE("Invalid scope string ''{0}''."),



  /**
   * Invalid scope value ''{0}''.
   */
  ERR_LDAPURL_INVALID_SCOPE_VALUE("Invalid scope value ''{0}''."),



  /**
   * An IPv6 literal address cannot be a zero-length string.
   */
  ERR_LDAPURL_IPV6_HOST_EMPTY("An IPv6 literal address cannot be a zero-length string."),



  /**
   * The host/port element of the LDAP URL started with a square bracket to begin an IPv6 address, but there was no closing bracket to end the address.
   */
  ERR_LDAPURL_IPV6_HOST_MISSING_BRACKET("The host/port element of the LDAP URL started with a square bracket to begin an IPv6 address, but there was no closing bracket to end the address."),



  /**
   * Unexpected character ''{0}'' found after the bracket-enclosed address.
   */
  ERR_LDAPURL_IPV6_HOST_UNEXPECTED_CHAR("Unexpected character ''{0}'' found after the bracket-enclosed address."),



  /**
   * The provided URL string cannot be parsed as an LDAP URL because it does not include a ''://'' to separate the scheme name from the rest of the URL.
   */
  ERR_LDAPURL_NO_COLON_SLASHES("The provided URL string cannot be parsed as an LDAP URL because it does not include a ''://'' to separate the scheme name from the rest of the URL."),



  /**
   * Unable to decode ''{0}'' as an address:port because the value after the colon could not be parsed as an integer.
   */
  ERR_LDAPURL_PORT_NOT_INT("Unable to decode ''{0}'' as an address:port because the value after the colon could not be parsed as an integer."),



  /**
   * The provided search request has a search result listener.  The search request provided to an LDAP entry source must not have a search result listener associated with it.
   */
  ERR_LDAP_ENTRY_SOURCE_REQUEST_HAS_LISTENER("The provided search request has a search result listener.  The search request provided to an LDAP entry source must not have a search result listener associated with it."),



  /**
   * Modify DN processing was interrupted while waiting for a response from server {0}.
   */
  ERR_MODDN_INTERRUPTED("Modify DN processing was interrupted while waiting for a response from server {0}."),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for a modify response from server {1}.
   */
  ERR_MODIFY_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for a modify response from server {1}."),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for a modify DN response from server {1}.
   */
  ERR_MODIFY_DN_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for a modify DN response from server {1}."),



  /**
   * Modify processing was interrupted while waiting for a response from server {0}.
   */
  ERR_MODIFY_INTERRUPTED("Modify processing was interrupted while waiting for a response from server {0}."),



  /**
   * The provided LDIF lines did not represent a modify change record.
   */
  ERR_MODIFY_INVALID_LDIF("The provided LDIF lines did not represent a modify change record."),



  /**
   * Unable to read or decode a modification:  {0}
   */
  ERR_MOD_CANNOT_DECODE("Unable to read or decode a modification:  {0}"),



  /**
   * Unable to decode the attribute element of the modification as a sequence:  {0}
   */
  ERR_MOD_DECODE_CANNOT_PARSE_ATTR("Unable to decode the attribute element of the modification as a sequence:  {0}"),



  /**
   * Unable to decode the attribute value set from the modification sequence:  {0}
   */
  ERR_MOD_DECODE_CANNOT_PARSE_ATTR_VALUE_SET("Unable to decode the attribute value set from the modification sequence:  {0}"),



  /**
   * Unable to decode the modification type element as an enumerated element:  {0}
   */
  ERR_MOD_DECODE_CANNOT_PARSE_MOD_TYPE("Unable to decode the modification type element as an enumerated element:  {0}"),



  /**
   * An LDAP modification sequence must have exactly two elements in the attribute sequence, but the provided sequence had {0,number,0}.
   */
  ERR_MOD_DECODE_INVALID_ATTR_ELEMENT_COUNT("An LDAP modification sequence must have exactly two elements in the attribute sequence, but the provided sequence had {0,number,0}."),



  /**
   * An LDAP modification sequence must have exactly two elements but the provided sequence had {0,number,0}.
   */
  ERR_MOD_DECODE_INVALID_ELEMENT_COUNT("An LDAP modification sequence must have exactly two elements but the provided sequence had {0,number,0}."),



  /**
   * This connection pool has been closed.
   */
  ERR_POOL_CLOSED("This connection pool has been closed."),



  /**
   * Unable to establish a connection for use in the connection pool:  {0}
   */
  ERR_POOL_CONNECT_ERROR("Unable to establish a connection for use in the connection pool:  {0}"),



  /**
   * The provided connection is not established.
   */
  ERR_POOL_CONN_NOT_ESTABLISHED("The provided connection is not established."),



  /**
   * An attempt to read from a connection during health check processing indicated that the connection has been closed.
   */
  ERR_POOL_HEALTH_CHECK_CONN_CLOSED("An attempt to read from a connection during health check processing indicated that the connection has been closed."),



  /**
   * The connection is no longer valid after an exception encountered during processing:  {0}
   */
  ERR_POOL_HEALTH_CHECK_CONN_INVALID_AFTER_EXCEPTION("The connection is no longer valid after an exception encountered during processing:  {0}"),



  /**
   * An unexpected error occurred while attempting to read from a connection during health check processing:  {0}
   */
  ERR_POOL_HEALTH_CHECK_READ_FAILURE("An unexpected error occurred while attempting to read from a connection during health check processing:  {0}"),



  /**
   * No connections are currently available in the connection pool.
   */
  ERR_POOL_NO_CONNECTIONS("No connections are currently available in the connection pool."),



  /**
   * An unexpected error occurred while processing the operation:  {0}
   */
  ERR_POOL_OP_EXCEPTION("An unexpected error occurred while processing the operation:  {0}"),



  /**
   * An error occurred while performing post-connect processing on a connection created for use in a connection pool:  {0}
   */
  ERR_POOL_POST_CONNECT_ERROR("An error occurred while performing post-connect processing on a connection created for use in a connection pool:  {0}"),



  /**
   * The StartTLS operation cannot be processed on a connection that is part of a connection pool.
   */
  ERR_POOL_STARTTLS_NOT_ALLOWED("The StartTLS operation cannot be processed on a connection that is part of a connection pool."),



  /**
   * Unexpected character {0} found at position {1,number,0} outside the quotes around the RDN value.
   */
  ERR_RDN_CHAR_OUTSIDE_QUOTES("Unexpected character {0} found at position {1,number,0} outside the quotes around the RDN value."),



  /**
   * The RDN component does not have a non-empty value.
   */
  ERR_RDN_EMPTY_VALUE("The RDN component does not have a non-empty value."),



  /**
   * Unexpected backslash found at the end of the RDN string.
   */
  ERR_RDN_ENDS_WITH_BACKSLASH("Unexpected backslash found at the end of the RDN string."),



  /**
   * Invalid character {0} found at position {1,number,0} in hex string.
   */
  ERR_RDN_INVALID_HEX_CHAR("Invalid character {0} found at position {1,number,0} in hex string."),



  /**
   * There were an odd number of characters in the hex string.
   */
  ERR_RDN_MISSING_HEX_CHAR("There were an odd number of characters in the hex string."),



  /**
   * The provided RDN string does not contain an attribute name.
   */
  ERR_RDN_NO_ATTR_NAME("The provided RDN string does not contain an attribute name."),



  /**
   * The provided RDN string does not have a value for attribute {0}.
   */
  ERR_RDN_NO_ATTR_VALUE("The provided RDN string does not have a value for attribute {0}."),



  /**
   * The provided string could not be decoded as an RDN because no equal sign was found after attribute ''{0}''.
   */
  ERR_RDN_NO_EQUAL_SIGN("The provided string could not be decoded as an RDN because no equal sign was found after attribute ''{0}''."),



  /**
   * The provided RDN string ends with a plus sign that is not followed by a name-value pair.
   */
  ERR_RDN_PLUS_NOT_FOLLOWED_BY_AVP("The provided RDN string ends with a plus sign that is not followed by a name-value pair."),



  /**
   * Unclosed double quote in RDN value.
   */
  ERR_RDN_UNCLOSED_DOUBLE_QUOTE("Unclosed double quote in RDN value."),



  /**
   * Unexpected unescaped double quote found at position {0,number,0} in the RDN string.
   */
  ERR_RDN_UNEXPECTED_DOUBLE_QUOTE("Unexpected unescaped double quote found at position {0,number,0} in the RDN string."),



  /**
   * The provided RDN string has a value which is followed by a character that is not a plus sign.
   */
  ERR_RDN_VALUE_NOT_FOLLOWED_BY_PLUS("The provided RDN string has a value which is followed by a character that is not a plus sign."),



  /**
   * Discarding response LDAP message {0} from server {1} because an error occurred while passing the message to the corresponding acceptor:  {2}
   */
  ERR_READER_ACCEPTOR_ERROR("Discarding response LDAP message {0} from server {1} because an error occurred while passing the message to the corresponding acceptor:  {2}"),



  /**
   * An error occurred while attempting to set an SO_TIMEOUT value of {0,number0}ms for LDAP connection {1}:  {2}
   */
  ERR_READER_CANNOT_SET_SO_TIMEOUT("An error occurred while attempting to set an SO_TIMEOUT value of {0,number0}ms for LDAP connection {1}:  {2}"),



  /**
   * Terminating the connection to server {0} because data read from the server could not be parsed as an ASN.1 element:  {1}
   */
  ERR_READER_CLOSING_DUE_TO_ASN1_EXCEPTION("Terminating the connection to server {0} because data read from the server could not be parsed as an ASN.1 element:  {1}"),



  /**
   * Terminating the connection to server {0} because an unexpected error occurred during processing:  {1}
   */
  ERR_READER_CLOSING_DUE_TO_EXCEPTION("Terminating the connection to server {0} because an unexpected error occurred during processing:  {1}"),



  /**
   * Terminating the connection to server {0} because an attempted I/O operation was interrupted when the connection was discovered to have been closed by the server.
   */
  ERR_READER_CLOSING_DUE_TO_INTERRUPTED_IO("Terminating the connection to server {0} because an attempted I/O operation was interrupted when the connection was discovered to have been closed by the server."),



  /**
   * Terminating the connection to server {0} because an I/O problem occurred while trying to read data:  {1}
   */
  ERR_READER_CLOSING_DUE_TO_IO_EXCEPTION("Terminating the connection to server {0} because an I/O problem occurred while trying to read data:  {1}"),



  /**
   * Unable to read or decode an LDAP result:  {0}
   */
  ERR_RESULT_CANNOT_DECODE("Unable to read or decode an LDAP result:  {0}"),



  /**
   * Unable to create the initial {0} SASL request:  {1}
   */
  ERR_SASL_CANNOT_CREATE_INITIAL_REQUEST("Unable to create the initial {0} SASL request:  {1}"),



  /**
   * Unable to create a subsequent {0} SASL request:  {1}
   */
  ERR_SASL_CANNOT_CREATE_SUBSEQUENT_REQUEST("Unable to create a subsequent {0} SASL request:  {1}"),



  /**
   * A client-side timeout was encountered while waiting {0,number,0}ms for a search response from server {1}.
   */
  ERR_SEARCH_CLIENT_TIMEOUT("A client-side timeout was encountered while waiting {0,number,0}ms for a search response from server {1}."),



  /**
   * Unable to read or decode a search result entry:  {0}
   */
  ERR_SEARCH_ENTRY_CANNOT_DECODE("Unable to read or decode a search result entry:  {0}"),



  /**
   * The protocol op contained in the provided LDAP message could not be decoded as a search result entry sequence:  {0}
   */
  ERR_SEARCH_ENTRY_ELEMENT_NOT_SEQUENCE("The protocol op contained in the provided LDAP message could not be decoded as a search result entry sequence:  {0}"),



  /**
   * Search processing was interrupted while waiting for a response from server {0}.
   */
  ERR_SEARCH_INTERRUPTED("Search processing was interrupted while waiting for a response from server {0}."),



  /**
   * Unable to read or decode a search result reference:  {0}
   */
  ERR_SEARCH_REFERENCE_CANNOT_DECODE("Unable to read or decode a search result reference:  {0}"),



  /**
   * Simple bind operations are not allowed to contain a bind DN without a password.
   */
  ERR_SIMPLE_BIND_DN_WITHOUT_PASSWORD("Simple bind operations are not allowed to contain a bind DN without a password."),



  /**
   * The SimpleBindRequest.encodeProtocolOp method may only be called for bind requests created with a static password.  It may not be used for bind requests created with a password provider.
   */
  ERR_SIMPLE_BIND_ENCODE_PROTOCOL_OP_WITH_PROVIDER("The SimpleBindRequest.encodeProtocolOp method may only be called for bind requests created with a static password.  It may not be used for bind requests created with a password provider."),



  /**
   * Unable to parse string ''{0}'' to retrieve information about a DNS SRV record:  {1}
   */
  ERR_SRV_RECORD_MALFORMED_STRING("Unable to parse string ''{0}'' to retrieve information about a DNS SRV record:  {1}"),



  /**
   * An error occurred while attempting to query DNS in order to retrieve SRV records with name ''{0}'':  {1}
   */
  ERR_SRV_RECORD_SET_ERROR_QUERYING_DNS("An error occurred while attempting to query DNS in order to retrieve SRV records with name ''{0}'':  {1}"),



  /**
   * No DNS SRV records were found with record name ''{0}''.
   */
  ERR_SRV_RECORD_SET_NO_RECORDS("No DNS SRV records were found with record name ''{0}''."),



  /**
   * Too many referrals were encountered while attempting to process the operation.
   */
  ERR_TOO_MANY_REFERRALS("Too many referrals were encountered while attempting to process the operation."),



  /**
   * The asynchronous operation encountered a client-side timeout after waiting {0,number,0} milliseconds for a response to arrive.
   */
  INFO_ASYNC_OPERATION_TIMEOUT_WITHOUT_ABANDON("The asynchronous operation encountered a client-side timeout after waiting {0,number,0} milliseconds for a response to arrive."),



  /**
   * The asynchronous operation encountered a client-side timeout after waiting {0,number,0} milliseconds for a response to arrive.  The operation will be abandoned.
   */
  INFO_ASYNC_OPERATION_TIMEOUT_WITH_ABANDON("The asynchronous operation encountered a client-side timeout after waiting {0,number,0} milliseconds for a response to arrive.  The operation will be abandoned."),



  /**
   * An abandon request was sent for the operation through the AsyncRequestID.cancel() method.
   */
  INFO_ASYNC_REQUEST_USER_CANCELED("An abandon request was sent for the operation through the AsyncRequestID.cancel() method."),



  /**
   * The client closed the connection because a bind operation performed as part of creating the connection did not complete successfully.
   */
  INFO_DISCONNECT_TYPE_BIND_FAILED("The client closed the connection because a bind operation performed as part of creating the connection did not complete successfully."),



  /**
   * The connection was closed by a finalizer in the LDAP SDK, which indicates that it was not properly closed by the application that was using the connection.
   */
  INFO_DISCONNECT_TYPE_CLOSED_BY_FINALIZER("The connection was closed by a finalizer in the LDAP SDK, which indicates that it was not properly closed by the application that was using the connection."),



  /**
   * The connection was closed because an error occurred while attempting to decode data from the server.
   */
  INFO_DISCONNECT_TYPE_DECODE_ERROR("The connection was closed because an error occurred while attempting to decode data from the server."),



  /**
   * The connection was closed because an I/O problem was encountered while attempting to communicate with the server.
   */
  INFO_DISCONNECT_TYPE_IO_ERROR("The connection was closed because an I/O problem was encountered while attempting to communicate with the server."),



  /**
   * The connection was closed because of an unexpected error encountered within the LDAP SDK.
   */
  INFO_DISCONNECT_TYPE_LOCAL_ERROR("The connection was closed because of an unexpected error encountered within the LDAP SDK."),



  /**
   * The connection was closed for a reason that is not suitable for any other disconnect type.
   */
  INFO_DISCONNECT_TYPE_OTHER("The connection was closed for a reason that is not suitable for any other disconnect type."),



  /**
   * The connection was closed because it was part of a connection pool and had been classified as defunct.
   */
  INFO_DISCONNECT_TYPE_POOLED_CONNECTION_DEFUNCT("The connection was closed because it was part of a connection pool and had been classified as defunct."),



  /**
   * The connection was closed because it was part of a connection pool and had been established for longer than the pool's maximum connection age.
   */
  INFO_DISCONNECT_TYPE_POOLED_CONNECTION_EXPIRED("The connection was closed because it was part of a connection pool and had been established for longer than the pool's maximum connection age."),



  /**
   * The connection was closed because it was part of a connection pool and was no longer needed.
   */
  INFO_DISCONNECT_TYPE_POOLED_CONNECTION_UNNEEDED("The connection was closed because it was part of a connection pool and was no longer needed."),



  /**
   * The connection was closed because it was part of a connection pool that was closed.
   */
  INFO_DISCONNECT_TYPE_POOL_CLOSED("The connection was closed because it was part of a connection pool that was closed."),



  /**
   * The connection was closed because it was part of a connection pool and a failure occurred while attempting to create another connection for use as part of the pool.
   */
  INFO_DISCONNECT_TYPE_POOL_CREATION_FAILURE("The connection was closed because it was part of a connection pool and a failure occurred while attempting to create another connection for use as part of the pool."),



  /**
   * The client closed the connection because it was going to reconnect the existing connection.
   */
  INFO_DISCONNECT_TYPE_RECONNECT("The client closed the connection because it was going to reconnect the existing connection."),



  /**
   * The client closed the connection because it was a temporary connection created for following a referral and was no longer needed.
   */
  INFO_DISCONNECT_TYPE_REFERRAL("The client closed the connection because it was a temporary connection created for following a referral and was no longer needed."),



  /**
   * The connection was closed because an error was encountered while attempting to negotiate a security layer with the server.
   */
  INFO_DISCONNECT_TYPE_SECURITY_PROBLEM("The connection was closed because an error was encountered while attempting to negotiate a security layer with the server."),



  /**
   * The server closed the connection without prior notice.
   */
  INFO_DISCONNECT_TYPE_SERVER_CLOSED_WITHOUT_NOTICE("The server closed the connection without prior notice."),



  /**
   * The server closed the connection with a notice of disconnection unsolicited notification.
   */
  INFO_DISCONNECT_TYPE_SERVER_CLOSED_WITH_NOTICE("The server closed the connection with a notice of disconnection unsolicited notification."),



  /**
   * The client closed the connection with an unbind request.
   */
  INFO_DISCONNECT_TYPE_UNBIND("The client closed the connection with an unbind request."),



  /**
   * The reason for the disconnect is not known to the LDAP SDK.
   */
  INFO_DISCONNECT_TYPE_UNKNOWN("The reason for the disconnect is not known to the LDAP SDK."),



  /**
   * admin limit exceeded
   */
  INFO_RC_ADMIN_LIMIT_EXCEEDED("admin limit exceeded"),



  /**
   * affects multiple DSAs
   */
  INFO_RC_AFFECTS_MULTIPLE_DSAS("affects multiple DSAs"),



  /**
   * alias dereferencing problem
   */
  INFO_RC_ALIAS_DEREFERENCING_PROBLEM("alias dereferencing problem"),



  /**
   * alias problem
   */
  INFO_RC_ALIAS_PROBLEM("alias problem"),



  /**
   * assertion failed
   */
  INFO_RC_ASSERTION_FAILED("assertion failed"),



  /**
   * attribute or value exists
   */
  INFO_RC_ATTRIBUTE_OR_VALUE_EXISTS("attribute or value exists"),



  /**
   * authorization denied
   */
  INFO_RC_AUTHORIZATION_DENIED("authorization denied"),



  /**
   * auth method not supported
   */
  INFO_RC_AUTH_METHOD_NOT_SUPPORTED("auth method not supported"),



  /**
   * auth unknown
   */
  INFO_RC_AUTH_UNKNOWN("auth unknown"),



  /**
   * busy
   */
  INFO_RC_BUSY("busy"),



  /**
   * canceled
   */
  INFO_RC_CANCELED("canceled"),



  /**
   * cannot cancel
   */
  INFO_RC_CANNOT_CANCEL("cannot cancel"),



  /**
   * client loop
   */
  INFO_RC_CLIENT_LOOP("client loop"),



  /**
   * compare false
   */
  INFO_RC_COMPARE_FALSE("compare false"),



  /**
   * compare true
   */
  INFO_RC_COMPARE_TRUE("compare true"),



  /**
   * confidentiality required
   */
  INFO_RC_CONFIDENTIALITY_REQUIRED("confidentiality required"),



  /**
   * connect error
   */
  INFO_RC_CONNECT_ERROR("connect error"),



  /**
   * constraint violation
   */
  INFO_RC_CONSTRAINT_VIOLATION("constraint violation"),



  /**
   * control not found
   */
  INFO_RC_CONTROL_NOT_FOUND("control not found"),



  /**
   * decoding error
   */
  INFO_RC_DECODING_ERROR("decoding error"),



  /**
   * encoding error
   */
  INFO_RC_ENCODING_ERROR("encoding error"),



  /**
   * entry already exists
   */
  INFO_RC_ENTRY_ALREADY_EXISTS("entry already exists"),



  /**
   * e-sync refresh required
   */
  INFO_RC_E_SYNC_REFRESH_REQUIRED("e-sync refresh required"),



  /**
   * filter error
   */
  INFO_RC_FILTER_ERROR("filter error"),



  /**
   * inappropriate authentication
   */
  INFO_RC_INAPPROPRIATE_AUTHENTICATION("inappropriate authentication"),



  /**
   * inappropriate matching
   */
  INFO_RC_INAPPROPRIATE_MATCHING("inappropriate matching"),



  /**
   * insufficient access rights
   */
  INFO_RC_INSUFFICIENT_ACCESS_RIGHTS("insufficient access rights"),



  /**
   * interactive transaction aborted
   */
  INFO_RC_INTERACTIVE_TRANSACTION_ABORTED("interactive transaction aborted"),



  /**
   * invalid attribute syntax
   */
  INFO_RC_INVALID_ATTRIBUTE_SYNTAX("invalid attribute syntax"),



  /**
   * invalid credentials
   */
  INFO_RC_INVALID_CREDENTIALS("invalid credentials"),



  /**
   * invalid DN syntax
   */
  INFO_RC_INVALID_DN_SYNTAX("invalid DN syntax"),



  /**
   * local error
   */
  INFO_RC_LOCAL_ERROR("local error"),



  /**
   * loop detect
   */
  INFO_RC_LOOP_DETECT("loop detect"),



  /**
   * more results to return
   */
  INFO_RC_MORE_RESULTS_TO_RETURN("more results to return"),



  /**
   * naming violation
   */
  INFO_RC_NAMING_VIOLATION("naming violation"),



  /**
   * not allowed on non-leaf
   */
  INFO_RC_NOT_ALLOWED_ON_NONLEAF("not allowed on non-leaf"),



  /**
   * not allowed on RDN
   */
  INFO_RC_NOT_ALLOWED_ON_RDN("not allowed on RDN"),



  /**
   * not supported
   */
  INFO_RC_NOT_SUPPORTED("not supported"),



  /**
   * no memory
   */
  INFO_RC_NO_MEMORY("no memory"),



  /**
   * no operation
   */
  INFO_RC_NO_OPERATION("no operation"),



  /**
   * no results returned
   */
  INFO_RC_NO_RESULTS_RETURNED("no results returned"),



  /**
   * no such attribute
   */
  INFO_RC_NO_SUCH_ATTRIBUTE("no such attribute"),



  /**
   * no such object
   */
  INFO_RC_NO_SUCH_OBJECT("no such object"),



  /**
   * no such operation
   */
  INFO_RC_NO_SUCH_OPERATION("no such operation"),



  /**
   * object class mods prohibited
   */
  INFO_RC_OBJECT_CLASS_MODS_PROHIBITED("object class mods prohibited"),



  /**
   * object class violation
   */
  INFO_RC_OBJECT_CLASS_VIOLATION("object class violation"),



  /**
   * offset range error
   */
  INFO_RC_OFFSET_RANGE_ERROR("offset range error"),



  /**
   * operations error
   */
  INFO_RC_OPERATIONS_ERROR("operations error"),



  /**
   * other
   */
  INFO_RC_OTHER("other"),



  /**
   * parameter error
   */
  INFO_RC_PARAM_ERROR("parameter error"),



  /**
   * protocol error
   */
  INFO_RC_PROTOCOL_ERROR("protocol error"),



  /**
   * referral
   */
  INFO_RC_REFERRAL("referral"),



  /**
   * referral limit exceeded
   */
  INFO_RC_REFERRAL_LIMIT_EXCEEDED("referral limit exceeded"),



  /**
   * SASL bind in progress
   */
  INFO_RC_SASL_BIND_IN_PROGRESS("SASL bind in progress"),



  /**
   * server down
   */
  INFO_RC_SERVER_DOWN("server down"),



  /**
   * size limit exceeded
   */
  INFO_RC_SIZE_LIMIT_EXCEEDED("size limit exceeded"),



  /**
   * sort control missing
   */
  INFO_RC_SORT_CONTROL_MISSING("sort control missing"),



  /**
   * strong auth required
   */
  INFO_RC_STRONG_AUTH_REQUIRED("strong auth required"),



  /**
   * success
   */
  INFO_RC_SUCCESS("success"),



  /**
   * timeout
   */
  INFO_RC_TIMEOUT("timeout"),



  /**
   * time limit exceeded
   */
  INFO_RC_TIME_LIMIT_EXCEEDED("time limit exceeded"),



  /**
   * too late
   */
  INFO_RC_TOO_LATE("too late"),



  /**
   * unavailable
   */
  INFO_RC_UNAVAILABLE("unavailable"),



  /**
   * unavailable critical extension
   */
  INFO_RC_UNAVAILABLE_CRITICAL_EXTENSION("unavailable critical extension"),



  /**
   * undefined attribute type
   */
  INFO_RC_UNDEFINED_ATTRIBUTE_TYPE("undefined attribute type"),



  /**
   * unwilling to perform
   */
  INFO_RC_UNWILLING_TO_PERFORM("unwilling to perform"),



  /**
   * user canceled
   */
  INFO_RC_USER_CANCELED("user canceled"),



  /**
   * virtual list view error
   */
  INFO_RC_VIRTUAL_LIST_VIEW_ERR0R("virtual list view error"),



  /**
   * No response has been received from the server after a maximum wait time of {0,number,0} milliseconds.  The operation may still be in progress in the server, or it may have already been interrupted through another channel that could not be detected by the LDAP SDK.
   */
  WARN_ASYNC_REQUEST_GET_TIMEOUT("No response has been received from the server after a maximum wait time of {0,number,0} milliseconds.  The operation may still be in progress in the server, or it may have already been interrupted through another channel that could not be detected by the LDAP SDK."),



  /**
   * Received an intermediate response message ({0}) for which no listener was registered.
   */
  WARN_INTERMEDIATE_RESPONSE_WITH_NO_LISTENER("Received an intermediate response message ({0}) for which no listener was registered."),



  /**
   * Discarding response {0} because it did not have the expected message ID of {1,number,0}.
   */
  WARN_READER_DISCARDING_UNEXPECTED_RESPONSE("Discarding response {0} because it did not have the expected message ID of {1,number,0}."),



  /**
   * Received a response message {0} for which no response acceptor was registered.
   */
  WARN_READER_NO_ACCEPTOR("Received a response message {0} for which no response acceptor was registered."),



  /**
   * Discarding unsolicited notification {0} because no handler is registered with the connection.
   */
  WARN_READER_UNHANDLED_UNSOLICITED_NOTIFICATION("Discarding unsolicited notification {0} because no handler is registered with the connection.");



  /**
   * The resource bundle that will be used to load the properties file.
   */
  private static final ResourceBundle RESOURCE_BUNDLE;
  static
  {
    ResourceBundle rb = null;
    try
    {
      rb = ResourceBundle.getBundle("ldap-ldapsdk-ldap");
    } catch (Exception e) {}
    RESOURCE_BUNDLE = rb;
  }



  /**
   * The map that will be used to hold the unformatted message strings, indexed by property name.
   */
  private static final ConcurrentHashMap<LDAPMessages,String> MESSAGE_STRINGS = new ConcurrentHashMap<LDAPMessages,String>();



  /**
   * The map that will be used to hold the message format objects, indexed by property name.
   */
  private static final ConcurrentHashMap<LDAPMessages,MessageFormat> MESSAGES = new ConcurrentHashMap<LDAPMessages,MessageFormat>();



  // The default text for this message
  private final String defaultText;



  /**
   * Creates a new message key.
   */
  private LDAPMessages(final String defaultText)
  {
    this.defaultText = defaultText;
  }



  /**
   * Retrieves a localized version of the message.
   * This method should only be used for messages which do not take any arguments.
   *
   * @return  A localized version of the message.
   */
  public String get()
  {
    String s = MESSAGE_STRINGS.get(this);
    if (s == null)
    {
      if (RESOURCE_BUNDLE == null)
      {
        return defaultText;
      }
      else
      {
        try
        {
          s = RESOURCE_BUNDLE.getString(name());
        }
        catch (final Exception e)
        {
          s = defaultText;
        }
        MESSAGE_STRINGS.putIfAbsent(this, s);
      }
    }
    return s;
  }



  /**
   * Retrieves a localized version of the message.
   *
   * @param  args  The arguments to use to format the message.
   *
   * @return  A localized version of the message.
   */
  public String get(final Object... args)
  {
    MessageFormat f = MESSAGES.get(this);
    if (f == null)
    {
      if (RESOURCE_BUNDLE == null)
      {
        f = new MessageFormat(defaultText);
      }
      else
      {
        try
        {
          f = new MessageFormat(RESOURCE_BUNDLE.getString(name()));
        }
        catch (final Exception e)
        {
          f = new MessageFormat(defaultText);
        }
      }
      MESSAGES.putIfAbsent(this, f);
    }
    synchronized (f)
    {
      return f.format(args);
    }
  }



  /**
   * Retrieves a string representation of this message key.
   *
   * @return  A string representation of this message key.
   */
  @Override()
  public String toString()
  {
    return get();
  }
}

