package network.messages;

import enumerations.Color;
import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;

/**
 * Message sent from the server to a specific client to notify the player that they have gained goods
 * as a result of resolving a specific card. This message includes the card that generated the goods
 * and the temporary storage block containing the gained goods, identified by their color.
 *
 * This message is typically generated during the resolution phase of a round, when the game logic
 * determines that a player should receive goods from a card effect.
 */
public class GainedGoodsMessage extends Message {
    private Card card;
    private ArrayList<Color> tempGoodsBlock;

    /**
     * Constructs a new {@code GainedGoodsMessage}.
     * @param clientId the ID of the client who is receiving the message.
     * @param card the card that caused the player to gain goods.
     * @param tempGoodsBlock a list of goods gained by the player, represented as a list of {@link Color}.
     */
    public GainedGoodsMessage(String clientId, Card card, ArrayList<Color> tempGoodsBlock) {
        super(clientId);
        this.card = card;
        this.tempGoodsBlock = tempGoodsBlock;
    }

    /**
     * Called on the server side to handle the logic for this message.
     * In this case, it sends the message directly back to the corresponding client
     * using the provided {@link NetworkView}.
     *
     * @param gameModel the current game state (not used in this implementation).
     * @param networkView the network abstraction used to send the message to the client.
     */
    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    /**
     * Called on the client side to update the client view.
     * It triggers the display of the goods gained, using the provided {@link View}.
     *
     * @param view the client's user interface abstraction, used to show the gained goods.
     */
    @Override
    public void updateClient(View view) {
        view.showGoods(card, tempGoodsBlock);
    }

}
