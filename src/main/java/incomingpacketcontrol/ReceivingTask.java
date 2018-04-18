package incomingpacketcontrol;

import client.Client;
import general.Protocol;
import gui.InfoGUI;
import host.Host;
import host.Task;
import outgoingpacketcontrol.OutgoingData;
import outgoingpacketcontrol.OutgoingPacket;
import outgoingpacketcontrol.SendPacket;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * All packets of a specific task will be controlled using the ReceivingTask class.
 * The class checks if it has received all packets and will forward it to the DataAssembler when it has.
 */
public class ReceivingTask extends Task {

    private InfoGUI infoGUI;
    private ArrayList<IncomingPacket> receivedPackets;

    private String command;
    private int taskNo;
    private InetAddress sourceIP;
    private int sourcePort;

    private ArrayList<Integer> receivedSequenceNo;
    private int cycleNo;
    private int LFR;
    private int LAF;
    private int totalLFR;
    private int totalLAF;
    private int firstSeq;
    private int lastSeq;

    private int nBytes;
    private int nPackets;

    private boolean taskDone;
    private long taskDoneTime;

    private long beginTask;
    private long endTask;

    public ReceivingTask(Host host, IncomingPacket incomingPacket) {
        super(host);
        this.receivedPackets = new ArrayList<>();
        this.receivedSequenceNo = new ArrayList<>();

        this.beginTask = System.currentTimeMillis();

        this.command = incomingPacket.getCommand();
        this.taskNo = incomingPacket.getTaskNo();
        this.sourceIP = incomingPacket.getSourceIP();
        this.sourcePort = incomingPacket.getSourcePort();

        this.cycleNo = 0;
        this.LFR = -1;
        this.LAF = Protocol.WS - 1;
        this.totalLFR = -1;
        this.totalLAF = Protocol.WS - 1;
        this.firstSeq = -1;
        this.lastSeq = -1;

        this.nPackets = 0;

        this.taskDone = false;
        this.taskDoneTime = 0;

        if (host instanceof Client && incomingPacket.getCommand().equals(Protocol.SENDDATA)) {
            infoGUI = new InfoGUI(this);
        }
    }

    @Override
    public void pause() {
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.PAUSE, this.sourceIP, this.sourcePort, data, this.LAF);
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, taskNo, Protocol.SINGLE, data, 0, this.LAF);
        new Thread(new SendPacket(getHost(), null, outgoingPacket)).start();
    }

    @Override
    public void resume() {
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.RESUME, this.sourceIP, this.sourcePort, data, this.LAF);
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, taskNo, Protocol.SINGLE, data, 0, this.LAF);
        new Thread(new SendPacket(getHost(), null, outgoingPacket)).start();
    }

    /**
     * Class keeps calling update() to perform all checks.
     */
    @Override
    public void run() {
        while (true) {
            update();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Keeps checking the following things:
     * For the receiving window: Which sequenceNumber are expected (What are the LFR and LAR)
     * If the LAR changes, send a new packet to the source (This is because of the thread situation)
     * Checks if all the packets of the task are received, if this is the case, forward to DataAssembler
     */
    private void update() {
        int nextLFR = totalLFR + 1;

        boolean LAFchanged = false;
        while (receivedSequenceNo.contains(nextLFR)) {
            totalLFR = nextLFR;
            LFR = getSequenceNo(nextLFR);
            int nextLAF = nextLFR + Protocol.WS;
            totalLAF = nextLAF;
            LAF = getSequenceNo(nextLAF);
            LAFchanged = true;

            nextLFR = totalLFR + 1;

            if (LFR == 0 && receivedSequenceNo.size() > Protocol.maxSequenceNo) {
                cycleNo++;
            }

            if (nPackets != 0) {
                if (getHost() instanceof Client && command.equals(Protocol.SENDDATA)) {
                    infoGUI.setProgressLable(receivedSequenceNo.size() + " of " + nPackets + " received");
                }
            }
        }

        if (LAFchanged) {
            sendLAF();
        }

        if (firstSeq >= 0 && lastSeq >= 0) {
            if (isFinished()) {
                new Thread(new DataAssembler(getHost(), receivedPackets)).start();
                if (getHost() instanceof Client && command.equals(Protocol.SENDDATA)) {
                    infoGUI.setProgressLable("File is begin saved...");
                }
                taskDone = true;
                taskDoneTime = System.currentTimeMillis();
            }
        }
    }

    private int getSequenceNo(int totalSequenceNo) {
        return totalSequenceNo % Protocol.maxSequenceNo;
    }

    // SENDLAF
    private void sendLAF() {
        InetAddress destinationIP = receivedPackets.get(0).getSourceIP();
        int destinationPort = receivedPackets.get(0).getSourcePort();
        int taskNo = receivedPackets.get(0).getTaskNo();
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.ACK, destinationIP, destinationPort, data, LAF);
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, taskNo, Protocol.SINGLE, data, 0, LAF);
        new Thread(new SendPacket(getHost(), null, outgoingPacket)).start();
    }

    private boolean isFinished() {
        if (taskDone) {
            return false;
        }

        for (int i = firstSeq; i <= lastSeq; i++) {
            if (!receivedSequenceNo.contains(i)) {
                return false;
            }
        }

        return true;
    }

    public void addReceivedPackets(IncomingPacket incomingPacket) {
        receivedPackets.add(incomingPacket);
        receivedSequenceNo.add(incomingPacket.getTotalSequenceNo());
    }

    public void guiFirst(String fileName, int nBytes) {
        this.nBytes = nBytes;
        if (getHost() instanceof Client && command.equals(Protocol.SENDDATA)) {
            infoGUI.setTitleLabel("Download: " + fileName);
        }

        if ((nBytes % Protocol.maxDataSize) == 0) {
            this.nPackets = (nBytes / Protocol.maxDataSize) + 1;
        } else {
            this.nPackets = (nBytes / Protocol.maxDataSize) + 2;
        }
    }

    public void fileTransmissionDone() {
        this.endTask = System.currentTimeMillis();
        long transmissionTime = endTask - beginTask;

        if (getHost() instanceof Client && command.equals(Protocol.SENDDATA)) {
            String line1 = nBytes + " bytes received in " + (transmissionTime / 1000) + " seconds (" + (nBytes / (transmissionTime / 1000)) + " bytes/sec)";
            String message = line1;
            infoGUI.setProgressLable(message);
        }
    }

    public int getCycleNo() {
        return cycleNo;
    }

    public int getLFR() {
        return LFR;
    }

    public int getLAF() {
        return LAF;
    }

    public void setFirstSeq(int firstSeq) {
        this.firstSeq = firstSeq;
    }

    public void setLastSeq(int lastSeq) {
        this.lastSeq = lastSeq;
    }

}
