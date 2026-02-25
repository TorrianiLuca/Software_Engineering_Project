package network.structure;

import controller.ClientController;
import network.messages.*;
import observer.Observable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a client connected with RMI.
 */
public class ClientRMI extends Observable implements Client, Runnable, ClientHandler {
    private transient Server server;
    private String nickname;
    private String clientId;
    private final int port;
    private final String address;
    private boolean isConnected = false;
    private final ArrayList<Message> messages = new ArrayList<>();
    private transient ScheduledExecutorService timer;

    /**
     * Constructor for the RMI client.
     * @param address is the IP of the server.
     * @param port is the port chosen for the connection.
     * @throws RemoteException when the server is not reachable.
     */
    public ClientRMI(String address, int port) throws RemoteException {
        this.address = address;
        this.port = port;
    }

    @Override
    public void connection() throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(address, port);
            server = (Server) registry.lookup("server");
            initialize(server);
            this.timer = Executors.newSingleThreadScheduledExecutor();
            isConnected = true;
        } catch (RemoteException e) {
            System.err.println("RMI registry not found");
        } catch (NotBoundException e) {
            System.err.println("Error in RMI lookup");
        }
    }

    @Override
    public void closeConnection() throws RemoteException {
        if (isConnected) {
            disconnect(true);
        }
    }

    @Override
    public void sendMessage(Message message) throws RemoteException {
        clientId = message.getClientId();
        if(!isConnected) {
            return;
        }
        if (message.getClass() == UsernameMessage.class) {
            nickname = ((UsernameMessage) message).getNickname();
        }
        try {
            server.receiveMessage(message);
        } catch (RemoteException e) {
            notifyObserver(new ServerDisconnectedMessage());
        }
    }

    @Override
    public void receivedMessage(Message message) throws RemoteException {
        if (!(message.getClass() == PingMessage.class)) {
            messages.add(message);
        }
    }

    /**
     * Method used to register an RMI client to the server.
     * @param server is the server.
     * @throws RemoteException when the server is not reachable.
     */
    void initialize(Server server) throws RemoteException {
        try {
            server.registry((ClientHandler) UnicastRemoteObject.exportObject(this, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
            System.err.println("Unable to connect.");
            isConnected = false;
            System.exit(-1);
        }
    }


    @Override
    public boolean isConnected() throws RemoteException {
        return isConnected;
    }

    @Override
    public void disconnect() throws RemoteException {}

    @Override
    public void disconnect(boolean notifyServer) throws RemoteException {
        isConnected = false;
        if (timer != null) {
            timer.shutdown();
        }
        if (notifyServer) {
            try {
                server.disconnectClient(clientId);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void ping() throws RemoteException {
        timer.scheduleAtFixedRate(() -> {
            try {
                sendMessage(new PingMessage(clientId));
            } catch (RemoteException e) {
                System.err.println("Ping error");
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        while (!Thread.interrupted() && isConnected) {
            if (!messages.isEmpty()) {
                notifyObserver(messages.remove(0));
            }
        }
    }
}