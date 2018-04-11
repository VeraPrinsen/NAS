package client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

class UDPClient {

    private ClientTUI clientTUI;
    private Thread clientTUIThread;

    public UDPClient() {
        this.clientTUI = new ClientTUI();
        this.clientTUIThread = new Thread(clientTUI);
        clientTUIThread.start();
    }

    public static void main(String[] args) {
        new UDPClient();
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

        byte[] receiveData = new byte[1024 * 63];

        if (sendData.length <= (1024*63)) {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            String sendDataString = new String(sendPacket.getData());
            System.out.println("TO SERVER: " + sendDataString.length() + " bytes: " + sendDataString);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            byte[] receivedDataByteArray = receivePacket.getData();
            String modifiedSentence = new String(receivedDataByteArray);
            System.out.println("FROM SERVER:" + modifiedSentence.length() + " bytes: " + modifiedSentence);
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

        byte[] receiveData = new byte[1024 * 63];

        if (sendData.length <= (1024*63)) {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            String sendDataString = new String(sendPacket.getData());
            System.out.println("TO SERVER: " + sendDataString.length() + " bytes: " + sendDataString);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            byte[] receivedDataByteArray = receivePacket.getData();
            String modifiedSentence = new String(receivedDataByteArray);
            System.out.println("FROM SERVER:" + modifiedSentence.length() + " bytes: " + modifiedSentence);

            clientSocket.close();
        } else {
            System.out.println("Data is too large");
        }
    }
}