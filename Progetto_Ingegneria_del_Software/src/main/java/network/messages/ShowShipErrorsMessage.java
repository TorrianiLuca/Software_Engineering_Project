package network.messages;

import model.GameModel;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message that contains all the errors related to the player's shipboard.
 */
public class ShowShipErrorsMessage extends Message {
    private final ArrayList<String> errors;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param errors is the list containing all the errors.
     */
    public ShowShipErrorsMessage(String clientId, ArrayList<String> errors) {
        super(clientId);
        this.errors = errors;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showShipErrors(errors);
    }
}
