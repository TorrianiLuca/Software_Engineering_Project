package network.messages;

import controller.Controller;
import enumerations.Color;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.Smugglers;
import model.player.Player;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message that contains when the player wants to proceed to fight the smugglers.
 */
public class SmugglersChoiceMessage extends Message {
    private Card card;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     */
    public SmugglersChoiceMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handleSmugglersChoiceMessage(getClientId(), card);
    }
}