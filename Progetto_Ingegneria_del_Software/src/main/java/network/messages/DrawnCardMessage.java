package network.messages;

import model.GameModel;
import model.card.*;
import network.structure.NetworkView;
import view.View;

import java.util.function.Consumer;

/**
 * Message that contains the card drawn for this game turn.
 */
public class DrawnCardMessage extends Message {
    private Card card;
    private boolean inTurn;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param card is the card drawn from the deck.
     * @param inTurn establish if the player is in turn or not.
     */
    public DrawnCardMessage(String clientId, Card card, boolean inTurn) {
        super(clientId);
        this.card = card;
        this.inTurn = inTurn;
    }


    /**
     * Getter method that returns the drawn card for this game turn.
     * @return the card.
     */
    public Card getCard() {
        return card;
    }

    /**
     *
     * @return a boolean that is true if the player is in turn
     */
    public boolean isInTurn() {
        return inTurn;
    }


    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showCard(inTurn);
    }
}
