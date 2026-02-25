package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

/**
 * Message that informs the player that he has to wait the other players.
 */
public class WaitMessage extends Message {
    private Card card;

    /**
     * Constructor.
     * @param clientId is the client id.
     * @param card is the card currently in use.
     */
    public WaitMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showWait(card);
    }

}
