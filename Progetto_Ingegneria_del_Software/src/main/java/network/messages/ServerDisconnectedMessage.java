package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

/**
 * Message used to inform that the server isn't working
 */

public class ServerDisconnectedMessage extends Message {
    String disconnectionError = "Server not reachable, terminating the game...";

    /**
     * Default constructor.
     */

    public ServerDisconnectedMessage() {
        super("Client");
    }

    /**
     * @return a disconnection error
     */

    public String getDisconnectionError() {
        return disconnectionError;
    }


    @Override
    public void updateClient(View view) {
        view.disconnected(disconnectionError);
    }
}