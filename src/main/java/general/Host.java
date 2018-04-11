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
    private List<String> expectedAcks;
    // een lijst waarin de taskNo's worden opgeslagen

    public Host () {
        expectedAcks = new ArrayList<>();
    }

    public void addAck(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        String newAck = getAckString(sourceIP, sourcePort, taskNo, sequenceNo);
        expectedAcks.add(newAck);
    }

    public void removeAck(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        String receivedAck = getAckString(sourceIP, sourcePort, taskNo, sequenceNo);
        boolean listContainsAck = expectedAcks.contains(receivedAck);
        if (listContainsAck) {
            expectedAcks.remove(receivedAck);
        }
    }

    public boolean hasAck(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        String requestedAck = getAckString(sourceIP, sourcePort, taskNo, sequenceNo);
        return expectedAcks.contains(requestedAck);
    }

    private String getAckString(InetAddress sourceIP, int sourcePort, int taskNo, int sequenceNo) {
        return sourceIP.toString() + Info.DELIMITER + sourcePort + Info.DELIMITER + taskNo + Info.DELIMITER + sequenceNo;
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
}
