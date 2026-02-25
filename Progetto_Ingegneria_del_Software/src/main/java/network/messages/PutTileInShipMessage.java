package network.messages;

import controller.Controller;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message used to notify that the card has just been inserted in the ship board
 */

public class PutTileInShipMessage extends Message {
    private int row;
    private int col;


    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param row is the row on the ship.
     * @param col is the column on the ship.
     */
    public PutTileInShipMessage(String clientId, int row,  int col) {
        super(clientId);
        this.row = row;
        this.col = col;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePutTileInShipMessage(getClientId(), row, col);
    }
}