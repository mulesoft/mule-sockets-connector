/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.exceptions;

import org.mule.extension.socket.api.SocketsExtension;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.MuleErrors;

import java.util.Optional;

/**
 * Errors definitions for {@link SocketsExtension}
 *
 * @since 1.0
 */
public enum SocketError implements ErrorTypeDefinition<SocketError> {
  CONNECTIVITY(MuleErrors.CONNECTIVITY), UNKNOWN_HOST(
      CONNECTIVITY), CONNECTION_TIMEOUT(CONNECTIVITY), LENGTH_EXCEEDED;

  private ErrorTypeDefinition<? extends Enum<?>> parent;

  SocketError(ErrorTypeDefinition<? extends Enum<?>> parent) {
    this.parent = parent;
  }

  SocketError() {}

  @Override
  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
    return Optional.ofNullable(parent);
  }
}
