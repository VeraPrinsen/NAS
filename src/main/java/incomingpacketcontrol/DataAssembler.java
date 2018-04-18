package incomingpacketcontrol;

import host.Host;
import general.Protocol;
import general.Utils;
import host.Reactions;
import client.Client;
import server.Server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * If all the packets are received of a task, this class is used to assemble the data and respond on it.
 */
public class DataAssembler implements Runnable {

    private Host host;
    private ArrayList<IncomingPacket> packetList;

    public DataAssembler(Host host, ArrayList<IncomingPacket> packetList) {
        this.host = host;
        this.packetList = packetList;
    }

    public void run() {
        // Save data in an array of byte arrays
        byte[][] dataArray = new byte[packetList.size()][];
        for (int i = 0; i < packetList.size(); i++) {
            dataArray[packetList.get(i).getTotalSequenceNo()] = packetList.get(i).getData();
        }

        String command = packetList.get(0).getCommand();
        InetAddress sourceIP = packetList.get(0).getSourceIP();
        int sourcePort = packetList.get(0).getSourcePort();

        // Forward data to the right response
        // TODO: a lot of duplicate code here with the println()....
        switch (command) {
            case Protocol.DOWNLOAD:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | DOWNLOAD: " +  new String(getDataArray(dataArray)) + " received");
                }
                Reactions.sendDownloadApproved(host, getDataArray(dataArray), sourceIP, sourcePort);
                String fullFileNameDL = Protocol.getSavePathServer() + new String(getDataArray(dataArray));
                Reactions.sendFile(host, fullFileNameDL.getBytes(), sourceIP, sourcePort);
                break;

            case Protocol.UPLOAD:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD: " +  new String(getDataArray(dataArray)) + " received");
                }
                Reactions.sendUploadApproved(host, getDataArray(dataArray), sourceIP, sourcePort);
                break;

            case Protocol.DOWNLOAD_APPROVED:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | DOWNLOAD_APPROVED: " +  new String(getDataArray(dataArray)) + " received");
                }
                Reactions.saveDownloadApproved(host, getDataArray(dataArray));
                break;

            case Protocol.UPLOAD_APPROVED:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD_APPROVED: " +  new String(getDataArray(dataArray)) + " received");
                }
                Reactions.sendFile(host, getDataArray(dataArray), sourceIP, sourcePort);
                break;

            case Protocol.UPLOAD_SAVED:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD_SAVED received");
                }
                host.getSendingWindow().fileTransmissionDone(packetList.get(0).getTaskNo());
                break;

            case Protocol.REQUEST_FILELIST:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | REQUEST_FILELIST received");
                }
                Reactions.sendFileList(host, sourceIP, sourcePort);
                break;

            case Protocol.FILELIST:
                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | FILELIST received");
                }
                Reactions.showFileList(getDataArray(dataArray));
                break;

            case Protocol.SENDDATA:
                // TODO: This should be in it's own Reactions.method()
                String pathName;
                if (host instanceof Client) {
                    pathName = Protocol.SAVEPATH_CLIENT;
                } else {
                    pathName = Protocol.getSavePathServer();
                }

                String message = new String(packetList.get(0).getData());
                String[] args = message.split(Protocol.DELIMITER);
                String fullFileName = args[0];
                String[] fileNameArgs = fullFileName.split("/");
                String fileName = fileNameArgs[fileNameArgs.length - 1];
                int nBytes = Integer.parseInt(args[1]);

                if (Protocol.showInfo) {
                    System.out.println(sourceIP.toString() + "/" + sourcePort + " | SENDDATA: " + fileName + " " + nBytes + " bytes received");
                }
                byte[][] dataArray2 = Arrays.copyOfRange(dataArray, 1, dataArray.length);

                Reactions.saveFile(pathName + fileName, nBytes, dataArray2);
                if (host instanceof Server) {
                    Reactions.sendUploadSaved(host, packetList);
                } else {
                    host.getReceivingWindow().fileTransmissionDone(packetList.get(0).getTaskNo());
                }
                break;

            default:
                break;
        }
    }

    /**
     * Concatenate the array of byte arrays into one byte array
     */
    public byte[] getDataArray(byte[][] dataPackets) {
        byte[] data = dataPackets[0];
        if (dataPackets.length > 1) {
            for (int j = 1; j < dataPackets.length; j++) {
                if (dataPackets[j] != null) {
                    data = Utils.concat2byte(data, dataPackets[j]);
                }
            }
        }

        return data;
    }
}
