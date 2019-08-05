/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.socket.tcp;

import org.mule.extension.socket.api.SocketsExtension;
import org.mule.extension.socket.api.stereotype.TcpProtocolStereotype;
import org.mule.runtime.extension.api.annotation.param.stereotype.Stereotype;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface enables to plug different application level protocols into the {@link SocketsExtension}.
 *
 * @since 1.0
 */
@Stereotype(TcpProtocolStereotype.class)
public interface TcpProtocol {

  /**
   * Reads the input stream and returns a whole message.
   *
   * @param is the input stream
   * @return an {@link InputStream} containing a full message
   * @throws IOException if an exception occurs
   */
  InputStream read(InputStream is) throws IOException;

  /**
   * Write the specified message into the {@link OutputStream}.
   *
   * @param os the {@link OutputStream} in which the data is going to be written
   * @param data data to be written
   * @throws IOException if an exception occurs
   */
  void write(OutputStream os, InputStream data) throws IOException;
}
