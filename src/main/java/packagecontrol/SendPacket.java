package packagecontrol;

import general.HeaderInfo;
import general.Methods;
import general.Host;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendPacket implements Runnable {

    private Host host;
    private InetAddress destinationIP;
    private int destinationPort;
    private String command;
    private int taskNo;
    private int sequenceNo;
    private byte[] data;

    public SendPacket(Host host, InetAddress destinationIP, int destinationPort, String command, int taskNo, int sequenceNo, byte[] data) {
        this.host = host;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.command = command;
        this.taskNo = taskNo;
        this.sequenceNo = sequenceNo;
        this.data = data;
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
        int nBytes = HeaderInfo.HEADERSIZE + data.length;

        byte[] bCommand = command.getBytes();
        byte[] bTask = new byte[1]; bTask[0] = (byte) taskNo;
        System.out.println(taskNo + " / " + bTask);
        byte[] bSequence = new byte[1]; bSequence[0] = (byte) sequenceNo;
        System.out.println(taskNo + " / " + bTask);
        byte[] sendData = Methods.concat(Methods.concat(Methods.concat(bCommand, bTask),bSequence), data);

        DatagramPacket sendPacket = null;
        if (nBytes == sendData.length) {
            sendPacket = new DatagramPacket(sendData, sendData.length, destinationIP, destinationPort);
            System.out.println("Packet succesfully made");
        } else {
            System.out.println("Expected size: " + nBytes + ", Real size: " + sendData.length);
        }
        return sendPacket;
    }

    private int sendPacket(DatagramPacket packet) {
        int nRetransmissions = 0;
        boolean ackReceived = false;

        while (!ackReceived) {
            host.send(packet);
            nRetransmissions++;

            // CHECK FOR ACKS




        }

        return nRetransmissions;

    }
}
