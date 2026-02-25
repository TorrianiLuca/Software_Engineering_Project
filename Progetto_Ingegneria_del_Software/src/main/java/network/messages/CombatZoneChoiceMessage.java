package network.messages;

import controller.Controller;
import enumerations.GameState;
import exceptions.MultipleValidationErrorsException;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.player.Player;
import model.shipBoard.ShipBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import static enumerations.PlayerState.REPAIR;

/**
 * Message that contains when the player wants to proceed for the final phase of the combat zone.
 */
public class CombatZoneChoiceMessage extends Message {
    private Card card;
    private int sum;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     */
    public CombatZoneChoiceMessage(String clientId, Card card, int sum) {
        super(clientId);
        this.card = card;
        this.sum = sum;
    }

    @Override
    public void process(Controller controller) {
        controller.handleCombatZoneChoiceMessage(getClientId(), card, sum);
    }
}