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



import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * This annotation type may be used to mark methods which may be used to set
 * values in the associated object using attributes read from an LDAP directory
 * server.  It should only be used for methods in classes that contain the
 * {@link com.hwlcn.ldap.ldap.sdk.persist.LDAPObject} annotation type.  Those methods must not be static and
 * must take a single argument, which is the value to set from the corresponding
 * LDAP attribute, but they may have any access modifier (including
 * {@code public}, {@code protected}, {@code private}, or no access modifier at
 * all indicating package-level access).  The associated attribute must not be
 * referenced by any other {@link com.hwlcn.ldap.ldap.sdk.persist.LDAPField} or {@code LDAPSetter} annotations
 * in the same class, and it may be referenced by at most one {@link com.hwlcn.ldap.ldap.sdk.persist.LDAPGetter}
 * annotation.
 */
@Documented()
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface LDAPSetter
{
  /**
   * Indicates whether attempts to initialize an object should fail if the LDAP
   * attribute has a value that cannot be represented in the required argument
   * type for the associated method.  If this is {@code true}, then an exception
   * will be thrown in such instances.  If this is {@code false}, then the
   * associated method will not be invoked, and attempts to modify the
   * corresponding entry in the directory may cause the existing values to be
   * lost.
   */
  boolean failOnInvalidValue() default true;



  /**
   * Indicates whether attempts to initialize an object should fail if the
   * LDAP attribute has multiple values but the argument for the associated
   * method only accepts a single value.  If this is {@code true}, then an
   * exception will be thrown in such instances.  If this is {@code false}, then
   * only the first value returned will be used, and attempts to modify the
   * corresponding entry in the directory may cause those additional values to
   * be lost.
   */
  boolean failOnTooManyValues() default true;



  /**
   * The class that provides the logic for encoding the value of this method to
   * an LDAP attribute.
   */
  Class<? extends ObjectEncoder> encoderClass()
       default DefaultObjectEncoder.class;



  /**
   * The name of the attribute type in which the value of the associated method
   * will be stored.  If this is not provided, then the method name must start
   * with "set" and it will be assumed that the attribute name is the remainder
   * of the method name.
   */
  String attribute() default "";
}
