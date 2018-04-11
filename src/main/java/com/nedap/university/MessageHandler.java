package com.nedap.university;

import general.Info;
import packagecontrol.ByteMessage;

public class MessageHandler implements Runnable {

    private ByteMessage message;
    private Server server;

    public MessageHandler(ByteMessage message, Server server) {
        this.message = message;
        this.server = server;
    }

    public void run() {
        if (!(message.getCommand().equals(Info.ACK_OK) || message.getCommand().equals(Info.ACK_DENIED))) {
            // If the command was not an ACK_OK or ACK_DENIED, send Ack
            CommandHandler.sendAck(message, server);
        }

        // DO SOMETHING
        switch (message.getCommand()) {
            case Info.DOWNLOAD:
                CommandHandler.download();
                break;

            case Info.UPLOAD:
                CommandHandler.upload();
                break;

            case Info.PAUSE:
                CommandHandler.pause();
                break;

            case Info.RESUME:
                CommandHandler.resume();
                break;

            case Info.SENDDATA:
                CommandHandler.senddata();
                break;

            case Info.FINAL:
                CommandHandler.finaldata();
                break;

            default:
                CommandHandler.defaultSend(message, server);
                break;
        }
    }
}
