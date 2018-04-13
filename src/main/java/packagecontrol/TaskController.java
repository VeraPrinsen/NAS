package packagecontrol;

import general.Host;
import general.Protocol;
import general.Utils;

import java.net.DatagramPacket;

public class TaskController {

    private Host host;

    public TaskController(Host host) {
        this.host = host;
    }

    public void doSomething(IncomingPacket incomingPacket) {
        switch (incomingPacket.getCommand()) {
            case Protocol.DOWNLOAD:
                sendAck(incomingPacket);
                break;

            case Protocol.UPLOAD:
                sendAck(incomingPacket);
                break;

            case Protocol.PAUSE:
                sendAck(incomingPacket);
                break;

            case Protocol.RESUME:
                sendAck(incomingPacket);
                break;

            case Protocol.SENDDATA:
                sendAck(incomingPacket);
                break;

            default:
                break;
        }
    }

    private void sendAck(IncomingPacket incomingPacket) {
        DatagramPacket newAck = createPacket(incomingPacket);
        sendPacket(newAck);
        System.out.println("Ack | " + incomingPacket.getTaskNo() + "-" + incomingPacket.getSequenceNo() + " | send to " + incomingPacket.getSourceIP() + " " + incomingPacket.getSourcePort());
    }

    private DatagramPacket createPacket(IncomingPacket incomingPacket) {
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
}
