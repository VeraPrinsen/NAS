package com.nedap.university;

import general.HeaderInfo;
import packagecontrol.ByteMessage;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class MessageHandler implements Runnable {

    private ByteMessage message;
    private Server server;

    public MessageHandler(ByteMessage message, Server server) {
        this.message = message;
        this.server = server;
    }

    public void run() {
        // SEND ACK

        // DO SOMETHING
        switch (message.getCommand()) {
            case HeaderInfo.DOWNLOAD:
                CommandHandler.download();
                break;

            case HeaderInfo.UPLOAD:
                CommandHandler.upload();
                break;

            case HeaderInfo.PAUSE:
                CommandHandler.pause();
                break;

            case HeaderInfo.SENDDATA:
                CommandHandler.senddata();
                break;

            default:
                CommandHandler.defaultSend(message, server);
                break;
        }
    }
}
