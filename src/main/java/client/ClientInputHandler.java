package client;

/**
 * When an option is choosen from the mainmenu, this class forwards it to the right method.
 */
public class ClientInputHandler {

    private Client client;
    private int choice;

    public ClientInputHandler(Client client, int choice) {
        this.client = client;
        this.choice = choice;
        start();
    }

    private void start() {
        switch (choice) {
            case 0:
                client.exit();
                break;

            case 1:
                ClientCommands.download(client);
                break;

            case 2:
                ClientCommands.upload(client);
                break;

            case 3:
                ClientCommands.fileList(client);
                break;

            default:
                // Unknown command
        }
    }
}
