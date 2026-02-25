package network.messages;

import controller.Controller;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.Pirates;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message that contains when the player wants to proceed to fight the smugglers.
 */
public class PiratesChoiceMessage extends Message {
    private Card card;

    /**
     * Default constructor.
     * @param clientId is the id of the client.
     * @param card is the card currently in use.
     */
    public PiratesChoiceMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePiratesChoiceMessage(getClientId(), card);
    }
}