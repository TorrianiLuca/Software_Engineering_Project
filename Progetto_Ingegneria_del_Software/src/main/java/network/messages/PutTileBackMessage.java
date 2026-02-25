package network.messages;

import controller.Controller;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.List;
import java.util.function.Consumer;

/**
 * Message used to notify that the card has been repositioned
 */

public class PutTileBackMessage extends Message {

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     */

    public PutTileBackMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void process(Controller controller) {
        controller.handlePutTileBackMessage(getClientId());
    }
}