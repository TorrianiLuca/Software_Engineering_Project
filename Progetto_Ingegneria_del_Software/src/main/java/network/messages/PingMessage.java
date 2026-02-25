package network.messages;

import model.card.Card;

import java.util.ArrayList;

/**
 * Message used to ping the server and verify if the player is still connected.
 */
public class PingMessage extends Message {

    /**
     * Default constructor.
     * @param clientId is the id of the client.
     */
    public PingMessage(String clientId) {
        super(clientId);
    }

}