package host;

import fileoperators.FileReaderClass;
import fileoperators.FileWriterClass;
import host.Host;
import general.Protocol;
import general.Utils;
import outgoingpacketcontrol.OutgoingData;
import outgoingpacketcontrol.Task;

import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Base64;

public class Reactions {

    public static void sendUploadApproved(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String command = Protocol.UPLOAD_APPROVED;
        int taskNo = host.getSendingWindow().getNewTask();
        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new Task(host, outgoingData)).start();
    }

    public static void sendDownloadApproved(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String command = Protocol.UPLOAD_APPROVED;
        int taskNo = host.getSendingWindow().getNewTask();
        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new Task(host, outgoingData)).start();
    }

    public static void sendFile(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String dataSentence = new String(data);
        String[] args = dataSentence.split(Protocol.DELIMITER);

        String fullFileName = host.getExpectedUploads(args[0]);
        byte[] dataBytes = FileReaderClass.fileToByteArray(fullFileName);

        String command = Protocol.SENDDATA;
        int taskNo = host.getSendingWindow().getNewTask();

        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, fullFileName, dataBytes, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new Task(host, outgoingData)).start();
    }

    public static void saveFile(String fileName, int nBytes, byte[] data) {
        FileWriterClass.byteArrayToFile(data, fileName);
        System.out.println(fileName + " saved (" + nBytes + "-" + data.length + ")");
    }

    public static void sendFileList(Host host, InetAddress destinationIP, int destinationPort) {
        File folder = new File(Protocol.SAVEPATH_SERVER);
        File[] listOfFiles = folder.listFiles();

        String message = "";
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                message = message + listOfFiles[i].getName();
            }
            if (i != listOfFiles.length - 1) {
                message = message + Protocol.DELIMITER;
            }
        }
        byte[] data = message.getBytes();

        String command = Protocol.FILELIST;
        int taskNo = host.getSendingWindow().getNewTask();

        OutgoingData outgoingData = new OutgoingData(command, taskNo, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(taskNo);
        new Thread(new Task(host, outgoingData)).start();
    }

    public static void showFileList(byte[] data) {
        String message = new String(data);
        String[] listOfFiles = message.split(Protocol.DELIMITER);

        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(listOfFiles[i]);
        }
    }
}
