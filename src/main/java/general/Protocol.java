package general;

public class Protocol {

    public static class General {

    }

    public static class Client {

        // COMMANDS
        public static final String DOWNLOAD = "D";
        public static final String UPLOAD = "U";
        public static final String PAUSE = "P";
        public static final String RESUME = "R";
        public static final String ACK = "A";
        public static final String FINAL = "F";
        public static final String SENDDATA = "S";

    }

    public static class Server {
        public static final String SENDDATA = "S";
        public static final String ACK = "A";
        public static final String FINAL = "F";
    }


}
