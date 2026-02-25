package model.tiles.componentTile;

import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents the cabin tile. It extends {@link ComponentTile}.
 */
public class Cabine extends ComponentTile {
    private int numFigures;
    private boolean connectedWithNormalCabine;
    private boolean connectedWithAlienCabine;
    private boolean connectedWithOccupiedCabine; // used for epidemic card
    private ArrayList<AlienCabine> alienCabineConnected =  new ArrayList<>();
    private boolean hasPurpleAlien;
    private boolean hasBrownAlien;

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     */
    public Cabine(TileName name, String url, int l1, int l2, int l3, int l4) {
        super(name, url, l1, l2, l3, l4);
        this.connectedWithOccupiedCabine = false;
        this.numFigures = 0;
    }

    /**
     * @return the number of figures contained in the cabin.
     */
    public int getNumFigures() {
        return numFigures;
    }

    /**
     * Method that decrements the number of figures contained in the cabin.
     */
    public void decrementNumFigures() {
        numFigures--;
    }

    /**
     * Method that sets the number of figures contained in the cabin.
     * @param numFigures is the value.
     */
    public void setNumFigures(int numFigures) {
        this.numFigures = numFigures;
    }

    /**
     * @return if the cabin is connected to an alien cabin.
     */
    public boolean isConnectedWithAlienCabine() {
        return connectedWithAlienCabine;
    }

    /**
     * Setter method that sets the flag when the cabin is connected to an alien cabin.
     */
    public void setConnectedWithAlienCabine() {
        this.connectedWithAlienCabine = true;
    }

    /**
     * Method that unsets the flag when the cabin is not connected to an alien cabin.
     */
    public void unsetConnectedWithAlienCabine() {
        this.connectedWithAlienCabine = false;
    }

    /**
     * @return the list of {@link AlienCabine} connected to this cabin.
     */
    public ArrayList<AlienCabine> getAlienCabineConnected() {
        return alienCabineConnected;
    }

    /**
     * Method that adds the {@link AlienCabine} to the cabin.
     * @param alienCabine is the alien cabin in consideration.
     */
    public void setAlienCabineConnected(AlienCabine alienCabine) {
        this.alienCabineConnected.add(alienCabine);
    }

    /**
     * @return {@code true} if the cabin contains a purple alien, {@code false} otherwise.
     */
    public boolean getHasPurpleAlien() {
        return hasPurpleAlien;
    }

    /**
     * @return {@code true} if the cabin contains a brown alien, {@code false} otherwise.
     */
    public boolean getHasBrownAlien() {
        return hasBrownAlien;
    }

    /**
     * Method that clears the alien cabin connected list.
     */
    public void clearListAlien()
    {
        alienCabineConnected.clear();
    }

    /**
     * Method that sets the flag to true if the player has a purple alien or false otherwise.
     * @param hasPurpleAlien is the value.
     */
    public void setHasPurpleAlien(boolean hasPurpleAlien) {
        this.hasPurpleAlien = hasPurpleAlien;
    }

    /**
     * Method that sets the flag to true if the player has a brown alien or false otherwise.
     * @param hasBrownAlien is the value.
     */
    public void setHasBrownAlien(boolean hasBrownAlien) {
        this.hasBrownAlien = hasBrownAlien;
    }

    /**
     * @return if this cabin is connected with another occupied cabine
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

}