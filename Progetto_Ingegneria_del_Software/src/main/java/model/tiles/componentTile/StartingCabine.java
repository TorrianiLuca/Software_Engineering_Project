package model.tiles.componentTile;

import enumerations.Color;
import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents the starting cabin cabin tile. It extends {@link ComponentTile}.
 */
public class StartingCabine extends ComponentTile {
    private int numFigures;
    private final Color color;
    private final int x;
    private final int y;
    private boolean connectedWithOccupiedCabine; // used for epidemic card

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     */
    public StartingCabine(TileName name, String url, int l1, int l2, int l3, int l4, Color color) {
        super(name, url, l1, l2, l3, l4);
        this.connectedWithOccupiedCabine = false;
        this.numFigures=0;
        this.x = 7;
        this.y = 7;
        this.color = color;
    }

    /**
     * @return the starting cabin color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the number of figure contained in this starting cabin.
     */
    public int getNumFigures() {
        return numFigures;
    }

    /**
     * @return if this cabin is connected with another occupied cabin.
     */
    public boolean isConnectedWithOccupiedCabine() {
        return connectedWithOccupiedCabine;
    }

    /**
     * Method that sets the occupied cabine value to true or false.
     * @param value is the value we want to set.
     */
    public void setConnectedWithOccupiedCabine(boolean value) {
        this.connectedWithOccupiedCabine = value;
    }

    /**
     * Method that decrements the number of figures of this starting cabin.
     */
    public void decrementNumFigures() {
        numFigures--;
    }

    /**
     * Method that sets the number of figures of this starting cabin.
     * @param numFigures is the value.
     */
    public void setNumFigures(int numFigures) {
        this.numFigures = numFigures;
    }
}