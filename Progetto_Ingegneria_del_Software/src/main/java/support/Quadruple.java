package support;

import java.io.Serializable;

/**
 * Generic class for storing a quadruple of objects.
 * @param <T> is the first object.
 * @param <U> is the second object.
 * @param <R> is the third object.
 * @param <S> is the fourth object.
 */
public class Quadruple<T,U, R, S> implements Serializable {
    private T first;
    private U second;
    private R third;
    private S fourth;

    /**
     * Constructor.
     * @param first is the first parameter of the quadruple.
     * @param second is the second parameter of the quadruple.
     * @param third is the third parameter of the quadruple
     * @param fourth is the fourth parameter of the quadruple
     */
    public Quadruple(T first, U second, R third, S fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    /**
     * Getter method.
     * @return the first parameter of the quadruple.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Getter method.
     * @return the second parameter of the quadruple.
     */
    public U getSecond() {
        return second;
    }

    /**
     * Getter method.
     * @return the third parameter of the quadruple.
     */
    public R getThird() {
        return third;
    }

    /**
     * Getter method.
     * @return the fourth parameter of the quadruple.
     */
    public S getFourth() {return fourth;}
    public void setQuadruple(T first, U second, R third, S fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }
}

