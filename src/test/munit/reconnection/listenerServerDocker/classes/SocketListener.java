/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package classes;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;

public class SocketListener {

    private static Socket socket;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress("0.0.0.0", 8085));

            System.out.println("\n-- Server started and listening on port 8085 --\n");

            String receivedMessage;
            BufferedReader reader;
            BufferedWriter writer;

            while (true) {
                socket = serverSocket.accept();
                System.out.println("\n-- NEW CONNECTION ACCEPTED --\n");

                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                receivedMessage = reader.readLine();

                System.out.println("\n-- Message received from client is " + receivedMessage + " --\n");

                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("-- ALL GOOD " + receivedMessage + " --");
                writer.flush();

                System.out.println("\n-- BYE BYE CONNECTION --\n");
            }
        } catch (Exception e) {
            System.out.println("Ooooooops 1 :(" + e);
            e.printStackTrace();
//        } finally {
//            try {
//                socket.close();
//            } catch (Exception e) {
//                System.out.println("Ooooooops 2 :(");
//            }
        }
    }

}