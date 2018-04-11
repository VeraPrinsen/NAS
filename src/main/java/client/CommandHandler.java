package client;

import fileoperators.FileReaderClass;

public class CommandHandler {

    public static void help() {

    }

    public static void download(String filename) {
        // send request to server
        // if ACK_OK received, start sending data

        byte[] bytesFile = FileReaderClass.fileToByteArray(filename);
        // sendDataTask, bytesFile = data;
    }

    public static void upload() {

    }

    public static void defaultsend(Client client) {

    }
}
