package client;

import general.Protocol;
import general.Host;

// Client doesn't listen to the Server, only gets expected responses?
class Client extends Host {

    private ClientTUI clientTUI;
    boolean keepGoing = true;

    public Client() {
        this.clientTUI = new ClientTUI();
        createSocket(Protocol.DEFAULT_CLIENT_PORT);
        new Thread(new SocketListener(this)).start();
        start();
    }

    /**
     * Starts the client
     *      Show options
     */
    private void start() {
        TextResources.General.welcomeMessage();

        while (keepGoing) {
            TextResources.General.mainMenu();
            int response = readInt("Enter your choice");
            while (response < 0 || response > 5) {
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
        new Client();
    }
}