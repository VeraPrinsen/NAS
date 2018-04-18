package incomingpacketcontrol;

import general.Protocol;
import gui.InfoGUI;
import host.Host;
import outgoingpacketcontrol.OutgoingData;
import outgoingpacketcontrol.OutgoingPacket;
import outgoingpacketcontrol.SendPacket;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The ReceivingWindow controls all incoming packets and forwards them to the right task.
 */
public class ReceivingWindow {

    private Host host;
    private HashMap<Integer, ReceivingTask> taskHashMap;

    // Lock lock = new ReentrantLock();

    public ReceivingWindow(Host host) {
        this.host = host;
        taskHashMap = new HashMap<>();
    }

    private synchronized void addTask(int taskNo, IncomingPacket incomingPacket) {
        ReceivingTask newTask = new ReceivingTask(host, incomingPacket);
        taskHashMap.put(taskNo, newTask);
        (new Thread(newTask)).start();
    }

    private ReceivingTask getTask(int taskNo) {
        return taskHashMap.get(taskNo);
    }

    // IF PACKET IS EXPECTED: SEND ACK & SAVE PACKET
    public void processIncomingPacket(IncomingPacket incomingPacket) {
        int taskNo = incomingPacket.getTaskNo();

        if (!taskExists(taskNo)) {
            addTask(taskNo, incomingPacket);
        }

        int totalSequenceNo = getTotalSequenceNo(taskNo, incomingPacket.getSequenceNo());

        if (totalSequenceNo != -1) {
            incomingPacket.setTotalSequenceNo(totalSequenceNo);
            if (Protocol.showInfo) {
                System.out.println("Received: " + incomingPacket.getCommand() + "-" + incomingPacket.getSequenceCmd() + "-" + incomingPacket.getTaskNo() + "-" + incomingPacket.getSequenceNo() + "-" + incomingPacket.getTotalSequenceNo());
            }

            getTask(taskNo).addReceivedPackets(incomingPacket);

            if (incomingPacket.getSequenceCmd().equals(Protocol.SINGLE)) {
                getTask(taskNo).setFirstSeq(totalSequenceNo);
                getTask(taskNo).setLastSeq(totalSequenceNo);
            } else if (incomingPacket.getSequenceCmd().equals(Protocol.FIRST)) {
                getTask(taskNo).setFirstSeq(totalSequenceNo);
                if (incomingPacket.getCommand().equals(Protocol.SENDDATA)) {
                    String message = new String(incomingPacket.getData());
                    String[] args = message.split(Protocol.DELIMITER);
                    String fullFileName = args[0];
                    String[] args2 = fullFileName.split("/");
                    String fileName = args2[args2.length-1];
                    int nBytes = Integer.parseInt(args[1]);
                    getTask(taskNo).guiFirst(fileName, nBytes);
                }
            } else if (incomingPacket.getSequenceCmd().equals(Protocol.LAST)) {
                getTask(taskNo).setLastSeq(totalSequenceNo);
            }
        }

        if (!incomingPacket.getCommand().equals(Protocol.UPLOAD_SAVED)) {
            sendAck(incomingPacket);
        }
    }

    public void fileTransmissionDone(int taskNo) {
        getTask(taskNo).fileTransmissionDone();
    }


    // SENDACK
    private void sendAck(IncomingPacket incomingPacket) {
        int taskNo = incomingPacket.getTaskNo();
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.ACK, incomingPacket.getSourceIP(), incomingPacket.getSourcePort(), data, getTask(taskNo).getLAF());
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, taskNo, Protocol.SINGLE, data, incomingPacket.getSequenceNo(), getTask(taskNo).getLAF());
        new Thread(new SendPacket(host, null, outgoingPacket)).start();
    }

    private int getTotalSequenceNo(int taskNo, int sequenceNo) {
        int LFR = getTask(taskNo).getLFR();
        int LAF = getTask(taskNo).getLAF();
        int cycleNo = getTask(taskNo).getCycleNo();

        if (LFR < LAF) {
            if (sequenceNo > LFR && sequenceNo <= LAF) {
                return (cycleNo*Protocol.maxSequenceNo) + sequenceNo;
            } else {
                // Do nothing, ack is not expected
                return -1;
            }
        } else if (LFR > LAF) {
            if (sequenceNo > LFR && sequenceNo <= Protocol.maxSequenceNo - 1) {
                return (cycleNo*Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo >= 0 && sequenceNo <= LAF) {
                return ((cycleNo+1)*Protocol.maxSequenceNo) + sequenceNo;
            } else if (sequenceNo > LAF && sequenceNo <= LFR) {
                // do nothing, ack is not expected
                return -1;
            } else {
                // which situations are these?
                return -1;
            }
        } else {
            // LFR == LAF
            return -1;
        }
    }

    private boolean taskExists(int taskNo) {
        return taskHashMap.containsKey(taskNo);
    }
}
