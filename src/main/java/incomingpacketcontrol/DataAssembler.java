package incomingpacketcontrol;

import host.Host;
import general.Protocol;
import general.Utils;
import host.Reactions;
import client.Client;
import java.net.InetAddress;
import java.util.ArrayList;

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

        byte[] data = dataArray[0];
        if (dataArray.length > 1) {
            for (int j = 1; j < dataArray.length; j++) {
                data = Utils.concat2byte(data, dataArray[j]);
            }
        }

        String command = packetList.get(0).getCommand();
        InetAddress sourceIP = packetList.get(0).getSourceIP();
        int sourcePort = packetList.get(0).getSourcePort();
        switch (command) {
            case Protocol.DOWNLOAD:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | DOWNLOAD: " +  new String(data) + " received");
//                Reactions.sendDownloadApproved(host, data, sourceIP, sourcePort);
                break;

            case Protocol.UPLOAD:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD: " +  new String(data) + " received");
                Reactions.sendUploadApproved(host, data, sourceIP, sourcePort);
                break;

            case Protocol.UPLOAD_APPROVED:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | UPLOAD_APPROVED: " +  new String(data) + " received");
                Reactions.sendFile(host, data, sourceIP, sourcePort);
                break;

            case Protocol.REQUEST_FILELIST:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | REQUEST_FILELIST received");
                Reactions.sendFileList(host, sourceIP, sourcePort);
                break;

            case Protocol.FILELIST:
                System.out.println(sourceIP.toString() + "/" + sourcePort + " | FILELIST received");
                Reactions.showFileList(data);
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
                Reactions.saveFile(pathName + fileName, nBytes, data);
                break;

            default:
                break;
        }
    }
}
