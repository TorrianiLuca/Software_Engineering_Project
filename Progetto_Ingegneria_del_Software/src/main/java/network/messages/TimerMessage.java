package network.messages;

import controller.Controller;
import enumerations.FlightType;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *Message that show the ship's construction time
 */

public class TimerMessage extends Message{
    private final int time;

    /**
     * Default constructor.
     * @param clientId is the ID of the player.
     */

    public TimerMessage(String clientId) {
        super(clientId);
        this.time = 10;
    }


    /**
     *
     * @return the duration of the timer
     */
    public int getTime() {
        return time;
    }


    @Override
    public void process(Controller controller) {
        controller.handleTimerMessage(getClientId(), time);
    }
}