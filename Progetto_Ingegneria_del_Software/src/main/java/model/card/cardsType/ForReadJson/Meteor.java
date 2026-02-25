/*
* METEOR'S CLASS
*
* This class describes the meteors that can hit a player's ship
* Every meteor has a direction (nord, est, sud or ovest) and a power(1: small, 2: large)
* */

package model.card.cardsType.ForReadJson;

import java.io.Serializable;

/**
 * Class that represents a single meteor.
 */
public class Meteor implements Serializable {
    private String direction;
    private int power;

    /**
     * Constructor.
     * @param direction is the direction of the meteor.
     * @param power is the power of the meteor.
     */
    public Meteor(String direction, int power) {
        this.direction = direction;
        this.power = power;
    }

    /**
     * @return the direction of the meteor (nord, est, sud or ovest)
     */
    public String getDirection() {
        return direction;
    }

    /**
     * @return the intensity of the meteor (1: small, 2: large)
     */
    public int getPower() {
        return power;
    }
}
