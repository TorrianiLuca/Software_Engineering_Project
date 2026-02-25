package network.messages;


import controller.Controller;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Message that contains the direction the player wants to set the tile.
 */
public class ChangeTileDirectionMessage extends Message {
    private final String direction;

    /**
     * Default constructor.
     * @param clientId is the sender of the message.
     * @param direction is the direction of the component the player has in hand.
     */
    public ChangeTileDirectionMessage(String clientId, String  direction) {
        super(clientId);
        this.direction = direction;
    }


    /**
     * @return the direction set.
     */
    public String getDirection() {
        return direction;
    }


    @Override
    public void process(Controller controller) {
        controller.handleChangeTileDirectionMessage(getClientId(), direction);
    }
}