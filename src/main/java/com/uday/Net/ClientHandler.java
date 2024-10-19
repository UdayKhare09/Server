package com.uday.Net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;

    private DataInputStream dis;
    private DataOutputStream dos;
    public String clientIP;
    public String clientUserName;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        dis = new DataInputStream(clientSocket.getInputStream());
        dos = new DataOutputStream(clientSocket.getOutputStream());
        clientIP = clientSocket.getInetAddress().getHostAddress();
        System.out.println("New client connected: " + clientIP);
    }

    @Override
    public void run() {
        try {
            clientUserName = dis.readUTF();
            if (Server.clients.containsKey(clientUserName)) {
                dos.writeUTF("Username already taken");
                clientSocket.close();
                return;
            }
            dos.writeUTF("Username set");
            System.out.println("Sent username set confirmation to " + clientIP);
            if (Server.serverPassword.equals(dis.readUTF())) {
                dos.writeUTF("Password correct");
            } else {
                dos.writeUTF("Password incorrect");
                clientSocket.close();
                return;
            }
            Server.clients.put(clientUserName, this);
            System.out.println("Client " + clientIP + " has set username to: " + clientUserName);
            while (true) {
                receivePacket();
            }
        } catch (IOException e) {
            try {
                clientSocket.close();
                Server.clients.remove(clientUserName);
                System.out.println("Client " + clientIP + " has disconnected");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }


    private void receivePacket() {
        try {
            int byteSize = dis.readInt();
            byte[] packet = new byte[byteSize];
            dis.readNBytes(packet, 0, byteSize);
            PacketHandler.handlePacket(clientUserName, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(byte[] packet) {
        try {
            dos.writeInt(packet.length);
            dos.write(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
