/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import org.junit.Test;
import org.mockito.Mockito;
import org.mule.extension.socket.internal.SocketUtils;
import org.slf4j.Logger;
import java.io.InputStream;

public class TcpLoggerForInputStreamTestCase {

  @Test
  public void testLogIfDebugEnabled() throws Exception {
    InputStream inputStream = Mockito.mock(InputStream.class);
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(true);
    SocketUtils.logIfDebugEnabled(inputStream, logger);

    Mockito.verify(logger, Mockito.atLeastOnce()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                                                        Mockito.anyString());
  }
}
