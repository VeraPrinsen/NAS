package host;

import fileoperators.FileReaderClass;
import fileoperators.FileWriterClass;
import general.Protocol;
import outgoingpacketcontrol.SendingTask;
import server.Server;
import outgoingpacketcontrol.OutgoingData;

import java.io.File;
import java.net.InetAddress;

public class Reactions {

    public static void sendUploadApproved(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String command = Protocol.UPLOAD_APPROVED;
        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(host, outgoingData);
    }

    public static void sendDownloadApproved(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String command = Protocol.DOWNLOAD_APPROVED;
        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(host, outgoingData);
    }

    public static void sendFile(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String dataSentence = new String(data);
        String[] args = dataSentence.split(Protocol.DELIMITER);

        String fullFileName;
        if (host instanceof Server) {
            fullFileName = args[0];
        } else {
            fullFileName = host.getExpectedUploads(args[0]);
        }

        byte[] dataBytes = FileReaderClass.fileToByteArray(fullFileName);

        String command = Protocol.SENDDATA;
        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, fullFileName, dataBytes, Protocol.WS);
        host.getSendingWindow().addTask(host, outgoingData);
    }

    public static void saveFile(String fileName, int nBytes, byte[][] data) {
        FileWriterClass.byteArrayToFile(data, fileName);
        System.out.println(fileName + " saved (" + nBytes + "-" + data.length + ")");

        // SEND A CONFIRMATION PACKET TO SOURCE
    }

    public static void sendFileList(Host host, InetAddress destinationIP, int destinationPort) {
        File folder = new File(Protocol.getSavePathServer());
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
        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(host, outgoingData);
    }

    public static void showFileList(byte[] data) {
        String message = new String(data);
        String[] listOfFiles = message.split(Protocol.DELIMITER);

        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(listOfFiles[i]);
        }
    }
}
