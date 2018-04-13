package client;

import general.Host;
import general.Protocol;
import packagecontrol.ReceivePacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class SocketListener implements Runnable {

    private Client client;

    public SocketListener(Client client) {
        this.client = client;
    }

    public void run() {
        while (true) {
            byte[] buffer = new byte[Protocol.maxPacketSize]; // empty buffer
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            try {
                client.getHostSocket().receive(receivePacket);
            } catch (IOException e) {
                // when connection is closed?
                e.printStackTrace();
            }

            new Thread(new ReceivePacket(client, receivePacket)).start();
        }
    }
}
