package network.messages;

import controller.Controller;

/**
 * Message that inform the server has just been updated
 */

public class RefreshGameOnServerMessage extends Message {

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     */

    public RefreshGameOnServerMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void process(Controller controller) {
    }

}
