package com.uday.Net;

import com.uday.Encryption.AESEncryption;
import com.uday.Encryption.EncryptedDataInputStream;
import com.uday.Encryption.EncryptedDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final EncryptedDataInputStream eDis;
    private final EncryptedDataOutputStream eDos;
    public String clientIP;
    public String clientUserName;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        dis = new DataInputStream(clientSocket.getInputStream());
        eDis = new EncryptedDataInputStream(dis);
        dos = new DataOutputStream(clientSocket.getOutputStream());
        eDos = new EncryptedDataOutputStream(dos);
        clientIP = clientSocket.getInetAddress().getHostAddress();
        System.out.println("New client connected: " + clientIP);
    }

    @Override
    public void run() {
        try {
            clientUserName = eDis.readUTF();
            if (Server.clients.containsKey(clientUserName)) {
                eDos.writeUTF("Username already taken");
                clientSocket.close();
                return;
            }
            eDos.writeUTF("Username set");
            System.out.println("Sent username set confirmation to " + clientIP);
            if (Server.serverPassword.equals(eDis.readUTF())) {
                eDos.writeUTF("Password correct");
            } else {
                eDos.writeUTF("Password incorrect");
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


    public void receivePacket() {
        try {
            int totalChunks = dis.readInt(); // Read the number of chunks
            System.out.println("Received packet with " + totalChunks + " chunks");
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            for (int i = 0; i < totalChunks; i++) {
                int length = dis.readInt(); // Read the length of the current chunk
                byte[] chunk = dis.readNBytes(length); // Read the chunk
                buffer.write(chunk); // Write the chunk to the buffer
            }
            PacketHandler.handlePacket(clientUserName,AESEncryption.decrypt(buffer.toByteArray())); // Handle the complete packet
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(byte[] packet) {
        try {
            int chunkSize = 1024; // Define a reasonable chunk size
            int totalChunks = (int) Math.ceil((double) packet.length / chunkSize);
            dos.writeInt(totalChunks); // Send the number of chunks
            packet = AESEncryption.encrypt(packet);

            for (int i = 0; i < totalChunks; i++) {
                int start = i * chunkSize;
                int length = Math.min(chunkSize, packet.length - start);
                dos.writeInt(length); // Send the length of the current chunk
                dos.write(packet, start, length); // Send the chunk
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
