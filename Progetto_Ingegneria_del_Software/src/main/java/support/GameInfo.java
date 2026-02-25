package support;

import enumerations.FlightType;

import java.util.ArrayList;

/**
 * Class used to return all the game informations.
 */
public class GameInfo {
    private String gameId;
    private FlightType flightType;
    private int numConnectedPlayers;
    private int numMaxPlayers;
    private ArrayList<String> connectedPlayers;

    /**
     * Constructor
     * @param gameId
     * @param flightType
     * @param numConnectedPlayers
     * @param numMaxPlayers
     * @param connectedPlayers
     */
    public GameInfo(String gameId, FlightType flightType, int numConnectedPlayers, int numMaxPlayers, ArrayList<String> connectedPlayers) {
        this.gameId = gameId;
        this.flightType = flightType;
        this.numConnectedPlayers = numConnectedPlayers;
        this.numMaxPlayers = numMaxPlayers;
        this.connectedPlayers = connectedPlayers;
    }

    /**
     *
     * @return the gameId
     */
    public String getGameId() {
        return gameId;
    }

    /**
     *
     * @return the flight type(enumeration)
     */
    public FlightType getFlightType() {
        return flightType;
    }

    /**
     *
     * @return number of connected players
     */
    public int getNumConnectedPlayers() {
        return numConnectedPlayers;
    }

    /**
     *
     * @return the max number of players
     */
    public int getNumMaxPlayers() {
        return numMaxPlayers;
    }

    /**
     *
     * @return the ArrayList of String of connected players
     */
    public ArrayList<String> getConnectedPlayers() {
        return connectedPlayers;
    }
}