package server;

import general.Protocol;
import packagecontrol.IncomingPacket;

public class ClientHandler implements Runnable {

    private IncomingPacket message;
    private Server server;

    public ClientHandler(IncomingPacket message, Server server) {
        this.message = message;
        this.server = server;
    }

    public void run() {
        if (!(message.getCommand().equals(Protocol.ACK_OK) || message.getCommand().equals(Protocol.ACK_DENIED))) {
            // If the command was not an ACK_OK or ACK_DENIED, send Ack
            //CommandHandler.sendAck(message, server);
        }

        // DO SOMETHING
        switch (message.getCommand()) {
            case Protocol.DOWNLOAD:
                CommandHandler.download();
                break;

            case Protocol.UPLOAD:
                CommandHandler.upload();
                break;

            case Protocol.PAUSE:
                CommandHandler.pause();
                break;

            case Protocol.RESUME:
                CommandHandler.resume();
                break;

            case Protocol.FIRST:
                CommandHandler.firstData();
                break;

            case Protocol.LAST:
                CommandHandler.lastData();
                break;

            case Protocol.SINGLE:
                CommandHandler.singleData();

            case Protocol.SENDDATA:
                CommandHandler.sendData();
                break;

            default:
                //CommandHandler.defaultSend(message, server);
                break;
        }
    }
}
