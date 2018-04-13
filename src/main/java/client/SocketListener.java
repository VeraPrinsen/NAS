package client;

import general.Host;
import general.Protocol;
import packagecontrol.IncomingPacket;

import java.io.IOException;
import java.net.DatagramPacket;

public class SocketListener implements Runnable {

    private Host host;

    public SocketListener(Host host) {
        this.host = host;
    }

    public void run() {
        while (true) {
            byte[] buffer = new byte[Protocol.maxPacketSize]; // empty buffer
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            try {
                host.getHostSocket().receive(receivePacket);
            } catch (IOException e) {
                // when connection is closed?
                e.printStackTrace();
            }

            IncomingPacket incomingPacket = new IncomingPacket(receivePacket);
            showInfo(incomingPacket);

            switch (incomingPacket.getCommand()) {
                case Protocol.ACK_OK:
                case Protocol.ACK_DENIED:
                    host.getAckController().receivedAck(incomingPacket.getTaskNo(), incomingPacket.getSequenceNo());
                    break;

                default:
                    host.getTaskController().doSomething(incomingPacket);
                    break;
            }

            //new Thread(new ReceivePacket(client, receivePacket)).start();
        }
    }

    private void showInfo(IncomingPacket packet) {
        System.out.println(packet.getSourceIP() + "/" + packet.getSourcePort() + ": " + packet.getCommand() + "-" + packet.getSequenceCmd() + "-" + packet.getTaskNo() + "-" + packet.getSequenceNo() + " received");
    }
}
