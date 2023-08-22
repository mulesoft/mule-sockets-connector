/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.exceptions;

import static org.mule.extension.socket.api.exceptions.SocketError.UNKNOWN_HOST;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.net.InetSocketAddress;

/**
 * Thrown to indicate an error resolving an {@link InetSocketAddress}
 *
 * @since 1.0
 */
public class UnresolvableHostException extends ModuleException {

  public UnresolvableHostException(String message) {
    super(message, UNKNOWN_HOST);
  }
}
