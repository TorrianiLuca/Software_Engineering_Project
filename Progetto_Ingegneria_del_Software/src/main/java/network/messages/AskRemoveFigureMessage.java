package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

public class AskRemoveFigureMessage extends Message {

    /**
     * Default Constructor.
     * @param clientId is the id assigned to each client.
     */
    public AskRemoveFigureMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.setRemoveFigure();
    }

}
