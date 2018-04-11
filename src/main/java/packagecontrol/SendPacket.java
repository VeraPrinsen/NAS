package packagecontrol;

import general.Info;
import general.Methods;
import general.Host;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class SendPacket implements Runnable {

    private Host host;
    BytePacket packet;

    public SendPacket(Host host, BytePacket packet) {
        this.host = host;
        this.packet = packet;
    }

    // TO DO: SEND DATA
    // TO DO: WAIT FOR ACK
    public void run() {
        DatagramPacket sendPacket = createPacket();
        if (sendPacket != null) {
            int retransmissions = sendPacket(sendPacket);
            System.out.println("Packet send in: " + retransmissions + " tries");
        } else {
            // Packet could not be made
        }
    }

    private DatagramPacket createPacket() {
        int nBytes = Info.HEADERSIZE + packet.getData().length;

        byte[] bCommand = packet.getCommand().getBytes();
        byte[] bTask = Methods.intToByteArray(packet.getTaskNo(), Info.TASKSIZE);
        byte[] bSequence = Methods.intToByteArray(packet.getSequenceNo(), Info.SEQUENCESIZE);

        byte[] sendData = Methods.concat(Methods.concat(Methods.concat(bCommand, bTask),bSequence), packet.getData());

        DatagramPacket packet = null;
        if (nBytes == sendData.length) {
            packet = new DatagramPacket(sendData, sendData.length, this.packet.getDestinationIP(), this.packet.getDestinationPort());
            System.out.println("Packet succesfully made");
        } else {
            System.out.println("Expected size: " + nBytes + ", Real size: " + sendData.length);
        }
        return packet;
    }

    private int sendPacket(DatagramPacket datagramPacket) {
        int nTransmissions = 0;
        boolean ackReceived = false;

        while (!ackReceived) {
            host.send(datagramPacket);
            nTransmissions++;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + Info.TIMEOUT;

            while (System.currentTimeMillis() < endTime) {
                if (host.hasAck(this.packet.getDestinationIP(), this.packet.getDestinationPort(), this.packet.getTaskNo(), this.packet.getSequenceNo())) {
                    host.removeAck(this.packet.getDestinationIP(), this.packet.getDestinationPort(), this.packet.getTaskNo(), this.packet.getSequenceNo());
                    ackReceived = true;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return nTransmissions;
    }
}
