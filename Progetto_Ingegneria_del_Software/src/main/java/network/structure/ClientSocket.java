package network.structure;

import network.messages.*;
import observer.Observable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This class represents a client connected via TCP.
 * It handles connection setup, message sending and receiving,
 * and notifies observers when messages arrive or when the connection status changes.
 */
public class ClientSocket extends Observable implements Client {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;
    private final int port;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private String clientId;
    private Server server;
    private final String address;
    private boolean isConnected = false;
    private ScheduledExecutorService timer;

    private final Object lock = new Object();
    private final Object executorLock = new Object();

    private final Logger LOGGER = Logger.getLogger(getClass().getName());
    private volatile boolean isExecutorActive = false;

    /**
     * Constructs a new TCP client socket.
     * Initializes connection and starts receiving messages and pings.
     *
     * @param address the IP address of the server
     * @param port the port used to connect
     */
    public ClientSocket(String address, int port) {
        this.address = address;
        this.port = port;
        try {
            connection();
            if (isConnected) {
                receivedMessage();
                ping();
            }
        } catch (IOException e) {
            System.err.println("Socket connection error");
            System.exit(1);
        }
    }

    /**
     * Establishes a socket connection to the server.
     * Initializes streams and sets the connection state.
     *
     * @throws IOException if the connection fails
     */
    @Override
    public void connection() throws IOException {
        try {
            socket = new Socket(address, port);
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.oos.flush();
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.isConnected = true;
        } catch (IOException e) {
            System.err.println("Cannot get IO for communication");
            isConnected = false;
            closeConnection();
            notifyObserver(new GenericErrorMessage(null,"Connection unable"));
        }
    }

    /**
     * Sends a message to the server using the output stream.
     * If the message is a UsernameMessage, the client ID is stored.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(Message message) {
        if (!isConnected) {
            return;
        }

        synchronized (lock) {
            try {
                if (message.getClass() == UsernameMessage.class) {
                    clientId = message.getClientId();
                }
                oos.writeObject(message);
                oos.flush();
            } catch (IOException e) {
                LOGGER.warning("Send message error: " + e.getMessage());
                isConnected = false;
                try {
                    closeConnection();
                } catch (IOException ex) {
                    LOGGER.severe("Closing connection error: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Starts a background thread to continuously receive messages from the server.
     * Each message is forwarded to observers.
     */
    public void receivedMessage() {
        synchronized (executorLock) {
            if (isExecutorActive && executor != null) {
                executor.shutdownNow();
            }
            executor = Executors.newSingleThreadExecutor();
            isExecutorActive = true;

            executor.execute(() -> {
                while (isConnected && !Thread.currentThread().isInterrupted()) {
                    try {
                        Message message = (Message) ois.readObject();
                        notifyObserver(message);
                    } catch (IOException e) {
                        LOGGER.warning("Connection lost: " + e.getMessage());
                        isConnected = false;
                        try {
                            closeConnection();
                        } catch (IOException ex) {
                            LOGGER.severe("Closing connection error: " + ex.getMessage());
                        }
                        break;
                    } catch (ClassNotFoundException e) {
                        LOGGER.severe("Class not found: " + e.getMessage());
                    }
                }
                isExecutorActive = false;
            });
        }
    }

    /**
     * Closes all open streams and the socket.
     * Sets the connection state to false and notifies observers of the disconnection.
     *
     * @throws IOException if closing resources fails
     */
    @Override
    public void closeConnection() throws IOException {
        if (oos != null) {
            try {
                oos.close();
            } catch (IOException e) {}
            oos = null;
        }

        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {}
            ois = null;
        }

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        isConnected = false;
        notifyObserver(new ServerDisconnectedMessage());
    }

    /**
     * Returns whether the client is currently connected to the server.
     *
     * @return true if connected, false otherwise
     */
    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Disconnects the client from the server.
     * Stops the ping timer and shuts down the message receiving thread.
     *
     * @param notifyServer unused parameter, present for interface compatibility
     */
    @Override
    public void disconnect(boolean notifyServer) {
        isConnected = false;
        if (timer != null) {
            timer.shutdown();
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            isExecutorActive = false;
        }
        try {
            closeConnection();
        } catch (IOException e) {
            LOGGER.severe("Closing connection error: " + e.getMessage());
        }
    }

    /**
     * Starts a scheduled task that sends a PingMessage to the server
     * at fixed intervals to keep the connection alive.
     */
    @Override
    public void ping() {
        timer.scheduleAtFixedRate(() -> sendMessage(new PingMessage(clientId)), 0, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Unused method override to comply with the Client interface.
     *
     * @param message ignored parameter
     */
    @Override
    public void receivedMessage(Message message) {}
}
