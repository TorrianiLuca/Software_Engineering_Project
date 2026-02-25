/*
* SLAVER'S CLASS
* This class extends the class Card
 *
 * Card description:
 * If the Slavers defeat you, they force
 * you to give up some of your crew.
 * You choose which humans or
 * aliens to surrender in exchange for
 * your own freedom.
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
 * This class represents the slavers card. It extends Card.
 */
public class Slavers extends Card {
    private int enemyStrength;
    private int numOfLoseFigures;
    private int  loseFlightDays;
    private int numOfCreditsTaken;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param enemyStrength is the enemies power.
     * @param numOfLoseFigures is the number of lost figures.
     * @param loseFlightDays is the number of the flight days lost.
     * @param numOfCreditsTaken is the number of credits gained.
     */
    public Slavers(CardName cardType, int level, String url, int enemyStrength, int numOfLoseFigures, int  loseFlightDays, int numOfCreditsTaken ) {
        super(cardType, level, url);
        this.enemyStrength = enemyStrength;
        this.numOfLoseFigures = numOfLoseFigures;
        this.loseFlightDays = loseFlightDays;
        this.numOfCreditsTaken = numOfCreditsTaken;

    }

    /**
     * @return the enemy strength. The player has to overcame it if he wants to win
     */
    public int getEnemyStrength(){
        return enemyStrength;
    }

    /**
     * @return the number of lost figures
     */
    public int getNumOfLoseFigures(){
        return numOfLoseFigures;
    }

    /**
     * @return the number of flight days lost
     */
    public int getLoseFlightDays(){
        return loseFlightDays;
    }

    /**
     * @return the number of cosmic credits taken
     */
    public int getNumOfCreditsTaken(){
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

        gameModel.setGameState(GameState.SLAVERS);
    }
}
