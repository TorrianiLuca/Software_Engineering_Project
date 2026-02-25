package network.structure;

import network.messages.Message;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used to represents a client.
 */

public interface Client extends Remote {

    /**
     * Method used to set the connection.
     * @throws IOException when the server is unreachable.
     */
    void connection() throws IOException;

    /**
     * Method used to send the message to the server.
     * @param message is the message sent to the server.
     * @throws RemoteException when the server is unreachable.
     */
    void sendMessage(Message message) throws RemoteException;

    /**
     * Method used to receive the message.
     * @param message is the message received from the server.
     * @throws RemoteException when the server is unreachable.
     */
    void receivedMessage(Message message) throws RemoteException;

    /**
     * Method used to know if the client is still connected.
     * @return {@code true} if the client is connected, {@code false} otherwise.
     * @throws RemoteException when the server is unreachable.
     */
    boolean isConnected() throws RemoteException;

    /**
     * Method used to close the connection.
     * @throws IOException when the connection with the server is already closed.
     */
    void closeConnection() throws IOException;

    /**
     * Method used to disconnect the client.
     * @param notifyServer is used to know if the client needs to send a message to the server (if the disconnection
     * comes from the client) or not (if the disconnection comes from the server).
     * @throws RemoteException when the server is unreachable.
     */
    void disconnect(boolean notifyServer) throws RemoteException;

    /**
     * Method used as a heartbeat to establish when a client is connected or not.
     * @throws RemoteException
     */
    void ping() throws RemoteException;
}