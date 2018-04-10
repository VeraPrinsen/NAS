package com.nedap.university;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

// Server doesn't connect with a specific client, it just listens to incoming packets
// and reacts on them. In this class the listening socket is made and incoming messages
// are further processed.
class UDPServer {

    private DatagramSocket serverSocket;

    public UDPServer() {
        try {
            this.serverSocket = new DatagramSocket(9876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception
    {
        while(true) {
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String message = new String(receivePacket.getData());
            System.out.println("RECEIVED: " + message);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            new Thread(new ClientHandler(message, IPAddress, port, this)).start();
        }
    }

    public void send(DatagramPacket datagramPacket) {
        try {
            serverSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}