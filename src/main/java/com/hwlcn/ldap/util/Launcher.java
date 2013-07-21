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
package com.hwlcn.ldap.util;



import java.io.OutputStream;
import java.io.PrintStream;

import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.ldap.listener.InMemoryDirectoryServerTool;
import com.hwlcn.ldap.ldap.sdk.ResultCode;
import com.hwlcn.ldap.ldap.sdk.Version;
import com.hwlcn.ldap.ldap.sdk.examples.AuthRate;
import com.hwlcn.ldap.ldap.sdk.examples.LDAPCompare;
import com.hwlcn.ldap.ldap.sdk.examples.LDAPDebugger;
import com.hwlcn.ldap.ldap.sdk.examples.LDAPModify;
import com.hwlcn.ldap.ldap.sdk.examples.LDAPSearch;
import com.hwlcn.ldap.ldap.sdk.examples.ModRate;
import com.hwlcn.ldap.ldap.sdk.examples.SearchRate;
import com.hwlcn.ldap.ldap.sdk.examples.SearchAndModRate;
import com.hwlcn.ldap.ldap.sdk.examples.ValidateLDIF;
import com.hwlcn.ldap.ldap.sdk.persist.GenerateSchemaFromSource;
import com.hwlcn.ldap.ldap.sdk.persist.GenerateSourceFromSchema;



/**
 * This class provides an entry point that may be used to launch other tools
 * provided as part of the LDAP SDK.  This is primarily a convenience for
 * someone who just has the jar file and none of the scripts, since you can run
 * "<CODE>java -jar ldap-ldapsdk-se.jar {tool-name} {tool-args}</CODE>"
 * in order to invoke any of the example tools.  Running just
 * "<CODE>java -jar ldap-ldapsdk-se.jar</CODE>" will display version
 * information about the LDAP SDK.
 * <BR><BR>
 * The tool names are case-insensitive.  Supported tool names include:
 * <UL>
 *   <LI>authrate -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.AuthRate} tool.</LI>
 *   <LI>in-memory-directory-server -- Launch the
 *       {@link com.hwlcn.ldap.ldap.listener.InMemoryDirectoryServerTool} tool.</LI>
 *   <LI>generate-schema-from-source -- Launch the
 *       {@link com.hwlcn.ldap.ldap.sdk.persist.GenerateSchemaFromSource} tool.</LI>
 *   <LI>generate-source-from-schema -- Launch the
 *       {@link com.hwlcn.ldap.ldap.sdk.persist.GenerateSourceFromSchema} tool.</LI>
 *   <LI>ldapcompare -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.LDAPCompare} tool.</LI>
 *   <LI>ldapmodify -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.LDAPModify} tool.</LI>
 *   <LI>ldapsearch -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.LDAPSearch} tool.</LI>
 *   <LI>ldap-debugger -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.LDAPDebugger} tool.</LI>
 *   <LI>modrate -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.ModRate} tool.</LI>
 *   <LI>searchrate -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.SearchRate} tool.</LI>
 *   <LI>search-and-mod-rate -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.SearchAndModRate} tool.</LI>
 *   <LI>validate-ldif -- Launch the {@link com.hwlcn.ldap.ldap.sdk.examples.ValidateLDIF} tool.</LI>
 *   <LI>version -- Display version information for the LDAP SDK.</LI>
 * </UL>
 */
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Launcher
{
  /**
   * Prevent this utility class from being externally instantiated.
   */
  Launcher()
  {
    // No implementation required.
  }



  /**
   * Parses the command-line arguments and performs any appropriate processing
   * for this program.
   *
   * @param  args  The command-line arguments provided to this program.
   */
  public static void main(final String... args)
  {
    main(System.out, System.err, args);
  }



  /**
   * Parses the command-line arguments and performs any appropriate processing
   * for this program.
   *
   * @param  outStream  The output stream to which standard out should be
   *                    written.  It may be {@code null} if output should be
   *                    suppressed.
   * @param  errStream  The output stream to which standard error should be
   *                    written.  It may be {@code null} if error messages
   *                    should be suppressed.
   * @param  args       The command-line arguments provided to this program.
   *
   * @return  A result code with information about the status of processing.
   */
  public static ResultCode main(final OutputStream outStream,
                                final OutputStream errStream,
                                final String... args)
  {
    if ((args == null) || (args.length == 0) ||
        args[0].equalsIgnoreCase("version"))
    {
      if (outStream != null)
      {
        final PrintStream out = new PrintStream(outStream);
        for (final String line : Version.getVersionLines())
        {
          out.println(line);
        }
      }

      return ResultCode.SUCCESS;
    }

    final String firstArg = StaticUtils.toLowerCase(args[0]);
    final String[] remainingArgs = new String[args.length - 1];
    System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);

    if (firstArg.equals("authrate"))
    {
      return AuthRate.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("in-memory-directory-server"))
    {
      return InMemoryDirectoryServerTool.main(remainingArgs, outStream,
           errStream);
    }
    else if (firstArg.equals("generate-schema-from-source"))
    {
      return GenerateSchemaFromSource.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("generate-source-from-schema"))
    {
      return GenerateSourceFromSchema.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("ldapcompare"))
    {
      return LDAPCompare.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("ldapmodify"))
    {
      return LDAPModify.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("ldapsearch"))
    {
      return LDAPSearch.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("ldap-debugger"))
    {
      return LDAPDebugger.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("modrate"))
    {
      return ModRate.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("searchrate"))
    {
      return SearchRate.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("search-and-mod-rate"))
    {
      return SearchAndModRate.main(remainingArgs, outStream, errStream);
    }
    else if (firstArg.equals("validate-ldif"))
    {
      return ValidateLDIF.main(remainingArgs, outStream, errStream);
    }
    else
    {
      if (errStream != null)
      {
        final PrintStream err = new PrintStream(errStream);
        err.println("Unrecognized tool name '" + args[0] + '\'');
        err.println("Supported tool names include:");
        err.println("     authrate");
        err.println("     in-memory-directory-server");
        err.println("     generate-schema-from-source");
        err.println("     generate-source-from-schema");
        err.println("     ldapcompare");
        err.println("     ldapmodify");
        err.println("     ldapsearch");
        err.println("     ldap-debugger");
        err.println("     modrate");
        err.println("     searchrate");
        err.println("     search-and-mod-rate");
        err.println("     validate-ldif");
        err.println("     version");
      }

      return ResultCode.PARAM_ERROR;
    }
  }
}
