package client;

import filereader.FileReader;

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
                // String filename = "/Users/vera.prinsen/Documents/Module2/Eindopdracht/testfile.txt";
                String filename = "/Users/vera.prinsen/Documents/Module2/Eindopdracht/testfile.docx";
                String outputFilename = "/Users/vera.prinsen/Documents/Module2/Eindopdracht/testfileReturn.docx";
                File file = new File(filename);
                byte[] array = FileReader.fileToByteArray(file);
                try {
                    UDPClient.sendBytes(array, outputFilename);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            default:
                System.out.println("Unknown command");

        }
    }
}
