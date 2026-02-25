package model.tiles.componentTile;

import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.HashMap;

/**
 * Class that represents battery tile. It extends {@link ComponentTile}.
 */
public class Battery extends ComponentTile {
    private final int numMaxBatteries;
    private int numBatteriesInUse;

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     * @param numOfComponent is the number of batteries the tile contains.
     */
    public Battery(TileName name, String url, int l1, int l2, int l3, int l4, int numOfComponent) {
        super(name, url, l1, l2, l3, l4);
        this.numMaxBatteries = numOfComponent;
        this.numBatteriesInUse = numOfComponent;
    }

    /**
     * @return the maximum number of batteries the tile can contain.
     */
    public int getNumMaxBatteries() {
        return numMaxBatteries;
    }

    /**
     * @return the number of batteries remained in the tile.
     */
    public int getNumBatteriesInUse() {
        return numBatteriesInUse;
    }

    /**
     * Method that decreases the number of batteries contained in the tile.
     */
    public void decreaseNumBatteriesInUse() {
        numBatteriesInUse--;
    }
}