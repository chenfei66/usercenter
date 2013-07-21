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
package com.hwlcn.ldap.ldap.sdk.persist;



import java.io.Serializable;

import com.hwlcn.core.annotation.Extensible;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class provides a mechanism that can be used for generating object
 * identifiers (OIDs) for use in attribute type and object class definitions
 * constructed for use in representing an object in the directory.
 * <BR><BR>
 * Note that OIDs generated are not necessarily required to be valid, nor are
 * they required to be unique.  As such, OIDs included in generated attribute
 * type and object class definitions may need to be edited before the
 * definitions can be added to the directory server.
 */
@Extensible()
@ThreadSafety(level=ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class OIDAllocator
       implements Serializable
{
  /**
   * Allocates an OID for the attribute type with the specified name.
   *
   * @param  name  The name of the attribute type for which to generate an OID.
   *               It must not be {@code null} or empty.
   *
   * @return  The OID to use for the attribute type definition.
   */
  public abstract String allocateAttributeTypeOID(final String name);



  /**
   * Allocates an OID for the object class with the specified name.
   *
   * @param  name  The name of the object class for which to generate an OID.
   *               It must not be {@code null} or empty.
   *
   * @return  The OID to use for the object class definition.
   */
  public abstract String allocateObjectClassOID(final String name);
}
