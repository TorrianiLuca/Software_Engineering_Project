/*
* ABANDONED STATION'S CLASS
 * This class extends the class Card
 *
 * Card description:
 * When fleeing the tragic disaster that
 * befell this space station, the inhabitants
 * probably left behind some good loot. It will
 * take a big crew to search for it, though.
 * To use this opportunity, you must have at
 * least as many crew as shown on the card.
 * Only one player can use this opportunity. The leader
 * decides first. If the leader does not have enough crew
 * or if the leader does not wish to lose those flight days,
 * the leader can pass the opportunity to the next player
 * along the flight. Once someone decides to dock, the
 * others are out of luck.
 * When you dock with a space station, load the indicated
 *  on your ship. (See Gaining Goods on page 11.)
 * You can rearrange or discard goods at this time. Move
 * your marker back the indicated number of flight days.
 * Note that on an Abandoned Station you lose no crew.
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
 * This class represents the abandoned station card. It extends Card.
 */
public class AbandonedStation extends Card {
    private int numOfFigureRequired;
    private int loseFlightDays;
    private ArrayList<Color> goodsColors;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param numOfFigureRequired is the number of figures required.
     * @param loseFlightDays is the number of the flight days lost.
     * @param goodsColors is the list of goods gained.
     */
    public AbandonedStation(CardName cardType, int level, String url, int numOfFigureRequired, int loseFlightDays, ArrayList<Color> goodsColors) {
        super(cardType, level, url);
        this.numOfFigureRequired = numOfFigureRequired;
        this.loseFlightDays = loseFlightDays;
        this.goodsColors = goodsColors;

    }

    /**
     * @return the number of figures (aliens or astronauts) required
     */
    public int getNumOfFigureRequired() {
        return numOfFigureRequired;
    }

    /**
     * @return the number of flight days lost
     */
    public int getLoseFlightDays() {
        return loseFlightDays;
    }

    /**
     * @return the colors of the goods gained
     */
    public ArrayList<Color> getColorOfGoodsTaken() {
        return goodsColors;
    }

    /**
     * Method that is used to check if a player can accept the proposal of the card.
     * @param player is the player currently in turn.
     * @return {@code false} if the player has not enough figures, {@code true} otherwise.
     */
    public boolean processAbandonedStationChoice(Player player) {
        int crew=player.getShipBoard().getNumFigures();
        if(player.getShipBoard().getHasPurpleAlien()) {
            crew++;
        }
        if(player.getShipBoard().getHasBrownAlien()) {
            crew++;
        }
        if (crew < numOfFigureRequired) {
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

        gameModel.setGameState(GameState.ABANDONED_STATION);
    }
}
