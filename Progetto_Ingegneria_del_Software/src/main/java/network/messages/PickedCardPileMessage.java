package network.messages;

import model.GameModel;
import model.card.CardPile;
import model.tiles.ComponentTile;
import network.messages.Message;
import network.structure.NetworkView;
import view.View;

import java.util.function.Consumer;

/**
 * Message that contains the card pile picked by the player.
 */
public class PickedCardPileMessage extends Message {
    private final CardPile cardPile;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param cardPile is the card pile picked.
     */
    public PickedCardPileMessage(String clientId, CardPile cardPile) {
        super(clientId);
        this.cardPile = cardPile;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showCardPile(cardPile);
    }

}
