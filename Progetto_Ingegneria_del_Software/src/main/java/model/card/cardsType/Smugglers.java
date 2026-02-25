/*
* SMUGGLER'S CLASS
 * This class extends the class Card
 *
 * Card description:
 * Smugglers and other enemies pose a
 * threat to everyone, but they attack the
 * players’ ships in order. First, they attack
 * the leader. If they win, they attack the
 * next player in line, and so on, until they
 * have attacked everyone or until someone
 * defeats them.
 * The upper right corner of the card shows what happens
 * if you lose. (These smugglers take your 2 most valuable
 * goods. If you run out of goods, they take batteries
 * instead.) The bottom part of the card shows what you
 * get if you win. (If you defeat Smugglers, you can load
 * the indicated goods)
 * The strength of the enemy is given by the number next to
 * the cannon symbol.
* */

package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
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
 * This class represents the smugglers card. It extends Card.
 */
public class Smugglers extends Card{
    private int enemyStrength;
    private int goodsLose;
    private int loseFlightDays;
    ArrayList<Color> goodsColors;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param enemyStrength is the enemies power.
     * @param goodsLose is the number of the goods lost.
     * @param loseFlightDays is the number of the flight days lost.
     * @param goodsColors is the list of goods gained.
     */
    public Smugglers(CardName cardType, int level, String url, int enemyStrength, int goodsLose, int loseFlightDays, ArrayList<Color> goodsColors) {
        super(cardType, level, url);
        this.enemyStrength = enemyStrength;
        this.goodsLose = goodsLose;
        this.loseFlightDays = loseFlightDays;
        this.goodsColors = goodsColors;
    }

    /**
     * @return the enemy strength. The player has to overcame it if he wants to win
     */
    public int getEnemyStrength(){
        return enemyStrength;
    }

    /**
     * @return the number of goods that the player has to give if he loses
     */
    public int getGoodsLose(){
        return goodsLose;
    }

    /**
     * @return the number of flight days lost
     */
    public int getLoseFlightDays(){
        return loseFlightDays;
    }

    /**
     *
     * @return the colors of the goods taken
     */
    public ArrayList<Color> getColorOfGoodsTaken(){
        return goodsColors;
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
                sender.accept(new DrawnCardMessage2(player.getId(), this)); // false if the player is not in turn
            }
        }

        gameModel.setGameState(GameState.SMUGGLERS);
    }

}
