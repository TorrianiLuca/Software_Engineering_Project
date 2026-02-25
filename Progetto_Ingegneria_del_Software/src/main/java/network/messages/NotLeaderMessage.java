package network.messages;

import model.GameModel;
import model.flightBoard.FlightBoard;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to inform all the players (except the leader) that the turn is finished and a new card has to be drawn.
 */
public class NotLeaderMessage extends Message {
    private int numCards;
    private FlightBoard flightBoard;

    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param numCards is the number of the card currently in use.
     * @param flightBoard is the flight board currently in use.
     */
    public NotLeaderMessage(String clientId, int numCards, FlightBoard flightBoard) {
        super(clientId);
        this.numCards = numCards;
        this.flightBoard = flightBoard;
    }

    public int getNumCards() {
        return numCards;
    }

    public FlightBoard getFlightBoard() {
        return flightBoard;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showNotLeader(numCards, flightBoard);
    }

}
