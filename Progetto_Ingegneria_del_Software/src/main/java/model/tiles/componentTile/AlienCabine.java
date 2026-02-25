package model.tiles.componentTile;

import enumerations.TileName;
import model.tiles.ComponentTile;
import enumerations.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents the alien cabin tile. It extends {@link ComponentTile}.
 */
public class AlienCabine extends ComponentTile {
    private final Color color;

    /**
     * Constructor.
     * @param name is the {@link TileName} of the tile.
     * @param url is the url of the image associated to the tile.
     * @param l1 is the side 1 connector.
     * @param l2 is the side 2 connector.
     * @param l3 is the side 3 connector.
     * @param l4 is the side 4 connector.
     * @param color is the color of the alien cabin.
     */
    public AlienCabine(TileName name, String url, int l1, int l2, int l3, int l4, String color) {
        super(name, url, l1, l2, l3, l4);

        if (color.toLowerCase().equals("yellow")) {
            this.color = Color.YELLOW;
        }
        else {
            this.color = Color.PURPLE;
        }
    }

    /**
     * @return the color of the alien cabin.
     */
    public Color getColor() {
        return color;
    }
}
