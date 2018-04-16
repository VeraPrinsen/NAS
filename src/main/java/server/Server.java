package server;

import host.SocketListener;
import general.Protocol;
import host.Host;

// Server doesn't connect with a specific client, it just listens to incoming packets
// and reacts on them. In this class the listening socket is made and incoming messages
// are further processed.
class Server extends Host {

    public Server() {
        createSocket(Protocol.DEFAULT_SERVER_PORT);
        new Thread(new SocketListener(this)).start();
    }
    
    public static void main(String[] args) {
        new Server();
    }
}