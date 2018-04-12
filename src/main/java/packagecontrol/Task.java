package packagecontrol;

import general.Info;
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

    // TO DO: Hier moet nog wel gecheckt worden of een packet gestuurd mag worden volgens de Sliding Window
    // TO DO: Sequence number moet na 255 terug naar 0 gaan
    private void sendPackets() {
        int nPackets = (int) Math.ceil((double) data.getData().length / Info.maxDataSize);
        int sequenceNo = 0;

        for (int i = 0; i < nPackets; i++) {
            int endIndex;
            if (i == nPackets - 1) {
                endIndex = data.getData().length;
            } else {
                endIndex = ((i+1)* Info.maxDataSize) + 1;
            }

            byte[] packet = Arrays.copyOfRange(data.getData(), i* Info.maxDataSize, endIndex);

            String tempCommand;
            if (data.getCommand().equals(Info.SENDDATA)) {
                if (nPackets == 1) {
                    tempCommand = Info.SINGLE;
                } else if (i == 0) {
                    tempCommand = Info.FIRST;
                } else if (i == nPackets - 1) {
                    tempCommand = Info.LAST;
                } else {
                    tempCommand = Info.SENDDATA;
                }
            } else {
                tempCommand = data.getCommand();
            }

            OutgoingPacket outgoingPacket = new OutgoingPacket(data, tempCommand, packet, sequenceNo);
            new Thread(new SendPacket(host, outgoingPacket)).start();
            sequenceNo = (sequenceNo + 1) % Info.maxDataSize;
        }
    }
}
