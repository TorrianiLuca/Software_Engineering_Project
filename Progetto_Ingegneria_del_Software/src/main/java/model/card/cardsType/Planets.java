/*
* PLANET'S CLASS
* This class extends the class Card
 *
 * Card description:
* A Planets card has 2 to 4 planets where
* you can pick up goods. Landing will cost
* you the number of flight days shown in
* the lower right corner. If you want to land,
* mark the planet you choose with your
* second rocket (the one not on the flight board). Only
* one rocket is allowed per planet.
* The leader chooses first, followed by the other players
* in order. No one is required to land. In fact, players in
* front sometimes prevent the others from landing at all.
* Those who landed load the indicated goods on their
* ships. Goods can be
* rearranged or discarded at this time. It is legal to land
* just to block others from landing, but be sure it is worth
* the loss of flight days.
* Once everyone has decided whether to land, those
* who landed move their markers back that many empty
* spaces, with the player who is farthest behind moving
* first.

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
import java.util.List;
import java.util.function.Consumer;

/**
 * This class represents the planets card. It extends Card.
 */
public class Planets extends Card {
    private int loseFlightDays;
    private ArrayList<ArrayList<Color>> planetsGoods;
    private int numOccupiedPlanets;
    private Player[] indexPlanets;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param loseFlightDays is the number of the flight days lost.
     * @param planetsGoods is the list of goods each planet contains.
     */
    public Planets(CardName cardType, int level, String url, int loseFlightDays, ArrayList<ArrayList<Color>> planetsGoods) {
        super(cardType, level, url);
        this.planetsGoods = planetsGoods;
        this.numOccupiedPlanets = 0;
        this.loseFlightDays = loseFlightDays;
        this.indexPlanets = new Player[planetsGoods.size()];
    }

    /**
     * @return the number of flight days lost
     */
    public int getLoseFlightDays() {
        return loseFlightDays;
    }

    /**
     * @return the number of planets that a card contains
     */
    public int getNumberOfPlanets()
    {
        return planetsGoods.size();
    }

    /**
     * @return the list of the goods of every planet
     */
    public ArrayList<ArrayList<Color>> getAllPlanetsGoods() {
        return planetsGoods;
    }

    /**
     * @param number represents the number of the planet where the player wants to land
     * @return the colors of the goods for that planet
     */
    public ArrayList<Color> choosePlanetGoods(int number) {
        return planetsGoods.get(number-1);
    }

    /**
     * Method that return the number of occupied planets.
     * @return the number of occupied planets.
     */
    public int getNumOccupiedPlanets() {
        return numOccupiedPlanets;
    }

    /**
     * Method that increments the number of occupied planets.
     */
    public void incrementNumOccupiedPlanets() {
        this.numOccupiedPlanets++;
    }

    /**
     * Method that is used to check if a planet is occupied by the players, when this card is drawn.
     * @param numPlanet is the number of planet the player wants to land on.
     * @return {@code false} if the planet is already occupied or if the number is over the range, {@code true} otherwise.
     */
    public boolean processPlanetChoice(Player player, int numPlanet) {
        if (numPlanet > (planetsGoods.size()) || numPlanet <= 0) {
            return false;
        }
        if (indexPlanets[numPlanet-1] != null) {
            return false;
        }
        for (int i = 0; i < planetsGoods.size(); i++) {
            if(indexPlanets[i] != null && indexPlanets[i].equals(player)) {
                return false;
            }
        }
        indexPlanets[numPlanet-1] = player;
        incrementNumOccupiedPlanets();
        return true;
    }

    /**
     * Method that check if there is a player on the planet.
     * @param numPlanet is the number of the planet
     * @return the color of the player, or null if it is free
     */
    public Color playerOnPlanet(int numPlanet) {
        Player player = indexPlanets[numPlanet-1];
        if (player == null) {
            return null;
        }
        return player.getColor();
    }

    /**
     * Method that returns the number of the planet in which the player has landed (if the player has landed).
     * @param nickname is the nickname of the player.
     * @return 0 if the player has not landed on any planet, the number of the planet otherwise.
     */
    public int numOfPlanet(String nickname) {
        for (int i = 0; i < planetsGoods.size(); i++) {
            if (indexPlanets[i] != null && indexPlanets[i].getNickname().equals(nickname)) {
                return i + 1;
            }
        }
        return 0;
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

        gameModel.setGameState(GameState.PLANETS);
    }
}
