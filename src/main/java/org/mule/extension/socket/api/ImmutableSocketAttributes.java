/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.security.cert.Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Immutable implementation of {@link SocketAttributes}.
 *
 * @since 1.0
 */
public class ImmutableSocketAttributes implements SocketAttributes, Serializable {

  private static final long serialVersionUID = 1991548360970880784L;
  private static final Logger LOGGER = LoggerFactory.getLogger(ImmutableSocketAttributes.class);

  @Parameter
  private int port;

  @Parameter
  private String hostAddress;

  @Parameter
  private String hostName;

  /**
   *  The SSL local certificates. If the Socket is not an SSL Sockets, this will be null.
   */
  @Parameter
  @Optional
  private Certificate[] localCertificates;

  /**
   *  The SSL peer certificates. If the Socket is not an SSL Sockets, this will be null.
   */
  @Parameter
  @Optional
  private Certificate[] peerCertificates;

  /**
   * Creates a new instance
   *
   * @param socket TCP {@link Socket} connection with the remote host
   */
  public ImmutableSocketAttributes(Socket socket) {
    fromInetAddress(socket.getPort(), socket.getInetAddress());

    if (socket instanceof SSLSocket) {
      try {
        SSLSocket sslSocket = (SSLSocket) socket;
        // getSession tries to set up a session if there is no currently valid session, and an implicit handshake is done
        SSLSession sslSession = sslSocket.getSession();
        localCertificates = sslSession.getLocalCertificates();
        peerCertificates = sslSession.getPeerCertificates();
      } catch (SSLPeerUnverifiedException e) {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Error obtaining SSLSocket attributes", e);
        }
      }
    }

  }

  /**
   * Creates a new instance
   *
   * @param socket UDP {@link DatagramSocket} connection with the remote host
   */
  public ImmutableSocketAttributes(DatagramSocket socket) {
    fromInetAddress(socket.getPort(), socket.getInetAddress());
  }

  /**
   * Creates a new instance
   *
   * @param packet UDP {@link DatagramPacket} received from remote host
   */
  public ImmutableSocketAttributes(DatagramPacket packet) {
    this(packet.getPort(), packet.getAddress().getHostAddress(), packet.getAddress().getHostName());
  }

  private void fromInetAddress(int port, InetAddress address) {
    this.port = port;

    if (address != null) {
      this.hostName = address.getHostName();
      this.hostAddress = address.getHostAddress();
    }

    if (hostName == null) {
      hostName = "";
    }

    if (hostAddress == null) {
      hostName = "";
    }
  }

  public ImmutableSocketAttributes(int remotePort, String remoteHostAddress, String remoteHostName) {
    this.port = remotePort;
    this.hostAddress = remoteHostAddress == null ? "" : remoteHostAddress;
    this.hostName = remoteHostName == null ? "" : remoteHostName;
  }

  @Override
  public String toString() {
    return "ImmutableSocketAttributes[port=" + this.port + ",hostAddress=" + this.hostAddress + ",hostName=" + this.hostName
        + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPort() {
    return port;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getHostAddress() {
    return hostAddress;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getHostName() {
    return hostName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Certificate[] getLocalCertificates() {
    return localCertificates;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Certificate[] getPeerCertificates() {
    return peerCertificates;
  }
}
