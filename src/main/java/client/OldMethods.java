package client;

import fileoperators.FileReaderClass;
import fileoperators.FileWriterClass;
import general.Protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

// TODO: remove when finished
public class OldMethods {


    public static void oldLines() {
        String filename = "";
        byte[] bytesFile = FileReaderClass.fileToByteArray(filename);
    }


    public static void sendFixedBytes() throws Exception {
        // byte[] sendData;
        byte[] sendData = new byte[1024 * 63];

        // String sentence = inFromUser.readLine();
        String sentence = "";
        for (int i = 0; i < sendData.length; i++) {
            sentence = sentence + "A";
        }

        sendBytes(sentence.getBytes());
    }

    public static void sendBytes(byte[] sendData) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = null;
        while (IPAddress == null) {
            try {
                // IPAddress = InetAddress.getByName("applepi");
                // IPAddress = InetAddress.getByName("192.168.1.1");

                IPAddress = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                break;
            }
        }

        if (sendData.length <= (1024*63)) {
            // send data
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Protocol.DEFAULT_SERVER_PORT);
            String sendDataString = new String(sendPacket.getData());
            System.out.println("TO SERVER: " + sendDataString.length() + " bytes: " + sendDataString);
            clientSocket.send(sendPacket);

            // receive data
            byte[] receiveData = new byte[1024 * 63];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            // Show on console information about the packet
            int k = receivePacket.getLength();
            byte[] messageBytes = receivePacket.getData();
            String message = new String(messageBytes);

            // Convert DatagramPacket into a byte array of the right length
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(messageBytes));
            byte[] receivedData = new byte[k];

            for (int i = 0; i < k; i++) {
                receivedData[i] = dataInputStream.readByte();
            }

            System.out.println("FROM SERVER:" + k + " bytes: " + new String(receivedData));
            clientSocket.close();
        } else {
            System.out.println("Data is too large");
        }
    }

    public static void sendBytes(byte[] sendData, String outputFilename) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = null;
        while (IPAddress == null) {
            try {
                // IPAddress = InetAddress.getByName("applepi");
                // IPAddress = InetAddress.getByName("192.168.1.1");

                IPAddress = InetAddress.getByName("localhost");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                break;
            }
        }

        if (sendData.length <= (1024*63)) {
            // send data
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Protocol.DEFAULT_SERVER_PORT);
            String sendDataString = new String(sendPacket.getData());
            System.out.println("TO SERVER: " + sendDataString.length() + " bytes: " + sendDataString);
            clientSocket.send(sendPacket);

            // receive data
            byte[] receiveData = new byte[1024 * 63];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            // Show on console information about the packet
            int k = receivePacket.getLength();
            byte[] messageBytes = receivePacket.getData();
            String message = new String(messageBytes);

            // Convert DatagramPacket into a byte array of the right length
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(messageBytes));
            byte[] receivedData = new byte[k];

            for (int i = 0; i < k; i++) {
                receivedData[i] = dataInputStream.readByte();
            }

            FileWriterClass.byteArrayToFile(receivedData, outputFilename);

            System.out.println("FROM SERVER: " + k + " bytes: " + new String(receivedData));
            clientSocket.close();
        } else {
            System.out.println("Data is too large");
        }
    }
}
