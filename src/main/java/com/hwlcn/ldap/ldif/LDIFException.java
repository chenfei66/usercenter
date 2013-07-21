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
package com.hwlcn.ldap.ldif;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hwlcn.ldap.util.LDAPSDKException;
import com.hwlcn.core.annotation.NotMutable;
import com.hwlcn.ldap.util.StaticUtils;
import com.hwlcn.core.annotation.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.util.Validator.*;



/**
 * This class defines an exception that may be thrown if a problem occurs while
 * attempting to decode data read from an LDIF source.  It has a flag to
 * indicate whether it is possible to try to continue reading additional
 * information from the LDIF source, and also the approximate line number on
 * which the problem was encountered.
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDIFException
       extends LDAPSDKException
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 1665883395956836732L;



  // Indicates whether it is possible to continue attempting to read from the
  // LDIF source.
  private final boolean mayContinueReading;

  // The line number in the LDIF source on which the problem occurred.
  private final long lineNumber;

  // A list of the lines comprising the LDIF data being parsed, if available.
  private final List<String> dataLines;



  /**
   * Creates a new LDIF exception with the provided information.
   *
   * @param  message             A message explaining the problem that occurred.
   *                             It must not be {@code null}.
   * @param  lineNumber          The line number in the LDIF source on which the
   *                             problem occurred.
   * @param  mayContinueReading  Indicates whether it is possible to continue
   *                             attempting to read from the LDIF source.
   */
  public LDIFException(final String message, final long lineNumber,
                       final boolean mayContinueReading)
  {
    this(message, lineNumber, mayContinueReading, (List<CharSequence>) null,
         null);
  }



  /**
   * Creates a new LDIF exception with the provided information.
   *
   * @param  message             A message explaining the problem that occurred.
   *                             It must not be {@code null}.
   * @param  lineNumber          The line number in the LDIF source on which the
   *                             problem occurred.
   * @param  mayContinueReading  Indicates whether it is possible to continue
   *                             attempting to read from the LDIF source.
   * @param  cause               The underlying exception that triggered this
   *                             exception.
   */
  public LDIFException(final String message, final long lineNumber,
                       final boolean mayContinueReading, final Throwable cause)
  {
    this(message, lineNumber, mayContinueReading, (List<CharSequence>) null,
         cause);
  }



  /**
   * Creates a new LDIF exception with the provided information.
   *
   * @param  message             A message explaining the problem that occurred.
   *                             It must not be {@code null}.
   * @param  lineNumber          The line number in the LDIF source on which the
   *                             problem occurred.
   * @param  mayContinueReading  Indicates whether it is possible to continue
   *                             attempting to read from the LDIF source.
   * @param  dataLines           The lines that comprise the data that could not
   *                             be parsed as valid LDIF.  It may be
   *                             {@code null} if this is not available.
   * @param  cause               The underlying exception that triggered this
   *                             exception.
   */
  public LDIFException(final String message, final long lineNumber,
                       final boolean mayContinueReading,
                       final CharSequence[] dataLines, final Throwable cause)
  {
    this(message, lineNumber, mayContinueReading,
         (dataLines == null) ? null : Arrays.asList(dataLines),
         cause);
  }



  /**
   * Creates a new LDIF exception with the provided information.
   *
   * @param  message             A message explaining the problem that occurred.
   *                             It must not be {@code null}.
   * @param  lineNumber          The line number in the LDIF source on which the
   *                             problem occurred.
   * @param  mayContinueReading  Indicates whether it is possible to continue
   *                             attempting to read from the LDIF source.
   * @param  dataLines           The lines that comprise the data that could not
   *                             be parsed as valid LDIF.  It may be
   *                             {@code null} if this is not available.
   * @param  cause               The underlying exception that triggered this
   *                             exception.
   */
  public LDIFException(final String message, final long lineNumber,
                       final boolean mayContinueReading,
                       final List<? extends CharSequence> dataLines,
                       final Throwable cause)
  {
    super(message, cause);

    ensureNotNull(message);

    this.lineNumber         = lineNumber;
    this.mayContinueReading = mayContinueReading;

    if (dataLines == null)
    {
      this.dataLines = null;
    }
    else
    {
      final ArrayList<String> lineList =
           new ArrayList<String>(dataLines.size());
      for (final CharSequence s : dataLines)
      {
        lineList.add(s.toString());
      }

      this.dataLines = Collections.unmodifiableList(lineList);
    }
  }



  /**
   * Retrieves the line number on which the problem occurred.
   *
   * @return  The line number on which the problem occurred.
   */
  public long getLineNumber()
  {
    return lineNumber;
  }



  /**
   * Indicates whether it is possible to continue attempting to read from the
   * LDIF source.
   *
   * @return  {@code true} if it is possible to continue attempting to read from
   *          the LDIF source, or {@code false} if it is not possible to
   *          continue.
   */
  public boolean mayContinueReading()
  {
    return mayContinueReading;
  }



  /**
   * Retrieves the lines comprising the data that could not be parsed as valid
   * LDIF, if available.
   *
   * @return  An unmodifiable list of the lines comprising the data that could
   *          not be parsed as valid LDIF, or {@code null} if that is not
   *          available.
   */
  public List<String> getDataLines()
  {
    return dataLines;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("LDIFException(lineNumber=");
    buffer.append(lineNumber);
    buffer.append(", mayContinueReading=");
    buffer.append(mayContinueReading);
    buffer.append(", message='");
    buffer.append(getMessage());

    if (dataLines != null)
    {
      buffer.append("', dataLines='");
      for (final CharSequence s : dataLines)
      {
        buffer.append(s);
        buffer.append("{end-of-line}");
      }
    }

    final Throwable cause = getCause();
    if (cause == null)
    {
      buffer.append("')");
    }
    else
    {
      buffer.append("', cause=");
      StaticUtils.getStackTrace(cause, buffer);
      buffer.append(')');
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getExceptionMessage()
  {
    return toString();
  }
}
