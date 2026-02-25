/*
* ABANDONED SHIP'S CLASS
* This class extends the class Card
*
* Card description:
* If you find an Abandoned Ship, you’re in gravy!
* There’s probably some sort of
* protocol for reporting these things, but
* who cares? Fix it up and sell it to your
* crew. (Yeah, they’re so sick of flying with
* you, they’ll pay for the chance to jump ship.)
* Only one player can use this opportunity. The leader
* decides first. The leader can give up the specified
* number of crew figures and take the indicated number
* of cosmic credits. This also costs flight days.
* If the leader chooses not to use the opportunity, it falls
* to the next player in line, and so on. Once one player
 * fixes up the ship, the remaining players are out of luck.
* */

package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import network.messages.DrawnCardMessage;
import network.messages.DrawnCardMessage2;
import network.messages.Message;
import network.messages.SetCardInUseMessage;

import java.util.function.Consumer;

/**
 * This class represents the abandoned ship card. It extends Card.
 */
public class AbandonedShip extends Card {
    private int numOfLoseFigures;
    private int loseFlightDays;
    private int numOfCreditsTaken;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param numOfLoseFigures is the number of lost figures.
     * @param loseFlightDays is the number of the flight days lost.
     * @param numOfCreditsTaken is the number of cosmic credits gained.
     */
    public AbandonedShip(CardName cardType, int level, String url, int numOfLoseFigures, int loseFlightDays, int numOfCreditsTaken) {
        super(cardType, level, url);
        this.numOfLoseFigures = numOfLoseFigures;
        this.loseFlightDays = loseFlightDays;
        this.numOfCreditsTaken = numOfCreditsTaken;
    }

    /**
     * @return the number of figures (alien or astronaut) lost
     */
    public int getNumOfLoseFigures() {
        return numOfLoseFigures;
    }

    /**
     * @return the number cosmic of credits gained
     */
    public int getNumOfCreditsTaken() {
        return numOfCreditsTaken;
    }

    /**
     * @return the number of flight days lost
     */
    public int getLoseFlightDays() {
        return loseFlightDays;
    }

    /**
     * Method that is used to check if a player can accept the proposal of the card.
     * @param player is the player currently in turn.
     * @return {@code false} if the player has not enough figures, {@code true} otherwise.
     */
    public boolean processAbandonedShipChoice(Player player) {
        int numFigures = player.getShipBoard().getNumFigures();
        if(player.getShipBoard().getHasPurpleAlien()) {
            numFigures++;
        }

        if(player.getShipBoard().getHasBrownAlien()) {
            numFigures++;
        }

        if (numFigures < numOfLoseFigures) {
            return false;
        }
        return true;
    }

    @Override
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {
        // Sets the leader as the player in turn
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

        for(Player player : gameModel.getPlayers()) {
            if(!gameModel.getRetiredPlayers().contains(player)) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    sender.accept(new SetCardInUseMessage(player.getId(),this));
                    sender.accept(new DrawnCardMessage(player.getId(), this, true)); // true if the player is in turn
                }
                else {
                    sender.accept(new SetCardInUseMessage(player.getId(),this));
                    sender.accept(new DrawnCardMessage(player.getId(), this, false)); // false if the player is not in turn
                }
            }
            else {
                sender.accept(new SetCardInUseMessage(player.getId(),this));
                sender.accept(new DrawnCardMessage2(player.getId(), this));
            }
        }

        gameModel.setGameState(GameState.ABANDONED_SHIP);
    }
}
