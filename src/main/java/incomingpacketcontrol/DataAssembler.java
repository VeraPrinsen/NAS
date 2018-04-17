package incomingpacketcontrol;

import host.Host;
import general.Protocol;
import general.Utils;
import host.Reactions;
import client.Client;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

// TODO: What must be done after receiving all these data?
public class DataAssembler implements Runnable {

    private Host host;
    private ArrayList<IncomingPacket> packetList;

    public DataAssembler(Host host, ArrayList<IncomingPacket> packetList) {
        this.host = host;
        this.packetList = packetList;
    }

    public void run() {
        byte[][] dataArray = new byte[packetList.size()][];
        for (int i = 0; i < packetList.size(); i++) {
            dataArray[packetList.get(i).getTotalSequenceNo()] = packetList.get(i).getData();
        }

        String command = packetList.get(0).getCommand();
        InetAddress sourceIP = packetList.get(0).getSourceIP();
        int sourcePort = packetList.get(0).getSourcePort();
        System.out.println(command + " received");
        switch (command) {
            case Protocol.DOWNLOAD:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | DOWNLOAD: " +  new String(getDataArray(dataArray)) + " received");
                Reactions.sendDownloadApproved(host, getDataArray(dataArray), sourceIP, sourcePort);
                String fullFileNameDL = Protocol.SAVEPATH_SERVER + new String(getDataArray(dataArray));
                Reactions.sendFile(host, fullFileNameDL.getBytes(), sourceIP, sourcePort);
                break;

            case Protocol.UPLOAD:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD: " +  new String(getDataArray(dataArray)) + " received");
                Reactions.sendUploadApproved(host, getDataArray(dataArray), sourceIP, sourcePort);
                break;

            case Protocol.DOWNLOAD_APPROVED:
                // for now do nothing
                break;

            case Protocol.UPLOAD_APPROVED:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD_APPROVED: " +  new String(getDataArray(dataArray)) + " received");
                Reactions.sendFile(host, getDataArray(dataArray), sourceIP, sourcePort);
                break;

            case Protocol.REQUEST_FILELIST:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | REQUEST_FILELIST received");
                Reactions.sendFileList(host, sourceIP, sourcePort);
                break;

            case Protocol.FILELIST:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | FILELIST received");
                Reactions.showFileList(getDataArray(dataArray));
                break;

            case Protocol.SENDDATA:
                String pathName;
                if (host instanceof Client) {
                    pathName = Protocol.SAVEPATH_CLIENT;
                } else {
                    pathName = Protocol.SAVEPATH_SERVER;
                }

                String message = new String(packetList.get(0).getData());
                String[] args = message.split(Protocol.DELIMITER);
                String fullFileName = args[0];
                String[] fileNameArgs = fullFileName.split("/");
                String fileName = fileNameArgs[fileNameArgs.length - 1];
                int nBytes = Integer.parseInt(args[1]);

                System.out.println(sourceIP.toString() + "/" + sourcePort + " | SENDDATA: " + fileName + " " + nBytes + " bytes received");
                byte[][] dataArray2 = Arrays.copyOfRange(dataArray, 1, dataArray.length);

                Reactions.saveFile(pathName + fileName, nBytes, getDataArray(dataArray2));
                break;

            default:
                break;
        }
    }

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
