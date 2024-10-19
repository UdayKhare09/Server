package com.uday;

import com.uday.Net.Server;

public class Main {
    public static void main(String[] args) {
        Thread serverThread = new Thread(new Server());
        serverThread.start();
    }
}