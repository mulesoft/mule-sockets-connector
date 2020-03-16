/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpSocketSendReconnectionTrigger extends SocketExtensionTestCase {

  private static String MESSAGE = "Hello World!";
  private static int MAX_CONNECTION_QUEUE = 2;
  private static int RUNS = 2;

  @Override
  public String getConfigFile() {
    return "socket-reconnection-config.xml";
  }


  @Test
  public void tcpSocketSendTriggersReconnectionWhenConnectionLost() throws Exception {
    AtomicInteger counter = new AtomicInteger(0);
    Thread t = new Thread(() -> {
      try (ServerSocket listenerSocket = new ServerSocket(dynamicPort.getNumber(), MAX_CONNECTION_QUEUE)) {
        String receivedMessage;
        BufferedReader reader;

        for (int i = 0; i < RUNS; i++) {
          LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).debug("Socket listening...");

          try (Socket serverSocket = listenerSocket.accept()) {
            LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).debug("Connection accepted!");

            reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            receivedMessage = reader.readLine();

            LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class)
                .debug("Listener Socket received message: " + receivedMessage);

            serverSocket.close();

            LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).debug("Socket closed!");

            assertThat(receivedMessage, is(equalTo(MESSAGE)));

            counter.incrementAndGet();

            LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class)
                .debug(String.format("Successfully received message times: %d/%d", counter.get(), RUNS));

          } catch (Exception e) {
            LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class)
                .error(e.getMessage());
          }

        }
      } catch (Exception e) {
        LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).error("ERROR: " + e.getCause());
      }
    });

    t.start();

    LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).debug("RUN FLOW 1");
    flowRunner("socketSendAndReconnect").run();

    LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).debug("RUN FLOW 2");
    flowRunner("socketSendAndReconnect").run();

    t.join();

    assertThat(counter.get(), is(equalTo(RUNS)));
  }

}
