package client;

// Is not yet a new Thread, so no input can be given before this is resolved.
public class ClientInputHandler {

    private Client client;
    private int choice;

    public ClientInputHandler(Client client, int choice) {
        this.client = client;
        this.choice = choice;
        start();
    }

    // TODO: Make tasks for client input commands
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

            case 4:
                ClientCommands.testSmallData(client);
                break;

            case 5:
                ClientCommands.testLargeData(client);
                break;

            default:
                // Unknown command
        }
    }
}
