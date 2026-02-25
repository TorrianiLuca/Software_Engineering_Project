package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to inform the player that he has to wait for the other players to proceed in the game.
 */
public class NotProceedMessage extends Message {

    /**
     * The sender of the message is the player.
     * @param clientId is the id of the player
     */

    public NotProceedMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.notProceed();
    }
}
