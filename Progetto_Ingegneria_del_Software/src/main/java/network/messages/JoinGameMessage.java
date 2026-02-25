package network.messages;

import model.GameModel;
import network.structure.NetworkView;

/**
 * Method used to join an already created game
 */
public class JoinGameMessage extends Message {

    /**
     * The nickname of the player who wants to join the game.
     */
    private String nickname;

    /**
     * The unique identifier of the game the player wants to join.
     */
    private String gameId;

    /**
     * Constructs a new JoinGameMessage.
     *
     * @param clientId the ID of the client sending the request
     * @param nickname the nickname of the player
     * @param gameId the ID of the game the player wants to join
     */
    public JoinGameMessage(String clientId, String nickname, String gameId) {
        super(clientId);
        this.nickname = nickname;
        this.gameId = gameId;
    }

    /**
     * Returns the nickname of the player.
     *
     * @return the player's nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the ID of the game the player wants to join.
     *
     * @return the game ID
     */
    public String getGameId() {
        return gameId;
    }
}
