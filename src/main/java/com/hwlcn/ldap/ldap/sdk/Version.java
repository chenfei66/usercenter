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



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;



/**
 * This class provides information about the current version of the UnboundID
 * LDAP SDK for Java.
 */
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Version
{
  //
  // NOTE -- This file is dynamically generated.  Do not edit it.  If you need
  //         to add something to it, then add it to the
  //         resource/Version.java.stub file below the LDAP SDK build root.
  //



  /**
   * The official full product name for the LDAP SDK.  For this build, the
   * value is "UnboundID LDAP SDK for Java".
   */
  public static final String PRODUCT_NAME =
       "UnboundID LDAP SDK for Java";



  /**
   * The short product name for the LDAP SDK.  This will not have any spaces.
   * For this build, the value is "ldap-ldapsdk".
   */
  public static final String SHORT_NAME =
       "ldap-ldapsdk";



  /**
   * The major version number for the LDAP SDK.  For this build, the value is
   * 2.
   */
  public static final int MAJOR_VERSION = 2;



  /**
   * The minor version number for the LDAP SDK.  For this build, the value is
   * 3.
   */
  public static final int MINOR_VERSION = 3;



  /**
   * The point version number for the LDAP SDK.  For this build, the value is
   * 4.
   */
  public static final int POINT_VERSION = 4;



  /**
   * The version qualifier string for the LDAP SDK.  It will often be a
   * zero-length string, but may be non-empty for special builds that should be
   * tagged in some way (e.g., "-beta1" or "-rc2").  For this build, the value
   * is "".
   */
  public static final String VERSION_QUALIFIER =
       "";



  /**
   * A timestamp that indicates when this build of the LDAP SDK was generated.
   * For this build, the value is "20130620201729Z".
   */
  public static final String BUILD_TIMESTAMP = "20130620201729Z";



  /**
   * The Subversion path associated with the build root directory from which
   * this build of the LDAP SDK was generated.  It may be an absolute local
   * filesystem path if the Subversion path isn't available at build time.
   * For this build, the value is "/directory/trunk/ldapsdk".
   */
  public static final String REPOSITORY_PATH =
       "/directory/trunk/ldapsdk";



  /**
   * The source revision number from which this build of the LDAP SDK was
   * generated.  It may be -1 if the Subversion revision isn't available at
   * build time.  For this build, the value is 15579.
   */
  public static final long REVISION_NUMBER = 15579;



  /**
   * The full version string for the LDAP SDK.  For this build, the value is
   * "UnboundID LDAP SDK for Java 2.3.4".
   */
  public static final String FULL_VERSION_STRING =
       PRODUCT_NAME + ' ' + MAJOR_VERSION + '.' + MINOR_VERSION + '.' +
       POINT_VERSION + VERSION_QUALIFIER;



  /**
   * The short version string for the LDAP SDK.  This will not have any spaces.
   * For this build, the value is
   * "ldap-ldapsdk-2.3.4".
   */
  public static final String SHORT_VERSION_STRING =
       SHORT_NAME + '-' + MAJOR_VERSION + '.' + MINOR_VERSION + '.' +
       POINT_VERSION + VERSION_QUALIFIER;



  /**
   *The version number string for the LDAP SDK, which contains just the major,
   * minor, and point version, and optional version qualifier.  For this build,
   * the version string is
   * "2.3.4".
   */
  public static final String NUMERIC_VERSION_STRING =
       MAJOR_VERSION + "." + MINOR_VERSION + '.' +
       POINT_VERSION + VERSION_QUALIFIER;



  /**
   * Prevent this class from being instantiated.
   */
  private Version()
  {
    // No implementation is required.
  }



  /**
   * Prints version information from this class to standard output.
   *
   * @param  args  The command-line arguments provided to this program.
   */
  public static void main(final String... args)
  {
    for (final String line : getVersionLines())
    {
      System.out.println(line);
    }
  }



  /**
   * Retrieves a list of lines containing information about the LDAP SDK
   * version.
   *
   * @return  A list of lines containing information about the LDAP SDK
   *          version.
   */
  public static List<String> getVersionLines()
  {
    final ArrayList<String> versionLines = new ArrayList<String>(11);

    versionLines.add("Full Version String:   " + FULL_VERSION_STRING);
    versionLines.add("Short Version String:  " + SHORT_VERSION_STRING);
    versionLines.add("Product Name:          " + PRODUCT_NAME);
    versionLines.add("Short Name:            " + SHORT_NAME);
    versionLines.add("Major Version:         " + MAJOR_VERSION);
    versionLines.add("Minor Version:         " + MINOR_VERSION);
    versionLines.add("Point Version:         " + POINT_VERSION);
    versionLines.add("Version Qualifier:     " + VERSION_QUALIFIER);
    versionLines.add("Build Timestamp:       " + BUILD_TIMESTAMP);
    versionLines.add("Repository Path:       " + REPOSITORY_PATH);
    versionLines.add("Revision Number:       " + REVISION_NUMBER);

    return Collections.unmodifiableList(versionLines);
  }
}
