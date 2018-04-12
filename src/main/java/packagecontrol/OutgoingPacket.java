package packagecontrol;

public class OutgoingPacket extends OutgoingData {

    private int sequenceNo;
    private String sequenceCmd;

    public OutgoingPacket(OutgoingData originalData, String sequenceCmd, byte[] dataFragment, int sequenceNo) {
        super(originalData.getCommand(), originalData.getTaskNo(), originalData.getDestinationIP(), originalData.getDestinationPort(), dataFragment);
        this.sequenceNo = sequenceNo;
        this.sequenceCmd = sequenceCmd;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getSequenceCmd() {
        return sequenceCmd;
    }
}
