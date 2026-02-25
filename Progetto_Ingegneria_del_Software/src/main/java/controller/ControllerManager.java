
package controller;

import enumerations.FlightType;
import network.structure.ClientHandler;
import support.Couple;
import network.messages.Message;
import network.structure.ServerMain;
import support.Quadruple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents a "manager" for all the game's controllers. It receives the message from the server and
 * throws it to the correct game controller.
 */
public class ControllerManager {
    private HashMap<String, Controller> activeGames = new HashMap<>();
    private HashMap<String, String> playerToGameMap = new HashMap<>();
    private ArrayList<String> usedNicknames = new ArrayList<>();
    private HashMap<String, String> nicknamesToIdMap = new HashMap<>();
    private AtomicInteger gameIdCounter = new AtomicInteger(1);

    /**
     * Creates a new game with the given ID.
     * @return the ID of the created game, or null if already existing.
     * @param clientId is the client id
     * @param nickname is the nickname of the player
     * @param server is the server
     */
    public void createGame(String nickname, String clientId, ServerMain server) throws IOException {
        String gameId = "game-" + gameIdCounter.getAndIncrement();
        Controller controller = new Controller(server);
        controller.getGameModel().incrementConnectedPlayers();
        controller.getGameModel().addNickname(nickname, clientId);
        playerToGameMap.put(clientId, gameId);
        nicknamesToIdMap.put(nickname, clientId);
        activeGames.put(gameId, controller);
    }

    /**
     * Add a player to an existing game.
     * @param nickname is the name of the player.
     * @param clientId is the ID of the client.
     * @param gameId is the ID of the game.
     * @return 1 if is the player is added to the game, 2 if the game is full, 3 id there is no game with the given ID
     */
    public int joinGame(String nickname, String clientId, String gameId) {
        if (activeGames.containsKey(gameId)) {
            Controller controller = activeGames.get(gameId);
            if (controller.getGameModel().getConnectedPlayers() < controller.getGameModel().getMaxPlayers()) {
                controller.getGameModel().incrementConnectedPlayers();
                controller.getGameModel().addNickname(nickname, clientId);

                playerToGameMap.put(clientId, gameId);
                nicknamesToIdMap.put(nickname, clientId);

                return 1;
            }
            return 2;
        }
        return 3;
    }

    public void initializeGame(String gameId) {
        Controller controller = activeGames.get(gameId);
        if(controller.getGameModel().getConnectedPlayers() == controller.getGameModel().getMaxPlayers()) {
            controller.initGame(nicknamesToIdMap);
        }
    }

    /**
     * Return the controller of the specified game.
     * @param gameId is the game ID.
     * @return the controller associated to the game.
     */
    public Controller getGameController(String gameId) {
        return activeGames.get(gameId);
    }


    /**
     * Return the HashMap containing the active games on the server.
     * @return the active games (with their flight type, the number of connected players, the number of max players
     * for the game, and the nicknames of the players associated to that game.
     */
    public HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> getActiveGames() {
        if(activeGames.isEmpty()) {
            return null;
        }

        HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> temp = new HashMap<>();
        for(String game : activeGames.keySet()) {
            ArrayList<String> temp2 = new ArrayList<>();
            for(String nickname : activeGames.get(game).getGameModel().getNicknames()) {
                temp2.add(nickname);
            }
            temp.put(game, new Quadruple<>((FlightType) getGameController(game).getGameModel().getFlightType(), (Integer) getGameController(game).getGameModel().getConnectedPlayers(), (Integer) getGameController(game).getGameModel().getMaxPlayers(), temp2));
        }
        return temp;
    }

    /**
     * this method does not allow to associate a nickname already taken
     * @return a boolean, true if nickname is already taken
     * @param nickname is the nickname chosen
     **/
    public boolean isNicknameTaken(String nickname) {
        return usedNicknames.contains(nickname);
    }

    /**
     * This method adds a nickname to the list of those already chosen
     * @param nickname is the nickname to add
     */
    public void addNickname(String nickname) {
        usedNicknames.add(nickname);
    }

    /**
     * Method that forwards the message to the specific controller.
     * @param message is the message that has to be forwarded.
     */
    public void onMessageReceived(Message message) {
        Controller controller = activeGames.get(playerToGameMap.get(message.getClientId()));
        System.out.println(controller);
        if (controller != null) {
            controller.onMessageReceived(message);
        }
    }

    /**
     *This method allows you to get game id from the player associated to that game
     * @param clientId is the client id
     * @return the id of the game
     **/
    public String getGameFromClient(String clientId) {
        return playerToGameMap.get(clientId);
    }

    /**
     *This method removes players from the game
     * @param gameId is the id of the game
     **/
    public void removeGame(String gameId) {
        activeGames.remove(gameId);

        ArrayList<String> playersToRemove = new ArrayList<>();
        for (String playerId : playerToGameMap.keySet()) {
            if (playerToGameMap.get(playerId).equals(gameId)) {
                playersToRemove.add(playerId);
            }
        }

        for (String playerId : playersToRemove) {
            playerToGameMap.remove(playerId);

            ArrayList<String> nicknamesToRemove = new ArrayList<>();
            for (String nickname : nicknamesToIdMap.keySet()) {
                if (nicknamesToIdMap.get(nickname).equals(playerId)) {
                    nicknamesToRemove.add(nickname);
                }
            }

            for (String nickname : nicknamesToRemove) {
                System.out.println("Remove nickname: " + nickname);
                nicknamesToIdMap.remove(nickname);
                usedNicknames.remove(nickname);
            }
        }
    }

    /**
     * This method allows to get the player id's from the game
     * @param gameId is the id of the game
     * @return the array list of the player id's
     **/
    public ArrayList<String> getClientIdsFromGame(String gameId) {
        ArrayList<String> temp = new ArrayList<>();
        for(String playerId : playerToGameMap.keySet()) {
            if(playerToGameMap.get(playerId).equals(gameId)) {
                temp.add(playerId);
            }
        }
        return temp;
    }

    //Use only for test!
    public HashMap<String, Controller> getActiveGamesTest(){
        return activeGames;
    }

    //Use only for test!
    public HashMap<String, String> getNicknamesToIdMapTest() {
        return nicknamesToIdMap;
    }

    //Use only for test!
    public HashMap<String, String> getPlayerToGameMapTest() {
        return playerToGameMap;
    }

}
