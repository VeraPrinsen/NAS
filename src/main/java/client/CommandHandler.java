package client;

import general.Host;
import general.Protocol;
import packagecontrol.OutgoingData;
import packagecontrol.Task;

import java.net.InetAddress;

public class CommandHandler {

    public static void help() {

    }

    public static void download() {
        TextResources.Download.askForFile();
    }

    public static void upload() {

    }

    public static void fileList() {

    }

    public static void testSmallPacket(Host host) {
        String command = Protocol.SENDDATA;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getAckController().getNewTask();
        byte[] data = "Dit is een stuk data".getBytes();

        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data);
        host.getAckController().addTask(taskNo);
        new Thread(new Task(host, outgoingData)).start();
    }

    public static void testLargePacket(Host host) {
        String command = Protocol.SENDDATA;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        int taskNo = host.getAckController().getNewTask();
        byte[] data = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.".getBytes();
        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data);
        host.getAckController().addTask(taskNo);
        new Thread(new Task(host, outgoingData)).start();
    }

}
