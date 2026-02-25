package network.messages;

import controller.Controller;
import controller.GameEventListener;

/**
 * Message that contains the username chosen by the player for the login.
 */
public class UsernameMessage extends Message {
    final String nickname;
    private GameEventListener listener;

    /**
     * Default constructor.
     * @param clientId is the id of the client.
     * @param nickname is the nickname chosen.
     */
    public UsernameMessage(String clientId, String nickname) {
        super(clientId);
        this.nickname = nickname;
    }

    /**
     * Method that returns the nickname of the player.
     * @return the nickname of the player.
     */
    public String getNickname() {
        return nickname;
    }

    @Override
    public void process(Controller controller) {
        controller.handleUsernameMessage(getClientId(), nickname);
    }
}