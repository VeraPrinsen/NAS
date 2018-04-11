package general;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Info {

    // HEADER INFO
    public static final int FIRSTINDEX_COMMAND = 0;
    public static final int LASTINDEX_COMMAND = 1;
    public static final int COMMANDSIZE = LASTINDEX_COMMAND - FIRSTINDEX_COMMAND + 1; // size of command in bytes

    public static final int FIRSTINDEX_TASK = 2;
    public static final int LASTINDEX_TASK = 2;
    public static final int TASKSIZE = LASTINDEX_TASK - FIRSTINDEX_TASK + 1;

    public static final int FIRSTINDEX_SEQUENCE = 3;
    public static final int LASTINDEX_SEQUENCE = 3;
    public static final int SEQUENCESIZE = LASTINDEX_SEQUENCE - FIRSTINDEX_SEQUENCE + 1;

    public static final int FIRSTINDEX_DATA = 4;
    public static final int HEADERSIZE = COMMANDSIZE + TASKSIZE + SEQUENCESIZE;

    // COMMANDS (2 bytes)
    public static final String DOWNLOAD = "DL";
    public static final String UPLOAD = "UL";
    public static final String PAUSE = "PS";
    public static final String RESUME = "RS";
    public static final String ACK_OK = "AO";
    public static final String ACK_DENIED = "AD";
    public static final String SENDDATA = "SD";
    public static final String FINAL = "FN";


    // Other info
    public static final int maxDataSize = 1024 * 63;  // maximum size of packet in bytes (without header)
    public static final int maxTaskNo = 256;                // maximum task number (1 byte)
    public static final int maxSequenceNo = 256;            // maximum sequence number (1 byte)
    public static final int TIMEOUT = 3000;                 // Timeout for Acknowledgement (in ms)
    public static final int SWS = 100;                      // Send Window Size (# packets)
    public static final int RWS = 50;                       // Receive Window Size (# packets)


    public static final String DELIMITER = "_";
    public static final int DEFAULT_PORT = 9876;
    public static InetAddress DEFAULT_IP = null;
    static {
        try {
            DEFAULT_IP = InetAddress.getByName("192.168.1.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
