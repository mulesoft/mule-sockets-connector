/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.protocol;

import static java.lang.System.arraycopy;
import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.mule.extension.socket.api.socket.tcp.TcpProtocol;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedOutputStream;

public class CustomTestTcpProtocol implements TcpProtocol {

  protected static final int READ_ATTEMPTS = 50;
  protected static final int CUSTOM_BUFFER_SIZE = 30;
  private static final String HEADER = "This is my custom protocol.";
  private static final Logger LOGGER = getLogger(CustomTestTcpProtocol.class);

  protected int bufferSize;

  public CustomTestTcpProtocol() {
    this(CUSTOM_BUFFER_SIZE);
  }

  public CustomTestTcpProtocol(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  @Override
  public InputStream read(InputStream socketIs) throws IOException {
    byte[] buffer = new byte[HEADER.length() + bufferSize];

    int bytesRead, attempts = 0;
    while ((bytesRead = socketIs.read(buffer)) <= 0 && (attempts < READ_ATTEMPTS)) {
      attempts++;
    }

    if (bytesRead <= 0) {
      throw new IOException("Number of read attempts exceeded! Failed to read any data from socket!");
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytesRead);
    byteArrayOutputStream.write(buffer, 0, bytesRead);
    return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
  }

  @Override
  public void write(OutputStream os, InputStream data) throws IOException {
    byte[] messageToSend = new byte[HEADER.length() + bufferSize];
    byte[] inputPayload = new byte[bufferSize];

    int dataLength = data.read(inputPayload);
    if (dataLength == bufferSize) {
      LOGGER.warn("Data length exceeds buffer size so data will be chunked.");
    }

    arraycopy(HEADER.getBytes(), 0, messageToSend, 0, HEADER.length());

    if (dataLength >= 0) {
      arraycopy(inputPayload, 0, messageToSend, HEADER.length(), dataLength);
    }

    try (BufferedOutputStream writer = new BufferedOutputStream(os)) {
      writer.write(messageToSend, 0, HEADER.length() + dataLength);
      writer.flush();
    }
  }
}
