package packagecontrol;

import java.net.InetAddress;
import java.util.Arrays;

public class ByteMessage {

    private String command;
    private int taskNo;
    private int sequenceNo;
    private byte[] data;
    private InetAddress sourceIP;
    private int sourcePort;

    public ByteMessage(byte[] message, InetAddress sourceIP, int sourcePort) {
        this.command = new String(Arrays.copyOfRange(message,0, 1));
        this.taskNo = (int) message[2];
        this.sequenceNo = (int) message[3];
        this.data = Arrays.copyOfRange(message,4, message.length-1);

        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
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

    public InetAddress getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

}
