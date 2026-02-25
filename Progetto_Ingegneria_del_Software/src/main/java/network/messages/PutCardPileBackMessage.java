package network.messages;

import controller.Controller;
import enumerations.GameState;
import model.GameModel;
import model.card.CardPile;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.List;
import java.util.function.Consumer;

/**
 * Message informing that the card stack has been reset
 */

public class PutCardPileBackMessage extends Message {

    /**
     * default constructor
     * @param clientId is the sender of the message
     **/
    public PutCardPileBackMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void process(Controller controller) {
        controller.handlePutCardPileBackMessage(getClientId());
    }
}