package host;

import client.Client;
import fileoperators.FileReaderClass;
import fileoperators.FileWriterClass;
import general.Protocol;
import incomingpacketcontrol.IncomingPacket;
import outgoingpacketcontrol.OutgoingPacket;
import outgoingpacketcontrol.SendPacket;
import outgoingpacketcontrol.SendingTask;
import server.Server;
import outgoingpacketcontrol.OutgoingData;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Reactions on received data, this is for the Server and Client together.
 * TODO: split this file into ClientReactions & ServerReactions, or create 2 subclasses (Client & Server)
 */
public class Reactions {

    // SERVER & CLIENT ==================================================================================================================

    /**
     * As reaction on the Download or Upload-Approved message, the file may be send using this method.
     */
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
        host.getSendingWindow().addTask(outgoingData);
    }

    /**
     * When all SENDDATA packets have been received, the file can be saved.
     */
    public static void saveFile(String fileName, int nBytes, byte[][] data) {
        FileWriterClass.byteArrayToFile(data, fileName);
        if (Protocol.showInfo) {
            System.out.println(fileName + " saved (" + nBytes + " bytes -" + data.length + " packets)");
        }
        // SEND A CONFIRMATION PACKET TO SOURCE
    }

    // SERVER ==============================================================================================================================

    /**
     * When the server receives a request for a file to be downloaded, this method is called.
     * TODO: Server must check if the file exists on the server, before sending this message
     */
    public static void sendDownloadApproved(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String command = Protocol.DOWNLOAD_APPROVED;
        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(outgoingData);
    }

    /**
     * When the server receives a request for a file to be uploaded to the server, this method is called.
     * TODO: Server must check if the file exists on the server, client can then maybe choose to overwrite it
     */
    public static void sendUploadApproved(Host host, byte[] data, InetAddress destinationIP, int destinationPort) {
        String command = Protocol.UPLOAD_APPROVED;
        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, data, Protocol.WS);
        host.getSendingWindow().addTask(outgoingData);
    }

    /**
     * When the server has saved the uploaded file, this message is send to confirm that to the client.
     */
    public static void sendUploadSaved(Host host, ArrayList<IncomingPacket> packetList) {
        int taskNo = packetList.get(0).getTaskNo();
        byte[] data = new byte[1];
        data[0] = 0;
        OutgoingData outgoingData = new OutgoingData(Protocol.UPLOAD_SAVED, packetList.get(0).getSourceIP(), packetList.get(0).getSourcePort(), data, 0);
        OutgoingPacket outgoingPacket = new OutgoingPacket(outgoingData, taskNo, Protocol.SINGLE, data, 0, 0);
        new Thread(new SendPacket(host, null, outgoingPacket)).start();
    }

    /**
     * When the client requested a list of files from the server, this method is called.
     */
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
        host.getSendingWindow().addTask(outgoingData);
    }

    // CLIENT ================================================================================================================================

    /**
     * When the server approved the download, the client calls this method and waits for the file to be received.
     */
    public static void saveDownloadApproved(Host host, byte[] dataArray) {
        String message = new String(dataArray);
        String[] args = message.split(Protocol.DELIMITER);
        host.addExpectedDownloads(args[0]);
    }

    /**
     * When the server sends the list of files, this method shows the files on the console.
     */
    public static void showFileList(byte[] data) {
        String message = new String(data);
        String[] listOfFiles = message.split(Protocol.DELIMITER);

        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(listOfFiles[i]);
        }
    }
}
