package network.messages;

import controller.Controller;
import enumerations.*;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.componentTile.Cabine;
import model.tiles.componentTile.StartingCabine;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message that contains the coordinates of the tile from which the player wants to remove the figure.
 */
public class RemoveFigureMessage extends Message{
    private Card card;
    private int col;
    private int row;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     * @param col is the column selected by the player.
     * @param row is the row selected by the player.
     */
    public RemoveFigureMessage(String clientId, Card card, int row, int col) {
        super(clientId);
        this.card = card;
        this.row = row;
        this.col = col;
    }

    /**
     * Getter method that returns the column selected by the player.
     * @return the column.
     */
    public int getCol() {
        return col;
    }

    /**
     * Getter method that returns the row selected by the player.
     * @return the row.
     */
    public int getRow() {
        return row;
    }

    @Override
    public void process(Controller controller) {
        controller.handleRemoveFigureMessage(getClientId(), card, row, col);
    }
}