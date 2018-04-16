package outgoingpacketcontrol;

import fileoperators.FileReaderClass;
import general.Protocol;
import general.Utils;
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
        if ((data.getData().length % Protocol.maxDataSize) == 0) {
            nPackets = (data.getData().length / Protocol.maxDataSize);
        } else {
            nPackets = (data.getData().length / Protocol.maxDataSize) + 1;
        }

        if (data.isFile()) {
            String packetString = data.getFullFileName() + Protocol.DELIMITER + data.getData().length;
            OutgoingPacket firstOutgoingPacket = new OutgoingPacket(data, Protocol.FIRST, packetString.getBytes(), 0, data.getLAF());
            new Thread(new SendPacket(host, firstOutgoingPacket)).start();
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
            if (nPackets == 1) {
                sequenceCmd = Protocol.SINGLE;
            } else if (j == 0) {
                sequenceCmd = Protocol.FIRST;
            } else if (i == nPackets - 1) {
                sequenceCmd = Protocol.LAST;
            } else {
                sequenceCmd = Protocol.CONTINUE;
            }

            // If host is not able to send because of sliding window, wait till the window opens again (keep updating)
            while (!data.getCommand().equals(Protocol.ACK) && !host.getSendingWindow().canSend(data.getTaskNo(), j)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            OutgoingPacket outgoingPacket = new OutgoingPacket(data, sequenceCmd, packet, j, data.getLAF());
            new Thread(new SendPacket(host, outgoingPacket)).start();
        }
    }
}
