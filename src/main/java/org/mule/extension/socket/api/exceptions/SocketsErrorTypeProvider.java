/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.exceptions;

import static org.mule.extension.socket.api.exceptions.SocketError.CONNECTION_TIMEOUT;
import static org.mule.extension.socket.api.exceptions.SocketError.LENGTH_EXCEEDED;
import static org.mule.extension.socket.api.exceptions.SocketError.UNKNOWN_HOST;
import org.mule.extension.socket.api.SocketOperations;
import org.mule.extension.socket.api.config.RequesterConfig;
import org.mule.extension.socket.api.connection.RequesterConnection;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * Errors that can be thrown both in
 * {@link SocketOperations#send(RequesterConnection, RequesterConfig, Object, String)}  or
 * {@link SocketOperations#sendAndReceive(RequesterConnection, RequesterConfig, Object, String)}  operation.
 *
 * @since 1.0
 */
public class SocketsErrorTypeProvider implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return ImmutableSet.<ErrorTypeDefinition>builder()
        .add(LENGTH_EXCEEDED)
        .add(CONNECTION_TIMEOUT)
        .add(UNKNOWN_HOST)
        .build();
  }
}

