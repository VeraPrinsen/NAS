package general;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Protocol {

    // HEADER INFO
    public static final int FIRSTINDEX_COMMAND = 0;
    public static final int LASTINDEX_COMMAND = 1;
    public static final int COMMANDSIZE = LASTINDEX_COMMAND - FIRSTINDEX_COMMAND + 1; // size of command in bytes

    public static final int FIRSTINDEX_SEQUENCECMD = 2;
    public static final int LASTINDEX_SEQUENCECMD = 3;
    public static final int SEQUENCECMDSIZE = LASTINDEX_SEQUENCECMD - LASTINDEX_SEQUENCECMD + 1;

    public static final int FIRSTINDEX_TASK = 4;
    public static final int LASTINDEX_TASK = 4;
    public static final int TASKSIZE = LASTINDEX_TASK - FIRSTINDEX_TASK + 1;

    public static final int FIRSTINDEX_SEQUENCE = 5;
    public static final int LASTINDEX_SEQUENCE = 5;
    public static final int SEQUENCESIZE = LASTINDEX_SEQUENCE - FIRSTINDEX_SEQUENCE + 1;

    public static final int FIRSTINDEX_PACKETSIZE = 6;
    public static final int LASTINDEX_PACKETSIZE = 7;
    public static final int PACKETSIZESIZE = LASTINDEX_PACKETSIZE - FIRSTINDEX_PACKETSIZE + 1;

    public static final int FIRSTINDEX_CHECKSUM = 8;
    public static final int LASTINDEX_CHECKSUM = 8;
    public static final int CHECKSUMSIZE = LASTINDEX_CHECKSUM - FIRSTINDEX_CHECKSUM + 1;

    public static final int FIRSTINDEX_DATA = 9;
    public static final int HEADERSIZE = COMMANDSIZE + SEQUENCECMDSIZE + TASKSIZE + SEQUENCESIZE + PACKETSIZESIZE + CHECKSUMSIZE;

    // COMMANDS (2 bytes)
    public static final String DOWNLOAD = "DL";
    public static final String UPLOAD = "UL";
    public static final String PAUSE = "PS";
    public static final String RESUME = "RS";

    public static final String SENDDATA = "SD";

    public static final String ACK_OK = "AO";
    public static final String ACK_DENIED = "AD";

    public static final String FIRST = "FS";
    public static final String LAST = "LS";
    public static final String SINGLE = "SN";
    public static final String CONTINUE = "CN";


    // Other info
    public static final int maxPacketSize = 50;
    public static final int maxDataSize = maxPacketSize - HEADERSIZE;  // maximum size of packet in bytes (without header)
    public static final int maxTaskNo = 2^(TASKSIZE * 8);            // maximum task number (1 byte)
    public static final int maxSequenceNo = 2^(SEQUENCESIZE * 8);   // maximum sequence number (1 byte)
    public static final int TIMEOUT = 3000;                 // Timeout for Acknowledgement (in ms)
    public static final int SWS = 100;                      // Send Window Size (# packets)
    public static final int RWS = 50;                       // Receive Window Size (# packets)


    public static final String DELIMITER = "_";
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

}
