package network.messages;

import model.GameModel;
import model.tiles.ComponentTile;
import network.structure.NetworkView;
import view.View;

import java.util.function.Consumer;

/**
 * Message that contains the component picked by the player.
 */
public class PickedTileMessage extends Message {
    private final ComponentTile componentTile;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     * @param componentTile is the component picked.
     */
    public PickedTileMessage(String clientId, ComponentTile componentTile) {
        super(clientId);
        this.componentTile = componentTile;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(), this);
    }

    @Override
    public void updateClient(View view) {
        view.showComponentTile(componentTile);
    }


}
