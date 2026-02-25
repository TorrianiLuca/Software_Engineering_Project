package network.messages;

import controller.Controller;
import controller.GameEventListener;

/**
 * Message that informs the player that he has to wait the other players to proceed with the next card.
 */
public class WaitProceedMessage extends Message {
    GameEventListener listener;

    /**
     * Constructor.
     * @param clientId is the client id.
     */
    public WaitProceedMessage(String clientId) {
        super(clientId);
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
        controller.handleWaitProceedMessage(getClientId(), listener);
    }
}