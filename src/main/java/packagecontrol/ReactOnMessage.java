package packagecontrol;

import general.Host;
import general.Protocol;
import general.Utils;

import java.net.DatagramPacket;

public class ReactOnMessage implements Runnable {

    private Host host;
    private IncomingPacket incomingPacket;

    public ReactOnMessage(Host host, IncomingPacket incomingPacket) {
        this.host = host;
        this.incomingPacket = incomingPacket;
    }

    public void run() {
        switch (incomingPacket.getCommand()) {
            case Protocol.DOWNLOAD:
                sendAck();
                break;

            case Protocol.UPLOAD:
                sendAck();
                break;

            case Protocol.PAUSE:
                sendAck();
                break;

            case Protocol.RESUME:
                sendAck();
                break;

            case Protocol.SENDDATA:
                sendAck();
                break;

            case Protocol.ACK_OK:
                saveAck();
                break;

            case Protocol.ACK_DENIED:
                saveAck();
                break;

            default:
                break;
        }
    }

    private void sendAck() {
        System.out.println("Packet | " + incomingPacket.getTaskNo() + Protocol.DELIMITER + incomingPacket.getSequenceNo() + " | received");
        DatagramPacket newAck = createPacket();
        sendPacket(newAck);
        System.out.println("Ack " + host.getAckString(host.getHostSocket().getInetAddress(), host.getHostSocket().getPort(), incomingPacket.getTaskNo(), incomingPacket.getSequenceNo()) + " send to " + incomingPacket.getSourceIP() + " " + incomingPacket.getSourcePort());
    }

    private DatagramPacket createPacket() {
        int nBytes = Protocol.HEADERSIZE;

        byte[] bCommand = Protocol.ACK_OK.getBytes();
        byte[] bSequenceCmd = Protocol.SINGLE.getBytes();
        byte[] bTask = Utils.intToByteArray(incomingPacket.getTaskNo(), Protocol.TASKSIZE);
        byte[] bSequence = Utils.intToByteArray(incomingPacket.getSequenceNo(), Protocol.SEQUENCESIZE);
        byte[] bTotalBytes = new byte[Protocol.PACKETSIZESIZE];
        byte[] bChecksum = new byte[Protocol.CHECKSUMSIZE];

        byte[] sendData = Utils.byteConcat(bCommand, bSequenceCmd, bTask ,bSequence, bTotalBytes, bChecksum);
        if (nBytes == sendData.length) {
            return new DatagramPacket(sendData, sendData.length, incomingPacket.getSourceIP(), incomingPacket.getSourcePort());
        } else {
            System.out.println("Size does not match (making ack)");
            return null;
        }
    }

    private void sendPacket(DatagramPacket datagramPacket) {
        host.send(datagramPacket);
    }

    private void saveAck() {
        System.out.println("Ack | " + host.getAckString(this.incomingPacket.getSourceIP(), this.incomingPacket.getSourcePort(), this.incomingPacket.getTaskNo(), this.incomingPacket.getSequenceNo()) + " | received");
        host.receivedAck(incomingPacket.getSourceIP(), incomingPacket.getSourcePort(), incomingPacket.getTaskNo(), incomingPacket.getSequenceNo());
    }

}
