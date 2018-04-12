package packagecontrol;

import general.Info;
import general.Methods;

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

        this.command = new String(Arrays.copyOfRange(byteMessage, Info.FIRSTINDEX_COMMAND, Info.LASTINDEX_COMMAND));
        this.taskNo = Methods.byteArrayToInt(Arrays.copyOfRange(byteMessage, Info.FIRSTINDEX_TASK, Info.LASTINDEX_TASK));
        this.sequenceNo = Methods.byteArrayToInt(Arrays.copyOfRange(byteMessage, Info.FIRSTINDEX_SEQUENCE, Info.LASTINDEX_SEQUENCE));
        this.packetSize = Arrays.copyOfRange(byteMessage, Info.FIRSTINDEX_PACKETSIZE, Info.LASTINDEX_PACKETSIZE);
        this.checkSum = Arrays.copyOfRange(byteMessage, Info.FIRSTINDEX_CHECKSUM, Info.LASTINDEX_CHECKSUM);
        this.data = Arrays.copyOfRange(byteMessage,Info.FIRSTINDEX_DATA, byteMessage.length-1);
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
