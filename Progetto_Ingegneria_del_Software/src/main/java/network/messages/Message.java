package network.messages;

import java.io.Serializable;

import controller.Controller;
import model.GameModel;
import network.structure.NetworkView;
import view.View;

import java.util.function.Consumer;

/**
 * Abstract class that manages the comunication between client and server.
 * This class is extended with all kind of messages used in the game.
 */

public abstract class Message implements Serializable {
    private final String clientId;
    // private final MessageType messageType;

    /**
     * Constructor for the class Message.
     * @param clientId is the ID of the sender of the message.
     // * @param messageType is the type of message sent from the sender.
     */
    public Message(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Getter method that returns the sender of the message.
     * @return the sender of the message.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Processes the message, updating the gameModel
     * @param controller is the controller.
     */
    public void process(Controller controller) {};

    /**
     * Processes the message, updating the gameModel
     * @param gameModel is the gameModel to interact with
     * @param networkView is the networkView passed by the controller
     */
    public void updateController(GameModel gameModel, NetworkView networkView) {};

    /**
     * Processes the message, updating the client
     * @param view is the view passed by the client controller
     */
    public void updateClient(View view) {};

}
