package packagecontrol;

import general.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAssembler implements Runnable {

    private ArrayList<IncomingPacket> packetList;

    public DataAssembler(ArrayList<IncomingPacket> packetList) {
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

        System.out.println(new String(data));
    }
}
