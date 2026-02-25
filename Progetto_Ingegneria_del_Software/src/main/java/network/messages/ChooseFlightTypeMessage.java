package network.messages;

import controller.Controller;
import enumerations.FlightType;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
import network.structure.NetworkView;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message that contains the flight type chosen for the game.
 */
public class ChooseFlightTypeMessage extends Message {
    private final FlightType flightType;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     * @param flightType is the type of flight chosen for the game.
     */
    public ChooseFlightTypeMessage (String clientId, FlightType flightType) {
        super(clientId);
        this.flightType = flightType;
    }

    /**
     * Getter method that returns the flight type chosen by the player for the game.
     * @return the flight type set by the player for the game.
     */
    public FlightType getFlightType() {
        return flightType;
    }

    @Override
    public void process(Controller controller) {
        controller.handleChooseFlightTypeMessage(getClientId(), flightType);
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), new GenericMessage("Added to the game. Waiting for other players to start the game..."));
    }
}