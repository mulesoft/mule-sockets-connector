/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.stereotype;

import static java.util.Optional.empty;

import org.mule.runtime.extension.api.stereotype.StereotypeDefinition;

import java.util.Optional;

/**
 * {@link StereotypeDefinition} for a generic {@code TcpClientSocketProperties}.
 *
 * @since 1.2, 1.1.6
 */
public class TcpClientSocketStereotype implements StereotypeDefinition {

  @Override
  public String getName() {
    return "TCP_CLIENT_SOCKET_PROPS";
  }

  @Override
  public Optional<StereotypeDefinition> getParent() {
    return empty();
  }

}
