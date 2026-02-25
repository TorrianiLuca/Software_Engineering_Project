package network.messages;

import model.GameModel;
import model.flightBoard.FlightBoard;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to announce the current round leader to a specific client.
 *
 * The leader is the player who has taken the fewest cards in the ongoing flight phase
 * and therefore gains the advantage to act first in the next step of the game turn.
 * Along with the number of cards the leader has taken, this message carries a snapshot
 * of the leader’s {@code FlightBoard}, allowing the receiving client to update its
 * user interface and show the leading ship layout.
 */
public class LeaderMessage extends Message {
    private int numCards;
    private FlightBoard flightBoard;

    /**
     * Constructs a new LeaderMessage.
     * @param clientId is the ID of the client that should receive this message
     * @param numCards is the number of cards remaining in the cards deck.
     * @param flightBoard the leader’s flight board to be displayed on the client side
     */
    public LeaderMessage(String clientId, int numCards, FlightBoard flightBoard) {
        super(clientId);
        this.numCards = numCards;
        this.flightBoard = flightBoard;
    }

    /**
     * Returns the number of cards the leader has drawn.
     * @return number of cards drawn by the leader
     */
    public int getNumCards() {
        return numCards;
    }

    /**
     * Returns a read-only snapshot of the leader’s flight board.
     *
     * @return the leader’s flight board
     */
    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    /**
     * Called on the server side: forwards this message to the intended client
     * via the provided network view.
     *
     * @param gameModel   the current server-side game model (unused here)
     * @param networkView the network view used to reach the client
     */
    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    /**
     * Called on the client side: updates the client’s view by displaying the leader
     * information (number of cards and ship layout).
     *
     * @param view the client-side view to be updated
     */
    @Override
    public void updateClient(View view) {
        view.showLeader(numCards, flightBoard);
    }

}
