
package model.flightBoard;

import support.Couple;
import enumerations.Color;
import enumerations.FlightType;
import model.GameModel;
import model.player.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlightBoardTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void setPlayersInitialPositions() {
        GameModel gameModel = mock(GameModel.class);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }

        // Test for FIRST_FLIGHT with two players
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);

        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 2)));
        assertEquals(4, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());
        assertEquals(2, flightBoard.getPlayerPosition(players.get(1)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(1)).getSecond());

        // Test for FIRST_FLIGHT with three players
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        flightBoard = new FlightBoard(gameModel);

        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 3)));
        assertEquals(4, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());
        assertEquals(2, flightBoard.getPlayerPosition(players.get(1)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(1)).getSecond());
        assertEquals(1, flightBoard.getPlayerPosition(players.get(2)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(2)).getSecond());

        // Test for FIRST_FLIGHT with four players
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        flightBoard = new FlightBoard(gameModel);

        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 4)));
        assertEquals(4, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());
        assertEquals(2, flightBoard.getPlayerPosition(players.get(1)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(1)).getSecond());
        assertEquals(1, flightBoard.getPlayerPosition(players.get(2)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(2)).getSecond());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(3)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(3)).getSecond());

        // Test for STANDARD_FLIGHT with two players
        when(gameModel.getFlightType()).thenReturn(FlightType.STANDARD_FLIGHT);
        flightBoard = new FlightBoard(gameModel);
        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 2)));
        flightBoard.setPlayersInitialPositions(players);
        assertEquals(6, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());
        assertEquals(3, flightBoard.getPlayerPosition(players.get(1)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(1)).getSecond());

        // Test for STANDARD_FLIGHT with three players
        when(gameModel.getFlightType()).thenReturn(FlightType.STANDARD_FLIGHT);
        flightBoard = new FlightBoard(gameModel);
        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 3)));
        flightBoard.setPlayersInitialPositions(players);
        assertEquals(6, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());
        assertEquals(3, flightBoard.getPlayerPosition(players.get(1)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(1)).getSecond());
        assertEquals(1, flightBoard.getPlayerPosition(players.get(2)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(2)).getSecond());

        // Test for STANDARD_FLIGHT with four players
        when(gameModel.getFlightType()).thenReturn(FlightType.STANDARD_FLIGHT);
        flightBoard = new FlightBoard(gameModel);
        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 4)));
        flightBoard.setPlayersInitialPositions(players);
        assertEquals(6, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());
        assertEquals(3, flightBoard.getPlayerPosition(players.get(1)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(1)).getSecond());
        assertEquals(1, flightBoard.getPlayerPosition(players.get(2)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(2)).getSecond());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(3)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(3)).getSecond());
    }

    @Test
    void getPlayerPosition() {
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }

        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 2)));
        Couple<Integer, Integer> position = flightBoard.getPlayerPosition(players.get(0));
        assertEquals(4, position.getFirst());
        assertEquals(0, position.getSecond());
    }

    @Test
    void getPlayersMap() {
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }

        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 2)));
        HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
        assertEquals(2, map.size());
        assertTrue(map.containsKey(players.get(0)));
        assertTrue(map.containsKey(players.get(1)));
    }

    @Test
    void removePlayer() {
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }

        flightBoard.setPlayersInitialPositions(new ArrayList<>(players.subList(0, 2)));
        HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
        assertEquals(2, map.size());
        assertTrue(map.containsKey(players.get(0)));
        assertTrue(map.containsKey(players.get(1)));

        flightBoard.removePlayer(players.get(0));
        assertEquals(1, map.size());
        assertFalse(map.containsKey(players.get(0)));
        assertTrue(map.containsKey(players.get(1)));
    }

    @Test
    void movePlayerBackward0() {
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }
        flightBoard.setPlayersInitialPositions(new ArrayList<>(players));

        flightBoard.movePlayerBackward(players.get(0), 1);
        assertEquals(3, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(0)).getSecond());

        flightBoard.movePlayerBackward(players.get(0), 4);
        assertEquals(14, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(-1, flightBoard.getPlayerPosition(players.get(0)).getSecond());

        flightBoard.movePlayerBackward(players.get(3), 3);
        assertEquals(15, flightBoard.getPlayerPosition(players.get(3)).getFirst());
        assertEquals(-1, flightBoard.getPlayerPosition(players.get(3)).getSecond());

        flightBoard.movePlayerBackward(players.get(2), 4);
        assertEquals(13, flightBoard.getPlayerPosition(players.get(2)).getFirst());
        assertEquals(-1, flightBoard.getPlayerPosition(players.get(2)).getSecond());
    }

    @Test
    void movePlayerBackward1() {
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }
        flightBoard.setPlayersInitialPositions(new ArrayList<>(players));

        flightBoard.movePlayerBackward(players.get(0), 15);
        assertEquals(4, flightBoard.getPlayerPosition(players.get(0)).getFirst());
        assertEquals(-1, flightBoard.getPlayerPosition(players.get(0)).getSecond());

    }

    @Test
    void movePlayerForward() {
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        FlightBoard flightBoard = new FlightBoard(gameModel);

        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(new Player("Player" + i, "11", Color.RED, FlightType.FIRST_FLIGHT));
        }
        flightBoard.setPlayersInitialPositions(new ArrayList<>(players));

        flightBoard.movePlayerForward(players.get(3), 1);
        assertEquals(3, flightBoard.getPlayerPosition(players.get(3)).getFirst());
        assertEquals(0, flightBoard.getPlayerPosition(players.get(3)).getSecond());

        flightBoard.movePlayerForward(players.get(3), 15);
        assertEquals(3, flightBoard.getPlayerPosition(players.get(3)).getFirst());
        assertEquals(1, flightBoard.getPlayerPosition(players.get(3)).getSecond());

    }

    @Test
    void getTotalPositions1() {
        FlightBoard flightBoard = new FlightBoard(new GameModel());
        GameModel gameModel = mock(GameModel.class);
        when(gameModel.getFlightType()).thenReturn(FlightType.FIRST_FLIGHT);
        assertEquals(18, flightBoard.getTotalPositions());
    }
}
