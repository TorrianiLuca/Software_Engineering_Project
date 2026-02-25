package network.messages;

import controller.Controller;
import enumerations.GameState;
import enumerations.TypeSpace;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Message that contains the id of the tile the player wants to pick up.
 */
public class PickUpTileMessage extends Message {
    private final int tileId;

    /**
     * Default constructor.
     * @param clientId is the sender of the message.
     * @param tileId is the number of th tile the player wants to take
     */
    public PickUpTileMessage(String clientId, int tileId) {
        super(clientId);
        this.tileId = tileId;
    }

    /**
     * @return the id of the tile.
     */
    public int getTileId() {
        return tileId;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePickUpTileMessage(getClientId(), tileId);
    }
}