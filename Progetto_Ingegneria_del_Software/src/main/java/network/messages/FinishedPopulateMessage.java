package network.messages;

import controller.Controller;
import controller.GameEventListener;

/**
 * Message used to inform that the player has finished populating his ship.
 */
public class FinishedPopulateMessage extends Message {
    GameEventListener listener;

    /**
     * Default constructor.
     * @param sender is the sender of the message
     */
    public FinishedPopulateMessage(String sender) {
        super(sender);
    }

    /**
     * set a new listener
     * @param listener
     */
    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(Controller controller) {
        controller.handleFinishedPopulateMessage(getClientId(), listener);
    }
}