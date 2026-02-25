package network.messages;

import model.GameModel;
import model.player.Player;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;

/**
 * Message that contain the sorted list of the player's time used to build the ship
 */

public class TempPositionMessage extends Message {
    private ArrayList<Player> tempPositions;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param tempPositions time for each player.
     */

    public TempPositionMessage(String clientId, ArrayList<Player> tempPositions) {
        super(clientId);
        this.tempPositions = tempPositions;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showTempPositions(tempPositions);
    }
}
