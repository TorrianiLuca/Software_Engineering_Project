package network.messages;


import controller.Controller;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Message that contains the row and the column of the tile the player wants to remove from the ship..
 */
public class RemoveTileMessage extends Message {
    private final int row;
    private final int col;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param row is the row on the ship.
     * @param col is the column on the ship.
     */
    public RemoveTileMessage(String clientId, int row, int col) {
        super(clientId);
        this.row = row;
        this.col = col;
    }

    /**
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return col;
    }

    @Override
    public void process(Controller controller) {
        controller.handleRemoveTileMessage(getClientId(), row, col);
    }
}
