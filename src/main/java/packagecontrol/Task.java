package packagecontrol;

import general.Protocol;
import general.Host;

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
        int nPackets = (int) Math.ceil((double) data.getData().length / Protocol.maxDataSize);
        int totalSequenceNo = 0;

        int LAR = -1;
        int LFS = -1;

        for (int i = 0; i < nPackets; i++) {
            int endIndex;
            if (i == nPackets - 1) {
                endIndex = data.getData().length;
            } else {
                endIndex = ((i+1)* Protocol.maxDataSize) + 1;
            }

            byte[] packet = Arrays.copyOfRange(data.getData(), i* Protocol.maxDataSize, endIndex);

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
            while (LFS - LAR >= Protocol.SWS) {
                if (host.getAckController().hasAck(data.getTaskNo(), LAR + 1)) {
                    LAR = LAR + 1;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            OutgoingPacket outgoingPacket = new OutgoingPacket(data, sequenceCmd, packet, totalSequenceNo % Protocol.maxSequenceNo);
            new Thread(new SendPacket(host, outgoingPacket)).start();
            host.getAckController().sendPacket(outgoingPacket.getTaskNo(), totalSequenceNo);
            LFS = totalSequenceNo;
            totalSequenceNo++;
        }
    }
}
