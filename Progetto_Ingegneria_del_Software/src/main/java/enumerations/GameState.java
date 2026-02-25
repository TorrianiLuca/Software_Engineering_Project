package enumerations;

import java.io.Serializable;

/**
 * This enumeration represent all the possibles game states.
 */
public enum GameState implements Serializable {
    LOGIN,
    START,
    BUILDING,
    FIXING,
    PLAYING,
    FINISHING,
    END,

    PLANETS,
    ABANDONED_SHIP,
    ABANDONED_STATION,
    STARDUST,
    SMUGGLERS,
    OPEN_SPACE,
    METEOR_SWARM,
    ROLL_DICE,
    FASE_1,
    FASE_2,
    FASE_3,
    COMBAT_ZONE,
    SLAVERS,
    PIRATES,
    DEFEATED_PLAYERS,
    EPIDEMIC,
    REMOVE_FIGURES,
    GAIN_GOODS,
    REMOVE_GOODS,
    REMOVE_BATTERIES,
    REPAIRING,
    POPULATE
}
