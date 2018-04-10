package com.nedap.university;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class ClientHandler implements Runnable {

    private String message;
    private InetAddress sourceIP;
    private int sourcePort;
    private UDPServer server;

    public ClientHandler(String message, InetAddress sourceIP, int sourcePort, UDPServer server) {
        this.message = message;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.server = server;
    }

    public void run() {
        byte[] sendData = new byte[1024];
        String capitalizedSentence = "CHECKV2: " + message.toUpperCase();
        sendData = capitalizedSentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sourceIP, sourcePort);
        server.send(sendPacket);
    }


}
