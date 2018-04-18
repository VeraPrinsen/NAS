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

/**
 * The received byte array is translated into the header and data given the protocol
 */
public class IncomingPacket {

    private InetAddress sourceIP;
    private int sourcePort;
    private boolean isOK;

    private String command;
    private String sequenceCmd;
    private int taskNo;
    private int sequenceNo;
    private int totalSequenceNo;
    private int LAF;
    private int packetSize;
    private byte[] data;

    public IncomingPacket(InetAddress sourceIP, int sourcePort, byte[] byteMessage) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;

        checkMessage(byteMessage);
    }

    /**
     * Check if the CRC of the received data is the same as the CRC that was send with the data.
     * TODO: Apparently the DatagramSocket and DatagramPackets already have this, so this is redundant. Checking the CRC of the whole data (multiple packets) should be done
     */
    private void checkMessage(byte[] byteMessage) {
        int checksumSource = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_CHECKSUM, Protocol.LASTINDEX_CHECKSUM+1));
        int checksumReceiver = CyclicRedundancyCheck.checksum(Arrays.copyOfRange(byteMessage, Protocol.LASTINDEX_CHECKSUM+1, byteMessage.length));

        if (checksumSource == checksumReceiver) {
            isOK = true;
            dissectMessage(byteMessage);
        } else {
            isOK = false;
        }
    }

    /**
     * Disect the message into its header and data.
     * The header is also being seperated into the flags
     *      command, sequenceCmd, taskNo, sequenceNo, LAF, packetSize
     */
    private void dissectMessage(byte[] byteMessage) {
        this.command = new String(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_COMMAND, Protocol.LASTINDEX_COMMAND+1));
        this.sequenceCmd = new String(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_SEQUENCECMD, Protocol.LASTINDEX_SEQUENCECMD+1));
        this.LAF = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_LAF, Protocol.LASTINDEX_LAF+1));
        this.taskNo = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_TASK, Protocol.LASTINDEX_TASK+1));
        this.sequenceNo = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_SEQUENCE, Protocol.LASTINDEX_SEQUENCE+1));
        this.packetSize = Utils.byteArrayToInt(Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_DATASIZE, Protocol.LASTINDEX_DATASIZE +1));
        this.data = Arrays.copyOfRange(byteMessage, Protocol.FIRSTINDEX_DATA, byteMessage.length);
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

    public byte[] getData() {
        return data;
    }

    public boolean isOK() {
        return isOK;
    }
}
