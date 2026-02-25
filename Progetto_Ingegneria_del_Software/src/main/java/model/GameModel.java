package model;
import enumerations.FlightType;
import enumerations.GameState;
import model.card.Card;
import model.card.CardPile;
import model.card.CardsDeck;
import support.Couple;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import network.messages.Message;
import observer.Observable;
import observer.Observer;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents the game. It contains the main components for the game.
 */
public class GameModel extends Observable implements Observer, Serializable {
    private ArrayList<String> nicknames;
    private ArrayList<String> ids;
    private ArrayList<Player> players;
    private FlightBoard flightBoard;
    private FlightType flightType = null;
    private GameTile gameTile;
    private Player leader;
    public static int MAX_PLAYERS = 4;
    private int chosenPlayersNumber = 1;
    private int connectedPlayers = 0;
    private GameState gameState;
    private ArrayList<Card> cardsToPlay;
    private int timerPosition;
    private AtomicBoolean timerActive;
    private ArrayList<Player> position;
    private Player playerInTurn;
    private ArrayList<Player> defeatedPlayers; // used in slavers
    private ArrayList<Player> retiredPlayers;
    private ArrayList<Player> tempPositions; // used to save the positions of the players after they have finished the building phase
    private ArrayList<CardPile> cardsPile;

    /**
     * Constructor.
     */
    public GameModel() {
        this.players = new ArrayList<>();
        this.nicknames = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.cardsToPlay = new ArrayList<>();
        timerActive = new AtomicBoolean(false);
        timerPosition = 2;
        tempPositions = new ArrayList<>();
        cardsPile = new ArrayList<>();
        defeatedPlayers = new ArrayList<>();
        retiredPlayers = new ArrayList<>();
    }

    /**
     * This method adds a player to the game, if there is space.
     * @param player is the player that will be added to this game.
     */
    public void addPlayer(Player player){
        if(players.size() >= chosenPlayersNumber){
        }
        else if(player.getNickname() == null){
            throw new NullPointerException();
        }
        else{
            players.add(player);
        }
    }

    /**
     * This method checks if the number of player is within the correct bounds.
     * @param chosenMaxPlayers is the number of player chosen by the first player.
     * @return {@code true} if the number is within the bounds, {@code false} if not.
     */
    public boolean isInBound(int chosenMaxPlayers) {
        if(chosenMaxPlayers >= 2 && chosenMaxPlayers <= 4){
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Setter method that sets the number of player for the game.
     * @param chosenMaxPlayers is the number of player chosen by the first player.
     */
    public void setMaxPlayers(int chosenMaxPlayers){
        this.chosenPlayersNumber = chosenMaxPlayers;
    }

    /**
     * Getter method that returns the number of player for the game.
     * @return the number of players allowed for this game.
     */
    public int getMaxPlayers(){
        return this.chosenPlayersNumber;
    }

    /**
     * Setter method that sets the flight type for the game.
     * @param chosenFlightType is the flight type chosen by the first player.
     */
    public void setFlightType(FlightType chosenFlightType){
        this.flightType = chosenFlightType;
    }

    /**
     * Getter method that returns the flight type chosen for the game.
     * @return the flight type of this game.
     */
    public FlightType getFlightType(){
        return this.flightType;
    }

    /**
     * Setter method for the state of the game.
     * @param gameState is the new state of the game
     */
    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }

    /**
     * Getter method that returns the state of the game.
     * @return the current state of the game
     */
    public GameState getGameState(){
        return this.gameState;
    }

    /**
     * This method creates the correct gameTile for the game.
     */
    public void setGameTile() {
        if(flightType != null){
            gameTile = new GameTile(flightType);
        }
    }

    /**
     * This method creates the correct flight board for the game.
     */
    public void setFlightBoard() {
        if(flightType != null){
            flightBoard = new FlightBoard(this);
        }
    }

    /**
     * Getter method that returns the flight board used for this game.
     * @return the flight board used.
     */
    public FlightBoard getFlightBoard(){
        return flightBoard;
    }

    /**
     * This method creates the correct card piles for the game, if the flight type is standard.
     */
    public void setCardsPiles() {
        if(flightType == FlightType.STANDARD_FLIGHT){
            CardsDeck cardsDeck = new CardsDeck(FlightType.STANDARD_FLIGHT);

            for (int i = 0; i < 4; i++) {
                ArrayList<Card> temp = new ArrayList<>();
                temp.add(cardsDeck.removeCard(1));
                temp.add(cardsDeck.removeCard(2));
                temp.add(cardsDeck.removeCard(2));
                cardsPile.add(new CardPile(i + 1, temp));
            }
        }
    }

    /**
     * This method returns the piles and the cards contained in it.
     * @return the hashmap containing the piles for the game.
     */
    public ArrayList<CardPile> getCardsPile(){
        return cardsPile;
    }

    /**
     * This method creates the deck of cards that has to be played during the game.
     * If the flight type is first, the cards are 8, if the flight type is standard it merges and shuffle the 4 cards piles deck.
     */
    public void setCardsToPlay() {
        if(flightType == FlightType.STANDARD_FLIGHT){
            for(CardPile cardsPile : cardsPile){
                cardsToPlay.addAll(cardsPile.getCards());
            }
            Collections.shuffle(cardsToPlay);
        }
        else if(flightType == FlightType.FIRST_FLIGHT){
            CardsDeck cardsDeck = new CardsDeck(FlightType.FIRST_FLIGHT);
            cardsToPlay.addAll(cardsDeck.getCardsDeck());
            Collections.shuffle(cardsToPlay);
        }
    }

    /**
     * This method returns the deck of cards that have to be played.
     * @return the remaining cards contained in the deck.
     */
    public ArrayList<Card> getCardsToPlay(){
        return cardsToPlay;
    }

    /**
     * This method removes from the deck the card extracted (the first card in the deck).
     */
    public void removeCardFromPlay(){
        cardsToPlay.remove(0);
    }

    /**
     * Setter method that changes the player in turn
     * @param player is the player that is currently in turn.
     */
    public void setPlayerInTurn(Player player) {
        this.playerInTurn = player;
    }

    /**
     * Getter method that return the current player in turn.
     * @return the player in turn.
     */
    public Player getPlayerInTurn(){
        return this.playerInTurn;
    }


    /**
     * Getter method that returns the player that has to play next.
     * @return the next player in the list position.
     */
    public Player getNextPlayer() {
        int currPlayerIndex = position.indexOf(this.playerInTurn);
        return position.get((currPlayerIndex + 1) % getPlayersPosition().size());

    }

    /**
     * Method that return the players for this game.
     * @return the players connected to this game.
     */
    public ArrayList<Player> getPlayers(){
        return players;
    }

    /**
     * Method that return the defeated players in the pirates card.
     * @return the players defeated.
     */
    public ArrayList<Player> getDefeatedPlayers(){
        return defeatedPlayers;
    }

    /**
     * Method that adds a player to the defeated players.
     * @param player is the player that has to be added.
     */
    public void addDefeatedPlayers(Player player){
        this.defeatedPlayers.add(player);
    }

    /**
     * Method that refresh the player position, after the game turn has finished.
     */
    public void refreshPlayersPosition(){
        HashMap<Player, Couple<Integer, Integer>> players = flightBoard.getPlayersMap();
        List<Map.Entry<Player, Couple<Integer, Integer>>> entries = new ArrayList<>(players.entrySet());

        Collections.sort(entries, (e1, e2) -> {
            int pos1 = e1.getValue().getFirst();
            int lap1 = e1.getValue().getSecond();
            int pos2 = e2.getValue().getFirst();
            int lap2 = e2.getValue().getSecond();
            if (lap1 != lap2) {
                return lap2 - lap1;
            }
            return pos2 - pos1;
        });

        ArrayList<Player> sortedPlayerNames = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            sortedPlayerNames.add(entries.get(i).getKey());
        }

        position = sortedPlayerNames;

        for (Player player : sortedPlayerNames) {
            System.out.println(player.getNickname());
        }
        this.playerInTurn = position.get(0);
    }

    /**
     * Method that return the players relative position in this turn (first, second, third, fourth).
     * @return the players ordered by their position on flight board.
     */
    public ArrayList<Player> getPlayersPosition(){
        return position;
    }

    /**
     * @return the number of hourglass flips remaining.
     */
    public int getTimerPosition() {
        return timerPosition;
    }

    /**
     * Method that sets the timer position.
     * @param timerPosition represents the number of hourglass flips remaining.
     */
    public void setTimerPosition(int timerPosition) {
        this.timerPosition = timerPosition;
    }

    /**
     * @return {@code true} if the timer is already active, {@code false} otherwise.
     */
    public boolean getTimerActive() {
        return timerActive.get();
    }

    /**
     * Method that sets the timer to active/inactive.
     * @param value indicates is the timer is set to true or false
     */
    public void setTimerActive(boolean value) {
        timerActive.set(value);
    }

    /**
     * Method that tries to flip the timer (if it is inactive).
     * @return {@code true} if the flip is successful, {@code false} if the timer is already active.
     */
    public boolean activateTimer() {
        return timerActive.compareAndSet(false, true);
    }

    /**
     * Method that returns the number of tiles for the game.
     * @return the number of tiles.
     */
    public int getNumTiles() {
        if(flightType == FlightType.STANDARD_FLIGHT){
            return 152;
        }
        return 140;
    }

    /**
     * Method used to pick the tile with the given ID.
     * @param id is the tile ID.
     * @return the {@link ComponentTile} picked.
     */
    public ComponentTile pickTile(int id) {
        ComponentTile componentTile = gameTile.getComponentTile(id);
        if(componentTile == null){
            return null;
        }
        if (componentTile.tryOccupy()) {
            return componentTile;
        }
        return null;
    }

    /**
     * Method used to put the tile back in the deck.
     * @param componentTile is the component tile considered.
     * @return {@code true} if the release is successful, {@code false} otherwise.
     */
    public boolean putTileBack(ComponentTile componentTile) {
        if (!componentTile.tryRelease()) {
            return false;
        }
        return true;
    }

    /**
     * @return the deck of tiles used for the game
     */
    public GameTile getGameTile() {
        return gameTile;
    }

    /**
     * Method that adds a player to the temp position array
     * @param player is the player that has to be added.
     */
    public void addTempPosition(Player player) {
        if(!tempPositions.contains(player)){
            tempPositions.add(player);
        }
    }

    /**
     * @return the list of players in the temp positions array.
     */
    public ArrayList<Player> getTempPositions() {
        return tempPositions;
    }

    /**
     * Method that removes a player from the temp positions array.
     * @param player is the player that has to be removed.
     */
    public void removeTempPosition(Player player) {
        tempPositions.remove(player);
    }

    /**
     * Method used to pick a card pile.
     * @param num is the card pile number.
     * @return the card pile if the occupation is successful, null otherwise.
     */
    public CardPile pickCardPile(int num) {
        CardPile cardPile = cardsPile.get(num-1);
        if (cardPile.tryObserved()) {
            return cardPile;
        }
        return null;
    }

    /**
     * Method used to put the card pile back.
     * @param cardPile is the card pile tile considered.
     * @return {@code true} if the release is successful, {@code false} otherwise.
     */
    public boolean putCardPileBack(CardPile cardPile) {
        if (!cardPile.tryRelease()) {
            return false;
        }
        return true;
    }

    /**
     * Method used before the game starts to count how many players are connected.
     * @return the number of connected players.
     */
    public int getConnectedPlayers() {
        return connectedPlayers;
    }

    /**
     * Method used to increment the number of connected players.
     */
    public void incrementConnectedPlayers() {
        this.connectedPlayers++;
    }

    /**
     * @return the nicknames of connected players.
     */
    public ArrayList<String> getNicknames() {
        return this.nicknames;
    }

    /**
     * @return the id's of connected players.
     */
    public ArrayList<String> getIds() {
        return this.ids;
    }

    /**
     * Method used to add the nickname of the player that has connected to the game.
     */
    public void addNickname(String nickname, String id) {
        this.nicknames.add(nickname);
        this.ids.add(id);
    }

    /**
     * @return the retired players list.
     */
    public ArrayList<Player> getRetiredPlayers() {
        return retiredPlayers;
    }

    /**
     * Method that adds a player to the player list.
     * @param player is the player that has to be added.
     */
    public void addRetiredPlayer(Player player) {
        this.retiredPlayers.add(player);
    }

    @Override
    public void update(Message message) {
        System.out.println(this.getClass().toString() + ": I have been notified!");
        notifyObserver(message);
    }




    /**
     * Used only for test
     * @param cardsPile
     */
    public void setCardsPileTest(ArrayList<CardPile> cardsPile) {
        this.cardsPile = cardsPile;
    }


    /**
     * Used only for test
     * @param gameTile
     */
    public void setGameTileTest(GameTile gameTile) {
        this.gameTile = gameTile;
    }
}

