package packagecontrol;

import filereader.FileReaderClass;
import general.HeaderInfo;
import general.Host;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class SendFileTask implements Runnable {

    private Host host;
    private InetAddress destinationIP;
    private int destinationPort;
    private byte[] file;
    private int taskNo;
    private int sequenceNo;

    public SendFileTask(Host host, InetAddress destinationIP, int destinationPort, int taskNo, String filename) {
        this.host = host;
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
        this.taskNo = taskNo;
        sequenceNo = 0;
        file = FileReaderClass.fileToByteArray(filename);
    }

    public void run() {
        sendFile();
    }

    // TO DO: Hier moet nog wel gecheckt worden of een packet gestuurd mag worden volgens de Sliding Window
    // TO DO: Sequence number moet na 255 terug naar 0 gaan
    private void sendFile() {
        int nPackets = (int) Math.ceil(file.length / HeaderInfo.maxDataSize);

        for (int i = 0; i < nPackets; i++) {
            byte[] packet = Arrays.copyOfRange(file, i*HeaderInfo.maxDataSize, ((i+1)*HeaderInfo.maxDataSize) + 1);
            new Thread(new SendPacket(host, destinationIP, destinationPort, HeaderInfo.SENDDATA, taskNo, sequenceNo, packet)).start();
            sequenceNo = (sequenceNo + 1) % HeaderInfo.maxDataSize;
        }
    }



}
