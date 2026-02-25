package network.messages;

import controller.Controller;
import enumerations.GameState;
import exceptions.MultipleValidationErrorsException;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.MeteorSwarm;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.HashMap;
import java.util.function.Consumer;

import static enumerations.PlayerState.*;

/**
 * Message that contains when the player wants to proceed in the meteor swarm.
 */
public class MeteorSwarmChoiceMessage extends Message {
    private Card card;
    private int sum;

    /**
     * Default constructor.
     * @param clientId is the player id.
     * @param card is the card in use.
     * @param sum is the row/col the meteor will hit.
     */
    public MeteorSwarmChoiceMessage(String clientId, Card card, int sum) {
        super(clientId);
        this.card = card;
        this.sum = sum;
    }

    @Override
    public void process(Controller controller) {
        controller.handleMeteorSwarmChoiceMessage(getClientId(), card, sum);
    }
}