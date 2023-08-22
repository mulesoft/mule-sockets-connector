/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mule.extension.socket.AllureConstants.SocketFeature.SOCKET_EXTENSION;
import static org.mule.extension.socket.AllureConstants.SocketFeature.SocketStory.RECONNECTION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

@Feature(SOCKET_EXTENSION)
@Story(RECONNECTION)
public class TcpSocketSendReconnectionTriggerTestCase extends SocketExtensionTestCase {

  private static final Logger LOGGER = LoggerFactory.getLogger(TcpSocketSendReconnectionTriggerTestCase.class);
  private static final String MESSAGE = "Hello World!";
  private static final int RUNS = 2;

  @Override
  public String getConfigFile() {
    return "socket-reconnection-config.xml";
  }


  @Test
  @Issue("MULE-18173")
  public void tcpSocketSendTriggersReconnectionWhenConnectionLost() throws Exception {
    AtomicInteger counter = new AtomicInteger(0);
    Thread t = new Thread(() -> {
      try (ServerSocket listenerSocket = new ServerSocket()) {
        String receivedMessage;
        BufferedReader reader;

        listenerSocket.setReuseAddress(true);
        listenerSocket.bind(new InetSocketAddress("localhost", dynamicPort.getNumber()));

        for (int i = 0; i < RUNS; i++) {
          LOGGER.debug("Socket listening...");

          try (Socket serverSocket = listenerSocket.accept()) {
            LOGGER.debug("Connection accepted!");

            reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            receivedMessage = reader.readLine();

            assertThat(receivedMessage, is(equalTo(MESSAGE)));

            LOGGER.debug("Listener Socket received message: " + receivedMessage);

            counter.incrementAndGet();

            LOGGER.debug(String.format("Successfully received messages times: %d/%d", counter.get(), RUNS));
          } catch (Exception e) {
            LOGGER.error(e.getMessage());
          }
        }
      } catch (Exception e) {
        LOGGER.error("ERROR: " + e.getCause());
      }
    });

    t.start();

    LOGGER.debug("RUN FLOW 1");
    flowRunner("socketSendAndReconnect").run();

    LOGGER.debug("RUN FLOW 2");
    flowRunner("socketSendAndReconnect").run();

    t.join();

    assertThat(counter.get(), is(equalTo(RUNS)));
  }

}
