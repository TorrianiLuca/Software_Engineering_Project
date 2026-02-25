package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

import java.util.function.Consumer;

/**
 * Message that contains an error message.
 */
public class GenericErrorMessage extends Message {
    private final String errorMessage;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param errorMessage is the string containing the error message.
     */
    public GenericErrorMessage(String clientId, String errorMessage) {
        super(clientId);
        this.errorMessage = errorMessage;
    }

    /**
     * Getter method that returns the string containing the error.
     * @return the error contained in the error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showGenericError(errorMessage);
    }
}
