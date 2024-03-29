/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.connection.tcp.protocol;

import static java.lang.String.format;
import static org.mule.extension.socket.internal.SocketUtils.logIfDebugEnabled;
import static org.mule.runtime.core.api.util.IOUtils.toByteArray;

import org.mule.extension.socket.api.exceptions.LengthExceededException;
import org.mule.extension.socket.api.socket.tcp.TcpProtocol;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * This protocol is an application level {@link TcpProtocol} that can be used to transfer large amounts of data without risking
 * some data to be loss. The protocol is defined by sending or reading an integer (the packet length) and then the data to transfer.
 * <p>
 * <p>
 * Note that use of this protocol must be symmetric - both the sending and receiving connectors must use the same protocol.
 * </p>
 *
 * @since 1.0
 */
@TypeDsl(allowTopLevelDefinition = true)
public class LengthProtocol extends DirectProtocol {

  public static final String LENGTH_EXCEEDED = "Message length is '%d' and exceeds the limit '%d";
  private static final int SIZE_INT = Integer.BYTES;
  /**
   * Indicates the maximum length of the message
   */
  @Parameter
  @Optional(defaultValue = "-1")
  private int maxMessageLength = NO_MAX_LENGTH;
  private static final Logger LOGGER = LoggerFactory.getLogger(LengthProtocol.class);

  public LengthProtocol() {
    this(NO_MAX_LENGTH);
  }

  public LengthProtocol(int maxMessageLength) {
    super(NO_STREAM, SIZE_INT);
    this.maxMessageLength = maxMessageLength;
  }

  /**
   * It consumes the socket {@link InputStream} according to {@link this#consume(InputStream)} and then wraps the result into a
   * {@link ByteArrayInputStream}.
   *
   * @param socketIs
   * @return {@code null} if {@link this#consume(InputStream)} throws an {@link IOException} and {@code rethrowExceptionOnRead} is
   *         not set
   * @throws IOException is thrown if {@code rethrowExceptionOnRead} is set
   */
  public InputStream read(InputStream socketIs) throws IOException {
    return nullIfEmptyArray(consume(socketIs));
  }

  /**
   * It first reads the size of an int in bytes from the {@link InputStream} that represents the total length of the data to be
   * read.
   *
   * @param inputStream to be read
   * @return a byte array with the data read from the {@code inputStream}
   * @throws LengthExceededException if the length of the message to be read exceeds the {@code maxMessageLength} set
   */
  private byte[] consume(InputStream inputStream) throws IOException {
    DataInputStream dis = new DataInputStream(inputStream);
    dis.mark(SIZE_INT);

    if (null == super.consume(dis, SIZE_INT)) {
      throw new IOException("Length Protocol could not read ");
    }

    dis.reset();
    int length = dis.readInt();

    if (length < 0 || (maxMessageLength > 0 && length > maxMessageLength)) {
      throw new LengthExceededException(format(LENGTH_EXCEEDED, length, maxMessageLength));
    }

    // finally read the rest of the data
    byte[] buffer = new byte[length];
    dis.readFully(buffer);

    logIfDebugEnabled(buffer, LOGGER);

    return buffer;
  }

  /**
   * It first writes the an int representing the length of the data to be written, and then writes the actual data.
   *
   * @param os in which the {@code data} will be written
   * @param inputStream to be written
   * @throws LengthExceededException if the length of the message to be written exceeds the {@code maxMessageLength} set
   */
  @Override
  public void write(OutputStream os, InputStream inputStream) throws IOException {
    byte[] data = toByteArray(inputStream);

    if (maxMessageLength > 0 && data.length > maxMessageLength) {
      throw new LengthExceededException(format("Message length is '%d' and exceeds the limit '%d", data.length,
                                               maxMessageLength));
    }

    DataOutputStream dataOutputStream = new DataOutputStream(os);
    dataOutputStream.writeInt(data.length);
    dataOutputStream.write(data);

    logIfDebugEnabled(data, LOGGER);

    if (dataOutputStream.size() != data.length + SIZE_INT) {
      dataOutputStream.flush();
    }
  }

  /**
   * Read all four bytes for initial integer (limit is set in read)
   *
   * @param len Amount transferred last call (-1 on EOF or socket error)
   * @param available Amount available
   * @return true if the transfer should continue
   */
  @Override
  protected boolean isRepeat(int len, int available) {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    LengthProtocol that = (LengthProtocol) o;
    return maxMessageLength == that.maxMessageLength;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), maxMessageLength);
  }
}
