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
