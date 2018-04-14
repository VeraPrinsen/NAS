package packagecontrol;

import general.Host;
import general.Protocol;
import general.Utils;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TaskController implements Runnable {

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

    public TaskController(Host host) {
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

    public void doSomething(IncomingPacket incomingPacket) {
        receivePacket(incomingPacket);
    }

    // SENDACK
    private void sendAck(IncomingPacket incomingPacket) {
        DatagramPacket newAck = createAckPacket(incomingPacket);
        sendPacket(newAck);
        System.out.println("Ack | " + incomingPacket.getTaskNo() + "-" + incomingPacket.getSequenceNo() + " | send to " + incomingPacket.getSourceIP() + " " + incomingPacket.getSourcePort());
    }

    private DatagramPacket createAckPacket(IncomingPacket incomingPacket) {
        int nBytes = Protocol.HEADERSIZE;

        byte[] bCommand = Protocol.ACK_OK.getBytes();
        byte[] bSequenceCmd = Protocol.SINGLE.getBytes();
        byte[] bLAF = Utils.intToByteArray(packetLAF.get(incomingPacket.getTaskNo()), Protocol.LAFSIZE);
        byte[] bTask = Utils.intToByteArray(incomingPacket.getTaskNo(), Protocol.TASKSIZE);
        byte[] bSequence = Utils.intToByteArray(incomingPacket.getSequenceNo(), Protocol.SEQUENCESIZE);
        byte[] bTotalBytes = new byte[Protocol.PACKETSIZESIZE];
        byte[] bChecksum = new byte[Protocol.CHECKSUMSIZE];

        byte[] sendData = Utils.byteConcat(bCommand, bSequenceCmd, bLAF, bTask ,bSequence, bTotalBytes, bChecksum);
        if (nBytes == sendData.length) {
            return new DatagramPacket(sendData, sendData.length, incomingPacket.getSourceIP(), incomingPacket.getSourcePort());
        } else {
            System.out.println("Size does not match (making ack)");
            return null;
        }
    }

    private DatagramPacket createLAFPacket(int taskNo) {
        int nBytes = Protocol.HEADERSIZE;

        byte[] bCommand = Protocol.ACK_OK.getBytes();
        byte[] bSequenceCmd = Protocol.SINGLE.getBytes();
        byte[] bLAF = Utils.intToByteArray(packetLAF.get(taskNo), Protocol.LAFSIZE);
        byte[] bTask = Utils.intToByteArray(taskNo, Protocol.TASKSIZE);
        byte[] bSequence = Utils.intToByteArray(packetLFR.get(taskNo), Protocol.SEQUENCESIZE);
        byte[] bTotalBytes = new byte[Protocol.PACKETSIZESIZE];
        byte[] bChecksum = new byte[Protocol.CHECKSUMSIZE];

        byte[] sendData = Utils.byteConcat(bCommand, bSequenceCmd, bLAF, bTask ,bSequence, bTotalBytes, bChecksum);
        if (nBytes == sendData.length) {
            return new DatagramPacket(sendData, sendData.length, packetsReceived.get(taskNo).get(0).getSourceIP(), packetsReceived.get(taskNo).get(0).getSourcePort());
        } else {
            System.out.println("Size does not match (making ack)");
            return null;
        }
    }

    private void sendPacket(DatagramPacket datagramPacket) {
        host.send(datagramPacket);
    }

    // SAVE PACKET
    private void receivePacket(IncomingPacket incomingPacket) {
        int taskNo = incomingPacket.getTaskNo();
        int totalSequenceNo;
        if (!taskExists(taskNo)) {
            totalSequenceNo = incomingPacket.getSequenceNo();
        } else {
            totalSequenceNo = getTotalSequenceNo(taskNo, incomingPacket.getSequenceNo());
        }

        System.out.println(incomingPacket.getSourceIP() + "/" + incomingPacket.getSourcePort() + ": " + incomingPacket.getCommand() + "-" + incomingPacket.getSequenceCmd() + "-" + incomingPacket.getTaskNo() + "-" + incomingPacket.getSequenceNo() + "-" + totalSequenceNo + " received");
        if (totalSequenceNo != -1) {
            if (!taskExists(taskNo)) {
                addTask(taskNo);
            }
            sendAck(incomingPacket);

            incomingPacket.setTotalSequenceNo(totalSequenceNo);
            System.out.println(totalSequenceNo);
            packetsReceived.get(taskNo).add(incomingPacket);
            sequenceReceived.get(taskNo).add(totalSequenceNo);
            System.out.println(totalSequenceNo + " added, total: " + sequenceReceived.get(taskNo).size());

            if (incomingPacket.getSequenceCmd().equals(Protocol.SINGLE)) {
                packetFirstReceived.replace(taskNo, totalSequenceNo);
                packetLastReceived.replace(taskNo, totalSequenceNo);
            } else if (incomingPacket.getSequenceCmd().equals(Protocol.FIRST)) {
                packetFirstReceived.replace(taskNo, totalSequenceNo);
            } else if (incomingPacket.getSequenceCmd().equals(Protocol.LAST)) {
                packetLastReceived.replace(taskNo, totalSequenceNo);
            }
        }
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

    private void update() {
        for (Integer taskNo : getTasks()) {
            System.out.println("Update for task " + taskNo);
            System.out.println("LastFrameReceived: " + packetLFR.get(taskNo) + "-" + packetTotalLFR.get(taskNo) + ", LastAcceptableFrame: " + packetLAF.get(taskNo) + "-" + packetTotalLAF.get(taskNo));
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
                DatagramPacket LAFPacket = createLAFPacket(taskNo);
                sendPacket(LAFPacket);
                System.out.println("Extra LAF packet send");

            }

            if (packetFirstReceived.get(taskNo) >= 0 && packetLastReceived.get(taskNo) >= 0) {
                if (isFinished(taskNo)) {
                    // do something
                    System.out.println("All packets received");
                    new Thread(new DataAssembler(packetsReceived.get(taskNo))).start();
                    removeTask(taskNo);
                }
            }
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
        return true;
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

        System.out.println("Task " + taskNo + " removed");
        pauseUpdates = false;
    }

    private Set<Integer> getTasks() {
        return sequenceReceived.keySet();
    }
}
