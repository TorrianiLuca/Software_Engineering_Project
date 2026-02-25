package model.shipBoard;

import enumerations.TypeSpace;
import model.tiles.ComponentTile;

import java.io.Serializable;

/**
 * Class that represents a single space (cell) on the ship board.
 */
public class ShipBoardSpace implements Serializable {
    private final TypeSpace typeSpace;
    private ComponentTile componentContained;
    private int check;

    /**
     * Constructor.
     * @param typeSpace is the space type (usable, unusable, reserve).
     */
    public ShipBoardSpace(TypeSpace typeSpace) {
        this.typeSpace = typeSpace;
        this.componentContained = null;
        this.check=0;
    }

    /**
     * @return the {@link TypeSpace} of the cell
     */
    public TypeSpace getTypeSpace() {
        return typeSpace;
    }

    /**
     * @return the component contained (if there is one), or null.
     */
    public ComponentTile getComponent() {
        return componentContained;
    }

    /**
     * Method that removes a component by setting the component contained to null.
     */
    public void removeComponent() {
        this.componentContained = null;
    }

    /**
     * @return if the tile contained in that space is in a part of the ship.
     */
    public int getCheck() {
        return check;
    }

    /**
     * Method that sets the check flag to 1 if the tile contained in that space is a part of the ship.
     * It is also set back to 0 when the player's shipboard is composed of more parts not connected to each other, and he has to correct it.
     * @param check is the value that has to be set.
     */
    public void setCheck(int check) {
        this.check = check;
    }

    /**
     * Setter method that sets the component contained in this space.
     * @param componentContained is the component that has to be set.
     */
    public void insertComponent(ComponentTile componentContained) {
        this.componentContained = componentContained;
    }
}