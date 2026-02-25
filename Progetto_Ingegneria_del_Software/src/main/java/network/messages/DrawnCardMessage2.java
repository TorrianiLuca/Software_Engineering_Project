package network.messages;

import model.GameModel;
import model.card.*;
import network.structure.NetworkView;
import view.View;

import java.util.function.Consumer;

/**
 * Message that contains the card drawn for this game turn.
 */
public class DrawnCardMessage2 extends Message {
    private Card card;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param card is the card drawn from the deck.
     */
    public DrawnCardMessage2(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    /**
     * Getter method that returns the drawn card for this game turn.
     * @return the card.
     */
    public Card getCard() {
        return card;
    }


    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showCard2();
    }

}
