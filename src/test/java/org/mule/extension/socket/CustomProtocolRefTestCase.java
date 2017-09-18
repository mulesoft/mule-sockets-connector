/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.mule.extension.socket.api.socket.tcp.TcpProtocol;

import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

public class CustomProtocolRefTestCase extends SocketExtensionTestCase {

  @Inject
  @Named("myProtocolRef")
  private TcpProtocol protocol;

  @Override
  protected boolean doTestClassInjection() {
    return true;
  }

  @Override
  protected String getConfigFile() {
    return "custom-protocol-ref-config.xml";
  }

  @Test
  public void useCustomProtocolClass() throws Exception {
    expectedException.expect(UnsupportedOperationException.class);

    assertThat(protocol, is(not(nullValue())));

    // throws UnsupportedOperationException
    protocol.write(null, null);
  }
}
