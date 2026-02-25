package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

/**
 * Message that informs the views of the card currently in use.
 */
public class SetCardInUseMessage extends Message {
    private Card card;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param card is the card in use.
     */
    public SetCardInUseMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.setCardInUse(card);
    }

}
