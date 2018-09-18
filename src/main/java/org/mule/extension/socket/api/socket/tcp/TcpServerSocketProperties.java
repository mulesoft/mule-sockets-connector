/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.socket.tcp;

import static org.mule.runtime.extension.api.annotation.param.display.Placement.ADVANCED_TAB;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.net.ServerSocket;
import java.util.Objects;

/**
 * Default immutable implementation of the {@code TcpServerSocketProperties} interface.
 *
 * @since 1.0
 */
@TypeDsl(allowTopLevelDefinition = true)
public class TcpServerSocketProperties extends AbstractTcpSocketProperties {

  /**
   * This sets the SO_TIMEOUT value when the socket is used as a server. This is the timeout that applies to the "accept"
   * operation. A value of 0 (the {@link ServerSocket} default) causes the accept to wait indefinitely (if no connection arrives).
   */
  @Parameter
  @Optional
  @Placement(tab = TIMEOUT_CONFIGURATION)
  @Summary("Sets the 'SO_TIMEOUT' value when the socket is used as a server")
  private Integer serverTimeout;

  /**
   * The maximum queue length for incoming connections.
   */
  @Parameter
  @Optional(defaultValue = "50")
  @Placement(tab = ADVANCED_TAB)
  @Summary("The maximum queue length for incoming connections")
  private int receiveBacklog = 50;

  /**
   * The maximum queue length for incoming connections.
   */
  public int getReceiveBacklog() {
    return receiveBacklog;
  }

  /**
   * Sets the SO_TIMEOUT value when the socket is used as a server. Reading from the socket will block for up to this long (in
   * milliseconds) before the read fails. A value of 0 (the {@link ServerSocket} default) causes the read to wait indefinitely (if
   * no data arrives).
   */
  public Integer getServerTimeout() {
    return serverTimeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    TcpServerSocketProperties that = (TcpServerSocketProperties) o;

    if (receiveBacklog != that.receiveBacklog)
      return false;
    return Objects.equals(serverTimeout, that.serverTimeout);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (serverTimeout != null ? serverTimeout.hashCode() : 0);
    result = 31 * result + receiveBacklog;
    return result;
  }
}
