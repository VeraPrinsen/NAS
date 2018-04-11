package com.nedap.university;

import java.net.DatagramPacket;
import java.net.InetAddress;
import packagecontrol.ByteMessage;

public class CommandHandler {

    public static void download() {

    }

    public static void upload() {

    }

    public static void pause() {

    }

    public static void senddata() {

    }

    public static void defaultSend(ByteMessage message, Server server) {
        byte[] sendData = message.getData();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, message.getSourceIP(), message.getSourcePort());
        server.send(sendPacket);
    }
}
