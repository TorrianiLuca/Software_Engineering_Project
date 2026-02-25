
package view;

import enumerations.Color;
import enumerations.FlightType;
import model.card.Card;
import model.card.CardPile;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import support.Couple;
import model.shipBoard.ShipBoard;
import network.messages.Message;
import support.Quadruple;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface implemented by both the TUI and GUI, as well as the NetworkView.
 * It defines all the user-facing interactions during the game
 */
public interface View {

    /**
     * Method used to clear the client terminal.
     */
    void clearPage();

    /**
     * Method used to ask a player to enter his nickname
     * @param clientId is the ID used to identify a specific client
     */
    void askNickname(String clientId);

    /**
     * Method used to ask a player to insert the maximum number of players and the flight type for the game.
     */
    void askMaxPlayerAndFlightType();


    /**
     * Method used to display all the different errors that can occur during the game
     * @param error is the description of the error
     */
    void showGenericError(String error);

    /**
     * Method used to inform the player about any mistakes made while building or repairing his ship
     * @param errors is the description of the mistake
     */
    void showShipErrors(ArrayList<String> errors);

    /**
     * Method used to show the shipboard of a player
     * @param shipBoard is the shipboard of the player
     */
    void showShipBoard(ShipBoard shipBoard);

    /**
     * Method used to show the tile's deck
     * @param gameTile is the set of the tile
     */
    void showTiles(GameTile gameTile);

    /**
     * Method used to show the shipboards of all the players in the game
     * @param shipBoards contains the nicknames of players associated with their shipboards
     */
    void showAllShipBoards(HashMap<String, ShipBoard> shipBoards);

    /**
     * This method shows the seconds remained until the end of the timer
     * @param time is the number of seconds remained
     */
    void showTimer(int time);

    /**
     * Method used to show the card picked to the players that are still in the flight
     * @param inTurn is a flag that indicates if a player is in turn or not
     */
     void showCard(boolean inTurn);

    /**
     * Method used to show the card picked to the retired players
     */
    void showCard2();

    /**
     * Method used to show to the players the instructions for rolling the dice
     * @param card is the card picked
     * @param inTurn indicates if a player is in turn
     * @param proceed indicates if a player is ready
     */
     void setRollDice(Card card, boolean inTurn, boolean proceed);

    /**
     * Method used to show to a player if a meteor has hit his ship or not
     * @param card is the card picked
     * @param isHit indicates if the player ship is hit
     * @param sum is the row or the colum where the meteor come from
     */
    void showMeteorHit(Card card, boolean isHit, int sum);

    /**
     * Method used to show to a player the instructions that he has to follow when he finishes to built or to repair his ship
     */
    void finishBuilding();

    /**
     * Method used when a player has to populate his ship
     */
    void proceed2();

    /**
     * Method used to show to the player the final starting positions
     * @param tempPositions all the players ordered by their positions
     */
    void showTempPositions(ArrayList<Player> tempPositions);

    /**
     * Method used to show the message to a specific client
     * @param clientId is the id of the client that has to receive the message
     * @param message is the message
     */
    void sendMessageToClient(String clientId, Message message);

    /**
     * Method used to show to the new players if there are games already active
     * @param games contains the information of every game
     */
    void showAvailableGames(HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games);

    /**
     * Method used to show the player a generic message.
     */
    void showGenericMessage(String message);

    /**
     * Method used to show the player the component he picked during building.
     * @param componentTile is the component picked.
     */
    void showComponentTile(ComponentTile componentTile);

    /**
     * Method used to show to a player the card pile he picked
     * @param cardPile is the card pile picked
     */
    void showCardPile(CardPile cardPile);

    /**
     * Method used to show to the leader the instruction for pick the next card
     * @param numCards indicates the number of cards remaining
     * @param flightBoard is the flight board of the game
     */
    void showLeader(int numCards, FlightBoard flightBoard);

    /**
     * Method used to show instructions to the players that are not the leader and that are still in the flight
     * @param numCards indicates the number of cards remaining
     * @param flightBoard is the flight board of the game
     */
    void showNotLeader(int numCards,FlightBoard flightBoard);

    /**
     * Method used to show instructions to the players that are not leader and that are not in the flight anymore
     * @param numCards indicates the number of cards remaining
     * @param flightBoard is the flight board of the game
     * @param players is the list of the players connected to the game
     */
    void showNotLeader2(int numCards,FlightBoard flightBoard, ArrayList<Player> players);

    /**
     * Method used to show instructions to the players that have to wait other players during the game
     * @param card is the card picked
     */
    void showWait(Card card);

    /**
     * Method used to show the good blocks that a player has gained from a card
     * @param card is the card picked
     * @param tempGoodsBlock is the array with the colors of the goods gained
     */
    void showGoods(Card card, ArrayList<Color> tempGoodsBlock);

    /**
     * Method used to show the instructions for pick the next card or for proceed with the next phase in case of Meteor Swarm or Combat Zone
     * @param card is the card picked
     * @param num indicates if there is a new phase to proceed
     * @param cardsToPlay are the cards remained
     */
    void proceedToNextCard(Card card, int num, ArrayList<Card> cardsToPlay);

    /**
     * Method that shows to a player that he has to wait other players that hasn't proceeded yet
     */
    void notProceed();

    /**
     * Method used to change tui state
     * @param num is a value used in the tui
     */
    void changeState(int num);

    /**
     * Method used to show the card in use
     * @param card is the card picked
     */
    void setCardInUse(Card card);

    /**
     * Method used to show instructions for a player that has to remove batteries
     */
    void setRemoveBattery();

    /**
     * Method used to show instructions for a player that has to remove goods
     */
    void setRemoveGood();

    /**
     * Method used to show instructions for a player that has to remove figures
     */
    void setRemoveFigure();

    /**
     * Method used to show the updated parameters of the player after the completion of a card or a phase
     * @param flightBoard is the flight board of the game
     */
    void updatePlayerParametres(FlightBoard flightBoard);

    /**
     * Method used to show the final screen that indicates if a player is a winner or not
     * @param players are the players in the game
     * @param winner indicates if a player wins or not
     */
    void showWinner(ArrayList<Player> players, boolean winner);

    /**
     * Method used to show an error if a player or the server disconnects
     * @param disconnectionError is the description the error
     */
    void disconnected(String disconnectionError);

    /**
     * Method used to add a message in the message queue
     * @param message is the message received
     */
    void addServerMessage(Message message);



}
