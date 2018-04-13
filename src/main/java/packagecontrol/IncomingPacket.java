package packagecontrol;

import general.Protocol;
import general.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

public class IncomingPacket {

    private InetAddress sourceIP;
    private int sourcePort;

    private String command;
    private String sequenceCmd;
    private int taskNo;
    private int sequenceNo;
    private byte[] packetSize;
    private byte[] checkSum;
    private byte[] data;

    public IncomingPacket(DatagramPacket receivedPacket) {
        dissectMessage(receivedPacket);
    }

    private void dissectMessage(DatagramPacket receivedPacket) {
        this.sourceIP = receivedPacket.getAddress();
        this.sourcePort = receivedPacket.getPort();

        byte[] byteMessage = getMessage(receivedPacket);

        this.command = new String(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_COMMAND, Protocol.LASTINDEX_COMMAND+1));
        this.sequenceCmd = new String(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_SEQUENCECMD, Protocol.LASTINDEX_SEQUENCECMD+1));
        this.taskNo = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_TASK, Protocol.LASTINDEX_TASK+1));
        this.sequenceNo = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_SEQUENCE, Protocol.LASTINDEX_SEQUENCE+1));
        this.packetSize = Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_PACKETSIZE, Protocol.LASTINDEX_PACKETSIZE+1);
        this.checkSum = Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_CHECKSUM, Protocol.LASTINDEX_CHECKSUM+1);
        this.data = Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_DATA, byteMessage.length);
    }

    private byte[] getMessage(DatagramPacket datagramPacket) {
        // Convert DatagramPacket into a byte array of the right length
        int k = datagramPacket.getLength();
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(datagramPacket.getData()));
        byte[] byteMessage = new byte[k];

        for (int i = 0; i < k; i++) {
            try {
                byteMessage[i] = dataInputStream.readByte();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return byteMessage;
    }

    public InetAddress getSourceIP() {
        return sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getCommand() {
        return this.command;
    }

    public String getSequenceCmd() {
        return this.sequenceCmd;
    }

    public int getTaskNo() {
        return taskNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public byte[] getPacketSize() {
        return packetSize;
    }

    public byte[] getCheckSum() {
        return checkSum;
    }

    public byte[] getData() {
        return data;
    }
}
