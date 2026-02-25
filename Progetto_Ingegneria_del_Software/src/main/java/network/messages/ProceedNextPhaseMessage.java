package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;

/**
 * Message used to inform all the players that the current combat zone phase is finished and that they have to
 * proceed in the next phase.
 */
public class ProceedNextPhaseMessage extends Message {
    Card card;
    ArrayList<Card> cardsToPlay;

    /**
     * Constructor.
     * @param clientId is the player's id.
     * @param card is the card currently in use.
     * @param cardsToPlay are the cards that still have to be drawn.
     */
    public ProceedNextPhaseMessage(String clientId, Card card, ArrayList<Card> cardsToPlay) {
        super(clientId);
        this.card = card;
        this.cardsToPlay = cardsToPlay;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.proceedToNextCard(card, 2, this.cardsToPlay);
    }
}
