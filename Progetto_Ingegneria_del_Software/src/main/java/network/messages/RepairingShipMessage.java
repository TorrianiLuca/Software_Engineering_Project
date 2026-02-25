package network.messages;

import controller.Controller;
import enumerations.*;
import exceptions.*;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.card.cardsType.MeteorSwarm;
import model.card.cardsType.Pirates;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import static enumerations.PlayerState.*;

/**
 * Message used to inform the model that the player has finished repairing his ship.
 */
public class RepairingShipMessage extends Message {
    Card card;

    /**
     * Default constructor.
     * @param clientId is the ID of the sender of the message.
     */
    public RepairingShipMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handleRepairingShipMessage(getClientId(), card);
    }
}
