package observer;

import network.messages.AskNicknameMessage;
import network.messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Observable {

    private final ArrayList<Observer> observers = new ArrayList<>();

    /**
     * add an obserer to the list of observers.Those will be notified when changes are made
     * @param obs the observer you want to add
     */
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    /**
     * remove the passed observer
     * @param obs  the observer to remove
     */
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    /**
     * notify all observers that something changed (message)
     * @param message
     */
    protected void notifyObserver(Message message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}