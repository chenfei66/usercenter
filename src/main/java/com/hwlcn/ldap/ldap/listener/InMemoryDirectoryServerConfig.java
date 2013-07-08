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



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;

import com.hwlcn.ldap.ldap.sdk.DN;
import com.hwlcn.ldap.ldap.sdk.LDAPException;
import com.hwlcn.ldap.ldap.sdk.OperationType;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.ldap.sdk.Version;
import com.hwlcn.ldap.ldap.sdk.schema.Schema;
import com.hwlcn.ldap.util.Mutable;
import com.hwlcn.ldap.util.NotExtensible;
import com.hwlcn.ldap.util.StaticUtils;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.ldap.listener.ListenerMessages.*;



/**
 * This class provides a simple data structure with information that may be
 * used to control the behavior of an {@link com.hwlcn.ldap.ldap.listener.InMemoryDirectoryServer} instance.
 * At least one base DN must be specified.  For all other properties, the
 * following default values will be used unless an alternate configuration is
 * provided:
 * <UL>
 *   <LI>Listeners:  The server will provide a single listener that will use an
 *       automatically-selected port on all interfaces, which will not use SSL
 *       or StartTLS.</LI>
 *   <LI>Allowed Operation Types:  All types of operations will be allowed.</LI>
 *   <LI>Authentication Required Operation Types:  Authentication will not be
 *       required for any types of operations.</LI>
 *   <LI>Schema:  The server will use a schema with a number of standard
 *       attribute types and object classes.</LI>
 *   <LI>Additional Bind Credentials:  The server will not have any additional
 *       bind credentials.</LI>
 *   <LI>Referential Integrity Attributes:  Referential integrity will not be
 *       maintained.</LI>
 *   <LI>Generate Operational Attributes:  The server will automatically
 *       generate a number of operational attributes.</LI>
 *   <LI>Extended Operation Handlers:  The server will support the password
 *       modify extended operation as defined in RFC 3062, the start and end
 *       transaction extended operations as defined in RFC 5805, and the
 *       "Who Am I?" extended operation as defined in RFC 4532.</LI>
 *   <LI>SASL Bind Handlers:  The server will support the SASL PLAIN mechanism
 *       as defined in RFC 4616.</LI>
 *   <LI>Max ChangeLog Entries:  The server will not provide an LDAP
 *       changelog.</LI>
 *   <LI>Access Log Handler:  The server will not perform any access
 *       logging.</LI>
 *   <LI>LDAP Debug Log Handler:  The server will not perform any LDAP debug
 *       logging.</LI>
 *   <LI>Listener Exception Handler:  The server will not use a listener
 *       exception handler.</LI>
 * </UL>
 */
@NotExtensible()
@Mutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public class InMemoryDirectoryServerConfig
{
  // Indicates whether to enforce the requirement that attribute values comply
  // with the associated attribute syntax.
  private boolean enforceAttributeSyntaxCompliance;

  // Indicates whether to enforce the requirement that entries contain exactly
  // one structural object class.
  private boolean enforceSingleStructuralObjectClass;

  // Indicates whether to automatically generate operational attributes.
  private boolean generateOperationalAttributes;

  // The base DNs to use for the LDAP listener.
  private DN[] baseDNs;

  // The log handler that should be used to record access log messages about
  // operations processed by the server.
  private Handler accessLogHandler;

  // The log handler that should be used to record detailed protocol-level
  // messages about LDAP operations processed by the server.
  private Handler ldapDebugLogHandler;

  // The maximum number of entries to retain in a generated changelog.
  private int maxChangeLogEntries;

  // The exception handler that should be used for the listener.
  private LDAPListenerExceptionHandler exceptionHandler;

  // The listener configurations that should be used for accepting connections
  // to the server.
  private final List<InMemoryListenerConfig> listenerConfigs;

  // The extended operation handlers that may be used to process extended
  // operations in the server.
  private final List<InMemoryExtendedOperationHandler>
       extendedOperationHandlers;

  // The SASL bind handlers that may be used to process SASL bind requests in
  // the server.
  private final List<InMemorySASLBindHandler> saslBindHandlers;

  // The names or OIDs of the attributes for which to maintain equality indexes.
  private final List<String> equalityIndexAttributes;

  // A set of additional credentials that can be used for binding without
  // requiring a corresponding entry in the data set.
  private final Map<DN,byte[]> additionalBindCredentials;

  // The schema to use for the server.
  private Schema schema;

  // The set of operation types that will be supported by the server.
  private final Set<OperationType> allowedOperationTypes;

  // The set of operation types for which authentication will be required.
  private final Set<OperationType> authenticationRequiredOperationTypes;

  // The set of attributes for which referential integrity should be maintained.
  private final Set<String> referentialIntegrityAttributes;

  // The vendor name to report in the server root DSE.
  private String vendorName;

  // The vendor version to report in the server root DSE.
  private String vendorVersion;



  /**
   * Creates a new in-memory directory server config object with the provided
   * set of base DNs.
   *
   * @param  baseDNs  The set of base DNs to use for the server.  It must not
   *                  be {@code null} or empty.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided set of base DN strings is null or
   *                         empty, or if any of the provided base DN strings
   *                         cannot be parsed as a valid DN.
   */
  public InMemoryDirectoryServerConfig(final String... baseDNs)
         throws LDAPException
  {
    this(parseDNs(Schema.getDefaultStandardSchema(), baseDNs));
  }



  /**
   * Creates a new in-memory directory server config object with the default
   * settings.
   *
   * @param  baseDNs  The set of base DNs to use for the server.  It must not
   *                  be {@code null} or empty.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided set of base DNs is null or empty.
   */
  public InMemoryDirectoryServerConfig(final DN... baseDNs)
         throws LDAPException
  {
    if ((baseDNs == null) || (baseDNs.length == 0))
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
           ERR_MEM_DS_CFG_NO_BASE_DNS.get());
    }

    this.baseDNs = baseDNs;

    listenerConfigs = new ArrayList<InMemoryListenerConfig>(1);
    listenerConfigs.add(InMemoryListenerConfig.createLDAPConfig("default"));

    additionalBindCredentials            = new LinkedHashMap<DN,byte[]>(1);
    accessLogHandler                     = null;
    ldapDebugLogHandler                  = null;
    enforceAttributeSyntaxCompliance     = true;
    enforceSingleStructuralObjectClass   = true;
    generateOperationalAttributes        = true;
    maxChangeLogEntries                  = 0;
    exceptionHandler                     = null;
    equalityIndexAttributes              = new ArrayList<String>(10);
    schema                               = Schema.getDefaultStandardSchema();
    allowedOperationTypes                = EnumSet.allOf(OperationType.class);
    authenticationRequiredOperationTypes = EnumSet.noneOf(OperationType.class);
    referentialIntegrityAttributes       = new HashSet<String>(0);
    vendorName                           = "UnboundID Corp.";
    vendorVersion                        = Version.FULL_VERSION_STRING;

    extendedOperationHandlers =
         new ArrayList<InMemoryExtendedOperationHandler>(3);
    extendedOperationHandlers.add(new PasswordModifyExtendedOperationHandler());
    extendedOperationHandlers.add(new TransactionExtendedOperationHandler());
    extendedOperationHandlers.add(new WhoAmIExtendedOperationHandler());

    saslBindHandlers = new ArrayList<InMemorySASLBindHandler>(1);
    saslBindHandlers.add(new PLAINBindHandler());
  }



  /**
   * Creates a new in-memory directory server config object that is a duplicate
   * of the provided config and may be altered without impacting the state of
   * the given config object.
   *
   * @param  cfg  The in-memory directory server config object for to be
   *              duplicated.
   */
  public InMemoryDirectoryServerConfig(final InMemoryDirectoryServerConfig cfg)
  {
    baseDNs = new DN[cfg.baseDNs.length];
    System.arraycopy(cfg.baseDNs, 0, baseDNs, 0, baseDNs.length);

    listenerConfigs = new ArrayList<InMemoryListenerConfig>(
         cfg.listenerConfigs);

    extendedOperationHandlers = new ArrayList<InMemoryExtendedOperationHandler>(
         cfg.extendedOperationHandlers);

    saslBindHandlers =
         new ArrayList<InMemorySASLBindHandler>(cfg.saslBindHandlers);

    additionalBindCredentials =
         new LinkedHashMap<DN,byte[]>(cfg.additionalBindCredentials);

    referentialIntegrityAttributes =
         new HashSet<String>(cfg.referentialIntegrityAttributes);

    allowedOperationTypes = EnumSet.noneOf(OperationType.class);
    allowedOperationTypes.addAll(cfg.allowedOperationTypes);

    authenticationRequiredOperationTypes = EnumSet.noneOf(OperationType.class);
    authenticationRequiredOperationTypes.addAll(
         cfg.authenticationRequiredOperationTypes);

    equalityIndexAttributes =
         new ArrayList<String>(cfg.equalityIndexAttributes);

    enforceAttributeSyntaxCompliance   = cfg.enforceAttributeSyntaxCompliance;
    enforceSingleStructuralObjectClass = cfg.enforceSingleStructuralObjectClass;
    generateOperationalAttributes      = cfg.generateOperationalAttributes;
    accessLogHandler                   = cfg.accessLogHandler;
    ldapDebugLogHandler                = cfg.ldapDebugLogHandler;
    maxChangeLogEntries                = cfg.maxChangeLogEntries;
    exceptionHandler                   = cfg.exceptionHandler;
    schema                             = cfg.schema;
    vendorName                         = cfg.vendorName;
    vendorVersion                      = cfg.vendorVersion;
  }



  /**
   * Retrieves the set of base DNs that should be used for the directory server.
   *
   * @return  The set of base DNs that should be used for the directory server.
   */
  public DN[] getBaseDNs()
  {
    return baseDNs;
  }



  /**
   * Specifies the set of base DNs that should be used for the directory server.
   *
   * @param  baseDNs  The set of base DNs that should be used for the directory
   *                  server.  It must not be {@code null} or empty.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided set of base DN strings is null or
   *                         empty, or if any of the provided base DN strings
   *                         cannot be parsed as a valid DN.
   */
  public void setBaseDNs(final String... baseDNs)
         throws LDAPException
  {
    setBaseDNs(parseDNs(schema, baseDNs));
  }



  /**
   * Specifies the set of base DNs that should be used for the directory server.
   *
   * @param  baseDNs  The set of base DNs that should be used for the directory
   *                  server.  It must not be {@code null} or empty.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If the provided set of base DNs is null or empty.
   */
  public void setBaseDNs(final DN... baseDNs)
         throws LDAPException
  {
    if ((baseDNs == null) || (baseDNs.length == 0))
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
           ERR_MEM_DS_CFG_NO_BASE_DNS.get());
    }

    this.baseDNs = baseDNs;
  }



  /**
   * Retrieves the list of listener configurations that should be used for the
   * directory server.
   *
   * @return  The list of listener configurations that should be used for the
   *          directory server.
   */
  public List<InMemoryListenerConfig> getListenerConfigs()
  {
    return listenerConfigs;
  }



  /**
   * Specifies the configurations for all listeners that should be used for the
   * directory server.
   *
   * @param  listenerConfigs  The configurations for all listeners that should
   *                          be used for the directory server.  It must not be
   *                          {@code null} or empty, and it must not contain
   *                          multiple configurations with the same name.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If there is a problem with the provided set of
   *                         listener configurations.
   */
  public void setListenerConfigs(
                   final InMemoryListenerConfig... listenerConfigs)
         throws LDAPException
  {
    setListenerConfigs(StaticUtils.toList(listenerConfigs));
  }



  /**
   * Specifies the configurations for all listeners that should be used for the
   * directory server.
   *
   * @param  listenerConfigs  The configurations for all listeners that should
   *                          be used for the directory server.  It must not be
   *                          {@code null} or empty, and it must not contain
   *                          multiple configurations with the same name.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If there is a problem with the provided set of
   *                         listener configurations.
   */
  public void setListenerConfigs(
                   final Collection<InMemoryListenerConfig> listenerConfigs)
         throws LDAPException
  {
    if ((listenerConfigs == null) || listenerConfigs.isEmpty())
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
           ERR_MEM_DS_CFG_NO_LISTENERS.get());
    }

    final HashSet<String> listenerNames =
         new HashSet<String>(listenerConfigs.size());
    for (final InMemoryListenerConfig c : listenerConfigs)
    {
      final String name = StaticUtils.toLowerCase(c.getListenerName());
      if (listenerNames.contains(name))
      {
        throw new LDAPException(ResultCode.PARAM_ERROR,
             ERR_MEM_DS_CFG_CONFLICTING_LISTENER_NAMES.get(name));
      }
      else
      {
        listenerNames.add(name);
      }
    }

    this.listenerConfigs.clear();
    this.listenerConfigs.addAll(listenerConfigs);
  }



  /**
   * Retrieves the set of operation types that will be allowed by the server.
   * Note that if the server is configured to support StartTLS, then it will be
   * allowed even if other types of extended operations are not allowed.
   *
   * @return  The set of operation types that will be allowed by the server.
   */
  public Set<OperationType> getAllowedOperationTypes()
  {
    return allowedOperationTypes;
  }



  /**
   * Specifies the set of operation types that will be allowed by the server.
   * Note that if the server is configured to support StartTLS, then it will be
   * allowed even if other types of extended operations are not allowed.
   *
   * @param  operationTypes  The set of operation types that will be allowed by
   *                         the server.
   */
  public void setAllowedOperationTypes(final OperationType... operationTypes)
  {
    allowedOperationTypes.clear();
    if (operationTypes != null)
    {
      allowedOperationTypes.addAll(Arrays.asList(operationTypes));
    }
  }



  /**
   * Specifies the set of operation types that will be allowed by the server.
   * Note that if the server is configured to support StartTLS, then it will be
   * allowed even if other types of extended operations are not allowed.
   *
   * @param  operationTypes  The set of operation types that will be allowed by
   *                         the server.
   */
  public void setAllowedOperationTypes(
                   final Collection<OperationType> operationTypes)
  {
    allowedOperationTypes.clear();
    if (operationTypes != null)
    {
      allowedOperationTypes.addAll(operationTypes);
    }
  }



  /**
   * Retrieves the set of operation types that will only be allowed for
   * authenticated clients.  Note that authentication will never be required for
   * bind operations, and if the server is configured to support StartTLS, then
   * authentication will never be required for StartTLS operations even if it
   * is required for other types of extended operations.
   *
   * @return  The set of operation types that will only be allowed for
   *          authenticated clients.
   */
  public Set<OperationType> getAuthenticationRequiredOperationTypes()
  {
    return authenticationRequiredOperationTypes;
  }



  /**
   * Specifies the set of operation types that will only be allowed for
   * authenticated clients.  Note that authentication will never be required for
   * bind operations, and if the server is configured to support StartTLS, then
   * authentication will never be required for StartTLS operations even if it
   * is required for other types of extended operations.
   *
   * @param  operationTypes  The set of operation types that will be allowed for
   *                         authenticated clients.
   */
  public void setAuthenticationRequiredOperationTypes(
                   final OperationType... operationTypes)
  {
    authenticationRequiredOperationTypes.clear();
    if (operationTypes != null)
    {
      authenticationRequiredOperationTypes.addAll(
           Arrays.asList(operationTypes));
    }
  }



  /**
   * Specifies the set of operation types that will only be allowed for
   * authenticated clients.  Note that authentication will never be required for
   * bind operations, and if the server is configured to support StartTLS, then
   * authentication will never be required for StartTLS operations even if it
   * is required for other types of extended operations.
   *
   * @param  operationTypes  The set of operation types that will be allowed for
   *                         authenticated clients.
   */
  public void setAuthenticationRequiredOperationTypes(
                   final Collection<OperationType> operationTypes)
  {
    authenticationRequiredOperationTypes.clear();
    if (operationTypes != null)
    {
      authenticationRequiredOperationTypes.addAll(operationTypes);
    }
  }



  /**
   * Retrieves a map containing DNs and passwords of additional users that will
   * be allowed to bind to the server, even if their entries do not exist in the
   * data set.  This can be used to mimic the functionality of special
   * administrative accounts (e.g., "cn=Directory Manager" in many directories).
   * The map that is returned may be altered if desired.
   *
   * @return  A map containing DNs and passwords of additional users that will
   *          be allowed to bind to the server, even if their entries do not
   *          exist in the data set.
   */
  public Map<DN,byte[]> getAdditionalBindCredentials()
  {
    return additionalBindCredentials;
  }



  /**
   * Adds an additional bind DN and password combination that can be used to
   * bind to the server, even if the corresponding entry does not exist in the
   * data set.  This can be used to mimic the functionality of special
   * administrative accounts (e.g., "cn=Directory Manager" in many directories).
   * If a password has already been defined for the given DN, then it will be
   * replaced with the newly-supplied password.
   *
   * @param  dn        The bind DN to allow.  It must not be {@code null} or
   *                   represent the null DN.
   * @param  password  The password for the provided bind DN.  It must not be
   *                   {@code null} or empty.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If there is a problem with the provided bind DN or
   *                         password.
   */
  public void addAdditionalBindCredentials(final String dn,
                                           final String password)
         throws LDAPException
  {
    addAdditionalBindCredentials(dn, StaticUtils.getBytes(password));
  }



  /**
   * Adds an additional bind DN and password combination that can be used to
   * bind to the server, even if the corresponding entry does not exist in the
   * data set.  This can be used to mimic the functionality of special
   * administrative accounts (e.g., "cn=Directory Manager" in many directories).
   * If a password has already been defined for the given DN, then it will be
   * replaced with the newly-supplied password.
   *
   * @param  dn        The bind DN to allow.  It must not be {@code null} or
   *                   represent the null DN.
   * @param  password  The password for the provided bind DN.  It must not be
   *                   {@code null} or empty.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If there is a problem with the provided bind DN or
   *                         password.
   */
  public void addAdditionalBindCredentials(final String dn,
                                           final byte[] password)
         throws LDAPException
  {
    if (dn == null)
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
           ERR_MEM_DS_CFG_NULL_ADDITIONAL_BIND_DN.get());
    }

    final DN parsedDN = new DN(dn, schema);
    if (parsedDN.isNullDN())
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
           ERR_MEM_DS_CFG_NULL_ADDITIONAL_BIND_DN.get());
    }

    if ((password == null) || (password.length == 0))
    {
      throw new LDAPException(ResultCode.PARAM_ERROR,
           ERR_MEM_DS_CFG_NULL_ADDITIONAL_BIND_PW.get());
    }

    additionalBindCredentials.put(parsedDN, password);
  }



  /**
   * Retrieves the object that should be used to handle any errors encountered
   * while attempting to interact with a client, if defined.
   *
   * @return  The object that should be used to handle any errors encountered
   *          while attempting to interact with a client, or {@code null} if no
   *          exception handler should be used.
   */
  public LDAPListenerExceptionHandler getListenerExceptionHandler()
  {
    return exceptionHandler;
  }



  /**
   * Specifies the LDAP listener exception handler that the server should use to
   * handle any errors encountered while attempting to interact with a client.
   *
   * @param  exceptionHandler  The LDAP listener exception handler that the
   *                           server should use to handle any errors
   *                           encountered while attempting to interact with a
   *                           client.  It may be {@code null} if no exception
   *                           handler should be used.
   */
  public void setListenerExceptionHandler(
                   final LDAPListenerExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }



  /**
   * Retrieves the schema that should be used by the server, if defined.  If a
   * schema is defined, then it will be used to validate entries and determine
   * which matching rules should be used for various types of matching
   * operations.
   *
   * @return  The schema that should be used by the server, or {@code null} if
   *          no schema should be used.
   */
  public Schema getSchema()
  {
    return schema;
  }



  /**
   * Specifies the schema that should be used by the server.  If a schema is
   * defined, then it will be used to validate entries and determine which
   * matching rules should be used for various types of matching operations.
   *
   * @param  schema  The schema that should be used by the server.  It may be
   *                 {@code null} if no schema should be used.
   */
  public void setSchema(final Schema schema)
  {
    this.schema = schema;
  }



  /**
   * Indicates whether the server should reject attribute values which violate
   * the constraints of the associated syntax.  This setting will be ignored if
   * a {@code null} schema is in place.
   *
   * @return  {@code true} if the server should reject attribute values which
   *          violate the constraints of the associated syntax, or {@code false}
   *          if not.
   */
  public boolean enforceAttributeSyntaxCompliance()
  {
    return enforceAttributeSyntaxCompliance;
  }



  /**
   * Specifies whether the server should reject attribute values which violate
   * the constraints of the associated syntax.  This setting will be ignored if
   * a {@code null} schema is in place.
   *
   * @param  enforceAttributeSyntaxCompliance  Indicates whether the server
   *                                           should reject attribute values
   *                                           which violate the constraints of
   *                                           the associated syntax.
   */
  public void setEnforceAttributeSyntaxCompliance(
                   final boolean enforceAttributeSyntaxCompliance)
  {
    this.enforceAttributeSyntaxCompliance = enforceAttributeSyntaxCompliance;
  }



  /**
   * Indicates whether the server should reject entries which do not contain
   * exactly one structural object class.  This setting will be ignored if a
   * {@code null} schema is in place.
   *
   * @return  {@code true} if the server should reject entries which do not
   *          contain exactly one structural object class, or {@code false} if
   *          it should allow entries which do not have any structural class or
   *          that have multiple structural classes.
   */
  public boolean enforceSingleStructuralObjectClass()
  {
    return enforceSingleStructuralObjectClass;
  }



  /**
   * Specifies whether the server should reject entries which do not contain
   * exactly one structural object class.  This setting will be ignored if a
   * {@code null} schema is in place.
   *
   * @param  enforceSingleStructuralObjectClass  Indicates whether the server
   *                                             should reject entries which do
   *                                             not contain exactly one
   *                                             structural object class.
   */
  public void setEnforceSingleStructuralObjectClass(
                   final boolean enforceSingleStructuralObjectClass)
  {
    this.enforceSingleStructuralObjectClass =
         enforceSingleStructuralObjectClass;
  }



  /**
   * Retrieves the log handler that should be used to record access log messages
   * about operations processed by the server, if any.
   *
   * @return  The log handler that should be used to record access log messages
   *          about operations processed by the server, or {@code null} if no
   *          access logging should be performed.
   */
  public Handler getAccessLogHandler()
  {
    return accessLogHandler;
  }



  /**
   * Specifies the log handler that should be used to record access log messages
   * about operations processed by the server.
   *
   * @param  accessLogHandler  The log handler that should be used to record
   *                           access log messages about operations processed by
   *                           the server.  It may be {@code null} if no access
   *                           logging should be performed.
   */
  public void setAccessLogHandler(final Handler accessLogHandler)
  {
    this.accessLogHandler = accessLogHandler;
  }



  /**
   * Retrieves the log handler that should be used to record detailed messages
   * about LDAP communication to and from the server, which may be useful for
   * debugging purposes.
   *
   * @return  The log handler that should be used to record detailed
   *          protocol-level debug messages about LDAP communication to and from
   *          the server, or {@code null} if no debug logging should be
   *          performed.
   */
  public Handler getLDAPDebugLogHandler()
  {
    return ldapDebugLogHandler;
  }



  /**
   * Specifies the log handler that should be used to record detailed messages
   * about LDAP communication to and from the server, which may be useful for
   * debugging purposes.
   *
   * @param  ldapDebugLogHandler  The log handler that should be used to record
   *                              detailed messages about LDAP communication to
   *                              and from the server.  It may be {@code null}
   *                              if no LDAP debug logging should be performed.
   */
  public void setLDAPDebugLogHandler(final Handler ldapDebugLogHandler)
  {
    this.ldapDebugLogHandler = ldapDebugLogHandler;
  }



  /**
   * Retrieves a list of the extended operation handlers that may be used to
   * process extended operations in the server.  The contents of the list may
   * be altered by the caller.
   *
   * @return  An updatable list of the extended operation handlers that may be
   *          used to process extended operations in the server.
   */
  public List<InMemoryExtendedOperationHandler> getExtendedOperationHandlers()
  {
    return extendedOperationHandlers;
  }



  /**
   * Adds the provided extended operation handler for use by the server for
   * processing certain types of extended operations.
   *
   * @param  handler  The extended operation handler that should be used by the
   *                  server for processing certain types of extended
   *                  operations.
   */
  public void addExtendedOperationHandler(
                   final InMemoryExtendedOperationHandler handler)
  {
    extendedOperationHandlers.add(handler);
  }



  /**
   * Retrieves a list of the SASL bind handlers that may be used to process
   * SASL bind requests in the server.  The contents of the list may be altered
   * by the caller.
   *
   * @return  An updatable list of the SASL bind handlers that may be used to
   *          process SASL bind requests in the server.
   */
  public List<InMemorySASLBindHandler> getSASLBindHandlers()
  {
    return saslBindHandlers;
  }



  /**
   * Adds the provided SASL bind handler for use by the server for processing
   * certain types of SASL bind requests.
   *
   * @param  handler  The SASL bind handler that should be used by the server
   *                  for processing certain types of SASL bind requests.
   */
  public void addSASLBindHandler(final InMemorySASLBindHandler handler)
  {
    saslBindHandlers.add(handler);
  }



  /**
   * Indicates whether the server should automatically generate operational
   * attributes (including entryDN, entryUUID, creatorsName, createTimestamp,
   * modifiersName, modifyTimestamp, and subschemaSubentry) for entries in the
   * server.
   *
   * @return  {@code true} if the server should automatically generate
   *          operational attributes for entries in the server, or {@code false}
   *          if not.
   */
  public boolean generateOperationalAttributes()
  {
    return generateOperationalAttributes;
  }



  /**
   * Specifies whether the server should automatically generate operational
   * attributes (including entryDN, entryUUID, creatorsName, createTimestamp,
   * modifiersName, modifyTimestamp, and subschemaSubentry) for entries in the
   * server.
   *
   * @param  generateOperationalAttributes  Indicates whether the server should
   *                                        automatically generate operational
   *                                        attributes for entries in the
   *                                        server.
   */
  public void setGenerateOperationalAttributes(
                   final boolean generateOperationalAttributes)
  {
    this.generateOperationalAttributes = generateOperationalAttributes;
  }



  /**
   * Retrieves the maximum number of changelog entries that the server should
   * maintain.
   *
   * @return  The maximum number of changelog entries that the server should
   *          maintain, or 0 if the server should not maintain a changelog.
   */
  public int getMaxChangeLogEntries()
  {
    return maxChangeLogEntries;
  }



  /**
   * Specifies the maximum number of changelog entries that the server should
   * maintain.  A value less than or equal to zero indicates that the server
   * should not attempt to maintain a changelog.
   *
   * @param  maxChangeLogEntries  The maximum number of changelog entries that
   *                              the server should maintain.
   */
  public void setMaxChangeLogEntries(final int maxChangeLogEntries)
  {
    if (maxChangeLogEntries < 0)
    {
      this.maxChangeLogEntries = 0;
    }
    else
    {
      this.maxChangeLogEntries = maxChangeLogEntries;
    }
  }



  /**
   * Retrieves a list containing the names or OIDs of the attribute types for
   * which to maintain an equality index to improve the performance of certain
   * kinds of searches.
   *
   * @return  A list containing the names or OIDs of the attribute types for
   *          which to maintain an equality index to improve the performance of
   *          certain kinds of searches, or an empty list if no equality indexes
   *          should be created.
   */
  public List<String> getEqualityIndexAttributes()
  {
    return equalityIndexAttributes;
  }



  /**
   * Specifies the names or OIDs of the attribute types for which to maintain an
   * equality index to improve the performance of certain kinds of searches.
   *
   * @param  equalityIndexAttributes  The names or OIDs of the attributes for
   *                                  which to maintain an equality index to
   *                                  improve the performance of certain kinds
   *                                  of searches.  It may be {@code null} or
   *                                  empty to indicate that no equality indexes
   *                                  should be maintained.
   */
  public void setEqualityIndexAttributes(
                   final String... equalityIndexAttributes)
  {
    setEqualityIndexAttributes(StaticUtils.toList(equalityIndexAttributes));
  }



  /**
   * Specifies the names or OIDs of the attribute types for which to maintain an
   * equality index to improve the performance of certain kinds of searches.
   *
   * @param  equalityIndexAttributes  The names or OIDs of the attributes for
   *                                  which to maintain an equality index to
   *                                  improve the performance of certain kinds
   *                                  of searches.  It may be {@code null} or
   *                                  empty to indicate that no equality indexes
   *                                  should be maintained.
   */
  public void setEqualityIndexAttributes(
                   final Collection<String> equalityIndexAttributes)
  {
    this.equalityIndexAttributes.clear();
    if (equalityIndexAttributes != null)
    {
      this.equalityIndexAttributes.addAll(equalityIndexAttributes);
    }
  }



  /**
   * Retrieves the names of the attributes for which referential integrity
   * should be maintained.  If referential integrity is to be provided and an
   * entry is removed, then any other entries containing one of the specified
   * attributes with a value equal to the DN of the entry that was removed, then
   * that value will also be removed.  Similarly, if an entry is moved or
   * renamed, then any references to that entry in one of the specified
   * attributes will be updated to reflect the new DN.
   *
   * @return  The names of the attributes for which referential integrity should
   *          be maintained, or an empty set if referential integrity should not
   *          be maintained for any attributes.
   */
  public Set<String> getReferentialIntegrityAttributes()
  {
    return referentialIntegrityAttributes;
  }



  /**
   * Specifies the names of the attributes for which referential integrity
   * should be maintained.  If referential integrity is to be provided and an
   * entry is removed, then any other entries containing one of the specified
   * attributes with a value equal to the DN of the entry that was removed, then
   * that value will also be removed.  Similarly, if an entry is moved or
   * renamed, then any references to that entry in one of the specified
   * attributes will be updated to reflect the new DN.
   *
   * @param  referentialIntegrityAttributes  The names of the attributes for
   *                                          which referential integrity should
   *                                          be maintained.  The values of
   *                                          these attributes should be DNs.
   *                                          It may be {@code null} or empty if
   *                                          referential integrity should not
   *                                          be maintained.
   */
  public void setReferentialIntegrityAttributes(
                   final String... referentialIntegrityAttributes)
  {
    setReferentialIntegrityAttributes(
         StaticUtils.toList(referentialIntegrityAttributes));
  }



  /**
   * Specifies the names of the attributes for which referential integrity
   * should be maintained.  If referential integrity is to be provided and an
   * entry is removed, then any other entries containing one of the specified
   * attributes with a value equal to the DN of the entry that was removed, then
   * that value will also be removed.  Similarly, if an entry is moved or
   * renamed, then any references to that entry in one of the specified
   * attributes will be updated to reflect the new DN.
   *
   * @param  referentialIntegrityAttributes  The names of the attributes for
   *                                          which referential integrity should
   *                                          be maintained.  The values of
   *                                          these attributes should be DNs.
   *                                          It may be {@code null} or empty if
   *                                          referential integrity should not
   *                                          be maintained.
   */
  public void setReferentialIntegrityAttributes(
                   final Collection<String> referentialIntegrityAttributes)
  {
    this.referentialIntegrityAttributes.clear();
    if (referentialIntegrityAttributes != null)
    {
      this.referentialIntegrityAttributes.addAll(
           referentialIntegrityAttributes);
    }
  }



  /**
   * Retrieves the vendor name value to report in the server root DSE.
   *
   * @return  The vendor name value to report in the server root DSE, or
   *          {@code null} if no vendor name should appear.
   */
  public String getVendorName()
  {
    return vendorName;
  }



  /**
   * Specifies the vendor name value to report in the server root DSE.
   *
   * @param  vendorName  The vendor name value to report in the server root DSE.
   *                     It may be {@code null} if no vendor name should appear.
   */
  public void setVendorName(final String vendorName)
  {
    this.vendorName = vendorName;
  }



  /**
   * Retrieves the vendor version value to report in the server root DSE.
   *
   * @return  The vendor version value to report in the server root DSE, or
   *          {@code null} if no vendor version should appear.
   */
  public String getVendorVersion()
  {
    return vendorVersion;
  }



  /**
   * Specifies the vendor version value to report in the server root DSE.
   *
   * @param  vendorVersion  The vendor version value to report in the server
   *                        root DSE.  It may be {@code null} if no vendor
   *                        version should appear.
   */
  public void setVendorVersion(final String vendorVersion)
  {
    this.vendorVersion = vendorVersion;
  }



  /**
   * Parses the provided set of strings as DNs.
   *
   * @param  dnStrings  The array of strings to be parsed as DNs.
   * @param  schema     The schema to use to generate the normalized
   *                    representations of the DNs, if available.
   *
   * @return  The array of parsed DNs.
   *
   * @throws  com.hwlcn.ldap.ldap.sdk.LDAPException  If any of the provided strings cannot be parsed as
   *                         DNs.
   */
  private static DN[] parseDNs(final Schema schema, final String... dnStrings)
          throws LDAPException
  {
    if (dnStrings == null)
    {
      return null;
    }

    final DN[] dns = new DN[dnStrings.length];
    for (int i=0; i < dns.length; i++)
    {
      dns[i] = new DN(dnStrings[i], schema);
    }
    return dns;
  }



  /**
   * Retrieves a string representation of this in-memory directory server
   * configuration.
   *
   * @return  A string representation of this in-memory directory server
   *          configuration.
   */
  @Override()
  public String toString()
  {
    final StringBuilder buffer = new StringBuilder();
    toString(buffer);
    return buffer.toString();
  }



  /**
   * Appends a string representation of this in-memory directory server
   * configuration to the provided buffer.
   *
   * @param  buffer  The buffer to which the string representation should be
   *                 appended.
   */
  public void toString(final StringBuilder buffer)
  {
    buffer.append("InMemoryDirectoryServerConfig(baseDNs={");

    for (int i=0; i < baseDNs.length; i++)
    {
      if (i > 0)
      {
        buffer.append(", ");
      }

      buffer.append('\'');
      baseDNs[i].toString(buffer);
      buffer.append('\'');
    }
    buffer.append('}');

    buffer.append(", listenerConfigs={");

    final Iterator<InMemoryListenerConfig> listenerCfgIterator =
         listenerConfigs.iterator();
    while(listenerCfgIterator.hasNext())
    {
      listenerCfgIterator.next().toString(buffer);
      if (listenerCfgIterator.hasNext())
      {
        buffer.append(", ");
      }
    }
    buffer.append('}');

    buffer.append(", schemaProvided=");
    buffer.append((schema != null));
    buffer.append(", enforceAttributeSyntaxCompliance=");
    buffer.append(enforceAttributeSyntaxCompliance);
    buffer.append(", enforceSingleStructuralObjectClass=");
    buffer.append(enforceSingleStructuralObjectClass);

    if (! additionalBindCredentials.isEmpty())
    {
      buffer.append(", additionalBindDNs={");

      final Iterator<DN> bindDNIterator =
           additionalBindCredentials.keySet().iterator();
      while (bindDNIterator.hasNext())
      {
        buffer.append('\'');
        bindDNIterator.next().toString(buffer);
        buffer.append('\'');
        if (bindDNIterator.hasNext())
        {
          buffer.append(", ");
        }
      }
      buffer.append('}');
    }

    if (! equalityIndexAttributes.isEmpty())
    {
      buffer.append(", equalityIndexAttributes={");

      final Iterator<String> attrIterator = equalityIndexAttributes.iterator();
      while (attrIterator.hasNext())
      {
        buffer.append('\'');
        buffer.append(attrIterator.next());
        buffer.append('\'');
        if (attrIterator.hasNext())
        {
          buffer.append(", ");
        }
      }
      buffer.append('}');
    }

    if (! referentialIntegrityAttributes.isEmpty())
    {
      buffer.append(", referentialIntegrityAttributes={");

      final Iterator<String> attrIterator =
           referentialIntegrityAttributes.iterator();
      while (attrIterator.hasNext())
      {
        buffer.append('\'');
        buffer.append(attrIterator.next());
        buffer.append('\'');
        if (attrIterator.hasNext())
        {
          buffer.append(", ");
        }
      }
      buffer.append('}');
    }

    buffer.append(", generateOperationalAttributes=");
    buffer.append(generateOperationalAttributes);

    if (maxChangeLogEntries > 0)
    {
      buffer.append(", maxChangelogEntries=");
      buffer.append(maxChangeLogEntries);
    }

    if (! extendedOperationHandlers.isEmpty())
    {
      buffer.append(", extendedOperationHandlers={");

      final Iterator<InMemoryExtendedOperationHandler>
           handlerIterator = extendedOperationHandlers.iterator();
      while (handlerIterator.hasNext())
      {
        buffer.append(handlerIterator.next().toString());
        if (handlerIterator.hasNext())
        {
          buffer.append(", ");
        }
      }
      buffer.append('}');
    }

    if (! saslBindHandlers.isEmpty())
    {
      buffer.append(", saslBindHandlers={");

      final Iterator<InMemorySASLBindHandler>
           handlerIterator = saslBindHandlers.iterator();
      while (handlerIterator.hasNext())
      {
        buffer.append(handlerIterator.next().toString());
        if (handlerIterator.hasNext())
        {
          buffer.append(", ");
        }
      }
      buffer.append('}');
    }

    if (accessLogHandler != null)
    {
      buffer.append(", accessLogHandlerClass='");
      buffer.append(accessLogHandler.getClass().getName());
      buffer.append('\'');
    }

    if (ldapDebugLogHandler != null)
    {
      buffer.append(", ldapDebugLogHandlerClass='");
      buffer.append(ldapDebugLogHandler.getClass().getName());
      buffer.append('\'');
    }

    if (exceptionHandler != null)
    {
      buffer.append(", listenerExceptionHandlerClass='");
      buffer.append(exceptionHandler.getClass().getName());
      buffer.append('\'');
    }

    if (vendorName != null)
    {
      buffer.append(", vendorName='");
      buffer.append(vendorName);
      buffer.append('\'');
    }

    if (vendorVersion != null)
    {
      buffer.append(", vendorVersion='");
      buffer.append(vendorVersion);
      buffer.append('\'');
    }

    buffer.append(')');
  }
}
