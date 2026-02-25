package network.messages;

import controller.Controller;
import enumerations.Color;
import enumerations.FlightType;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.Smugglers;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message that contains when the player wants to proceed to flight in the open space.
 */
public class OpenSpaceChoiceMessage extends Message {
    private Card card;

    /**
     * Default constructor.
     * @param clientId is the id of the client.
     * @param card is the card currently in use.
     */
    public OpenSpaceChoiceMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handleOpenSpaceChoiceMessage(getClientId(), card);
    }
}