package packagecontrol;

import general.Info;
import general.Methods;

import java.net.InetAddress;
import java.util.Arrays;

public class OutgoingPacket extends OutgoingData {

    private int sequenceNo;

    public OutgoingPacket(OutgoingData originalData, String command, byte[] dataFragment, int sequenceNo) {
        super(command, originalData.getTaskNo(), originalData.getDestinationIP(), originalData.getDestinationPort(), dataFragment);
        this.sequenceNo = sequenceNo;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

}
