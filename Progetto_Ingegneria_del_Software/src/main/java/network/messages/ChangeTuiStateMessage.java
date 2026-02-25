package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Method used to change the tui state.
 */
public class ChangeTuiStateMessage extends Message {
    private int num;

    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param num is a value used in the tui.
     */
    public ChangeTuiStateMessage(String clientId, int num) {
        super(clientId);
        this.num = num;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.changeState(num);
    }

}
