package network.messages;

import model.GameModel;
import model.card.Card;
import view.View;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message used for an update from the model.
 */
public class GenericMessage extends Message {
    String message;

    /**
     * Default constructor. The sender of the message is the model.
     */
    public GenericMessage(String message) {
        super("Model");
        this.message = message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    @Override
    public void updateClient(View view) {
        view.showGenericMessage(this.message);
    }

}