package model.tiles.componentTile;

import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.HashMap;

/**
 * Class that represents the structural module tile. It extends {@link ComponentTile}.
 */
public class StructuralModule extends ComponentTile {

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     */
    public StructuralModule(TileName name, String url, int l1, int l2, int l3, int l4) {
        super(name, url, l1, l2, l3, l4);
    }
}
