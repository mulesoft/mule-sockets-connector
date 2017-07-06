/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.exceptions;

import static org.mule.extension.socket.api.exceptions.SocketError.LENGTH_EXCEEDED;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * Thrown to indicate that there was an attempt of reading or writing more bytes that the allowed limit.
 *
 * @since 1.0
 */
public class LengthExceededException extends ModuleException {

  public LengthExceededException(String message) {
    super(message, LENGTH_EXCEEDED);
  }
}
