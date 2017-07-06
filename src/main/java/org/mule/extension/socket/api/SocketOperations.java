/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api;

import org.mule.extension.socket.api.client.SocketClient;
import org.mule.extension.socket.api.config.RequesterConfig;
import org.mule.extension.socket.api.connection.RequesterConnection;
import org.mule.extension.socket.api.exceptions.SocketsErrorTypeProvider;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic set of operations for socket extension
 *
 * @since 1.0
 */
@Throws(SocketsErrorTypeProvider.class)
public class SocketOperations {

  /**
   * Sends the data using the client associated to the {@link RequesterConnection} and
   * then blocks until a response is received or the timeout is met, in which case the
   * operation will return a {@code null} payload.
   *
   * @param content        data that will be serialized and sent through the socket.
   * @throws ConnectionException if the connection couldn't be established, if the remote host was unavailable.
   */
  public Result<InputStream, SocketAttributes> sendAndReceive(@Connection RequesterConnection connection,
                                                              @Content InputStream content)
      throws ConnectionException, IOException {
    SocketClient client = connection.getClient();
    client.write(content);

    return Result.<InputStream, SocketAttributes>builder()
        .output(client.read())
        .attributes(client.getAttributes())
        .build();
  }

  /**
   * Sends the data using the client associated to the {@link RequesterConnection}.
   *
   * @param content        data that will be serialized and sent through the socket.
   * @throws ConnectionException if the connection couldn't be established, if the remote host was unavailable.
   */
  public void send(@Connection RequesterConnection connection,
                   @Content InputStream content)
      throws ConnectionException, IOException {
    connection.getClient().write(content);
  }
}
