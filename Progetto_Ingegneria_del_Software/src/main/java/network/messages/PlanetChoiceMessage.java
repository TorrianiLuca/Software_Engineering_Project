package network.messages;

import controller.Controller;
import enumerations.Color;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.Planets;
import model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Message that contains if the players wants to land on a planet and which planet the player wants to land on.
 */
public class PlanetChoiceMessage extends Message {
    private Card card;
    private String choice;
    private int numPlanet;

    /**
     * Default constructor.
     * @param clientId is the id of the client.
     * @param card is the card currently in use.
     * @param choice is the choice the player has made (land, skip)
     * @param numPlanet is the number of planet the player wants to land on.
     */
    public PlanetChoiceMessage(String clientId, Card card, String choice, int numPlanet) {
        super(clientId);
        this.card = card;
        this.choice = choice;
        this.numPlanet = numPlanet;
    }

    /**
     * Getter method that returns the choice the player has made.
     * @return the choice mad by the player.
     */
    public String getChoice() {
        return choice;
    }

    /**
     * Getter method that returns the number of the planet the player wants to land on.
     * @return the number of the planet selected by the player.
     */
    public int getNumPlanet() {
        return numPlanet;
    }

    @Override
    public void process(Controller controller) {
        controller.handlePlanetChoiceMessage(getClientId(), card, choice, numPlanet);
    }
}