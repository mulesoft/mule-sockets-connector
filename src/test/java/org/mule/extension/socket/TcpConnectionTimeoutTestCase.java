/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.mule.extension.socket.api.exceptions.SocketError.CONNECTIVITY;
import static org.mule.functional.api.exception.ExpectedError.none;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.api.connection.ConnectionException;

import org.junit.Rule;
import org.junit.Test;

public class TcpConnectionTimeoutTestCase extends SocketExtensionTestCase {

  @Rule
  public ExpectedError expectedError = none();

  @Override
  protected String getConfigFile() {
    return "tcp-connection-timeout-config.xml";
  }

  @Test
  public void socketConnectionTimeout() throws Exception {
    expectedError.expectError("SOCKETS", CONNECTIVITY, ConnectionException.class, "Could not connect TCP requester socket");
    flowRunner("tcp-connection-timeout").withPayload(TEST_STRING).run();
  }
}
