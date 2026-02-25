package network.messages;

import enumerations.FlightType;
import support.Couple;
import support.Quadruple;
import view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Message sent from the server to a client containing a list of all active games currently
 * hosted by the server. Each game entry includes information about the game's type, the number
 * of current players, the maximum number of players allowed, and the usernames of players
 * already in the lobby.
 *
 * This message is typically sent in response to a client requesting a list of joinable
 * games, and it is used to populate the client-side game selection menu.
 */
public class GameListMessage extends Message {

    /**
     * A map of game IDs to their corresponding game metadata. Each entry consists of:

     *   {@code FlightType} – the type of game (e.g., basic or advanced flight)
     *   {@code Integer} – the current number of players in the game
     *   {@code Integer} – the maximum number of players allowed
     *   {@code ArrayList<String>} – a list of player usernames already in the lobby
     */
    private HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games;

    /**
     * Constructs a new {@code GameListMessage}.
     *
     * @param clientId the ID of the client that will receive this message.
     * @param games a map of game IDs to their associated metadata as a Quadruple.
     */
    public GameListMessage(String clientId, HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games) {
        super(clientId);
        this.games = games;
    }

    /**
     * Returns the list of available games with their corresponding metadata.
     *
     * @return a map containing game IDs and their associated data.
     */
    public HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> getGames() {
        return games;
    }

    /**
     * Called on the client side to update the view with the list of available games.
     * This method invokes showAvailableGames(HashMap) to display the data to the user.
     *
     * @param view the client-side user interface used to show the available games.
     */
    @Override
    public void updateClient(View view) {
        view.showAvailableGames(games);
//        view.showGameChoiceMenu(); // Mostra il menu di scelta
    }

}
