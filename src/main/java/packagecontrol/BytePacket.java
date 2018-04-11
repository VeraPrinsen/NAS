package packagecontrol;

import general.Info;

import java.net.InetAddress;
import java.util.Arrays;

public class BytePacket {

    private String command;
    private int taskNo;
    private int sequenceNo;
    private byte[] data;
    private InetAddress destinationIP;
    private int destinationPort;

    public BytePacket(byte[] data, String command, int taskNo, int sequenceNo, InetAddress destinationIP, int destinationPort) {
        this.command = command;
        this.taskNo = taskNo;
        this.sequenceNo = sequenceNo;
        this.data = data;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
    }

    public String getCommand() {
        return this.command;
    }

    public int getTaskNo() {
        return taskNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public byte[] getData() {
        return data;
    }

    public InetAddress getDestinationIP() {
        return destinationIP;
    }

    public int getDestinationPort() {
        return destinationPort;
    }
}
