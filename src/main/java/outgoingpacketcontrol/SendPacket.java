package outgoingpacketcontrol;

import checksum.CyclicRedundancyCheck;
import host.Host;
import general.Protocol;
import general.Utils;

import java.net.DatagramPacket;

public class SendPacket implements Runnable {

    private Host host;
    private OutgoingPacket packet;

    // TODO: Doc
    public SendPacket(Host host, OutgoingPacket outgoingPacket) {
        this.host = host;
        this.packet = outgoingPacket;
    }

    public void run() {
        DatagramPacket datagramPacket = createPacket();
        if (datagramPacket != null) {
            int transmission = sendPacket(datagramPacket);
        } else {
            System.out.println("Size of packet wasn't good");
        }
    }

    private DatagramPacket createPacket() {
        int nBytes = Protocol.HEADERSIZE + packet.getData().length;

        byte[] bDataSize = Utils.intToByteArray(packet.getData().length, Protocol.DATASIZESIZE);
        byte[] bCommand = packet.getCommand().getBytes();
        byte[] bSequenceCmd = packet.getSequenceCmd().getBytes();
        byte[] bTask = Utils.intToByteArray(packet.getTaskNo(), Protocol.TASKSIZE);
        byte[] bSequence = Utils.intToByteArray(packet.getSequenceNo(), Protocol.SEQUENCESIZE);
        byte[] bLAF = Utils.intToByteArray(packet.getLAF(), Protocol.LAFSIZE);

        byte[] sendData = Utils.byteConcat(bDataSize, bCommand, bSequenceCmd, bTask, bSequence, bLAF, packet.getData());

        int checksum = CyclicRedundancyCheck.checksum(sendData);
        byte[] bChecksum = Utils.intToByteArray(checksum, Protocol.CHECKSUMSIZE);

        sendData = Utils.byteConcat(bChecksum, sendData);
        if (nBytes == sendData.length) {
            return new DatagramPacket(sendData, sendData.length, packet.getDestinationIP(), packet.getDestinationPort());
        } else {
            return null;
        }
    }

    private int sendPacket(DatagramPacket datagramPacket) {
        int nTransmissions = 0;
        boolean ackReceived = false;

        if (!packet.getCommand().equals(Protocol.ACK)) {
            host.getSendingWindow().confirmPacketSend(packet.getTaskNo(), packet.getSequenceNo(), packet.getTotalSequenceNo());
        }
        while (!ackReceived) {
            host.send(datagramPacket);
            System.out.println("Send: " + packet.getCommand() + "-" + packet.getTaskNo() + "-" + packet.getSequenceNo());
            System.out.println(datagramPacket.getData().length);
            nTransmissions++;

            if (packet.getCommand().equals(Protocol.ACK)) {
                ackReceived = true;
            }

            long startTime = System.currentTimeMillis();
            long endTime = startTime + Protocol.TIMEOUT;

            while (!ackReceived && System.currentTimeMillis() < endTime) {
                if (host.getSendingWindow().hasAck(packet.getTaskNo(), packet.getTotalSequenceNo())) {
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
