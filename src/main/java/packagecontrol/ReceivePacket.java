package packagecontrol;

import java.net.DatagramPacket;

public class ReceivePacket implements Runnable {

    private DatagramPacket datagramPacket;

    public ReceivePacket(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

    public void run() {
        IncomingPacket incomingPacket = new IncomingPacket(datagramPacket);

        // check packetSize && checkSum

        // react on package
        // ack
        // further actions
    }



}
