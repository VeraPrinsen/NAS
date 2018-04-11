package com.nedap.university;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class ClientHandler implements Runnable {

    private byte[] message;
    private InetAddress sourceIP;
    private int sourcePort;
    private UDPServer server;

    public ClientHandler(byte[] message, InetAddress sourceIP, int sourcePort, UDPServer server) {
        this.message = message;
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.server = server;
    }

    public void run() {
        byte[] sendData = message;
        // String capitalizedSentence = "CHECKV2: " + message.toUpperCase();
        //System.out.println("TO CLIENT: " + sendData.length + " bytes: " + new String(sendData));
        System.out.println("TO CLIENT: " + sendData.length + " bytes");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, sourceIP, sourcePort);
        server.send(sendPacket);
    }


}
