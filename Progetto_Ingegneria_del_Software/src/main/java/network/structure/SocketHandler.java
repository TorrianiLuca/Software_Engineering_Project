
package network.structure;

import network.messages.Message;
import network.messages.PingMessage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class represents a handler for the socket connection.
 */
public class SocketHandler implements Runnable,ClientHandler {

    private final String clientID;
    private final SocketServer socketServer;
    private final Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private final Object lock = new Object();

    /**
     * Custom controller of the SocketHandler class.
     * @param socketServer is the socket server.
     * @param socket is the socket used for communicating.
     */
    public SocketHandler(SocketServer socketServer, Socket socket, String clientID) {
        this.socketServer = socketServer;
        this.socket = socket;
        this.clientID = clientID;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream()) ;
            this.ois = new ObjectInputStream(socket.getInputStream());
        }
        catch(IOException e){
            System.err.println("Cannot connect to the socket streams");
        }
    }

    /**
     * @return the client id.
     */
    public String getClientID() {
        return clientID;
    }

    @Override
    public void run() {
        try{clientCommunication();}
        catch(IOException e){
            System.err.println("Client "+ socket.getInetAddress()+" is unreachable");
            disconnect();
        }
    }

    /**
     * This method handles the communication with the server.
     * @throws IOException when there is a malfunction with the socket.
     */
    private void clientCommunication() throws IOException {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) ois.readObject();
                socketServer.receiveMessage(message);
            }
        } catch (ClassCastException | ClassNotFoundException e) {
            System.err.println("Client thread malfunction"+ e);
        } catch (EOFException eof) {
            System.out.println("Client closed the connection.");
            throw eof;
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean isConnected() {
        try {
            oos.writeObject(new PingMessage("Server"));
            oos.flush();
            oos.reset();
        }catch (IOException e) {return false;}
        return true;
    }

    @Override
    public void disconnect() {
        try{socket.close();}
        catch(IOException e){System.err.println("Cannot close the socket");}
    }

    @Override
    public void receivedMessage(Message message) {
        synchronized (lock) {
            try{
                oos.writeObject(message);
                oos.flush();
                oos.reset();
            } catch (IOException e) {disconnect();}
        }
    }
}
