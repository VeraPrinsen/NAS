package packagecontrol;

import general.Protocol;

import java.util.ArrayList;
import java.util.HashMap;

public class AckController {

    private HashMap<Integer, ArrayList<String>> receivedAcks;
    private HashMap<Integer, Integer> ackCycle;
    private HashMap<Integer, Integer> ackLAR;
    private HashMap<Integer, Integer> ackLFS;
    private HashMap<Integer, Integer> ackTotalLAR;
    private HashMap<Integer, Integer> ackTotalLFS;

    public AckController() {
        receivedAcks = new HashMap<>();
        ackCycle = new HashMap<>();
        ackLAR = new HashMap<>();
        ackLFS = new HashMap<>();
        ackTotalLAR = new HashMap<>();
        ackTotalLFS = new HashMap<>();
    }

    // When a host begins transmitting a new task, this is made to control the acks that will come back
    public void addTask(int taskNo) {
        receivedAcks.put(taskNo, new ArrayList<>());
        ackCycle.put(taskNo, 0);
        ackLAR.put(taskNo, -1);
        ackLFS.put(taskNo, -1);
        ackTotalLAR.put(taskNo, -1);
        ackTotalLFS.put(taskNo, -1);
    }

    public void sendPacket(int taskNo, int totalSequenceNo) {
        if (taskExists(taskNo)) {
            ackLFS.replace(taskNo, getSequenceNo(totalSequenceNo));
            ackTotalLFS.replace(taskNo, totalSequenceNo);
        } else {
            // TASK IS NOT KNOWN, packet could not have been send
        }
    }

    public boolean canSend(int taskNo, int totalSequenceNo) {
        return totalSequenceNo > ackTotalLAR.get(taskNo) && totalSequenceNo <= (ackTotalLAR.get(taskNo) + Protocol.SWS);
    }

    // sequenceNo is the restrained one of a specific amount of bytes
    // totalSequenceNo is the full sequence number, can go to the size of an integer
    public void receivedAck(int taskNo, int sequenceNo) {
        if (taskExists(taskNo)) {
            int totalSequenceNo = getTotalSequenceNo(taskNo, sequenceNo);

            if (totalSequenceNo != -1) {
                String receivedAck = ackString(taskNo, totalSequenceNo);
                receivedAcks.get(taskNo).add(receivedAck);
                update(taskNo);
            } else {
                // ack was not expected
            }
        } else {
            // TASK IS NOT KNOWN, ack was not expected
        }
    }

    public boolean hasAck(int taskNo, int totalSequenceNo) {
        String requestedAck = ackString(taskNo, totalSequenceNo);
        if (receivedAcks.containsKey(taskNo)) {
            return receivedAcks.get(taskNo).contains(requestedAck);
        } else {
            return false;
        }
    }

    public String ackString(int taskNo, int totalSequenceNo) {
        return taskNo + Protocol.DELIMITER + totalSequenceNo;
    }

    public void clearAcks(int taskNo) {
        receivedAcks.remove(taskNo);
    }

    private int getSequenceNo(int totalSequenceNo) {
        return totalSequenceNo % Protocol.maxSequenceNo;
    }

    private int getTotalSequenceNo(int taskNo, int sequenceNo) {
        int LAR = ackLAR.get(taskNo);
        int LFS = ackLFS.get(taskNo);
        int cycleNo = ackCycle.get(taskNo);

        if (LAR < LFS) {
            if (sequenceNo > LAR && sequenceNo <= LFS) {
                return (cycleNo*Protocol.maxSequenceNo) + sequenceNo;
            } else {
                // Do nothing, ack is not expected
                return -1;
            }
        } else if (LAR > LFS) {
            if (sequenceNo > LAR && sequenceNo < Protocol.maxSequenceNo - 1) {
                return (cycleNo*Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo >= 0 && sequenceNo <= LFS) {
                return ((cycleNo+1)*Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo > LFS && sequenceNo <= LAR) {
                // do nothing, ack is not expected
                return -1;
            } else {
                // which situations are these?
                return -1;
            }
        } else {
            // ackLAR == ackLFS?????
            return -1;
        }
    }

    private boolean taskExists(int taskNo) {
        return receivedAcks.containsKey(taskNo) && ackCycle.containsKey(taskNo) && ackLAR.containsKey(taskNo) && ackLFS.containsKey(taskNo);
    }

    private void update(int taskNo) {
        int totalLAR = ackTotalLAR.get(taskNo);
        int nextAck = totalLAR + 1;

        while (receivedAcks.get(taskNo).contains(ackString(taskNo, nextAck))) {
            ackTotalLAR.replace(taskNo, nextAck);
            ackLAR.replace(taskNo, getSequenceNo(nextAck));

            totalLAR = ackLAR.get(taskNo);
            nextAck = totalLAR + 1;

            if (ackLAR.get(taskNo) == 0 && receivedAcks.get(taskNo).size() > Protocol.maxSequenceNo) {
                ackCycle.replace(taskNo, ackCycle.get(taskNo) + 1);
            }
        }
    }

    public int getNewTask() {
        int i = 0;
        while (true) {
            if (taskExists(i)) {
                i++;
            } else {
                return i;
            }
        }
    }
}
