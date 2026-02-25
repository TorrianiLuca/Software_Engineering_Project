package model.tiles.componentTile;

import enumerations.Color;
import enumerations.TileName;
import model.tiles.ComponentTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that represents the cargo tile. It extends {@link ComponentTile}.
 */
public class Cargo extends ComponentTile {
    private final Color color;
    private final int numMaxCargos;
    private int numOccupiedCargos;
    private ArrayList<Color> cargosIn;

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     * @param numOfComponent is the number of cargos (one, two or three).
     * @param color is the cargo color (red or blue).
     */
    public Cargo(TileName name, String url, int l1, int l2, int l3, int l4, int numOfComponent, String color) {
        super(name, url, l1, l2, l3, l4);
        this.numMaxCargos = numOfComponent;
        this.numOccupiedCargos = 0;
        this.cargosIn = new ArrayList<>();
        if(color.toLowerCase().equals("red")) {
            this.color = Color.RED;
        }
        else {
            this.color = Color.BLUE;
        }
    }

    /**
     * @return the cargo color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the maximum number of cargos.
     */
    public int getNumMaxCargos() {
        return numMaxCargos;
    }

    /**
     * @return the cargos occupied.
     */
    public int getNumOccupiedCargos() {
        return numOccupiedCargos;
    }

    /**
     * Method that increases the number of occupied cargos spot.
     */
    public void increaseNumOccupiedCargos() {
        numOccupiedCargos++;
    }

    /**
     * Method that decreases the number of occupied cargos spot.
     */
    public void decreaseNumOccupiedCargos() {
        numOccupiedCargos--;
    }

    /**
     * Method that insert a cargo in a spot.
     * @param goodsBlock is the cargo type we want to insert.
     * @return {@code true} if the cargo is inserted, {@code false} otherwise.
     */
    public boolean putCargoIn(Color goodsBlock) {
        if(numOccupiedCargos == numMaxCargos) {
            return false;
        }
        if (goodsBlock == Color.RED && this.color == Color.BLUE) {
            return false;
        }
        cargosIn.add(goodsBlock);
        this.increaseNumOccupiedCargos();
        return true;
    }

    /**
     * Method that removes the specified cargo.
     * @param goodsBlock is the color of the cargo that has to be removed.
     */
    public void removeCargo(Color goodsBlock) {
        Iterator<Color> iterator = cargosIn.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == goodsBlock) {
                iterator.remove();
                this.decreaseNumOccupiedCargos();
                break;
            }
        }
    }

    /**
     * Method that returns the color of the rarest goodsBlock in this tile
     * @return the color.
     */
    public Color rarestCargoIn() {
        if (cargosIn.size() == 0) {
            return null;
        }
        if (cargosIn.contains(Color.RED)) {
            return Color.RED;
        }
        if (cargosIn.contains(Color.YELLOW)) {
            return Color.YELLOW;
        }
        if (cargosIn.contains(Color.GREEN)) {
            return Color.GREEN;
        }
        return Color.BLUE;
    }

    /**
     * Method that returns the goods blocks contained in the tile
     * @return the list of goods blocks.
     */
    public ArrayList<Color> getCargosIn() {
        return cargosIn;
    }
}
