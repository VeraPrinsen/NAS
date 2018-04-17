package incomingpacketcontrol;

import checksum.CyclicRedundancyCheck;
import general.Protocol;
import general.Utils;
import host.Host;
import outgoingpacketcontrol.OutgoingData;
import outgoingpacketcontrol.OutgoingPacket;
import outgoingpacketcontrol.SendPacket;
import outgoingpacketcontrol.Task;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// TODO: Clean up
// TODO: Receiving packets goes to quick for the LFR and LAF to keep up. Maybe some wait() and notify() ?
// TODO: If a task was removed, and another packet comes in, do not make a new task.
public class ReceivingWindow implements Runnable {

    private Host host;
    private boolean pauseUpdates = false;

    private HashMap<Integer, ArrayList<IncomingPacket>> packetsReceived;
    private HashMap<Integer, ArrayList<Integer>> sequenceReceived;
    private HashMap<Integer, Integer> packetCycleNo;
    private HashMap<Integer, Integer> packetLFR;
    private HashMap<Integer, Integer> packetLAF;
    private HashMap<Integer, Integer> packetTotalLFR;
    private HashMap<Integer, Integer> packetTotalLAF;
    private HashMap<Integer, Integer> packetFirstReceived;
    private HashMap<Integer, Integer> packetLastReceived;
    private HashMap<Integer, Boolean> taskDone;
    private HashMap<Integer, Long> taskDoneTime;

    Lock lock = new ReentrantLock();

    public ReceivingWindow(Host host) {
        this.host = host;

        packetsReceived = new HashMap<>();
        sequenceReceived = new HashMap<>();
        packetCycleNo = new HashMap<>();
        packetLFR = new HashMap<>();
        packetLAF = new HashMap<>();
        packetTotalLFR = new HashMap<>();
        packetTotalLAF = new HashMap<>();
        packetFirstReceived = new HashMap<>();
        packetLastReceived = new HashMap<>();
        taskDone = new HashMap<>();
        taskDoneTime = new HashMap<>();
    }

    public void run() {
        while (true) {
            if (!pauseUpdates) {
                update();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        for (Integer taskNo : getTasks()) {
            int totalLFR = packetTotalLFR.get(taskNo);
            int nextLFR = totalLFR + 1;

            boolean LAFchanged = false;
            while (sequenceReceived.get(taskNo).contains(nextLFR)) {
                packetTotalLFR.replace(taskNo, nextLFR);
                packetLFR.replace(taskNo, getSequenceNo(nextLFR));
                int nextLAF = nextLFR + Protocol.WS;
                packetTotalLAF.replace(taskNo, nextLAF);
                packetLAF.replace(taskNo, getSequenceNo(nextLAF));
                LAFchanged = true;

                totalLFR = packetTotalLFR.get(taskNo);
                nextLFR = totalLFR + 1;

                if (packetLFR.get(taskNo) == 0 && sequenceReceived.get(taskNo).size() > Protocol.maxSequenceNo) {
                    packetCycleNo.replace(taskNo, packetCycleNo.get(taskNo) + 1);
                }
            }

            if (LAFchanged) {
                sendLAF(taskNo, packetsReceived.get(taskNo).get(0).getSourceIP(), packetsReceived.get(taskNo).get(0).getSourcePort());
            }

            if (packetFirstReceived.get(taskNo) >= 0 && packetLastReceived.get(taskNo) >= 0) {
                if (isFinished(taskNo)) {
                    // do something
                    new Thread(new DataAssembler(host, packetsReceived.get(taskNo))).start();
                    taskDone.replace(taskNo, true);
                    taskDoneTime.replace(taskNo, System.currentTimeMillis());
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                } else if (taskDone.get(taskNo) && taskDoneTime.get(taskNo) + Protocol.TIMEBEFOREREMOVE < System.currentTimeMillis()) {
                    //System.out.println("Task " + taskNo + " removed");
                    //removeTask(taskNo);
                }
            }
        }
    }

    // EVERY RECEIVED PACKET GOES THROUGH THIS METHOD
    public void processIncomingPackets(IncomingPacket incomingPacket) {
        receivePacket(incomingPacket);
    }

    // IF PACKET IS EXPECTED: SEND ACK & SAVE PACKET
    private void receivePacket(IncomingPacket incomingPacket) {
        int taskNo = incomingPacket.getTaskNo();
        int totalSequenceNo;
        if (!taskExists(taskNo)) {
            totalSequenceNo = incomingPacket.getSequenceNo();
        } else {
            totalSequenceNo = getTotalSequenceNo(taskNo, incomingPacket.getSequenceNo());
        }

        if (totalSequenceNo != -1) {
            if (!taskExists(taskNo)) {
                addTask(taskNo);
            }
            incomingPacket.setTotalSequenceNo(totalSequenceNo);
            sendAck(incomingPacket);

            packetsReceived.get(taskNo).add(incomingPacket);
            sequenceReceived.get(taskNo).add(totalSequenceNo);

            if (incomingPacket.getSequenceCmd().equals(Protocol.SINGLE)) {
                packetFirstReceived.replace(taskNo, totalSequenceNo);
                packetLastReceived.replace(taskNo, totalSequenceNo);
            } else if (incomingPacket.getSequenceCmd().equals(Protocol.FIRST)) {
                packetFirstReceived.replace(taskNo, totalSequenceNo);
            } else if (incomingPacket.getSequenceCmd().equals(Protocol.LAST)) {
                packetLastReceived.replace(taskNo, totalSequenceNo);
            }
        } else {
            sendAck(incomingPacket);
        }
    }

    // SENDACK
    private void sendAck(IncomingPacket incomingPacket) {
        System.out.println("Received: " + incomingPacket.getCommand() + "-" + incomingPacket.getSequenceCmd() + "-" + incomingPacket.getTaskNo() + "-" + incomingPacket.getSequenceNo() + "-" + incomingPacket.getTotalSequenceNo());
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.ACK, incomingPacket.getTaskNo(), incomingPacket.getSourceIP(), incomingPacket.getSourcePort(), data, packetLAF.get(incomingPacket.getTaskNo()));
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, Protocol.SINGLE, data, incomingPacket.getSequenceNo(), packetLAF.get(incomingPacket.getTaskNo()));
        new Thread(new SendPacket(host, outgoingPacket)).start();
    }

    // SENDLAF
    private void sendLAF(int taskNo, InetAddress destinationIP, int destinationPort) {
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.ACK, taskNo, destinationIP, destinationPort, data, packetLAF.get(taskNo));
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, Protocol.SINGLE, data, 0, packetLAF.get(taskNo));
        new Thread(new SendPacket(host, outgoingPacket)).start();
    }

    private int getSequenceNo(int totalSequenceNo) {
        return totalSequenceNo % Protocol.maxSequenceNo;
    }

    private int getTotalSequenceNo(int taskNo, int sequenceNo) {
        int LFR = packetLFR.get(taskNo);
        int LAF = packetLAF.get(taskNo);
        int cycleNo = packetCycleNo.get(taskNo);

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

    private boolean isFinished(int taskNo) {
        int firstSequence = packetFirstReceived.get(taskNo);
        int lastSequence = packetLastReceived.get(taskNo);

        for (int i = firstSequence; i <= lastSequence; i++) {
            if (!sequenceReceived.get(taskNo).contains(i)) {
                return false;
            }
        }
        return !taskDone.get(taskNo);
    }

    private boolean taskExists(int taskNo) {
        return packetsReceived.containsKey(taskNo);
    }

    private void addTask(int taskNo) {
        packetsReceived.put(taskNo, new ArrayList<>());
        sequenceReceived.put(taskNo, new ArrayList<>());
        packetCycleNo.put(taskNo, 0);
        packetLFR.put(taskNo, -1);
        packetLAF.put(taskNo, Protocol.WS - 1);
        packetTotalLFR.put(taskNo, -1);
        packetTotalLAF.put(taskNo, Protocol.WS - 1);
        packetFirstReceived.put(taskNo, -1);
        packetLastReceived.put(taskNo, -1);
        taskDone.put(taskNo, false);
        taskDoneTime.put(taskNo, new Long(0));
    }

    private void removeTask(int taskNo) {
        pauseUpdates = true;

        packetsReceived.remove(taskNo);
        sequenceReceived.remove(taskNo);
        packetCycleNo.remove(taskNo);
        packetLFR.remove(taskNo);
        packetLAF.remove(taskNo);
        packetTotalLFR.remove(taskNo);
        packetTotalLAF.remove(taskNo);
        packetFirstReceived.remove(taskNo);
        packetLastReceived.remove(taskNo);
        taskDone.remove(taskNo);
        taskDoneTime.remove(taskNo);

        pauseUpdates = false;
    }

    private synchronized Set<Integer> getTasks() {
        return sequenceReceived.keySet();
    }
}
