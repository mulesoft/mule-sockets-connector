/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.socket.tcp;

import org.mule.extension.socket.api.stereotype.TcpClientSocketStereotype;
import org.mule.runtime.extension.api.annotation.Extensible;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.mule.runtime.extension.api.annotation.param.stereotype.Stereotype;

/**
 * Default immutable implementation of the {@code TcpClientSocketProperties} interface.
 *
 * @since 1.0
 */
@Extensible
@TypeDsl(allowTopLevelDefinition = true)
@Stereotype(TcpClientSocketStereotype.class)
public class TcpClientSocketProperties extends AbstractTcpSocketProperties {

  /**
   * Number of milliseconds to wait until an outbound connection to a remote server is successfully created. Defaults to 30
   * seconds.
   */
  @Parameter
  @Optional(defaultValue = "30000")
  @Placement(tab = TIMEOUT_CONFIGURATION)
  @Summary("Time to wait during a connection to a remote server before failing with a timeout")
  private int connectionTimeout = 30000;

  /**
   * Number of milliseconds to wait until an outbound connection to a remote server is successfully created. Defaults to 30
   * seconds.
   */
  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;

    TcpClientSocketProperties that = (TcpClientSocketProperties) o;

    return connectionTimeout == that.connectionTimeout;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + connectionTimeout;
    return result;
  }
}
