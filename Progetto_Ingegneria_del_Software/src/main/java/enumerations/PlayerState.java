package enumerations;

import java.io.Serializable;

/**
 * This enumeration represent all the possibles states of the player.
 */
public enum PlayerState implements Serializable {
    IDLE,
    PICK_UP,
    WAIT,
    FIX,
    REMOVE_FIGURE,
    GAIN_GOOD,
    REMOVE_GOOD,
    REMOVE_BATTERY,
    PLAY,
    CONTROL,
    REPAIR,
    DEFEATED,
    POPULATE,

}
