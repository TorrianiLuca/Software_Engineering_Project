package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message used for an update from the model.
 */
public class GenericMessage2 extends Message {
    String message;

    /**
     * Default constructor. The sender of the message is the model.
     */
    public GenericMessage2(String clientId, String message) {
        super(clientId);
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showGenericMessage(message);
    }

}