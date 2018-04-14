package server;

import packagecontrol.OutgoingData;

import java.net.DatagramPacket;

// TODO: What to do with client commands?
public class CommandHandler {

    public static void download() {
        // Server needs to send a file to the client
    }

    public static void upload() {
        // Client wants to upload a file for the server to save
    }

    public static void fileList() {

    }

    public static void pause() {
        // Server needs to pause sending a file to the client
    }

    public static void resume() {
        // Server needs to resume sending a file to the client (that was paused)
    }

    public static void firstData() {

    }

    public static void lastData() {
        // The final piece of the file the client wants to upload (needs an upload request)
    }

    public static void singleData() {

    }

    public static void sendData() {
        // A piece of the file the client wants to upload (needs an upload request)
    }

    public static void defaultSend(OutgoingData message, Server server) {
        // If a command is unknown, for now it will be send back to the client
        byte[] sendData = message.getData();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, message.getDestinationIP(), message.getDestinationPort());
        server.send(sendPacket);
    }

    public static void sendAck(OutgoingData message, Server server) {

    }
}
