package network.messages;

import model.card.Card;

import java.util.ArrayList;

/**
 * Message used to ask the player the maximum number of players and the flight type chosen for the game.
 */
public class AskMaxPlayerAndFlightTypeMessage extends Message {
    /**
     * Default constructor.
     */
    public AskMaxPlayerAndFlightTypeMessage(String clientId) {
        super(clientId);
    }
}