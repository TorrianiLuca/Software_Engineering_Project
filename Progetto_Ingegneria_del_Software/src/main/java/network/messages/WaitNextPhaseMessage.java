package network.messages;

import controller.Controller;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.CombatZone;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Message that informs the player that he has to wait the other players to proceed in the next phase.
 */
public class WaitNextPhaseMessage extends Message {
    private Card card;

    /**
     * Constructor.
     * @param clientId is the client id.
     * @param card is the card currently in use.
     */
    public WaitNextPhaseMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handleWaitNextPhaseMessage(getClientId(), card);
    }
}