/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.exceptions;

import static org.mule.extension.socket.api.exceptions.SocketError.CONNECTION_TIMEOUT;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * Thrown to indicate that a timeout has been reached while awaiting for new data to arrive and be read.
 *
 * @since 1.0
 */
public class ReadingTimeoutException extends ModuleException {

  public ReadingTimeoutException(String message, Throwable cause) {
    super(message, CONNECTION_TIMEOUT, cause);
  }
}
