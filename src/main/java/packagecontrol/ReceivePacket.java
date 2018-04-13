package packagecontrol;

import general.Host;

import java.net.DatagramPacket;

public class ReceivePacket implements Runnable {

    private Host host;
    private DatagramPacket datagramPacket;

    public ReceivePacket(Host host, DatagramPacket datagramPacket) {
        this.host = host;
        this.datagramPacket = datagramPacket;
    }

    public void run() {
        IncomingPacket incomingPacket = new IncomingPacket(datagramPacket);

        // check packetSize && checkSum

        // react on package
        // ack
        new Thread(new ReactOnMessage(host, incomingPacket)).start();

        // further actions
    }



}
