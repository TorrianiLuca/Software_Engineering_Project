package model.flightBoard;

import support.Couple;
import enumerations.FlightType;
import model.GameModel;
import model.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;




/**
 * a player's position is identified with his position on the table(first attribute of Couple)
 * and with his number of turns on the board(second attribute of Couple)
 * !!   Couple<POSITION, NUM_OF_TURNS>  !!
*/
public class FlightBoard implements Serializable {
    private HashMap<Player, Couple<Integer,Integer>> playersPositions;
    private final int totalPositions;//total number of cells in the shipboard

    /**
     * Cntructor.
     * @param gameModel is the model of the game of the game.
     */
    public FlightBoard(GameModel gameModel) {
        this.playersPositions = new HashMap<>();
        if(gameModel.getFlightType()== FlightType.STANDARD_FLIGHT){
            this.totalPositions = 24;
        }
        else {
            this.totalPositions = 18;
        }
    }

    /**
     * Method that sets the initial positions of the players in the flight board.
     * @param players are the players that are playing this game.
     */
    public void setPlayersInitialPositions(ArrayList<Player> players) {
        if (this.totalPositions == 18) {
            if(players.size() == 2) {
                playersPositions.put(players.get(0), new Couple(4, 0));
                players.get(0).setPositionOnFlightBoard(4);
                players.get(0).setLapCounter(0);
                playersPositions.put(players.get(1), new Couple(2, 0));
                players.get(1).setPositionOnFlightBoard(2);
                players.get(1).setLapCounter(0);
            }
            else if (players.size() == 3) {
                playersPositions.put(players.get(0), new Couple(4, 0));
                players.get(0).setPositionOnFlightBoard(4);
                players.get(0).setLapCounter(0);
                playersPositions.put(players.get(1), new Couple(2, 0));
                players.get(1).setPositionOnFlightBoard(2);
                players.get(1).setLapCounter(0);
                playersPositions.put(players.get(2), new Couple(1, 0));
                players.get(2).setPositionOnFlightBoard(1);
                players.get(2).setLapCounter(0);
            }
            else if (players.size() == 4) {
                playersPositions.put(players.get(0), new Couple(4, 0));
                players.get(0).setPositionOnFlightBoard(4);
                players.get(0).setLapCounter(0);
                playersPositions.put(players.get(1), new Couple(2, 0));
                players.get(1).setPositionOnFlightBoard(2);
                players.get(1).setLapCounter(0);
                playersPositions.put(players.get(2), new Couple(1, 0));
                players.get(2).setPositionOnFlightBoard(1);
                players.get(2).setLapCounter(0);
                playersPositions.put(players.get(3), new Couple(0, 0));
                players.get(3).setPositionOnFlightBoard(0);
                players.get(3).setLapCounter(0);
            }
        }
        else {
            if(players.size() == 2) {
                playersPositions.put(players.get(0), new Couple(6, 0));
                players.get(0).setPositionOnFlightBoard(6);
                players.get(0).setLapCounter(0);
                playersPositions.put(players.get(1), new Couple(3, 0));
                players.get(1).setPositionOnFlightBoard(3);
                players.get(1).setLapCounter(0);
            }
            else if (players.size() == 3) {
                playersPositions.put(players.get(0), new Couple(6, 0));
                players.get(0).setPositionOnFlightBoard(6);
                players.get(0).setLapCounter(0);
                playersPositions.put(players.get(1), new Couple(3, 0));
                players.get(1).setPositionOnFlightBoard(3);
                players.get(1).setLapCounter(0);
                playersPositions.put(players.get(2), new Couple(1, 0));
                players.get(2).setPositionOnFlightBoard(1);
                players.get(2).setLapCounter(0);
            }
            else if (players.size() == 4) {
                playersPositions.put(players.get(0), new Couple(6, 0));
                players.get(0).setPositionOnFlightBoard(6);
                players.get(0).setLapCounter(0);
                playersPositions.put(players.get(1), new Couple(3, 0));
                players.get(1).setPositionOnFlightBoard(3);
                players.get(1).setLapCounter(0);
                playersPositions.put(players.get(2), new Couple(1, 0));
                players.get(2).setPositionOnFlightBoard(1);
                players.get(2).setLapCounter(0);
                playersPositions.put(players.get(3), new Couple(0, 0));
                players.get(3).setPositionOnFlightBoard(0);
                players.get(3).setLapCounter(0);
            }
        }
    }

    /**
     * Method that returns the player position on the board.
     * @param p is the player.
     * @return the player position (position, lap counter).
     */
    public Couple<Integer,Integer> getPlayerPosition(Player p){
        return playersPositions.get(p);
    }

    /**
     * Method that return all the players position on the flight board.
     * The first attribute of the HashMap is the player, the second is a {@link Couple} that contains the player's
     * position on the flight board and its lap counter.
     */
    public HashMap<Player,Couple<Integer, Integer>> getPlayersMap(){
        return playersPositions;
    }

    /**
     * Method that removes a specified player.
     * @param p is the player that has to be removed.
     */
    public void removePlayer(Player p){
        playersPositions.remove(p);
    }

    /**
     * Method that moves the player backwards on the flight board.
     * @param p is the player that needs to move backward.
     * @param numOfPositions is the number of position the player must be moved backwards.
     */
    public void movePlayerBackward(Player p, int numOfPositions) {
        int temp_position = p.getPositionOnFlightBoard();
        int remember_initial_position = p.getPositionOnFlightBoard();
        for (int i = 0; i < numOfPositions; i++) {
            temp_position--;
            if (temp_position < 0) {
                temp_position = this.totalPositions - 1;
                p.setLapCounter(p.getLapCounter() - 1);
            }

            int proceed = 1;
            while (proceed == 1) {
                int val = 0;
                for (Player player : playersPositions.keySet()) {
                    if (player.getPositionOnFlightBoard() == temp_position && val == 0) {
                        if(temp_position != remember_initial_position) {
                            val = 1;
                        }
                    }
                }
                if (val == 1) {
                    temp_position--;
                    if (temp_position < 0) {
                        temp_position = this.totalPositions - 1;
                        p.setLapCounter(p.getLapCounter() - 1);
                    }
                } else {
                    proceed = 0;
                }
            }
        }

        p.setPositionOnFlightBoard(temp_position);
        playersPositions.get(p).setCouple(temp_position, p.getLapCounter());
    }

    /**
     * Method that moves the player forwards on the flight board.
     * @param p is the player that needs to move forward.
     * @param numOfPositions is the number of position the player must be moved forward.
     */
    public void movePlayerForward(Player p, int numOfPositions) {
        int temp_position = p.getPositionOnFlightBoard();
        int remember_initial_position = p.getPositionOnFlightBoard();
        for (int i = 0; i < numOfPositions; i++) {
            temp_position++;
            if (temp_position >= this.totalPositions) {
                temp_position = 0;
                p.setLapCounter(p.getLapCounter() + 1);
            }

            int proceed = 1;
            while (proceed == 1) {
                int val = 0;
                for (Player player : playersPositions.keySet()) {
                    if (player.getPositionOnFlightBoard() == temp_position && val == 0) {
                        if(temp_position != remember_initial_position) {
                            val = 1;
                        }
                    }
                }
                if (val == 1) {
                    temp_position++;
                    if (temp_position >= this.totalPositions) {
                        temp_position = 0;
                        p.setLapCounter(p.getLapCounter() + 1);
                    }
                } else {
                    proceed = 0;
                }
            }
        }

        p.setPositionOnFlightBoard(temp_position);
        playersPositions.get(p).setCouple(temp_position, p.getLapCounter());
    }

    /**
     * Method that returns the number of positions of the flight board.
     * @return the number of positions.
     */
    public int getTotalPositions() {
        return totalPositions;
    }
}



