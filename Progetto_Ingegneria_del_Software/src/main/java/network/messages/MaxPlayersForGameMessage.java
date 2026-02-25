package network.messages;

import controller.Controller;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
import network.structure.NetworkView;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message that contains the maximum number of player for the game.
 */
public class MaxPlayersForGameMessage extends Message {
    private final int maxPlayers;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param maxPlayers is the number of players for the game.
     */
    public MaxPlayersForGameMessage(String clientId, int maxPlayers) {
        super(clientId);
        this.maxPlayers = maxPlayers;
    }

    /**
     * Getter method that returns the number of player for the game.
     * @return the number of player set for the game.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public void process(Controller controller) {
        controller.handleMaxPlayersForGameMessage(getClientId(), maxPlayers);
    }
}
