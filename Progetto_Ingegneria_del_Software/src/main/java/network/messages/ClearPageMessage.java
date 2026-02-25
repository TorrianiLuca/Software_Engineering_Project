package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to inform the views that the game has started.
 */
public class ClearPageMessage extends Message {
    /**
     * Default constructor.
     * @param clientId is the id of the client.
     */
    public ClearPageMessage(String clientId) {
        super(clientId);
    }

    /**
     * getter method.
     * @return the clientId
     */
    public String getClientId() {
        return super.getClientId();
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.clearPage();
    }
}
