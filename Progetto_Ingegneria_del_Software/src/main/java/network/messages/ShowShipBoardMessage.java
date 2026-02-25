package network.messages;

import enumerations.GameState;
import model.GameModel;
import model.shipBoard.ShipBoard;
import network.structure.NetworkView;
import view.View;

/**
 * Message that contains the shipboard associated to the player.
 */

public class ShowShipBoardMessage extends Message {
    private ShipBoard shipBoard;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     */

    public ShowShipBoardMessage(String clientId, ShipBoard shipBoard) {
        super(clientId);
        this.shipBoard = shipBoard;
    }


    /**
     * @return the ship board of the player
     */

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showShipBoard(shipBoard);
    }

}