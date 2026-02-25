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
 * Message used to inform the position of the alien in the ship board
 **/

public class PutBrownInShipMessage extends Message {
    private int row;
    private int col;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     */

    public PutBrownInShipMessage(String clientId, int row,  int col) {
        super(clientId);
        this.row = row;
        this.col = col;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePutBrownInShipMessage(getClientId(), row, col);
    }
}