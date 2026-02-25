package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message that contains information about the remaining time for building the ship board.
 */
public class RemainingTimeMessage extends Message {
    private final int totalTime;

    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param totalTime is the remaining time.
     */
    public RemainingTimeMessage(String clientId, int totalTime) {
        super(clientId);
        this.totalTime = totalTime;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showTimer(totalTime);
    }
}
