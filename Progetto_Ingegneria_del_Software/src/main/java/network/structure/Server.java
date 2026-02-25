
package network.structure;

import network.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class represents the server.
 */
public interface Server extends Remote{

    /**
     * Method used to receive a message from the clients.
     * @param message is the message received.
     * @throws RemoteException when the server is not reachable.
     */
    void receiveMessage(Message message) throws RemoteException;

    /**
     * Method used to send a message to a specific client.
     * @param message is the message that has to be sent.
     * @param clientId is the ID of the client.
     * @throws RemoteException when the server is not reachable.
     */
    void sendMessageToClient(String clientId, Message message) throws RemoteException;

    /**
     * Method used to disconnect a client.
     * @param clientId is the ID of client that has to be disconnected.
     * @throws RemoteException when the client already has disconnected from the server.
     */
     void disconnectClient(String clientId) throws RemoteException;

    /**
     * Method used as a heartbeat to check if the clients are connected.
     * @throws RemoteException when the server is not reachable.
     */
    void ping() throws RemoteException;

    /**
     * Method used to register a new client.
     * @param clientHandler is the {@link ClientHandler} of the client that is trying to connect with the server.
     * @throws RemoteException when the server is not reachable.
     */
    void registry(ClientHandler clientHandler) throws RemoteException;
}
