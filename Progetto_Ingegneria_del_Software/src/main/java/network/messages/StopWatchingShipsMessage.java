package network.messages;

import controller.Controller;
import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;

import java.util.function.Consumer;

/**
 * Message that represents a request from the client to stop watching at the other players shipboards.
 */
public class StopWatchingShipsMessage extends Message {
    private Card card;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param card is the component picked.
     */

    public StopWatchingShipsMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;

    }

    @Override
    public void process(Controller controller) {
        controller.handleStopWatchingShipsMessage(getClientId(), card);
    }
}