package network.messages;

import controller.Controller;
import controller.GameEventListener;
import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

/**
 * Message that informs the model if the player has retired from the game
 */
public class RetireMessage extends Message {
    GameEventListener listener;

    public RetireMessage(String clientId) {
        super(clientId);
    }

    /**
     * Method that sets the listener.
     * @param listener is the listener that has to be set.
     */
    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(Controller controller) {
        controller.handleRetireMessage(getClientId(), listener);
    }
}