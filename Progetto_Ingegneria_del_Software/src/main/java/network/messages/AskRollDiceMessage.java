package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

/**
 * Method used to ask the player to roll the dice.
 */
public class AskRollDiceMessage extends Message {
    private Card card;
    public boolean inTurn;
    private boolean proceed;

    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param card is the card.
     * @param inTurn represents if the player is in turn or not.
     * @param proceed is a flag used in the tui.
     */
    public AskRollDiceMessage(String clientId, Card card, boolean inTurn, boolean proceed) {
        super(clientId);
        this.card = card;
        this.inTurn = inTurn;
        this.proceed = proceed;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.setRollDice(card, inTurn, proceed);
    }

}
