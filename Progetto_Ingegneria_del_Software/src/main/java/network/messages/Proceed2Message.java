package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to notify the controller update
 * */

public class Proceed2Message extends Message {

    /**
     * @param clientId is the sender of the message is the player.
     */

    public Proceed2Message(String clientId) {
        super(clientId);
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.proceed2();
    }
}