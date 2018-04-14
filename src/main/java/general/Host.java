package general;

import packagecontrol.AckController;
import packagecontrol.TaskController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Host {

    private DatagramSocket hostSocket;
    private AckController ackController;
    private TaskController taskController;

    public Host () {
        ackController = new AckController();
        taskController = new TaskController(this);
        new Thread(taskController).start();
    }

    public void send(DatagramPacket datagramPacket) {
        try {
            hostSocket.send(datagramPacket);
            System.out.println(datagramPacket.getAddress().toString() + "/" + datagramPacket.getPort() + " | " + new String(datagramPacket.getData()) + " send");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DatagramSocket getHostSocket() {
        return hostSocket;
    }

    public AckController getAckController() {
        return ackController;
    }

    public TaskController getTaskController() {
        return taskController;
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
