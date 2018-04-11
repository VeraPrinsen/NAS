package client;

import filereader.FileReaderClass;

import java.io.File;

public class ServerHandler implements Runnable {

    private String message;

    public ServerHandler(String message) {
        this.message = message;
    }

    public void run() {
        switch (message) {
            case "Send1":
                try {
                    UDPClient.sendFixedBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "UPLOAD":
                String pathname = "/Users/vera.prinsen/Documents/Module2/Eindopdracht/";
                //String filename = "testfile.txt";
                //String outputFilename = "testfileReturn.txt";
                //String filename = "testfile.docx";
                //String outputFilename = "testfileReturn.docx";
                //String filename = "rdtcInput5.png";
                //String outputFilename = "rdtcInput5Return.png";
                String filename = "mariokart.jpg";
                String outputFilename = "mariokartReturn.jpg";

                File file = new File(pathname + filename);
                byte[] array = FileReaderClass.fileToByteArray(file);
                try {
                    UDPClient.sendBytes(array, pathname + outputFilename);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            default:
                System.out.println("Unknown command");

        }
    }
}
