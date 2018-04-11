package general;

public class HeaderInfo {

    // HEADER INFO
    public static final int COMMANDSIZE = 2; // size of command in bytes
    public static final int TASKSIZE = 1;
    public static final int SEQUENCESIZE = 1;

    public static final int HEADERSIZE = COMMANDSIZE + TASKSIZE + SEQUENCESIZE;

    // COMMANDS (2 bytes)
    public static final String DOWNLOAD = "DL";
    public static final String UPLOAD = "UL";
    public static final String PAUSE = "PS";
    public static final String RESUME = "RS";
    public static final String ACK_OK = "AO";
    public static final String ACK_DENIED = "AD";
    public static final String FINAL = "FN";
    public static final String SENDDATA = "SD";

    // Other info
    public static final int maxDataSize = (1024 * 64) - 1;  // maximum size of packet in bytes
    public static final int maxTaskNo = 256;                // maximum task number (1 byte)
    public static final int maxSequenceNo = 256;            // maximum sequence number (1 byte)
    public static final int TIMEOUT = 3000;                 // Timeout for Acknowledgement (in ms)
    public static final int SWS = 100;                      // Send Window Size (# packets)
    public static final int RWS = 50;                       // Receive Window Size (# packets)


    public static final String DELIMITER = "_";
    public static final int DEFAULT_PORT = 9876;
}
