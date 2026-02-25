package model.player;
/*
 * PLAYER'S CLASS
 * A player has a nickname, his color and his shipboard
 * During a game, a player can collect a certain number of cosmic credits
 * A player has his position on the flight board and a lap counter which is needed in order to understand
 * if he is lapped by another player
 * A player can have a tile, a card or a card pile in his hand
 * The flag "hasFinishedBuilding" indicates if he has completed his ship and if he is ready to get his ship checked
 * The points of every player are calculated at the end of the game
 */

import enumerations.*;
import enumerations.TypeSpace;
import model.GameModel;
import model.card.CardPile;
import model.shipBoard.ShipBoard;
import model.card.Card;
import model.shipBoard.ShipBoardSpace;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.shipBoard.ShipBoardSpace;
import observer.Observable;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Class that represents a player.
 */
public class Player extends Observable implements Serializable {
    private String nickname;
    private String id;
    private final ShipBoard shipBoard;
    private Color color;
    private int cosmicCredit;
    private int positionOnFlightBoard; // cell number
    private int lapCounter; //Number of laps completed by the player
    private boolean hasFinishedBuilding;
    private Card cardInHand;
    private ComponentTile tileInHand;
    private CardPile cardPileInHand;
    private PlayerState playerState;

    private ArrayList<Color> tempGoodsBlock; // temporary array to save the goods block that needs to be inserted in the cargo tiles
    private int penaltyGoods; // number of goods block to remove
    private int penaltyEquipment; // number of equipment to remove
    private boolean proceed;
    private boolean toBeRetiredFlag = false;

    /**
     * Constructor of the class Player.
     * @param nickname the player's nickname.
     * @param id is the id of the player.
     * @param color the player's color.
     * @param flightType the type of flight chosen.
     */
    public Player(String nickname, String id, Color color, FlightType flightType)
    {
        this.nickname = nickname;
        this.id = id;
        this.color = color;
        this.cosmicCredit =0;
        this.positionOnFlightBoard =0;
        this.lapCounter =0;
        this.hasFinishedBuilding = false;
        this.shipBoard= new ShipBoard(flightType, color);
        this.cardInHand = null;
        this.tileInHand = null;
        this.cardPileInHand = null;
        this.tempGoodsBlock = new ArrayList<>();
        this.proceed = false;
        this.playerState = PlayerState.IDLE;
    }

    /**
     * @return player's nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return player's ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the nickname of the player
     * @param nickname is the nickname of the player.
     */
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    /**
     * @return player's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the player.
     * @param color is the color of the player.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the flag hasFinishedBuilding.
     */
    public boolean getHasFinishedBuilding() {
        return hasFinishedBuilding;
    }

    /**
     * Set the flag hasFinishedBuilding.
     */
    public void setHasFinishedBuilding(boolean value) {
        this.hasFinishedBuilding = value;
    }

    /**
     * @return player's position on flight board.
     */
    public int getPositionOnFlightBoard() {
        return positionOnFlightBoard;
    }

    /**
     * Sets the player's position on flight board.
     * @param positionOnFlightBoard is the number of cell.
     */
    public void setPositionOnFlightBoard(int positionOnFlightBoard) {
        this.positionOnFlightBoard = positionOnFlightBoard;
    }

    /**
     * @return the player's shipboard
     */
    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    /**
     * @return the number of player's cosmic credits
     */
    public int getCosmicCredit() {
        return cosmicCredit;
    }

    /**
     * Method that increments the number of cosmic credits.
     * @param number is the number of cosmic credit.
     */
    public void incrementCosmicCredit(int number) {
        this.cosmicCredit += number;
    }

    /**
     * Method that decreases the number of cosmic credits.
     * @param number is the number of cosmic credit.
     */
    public void decreaseCosmicCredit(int number) {
        this.cosmicCredit -= number;
    }

    /**
     * Method that is used by the leader to take a card
     */
    public void takeCardInHand(Card card) {
        cardInHand=card;
    }

    /**
     * @return the card that the leader has taken.
     */
    public Card getCard() {
        return this.cardInHand;
    }

    /**
     * Method that is used by the player to take a tile.
     */
    public void addTileInHand(ComponentTile tile) {
        this.tileInHand = tile;
    }

    /**
     * @return the tile that the player has taken
     */
    public ComponentTile getTileInHand() {
        return tileInHand;
    }

    /**
     * Method that sets the tile in hand to null.
     */
    public void removeTileInHand(){
        tileInHand = null;
    }

    /**
     * @return the number of laps that the player has completed.
     */
    public int getLapCounter() {
        return lapCounter;
    }

    /**
     * Method that sets the lap counter.
     * @param lapCounter is the number of laps the player has completed.
     */
    public void setLapCounter(int lapCounter) {
        this.lapCounter = lapCounter;
    }

    /**
     * With this method the player takes a pile.
     */
    public CardPile getCardPileInHand() {
        return cardPileInHand;
    }

    /**
     * With this method the player takes a card pile.
     */
    public void addCardPileInHand(CardPile cardPile) {
        this.cardPileInHand = cardPile;
    }

    /**
     * Method that sets the card pile in hand to null.
     */
    public void removeCardPileInHand() {
        cardPileInHand = null;
    }

    /**
     * @return the card pile that the player has taken.
     */
    public CardPile getCardPile() {
        return cardPileInHand;
    }

    /**
     * Getter method that returns tempGoodsBlock.
     * @return tempGoodsBlock.
     */
    public ArrayList<Color> getTempGoodsBlock() {
        return tempGoodsBlock;
    }

    /**
     * Setter method that inserts a new GoodBlock in the temporary array.
     * @param color is the color of the goods block.
     */
    public void insertGoodsBlock(Color color) {
        tempGoodsBlock.add(color);
    }

    /**
     * Method that returns the number of penalty goods.
     * @return the number of penalty goods of the player.
     */
    public int getPenaltyGoods() {
        return penaltyGoods;
    }

    /**
     * Setter method that sets the number of penalty goods the player has to remove -- penalty card Smugglers
     * @param penaltyGoods is the number of goods to remove for penalty.
     */
    public void setPenaltyGoods(int penaltyGoods) {
        this.penaltyGoods = penaltyGoods;
    }

    /**
     * Method that decrements the number of penalty goods
     */
    public void decrementPenaltyGoods() {
        this.penaltyGoods--;
    }

    /**
     * Method that returns the number of penalty equipment.
     * @return the number of penalty equipment of the player.
     */
    public int getPenaltyEquipment() {
        return penaltyEquipment;
    }

    /**
     * Setter method that sets the number of penalty equipment figures the player has to remove -- penalty card Slavers
     * @param penaltyEquipment is the number of figures to remove for penalty.
     */
    public void setPenaltyEquipment(int penaltyEquipment) {
        this.penaltyEquipment = penaltyEquipment;
    }

    /**
     * Method that decrements the number of penalty equipment
     */
    public void decrementPenaltyEquipment() {
        this.penaltyEquipment--;
    }

    /**
     * Getter method that returns if the player has already made the choice to proceed.
     * @return {@code true} if the player has proceeded, {@code false} otherwise.
     */
    public boolean getProceed() {
        return proceed;
    }

    /**
     * Setter method that sets proceed parameter.
     * @param proceed is the choice made by the player to proceed during the game.
     */
    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    /**
     * Method that returns the player state.
     * @return this player current state.
     */
    public PlayerState getPlayerState() {
        return playerState;
    }

    /**
     * Method that sets the player state to the given one.
     * @param playerState is the player state we want to set.
     */
    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    /**
     * Method that sets a flag used to the game to remember to remove the player from the game in the next turn.
     * @param toBeRetiredFlag is the flag.
     */
    public void setToBeRetiredFlag(boolean toBeRetiredFlag) {
        this.toBeRetiredFlag = toBeRetiredFlag;
    }

    /**
     * @return if the player has to be retired from the game.
     */
    public boolean getToBeRetiredFlag() {
        return toBeRetiredFlag;
    }
}
