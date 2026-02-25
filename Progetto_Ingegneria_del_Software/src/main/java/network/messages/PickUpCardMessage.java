package network.messages;

import controller.Controller;
import controller.GameEventListener;

/**
 * Message that informs the controller that the player wants to pick up a card.
 */
public class PickUpCardMessage extends Message {
    GameEventListener listener;

    /**
     * Default constructor.
     * @param clientId is the id of the client.
     */
    public PickUpCardMessage(String clientId) {
        super(clientId);
    }

    /**
     * @param listener is the game listener
     * Method that sets the listener.
     */
    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }


    @Override
    public void process(Controller controller) {
        controller.handlePickUpCardMessage(getClientId(), listener);
    }

}