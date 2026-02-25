package network.messages;

import controller.Controller;
import enumerations.FlightType;
import enumerations.GameState;
import enumerations.TileName;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;

import model.shipBoard.ShipBoardSpace;
import model.tiles.ComponentTile;
import model.tiles.componentTile.Cabine;
import model.tiles.componentTile.StartingCabine;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message used to notify the position of the object in the ship board
 */

public class PutFigureInShipMessage extends Message {
    private int row;
    private int col;


    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param row is the row on the ship.
     * @param col is the column on the ship.
     */
    public PutFigureInShipMessage(String clientId, int row,  int col) {
        super(clientId);
        this.row = row;
        this.col = col;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePutFigureInShipMessage(getClientId(), row, col);
    }
}