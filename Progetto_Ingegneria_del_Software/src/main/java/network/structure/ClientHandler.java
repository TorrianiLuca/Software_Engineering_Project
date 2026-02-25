package network.structure;

import network.messages.Message;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is used by the server to represent a client.
 */
public interface ClientHandler extends Remote, Serializable {

    /**
     * Method used to know if the client is still connected
     * @return {@code true} if the client is connected, {@code false} otherwise.
     * @throws RemoteException when the server is not reachable.
     */
    boolean isConnected() throws RemoteException;

    /**
     * Method used to disconnect a client.
     * @throws RemoteException when the server is not reachable.
     */
    void disconnect() throws RemoteException;

    /**
     * Method used to receive a message from the server.
     * @param message is the message received.
     * @throws RemoteException when the server is not reachable.
     */
    void receivedMessage(Message message) throws RemoteException;
}