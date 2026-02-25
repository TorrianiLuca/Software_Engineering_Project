package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to inform all the clients that a player has disconnected.
 */
public class ClientDisconnectedMessage extends Message {
    String disconnectionError = "A player has disconnected, terminating the game...";

    /**
     * Default constructor.
     * Sent a message for disconnection
     */
    public ClientDisconnectedMessage() {
        super("Client");
    }

    /**
     *
     * @return a string message for disconnection
     */
    public String getDisconnectionError() {
        return disconnectionError;
    }


    @Override
    public void updateClient(View view) {
        view.disconnected(disconnectionError);
    }
}