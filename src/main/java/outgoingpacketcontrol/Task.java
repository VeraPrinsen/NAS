package outgoingpacketcontrol;

import general.Protocol;
import host.Host;

import java.util.Arrays;

public class Task implements Runnable {

    private Host host;
    private OutgoingData data;

    public Task(Host host, OutgoingData outgoingData) {
        this.host = host;
        this.data = outgoingData;
    }

    public void run() {
        sendPackets();
    }

    private void sendPackets() {
        int nPackets;
        if (data.getData().length % Protocol.maxDataSize == 0) {
            nPackets = (data.getData().length / Protocol.maxDataSize);
        } else {
            nPackets = (data.getData().length / Protocol.maxDataSize) + 1;
        }

        for (int i = 0; i < nPackets; i++) {
            int endIndex;
            if (i == nPackets - 1) {
                endIndex = data.getData().length;
            } else {
                endIndex = ((i+1)* Protocol.maxDataSize) + 1;
            }

            byte[] packet = Arrays.copyOfRange(data.getData(), i*Protocol.maxDataSize, endIndex);

            String sequenceCmd;
            if (nPackets == 1) {
                sequenceCmd = Protocol.SINGLE;
            } else if (i == 0) {
                sequenceCmd = Protocol.FIRST;
            } else if (i == nPackets - 1) {
                sequenceCmd = Protocol.LAST;
            } else {
                sequenceCmd = Protocol.CONTINUE;
            }

            // If host is not able to send because of sliding window, wait till the window opens again (keep updating)
            while (!data.getCommand().equals(Protocol.ACK) && !host.getSendingWindow().canSend(data.getTaskNo(), i)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            OutgoingPacket outgoingPacket = new OutgoingPacket(data, sequenceCmd, packet, i, data.getLAF());
            new SendPacket(host, outgoingPacket).run();
            // new Thread(new SendPacket(host, outgoingPacket)).start();
        }
    }
}
