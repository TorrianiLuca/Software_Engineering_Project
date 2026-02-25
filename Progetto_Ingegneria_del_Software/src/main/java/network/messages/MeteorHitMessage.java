package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Message used for an update from the model to the single player, if the meteor has it his shipboard.
 */
public class MeteorHitMessage extends Message {
    private Card card;
    private boolean isHit;
    private int sum;

    /**
     * Default constructor.
     * @param clientId it the id of the player.
     * @param card is the card in use.
     * @param isHit is a flag that indicates if the ship board is hot by the meteor.
     * @param sum is the row/col the meteor will hit.
     */
    public MeteorHitMessage(String clientId, Card card, boolean isHit, int sum) {
        super(clientId);
        this.card = card;
        this.isHit = isHit;
        this.sum = sum;
    }

    /**
     *
     * @param gameModel is the gameModel to interact with
     * @param networkView is the networkView passed by the controller
     *
     */

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    /**
     *
     * @param view is the view passed by the client controller
     */
    @Override
    public void updateClient(View view) {
        view.showMeteorHit(card, isHit, sum);
    }
}