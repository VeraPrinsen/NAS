package outgoingpacketcontrol;

import java.net.InetAddress;

public class OutgoingData {

    private String command;
    private int taskNo;
    private InetAddress destinationIP;
    private int destinationPort;
    private byte[] data;

    private int LAF;

    public OutgoingData(String command, int taskNo, InetAddress destinationIP, int destinationPort, byte[] data, int LAF) {
        this.command = command;
        this.taskNo = taskNo;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.data = data;
        this.LAF = LAF;
    }

    public String getCommand() {
        return command;
    }

    public int getTaskNo() {
        return taskNo;
    }

    public InetAddress getDestinationIP() {
        return destinationIP;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public byte[] getData() {
        return data;
    }

    public int getLAF() {
        return LAF;
    }
}
