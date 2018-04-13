package general;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Host {

    private DatagramSocket hostSocket;
    private List<String> receivedAcks;
    // een lijst waarin de taskNo's worden opgeslagen

    public Host () {
        receivedAcks = new ArrayList<>();
    }

    public void receivedAck(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        String receivedAck = getAckString(sourceIP, sourcePort, taskNo, sequenceNo);
        if (receivedAcks.add(receivedAck)) {
            System.out.println("Ack | " + receivedAck + " | added to the received list");
        }
    }

    public boolean hasAck(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        String requestedAck = getAckString(sourceIP, sourcePort, taskNo, sequenceNo);
//        if (!receivedAcks.contains(requestedAck)) {
//            System.out.println(requestedAck + " has not been received yet:");
//            showList();
//        }

        return receivedAcks.contains(requestedAck);
    }

    public String getAckString(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        // return sourceIP.toString() + Protocol.DELIMITER + sourcePort + Protocol.DELIMITER + taskNo + Protocol.DELIMITER + sequenceNo;
        return taskNo + Protocol.DELIMITER + sequenceNo;
    }

    public void clearAcks() {
        // receivedAcks.clear();
    }

    public void send(DatagramPacket datagramPacket) {
        try {
            hostSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramSocket getHostSocket() {
        return hostSocket;
    }

    public void createSocket() {
        try {
            this.hostSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void createSocket(int portNumber) {
        try {
            this.hostSocket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void showList() {
        for (String s : receivedAcks) {
            System.out.println(s);
        }
    }
}
