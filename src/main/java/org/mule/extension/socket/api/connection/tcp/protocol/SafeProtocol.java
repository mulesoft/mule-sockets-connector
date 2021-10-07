/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.connection.tcp.protocol;

import org.mule.extension.socket.api.socket.tcp.TcpProtocol;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.DefaultEncoding;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * This test protocol is an application level {@link TcpProtocol} and precedes every message with a cookie.
 * The protocol should not be used in production environments.
 * Both sender and receiver must use the same protocol.
 *
 * @since 1.0
 */
@TypeDsl(allowTopLevelDefinition = true)
public class SafeProtocol extends AbstractByteProtocol {

  public static final String COOKIE = "You are using SafeProtocol";

  private final TcpProtocol cookieProtocol = new LengthProtocol(COOKIE.length());
  private TcpProtocol delegate;

  @DefaultEncoding
  private String muleEncoding;

  /**
   * Indicates the maximum length of the message
   */
  @Parameter
  @Optional(defaultValue = "-1")
  private int maxMessageLeght = NO_MAX_LENGTH;
  /**
   * Indicates the encoding used for serializing the cookie
   */
  @Parameter
  @Optional
  private String encoding;

  public SafeProtocol() {
    super(false);
    delegate = new LengthProtocol();
  }

  public String getEncoding() {
    return encoding == null ? muleEncoding : encoding;
  }

  /**
   * Reads the actual data only after assuring that the cookie was preceding the message.
   *
   * @param inputStream
   * @return {@code null} if the cookie could not be successfully received.
   * @throws IOException
   */
  public InputStream read(InputStream inputStream) throws IOException {
    if (assertSiblingSafe(inputStream)) {
      InputStream result = delegate.read(inputStream);
      if (null == result) {
        // EOF after cookie but before data
        helpUser();
      }
      return result;
    } else {
      throw new IOException("Safe protocol failed while asserting message prefix");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(OutputStream os, InputStream data) throws IOException {
    assureSibling(os);
    delegate.write(os, data);
  }

  /**
   * Writes COOKIE message into #{code outputStream}. It should be invoked before sending the actual data.
   *
   * @param outputStream
   * @throws IOException
   */
  private void assureSibling(OutputStream outputStream) throws IOException {
    cookieProtocol.write(outputStream, new ByteArrayInputStream(COOKIE.getBytes(getEncoding())));
  }

  /**
   * Reads from #{code inputStream} and checks that the COOKIE message was received successfully.
   *
   * @param inputStream Stream to read data from
   * @return true if further data are available; false if EOF
   * @throws IOException
   */
  private boolean assertSiblingSafe(InputStream inputStream) throws IOException {
    Object cookie = null;
    try {
      cookie = cookieProtocol.read(inputStream);
    } catch (Exception e) {
      helpUser(e);
    }
    if (null != cookie) {
      String parsedCookie = IOUtils.toString((InputStream) cookie, getEncoding());
      if (parsedCookie.length() != COOKIE.length() || !COOKIE.equals(parsedCookie)) {
        helpUser();
      } else {
        return true;
      }
    }
    return false; // eof
  }

  private void helpUser() throws IOException {
    throw new IOException("You are not using a consistent protocol on your TCP transport. "
        + "Please read the documentation for the TCP transport, " + "paying particular attention to the protocol parameter.");
  }

  private void helpUser(Exception e) throws IOException {
    throw (IOException) new IOException("An error occurred while verifying your connection.  "
        + "You may not be using a consistent protocol on your TCP transport. "
        + "Please read the documentation for the TCP transport, " + "paying particular attention to the protocol parameter.")
            .initCause(e);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    SafeProtocol that = (SafeProtocol) o;
    return maxMessageLeght == that.maxMessageLeght &&
        Objects.equals(cookieProtocol, that.cookieProtocol) &&
        Objects.equals(delegate, that.delegate) &&
        Objects.equals(muleEncoding, that.muleEncoding) &&
        Objects.equals(encoding, that.encoding);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), cookieProtocol, delegate, muleEncoding, maxMessageLeght, encoding);
  }
}
