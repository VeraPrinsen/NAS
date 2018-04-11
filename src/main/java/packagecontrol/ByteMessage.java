package packagecontrol;

import general.Info;
import general.Methods;

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
        this.command = new String(Arrays.copyOfRange(message, Info.FIRSTINDEX_COMMAND, Info.LASTINDEX_COMMAND));
        this.taskNo = Methods.byteArrayToInt(Arrays.copyOfRange(message, Info.FIRSTINDEX_TASK, Info.LASTINDEX_TASK));
        this.sequenceNo = Methods.byteArrayToInt(Arrays.copyOfRange(message, Info.FIRSTINDEX_SEQUENCE, Info.LASTINDEX_SEQUENCE));
        this.data = Arrays.copyOfRange(message,Info.FIRSTINDEX_DATA, message.length-1);
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
