package observer;

import enumerations.FlightType;
import model.card.Card;
import model.tiles.ComponentTile;

import java.util.HashMap;

/**
 * Observer interface used for the view.
 */
public interface ViewObserver {
    /**
     * This method will send the necessary info to open a socket with the server.
     * @param serverInfo is a HashMap that has the address as key and the IP address as value.
     */
    void onUpdateServerInfo(HashMap<String, String> serverInfo);

    /**
     * This method will send the nickname on the network.
     * @param nickname is the nickname passed by the view.
     */
    void onUpdateNickname(String nickname);

    /**
     * This method will send the maximum number of players for the game.
     * @param maxPlayer is the number of players chosen for the game.
     * @param flightType is the flight type chosen for the game.
     */
    void onUpdateMaxPlayerAndFlightType(int maxPlayer, FlightType flightType);

    /**
     * This method will send the tile picked by the player.
     * @param tileId is the ID of the tile picked.
     */
    void onUpdatePickTile(int tileId);

    /**
     * This method will send the coordinates of the ship form which the player wants to pick the tile.
     * @param numRow is the row number.
     * @param numCol is the colum number.
     */
    void onUpdatePickTileFromShip(int numRow, int numCol);

    /**
     * This method will send a request to show the ships of the other players.
     */
    void onUpdateShowShips();

    /**
     * This method will send a message of finished building.
     */
    void onUpdateFinishedBuild();

    /**
     * This method will send a message of finished populate ship
     */
    void onUpdateFinishedPopulate();

    /**
     * This method will send the coordinates of the ship form which the player wants to remove the tile.
     * @param numRow is the row number.
     * @param numCol is the colum number.
     */
    void onUpdateRemoveTile(int numRow, int numCol);

    /**
     * This method will send a request to start the timer.
     */
    void onUpdateTimerMessage();

    /**
     * This method will send the coordinates of the ship in which the player has decided to put the component.
     * @param row is the number of row
     * @param col is the number of col
     */
    void onUpdatePutTileInShip(int row, int col);

    /**
     * This method will send the coordinates of the ship in which the player has decided to put the astronauts.
     * @param row is the number of row
     * @param col is the number of col
     */
    void onUpdatePutAstronautInShip(int row, int col);

    /**
     * This method will send the coordinates of the ship in which the player has decided to put the purple alien.
     * @param row is the number of row
     * @param col is the number of col
     */
    void onUpdatePutPurpleInShip(int row, int col);

    /**
     * This method will send the coordinates of the ship in which the player has decided to put the brown alien.
     * @param row is the number of row
     * @param col is the number of col
     */
    void onUpdatePutBrownInShip(int row, int col);

    /**
     * This method will send the number of the card pile the player wants to pick.
     * @param num is the number of the card pile.
     */
    void onUpdatePickCardPile(int num);

    /**
     * This method will send a request to pick the next card.
     */
    void onUpdatePickCard();

    /**
     * This method will send all the parameters to proceed in the open space card.
     * @param card is the card in use.
     * @param choice is the choice made by the player (land/skip).
     * @param numPlanet is the number of planet chosen by the player.
     */
    void onUpdatePlanetChoice(Card card, String choice, int numPlanet);

    /**
     * This method will send all the parameters to proceed in the smugglers card.
     * @param card is the card in use.
     */
    void onUpdateSmugglersChoice(Card card);

    /**
     * This method will send all the parameters to proceed in the slavers card.
     * @param card is the card in use.
     */
    void onUpdateSlaversChoice(Card card);

    /**
     * This method will send all the parameters to proceed in the pirates card.
     * @param card is the card in use.
     */
    void onUpdatePiratesChoice(Card card);

    /**
     * This method will send all the parameters to proceed in the abandoned station card.
     * @param card is the card in use.
     * @param choice is the choice made by the player (dock/skip)
     */
    void onUpdateAbandonedStationChoice(Card card, String choice);

    /**
     * This method will send all the parameters to proceed in the abandoned ship card.
     * @param card is the card in use.
     * @param choice is the choice made by the player (dock/skip)
     */
    void onUpdateAbandonedShipChoice(Card card, String choice);

    /**
     * This method will send all the parameters to proceed in the open space card.
     * @param card is the card in use.
     */
    void onUpdateOpenSpaceChoice(Card card);

    /**
     * This method will send all the parameters to proceed in the meteor swarm card.
     * @param card is the card in use.
     * @param sum indicated the number of row/column the meteor will hit.
     */
    void onUpdateMeteorSwarmChoice(Card card, int sum);

    /**
     * This method will send all the parameters to proceed in the final phase of the combat zone card.
     * @param card is the card in use.
     * @param sum indicated the number of row/column the meteor will hit.
     */
    void onUpdateCombatZoneChoice(Card card, int sum);

    /**
     * This method will send all the parameters to proceed in the pirates card.
     * @param card is the card in use.
     * @param sum indicated the number of row/column the cannon fire will hit.
     */
    void onUpdateDefeatedPiratesChoice(Card card, int sum);

    /**
     * This method will send all the parameters to proceed in the phase 1 of the combat zone card.
     * @param card is the card in use.
     */
    void onUpdatePhase1Choice(Card card);

    /**
     * This method will send all the parameters to proceed in the phase 2 of the combat zone card.
     * @param card is the card in use.
     */
    void onUpdatePhase2Choice(Card card);

    /**
     * This method will send all the parameters to proceed in the phase 3 of the combat zone card.
     * @param card is the card in use.
     */
    void onUpdatePhase3Choice(Card card);

    /**
     * This method will send the coordinates to activate a cannon in the ship.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateActivateCannon(Card card, int numRow, int numCol);

    /**
     * This method will send the coordinates to activate an engine in the ship.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateActivateEngine(Card card, int numRow, int numCol);

    /**
     * This method will send the coordinates to activate a shield in the ship.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateActivateShield(Card card, int numRow, int numCol);

    /**
     * This method will send the coordinates of the tile where the player wants to put the good gained.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateGainGood(Card card, String choice, int numRow, int numCol);

    /**
     * This method will send a request to proceed.
     */
    void onUpdateProceed();

    /**
     * This method will send a request to retire.
     */
    void onUpdateRetire();

    /**
     * This method will send a request to proceed in the next phase of the combat zone card.
     * @param card is the card in use.
     */
    void onUpdateNextPhase(Card card);

    /**
     * This method will send the coordinates to remove a battery from the ship.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateRemoveBattery(Card card, int numRow, int numCol, int sum);

    /**
     * This method will send the coordinates to remove a good from the ship.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateRemoveGood(Card card, int numRow, int numCol);

    /**
     * This method will send the coordinates to remove a figure from the ship.
     * @param card is the card in use.
     * @param numRow is the number of row.
     * @param numCol is the number of column.
     */
    void onUpdateRemoveFigure(Card card, int numRow, int numCol);

    /**
     * This method will send a request to roll the dice.
     * @param card is the card in use.
     */
    void onUpdateRollDice(Card card);

    /**
     * This method is used to notify the controller that the player has finished repairing the ship.
     * @param card is the card in use.
     */
    void onUpdateRepaired(Card card);


    /**
     * This method will send the choice made by the player to put the tile he has in hand back in the deck.
     */
    void onUpdatePutTileInDeck();

    /**
     * Method used to set the tile direction while assembling the ship.
     * @param direction is the direction set.
     */
    void onUpdateSetDirection(String direction);

    /**
     * Method used to stop watching the ship board of other players.
     */
    void onUpdateStopWatchingShips(Card card);

    /**
     * Method used to stop watching the card pile the player has in hand.
     */
    void onUpdateStopWatchingCardPile();

    /**
     * This method will send the flight type chosen for the game.
     * @param flightType is the flight type chosen for the game.
     */
    void onUpdateFlightType(FlightType flightType);

    /**
     * This method will send to the controller the input to create a new game.
     */
    void createGame();

    /**
     * This method will send to the controller the input to join a game.
     */
    void joinGame(String gameId);

    /**
     * This method will send a request to refresh all the active games.
     */
    void refreshGameOnServer();
}
