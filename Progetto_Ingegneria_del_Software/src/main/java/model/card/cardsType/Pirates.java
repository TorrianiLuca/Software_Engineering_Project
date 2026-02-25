/*
* PIRATES'S CLASS
* This class extends the class Card
 *
 * Card description:
 * If the Pirates defeat you, your ship
 * gets shot at. (The card indicates
 * the size and direction of the
 * cannon fire.) Keep track of all the
 * players who were defeated and
 * then have the first defeated player
 * roll two dice to determine the row
 * or column of each shot. This roll
 * applies to all defeated players.
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

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * This class represents the pirates card. It extends Card.
 */
public class Pirates extends Card {
    private int enemyStrength;
    private ArrayList<Integer> shotsPowerArray; //Power of the shots (the direction is always nord)
    private int loseFlightDays;
    private int numOfCreditsTaken;
    private int counter;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param enemyStrength is the enemies power.
     * @param shotsPowerArray is the list of the cannon shots.
     * @param loseFlightDays is the number of the flight days lost.
     * @param numOfCreditsTaken is the number of credits gained.
     */
    public Pirates(CardName cardType, int level, String url, int enemyStrength, ArrayList<Integer> shotsPowerArray, int loseFlightDays, int numOfCreditsTaken) {
        super(cardType, level, url);
        this.enemyStrength = enemyStrength;
        this.shotsPowerArray = shotsPowerArray;
        this.loseFlightDays = loseFlightDays;
        this.numOfCreditsTaken = numOfCreditsTaken;
        this.counter = 0;
    }

    /**
     * @return the cannon shots counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Setter method that increments the counter
     */
    public void incrementCounter() {
        counter++;
    }

    /**
     * @return the enemy strength. The player has to overcame it if he wants to win
     */
    public int getEnemyStrength()
    {
        return enemyStrength;
    }

    /**
     * @return the power of the enemy shots
     */
    public ArrayList<Integer> getShotsPowerArray() {
        return shotsPowerArray;
    }

    /**
     * @return the number of flight days lost
     */
    public int getLoseFlightDays() {
        return loseFlightDays;
    }

    /**
     * @return the number of cosmic credits taken
     */
    public int getNumOfCreditsTaken() {
        return numOfCreditsTaken;
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

        gameModel.setGameState(GameState.PIRATES);
    }

}
