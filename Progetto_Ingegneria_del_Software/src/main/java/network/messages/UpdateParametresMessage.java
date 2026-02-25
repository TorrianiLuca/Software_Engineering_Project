package network.messages;

import model.GameModel;
import model.flightBoard.FlightBoard;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to update parameters on the flight board
 */

public class UpdateParametresMessage extends Message {
    private FlightBoard flightBoard;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param flightBoard flightBoard of the game.
     */

    public UpdateParametresMessage(String clientId, FlightBoard flightBoard) {
        super(clientId);
        this.flightBoard = flightBoard;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.updatePlayerParametres(flightBoard);
    }
}
