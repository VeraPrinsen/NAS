package incomingpacketcontrol;

import checksum.CyclicRedundancyCheck;
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
    private int LAF;
    private int taskNo;
    private int sequenceNo;
    private int totalSequenceNo;
    private byte[] packetSize;
    private byte[] checkSum;
    private byte[] data;
    private boolean isOK;

    public IncomingPacket(InetAddress sourceIP, int sourcePort, byte[] byteMessage) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;

        checkMessage(byteMessage);
    }

    private void checkMessage(byte[] byteMessage) {
        int checksumSource = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_CHECKSUM, Protocol.LASTINDEX_CHECKSUM+1));
        int checksumReceiver = CyclicRedundancyCheck.checksum(Arrays.copyOfRange(byteMessage, Protocol.LASTINDEX_CHECKSUM+1, byteMessage.length));

        System.out.println("CRC_source: " + checksumSource + ", CRC_receiver: " + checksumReceiver);
        if (checksumSource == checksumReceiver) {
            isOK = true;
            dissectMessage(byteMessage);
        } else {
            isOK = false;
        }
    }

    private void dissectMessage(byte[] byteMessage) {
        this.command = new String(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_COMMAND, Protocol.LASTINDEX_COMMAND+1));
        this.sequenceCmd = new String(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_SEQUENCECMD, Protocol.LASTINDEX_SEQUENCECMD+1));
        this.LAF = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_LAF, Protocol.LASTINDEX_LAF+1));
        this.taskNo = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_TASK, Protocol.LASTINDEX_TASK+1));
        this.sequenceNo = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_SEQUENCE, Protocol.LASTINDEX_SEQUENCE+1));
        this.packetSize = Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_DATASIZE, Protocol.LASTINDEX_DATASIZE +1);
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

    public void setTotalSequenceNo(int totalSequenceNo) {
        this.totalSequenceNo = totalSequenceNo;
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

    public int getLAF() {
        return this.LAF;
    }

    public int getTaskNo() {
        return taskNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public int getTotalSequenceNo() {
        return totalSequenceNo;
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

    public boolean isOK() {
        return isOK;
    }
}
