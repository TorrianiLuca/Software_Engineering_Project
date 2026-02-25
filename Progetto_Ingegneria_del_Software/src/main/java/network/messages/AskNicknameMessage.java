package network.messages;

import model.GameModel;
import model.card.Card;
import network.structure.Client;
import network.structure.NetworkView;
import view.View;

import java.util.ArrayList;

/**
 * Message used to ask the player for the username for the game.
 */
public class AskNicknameMessage extends Message {
    /**
     * Default constructor.
     */
    public AskNicknameMessage(String clientId) {
        super(clientId);
    }

    public String getClientId() {
        return super.getClientId();
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.askNickname(getClientId());
    }

}