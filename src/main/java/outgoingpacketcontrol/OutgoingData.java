package outgoingpacketcontrol;

import java.net.InetAddress;

public class OutgoingData {

    private String command;
    private InetAddress destinationIP;
    private int destinationPort;
    private byte[] data;
    private int taskNo;

    private int LAF;
    private String fullFileName = null;

    public OutgoingData(String command, InetAddress destinationIP, int destinationPort, byte[] data, int LAF) {
        this.command = command;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.data = data;
        this.LAF = LAF;
    }

    public OutgoingData(String command, InetAddress destinationIP, int destinationPort, String fullFileName, byte[] data, int LAF) {
        this(command, destinationIP, destinationPort, data, LAF);
        this.fullFileName = fullFileName;
    }

    public String getCommand() {
        return command;
    }

    public void setTaskNo(int taskNo) {
        this.taskNo = taskNo;
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

    public String getFullFileName() {
        return fullFileName;
    }

    public boolean isFile() {
        return fullFileName != null;
    }
}
