package network.messages;

import controller.Controller;
import enumerations.Color;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.Smugglers;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Message that contains the coordinates od the cannon the player wants to activate.
 */
public class ActivateEngineMessage extends Message {
    private Card card;
    private int col;
    private int row;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     * @param col is the column selected by the player.
     * @param row is the row selected by the player.
     */
    public ActivateEngineMessage(String clientId, Card card, int row, int col) {
        super(clientId);
        this.card = card;
        this.col = col;
        this.row = row;
    }

    /**
     * Getter method that returns the card selected for this turn.
     * @return card picked up for this game turn.
     */
    public Card getCard() {
        return card;
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
        controller.handleActivateEngineMessage(getClientId(), card, col, row);
    }
}