package model.tiles.componentTile;

import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.HashMap;

/**
 * Class that represents the engine tile. It extends {@link ComponentTile}.
 */
public class Engine extends ComponentTile {
    private final boolean isDouble;
    private boolean active;
    private final String engineSide = "down";

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     * @param numOfComponent is the number of engines (one or two).
     */
    public Engine(TileName name, String url, int l1, int l2, int l3, int l4, int numOfComponent) {
        super(name, url, l1, l2, l3, l4);
        if (numOfComponent == 1) {
            isDouble = false;
            active = true;
        }
        else {
            isDouble = true;
            active = false;
        }
    }

    /**
     * @return {@code true} if the engine is double, {@code false} otherwise.
     */
    public boolean isDouble() {
        return isDouble;
    }

    /**
     * Getter method that returns if the cannon is active or inactive.
     * @return {@code true} if the cannon is active, {@code false} otherwise.
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Setter method that sets the engine to active.
     */
    public void setActive(boolean value) {
        this.active = value;
    }

}
