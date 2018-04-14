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
                CommandHandler.download(client);
                break;

            case 2:
                CommandHandler.upload(client);
                break;

            case 3:
                CommandHandler.fileList(client);
                break;

            case 4:
                CommandHandler.testSmallData(client);
                break;

            case 5:
                CommandHandler.testLargeData(client);
                break;

            default:
                // Unknown command
        }
    }
}
