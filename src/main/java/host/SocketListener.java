package host;

import general.Protocol;
import incomingpacketcontrol.IncomingPacket;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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
            int k = receivePacket.getLength();
            System.out.println(k);
            byte[] messageBytes = receivePacket.getData();
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(messageBytes));

            byte[] receivedData = new byte[k];

            for (int i = 0; i < k; i++) {
                try {
                    receivedData[i] = dataInputStream.readByte();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            IncomingPacket incomingPacket = new IncomingPacket(receivePacket.getAddress(), receivePacket.getPort(), receivedData);
            showInfo(incomingPacket);
            if (incomingPacket.isOK()) {
                switch (incomingPacket.getCommand()) {
                    case Protocol.ACK:
                        host.getSendingWindow().processReceivedAck(incomingPacket);
                        break;

                    default:
                        host.getReceivingWindow().processIncomingPackets(incomingPacket);
                        break;
                }
            }
        }
    }

    private void showInfo(IncomingPacket packet) {
        System.out.println(packet.getSourceIP() + "/" + packet.getSourcePort() + " | " + packet.getCommand() +  "-" + packet.getTaskNo() + "-" + packet.getSequenceNo() + " received");
    }
}
