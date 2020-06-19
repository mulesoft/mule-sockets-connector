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

  private final String SOME_TEXT = "Some Text";

  @Test
  public void testLogIfDebugEnabledForInputStream() throws Exception {
    InputStream inputStream = Mockito.mock(InputStream.class);
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(true);
    SocketUtils.logIfDebugEnabled(inputStream, logger);

    Mockito.verify(logger, Mockito.atLeastOnce()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                                                        Mockito.anyString());
  }

  @Test
  public void testLogIfDebugDisabledForInputStream() throws Exception {
    InputStream inputStream = Mockito.mock(InputStream.class);
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(false);
    SocketUtils.logIfDebugEnabled(inputStream, logger);

    Mockito.verify(logger, Mockito.never()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
  }

  @Test
  public void testLogIfDebugEnabledForString() {
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(true);
    SocketUtils.logIfDebugEnabled(SOME_TEXT, logger);

    Mockito.verify(logger, Mockito.atLeastOnce()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
  }

  @Test
  public void testLogIfDebugDisabledForString() {
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(false);
    SocketUtils.logIfDebugEnabled(SOME_TEXT, logger);

    Mockito.verify(logger, Mockito.never()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
  }

  @Test
  public void testLogIfDebugEnabledForByteArray() {
    byte[] byteArray = SOME_TEXT.getBytes();
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(true);
    SocketUtils.logIfDebugEnabled(byteArray, logger);

    Mockito.verify(logger, Mockito.atLeastOnce()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
  }

  @Test
  public void testLogIfDebugDisabledForByteArray() {
    byte[] byteArray = SOME_TEXT.getBytes();
    Logger logger = Mockito.mock(Logger.class);

    Mockito.when(logger.isDebugEnabled()).thenReturn(false);
    SocketUtils.logIfDebugEnabled(byteArray, logger);

    Mockito.verify(logger, Mockito.never()).debug(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
  }

}
