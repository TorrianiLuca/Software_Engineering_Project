package model.tiles;

import enumerations.TileName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class that represents the single component tile.
 * It has associated his tile image.
 */
public abstract class ComponentTile implements Serializable {
    private int id;
    private final TileName name;
    private final String url;
    private final HashMap<String, Integer> connectorsOnSide; // up, right, down, left

    private boolean faceDown = true;
    //private boolean faceDown = false;
    private String direction = "nord"; // nord, est, sud, ovest
    private AtomicBoolean isOccupied;

    /**
     * Default constructor.
     * @param name is the name of the component.
     * @param url is the url image associated.
     * @param l1 is the connector on the first side of the component (up).
     * @param l2 is the connector on the second side of the component (right).
     * @param l3 is the connector on the third side of the component (down).
     * @param l4 is the connector on the fourth side of the component (left).
     */
    public ComponentTile(TileName name, String url, int l1, int l2, int l3, int l4) {
        this.id = 0;
        this.name = name;
        this.url = url;
        this.connectorsOnSide = new HashMap<>();
        connectorsOnSide.put("up", l1);
        connectorsOnSide.put("right", l2);
        connectorsOnSide.put("down", l3);
        connectorsOnSide.put("left", l4);
        this.isOccupied = new AtomicBoolean(false);
    }

    /**
     * Getter method that returns the id of the component tile.
     * @return the id of the component.
     */
    public int getId() {
        return id;
    }

    /**
     * @return the url associated to the tile.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method that sets the id of the component tile.
     * @param id is the id given to the component.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method that returns the name of the component tile.
     * @return the name of the component.
     */
    public TileName getName() {
        return name;
    }

    /**
     * Getter method that returns the side of the component and the connector associated.
     * @return the hashMap containing the side and the type of connector.
     */
    public HashMap<String, Integer> getConnectorsOnSide() {
        return connectorsOnSide;
    }

    /**
     * Getter method that return the connector given the side.
     * @param side is the side of the tile in consideration.
     * @return an int that represents the connector.
     */
    public int getConnector(String side) {
        return connectorsOnSide.get(side);
    }

    /**
     * Getter method that returns the direction of the component tile.
     * @return the direction.
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Setter method that sets the direction of the component tile.
     * @param direction is the direction that will be given to the component.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Method that indicates if a component tile is facing down.
     * @return {@code true} if the component is facing down, {@code false} otherwise.
     */
    public boolean isFaceDown() {
        return faceDown;
    }

    /**
     * Setter method that sets the component facing down.
     */
    public void setFaceDown() {
        faceDown = true;
    }

    /**
     * Setter method that sets the component facing up.
     */
    public void setFaceUp() {
        faceDown = false;
    }

    /**
     * Method that tries to occupy a tile.
     * @return {@code true} if the occupation is successful, {@code false} if the tile is already occupied by another player.
     */
    public boolean tryOccupy() {
        return isOccupied.compareAndSet(false, true); // Tenta di occupare atomicamente
    }

    /**
     * Method that tries to release a tile.
     * @return {@code true} if the release is successful, {@code false} otherwise.
     */
    public boolean tryRelease() {
        return isOccupied.compareAndSet(true, false); // Tenta di rilasciare atomicamente
    }

}
