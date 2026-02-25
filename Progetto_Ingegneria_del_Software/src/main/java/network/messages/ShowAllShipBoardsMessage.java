package network.messages;

import controller.Controller;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import network.structure.NetworkView;
import view.View;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message that contains a request to show all the shipboard associated to each player.
 */

public class ShowAllShipBoardsMessage extends Message {

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     */
    public ShowAllShipBoardsMessage(String clientId) {
        super(clientId);
    }

    @Override
    public void process(Controller controller) {
        controller.handleShowAllShipBoardsMessage(getClientId());
    }

}