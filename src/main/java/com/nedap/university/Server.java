package com.nedap.university;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.ByteArrayInputStream;

import general.HeaderInfo;
import general.Host;
import packagecontrol.ByteMessage;

// Server doesn't connect with a specific client, it just listens to incoming packets
// and reacts on them. In this class the listening socket is made and incoming messages
// are further processed.
class Server extends Host {

    private DatagramSocket serverSocket;

    public Server() {
        createSocket(HeaderInfo.DEFAULT_PORT);
    }

    // Keeps listening on the port for requests from clients.
    public void start() {

        while(true) {               // maybe make an option to stop the program?
            // Receive a packet
            byte[] receiveData = new byte[HeaderInfo.maxDataSize]; // empty buffer
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                getHostSocket().receive(receivePacket);
            } catch (IOException e) {
                // when connection is closed?
                e.printStackTrace();
            }

            // Convert DatagramPacket into a byte array of the right length
            int k = receivePacket.getLength();
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(receivePacket.getData()));
            byte[] byteMessage = new byte[k];

            for (int i = 0; i < k; i++) {
                try {
                    byteMessage[i] = dataInputStream.readByte();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            showInfo(byteMessage);

            // Give to MessageHandler for further processing
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            new Thread(new MessageHandler(new ByteMessage(byteMessage, IPAddress, port), this)).start();
        }
    }

    private void showInfo(byte[] message) {
        System.out.println("FROM CLIENT: " + message.length + " bytes");
        System.out.println("MESSAGE TO STRING: " + new String(message));
    }
}