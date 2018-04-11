package com.nedap.university;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.ByteArrayInputStream;

import general.HeaderInfo;
import general.Host;
import packagecontrol.SendPacket;

// Server doesn't connect with a specific client, it just listens to incoming packets
// and reacts on them. In this class the listening socket is made and incoming messages
// are further processed.
class Server extends Host {

    private DatagramSocket serverSocket;

    public Server() {
        createSocket(HeaderInfo.DEFAULT_PORT);
    }

    public void start() throws Exception
    {
        while(true) {
            // Receive a packet
            byte[] receiveData = new byte[HeaderInfo.maxDataSize];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            getHostSocket().receive(receivePacket);

            // Show on console information about the packet
            int k = receivePacket.getLength();
            byte[] messageBytes = receivePacket.getData();
            String message = new String(messageBytes);

            // Convert DatagramPacket into a byte array of the right length
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(messageBytes));
            byte[] sendData = new byte[k];

            for (int i = 0; i < k; i++) {
                sendData[i] = dataInputStream.readByte();
            }

            //System.out.println("FROM CLIENT: " + k + " bytes: " + new String(sendData));
            System.out.println("FROM CLIENT: " + k + " bytes");

            // Send back to the client
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();

            new Thread(new SendPacket(this, IPAddress, port, HeaderInfo.SENDDATA, 0, 0, sendData)).start();

            // new Thread(new ClientHandler(sendData, IPAddress, port, this)).start();
        }
    }
}