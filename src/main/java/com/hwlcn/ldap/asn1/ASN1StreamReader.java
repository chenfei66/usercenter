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
package com.hwlcn.ldap.asn1;



import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

import com.hwlcn.ldap.util.Mutable;
import com.hwlcn.ldap.util.ThreadSafety;
import com.hwlcn.ldap.util.ThreadSafetyLevel;

import static com.hwlcn.ldap.asn1.ASN1Messages.*;
import static com.hwlcn.ldap.util.Debug.*;
import static com.hwlcn.ldap.util.StaticUtils.*;



/**
 * This class provides a mechanism for ASN.1 elements (including sequences and
 * sets) from an input stream in a manner that allows the data to be decoded on
 * the fly without constructing {@link com.hwlcn.ldap.asn1.ASN1Element} objects if they are not
 * needed.  If any method in this class throws an {@code IOException}, then the
 * caller must close this reader and must not attempt to use it any more.
 * {@code ASN1StreamReader} instances are not threadsafe and must not be
 * accessed concurrently by multiple threads.
 */
@Mutable()
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class ASN1StreamReader
{
  // Indicates whether socket timeout exceptions should be ignored for the
  // initial read of an element.
  private boolean ignoreInitialSocketTimeout;

  // Indicates whether socket timeout exceptions should be ignored for
  // subsequent reads of an element.
  private boolean ignoreSubsequentSocketTimeout;

  // The input stream from which data will be read.
  private final InputStream inputStream;

  // The maximum element size that will be allowed.
  private final int maxElementSize;

  // The total number of bytes read from the underlying input stream.
  private long totalBytesRead;



  /**
   * Creates a new ASN.1 stream reader that will read data from the provided
   * input stream.  It will use a maximum element size of
   * {@code Integer.MAX_VALUE}.
   *
   * @param  inputStream  The input stream from which data should be read.  If
   *                      the provided input stream does not support the use of
   *                      the {@code mark} and {@code reset} methods, then it
   *                      will be wrapped with a {@code BufferedInputStream}.
   */
  public ASN1StreamReader(final InputStream inputStream)
  {
    this(inputStream, Integer.MAX_VALUE);
  }



  /**
   * Creates a new ASN.1 stream reader that will read data from the provided
   * input stream.  It will use a maximum element size of
   * {@code Integer.MAX_VALUE}.
   *
   * @param  inputStream     The input stream from which data should be read.
   *                         If the provided input stream does not support the
   *                         use of the {@code mark} and {@code reset} methods,
   *                         then it will be wrapped with a
   *                         {@code BufferedInputStream}.
   * @param  maxElementSize  The maximum size in bytes of an ASN.1 element that
   *                         may be read.  A value less than or equal to zero
   *                         will be interpreted as {@code Integer.MAX_VALUE}.
   */
  public ASN1StreamReader(final InputStream inputStream,
                          final int maxElementSize)
  {
    if (inputStream.markSupported())
    {
      this.inputStream = inputStream;
    }
    else
    {
      this.inputStream = new BufferedInputStream(inputStream);
    }

    if (maxElementSize > 0)
    {
      this.maxElementSize = maxElementSize;
    }
    else
    {
      this.maxElementSize = Integer.MAX_VALUE;
    }

    totalBytesRead                = 0L;
    ignoreInitialSocketTimeout    = false;
    ignoreSubsequentSocketTimeout = false;
  }



  /**
   * Closes this ASN.1 stream reader and the underlying input stream.  This
   * reader must not be used after it has been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while closing the underlying
   *                       input stream.
   */
  public void close()
         throws IOException
  {
    inputStream.close();
  }



  /**
   * Retrieves the total number of bytes read so far from the underlying input
   * stream.
   *
   * @return  The total number of bytes read so far from the underlying input
   *          stream.
   */
  long getTotalBytesRead()
  {
    return totalBytesRead;
  }



  /**
   * Indicates whether to ignore {@code java.net.SocketTimeoutException}
   * exceptions that may be caught during processing.
   *
   * @return  {@code true} if {@code SocketTimeoutException} exceptions should
   *          be ignored, or {@code false} if they should not be ignored and
   *          should be propagated to the caller.
   *
   * @deprecated  Use the {@link #ignoreInitialSocketTimeoutException()} and
   *              {@link #ignoreSubsequentSocketTimeoutException()} methods
   *              instead.
   */
  @Deprecated()
  public boolean ignoreSocketTimeoutException()
  {
    return ignoreInitialSocketTimeout;
  }



  /**
   * Indicates whether to ignore {@code java.net.SocketTimeoutException}
   * exceptions that may be caught while trying to read the first byte of an
   * element.
   *
   * @return  {@code true} if {@code SocketTimeoutException} exceptions should
   *          be ignored while trying to read the first byte of an element, or
   *          {@code false} if they should not be ignored and should be
   *          propagated to the caller.
   */
  public boolean ignoreInitialSocketTimeoutException()
  {
    return ignoreInitialSocketTimeout;
  }



  /**
   * Indicates whether to ignore {@code java.net.SocketTimeoutException}
   * exceptions that may be caught while trying to read subsequent bytes of an
   * element (after one or more bytes have already been read for that element).
   *
   * @return  {@code true} if {@code SocketTimeoutException} exceptions should
   *          be ignored while trying to read subsequent bytes of an element, or
   *          {@code false} if they should not be ignored and should be
   *          propagated to the caller.
   */
  public boolean ignoreSubsequentSocketTimeoutException()
  {
    return ignoreSubsequentSocketTimeout;
  }



  /**
   * Indicates whether to ignore {@code java.net.SocketTimeoutException}
   * exceptions that may be caught during processing.
   *
   * @param  ignoreSocketTimeout  Indicates whether to ignore
   *                              {@code SocketTimeoutException} exceptions that
   *                              may be caught during processing.
   *
   * @deprecated  Use the {@link #setIgnoreSocketTimeout(boolean,boolean)}
   *              method instead.
   */
  @Deprecated()
  public void setIgnoreSocketTimeout(final boolean ignoreSocketTimeout)
  {
    ignoreInitialSocketTimeout    = ignoreSocketTimeout;
    ignoreSubsequentSocketTimeout = ignoreSocketTimeout;
  }



  /**
   * Indicates whether to ignore {@code java.net.SocketTimeoutException}
   * exceptions that may be caught during processing.
   *
   * @param  ignoreInitialSocketTimeout     Indicates whether to ignore
   *                                        {@code SocketTimeoutException}
   *                                        exceptions that may be caught while
   *                                        trying to read the first byte of an
   *                                        element.
   * @param  ignoreSubsequentSocketTimeout  Indicates whether to ignore
   *                                        {@code SocketTimeoutException}
   *                                        exceptions that may be caught while
   *                                        reading beyond the first byte of an
   *                                        element.
   */
  public void setIgnoreSocketTimeout(final boolean ignoreInitialSocketTimeout,
                   final boolean ignoreSubsequentSocketTimeout)
  {
    this.ignoreInitialSocketTimeout    = ignoreInitialSocketTimeout;
    this.ignoreSubsequentSocketTimeout = ignoreSubsequentSocketTimeout;
  }



  /**
   * Peeks at the next byte to be read from the input stream without actually
   * consuming it.
   *
   * @return  An integer value encapsulating the BER type of the next element in
   *          the input stream, or -1 if the end of the input stream has been
   *          reached and there is no data to be read.  If a value of -1 is
   *          returned, then the input stream will not have been closed since
   *          this method is not intended to have any impact on the underlying
   *          input stream.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream.
   */
  public int peek()
         throws IOException
  {
    inputStream.mark(1);
    final int byteRead = read(true);
    inputStream.reset();

    return byteRead;
  }



  /**
   * Reads the BER type of the next element from the input stream.  This may not
   * be called if a previous element has been started but not yet completed.
   *
   * @return  An integer value encapsulating the BER type of the next element in
   *          the input stream, or -1 if the end of the input stream has been
   *          reached and there is no data to be read.  If a value of -1 is
   *          returned, then the input stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream.
   */
  private int readType()
          throws IOException
  {
    final int typeInt = read(true);
    if (typeInt < 0)
    {
      close();
    }
    else
    {
      totalBytesRead++;
    }
    return typeInt;
  }



  /**
   * Reads the length of the next element from the input stream.  This may only
   * be called after reading the BER type.
   *
   * @return  The length of the next element from the input stream.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the stream has been reached, or
   *                       if the decoded length is greater than the maximum
   *                       allowed length.
   */
  private int readLength()
          throws IOException
  {
    int length = read(false);
    if (length < 0)
    {
      throw new IOException(ERR_READ_END_BEFORE_FIRST_LENGTH.get());
    }

    totalBytesRead++;
    if (length > 127)
    {
      final int numLengthBytes = length & 0x7F;
      length = 0;
      if ((numLengthBytes < 1) || (numLengthBytes > 4))
      {
        throw new IOException(ERR_READ_LENGTH_TOO_LONG.get(numLengthBytes));
      }

      for (int i=0; i < numLengthBytes; i++)
      {
        final int lengthInt = read(false);
        if (lengthInt < 0)
        {
          throw new IOException(ERR_READ_END_BEFORE_LENGTH_END.get());
        }

        length <<= 8;
        length |= (lengthInt & 0xFF);
      }

      totalBytesRead += numLengthBytes;
    }

    if ((length < 0) || ((maxElementSize > 0) && (length > maxElementSize)))
    {
      throw new IOException(ERR_READ_LENGTH_EXCEEDS_MAX.get(length,
                                                            maxElementSize));
    }

    return length;
  }



  /**
   * Skips over the specified number of bytes.
   *
   * @param  numBytes  The number of bytes to skip.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, or if the end of the stream is reached before
   *                       having skipped the specified number of bytes.
   */
  private void skip(final int numBytes)
          throws IOException
  {
    if (numBytes <= 0)
    {
      return;
    }

    long totalBytesSkipped = inputStream.skip(numBytes);
    while (totalBytesSkipped < numBytes)
    {
      final long bytesSkipped = inputStream.skip(numBytes - totalBytesSkipped);
      if (bytesSkipped <= 0)
      {
        while (totalBytesSkipped < numBytes)
        {
          final int byteRead = read(false);
          if (byteRead < 0)
          {
            throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
          }
          totalBytesSkipped++;
        }
      }
      else
      {
        totalBytesSkipped += bytesSkipped;
      }
    }

    totalBytesRead += numBytes;
  }



  /**
   * Reads a complete ASN.1 element from the input stream.
   *
   * @return  The ASN.1 element read from the input stream, or {@code null} if
   *          the end of the input stream was reached before any data could be
   *          read.  If {@code null} is returned, then the input stream will
   *          have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   */
  public ASN1Element readElement()
         throws IOException
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();

    int valueBytesRead = 0;
    int bytesRemaining = length;
    final byte[] value = new byte[length];
    while (valueBytesRead < length)
    {
      final int bytesRead = read(false, value, valueBytesRead, bytesRemaining);
      if (bytesRead < 0)
      {
        throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
      }

      valueBytesRead += bytesRead;
      bytesRemaining -= bytesRead;
    }

    totalBytesRead += length;
    final ASN1Element e = new ASN1Element((byte) type, value);
    debugASN1Read(e);
    return e;
  }



  /**
   * Reads an ASN.1 Boolean element from the input stream and returns the value
   * as a {@code Boolean}.
   *
   * @return  The {@code Boolean} value of the ASN.1 Boolean element read, or
   *          {@code null} if the end of the input stream was reached before any
   *          data could be read.  If {@code null} is returned, then the input
   *          stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the data read cannot be parsed as an ASN.1
   *                         Boolean element.
   */
  public Boolean readBoolean()
         throws IOException, ASN1Exception
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();

    if (length == 1)
    {
      final int value = read(false);
      if (value < 0)
      {
        throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
      }

      totalBytesRead++;
      return (value != 0);
    }
    else
    {
      skip(length);
      throw new ASN1Exception(ERR_BOOLEAN_INVALID_LENGTH.get());
    }
  }



  /**
   * Reads an ASN.1 enumerated element from the input stream and returns the
   * value as an {@code Integer}.
   *
   * @return  The {@code Integer} value of the ASN.1 enumerated element read, or
   *          {@code null} if the end of the input stream was reached before any
   *          data could be read.  If {@code null} is returned, then the input
   *          stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the data read cannot be parsed as an ASN.1
   *                         enumerated element.
   */
  public Integer readEnumerated()
         throws IOException, ASN1Exception
  {
    return readInteger();
  }



  /**
   * Reads an ASN.1 integer element from the input stream and returns the value
   * as an {@code Integer}.
   *
   * @return  The {@code Integer} value of the ASN.1 integer element read, or
   *          {@code null} if the end of the input stream was reached before any
   *          data could be read.  If {@code null} is returned, then the input
   *          stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the data read cannot be parsed as an ASN.1
   *                         integer element.
   */
  public Integer readInteger()
         throws IOException, ASN1Exception
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();
    if ((length == 0) || (length > 4))
    {
      skip(length);
      throw new ASN1Exception(ERR_INTEGER_INVALID_LENGTH.get(length));
    }

    boolean negative = false;
    int intValue = 0;
    for (int i=0; i < length; i++)
    {
      final int byteRead = read(false);
      if (byteRead < 0)
      {
        throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
      }

      if (i == 0)
      {
        negative = ((byteRead & 0x80) != 0x00);
      }

      intValue <<= 8;
      intValue |= (byteRead & 0xFF);
    }

    if (negative)
    {
      switch (length)
      {
        case 1:
          intValue |= 0xFFFFFF00;
          break;
        case 2:
          intValue |= 0xFFFF0000;
          break;
        case 3:
          intValue |= 0xFF000000;
          break;
      }
    }

    totalBytesRead += length;
    return intValue;
  }



  /**
   * Reads an ASN.1 integer element from the input stream and returns the value
   * as a {@code Long}.
   *
   * @return  The {@code Long} value of the ASN.1 integer element read, or
   *          {@code null} if the end of the input stream was reached before any
   *          data could be read.  If {@code null} is returned, then the input
   *          stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the data read cannot be parsed as an ASN.1
   *                         integer element.
   */
  public Long readLong()
         throws IOException, ASN1Exception
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();
    if ((length == 0) || (length > 8))
    {
      skip(length);
      throw new ASN1Exception(ERR_LONG_INVALID_LENGTH.get(length));
    }

    boolean negative = false;
    long longValue = 0;
    for (int i=0; i < length; i++)
    {
      final int byteRead = read(false);
      if (byteRead < 0)
      {
        throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
      }

      if (i == 0)
      {
        negative = ((byteRead & 0x80) != 0x00);
      }

      longValue <<= 8;
      longValue |= (byteRead & 0xFFL);
    }

    if (negative)
    {
      switch (length)
      {
        case 1:
          longValue |= 0xFFFFFFFFFFFFFF00L;
          break;
        case 2:
          longValue |= 0xFFFFFFFFFFFF0000L;
          break;
        case 3:
          longValue |= 0xFFFFFFFFFF000000L;
          break;
        case 4:
          longValue |= 0xFFFFFFFF00000000L;
          break;
        case 5:
          longValue |= 0xFFFFFF0000000000L;
          break;
        case 6:
          longValue |= 0xFFFF000000000000L;
          break;
        case 7:
          longValue |= 0xFF00000000000000L;
          break;
      }
    }

    totalBytesRead += length;
    return longValue;
  }



  /**
   * Reads an ASN.1 null element from the input stream.  No value will be
   * returned but the null element will be consumed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   *
   * @throws  com.hwlcn.ldap.asn1.ASN1Exception  If the data read cannot be parsed as an ASN.1 null
   *                         element.
   */
  public void readNull()
         throws IOException, ASN1Exception
  {
    final int type = readType();
    if (type < 0)
    {
      return;
    }

    final int length = readLength();

    if (length != 0)
    {
      skip(length);
      throw new ASN1Exception(ERR_NULL_HAS_VALUE.get());
    }
  }



  /**
   * Reads an ASN.1 octet string element from the input stream and returns the
   * value as a byte array.
   *
   * @return  The byte array value of the ASN.1 octet string element read, or
   *          {@code null} if the end of the input stream was reached before any
   *          data could be read.  If {@code null} is returned, then the input
   *          stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   */
  public byte[] readBytes()
         throws IOException
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();

    int valueBytesRead = 0;
    int bytesRemaining = length;
    final byte[] value = new byte[length];
    while (valueBytesRead < length)
    {
      final int bytesRead = read(false, value, valueBytesRead, bytesRemaining);
      if (bytesRead < 0)
      {
        throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
      }

      valueBytesRead += bytesRead;
      bytesRemaining -= bytesRead;
    }

    totalBytesRead += length;
    return value;
  }



  /**
   * Reads an ASN.1 octet string element from the input stream and returns the
   * value as a {@code String} using the UTF-8 encoding.
   *
   * @return  The {@code String} value of the ASN.1 octet string element read,
   *          or {@code null} if the end of the input stream was reached before
   *          any data could be read.  If {@code null} is returned, then the
   *          input stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   */
  public String readString()
         throws IOException
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();

    int valueBytesRead = 0;
    int bytesRemaining = length;
    final byte[] value = new byte[length];
    while (valueBytesRead < length)
    {
      final int bytesRead = read(false, value, valueBytesRead, bytesRemaining);
      if (bytesRead < 0)
      {
        throw new IOException(ERR_READ_END_BEFORE_VALUE_END.get());
      }

      valueBytesRead += bytesRead;
      bytesRemaining -= bytesRead;
    }

    totalBytesRead += length;
    return toUTF8String(value);
  }



  /**
   * Reads the beginning of an ASN.1 sequence from the input stream and
   * returns a value that can be used to determine when the end of the sequence
   * has been reached.  Elements which are part of the sequence may be read from
   * this ASN.1 stream reader until the
   * {@link ASN1StreamReaderSequence#hasMoreElements} method returns
   * {@code false}.
   *
   * @return  An object which may be used to determine when the end of the
   *          sequence has been reached, or {@code null} if the end of the input
   *          stream was reached before any data could be read.  If {@code null}
   *          is returned, then the input stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   */
  public ASN1StreamReaderSequence beginSequence()
         throws IOException
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();

    return new ASN1StreamReaderSequence(this, (byte) type, length);
  }



  /**
   * Reads the beginning of an ASN.1 set from the input stream and returns a
   * value that can be used to determine when the end of the set has been
   * reached.  Elements which are part of the set may be read from this ASN.1
   * stream reader until the {@link ASN1StreamReaderSet#hasMoreElements} method
   * returns {@code false}.
   *
   * @return  An object which may be used to determine when the end of the set
   *          has been reached, or {@code null} if the end of the input stream
   *          was reached before any data could be read.  If {@code null} is
   *          returned, then the input stream will have been closed.
   *
   * @throws  java.io.IOException  If a problem occurs while reading from the input
   *                       stream, if the end of the input stream is reached in
   *                       the middle of the element, or or if an attempt is
   *                       made to read an element larger than the maximum
   *                       allowed size.
   */
  public ASN1StreamReaderSet beginSet()
         throws IOException
  {
    final int type = readType();
    if (type < 0)
    {
      return null;
    }

    final int length = readLength();

    return new ASN1StreamReaderSet(this, (byte) type, length);
  }



  /**
   * Reads a byte of data from the underlying input stream, optionally ignoring
   * socket timeout exceptions.
   *
   * @param  initial  Indicates whether this is the initial read for an element.
   *
   * @return  The byte read from the input stream, or -1 if the end of the
   *          input stream was reached.
   *
   * @throws  java.io.IOException  If a problem occurs while reading data.
   */
  private int read(final boolean initial)
          throws IOException
  {
    try
    {
      return inputStream.read();
    }
    catch (SocketTimeoutException ste)
    {
      debugException(Level.FINEST, ste);

      if ((initial && ignoreInitialSocketTimeout) ||
          ((! initial) && ignoreSubsequentSocketTimeout))
      {
        while (true)
        {
          try
          {
            return inputStream.read();
          }
          catch (SocketTimeoutException ste2)
          {
            debugException(Level.FINEST, ste2);
          }
        }
      }
      else
      {
        throw ste;
      }
    }
  }



  /**
   * Reads data from the underlying input stream, optionally ignoring socket
   * timeout exceptions.
   *
   * @param  initial  Indicates whether this is the initial read for an element.
   * @param  buffer   The buffer into which the data should be read.
   * @param  offset   The position at which to start placing the data that was
   *                  read.
   * @param  length   The maximum number of bytes to read.
   *
   * @return  The number of bytes read, or -1 if the end of the input stream
   *          was reached.
   *
   * @throws  java.io.IOException  If a problem occurs while reading data.
   */
  private int read(final boolean initial, final byte[] buffer, final int offset,
                   final int length)
          throws IOException
  {
    try
    {
      return inputStream.read(buffer, offset, length);
    }
    catch (SocketTimeoutException ste)
    {
      debugException(Level.FINEST, ste);
      if ((initial && ignoreInitialSocketTimeout) ||
          ((! initial) && ignoreSubsequentSocketTimeout))
      {
        while (true)
        {
          try
          {
            return inputStream.read(buffer, offset, length);
          }
          catch (SocketTimeoutException ste2)
          {
            debugException(Level.FINEST, ste2);
          }
        }
      }
      else
      {
        throw ste;
      }
    }
  }
}
