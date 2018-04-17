package client;

import fileoperators.FileReaderClass;
import fileoperators.FileChooserClass;
import general.Protocol;
import outgoingpacketcontrol.OutgoingData;

import java.io.File;
import java.net.InetAddress;

// TODO: Implement tasks Client input
public class ClientCommands {

    public static void download(Client client) {
        // OTHER IDEA:
        // ASK SERVER FOR FILELIST
        // CHOOSE FILE FROM LIST
        // RECEIVE PACKETS AND SAVE FILE
        // ALSO SERVER DOES NOT CHECK YET IF THE FILE IS ACTUALLY THERE

        String fileName = client.readString("What file do you want to download?");
        byte[] dataBytes = fileName.getBytes();
        client.addExpectedDownloads(fileName);
        OutgoingData outgoingData = new OutgoingData(Protocol.DOWNLOAD, client.getServerIP(), client.getServerPort(), dataBytes, Protocol.WS);
        client.getSendingWindow().addTask(outgoingData);
    }

    public static void upload(Client client) {
        // REQUEST IF YOU CAN UPLOAD A SPECIFIC FILE
        // IF APPROVED, SEND ALL DATA OF THE FILE
        // AT THE MOMENT, SERVER DOES NOT CHECK IF IT ALREADY HAS A FILE WITH THE SAME NAME

        // Choose file from window explorer
        FileChooserClass chooser = new FileChooserClass();
        try {
            chooser.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = chooser.getFile();

        // Save full filename in expected uploads
        String fullFileName = file.getAbsolutePath();
        String fileName = file.getName();
        if (Protocol.showInfo) {
            System.out.println("You choose to upload: " + fileName);
        }
        byte[] data = FileReaderClass.fileToByteArray(fullFileName);
        int fileSize = data.length;
        client.addExpectedUploads(fullFileName);

        // and send filename to server
        String dataSentence = fileName + Protocol.DELIMITER + fileSize;
        byte[] dataBytes = dataSentence.getBytes();
        OutgoingData outgoingData = new OutgoingData(Protocol.UPLOAD, client.getServerIP(), client.getServerPort(), dataBytes, Protocol.WS);
        client.getSendingWindow().addTask(outgoingData);
    }

    public static void fileList(Client client) {
        // ASK FOR FILELIST
        // RECEIVE PACKETS AND SHOW FILELIST

        String command = Protocol.REQUEST_FILELIST;
        InetAddress destinationIP = Protocol.getDefaultIp();
        int destinationPort = Protocol.DEFAULT_SERVER_PORT;
        byte[] data = new byte[1];
        data[0] = 0;

        OutgoingData outgoingData = new OutgoingData(command, destinationIP, destinationPort, data, Protocol.WS);
        client.getSendingWindow().addTask(outgoingData);
    }
}
