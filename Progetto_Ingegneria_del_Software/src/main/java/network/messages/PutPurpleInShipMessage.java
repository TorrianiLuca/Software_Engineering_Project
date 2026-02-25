package network.messages;

import controller.Controller;
import enumerations.Color;
import enumerations.FlightType;
import enumerations.GameState;
import enumerations.TileName;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.shipBoard.ShipBoardSpace;
import model.tiles.ComponentTile;
import model.tiles.componentTile.AlienCabine;
import model.tiles.componentTile.Cabine;
import model.tiles.componentTile.StartingCabine;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message used to inform the model that the player wants to put the purple alien in a specific position.
 */
public class PutPurpleInShipMessage extends Message {
    private int row;
    private int col;



    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param row is the row on the ship.
     * @param col is the column on the ship.
     */
    public PutPurpleInShipMessage(String clientId, int row,  int col) {
        super(clientId);
        this.row = row;
        this.col = col;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePutPurpleInShipMessage(getClientId(), row, col);
    }
}