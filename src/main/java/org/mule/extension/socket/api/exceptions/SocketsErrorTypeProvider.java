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
import org.mule.extension.socket.api.connection.RequesterConnection;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Errors that can be thrown both in
 * {@link SocketOperations#send(RequesterConnection, java.io.InputStream)}  or
 * {@link SocketOperations#sendAndReceive(RequesterConnection, java.io.InputStream)}  operation.
 *
 * @since 1.0
 */
public class SocketsErrorTypeProvider implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(LENGTH_EXCEEDED, CONNECTION_TIMEOUT, UNKNOWN_HOST)));
  }
}

