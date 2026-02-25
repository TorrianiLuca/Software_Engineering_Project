package network.messages;

import model.GameModel;
import model.flightBoard.FlightBoard;
import model.player.Player;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;

/**
 * Message used to inform all the retired players that the turn is finished and a new card has to be drawn.
 */
public class NotLeader2Message extends Message {
    private int numCards;
    private FlightBoard flightBoard;
    private ArrayList<Player> players;

    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param numCards is the number of the card currently in use.
     * @param flightBoard is the flight board currently in use.
     * @param players is the array list of the player in the game
     */
    public NotLeader2Message(String clientId, int numCards, FlightBoard flightBoard, ArrayList<Player> players) {
        super(clientId);
        this.numCards = numCards;
        this.flightBoard = flightBoard;
        this.players = players;
    }
    /**
     * @return the id of the previously drawn card
     * */
    public int getNumCards() {
        return numCards;
    }

    /**
     * @return the flight board of the game
     * */
    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showNotLeader2(numCards, flightBoard, players);
    }

}
