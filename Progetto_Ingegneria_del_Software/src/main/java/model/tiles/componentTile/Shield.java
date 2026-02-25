package model.tiles.componentTile;

import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class that represents the shield tile. It extends {@link ComponentTile}.
 */
public class Shield extends ComponentTile {
    private final ArrayList<String> shieldSides = new ArrayList<>(Arrays.asList("up", "right"));
    private boolean active;

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     */
    public Shield(TileName name, String url, int l1, int l2, int l3, int l4) {
        super(name, url, l1, l2, l3, l4);
    }

    /**
     * Getter method that returns if the shield is active or inactive.
     * @return {@code true} if the cannon is active, {@code false} otherwise.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Setter method that sets the cannon to active.
     */
    public void setActive(boolean value) {
        this.active = value;
    }
}
