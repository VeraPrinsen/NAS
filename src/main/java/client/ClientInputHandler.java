package client;

import fileoperators.FileReaderClass;

public class ClientInputHandler implements Runnable {

    private String message;

    public ClientInputHandler(String message) {
        this.message = message;
    }

    public void run() {
        switch (message) {
            case "HELP":
                CommandHandler.help();
                break;

            case "Send1":
                try {
                    Client.sendFixedBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "UPLOAD":
                String pathname = "/Users/vera.prinsen/Documents/Module2/Eindopdracht/";
                String filename = "testfile.txt";
                String outputFilename = "testfileReturn.txt";
                //String filename = "testfile.docx";
                //String outputFilename = "testfileReturn.docx";
                //String filename = "rdtcInput5.png";
                //String outputFilename = "rdtcInput5Return.png";
                //String filename = "mariokart.jpg";
                //String outputFilename = "mariokartReturn.jpg";

                byte[] array = FileReaderClass.fileToByteArray(pathname + filename);
                try {
                    Client.sendBytes(array, pathname + outputFilename);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            default:
                //CommandHandler.defaultsend(client);

        }
    }
}
