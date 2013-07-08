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
package com.hwlcn.ldap.ldap.sdk.migrate.ldapjdk;



import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.hwlcn.ldap.util.InternalUseOnly;
import com.hwlcn.ldap.util.Mutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class provides an implementation of an {@code Enumeration} that is based
 * on an {@code Iterable} object.
 *
 * @param  <T>  The type of object for this enumeration.
 */
@InternalUseOnly()
@Mutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
final class IterableEnumeration<T>
      implements Enumeration<T>
{
  // The iteration over which this enumeration operates.
  private final Iterator<T> iterator;



  /**
   * Creates a new enumeration for the provided {@code Iterable} object.
   *
   * @param  i  The {@code Iterable} object to use to create this enumeration.
   */
  IterableEnumeration(final Iterable<T> i)
  {
    iterator = i.iterator();
  }



  /**
   * Indicates whether this enumeration has any more elements.
   *
   * @return  {@code true} if this enumeration has at least one more element, or
   *          {@code false} if not.
   */
  public boolean hasMoreElements()
  {
    return iterator.hasNext();
  }



  /**
   * Retrieves the next element from this enumeration.
   *
   * @return  The next element for this enumeration.
   *
   * @throws  java.util.NoSuchElementException  If there are no more elements to retrieve.
   */
  public T nextElement()
         throws NoSuchElementException
  {
    return iterator.next();
  }
}
