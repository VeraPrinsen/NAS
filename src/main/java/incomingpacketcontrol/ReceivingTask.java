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
            String line1 = nBytes + " bytes received in " + (transmissionTime/1000) + " seconds (" + (nBytes/(transmissionTime/1000)) + " bytes/sec)";
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

    public int getTotalLFR() {
        return totalLFR;
    }

    public int getTotalLAF() {
        return totalLAF;
    }

    public int getFirstSeq() {
        return firstSeq;
    }

    public int getLastSeq() {
        return lastSeq;
    }

    public boolean isTaskDone() {
        return taskDone;
    }

    public void setLFR(int LFR) {
        this.LFR = LFR;
    }

    public void setLAF(int LAF) {
        this.LAF = LAF;
    }

    public void setTotalLFR(int totalLFR) {
        this.totalLFR = totalLFR;
    }

    public void setTotalLAF(int totalLAF) {
        this.totalLAF = totalLAF;
    }

    public void setFirstSeq(int firstSeq) {
        this.firstSeq = firstSeq;
    }

    public void setLastSeq(int lastSeq) {
        this.lastSeq = lastSeq;
    }

    public void setTaskDone(boolean taskDone) {
        this.taskDone = taskDone;
    }

    public void setTaskDoneTime(long time) {
        this.taskDoneTime = time;
    }
}
