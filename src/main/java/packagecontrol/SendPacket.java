package packagecontrol;

import general.Host;
import general.Protocol;
import general.Utils;

import java.net.DatagramPacket;

public class SendPacket implements Runnable {

    private Host host;
    private OutgoingPacket packet;

    // TODO: Clean up
    public SendPacket(Host host, OutgoingPacket outgoingPacket) {
        this.host = host;
        this.packet = outgoingPacket;
    }

    public void run() {
        DatagramPacket datagramPacket = createPacket();
        if (datagramPacket != null) {
            int transmission = sendPacket(datagramPacket);
        } else {

        }
    }

    private DatagramPacket createPacket() {
        int nBytes = Protocol.HEADERSIZE + packet.getData().length;

        byte[] bCommand = packet.getCommand().getBytes();
        byte[] bSequenceCmd = packet.getSequenceCmd().getBytes();
        byte[] bLAF = new byte[Protocol.LAFSIZE];
        byte[] bTask = Utils.intToByteArray(packet.getTaskNo(), Protocol.TASKSIZE);
        byte[] bSequence = Utils.intToByteArray(packet.getSequenceNo(), Protocol.SEQUENCESIZE);
        byte[] bTotalBytes = new byte[Protocol.PACKETSIZESIZE];
        byte[] bChecksum = new byte[Protocol.CHECKSUMSIZE];

        byte[] sendData = Utils.byteConcat(bCommand, bSequenceCmd, bLAF, bTask ,bSequence, bTotalBytes, bChecksum, packet.getData());
        if (nBytes == sendData.length) {
            return new DatagramPacket(sendData, sendData.length, packet.getDestinationIP(), packet.getDestinationPort());
        } else {
            System.out.println("Size of packet wasn't good");
            return null;
        }
    }

    private int sendPacket(DatagramPacket datagramPacket) {
        int nTransmissions = 0;
        boolean ackReceived = false;

        while (!ackReceived) {
            host.send(datagramPacket);
            nTransmissions++;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + Protocol.TIMEOUT;

            while (!ackReceived && System.currentTimeMillis() < endTime) {
                if (host.getAckController().hasAck(this.packet.getTaskNo(), this.packet.getSequenceNo())) {
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
