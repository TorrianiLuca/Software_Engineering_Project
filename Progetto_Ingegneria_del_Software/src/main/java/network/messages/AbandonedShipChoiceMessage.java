package network.messages;

import controller.Controller;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.AbandonedShip;
import model.player.Player;

import java.util.function.Consumer;

/**
 * Message that contains if the players wants to land on a ship.
 */
public class AbandonedShipChoiceMessage extends Message {
    private Card card;
    private String choice;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     * @param choice is the choice the player has made (dock, skip)
     */
    public AbandonedShipChoiceMessage(String clientId, Card card, String choice) {
        super(clientId);
        this.card = card;
        this.choice = choice;
    }

    /**
     * Getter method that returns the card selected for this turn.
     * @return card picked up for this game turn.
     */
    public Card getCard() {
        return card;
    }

    /**
     * Getter method that returns the choice the player has made.
     * @return the choice mad by the player.
     */
    public String getChoice() {
        return choice;
    }

    @Override
    public void process(Controller controller) {
        controller.handleAbandonedShipChoiceMessage(getClientId(), card, choice);
    }
}