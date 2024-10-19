package com.uday.Net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Runnable {

    public static String serverIP;
    public static int serverPort = 7415;
    public static ServerSocket serverSocket;

    public static String serverPassword = "793551";

    public static ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    @Override
    public void run() {
        System.out.println("Trying to start the server...");
        try {
            serverSocket = new ServerSocket(serverPort);
            serverIP = serverSocket.getInetAddress().getHostAddress();
            System.out.println("Server started at " + serverIP + ":" + serverPort);

            while (true) {
                new Thread(new ClientHandler(serverSocket.accept())).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
