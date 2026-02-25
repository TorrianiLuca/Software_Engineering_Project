package observer;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Observable class used only for the view.
 */
public class ViewObservable {
    protected ArrayList<ViewObserver> observers = new ArrayList<>();

    /**
     * This method will add an observer to the list of view observers.
     * @param viewObserver is the new view observer.
     */
    public void addObserver(ViewObserver viewObserver){
        observers.add(viewObserver);
    }

    /**
     * This method will notify a specific view observer that will accept the provided function.
     * @param lambda is the lambda function that the view observer has to satisfy.
     */
    protected void notifyObserver(Consumer<ViewObserver> lambda) {
        for (ViewObserver observer : observers) {
            lambda.accept(observer);
        }
    }
}
