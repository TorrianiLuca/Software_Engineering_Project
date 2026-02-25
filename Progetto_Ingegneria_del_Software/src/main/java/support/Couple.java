package support;

import java.io.Serializable;

/**
 * Generic class for storing a couple of objects.
 * @param <T> is the first object.
 * @param <U> is the second object.
 */
public class Couple<T,U> implements Serializable {
    private T first;
    private U second;

    /**
     * constructor
     * @param first
     * @param second
     */
    public Couple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    /**
     *
     * @return firt element of the Couple
     */
    public T getFirst() {
        return first;
    }

    /**
     *
     * @return the second element of the Couple
     */
    public U getSecond() {
        return second;
    }

    /**
     * Constructor
     * @param first
     * @param second
     */
    public void setCouple(T first, U second) {
        this.first = first;
        this.second = second;
    }
}

