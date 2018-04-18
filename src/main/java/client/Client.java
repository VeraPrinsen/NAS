package client;

import general.Protocol;
import host.Host;
import host.SocketListener;
import outgoingpacketcontrol.OutgoingData;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client extends Host {

    private ClientTUI clientTUI;
    private boolean keepGoing = true;

    private InetAddress serverIP;
    private int serverPort;

    public Client() {
        super();
        this.clientTUI = new ClientTUI();
        createSocket(Protocol.DEFAULT_CLIENT_PORT);
        setBroadCast(true);
        new Thread(new SocketListener(this)).start();

        byte[] data = new byte[1];
        data[0] = 0;
        String command = Protocol.HELLO;
        OutgoingData outgoingData = new OutgoingData(command, null, 0, data, Protocol.WS);
        getSendingWindow().addTask(outgoingData);

        //serverIP = Protocol.getDefaultIp();
        //serverPort = Protocol.DEFAULT_SERVER_PORT;
    }

    public void setServerIP(String IP) {
        try {
            this.serverIP = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public InetAddress getServerIP() {
        return serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    /**
     * Starts the client
     *      Show options
     */
    private void start() {
        TextResources.General.welcomeMessage();

        while (keepGoing) {
            TextResources.General.mainMenu();
            // kan dit ook naar TextResources? Met de client in de input?
            int response = readInt("Enter your choice");
            while (response < 0 || response > 3) {
                response = readInt("Invalid option, choose again");
            }
            new ClientInputHandler(this, response);
        }
    }

    // Input & Output ======================================================
    public void print(String msg) {
        clientTUI.print(msg);
    }

    public String readString(String prompt) {
        return clientTUI.readString(prompt);
    }

    public int readInt(String prompt) {
        return clientTUI.readInt(prompt);
    }

    // Exit ================================================================
    public void exit() {
        TextResources.General.exit();
        keepGoing = false;
    }

    public static void main(String[] args) {
        new Client().start();
    }
}