package packagecontrol;

import client.SocketListener;
import general.Host;
import general.Info;
import general.Methods;

import java.net.DatagramPacket;

public class SendPacket implements Runnable {

    private Host host;
    private OutgoingPacket packet;

    public SendPacket(Host host, OutgoingPacket outgoingPacket) {
        this.host = host;
        this.packet = outgoingPacket;
    }

    public void run() {
        DatagramPacket datagramPacket = createPacket();
        if (datagramPacket != null) {
            sendPacket(datagramPacket);
        } else {

        }
    }

    private DatagramPacket createPacket() {
        int nBytes = Info.HEADERSIZE + packet.getData().length;

        byte[] bCommand = packet.getCommand().getBytes();
        byte[] bTask = Methods.intToByteArray(packet.getTaskNo(), Info.TASKSIZE);
        byte[] bSequence = Methods.intToByteArray(packet.getSequenceNo(), Info.SEQUENCESIZE);
        byte[] bTotalBytes = new byte[Info.PACKETSIZESIZE];
        byte[] bChecksum = new byte[Info.CHECKSUMSIZE];

        byte[] sendData = Methods.byteConcat(bCommand, bTask ,bSequence, bTotalBytes, bChecksum, packet.getData());
        if (nBytes == sendData.length) {
            return new DatagramPacket(sendData, sendData.length, packet.getDestinationIP(), packet.getDestinationPort());
        } else {
            return null;
        }

    }

    private int sendPacket(DatagramPacket datagramPacket) {
        int nTransmissions = 0;
        boolean ackReceived = false;

        while (!ackReceived) {
            host.send(datagramPacket);
            System.out.println("Packet send");
            nTransmissions++;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + Info.TIMEOUT;

            while (!ackReceived && System.currentTimeMillis() < endTime) {
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
