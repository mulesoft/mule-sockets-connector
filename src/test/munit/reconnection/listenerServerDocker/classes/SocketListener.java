/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SocketListener {

    protected void createSocketServer() {
        try {
            while(true) {
                ServerSocket middleman = new ServerSocket();
                middleman.setReuseAddress(true);
                middleman.bind(new InetSocketAddress("0.0.0.0", 8085));

                System.out.println("\n-- SERVER ACCEPTING CONNECTIONS ON 0.0.0.0:8085 --\n");

                Socket client = middleman.accept();
                middleman.close();

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("\n-- CONTENT FROM REQUESTER IS : " + line + " --\n");

                    if (line.contains("2")) {
                        System.out.println("\n-- STOPPING SOCKET LISTENER BY 1 SECOND --\n");
                        TimeUnit.SECONDS.sleep(1);
                    }

                    out.println("-- ALL GOOD " + line + " --");
                }

                out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n-- OOPS 1 :(" + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketListener test = new SocketListener();
        test.createSocketServer();
    }

}
