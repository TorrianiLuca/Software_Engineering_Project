package network.messages;

import model.GameModel;
import model.player.Player;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;

/**
 * Message that informs the clients about the winner of the game.
 */
public class WinnerMessage extends Message {
    private ArrayList<Player> players;
    private boolean winner;

    /**
     * Constructor
     * @param clientId is the id of the client.
     * @param winner is a flag that indicates if the player has won or not.
     * @param players is the list of players.
     */
    public WinnerMessage(String clientId, boolean winner, ArrayList<Player> players) {
        super(clientId);
        this.players = players;
        this.winner = winner;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showWinner(players, winner);
    }

}
