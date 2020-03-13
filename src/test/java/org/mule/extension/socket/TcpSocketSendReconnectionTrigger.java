package org.mule.extension.socket;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.mule.extension.socket.api.SocketConnectionSettings;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpSocketSendReconnectionTrigger extends SocketExtensionTestCase {

    private Socket serverSocket;
    private ServerSocket listenerSocket;
    private static String MESSAGE = "Hello World!";
    private static int RUNS = 2;

    @Override
    public String getConfigFile() {
        return "socket-reconnection-config.xml";
    }

    @Before
    public void before() throws IOException {
        listenerSocket = new ServerSocket(dynamicPort.getNumber());
    }

    @Test
    public void tcpSocketSendTriggersReconnectionWhenConnectionLost() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        Thread task = new Thread(() -> {
            try {
                String receivedMessage;
                BufferedReader reader;

                for (int i = 0; i < RUNS; i++) {
                    serverSocket = listenerSocket.accept();
                    reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                    receivedMessage = reader.readLine();
                    LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).info("Listener Socket received message: " + receivedMessage);
                    assertThat(receivedMessage, is(equalTo(MESSAGE)));

                    counter.incrementAndGet();
                    serverSocket.close();

                }
            } catch (Exception e) {
                LoggerFactory.getLogger(TcpSocketSendReconnectionTrigger.class).debug(e.getMessage());
            } finally {
                //listenerSocket.close(); cambiar esto por un try with resources para que cierre todos
            }
        });

        try {
            task.start();

            flowRunner("socketSendAndReconnect").run();
            flowRunner("socketSendAndReconnect").run();
            assertThat(counter.incrementAndGet(), is(equalTo(RUNS)));

        } finally {
            task.join();
            //if (serverSocket != null) {
            //    serverSocket.close();
            //}
            if (listenerSocket != null) {
                listenerSocket.close();
            }
        }
    }

}
