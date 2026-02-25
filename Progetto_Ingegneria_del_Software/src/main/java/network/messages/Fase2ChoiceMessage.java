package network.messages;

import controller.Controller;
import enumerations.GameState;
import enumerations.PlayerState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.CombatZone;
import model.player.Player;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Message that contains when the player wants to proceed in the first phase of the combat zone.
 */
public class Fase2ChoiceMessage extends Message {
    private Card card;

    /**
     * Default constructor. The sender of the message is the player.
     * @param card is the card currently in use.
     */
    public Fase2ChoiceMessage(String clientId, Card card) {
        super(clientId);
        this.card = card;
    }

    @Override
    public void process(Controller controller) {
        controller.handleFase2ChoiceMessage(getClientId(), card);
    }
}