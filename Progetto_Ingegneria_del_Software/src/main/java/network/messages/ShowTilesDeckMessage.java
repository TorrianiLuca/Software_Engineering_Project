package network.messages;


import model.GameModel;
import model.tiles.GameTile;
import network.structure.NetworkView;
import view.View;

/**
 * Message that return the tiles deck.
 */
public class ShowTilesDeckMessage extends Message {
    private GameTile gameTile;

    /**
     * Constructor.
     * @param clientId is the id of the client.
     * @param gameTile is the {@link GameTile} containing all the tiles in the deck.
     */
    public ShowTilesDeckMessage(String clientId, GameTile gameTile) {
        super(clientId);
        this.gameTile = gameTile;
    }

    @Override
    public void updateController(GameModel gameModel, NetworkView networkView) {
        networkView.sendMessageToClient(getClientId(),this);
    }

    @Override
    public void updateClient(View view) {
        view.showTiles(gameTile);
    }

}