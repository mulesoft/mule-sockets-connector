/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.internal;

import static java.lang.String.format;
import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.extension.socket.api.ImmutableSocketAttributes;
import org.mule.extension.socket.api.connection.AbstractSocketConnection;
import org.mule.extension.socket.api.exceptions.UnresolvableHostException;
import org.mule.extension.socket.api.socket.tcp.TcpSocketProperties;
import org.mule.extension.socket.api.socket.udp.UdpSocketProperties;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public final class SocketUtils {

  private SocketUtils() {

  }

  private static final String SOCKET_COULD_NOT_BE_CREATED = "%s Socket could not be created correctly";
  public static final String WORK = "work";

  /**
   * @param connection delegates connectin's validation on {@link AbstractSocketConnection#validate()}
   * @return a {@link ConnectionValidationResult} with the outcome of the validation
   */
  public static ConnectionValidationResult validate(AbstractSocketConnection connection) {
    return connection.validate();
  }

  public static Result<InputStream, ImmutableSocketAttributes> createResult(InputStream content,
                                                                            ImmutableSocketAttributes attributes) {
    return Result.<InputStream, ImmutableSocketAttributes>builder().output(content).attributes(attributes).build();
  }

  /**
   * Creates a {@link DatagramPacket} with the size of the content, addressed to the port and address of the client.
   *
   * @param content that is going to be sent inside the packet
   * @return a packet ready to be sent
   * @throws UnresolvableHostException
   */
  public static DatagramPacket createPacket(byte[] content) throws UnresolvableHostException {
    return new DatagramPacket(content, content.length);
  }

  /**
   * Creates a {@link DatagramPacket} with with the content {@link byte[]} but with the {@code dataLength} size, addressed to the port and address of the client.
   *
   * @param content that is going to be sent inside the packet
   * @param dataLength size of the content to be send. It may differ from {@code content.size}
   * @return a packet ready to be sent
   * @throws UnresolvableHostException
   */
  public static DatagramPacket createPacket(byte[] content, int dataLength) throws UnresolvableHostException {
    return new DatagramPacket(content, dataLength);
  }

  /**
   * @return a packet configured to be used for receiving purposes
   * @throws UnresolvableHostException
   */
  public static DatagramPacket createPacket(int bufferSize) throws UnresolvableHostException {
    return new DatagramPacket(new byte[bufferSize], bufferSize);
  }


  /**
   * Sets the configuration parameters into the socket.
   *
   * @param socket UDP Socket
   * @param socketProperties Configuration properties
   * @throws ConnectionException
   */
  public static void configureConnection(DatagramSocket socket, UdpSocketProperties socketProperties) throws ConnectionException {
    checkArgument(socket != null, "Null socket found. UDP Socket must be created before being configured");

    try {
      if (socketProperties.getSendBufferSize() != null) {
        socket.setSendBufferSize(socketProperties.getSendBufferSize());
      }

      if (socketProperties.getReceiveBufferSize() != null) {
        socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
      }

      if (socketProperties.getClientTimeout() != null) {
        socket.setSoTimeout(socketProperties.getClientTimeout());
      }

      socket.setBroadcast(socketProperties.getBroadcast());
      socket.setReuseAddress(socketProperties.getReuseAddress());
    } catch (Exception e) {
      throw new ConnectionException(format(SOCKET_COULD_NOT_BE_CREATED, "UDP"), e);
    }
  }

  /**
   * Sets the configuration parameters into the socket
   *
   * @param socket TCP Socket
   * @param socketProperties Configuration properties
   * @throws ConnectionException
   */
  public static void configureConnection(Socket socket, TcpSocketProperties socketProperties) throws ConnectionException

  {
    try {
      if (socketProperties.getSendBufferSize() != null) {
        socket.setSendBufferSize(socketProperties.getSendBufferSize());
      }

      if (socketProperties.getReceiveBufferSize() != null) {
        socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
      }

      if (socketProperties.getClientTimeout() != null) {
        socket.setSoTimeout(socketProperties.getClientTimeout());
      }

      if (socketProperties.getKeepAlive()) {
        socket.setKeepAlive(socketProperties.getKeepAlive());
      }

      if (socketProperties.getLinger() != null) {
        socket.setSoLinger(true, socketProperties.getLinger());
      }
    } catch (SocketException e) {
      throw new ConnectionException(format(SOCKET_COULD_NOT_BE_CREATED, "TCP"), e);
    }

    try {
      socket.setTcpNoDelay(socketProperties.getSendTcpNoDelay());
    } catch (SocketException e) {
      // MULE-2800 - Bug in Solaris
    }
  }

  /**
   *
   * @param inputStream data that's going to be sent through the {@link DatagramSocket}
   * @param bufferSize size of the buffer used for reading the {@code inputStream}
   * @param address {@link SocketAddress} to which the data is going to be send towards. 
   * @param socket used for sending the data
   * @throws IOException if {@link InputStream#read()} or {@link DatagramSocket#send(DatagramPacket)} fails.
   */
  public static void sendUdpPackages(InputStream inputStream, int bufferSize, SocketAddress address, DatagramSocket socket)
      throws IOException {
    byte[] buffer = new byte[bufferSize];
    int chunkLen = 0;
    while ((chunkLen = inputStream.read(buffer, 0, buffer.length)) != -1) {
      DatagramPacket sendPacket = createPacket(buffer, chunkLen);
      sendPacket.setSocketAddress(address);
      socket.send(sendPacket);
    }
  }
}
