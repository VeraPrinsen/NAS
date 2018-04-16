package general;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Protocol {

    // HEADER INFO
    public static final int FIRSTINDEX_CHECKSUM = 0;
    public static final int LASTINDEX_CHECKSUM = 3;
    public static final int CHECKSUMSIZE = LASTINDEX_CHECKSUM - FIRSTINDEX_CHECKSUM + 1;

    public static final int FIRSTINDEX_DATASIZE = 4;
    public static final int LASTINDEX_DATASIZE = 5;
    public static final int DATASIZESIZE = LASTINDEX_DATASIZE - FIRSTINDEX_DATASIZE + 1;

    public static final int FIRSTINDEX_COMMAND = 6;
    public static final int LASTINDEX_COMMAND = 7;
    public static final int COMMANDSIZE = LASTINDEX_COMMAND - FIRSTINDEX_COMMAND + 1;

    public static final int FIRSTINDEX_SEQUENCECMD = 8;
    public static final int LASTINDEX_SEQUENCECMD = 9;
    public static final int SEQUENCECMDSIZE = LASTINDEX_SEQUENCECMD - FIRSTINDEX_SEQUENCECMD + 1;

    public static final int FIRSTINDEX_TASK = 10;
    public static final int LASTINDEX_TASK = 10;
    public static final int TASKSIZE = LASTINDEX_TASK - FIRSTINDEX_TASK + 1;

    public static final int FIRSTINDEX_SEQUENCE = 11;
    public static final int LASTINDEX_SEQUENCE = 12;
    public static final int SEQUENCESIZE = LASTINDEX_SEQUENCE - FIRSTINDEX_SEQUENCE + 1;

    public static final int FIRSTINDEX_LAF = 13;
    public static final int LASTINDEX_LAF = 14;
    public static final int LAFSIZE = LASTINDEX_LAF - FIRSTINDEX_LAF + 1;

    public static final int FIRSTINDEX_DATA = 15;
    public static final int HEADERSIZE = CHECKSUMSIZE + COMMANDSIZE + SEQUENCECMDSIZE + LAFSIZE + TASKSIZE + SEQUENCESIZE + DATASIZESIZE;

    // COMMANDS (2 bytes)
    public static final String DOWNLOAD = "DL";
    public static final String DOWNLOAD_APPROVED = "DA";
    public static final String DOWNLOAD_DENIED = "DD";

    public static final String UPLOAD = "UL";
    public static final String UPLOAD_APPROVED = "UA";
    public static final String UPLOAD_DENIED = "UD";

    public static final String REQUEST_FILELIST = "AF";
    public static final String FILELIST = "FL";

    public static final String SENDDATA = "SD";

    // Reaction to specific task of other host
    public static final String ACK = "AK";
    public static final String DATA_RECEIVED = "DR";
    public static final String PAUSE = "PS";
    public static final String RESUME = "RS";

    // Says something about how many more packets are coming
    public static final String FIRST = "FS";
    public static final String LAST = "LS";
    public static final String SINGLE = "SN";
    public static final String CONTINUE = "CN";

    // Other info
    public static final int maxPacketSize = 100;                                   // maximum size of packet in bytes (data and header)
    public static final int maxDataSize = maxPacketSize - HEADERSIZE;               // maximum size of data in bytes (without header)
    public static final int maxTaskNo = (int) Math.pow(2, TASKSIZE*8);              // maximum task number (1 byte)
    public static final int maxSequenceNo = (int) Math.pow(2, SEQUENCESIZE*8);      // maximum sequence number (1 byte)
    public static final int TIMEOUT = 3000;                                         // Timeout for Acknowledgement (in ms)
    public static final int WS = maxSequenceNo / 4;                                 // Send Window Size (# packets)

    public static final String DELIMITER = "@@@";
    public static final int DEFAULT_SERVER_PORT = 9876;
    public static final int DEFAULT_CLIENT_PORT = 4567;
    public static InetAddress getDefaultIp() {
        try {
            // return InetAddress.getByName("192.168.1.1");
            return InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static final String SAVEPATH_SERVER = "saves/";
    public static final String SAVEPATH_CLIENT = "Users/vera.prinsen/Documents/Module2/Eindopdracht/saves/";

}
