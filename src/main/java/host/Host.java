package host;

import general.Protocol;
import incomingpacketcontrol.ReceivingWindow;
import outgoingpacketcontrol.SendingWindow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class Host {

    private DatagramSocket hostSocket;
    private SendingWindow sendingWindow;
    private ReceivingWindow receivingWindow;

    private ArrayList<String> expectedDownloads;
    private ArrayList<String> expectedUploads;

    public Host () {
        sendingWindow = new SendingWindow(this);
        receivingWindow = new ReceivingWindow(this);
        expectedDownloads = new ArrayList<>();
        expectedUploads = new ArrayList<>();
    }

    public void send(DatagramPacket datagramPacket) {
        try {
            hostSocket.send(datagramPacket);
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

    public void addExpectedDownloads(String fileName) {
        expectedDownloads.add(fileName);
    }

    public void addExpectedUploads(String fullFileName) {
        expectedUploads.add(fullFileName);
    }

    public String getExpectedDownloads(String fileName) {
        for (String s : expectedDownloads) {
            if (s.equals(fileName)) {
                return s;
            }
        }
        return null;
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

    public void createSocket(int portNumber) {
        try {
            this.hostSocket = new DatagramSocket(portNumber);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void setBroadCast(boolean doBroadcast) {
        try {
            hostSocket.setBroadcast(doBroadcast);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}
