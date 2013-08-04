
package com.hwlcn.ldap.util;



import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;


enum UtilityMessages
{
  /**
   * The aggregate input stream does not support the use of mark and reset functionality.
   */
  ERR_AGGREGATE_INPUT_STREAM_MARK_NOT_SUPPORTED("The aggregate input stream does not support the use of mark and reset functionality."),



  /**
   * The provided string cannot be parsed as an argument list because it ends with a backslash that was not immediately preceded by another backslash.  The backslash character will be considered an escape to indicate that the next character should be included as-is with no interpretation.
   */
  ERR_ARG_STRING_DANGLING_BACKSLASH("The provided string cannot be parsed as an argument list because it ends with a backslash that was not immediately preceded by another backslash.  The backslash character will be considered an escape to indicate that the next character should be included as-is with no interpretation."),



  /**
   * The provided string cannot be parsed as an argument list because it has an unmatched quote starting at or near position {0}.
   */
  ERR_ARG_STRING_UNMATCHED_QUOTE("The provided string cannot be parsed as an argument list because it has an unmatched quote starting at or near position {0}."),



  /**
   * A base32-encoded string must have a length that is a multiple of 8 bytes.
   */
  ERR_BASE32_DECODE_INVALID_LENGTH("A base32-encoded string must have a length that is a multiple of 8 bytes."),



  /**
   * Invalid character ''{0}'' encountered.
   */
  ERR_BASE32_DECODE_UNEXPECTED_CHAR("Invalid character ''{0}'' encountered."),



  /**
   * Unexpected equal sign found at position {0,number,0}.
   */
  ERR_BASE32_DECODE_UNEXPECTED_EQUAL("Unexpected equal sign found at position {0,number,0}."),



  /**
   * A base64-encoded string must have a length that is a multiple of 4 bytes.
   */
  ERR_BASE64_DECODE_INVALID_LENGTH("A base64-encoded string must have a length that is a multiple of 4 bytes."),



  /**
   * Invalid character ''{0}'' encountered.
   */
  ERR_BASE64_DECODE_UNEXPECTED_CHAR("Invalid character ''{0}'' encountered."),



  /**
   * Unexpected equal sign found at position {0,number,0}.
   */
  ERR_BASE64_DECODE_UNEXPECTED_EQUAL("Unexpected equal sign found at position {0,number,0}."),



  /**
   * The provided string did not have a valid length for base64url-encoded data.
   */
  ERR_BASE64_URLDECODE_INVALID_LENGTH("The provided string did not have a valid length for base64url-encoded data."),



  /**
   * The provided array is null.
   */
  ERR_BS_BUFFER_ARRAY_NULL("The provided array is null."),



  /**
   * The provided buffer is null.
   */
  ERR_BS_BUFFER_BUFFER_NULL("The provided buffer is null."),



  /**
   * The provided byte string is null.
   */
  ERR_BS_BUFFER_BYTE_STRING_NULL("The provided byte string is null."),



  /**
   * The provided capacity {0,number,0} is negative.
   */
  ERR_BS_BUFFER_CAPACITY_NEGATIVE("The provided capacity {0,number,0} is negative."),



  /**
   * The provided character sequence is null.
   */
  ERR_BS_BUFFER_CHAR_SEQUENCE_NULL("The provided character sequence is null."),



  /**
   * The provided length {0,number,0} is negative.
   */
  ERR_BS_BUFFER_LENGTH_NEGATIVE("The provided length {0,number,0} is negative."),



  /**
   * The provided offset {0,number,0} is negative.
   */
  ERR_BS_BUFFER_OFFSET_NEGATIVE("The provided offset {0,number,0} is negative."),



  /**
   * The provided offset {0,number,0} plus the provided length {1,number,0} is greater than the size of the provided array ({2,number,0}).
   */
  ERR_BS_BUFFER_OFFSET_PLUS_LENGTH_TOO_LARGE("The provided offset {0,number,0} plus the provided length {1,number,0} is greater than the size of the provided array ({2,number,0})."),



  /**
   * The provided position {0,number,0} is negative.
   */
  ERR_BS_BUFFER_POS_NEGATIVE("The provided position {0,number,0} is negative."),



  /**
   * The provided position {0,number,0} is greater than the length of the buffer ({1,number,0}).
   */
  ERR_BS_BUFFER_POS_TOO_LARGE("The provided position {0,number,0} is greater than the length of the buffer ({1,number,0})."),



  /**
   * A shutdown hook was invoked for command-line tool ''{0}'' but the doShutdownHookProcessing method has not been implemented for that tool.  This method must be overridden and implemented for all tools that override the registerShutdownHook method to return true.
   */
  ERR_COMMAND_LINE_TOOL_SHUTDOWN_HOOK_NOT_IMPLEMENTED("A shutdown hook was invoked for command-line tool ''{0}'' but the doShutdownHookProcessing method has not been implemented for that tool.  This method must be overridden and implemented for all tools that override the registerShutdownHook method to return true."),



  /**
   * Unable to decode bytes ''{0}'' as a valid UUID because the length of the provided content was not exactly 128 bits.
   */
  ERR_DECODE_UUID_INVALID_LENGTH("Unable to decode bytes ''{0}'' as a valid UUID because the length of the provided content was not exactly 128 bits."),



  /**
   * Unable to access data in file ''{0}'' for use in the value pattern:  {1}
   */
  ERR_FILE_VALUE_PATTERN_NOT_USABLE("Unable to access data in file ''{0}'' for use in the value pattern:  {1}"),



  /**
   * Attempted to write beyond the end of the array backing the output stream
   */
  ERR_FIXED_ARRAY_OS_WRITE_BEYOND_END("Attempted to write beyond the end of the array backing the output stream"),



  /**
   * Unable to parse the provided timestamp ''{0}'' because it had an invalid number of characters before the sub-second and/or time zone portion.
   */
  ERR_GENTIME_CANNOT_PARSE_INVALID_LENGTH("Unable to parse the provided timestamp ''{0}'' because it had an invalid number of characters before the sub-second and/or time zone portion."),



  /**
   * Unable to parse time zone information from the provided timestamp ''{0}''.
   */
  ERR_GENTIME_DECODE_CANNOT_PARSE_TZ("Unable to parse time zone information from the provided timestamp ''{0}''."),



  /**
   * Unable to access data from ''{0}'' for use in the value pattern:  {1}
   */
  ERR_HTTP_VALUE_PATTERN_NOT_USABLE("Unable to access data from ''{0}'' for use in the value pattern:  {1}"),



  /**
   * Unable to create the key manager for secure communication:  {0}
   */
  ERR_LDAP_TOOL_CANNOT_CREATE_KEY_MANAGER("Unable to create the key manager for secure communication:  {0}"),



  /**
   * Unable to create the SSL context to for StartTLS communication with the server:  {0}
   */
  ERR_LDAP_TOOL_CANNOT_CREATE_SSL_CONTEXT("Unable to create the SSL context to for StartTLS communication with the server:  {0}"),



  /**
   * Unable to create the SSL socket factory to use for secure communication with the server:  {0}
   */
  ERR_LDAP_TOOL_CANNOT_CREATE_SSL_SOCKET_FACTORY("Unable to create the SSL socket factory to use for secure communication with the server:  {0}"),



  /**
   * Unable to read the bind password:  {0}
   */
  ERR_LDAP_TOOL_CANNOT_READ_BIND_PASSWORD("Unable to read the bind password:  {0}"),



  /**
   * Unable to read the key store password:  {0}
   */
  ERR_LDAP_TOOL_CANNOT_READ_KEY_STORE_PASSWORD("Unable to read the key store password:  {0}"),



  /**
   * Unable to read the trust store password:  {0}
   */
  ERR_LDAP_TOOL_CANNOT_READ_TRUST_STORE_PASSWORD("Unable to read the trust store password:  {0}"),



  /**
   * If either the ''--{0}'' or ''--{1}'' arguments are provided multiple times, then both arguments must be provided the same number of times.
   */
  ERR_LDAP_TOOL_HOST_PORT_COUNT_MISMATCH("If either the ''--{0}'' or ''--{1}'' arguments are provided multiple times, then both arguments must be provided the same number of times."),



  /**
   * SASL option ''{0}'' cannot be used with the {1} mechanism.
   */
  ERR_LDAP_TOOL_INVALID_SASL_OPTION("SASL option ''{0}'' cannot be used with the {1} mechanism."),



  /**
   * SASL option ''{0}'' is invalid.  SASL options must be in the form ''name=value''.
   */
  ERR_LDAP_TOOL_MALFORMED_SASL_OPTION("SASL option ''{0}'' is invalid.  SASL options must be in the form ''name=value''."),



  /**
   * SASL option ''{0}'' is required for use with the {1} mechanism.
   */
  ERR_LDAP_TOOL_MISSING_REQUIRED_SASL_OPTION("SASL option ''{0}'' is required for use with the {1} mechanism."),



  /**
   * One or more SASL options were provided, but the ''mech'' option was not given to indicate which SASL mechanism to use.
   */
  ERR_LDAP_TOOL_NO_SASL_MECH("One or more SASL options were provided, but the ''mech'' option was not given to indicate which SASL mechanism to use."),



  /**
   * StartTLS negotiation failed:  {0}
   */
  ERR_LDAP_TOOL_START_TLS_FAILED("StartTLS negotiation failed:  {0}"),



  /**
   * SASL mechanism ''{0}'' is not supported.
   */
  ERR_LDAP_TOOL_UNSUPPORTED_SASL_MECH("SASL mechanism ''{0}'' is not supported."),



  /**
   * If non-null, the sets of server name prefixes and server name suffixes must not be empty.
   */
  ERR_MULTI_LDAP_TOOL_PREFIXES_AND_SUFFIXES_EMPTY("If non-null, the sets of server name prefixes and server name suffixes must not be empty."),



  /**
   * If both the sets of server name prefixes and server name suffixes are non-null, they must have the same number of elements.
   */
  ERR_MULTI_LDAP_TOOL_PREFIXES_AND_SUFFIXES_MISMATCH("If both the sets of server name prefixes and server name suffixes are non-null, they must have the same number of elements."),



  /**
   * The sets of server name prefixes and server name suffixes must not both be null.
   */
  ERR_MULTI_LDAP_TOOL_PREFIXES_AND_SUFFIXES_NULL("The sets of server name prefixes and server name suffixes must not both be null."),



  /**
   * No Exception
   */
  ERR_NO_EXCEPTION("No Exception"),



  /**
   * An error occurred while attempting to read a password from the terminal:  {0}
   */
  ERR_PW_READER_FAILURE("An error occurred while attempting to read a password from the terminal:  {0}"),



  /**
   * The provided value pattern string contained a back-reference component with an index of ''{0}'', but there are not that many components to reference at that point in the pattern.
   */
  ERR_REF_VALUE_PATTERN_INVALID_INDEX("The provided value pattern string contained a back-reference component with an index of ''{0}'', but there are not that many components to reference at that point in the pattern."),



  /**
   * The provided value pattern string contained a back-reference component with an invalid value of ''{0}''.
   */
  ERR_REF_VALUE_PATTERN_NOT_VALID("The provided value pattern string contained a back-reference component with an invalid value of ''{0}''."),



  /**
   * The provided value pattern string contained a back-reference component with an index of zero.  Back-reference indexes should start with one rather than zero.
   */
  ERR_REF_VALUE_PATTERN_ZERO_INDEX("The provided value pattern string contained a back-reference component with an index of zero.  Back-reference indexes should start with one rather than zero."),



  /**
   * SASL option ''{0}'' is required with the {1} mechanism but was not provided.
   */
  ERR_SASL_MISSING_REQUIRED_OPTION("SASL option ''{0}'' is required with the {1} mechanism but was not provided."),



  /**
   * A password must be provided for a GSSAPI bind unless useTicketCache=true and requireCache=true options are provided.
   */
  ERR_SASL_OPTION_GSSAPI_PASSWORD_REQUIRED("A password must be provided for a GSSAPI bind unless useTicketCache=true and requireCache=true options are provided."),



  /**
   * SASL option ''{0}'' must have a value of either ''true'' or ''false''.
   */
  ERR_SASL_OPTION_MALFORMED_BOOLEAN_VALUE("SASL option ''{0}'' must have a value of either ''true'' or ''false''."),



  /**
   * Conflicting SASL mechanisms ''{0}'' and ''{1}'' were specified.
   */
  ERR_SASL_OPTION_MECH_CONFLICT("Conflicting SASL mechanisms ''{0}'' and ''{1}'' were specified."),



  /**
   * A password was provided, but SASL mechanism {0} does not accept a password.
   */
  ERR_SASL_OPTION_MECH_DOESNT_ACCEPT_PASSWORD("A password was provided, but SASL mechanism {0} does not accept a password."),



  /**
   * SASL mechanism {0} requires a password but none was provided.
   */
  ERR_SASL_OPTION_MECH_REQUIRES_PASSWORD("SASL mechanism {0} requires a password but none was provided."),



  /**
   * Unable to parse string ''{0}'' as a SASL option because it is missing an equal sign to separate the name from the value.
   */
  ERR_SASL_OPTION_MISSING_EQUAL("Unable to parse string ''{0}'' as a SASL option because it is missing an equal sign to separate the name from the value."),



  /**
   * SASL option ''{0}'' was provided multiple times.
   */
  ERR_SASL_OPTION_NOT_MULTI_VALUED("SASL option ''{0}'' was provided multiple times."),



  /**
   * No SASL mechanism was specified.
   */
  ERR_SASL_OPTION_NO_MECH("No SASL mechanism was specified."),



  /**
   * Unable to parse string ''{0}'' as a SASL option because it does not have an option name before the equal sign.
   */
  ERR_SASL_OPTION_STARTS_WITH_EQUAL("Unable to parse string ''{0}'' as a SASL option because it does not have an option name before the equal sign."),



  /**
   * SASL option ''{0}'' is not supported with the {1} mechanism.
   */
  ERR_SASL_OPTION_UNSUPPORTED_FOR_MECH("SASL option ''{0}'' is not supported with the {1} mechanism."),



  /**
   * Unsupported SASL mechanism ''{0}''.
   */
  ERR_SASL_OPTION_UNSUPPORTED_MECH("Unsupported SASL mechanism ''{0}''."),



  /**
   * Entry ''{0}'' was found to contain attribute ''{1}'' when that attribute was expected to be missing.
   */
  ERR_TEST_ATTR_EXISTS("Entry ''{0}'' was found to contain attribute ''{1}'' when that attribute was expected to be missing."),



  /**
   * Entry ''{0}'' was found to contain attribute ''{1}'' with value(s) {2} when that attribute was expected to be missing.
   */
  ERR_TEST_ATTR_EXISTS_WITH_VALUES("Entry ''{0}'' was found to contain attribute ''{1}'' with value(s) {2} when that attribute was expected to be missing."),



  /**
   * Entry ''{0}'' exists but does not have any values for attribute ''{1}''.
   */
  ERR_TEST_ATTR_MISSING("Entry ''{0}'' exists but does not have any values for attribute ''{1}''."),



  /**
   * Entry ''{0}'' exists and contains attribute ''{1}'' with value(s) {2}, but does not include expected value(s) {3}.
   */
  ERR_TEST_ATTR_MISSING_VALUE("Entry ''{0}'' exists and contains attribute ''{1}'' with value(s) {2}, but does not include expected value(s) {3}."),



  /**
   * The provided DN values ''{0}'' and ''{1}'' are not equal.
   */
  ERR_TEST_DNS_NOT_EQUAL("The provided DN values ''{0}'' and ''{1}'' are not equal."),



  /**
   * Entry ''{0}'' was found to exist in the server but does not match expected filter ''{1}''.
   */
  ERR_TEST_ENTRY_DOES_NOT_MATCH_FILTER("Entry ''{0}'' was found to exist in the server but does not match expected filter ''{1}''."),



  /**
   * Entry ''{0}'' was found in the server but was expected to be missing.
   */
  ERR_TEST_ENTRY_EXISTS("Entry ''{0}'' was found in the server but was expected to be missing."),



  /**
   * Search result entry {0} was not expected to have any control with OID ''{1}'' but one was found.
   */
  ERR_TEST_ENTRY_HAS_CONTROL("Search result entry {0} was not expected to have any control with OID ''{1}'' but one was found."),



  /**
   * Entry ''{0}'' does not exist in the server.
   */
  ERR_TEST_ENTRY_MISSING("Entry ''{0}'' does not exist in the server."),



  /**
   * Search result entry {0} was expected to have at least one control with OID ''{1}'' but none was found.
   */
  ERR_TEST_ENTRY_MISSING_CONTROL("Search result entry {0} was expected to have at least one control with OID ''{1}'' but none was found."),



  /**
   * Result {0} was expected to contain a matched DN value of ''{1}'' but ''{0}'' was found instead.
   */
  ERR_TEST_MATCHED_DN_MISMATCH("Result {0} was expected to contain a matched DN value of ''{1}'' but ''{0}'' was found instead."),



  /**
   * Result {0} had one of the unacceptable result codes {1}.
   */
  ERR_TEST_MULTI_RESULT_CODE_FOUND("Result {0} had one of the unacceptable result codes {1}."),



  /**
   * Result {0} did not have any of the acceptable result codes {1}.
   */
  ERR_TEST_MULTI_RESULT_CODE_MISSING("Result {0} did not have any of the acceptable result codes {1}."),



  /**
   * Result {0} received from processing request {1} had one of the unacceptable result codes {2}.
   */
  ERR_TEST_PROCESSING_MULTI_RESULT_CODE_FOUND("Result {0} received from processing request {1} had one of the unacceptable result codes {2}."),



  /**
   * Result {0} received from processing request {1} did not have any of the acceptable result codes {2}.
   */
  ERR_TEST_PROCESSING_MULTI_RESULT_CODE_MISSING("Result {0} received from processing request {1} did not have any of the acceptable result codes {2}."),



  /**
   * Result {0} received from processing request {1} had an unacceptable result code of ''{2}''.
   */
  ERR_TEST_PROCESSING_SINGLE_RESULT_CODE_FOUND("Result {0} received from processing request {1} had an unacceptable result code of ''{2}''."),



  /**
   * Result {0} received from processing request {1} did not have the expected result code of {2}.
   */
  ERR_TEST_PROCESSING_SINGLE_RESULT_CODE_MISSING("Result {0} received from processing request {1} did not have the expected result code of {2}."),



  /**
   * Search result reference {0} was not expected to have any control with OID ''{1}'' but one was found.
   */
  ERR_TEST_REF_HAS_CONTROL("Search result reference {0} was not expected to have any control with OID ''{1}'' but one was found."),



  /**
   * Search result reference {0} was expected to have at least one control with OID ''{1}'' but none was found.
   */
  ERR_TEST_REF_MISSING_CONTROL("Search result reference {0} was expected to have at least one control with OID ''{1}'' but none was found."),



  /**
   * Result {0} was not expected to contain a matched DN but a value of ''{1}'' was found.
   */
  ERR_TEST_RESULT_CONTAINS_MATCHED_DN("Result {0} was not expected to contain a matched DN but a value of ''{1}'' was found."),



  /**
   * Result {0} was not expected to have any control with OID ''{1}'' but one was found.
   */
  ERR_TEST_RESULT_HAS_CONTROL("Result {0} was not expected to have any control with OID ''{1}'' but one was found."),



  /**
   * Result {0} was not expected to have any referral URLs but one or more were found.
   */
  ERR_TEST_RESULT_HAS_REFERRAL("Result {0} was not expected to have any referral URLs but one or more were found."),



  /**
   * Result {0} was expected to have at least one control with OID ''{1}'' but none was found.
   */
  ERR_TEST_RESULT_MISSING_CONTROL("Result {0} was expected to have at least one control with OID ''{1}'' but none was found."),



  /**
   * Result {0} was expected to contain a matched DN of ''{1}'' but none was found.
   */
  ERR_TEST_RESULT_MISSING_EXPECTED_MATCHED_DN("Result {0} was expected to contain a matched DN of ''{1}'' but none was found."),



  /**
   * Result {0} was expected to contain a matched DN but none was found.
   */
  ERR_TEST_RESULT_MISSING_MATCHED_DN("Result {0} was expected to contain a matched DN but none was found."),



  /**
   * Result {0} was expected to have at least one referral URL but did not contain any.
   */
  ERR_TEST_RESULT_MISSING_REFERRAL("Result {0} was expected to have at least one referral URL but did not contain any."),



  /**
   * The search was not expected to have returned any entries, but result {0} indicates that the number of entries returned was {1}.
   */
  ERR_TEST_SEARCH_ENTRIES_RETURNED("The search was not expected to have returned any entries, but result {0} indicates that the number of entries returned was {1}."),



  /**
   * The search was expected to have returned {0} entries, but result {1} indicates that the number of entries returned was {2}.
   */
  ERR_TEST_SEARCH_ENTRY_COUNT_MISMATCH_MULTI_EXPECTED("The search was expected to have returned {0} entries, but result {1} indicates that the number of entries returned was {2}."),



  /**
   * The search was expected to have returned one entry, but result {0} indicates that the number of entries returned was {1}.
   */
  ERR_TEST_SEARCH_ENTRY_COUNT_MISMATCH_ONE_EXPECTED("The search was expected to have returned one entry, but result {0} indicates that the number of entries returned was {1}."),



  /**
   * The search with result {0} was expected to have returned entry ''{1}'', but either was not included in the set of entries that were returned, or a search result listener was used for the search that makes it impossible to determine what entries were returned.
   */
  ERR_TEST_SEARCH_ENTRY_NOT_RETURNED("The search with result {0} was expected to have returned entry ''{1}'', but either was not included in the set of entries that were returned, or a search result listener was used for the search that makes it impossible to determine what entries were returned."),



  /**
   * The search was expected to have returned one or more entries, but result {0} indicates that none were returned.
   */
  ERR_TEST_SEARCH_NO_ENTRIES_RETURNED("The search was expected to have returned one or more entries, but result {0} indicates that none were returned."),



  /**
   * The search was expected to have returned one or more references, but result {0} indicates that none were returned.
   */
  ERR_TEST_SEARCH_NO_REFS_RETURNED("The search was expected to have returned one or more references, but result {0} indicates that none were returned."),



  /**
   * The search was not expected to have returned any references, but result {0} indicates that the number of references returned was {1}.
   */
  ERR_TEST_SEARCH_REFS_RETURNED("The search was not expected to have returned any references, but result {0} indicates that the number of references returned was {1}."),



  /**
   * The search was expected to have returned {0} references, but result {1} indicates that the number of references returned was {2}.
   */
  ERR_TEST_SEARCH_REF_COUNT_MISMATCH_MULTI_EXPECTED("The search was expected to have returned {0} references, but result {1} indicates that the number of references returned was {2}."),



  /**
   * The search was expected to have returned one reference, but result {0} indicates that the number of references returned was {1}.
   */
  ERR_TEST_SEARCH_REF_COUNT_MISMATCH_ONE_EXPECTED("The search was expected to have returned one reference, but result {0} indicates that the number of references returned was {1}."),



  /**
   * Result {0} had an unacceptable result code of ''{1}''.
   */
  ERR_TEST_SINGLE_RESULT_CODE_FOUND("Result {0} had an unacceptable result code of ''{1}''."),



  /**
   * Result {0} did not have the expected result code of ''{1}''.
   */
  ERR_TEST_SINGLE_RESULT_CODE_MISSING("Result {0} did not have the expected result code of ''{1}''."),



  /**
   * Entry ''{0}'' was found to contain attribute ''{1}'' with value ''{2}'' when that value was expected to be missing.
   */
  ERR_TEST_VALUE_EXISTS("Entry ''{0}'' was found to contain attribute ''{1}'' with value ''{2}'' when that value was expected to be missing."),



  /**
   * Entry ''{0}'' exists and contains attribute ''{1}'', but that attribute does not include value ''{2}''.
   */
  ERR_TEST_VALUE_MISSING("Entry ''{0}'' exists and contains attribute ''{1}'', but that attribute does not include value ''{2}''."),



  /**
   * The provided string value ''{0}'' does not represent a valid LDAP distinguished name:  {1}
   */
  ERR_TEST_VALUE_NOT_VALID_DN("The provided string value ''{0}'' does not represent a valid LDAP distinguished name:  {1}"),



  /**
   * {0}.  Thread stack trace:  {1}
   */
  ERR_VALIDATOR_FAILURE_CUSTOM_MESSAGE("{0}.  Thread stack trace:  {1}"),



  /**
   * A result of true was found for a condition which the LDAP SDK requires to be false.  Thread stack trace {0}
   */
  ERR_VALIDATOR_FALSE_CHECK_FAILURE("A result of true was found for a condition which the LDAP SDK requires to be false.  Thread stack trace {0}"),



  /**
   * A null object was provided where a non-null object is required (non-null index {0,number,0}).  Thread stack trace:  {1}
   */
  ERR_VALIDATOR_NULL_CHECK_FAILURE("A null object was provided where a non-null object is required (non-null index {0,number,0}).  Thread stack trace:  {1}"),



  /**
   * A result of false was found for a condition which the LDAP SDK requires to be true.  Thread stack trace {0}
   */
  ERR_VALIDATOR_TRUE_CHECK_FAILURE("A result of false was found for a condition which the LDAP SDK requires to be true.  Thread stack trace {0}"),



  /**
   * The specified file does not contain any data.
   */
  ERR_VALUE_PATTERN_COMPONENT_EMPTY_FILE("The specified file does not contain any data."),



  /**
   * The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length format string.
   */
  ERR_VALUE_PATTERN_EMPTY_FORMAT("The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length format string."),



  /**
   * The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length increment.
   */
  ERR_VALUE_PATTERN_EMPTY_INCREMENT("The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length increment."),



  /**
   * The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length lower bound.
   */
  ERR_VALUE_PATTERN_EMPTY_LOWER_BOUND("The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length lower bound."),



  /**
   * The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length upper bound.
   */
  ERR_VALUE_PATTERN_EMPTY_UPPER_BOUND("The provided value pattern string contained a numeric range starting at position {0,number,0} which contained a zero-length upper bound."),



  /**
   * The provided value pattern string contained an invalid character ''{0}'' at position {1,number,0}.
   */
  ERR_VALUE_PATTERN_INVALID_CHARACTER("The provided value pattern string contained an invalid character ''{0}'' at position {1,number,0}."),



  /**
   * The provided value pattern string contained a numeric range starting at position {0,number,0} which did not contain either a dash or colon to separate the lower bound from the upper bound.
   */
  ERR_VALUE_PATTERN_NO_DELIMITER("The provided value pattern string contained a numeric range starting at position {0,number,0} which did not contain either a dash or colon to separate the lower bound from the upper bound."),



  /**
   * The provided value pattern string contained an unmatched closing brace at position {0,number,0}.
   */
  ERR_VALUE_PATTERN_UNMATCHED_CLOSE("The provided value pattern string contained an unmatched closing brace at position {0,number,0}."),



  /**
   * The provided value pattern string contained an unmatched opening brace at position {0,number,0}.
   */
  ERR_VALUE_PATTERN_UNMATCHED_OPEN("The provided value pattern string contained an unmatched opening brace at position {0,number,0}."),



  /**
   * The provided value pattern string contained a numeric range starting at position {0,number,0} with a value that is outside the acceptable range.  Values must be between {1,number,0} and {2,number,0}.
   */
  ERR_VALUE_PATTERN_VALUE_NOT_LONG("The provided value pattern string contained a numeric range starting at position {0,number,0} with a value that is outside the acceptable range.  Values must be between {1,number,0} and {2,number,0}."),



  /**
   * Display usage information for this program.
   */
  INFO_CL_TOOL_DESCRIPTION_HELP("Display usage information for this program."),



  /**
   * Display version information for this program.
   */
  INFO_CL_TOOL_DESCRIPTION_VERSION("Display version information for this program."),



  /**
   * Examples
   */
  INFO_CL_TOOL_LABEL_EXAMPLES("Examples"),



  /**
   * Timestamp
   */
  INFO_COLUMN_LABEL_TIMESTAMP("Timestamp"),



  /**
   * The DN to use to bind to the directory server when performing simple authentication.
   */
  INFO_LDAP_TOOL_DESCRIPTION_BIND_DN("The DN to use to bind to the directory server when performing simple authentication."),



  /**
   * The password to use to bind to the directory server when performing simple authentication or a password-based SASL mechanism.
   */
  INFO_LDAP_TOOL_DESCRIPTION_BIND_PW("The password to use to bind to the directory server when performing simple authentication or a password-based SASL mechanism."),



  /**
   * The path to the file containing the password to use to bind to the directory server when performing simple authentication or a password-based SASL mechanism.
   */
  INFO_LDAP_TOOL_DESCRIPTION_BIND_PW_FILE("The path to the file containing the password to use to bind to the directory server when performing simple authentication or a password-based SASL mechanism."),



  /**
   * Indicates that the tool should interactively prompt the user for the bind password.
   */
  INFO_LDAP_TOOL_DESCRIPTION_BIND_PW_PROMPT("Indicates that the tool should interactively prompt the user for the bind password."),



  /**
   * The nickname (alias) of the client certificate in the key store to present to the directory server for SSL client authentication.
   */
  INFO_LDAP_TOOL_DESCRIPTION_CERT_NICKNAME("The nickname (alias) of the client certificate in the key store to present to the directory server for SSL client authentication."),



  /**
   * The IP address or resolvable name to use to connect to the directory server.  If this is not provided, then a default value of 'localhost' will be used.
   */
  INFO_LDAP_TOOL_DESCRIPTION_HOST("The IP address or resolvable name to use to connect to the directory server.  If this is not provided, then a default value of 'localhost' will be used."),



  /**
   * The format (e.g., jks, jceks, pkcs12, etc.) for the key store file.
   */
  INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_FORMAT("The format (e.g., jks, jceks, pkcs12, etc.) for the key store file."),



  /**
   * The password to use to access the key store contents.
   */
  INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD("The password to use to access the key store contents."),



  /**
   * The path to the file containing the password to use to access the key store contents.
   */
  INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD_FILE("The path to the file containing the password to use to access the key store contents."),



  /**
   * Indicates that the tool should interactively prompt the user for the password to use to access the key store contents.
   */
  INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PASSWORD_PROMPT("Indicates that the tool should interactively prompt the user for the password to use to access the key store contents."),



  /**
   * The path to the file to use as the key store for obtaining client certificates when communicating securely with the directory server.
   */
  INFO_LDAP_TOOL_DESCRIPTION_KEY_STORE_PATH("The path to the file to use as the key store for obtaining client certificates when communicating securely with the directory server."),



  /**
   * The port to use to connect to the directory server.  If this is not provided, then a default value of 389 will be used.
   */
  INFO_LDAP_TOOL_DESCRIPTION_PORT("The port to use to connect to the directory server.  If this is not provided, then a default value of 389 will be used."),



  /**
   * A name-value pair providing information to use when performing SASL authentication.
   */
  INFO_LDAP_TOOL_DESCRIPTION_SASL_OPTION("A name-value pair providing information to use when performing SASL authentication."),



  /**
   * Trust any certificate presented by the directory server.
   */
  INFO_LDAP_TOOL_DESCRIPTION_TRUST_ALL("Trust any certificate presented by the directory server."),



  /**
   * The format (e.g., jks, jceks, pkcs12, etc.) for the trust store file.
   */
  INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_FORMAT("The format (e.g., jks, jceks, pkcs12, etc.) for the trust store file."),



  /**
   * The password to use to access the trust store contents.
   */
  INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD("The password to use to access the trust store contents."),



  /**
   * The path to the file containing the password to use to access the trust store contents.
   */
  INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD_FILE("The path to the file containing the password to use to access the trust store contents."),



  /**
   * Indicates that the tool should interactively prompt the user for the password to use to access the trust store contents.
   */
  INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PASSWORD_PROMPT("Indicates that the tool should interactively prompt the user for the password to use to access the trust store contents."),



  /**
   * The path to the file to use as trust store when determining whether to trust a certificate presented by the directory server.
   */
  INFO_LDAP_TOOL_DESCRIPTION_TRUST_STORE_PATH("The path to the file to use as trust store when determining whether to trust a certificate presented by the directory server."),



  /**
   * Use SSL when communicating with the directory server.
   */
  INFO_LDAP_TOOL_DESCRIPTION_USE_SSL("Use SSL when communicating with the directory server."),



  /**
   * Use StartTLS when communicating with the directory server.
   */
  INFO_LDAP_TOOL_DESCRIPTION_USE_START_TLS("Use StartTLS when communicating with the directory server."),



  /**
   * Enter the bind password:
   */
  INFO_LDAP_TOOL_ENTER_BIND_PASSWORD("Enter the bind password:"),



  /**
   * Enter the key store password:
   */
  INFO_LDAP_TOOL_ENTER_KEY_STORE_PASSWORD("Enter the key store password:"),



  /**
   * Enter the trust store password:
   */
  INFO_LDAP_TOOL_ENTER_TRUST_STORE_PASSWORD("Enter the trust store password:"),



  /**
   * {nickname}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_CERT_NICKNAME("{nickname}"),



  /**
   * {dn}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_DN("{dn}"),



  /**
   * {format}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_FORMAT("{format}"),



  /**
   * {host}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_HOST("{host}"),



  /**
   * {password}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_PASSWORD("{password}"),



  /**
   * {path}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_PATH("{path}"),



  /**
   * {port}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_PORT("{port}"),



  /**
   * {name=value}
   */
  INFO_LDAP_TOOL_PLACEHOLDER_SASL_OPTION("{name=value}"),



  /**
   * {0,number,0} days
   */
  INFO_NUM_DAYS_PLURAL("{0,number,0} days"),



  /**
   * {0,number,0} day
   */
  INFO_NUM_DAYS_SINGULAR("{0,number,0} day"),



  /**
   * {0,number,0} hours
   */
  INFO_NUM_HOURS_PLURAL("{0,number,0} hours"),



  /**
   * {0,number,0} hour
   */
  INFO_NUM_HOURS_SINGULAR("{0,number,0} hour"),



  /**
   * {0,number,0} minutes
   */
  INFO_NUM_MINUTES_PLURAL("{0,number,0} minutes"),



  /**
   * {0,number,0} minute
   */
  INFO_NUM_MINUTES_SINGULAR("{0,number,0} minute"),



  /**
   * {0,number,0} seconds
   */
  INFO_NUM_SECONDS_PLURAL("{0,number,0} seconds"),



  /**
   * {0,number,0} second
   */
  INFO_NUM_SECONDS_SINGULAR("{0,number,0} second"),



  /**
   * {0} seconds
   */
  INFO_NUM_SECONDS_WITH_DECIMAL("{0} seconds"),



  /**
   * A mechanism that can be used to destroy an existing authentication session, or to perform a bind without actually authenticating but optionally including a trace string that may help provide information about the client.
   */
  INFO_SASL_ANONYMOUS_DESCRIPTION("A mechanism that can be used to destroy an existing authentication session, or to perform a bind without actually authenticating but optionally including a trace string that may help provide information about the client."),



  /**
   * A trace string that may be used to provide additional information about the client performing the bind.  Note, however, that because the client is not providing any proof of its identity, it is not possible to determine the validity of any trace information given.
   */
  INFO_SASL_ANONYMOUS_OPTION_TRACE("A trace string that may be used to provide additional information about the client performing the bind.  Note, however, that because the client is not providing any proof of its identity, it is not possible to determine the validity of any trace information given."),



  /**
   * A mechanism that can be used to perform password-based authentication in a manner that prevents an observer from discovering the password by generating an MD5 digest based on the provided password and additional information, including random data provided by the server.
   */
  INFO_SASL_CRAM_MD5_DESCRIPTION("A mechanism that can be used to perform password-based authentication in a manner that prevents an observer from discovering the password by generating an MD5 digest based on the provided password and additional information, including random data provided by the server."),



  /**
   * A string which identifies the user that is trying to authenticate.  The value is typically in the form "dn:" followed by the DN of the target user's entry, or "u:" followed by the username for the target user.
   */
  INFO_SASL_CRAM_MD5_OPTION_AUTH_ID("A string which identifies the user that is trying to authenticate.  The value is typically in the form \"dn:\" followed by the DN of the target user's entry, or \"u:\" followed by the username for the target user."),



  /**
   * A mechanism that can be used to perform password-based authentication in a manner that prevents an observer from discovering the password by generating an MD5 digest based on the provided password and additional information, including random data provided by both the client and the server.
   */
  INFO_SASL_DIGEST_MD5_DESCRIPTION("A mechanism that can be used to perform password-based authentication in a manner that prevents an observer from discovering the password by generating an MD5 digest based on the provided password and additional information, including random data provided by both the client and the server."),



  /**
   * A string which identifies the user under whose authority subsequent operations should be processed.  If this is not provided, then no alternate authorization identity will be used.
   */
  INFO_SASL_DIGEST_MD5_OPTION_AUTHZ_ID("A string which identifies the user under whose authority subsequent operations should be processed.  If this is not provided, then no alternate authorization identity will be used."),



  /**
   * A string which identifies the user that is trying to authenticate.  The value is typically in the form "dn:" followed by the DN of the target user's entry, or "u:" followed by the username for the target user.
   */
  INFO_SASL_DIGEST_MD5_OPTION_AUTH_ID("A string which identifies the user that is trying to authenticate.  The value is typically in the form \"dn:\" followed by the DN of the target user's entry, or \"u:\" followed by the username for the target user."),



  /**
   * The realm for the user trying to authenticate.  If this is not provided, then no realm will be specified in the bind request.
   */
  INFO_SASL_DIGEST_MD5_OPTION_REALM("The realm for the user trying to authenticate.  If this is not provided, then no realm will be specified in the bind request."),



  /**
   * A mechanism that can allow the client to authenticate to the server using information that the server may have about the client which was not provided in the form of an LDAP message (e.g., a client certificate that was presented during an SSL or StartTLS handshake).
   */
  INFO_SASL_EXTERNAL_DESCRIPTION("A mechanism that can allow the client to authenticate to the server using information that the server may have about the client which was not provided in the form of an LDAP message (e.g., a client certificate that was presented during an SSL or StartTLS handshake)."),



  /**
   * A mechanism that can allow the client to authenticate to the server using Kerberos V.  It may be possible to leverage an existing Kerberos session or to authenticate using a newly-created session.
   */
  INFO_SASL_GSSAPI_DESCRIPTION("A mechanism that can allow the client to authenticate to the server using Kerberos V.  It may be possible to leverage an existing Kerberos session or to authenticate using a newly-created session."),



  /**
   * A string which identifies the user under whose authority subsequent operations should be processed.  If this is not provided, then no alternate authorization identity will be used.
   */
  INFO_SASL_GSSAPI_OPTION_AUTHZ_ID("A string which identifies the user under whose authority subsequent operations should be processed.  If this is not provided, then no alternate authorization identity will be used."),



  /**
   * A string which identifies the user that is trying to authenticate.  This should generally be the user's Kerberos principal.
   */
  INFO_SASL_GSSAPI_OPTION_AUTH_ID("A string which identifies the user that is trying to authenticate.  This should generally be the user's Kerberos principal."),



  /**
   * The path to a file containing the JAAS configuration that will be used for Kerberos processing.  if this is not specified then an automatically-generated JAAS configuration will be used.
   */
  INFO_SASL_GSSAPI_OPTION_CONFIG_FILE("The path to a file containing the JAAS configuration that will be used for Kerberos processing.  if this is not specified then an automatically-generated JAAS configuration will be used."),



  /**
   * Indicates whether JAAS and Kerberos processing debug information may be written to standard error.
   */
  INFO_SASL_GSSAPI_OPTION_DEBUG("Indicates whether JAAS and Kerberos processing debug information may be written to standard error."),



  /**
   * The address of the Kerberos KDC that should be used during authentication processing.  If this is not provided, then an attempt will be made to determine the appropriate KDC address from the underlying system configuration.
   */
  INFO_SASL_GSSAPI_OPTION_KDC_ADDRESS("The address of the Kerberos KDC that should be used during authentication processing.  If this is not provided, then an attempt will be made to determine the appropriate KDC address from the underlying system configuration."),



  /**
   * The name of the protocol used in the directory server's service principal.  If this is not provided, then a default protocol of "ldap" will be used.
   */
  INFO_SASL_GSSAPI_OPTION_PROTOCOL("The name of the protocol used in the directory server's service principal.  If this is not provided, then a default protocol of \"ldap\" will be used."),



  /**
   * The name of the Kerberos realm in which the authentication will be processed.  if this is not provided, then an attempt will be made to determine the appropriate realm from the underlying system configuration.
   */
  INFO_SASL_GSSAPI_OPTION_REALM("The name of the Kerberos realm in which the authentication will be processed.  if this is not provided, then an attempt will be made to determine the appropriate realm from the underlying system configuration."),



  /**
   * Indicates whether to attempt to renew the client's Kerberos ticket-granting ticket if authentication succeeds using an existing Kerberos session.  If this is not provided, then no attempt will be made to renew the TGT.
   */
  INFO_SASL_GSSAPI_OPTION_RENEW_TGT("Indicates whether to attempt to renew the client's Kerberos ticket-granting ticket if authentication succeeds using an existing Kerberos session.  If this is not provided, then no attempt will be made to renew the TGT."),



  /**
   * Indicates whether the client will be required to have an existing Kerberos session that will be used for the authentication rather than using a newly-created session.  This option will only be examined if a ticket cache should be used during authentication processing.  If this is not provided, then an existing Kerberos session will not be required.
   */
  INFO_SASL_GSSAPI_OPTION_REQUIRE_TICKET_CACHE("Indicates whether the client will be required to have an existing Kerberos session that will be used for the authentication rather than using a newly-created session.  This option will only be examined if a ticket cache should be used during authentication processing.  If this is not provided, then an existing Kerberos session will not be required."),



  /**
   * Specifies the path to a ticket cache file that should be used to look for an existing Kerberos session.  This option will only be examined if a ticket cache should be used during authentication processing.  If this is not provided, then the default ticket cache file path will be used.
   */
  INFO_SASL_GSSAPI_OPTION_TICKET_CACHE("Specifies the path to a ticket cache file that should be used to look for an existing Kerberos session.  This option will only be examined if a ticket cache should be used during authentication processing.  If this is not provided, then the default ticket cache file path will be used."),



  /**
   * Indicates whether to attempt to use a ticket cache in order to determine whether the user has an existing Kerberos session that may be used instead of using a newly-created session.
   */
  INFO_SASL_GSSAPI_OPTION_USE_TICKET_CACHE("Indicates whether to attempt to use a ticket cache in order to determine whether the user has an existing Kerberos session that may be used instead of using a newly-created session."),



  /**
   * A mechanism that can allow the client to perform password-based authentication, optionally using an alternate authorization identity.
   */
  INFO_SASL_PLAIN_DESCRIPTION("A mechanism that can allow the client to perform password-based authentication, optionally using an alternate authorization identity."),



  /**
   * A string which idnetifies the user under whose authority subsequent operations should be processed.  If this is not provided, then no alternate authorization identity will be used.
   */
  INFO_SASL_PLAIN_OPTION_AUTHZ_ID("A string which idnetifies the user under whose authority subsequent operations should be processed.  If this is not provided, then no alternate authorization identity will be used."),



  /**
   * A string which identifies the user that is trying to authenticate.  The value is typically in the from "dn:" followed by the DN of the target user's entry, or "u:" followed  by the username for the target user.
   */
  INFO_SASL_PLAIN_OPTION_AUTH_ID("A string which identifies the user that is trying to authenticate.  The value is typically in the from \"dn:\" followed by the DN of the target user's entry, or \"u:\" followed  by the username for the target user.");



  /**
   * The resource bundle that will be used to load the properties file.
   */
  private static final ResourceBundle RESOURCE_BUNDLE;
  static
  {
    ResourceBundle rb = null;
    try
    {
      rb = ResourceBundle.getBundle("ldap-ldapsdk-util");
    } catch (Exception e) {}
    RESOURCE_BUNDLE = rb;
  }



  /**
   * The map that will be used to hold the unformatted message strings, indexed by property name.
   */
  private static final ConcurrentHashMap<UtilityMessages,String> MESSAGE_STRINGS = new ConcurrentHashMap<UtilityMessages,String>();



  /**
   * The map that will be used to hold the message format objects, indexed by property name.
   */
  private static final ConcurrentHashMap<UtilityMessages,MessageFormat> MESSAGES = new ConcurrentHashMap<UtilityMessages,MessageFormat>();



  // The default text for this message
  private final String defaultText;



  /**
   * Creates a new message key.
   */
  private UtilityMessages(final String defaultText)
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

