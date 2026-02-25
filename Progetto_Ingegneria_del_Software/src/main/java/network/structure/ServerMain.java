
package network.structure;

import controller.Controller;
import controller.ControllerManager;
import enumerations.GameState;
import network.messages.*;
import observer.Observer;

import java.io.IOException;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class for the server application. The user can decide the ports to use; if he doesn't choose there will be set by default.
 */
public class ServerMain implements Observer, Runnable {
    private ServerRMI serverRMI;
    private SocketServer socketServer;
    private ControllerManager controllerManager = new ControllerManager();
    private static ExecutorService executor;

    /**
     * Custom constructor for the ServerMain class.
     * @throws IOException if there is an error during the creation of the RMI or socket connection.
     */
    public ServerMain(int socketPort, int RMIPort) throws IOException {
        String[] stringToSplit = getLocalHostLANAddress().toString().split("/");
        String IPAddress = stringToSplit[1];
        System.out.println("The LAN IP address for the server is: " + IPAddress);
        startRMI(RMIPort);
        startSocket(socketPort);
        executor.submit(this);
    }

    /**
     * This method returns the host LAN address.
     * @return the IPv4 LAN address of the machine.
     * @throws UnknownHostException if it fails to find a valid IP address.
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                if(!iface.getDisplayName().contains("Virtual")) {
                    for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                        InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                        if (!inetAddr.isLoopbackAddress()) {
                            if (inetAddr.isSiteLocalAddress()) {
                                return inetAddr;
                            } else if (candidateAddress == null) {
                                candidateAddress = inetAddr;
                            }
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    /**
     * Main method, used to create the instance of the server.
     */
    public static void main(String[] args){
        boolean proceed = true;
        final int defaultSocketPort = 12345;
        final int defaultRMIPort = 1099;
        int socketPort = 0;
        int RMIPort = 0;
        executor = Executors.newCachedThreadPool();

        if (args.length == 0) {
            socketPort = defaultSocketPort;
            RMIPort = defaultRMIPort;
        } else {
            if (args.length != 2) {
                System.out.println("Invalid number of parameters. Required: 2; Provided: " + args.length);
                proceed = false;
            } else {
                try {
                    socketPort = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + args[0]);
                    proceed = false;
                }

                try {
                    RMIPort = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: " + args[1]);
                    proceed = false;
                }

                if (!isPortValid(socketPort)) {
                    System.out.println("Invalid input: " + socketPort);
                    proceed = false;
                } else if (!isPortValid(RMIPort)) {
                    System.out.println("Invalid input: " + RMIPort);
                    proceed = false;
                }
            }
        }

        if (proceed) {
            try {
                new ServerMain(socketPort, RMIPort);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Cannot instantiate the server structure");
                System.exit(-1);
            }
        } else {
            System.exit(-1);
        }
    }

    /**
     * This method checks if the port is valid.
     * @param port is the port to verify.
     * @return {@code true} if the port is valid, {@code false} otherwise.
     */
    public static boolean isPortValid(int port){
        if(port >= 1 && port <= 65535) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the instance of the RMI server.
     * @return the serverRMI.
     */
    public ServerRMI getServerRMI() {
        return serverRMI;
    }

    /**
     * Start the server RMI.
     * @param port is the port for the RMI server.
     * @throws RemoteException if the server in not reachable.
     */
    private void startRMI(int port) throws RemoteException {
        serverRMI = new ServerRMI(this);
        Server stubRMI = (Server) UnicastRemoteObject.exportObject(serverRMI,port);
        LocateRegistry.createRegistry(port);
        Registry registry = LocateRegistry.getRegistry(port);
        registry.rebind("server", stubRMI);
        serverRMI.addObserver(this);
        getExecutor().submit(serverRMI);
        System.out.println("RMI server started on port: " + port + ", waiting for clients...");
    }

    /**
     * Start the server socket.
     * @param port is the port for the Socket server.
     */
    private void startSocket(int port){
        socketServer = new SocketServer(this, port);
        getExecutor().submit(socketServer);
        System.out.println("Socket server started on port: " + port + ", waiting for clients...");
    }

    /**
     * Return the thread pool executor.
     * @return the executor service related to the server.
     */
    public ExecutorService getExecutor(){
        return executor;
    }

    /**
     * Forwards a message received from the clients to the controller.
     * @param message is the message received.
     */
    public void receiveMessage(Message message){
        if(message instanceof UsernameMessage) {
            UsernameMessage usernameMessage = (UsernameMessage) message;
            String nickname = usernameMessage.getNickname();
            if (controllerManager.isNicknameTaken(nickname)) {
                sendMessageToClient(usernameMessage.getClientId(), new GenericErrorMessage(usernameMessage.getClientId(), "Username is already taken."));
            } else {
                controllerManager.addNickname(nickname);
                sendMessageToClient(usernameMessage.getClientId(), new GameListMessage("Controller",controllerManager.getActiveGames()));
            }
        }
        else if(message instanceof CreateGameMessage){
            CreateGameMessage createGameMessage = (CreateGameMessage) message;
            try {
                controllerManager.createGame(createGameMessage.getNickname(), createGameMessage.getClientId(), this);
            }
            catch (Exception e) {}
        }
        else if(message instanceof JoinGameMessage){
            JoinGameMessage joinGameMessage = (JoinGameMessage) message;
            try {
                int valid = controllerManager.joinGame(joinGameMessage.getNickname(), joinGameMessage.getClientId(), joinGameMessage.getGameId());
                if (valid == 3) {
                    sendMessageToClient(joinGameMessage.getClientId(), new GenericErrorMessage(message.getClientId(), "There is no game with this ID. Try again."));
                    return;
                }
                if (valid == 2) {
                    sendMessageToClient(joinGameMessage.getClientId(), new GenericErrorMessage(message.getClientId(), "The game is already full. Try again"));
                    return;
                }
                if (valid == 1) {
                    sendMessageToClient(joinGameMessage.getClientId(), new GenericMessage("Added to the game. Waiting for other players to start the game..."));
                    controllerManager.initializeGame(((JoinGameMessage) message).getGameId());
                    return;
                }
            }
            catch (Exception e) {}
        }
        else if(message instanceof RefreshGameOnServerMessage){
            RefreshGameOnServerMessage refreshGameOnServerMessage = (RefreshGameOnServerMessage) message;
            sendMessageToClient(refreshGameOnServerMessage.getClientId(), new GameListMessage("Controller",controllerManager.getActiveGames()));
        }
        else {
            controllerManager.onMessageReceived(message);
        }
    }

    /**
     * Method used to send a message to a specific client.
     * @param clientId is the ID of the client the server wants to send the message to.
     * @param message is the message sent.
     */
    public void sendMessageToClient(String clientId, Message message) {
        if (clientId.startsWith("TCP_")) {
            socketServer.sendMessageToClient(clientId, message);
        } else if (clientId.startsWith("RMI_")) {
            serverRMI.sendMessageToClient(clientId, message);
        } else {
            System.err.println("Client ID not valid: " + clientId);
        }
    }

    /**
     * Method that manages a client disconnection: it will disconnect all the clients that are playing the specific game.
     * @param clientId is the ID of the disconnected client.
     */
    public void handleClientDisconnection(String clientId) {
        String gameId = controllerManager.getGameFromClient(clientId);
        if (gameId != null) {
            disconnectGame(gameId);
            controllerManager.removeGame(gameId);
        }
    }

    /**
     * Method that disconnects all the client associated to a specific game.
     * @param gameId is the ID of the game to terminate.
     */
    public void disconnectGame(String gameId) {
        ArrayList<String> clientIds = controllerManager.getClientIdsFromGame(gameId);
        if (clientIds != null) {
            for (String clientId : clientIds) {
                if (clientId.startsWith("TCP_")) {
                    socketServer.disconnectClient(clientId);
                } else if (clientId.startsWith("RMI_")) {
                    serverRMI.disconnectClient(clientId);
                }
            }
        }
    }

    @Override
    public void update(Message message) {
        receiveMessage(message);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                serverRMI.ping();
            } catch (RemoteException e) {}
            try {
                socketServer.ping();
            } catch (RemoteException e) {}
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

