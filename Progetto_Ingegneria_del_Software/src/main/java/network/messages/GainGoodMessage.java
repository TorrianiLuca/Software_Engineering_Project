package network.messages;

import controller.Controller;
import enumerations.*;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.componentTile.Cargo;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message that contains the coordinates of the tile in which the player wants to put the goods block.
 */
public class GainGoodMessage extends Message{
    private Card card;
    private String choice;
    private int col;
    private int row;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     * @param choice is the choice made by the player: put/skip.
     * @param col is the column selected by the player.
     * @param row is the row selected by the player.
     */
    public GainGoodMessage(String clientId, String choice, Card card, int row, int col) {
        super(clientId);
        this.choice = choice;
        this.card = card;
        this.row = row;
        this.col = col;
    }

    @Override
    public void process(Controller controller) {
        controller.handleGainGoodMessage(getClientId(), choice, card, row, col);
    }
}