package network.messages;


import controller.Controller;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.card.CardPile;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Message that contains the number of the card pile the player wants to pick up.
 */
public class PickUpCardPileMessage extends Message {
    private final int numCardPile;

    /**
     * Default constructor.
     * @param clientId is the sender of the message.
     * @param numCardPile is the number of the pile the player wants to observe
     */
    public PickUpCardPileMessage(String clientId, int numCardPile) {
        super(clientId);
        this.numCardPile = numCardPile;
    }


    @Override
    public void process(Controller controller) {
        controller.handlePickUpCardPileMessage(getClientId(), numCardPile);
    }
}
