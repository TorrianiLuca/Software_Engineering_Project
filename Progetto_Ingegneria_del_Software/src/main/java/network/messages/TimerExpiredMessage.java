package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to inform when time is expired
 */

public class TimerExpiredMessage extends Message {

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     */

    public TimerExpiredMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showTimer(0);
        view.finishBuilding();
    }

}
