/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.socket.tcp;

import org.mule.extension.socket.api.socket.AbstractSocketProperties;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.sdk.api.annotation.semantics.connectivity.ExcludeFromConnectivitySchema;

import java.net.Socket;
import java.util.Objects;

/**
 * Common configuration fields for TCP {@link Socket}
 *
 * @since 1.0
 */
public abstract class AbstractTcpSocketProperties extends AbstractSocketProperties implements TcpSocketProperties {

  /**
   * If set, transmitted data is not collected together for greater efficiency but sent immediately.
   * <p>
   * Defaults to {@code true} even though {@link Socket} default is false because optimizing to reduce amount of network traffic
   * over latency is hardly ever a concern today.
   */
  @Parameter
  @Optional(defaultValue = "true")
  @Summary("Indicates whether the transmitted data should not be collected together for greater efficiency, and sent immediately")
  @DisplayName("Send TCP With No Delay")
  @ExcludeFromConnectivitySchema
  protected boolean sendTcpNoDelay;


  /**
   * This sets the SO_LINGER value. This is related to how long (in milliseconds) the socket will take to close so that any
   * remaining data is transmitted correctly.
   */
  @Parameter
  @Optional
  @Summary("This indicates for how long, in milliseconds, the socket will take to close so any remaining data is"
      + "transmitted correctly")
  @ExcludeFromConnectivitySchema
  protected Integer linger;

  /**
   * Enables SO_KEEPALIVE behavior on open sockets. This automatically checks socket connections that are open but unused for long
   * periods and closes them if the connection becomes unavailable.
   * <p>
   * This is a property on the socket itself and is used by a server socket to control whether connections to the server are kept
   * alive before they are recycled.
   */
  @Parameter
  @Optional(defaultValue = "false")
  @Summary("Indicates whether the open socket connections unused for a long period and with an unavailable connection "
      + "should be closed")
  @ExcludeFromConnectivitySchema
  protected boolean keepAlive;


  /**
   * Whether the socket should fail during its creation if the host set on the endpoint cannot be resolved. However, it can be set
   * to false to allow unresolved hosts (useful when connecting through a proxy).
   */
  @Parameter
  @Optional(defaultValue = "true")
  @Summary("Whether the socket should fail during its creation if the host set on the endpoint cannot be resolved")
  @ExcludeFromConnectivitySchema
  protected boolean failOnUnresolvedHost;

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getLinger() {
    return linger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getKeepAlive() {
    return keepAlive;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getSendTcpNoDelay() {
    return sendTcpNoDelay;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getFailOnUnresolvedHost() {
    return failOnUnresolvedHost;

  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    AbstractTcpSocketProperties that = (AbstractTcpSocketProperties) o;

    if (sendTcpNoDelay != that.sendTcpNoDelay)
      return false;
    if (keepAlive != that.keepAlive)
      return false;
    if (failOnUnresolvedHost != that.failOnUnresolvedHost)
      return false;
    return Objects.equals(linger, that.linger);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (sendTcpNoDelay ? 1 : 0);
    result = 31 * result + Objects.hashCode(linger);
    result = 31 * result + (keepAlive ? 1 : 0);
    result = 31 * result + (failOnUnresolvedHost ? 1 : 0);
    return result;
  }
}
