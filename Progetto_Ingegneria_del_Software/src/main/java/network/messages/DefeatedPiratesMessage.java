package network.messages;

import controller.Controller;
import enumerations.GameState;
import exceptions.MultipleValidationErrorsException;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.*;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.HashMap;
import java.util.function.Consumer;

import static enumerations.PlayerState.*;

/**
 * Message for the players that have been defeated by the pirates and wants to activate shields
 */
public class DefeatedPiratesMessage extends Message {
    private Card card;
    private int sum;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     */
    public DefeatedPiratesMessage(String clientId, Card card, int sum) {
        super(clientId);
        this.card = card;
        this.sum = sum;
    }

    @Override
    public void process(Controller controller) {
        controller.handleDefeatedPiratesMessage(getClientId(), card, sum);
    }
}