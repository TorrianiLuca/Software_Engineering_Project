/*
* CARD PILE'S CLASS
* This class contains the card piles that are used for the standard flight
* Every pile has a number that goes from one to four. The fourth pile can't be observed.
* A pile contains two cards of level 2 and one card of level 1
* The flag 'isObservedByAPlayer' is set to true when a player is watching and pile. It is set back to false when the player
* puts the pile on the flight board again
* */

package model.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class that represents all the cards piles used for the standard flight.
 */
public class CardPile implements Serializable {
    private int pileNumber;
    private ArrayList<Card> cardPile;
    private AtomicBoolean isObservedByAPlayer;

    /**
     * Constructor of the class CardPile
     * @param pileNumber the identification number of the pile
     * @param cardPile the cards that compose the pile
     */
    public CardPile(int pileNumber, ArrayList<Card> cardPile) {
        this.pileNumber = pileNumber;
        this.cardPile = cardPile;
        this.isObservedByAPlayer = new AtomicBoolean(false);
    }

    /**
     *
     * @return the identification number of the pile
     */
    public int getPileNumber() {
        return pileNumber;
    }

    /**
     *
     * @return the cards that compose the pile
     */
    public ArrayList<Card> getCards() {
        return cardPile;
    }

    /**
     *
     * @return the value stored in the AtomicBoolean isObservedByPlayer(if the cardPile is observed by a plauer)
     */
    public boolean isObserved() {
        return isObservedByAPlayer.get();
    }

    /**
     * if nobody is observing(false) returns true and start observing the cardPile
     * if someone is already observing the pile(true) return false and isObservedByAPlayer is not modified
     * @return true if isObservedByAPlayer is false
     */
    public boolean tryObserved() {
        return isObservedByAPlayer.compareAndSet(false, true); // Tenta di occupare atomicamente
    }

    /**
     * Attempts to release the observation state atomically.
     * If the pile is currently being observed this method sets it to  false, indicating that the observation has ended.
     * The operation is atomic, ensuring thread safety in concurrent contexts.
     *
     * @return true if the state was true and is now  set to false
     *          false otherwise.
     */
    public boolean tryRelease() {
        return isObservedByAPlayer.compareAndSet(true, false); // Tenta di rilasciare atomicamente
    }
}
