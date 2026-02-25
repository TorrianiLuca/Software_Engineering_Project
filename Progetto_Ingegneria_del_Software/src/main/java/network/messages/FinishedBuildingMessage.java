package network.messages;

import controller.Controller;
import controller.GameEventListener;

/**
 * Message used to inform that the player has finished building his ship.
 */
public class FinishedBuildingMessage extends Message {
    GameEventListener listener;

    /**
     * Default constructor.
     * @param sender is the sender of the message
     */
    public FinishedBuildingMessage(String sender) {
        super(sender);
    }

    /**
     * Method that sets the listener.
     * @param listener is the listener that has to be set.
     */
    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void process(Controller controller) {
        controller.handleFinishedBuildingMessage(getClientId(), listener);
    }
}
