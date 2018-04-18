package outgoingpacketcontrol;

import client.Client;
import general.Protocol;
import gui.InfoGUI;
import host.Host;
import host.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class SendingTask extends Task implements Runnable {

    private OutgoingData data;

    private InfoGUI infoGUI;
    private int nPackets;
    private int packetsSend;

    // SENDING WINDOW INFORMATION
    private ArrayList<String> receivedAcks;
    private int cycleNo;
    private int LFS;
    private int LAR;
    private int LAF;
    private int totalLFS;
    private int totalLAR;
    private int totalLAF;

    private int retransmissions;
    private long beginTask;
    private long endTask;

    public SendingTask(Host host, OutgoingData outgoingData) {
        super(host);
        this.beginTask = System.currentTimeMillis();
        this.retransmissions = 0;
        this.data = outgoingData;

        this.receivedAcks = new ArrayList<>();
        this.cycleNo = 0;
        this.LFS = -1;
        this.LAR = -1;
        this.LAF = Protocol.WS;
        this.totalLFS = -1;
        this.totalLAR = -1;
        this.totalLAF = Protocol.WS;

        if (host instanceof Client && data.getCommand().equals(Protocol.SENDDATA)) {
            infoGUI = new InfoGUI(this);
            String fullFileName = data.getFullFileName();
            String[] args = fullFileName.split("/");
            infoGUI.setTitleLabel("Upload " + args[args.length-1]);
        }
        packetsSend = 0;
    }

    @Override
    public void run() {
        sendPackets();
    }

    private void sendPackets() {
        if ((data.getData().length % Protocol.maxDataSize) == 0) {
            nPackets = (data.getData().length / Protocol.maxDataSize);
        } else {
            nPackets = (data.getData().length / Protocol.maxDataSize) + 1;
        }

        if (data.isFile()) {
            String packetString = data.getFullFileName() + Protocol.DELIMITER + data.getData().length;
            OutgoingPacket firstOutgoingPacket = new OutgoingPacket(data, data.getTaskNo(), Protocol.FIRST, packetString.getBytes(), 0, data.getLAF());
            new Thread(new SendPacket(getHost(), this, firstOutgoingPacket)).start();
        }

        for (int i = 0; i < nPackets; i++) {
            int j = i;
            if (data.isFile()) {
                j = j + 1;
            }

            int endIndex;
            if (i == nPackets - 1) {
                endIndex = data.getData().length;
            } else {
                endIndex = ((i+1)* Protocol.maxDataSize);
            }

            byte[] packet = Arrays.copyOfRange(data.getData(), i*Protocol.maxDataSize, endIndex);

            String sequenceCmd;
            if (nPackets == 1 && i == j) {
                sequenceCmd = Protocol.SINGLE;
            } else if (j == 0) {
                sequenceCmd = Protocol.FIRST;
            } else if (i == nPackets - 1) {
                sequenceCmd = Protocol.LAST;
            } else {
                sequenceCmd = Protocol.CONTINUE;
            }

            // If host is not able to send because of sliding window, wait till the window opens again (keep updating)
            while (isPaused() || (!data.getCommand().equals(Protocol.ACK) && !getHost().getSendingWindow().canSend(data.getTaskNo(), j))) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            OutgoingPacket outgoingPacket = new OutgoingPacket(data, data.getTaskNo(), sequenceCmd, packet, j, data.getLAF());
            new Thread(new SendPacket(getHost(), this, outgoingPacket)).start();
        }

        if (data.getCommand().equals(Protocol.HELLO)) {
            getHost().setBroadCast(false);
        }
    }

    public synchronized void setPacketSend() {
        packetsSend++;
        if (packetsSend == nPackets+1) {
            infoGUI.setProgressLable("Server is saving the file...");
        } else {
            infoGUI.setProgressLable(packetsSend + " of " + (nPackets + 1) + " send.");
        }
    }

    public void fileTransmissionDone() {
        this.endTask = System.currentTimeMillis();
        long transmissionTime = endTask - beginTask;
        int nBytes = data.getData().length;

        if (getHost() instanceof Client && data.getCommand().equals(Protocol.SENDDATA)) {
            String line1 = nBytes + " bytes send in " + (transmissionTime/1000) + " seconds (" + (nBytes/(transmissionTime/1000)) + " bytes/sec)";
            String line2 = retransmissions + " packets had to be retransmitted.";
            String message = "<html>" + line1 + "<br/>" + line2 + "</html>";
            infoGUI.setProgressLable(message);
        }
    }

    public synchronized void addRetransmissions(int retransmissions) {
        this.retransmissions = this.retransmissions + retransmissions;
    }

    public boolean hasAck(String requestedAck) {
        return receivedAcks.contains(requestedAck);
    }

    public int getNAcksReceived() {
        return receivedAcks.size();
    }

    public int getCycleNo() {
        return cycleNo;
    }

    public int getLFS() {
        return LFS;
    }

    public int getLAR() {
        return LAR;
    }

    public int getLAF() {
        return LAF;
    }

    public int getTotalLFS() {
        return totalLFS;
    }

    public int getTotalLAR() {
        return totalLAR;
    }

    public int getTotalLAF() {
        return totalLAF;
    }

    public void addReceivedAck(String receivedAck) {
        this.receivedAcks.add(receivedAck);
    }

    public void setCycleNo(int cycleNo) {
        this.cycleNo = cycleNo;
    }

    public void setLFS(int LFS) {
        this.LFS = LFS;
    }

    public void setLAR(int LAR) {
        this.LAR = LAR;
    }

    public void setLAF(int LAF) {
        this.LAF = LAF;
    }

    public void setTotalLFS(int totalLFS) {
        this.totalLFS = totalLFS;
    }

    public void setTotalLAR(int totalLAR) {
        this.totalLAR = totalLAR;
    }

    public void setTotalLAF(int totalLAF) {
        this.totalLAF = totalLAF;
    }

}
