package client;

import general.Info;
import packagecontrol.ReceivePacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SocketListener implements Runnable {

    private DatagramSocket hostSocket;

    public SocketListener(Client client) {
        this.hostSocket = client.getHostSocket();
    }

    public void run() {
        byte[] buffer = new byte[Info.maxPacketSize]; // empty buffer
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        try {
            hostSocket.receive(receivePacket);
        } catch (IOException e) {
            // when connection is closed?
            e.printStackTrace();
        }

        new Thread(new ReceivePacket(receivePacket)).start();
    }
}
