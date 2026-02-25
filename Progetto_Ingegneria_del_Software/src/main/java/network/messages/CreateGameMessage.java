package network.messages;

import model.GameModel;
import network.structure.NetworkView;

/**
 * Method used to create a new game.
 */
public class CreateGameMessage extends Message {
    private String nickname;

    /**
     * Constructor.
     * @param clientId is the id of the player.
     * @param nickname is the nickname of the player.
     */
    public CreateGameMessage(String clientId, String nickname) {
        super(clientId);
        this.nickname = nickname;

    }

    /**
     * @return the nickname of the player
     */
    public String getNickname() {
        return nickname;
    }

}