package network.structure;

import network.messages.*;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.out;

/**
 * This class represents an instance for the socket server.
 * It manages incoming TCP client connections and delegates message handling to the main server.
 */
public class SocketServer implements Runnable, Server {
    private final ServerMain server;
    private final int port;
    private final List<SocketHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, SocketHandler> clientsById = new ConcurrentHashMap<>();
    private final Object clientLock = new Object();
    java.net.ServerSocket serverSocket;

    /**
     * Custom constructor for the socket server.
     * Initializes the server socket on the specified port.
     *
     * @param server the main server instance used for delegating messages
     * @param port the port to bind the server socket
     */
    public SocketServer(ServerMain server, int port) {
        this.server = server;
        this.port = port;
        try {
            serverSocket = new java.net.ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Server could not start!");
        }
    }

    /**
     * Continuously listens for new client connections.
     * For each new connection, a new SocketHandler is created and executed in a separate thread.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                String clientId = "TCP_" + System.currentTimeMillis();
                SocketHandler clientHandler = new SocketHandler(this, socket, clientId);
                Thread thread = new Thread(clientHandler);
                thread.start();
                registry(clientHandler);
                out.println("Client connected with TCP: " + clientId);
            } catch (IOException e) {
                System.err.println("Server socket unreachable ");
            }
        }
    }

    /**
     * Receives a message from a client and forwards it to the appropriate destination.
     *
     * @param message the message received
     */
    @Override
    public void receiveMessage(Message message) {
        if (message.getClass().equals(PingMessage.class)) {
            server.getServerRMI().receiveMessage(message);
        } else {
            server.receiveMessage(message);
        }
    }

    /**
     * Sends a message to a specific client, identified by its client ID.
     *
     * @param clientId the ID of the client to send the message to
     * @param message the message to send
     */
    @Override
    public void sendMessageToClient(String clientId, Message message) {
        synchronized (clientLock) {
            SocketHandler handler = clientsById.get(clientId);
            if (handler != null) {
                handler.receivedMessage(message);
            } else {
                String type = clientId.startsWith("RMI_") ? "RMI" : "TCP";
                System.err.println("Client " + type + " with ID " + clientId + " not found");
            }
        }
    }

    /**
     * Method that manages a client disconnection.
     * Removes the client from the lists and informs the main server.
     *
     * @param clientId the ID of the disconnected client
     */
    public void handleClientDisconnection(String clientId) {
        synchronized (clientLock) {
            SocketHandler handler = clientsById.get(clientId);
            if (handler != null) {
                try {
                    handler.disconnect();
                } catch (Exception e) {
                    System.err.println("Error during connection with client: " + clientId);
                }
                clients.remove(handler);
                clientsById.remove(clientId);
            }
            server.handleClientDisconnection(clientId);
        }
    }

    /**
     * Method that disconnects a specific client by sending a disconnection message.
     *
     * @param clientId the ID of the client to disconnect
     */
    public void disconnectClient(String clientId) {
        SocketHandler handler = clientsById.get(clientId);
        if (handler != null) {
            try {
                handler.receivedMessage(new ClientDisconnectedMessage());
                handler.disconnect();
            } catch (Exception e) {
                System.err.println("Error during the connection with client:  " + clientId);
            }
            clients.remove(handler);
            clientsById.remove(clientId);
        }
    }

    /**
     * Checks all connected clients and removes the ones that are no longer connected.
     *
     * @throws RemoteException if an RMI error occurs
     */
    @Override
    public void ping() throws RemoteException {
        ArrayList<String> disconnectedClients = new ArrayList<>();
        for (SocketHandler c : clients) {
            if (!c.isConnected()) {
                disconnectedClients.add(c.getClientID());
            }
        }
        for (String clientId : disconnectedClients) {
            handleClientDisconnection(clientId);
        }
    }

    /**
     * Registers a new client to the server.
     *
     * @param clientHandler the client handler to register
     * @throws RemoteException if an RMI error occurs
     */
    @Override
    public void registry(ClientHandler clientHandler) throws RemoteException {
        if (!clients.contains((SocketHandler) clientHandler)) {
            clients.add((SocketHandler) clientHandler);
            clientsById.put(((SocketHandler) clientHandler).getClientID(), (SocketHandler) clientHandler);
            clientHandler.receivedMessage(new AskNicknameMessage(((SocketHandler) clientHandler).getClientID()));
        }
    }

    /**
     * Getter method for the main server.
     *
     * @return the ServerMain instance associated with this socket server
     */
    public ServerMain getServer() {
        return server;
    }
}
