package outgoingpacketcontrol;

import general.Protocol;

public class OutgoingPacket extends OutgoingData {

    private int sequenceNo;
    private int totalSequenceNo;
    private String sequenceCmd;

    public OutgoingPacket(OutgoingData originalData, String sequenceCmd, byte[] dataFragment, int totalSequenceNo, int LAF) {
        super(originalData.getCommand(), originalData.getDestinationIP(), originalData.getDestinationPort(), dataFragment, LAF);
        this.sequenceNo = totalSequenceNo % Protocol.maxSequenceNo;
        this.totalSequenceNo = totalSequenceNo;
        this.sequenceCmd = sequenceCmd;
    }

    public OutgoingPacket(OutgoingData originalData, int taskNo, String sequenceCmd, byte[] dataFragment, int totalSequenceNo, int LAF) {
        super(originalData.getCommand(), originalData.getDestinationIP(), originalData.getDestinationPort(), dataFragment, LAF);
        this.sequenceNo = totalSequenceNo % Protocol.maxSequenceNo;
        this.totalSequenceNo = totalSequenceNo;
        this.sequenceCmd = sequenceCmd;
        super.setTaskNo(taskNo);
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public int getTotalSequenceNo() {
        return totalSequenceNo;
    }

    public String getSequenceCmd() {
        return sequenceCmd;
    }
}
