package com.uday.Net;

public class PacketHandler {
    public static void handlePacket(String senderUsername, byte[] tempPacket) {
        // handle the packet
        int packetType = tempPacket[0];
        byte[] packet = new byte[tempPacket.length - 1];
        System.arraycopy(tempPacket, 1, packet, 0, packet.length);
        switch (packetType) {
            case 0:
                System.out.println("Received packet type 0 from " + senderUsername);
                System.out.println("Packet data: " + new String(packet));
                break;
            case 1:
                // handle Text packet
                handleTextPacket(senderUsername, packet);
                break;
            case 2:
                // handle packet type 2
                break;
            default:
                System.out.println("Unknown packet type received from " + senderUsername);
        }
    }

    private static void handleTextPacket(String senderUsername, byte[] packet) {
        int textType = packet[0];
        byte[] text = new byte[packet.length - 1];
        System.arraycopy(packet, 1, text, 0, text.length);
        switch (textType) {
            case 0:
                broadcastText(senderUsername, text);
                break;
            case 1:
                // handle private text
                privateText(senderUsername, text);
                break;
            case 2:
                // handle server command
                break;
            default:
                System.out.println("Unknown text type received from " + senderUsername);
        }
    }

    public static void broadcastText(String senderUsername, byte[] text) {
        // broadcast the text to all clients
        String msg = "This is a broadcast sent by " + senderUsername + ": " + new String(text);
        byte[] packet = new byte[msg.length() + 2];
        packet[0] = 1;
        packet[1] = 0;
        System.arraycopy(msg.getBytes(), 0, packet, 2, msg.length());
        for (ClientHandler client : Server.clients.values()) {
            client.sendPacket(packet);
        }
    }

    private static void privateText(String senderUsername, byte[] text) {
        // handle private text
        String[] msg = new String(text).split("69PiP69", 2);
        String receiverUsername = msg[0];
        String message = msg[1];
        if (Server.clients.containsKey(receiverUsername)) {
            String msgToSend = "Private message from " + senderUsername + ": " + message;
            byte[] packet = new byte[msgToSend.length() + 2];
            packet[0] = 1;
            packet[1] = 1;
            System.arraycopy(msgToSend.getBytes(), 0, packet, 2, msgToSend.length());
            Server.clients.get(receiverUsername).sendPacket(packet);
        } else {
            System.out.println("Client " + receiverUsername + " not found");
        }
    }
}
