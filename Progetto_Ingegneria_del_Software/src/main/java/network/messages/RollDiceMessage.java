package network.messages;

import controller.Controller;
import enumerations.CardName;
import enumerations.FlightType;
import enumerations.GameState;
import exceptions.MultipleValidationErrorsException;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.card.cardsType.MeteorSwarm;
import model.card.cardsType.Pirates;
import model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static enumerations.PlayerState.*;

/**
 * Message that contains the decision of the player to roll the dice.
 */
public class RollDiceMessage extends Message {
    Card card;

    /**
     * Default constructor.
     * @param clientId is the sender of the message.
     */
    public RollDiceMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handleRollDiceMessage(getClientId(), card);
    }
}
