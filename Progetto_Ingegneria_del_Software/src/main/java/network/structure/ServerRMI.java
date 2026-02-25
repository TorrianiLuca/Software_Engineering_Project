package network.structure;

import network.messages.*;
import observer.Observable;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents an instance for the RMI server
 */
public class ServerRMI extends Observable implements Server, Runnable {
    private ArrayList<ClientHandler> clientsRMI;
    private HashMap<String, ClientHandler> clientsById = new HashMap<>();
    private ArrayList<Message> messages = new ArrayList<>();
    private final ServerMain server;
    private HashMap<String, Boolean> pingReceived = new HashMap<>();

    /**
     * Custom constructor for the RMI server.
     * @param server is the server's main that starts this instance.
     * @throws RemoteException is thrown when the server is not reachable.
     */
    public ServerRMI(ServerMain server) throws RemoteException {
        super();
        this.server = server;
        this.clientsRMI = new ArrayList<>();
    }

    @Override
    public void registry(ClientHandler clientHandler) throws RemoteException {
        if(!clientsRMI.contains(clientHandler)){
            clientsRMI.add(clientHandler);
            String clientId = "RMI_" + System.currentTimeMillis();
            clientsById.put(clientId, clientHandler);
            System.out.println("Client connected with RMI: " + clientId);
            clientHandler.receivedMessage(new AskNicknameMessage(clientId));
        }
    }

    @Override
    public void receiveMessage(Message message){
        if(!message.getClass().equals(PingMessage.class)) {
            messages.add(message);
        }
        else {
            System.out.println(message);
            if(message.getClientId() != null) {
                pingReceived.put(message.getClientId(), true);
            }
        }
    }

    @Override
    public void sendMessageToClient(String clientId, Message message) {
        ClientHandler handler = clientsById.get(clientId);
        if (handler != null) {
            try {
                handler.receivedMessage(message);
            }
            catch (RemoteException e) {}
        } else {
            System.err.println("Client with ID " + clientId + " non found");
        }
    }

    @Override
    public void ping() throws RemoteException {
        ArrayList<String> disconnectedClients = new ArrayList<>();
        for (String clientId : pingReceived.keySet()) {
            System.out.println(pingReceived.get(clientId) + ": " + clientId);
            if (!pingReceived.get(clientId)) {
                disconnectedClients.add(clientId);
            }
            else {
                pingReceived.replace(clientId, false);
            }
        }

        for (String clientId : disconnectedClients) {
            pingReceived.remove(clientId);
            server.handleClientDisconnection(clientId);
        }
    }

    @Override
    public void disconnectClient(String clientId) {
        ClientHandler handler = clientsById.get(clientId);
        if (handler != null) {
            try {
                handler.receivedMessage(new ClientDisconnectedMessage());
            } catch (RemoteException e) {
                System.err.println("Client " + clientId + " already disconnected");
            }
            clientsRMI.remove(handler);
            clientsById.remove(clientId);
            pingReceived.remove(clientId);
        }
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            if(messages.size()>0) {
                server.receiveMessage(messages.remove(0));
            }
        }
    }

    /**
     * Getter method for the serverMain parameter.
     * @return the server main instance.
     */
    public ServerMain getServer() {
        return server;
    }
}
