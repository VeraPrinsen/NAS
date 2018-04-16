package host;

import incomingpacketcontrol.ReceivingWindow;
import outgoingpacketcontrol.SendingWindow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Host {

    private DatagramSocket hostSocket;
    private SendingWindow sendingWindow;
    private ReceivingWindow receivingWindow;

    private ArrayList<String> expectedDownloads;
    private ArrayList<String> expectedUploads;

    public Host () {
        sendingWindow = new SendingWindow();
        receivingWindow = new ReceivingWindow(this);
        expectedDownloads = new ArrayList<>();
        expectedUploads = new ArrayList<>();

        new Thread(receivingWindow).start();
    }

    public void send(DatagramPacket datagramPacket) {
        try {
            hostSocket.send(datagramPacket);
            // System.out.println(datagramPacket.getAddress().toString() + "/" + datagramPacket.getPort() + " | " + new String(datagramPacket.getData()) + " send");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramSocket getHostSocket() {
        return hostSocket;
    }

    public SendingWindow getSendingWindow() {
        return sendingWindow;
    }

    public ReceivingWindow getReceivingWindow() {
        return receivingWindow;
    }

    public ArrayList<String> getExpectedDownloads() {
        return expectedDownloads;
    }

    public ArrayList<String> getExpectedUploads() {
        return expectedUploads;
    }

    public void addExpectedDownloads(String fullFileName) {
        expectedDownloads.add(fullFileName);
    }

    public void addExpectedUploads(String fullFileName) {
        expectedUploads.add(fullFileName);
    }

    public String getExpectedDownloads(String fileName) {
        return "";
    }

    public String getExpectedUploads(String fileName) {
        for (String fullFileName : expectedUploads) {
            if (fullFileName.contains(fileName)) {
                return fullFileName;
            }
        }
        return null;
    }

    public void removeExpectedDownloads(String fileName) {
        expectedDownloads.remove(fileName);
    }

    public void removeExpectedUploads(String fileName) {
        expectedUploads.remove(fileName);
    }

    // TODO: remove when not used
    public void createSocket() {
        try {
            this.hostSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void createSocket(int portNumber) {
        try {
            this.hostSocket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}