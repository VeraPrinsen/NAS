package packagecontrol;

import fileoperators.FileReaderClass;
import general.Info;
import general.Host;

import java.net.InetAddress;
import java.util.Arrays;

public class SendDataTask extends Task {

    private Host host;
    private InetAddress destinationIP;
    private int destinationPort;

    private byte[] data;
    private int sequenceNo;

    public SendDataTask(Host host, InetAddress destinationIP, int destinationPort, int taskNo, byte[] data) {
        super(taskNo);
        this.host = host;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.data = data;
        sequenceNo = 0;
    }

    public void run() {
        sendFile();
    }

    // TO DO: Hier moet nog wel gecheckt worden of een packet gestuurd mag worden volgens de Sliding Window
    // TO DO: Sequence number moet na 255 terug naar 0 gaan
    private void sendFile() {
        int nPackets = (int) Math.ceil(data.length / Info.maxDataSize);

        for (int i = 0; i < nPackets; i++) {
            byte[] packet = Arrays.copyOfRange(data, i* Info.maxDataSize, ((i+1)* Info.maxDataSize) + 1);

            String command;
            if (i == nPackets - 1) {
                command = Info.FINAL;
            } else {
                command = Info.SENDDATA;
            }

            BytePacket newPacket = new BytePacket(packet, command, getTaskNo(), this.sequenceNo, this.destinationIP, this.destinationPort);
            new Thread(new SendPacket(host, newPacket)).start();
            this.sequenceNo = (this.sequenceNo + 1) % Info.maxDataSize;
        }
    }
}
