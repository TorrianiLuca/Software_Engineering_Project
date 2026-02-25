package network.messages;

import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import network.structure.NetworkView;
import view.View;

import java.util.HashMap;

/**
 * Message that return the shipboards of all the players in the game.
 */
public class ReturnAllShipBoardsMessage extends Message {
    HashMap<String, ShipBoard> shipBoards = new HashMap<>();


    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param shipBoards are the player's ship boards.
     */
    public ReturnAllShipBoardsMessage(String clientId, HashMap<String, ShipBoard> shipBoards) {
        super(clientId);
        this.shipBoards = shipBoards;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showAllShipBoards(shipBoards);
    }
}
