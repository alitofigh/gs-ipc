package org.gsstation.novin.server;

import org.gsstation.novin.TransactionData;
import org.jpos.iso.ISOException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by A_Tofigh at 7/13/2024
 */

public class DataReceiverServer {
    static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataReceiverServer.class);
    private ServerSocket serverSocket;



    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        while (true)
            new DataReceiverHandler(serverSocket.accept()).start();
    }

    public void stop() throws IOException {
        this.serverSocket.close();
    }

    private static class DataReceiverHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private DataInputStream in;

        static int counter = 0;

        public DataReceiverHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            logger.info("this thread number: {}", ++counter);
            System.out.println("this thread number: " + (++counter));
            System.out.println(clientSocket);
            System.out.println(this);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new DataInputStream(clientSocket.getInputStream());
                    String inputLine;
                    while (clientSocket.isBound() &&
                            clientSocket.isConnected() &&
                            !clientSocket.isInputShutdown() &&
                            !clientSocket.isOutputShutdown()) {
                        byte[] inLen = new byte[4];
                        int inLenBytes = 0;
                        try {
                            inLenBytes = in.read(inLen);
                        } catch (EOFException e) {}

                        if (inLenBytes == 4) {
                            String inLenStr = new String(inLen);
                            byte[] response = new byte[Integer.parseInt(inLenStr)];
                            in.read(response);
                            TransactionData transactionData = new TransactionData(response);
                            System.out.println("---" + new String(response) + "---");
                        }

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ISOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
