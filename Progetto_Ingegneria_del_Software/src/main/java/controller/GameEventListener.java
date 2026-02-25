package controller;

/**
 * Listener interface to handle key game events during gameplay.
 */
public interface GameEventListener {

    /**
     * Method called when the required number of players has been reached.
     */
    void onPlayerCountReached();

    /**
     * Called when all the player ships are correct.
     */
    void onPlayerShipBoardCorrect();

    /**
     * Method called when all the player's have put the figures on the ships.
     */
    void onPutFiguresInShip();

    /**
     * Method called when the game has finished
     */
    void finishGame();
}
