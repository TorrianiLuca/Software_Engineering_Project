package controller;

import enumerations.FlightType;
import model.GameModel;
import network.messages.Message;
import network.structure.Client;
import network.structure.ServerMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.Quadruple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerManagerTest {

    ControllerManager controllerManager = new ControllerManager();
    ServerMain serverMain = mock(ServerMain.class);


    @Test
    void createGame() throws IOException {
        String nickname = "Player1";
        String clientId = "client1";

        controllerManager.createGame(nickname, clientId, serverMain);

        String expectedGameId = "game-1";

        assertTrue(controllerManager.getActiveGamesTest().containsKey(expectedGameId));

        assertEquals(expectedGameId, controllerManager.getGameFromClient(clientId));

        assertEquals(clientId, controllerManager.getNicknamesToIdMapTest().get(nickname));
    }

    @Test
    void joinGame0() {
        Controller controller=mock(Controller.class);
        GameModel gameModel = mock(GameModel.class);

        String gameId = "game-1";
        String nickname = "Player1";
        String clientId = "client1";

        when(controller.getGameModel()).thenReturn(gameModel);
        when(gameModel.getConnectedPlayers()).thenReturn(1);
        when(gameModel.getMaxPlayers()).thenReturn(4);
        controllerManager.getActiveGamesTest().put("game-1", controller);

        assertEquals(1, controllerManager.joinGame(nickname, clientId, gameId));
    }

    @Test
    void joinGame1() {
        Controller controller=mock(Controller.class);
        GameModel gameModel = mock(GameModel.class);

        String gameId = "game-1";
        String nickname = "Player1";
        String clientId = "client1";

        when(controller.getGameModel()).thenReturn(gameModel);
        when(gameModel.getConnectedPlayers()).thenReturn(4);
        when(gameModel.getMaxPlayers()).thenReturn(4);
        controllerManager.getActiveGamesTest().put("game-1", controller);

        assertEquals(2, controllerManager.joinGame(nickname, clientId, gameId));
    }

    @Test
    void joinGame2() {
        Controller controller=mock(Controller.class);
        GameModel gameModel = mock(GameModel.class);

        String gameId = "game-1";
        String nickname = "Player1";
        String clientId = "client1";

        when(controller.getGameModel()).thenReturn(gameModel);
        when(gameModel.getConnectedPlayers()).thenReturn(1);
        when(gameModel.getMaxPlayers()).thenReturn(4);

        assertEquals(3, controllerManager.joinGame(nickname, clientId, gameId));
    }

    @Test
    void initializeGame0() {
        Controller controller=mock(Controller.class);
        GameModel gameModel = mock(GameModel.class);
        String gameId = "game-1";
        when(controller.getGameModel()).thenReturn(gameModel);

        when(gameModel.getConnectedPlayers()).thenReturn(4);
        when(gameModel.getMaxPlayers()).thenReturn(4);

        controllerManager.getActiveGamesTest().put(gameId, controller);
        controllerManager.initializeGame(gameId);

        verify(controller).initGame(controllerManager.getNicknamesToIdMapTest());
    }

    @Test
    void initializeGame1() {
        Controller controller=mock(Controller.class);
        GameModel gameModel = mock(GameModel.class);
        String gameId = "game-1";
        when(controller.getGameModel()).thenReturn(gameModel);

        when(gameModel.getConnectedPlayers()).thenReturn(2);
        when(gameModel.getMaxPlayers()).thenReturn(4);

        controllerManager.getActiveGamesTest().put(gameId, controller);
        controllerManager.initializeGame(gameId);

        verify(controller, never()).initGame(any());
    }

    @Test
    void getGameController() {
        String gameId = "game-1";
        Controller controller=mock(Controller.class);
        controllerManager.getActiveGamesTest().put(gameId, controller);
        ;
        assertEquals(controller, controllerManager.getGameController(gameId));
    }


    @Test
    void getActiveGames0() {
        Controller controller = mock(Controller.class);
        GameModel gameModel = mock(GameModel.class);
        String gameId = "game-1";
        ArrayList<String> nicknames = new ArrayList<>();
        nicknames.add("Player1");
        nicknames.add("Player2");

        when(controller.getGameModel()).thenReturn(gameModel);
        when(gameModel.getFlightType()).thenReturn(FlightType.STANDARD_FLIGHT);
        when(gameModel.getConnectedPlayers()).thenReturn(2);
        when(gameModel.getMaxPlayers()).thenReturn(4);
        when(gameModel.getNicknames()).thenReturn(nicknames);

        controllerManager.getActiveGamesTest().put(gameId, controller);

        assertNotNull(controllerManager.getActiveGames());
        assertTrue(controllerManager.getActiveGames().containsKey(gameId));

        assertEquals(FlightType.STANDARD_FLIGHT, controllerManager.getActiveGames().get(gameId).getFirst());
        assertEquals(2, controllerManager.getActiveGames().get(gameId).getSecond());
        assertEquals(4, controllerManager.getActiveGames().get(gameId).getThird());
        assertEquals(nicknames, controllerManager.getActiveGames().get(gameId).getFourth());
    }

    @Test
    void getActiveGames1(){
        assertNull(controllerManager.getActiveGames());
    }

    @Test
    void isNicknameTaken() {
        controllerManager.addNickname("player1");
        assertTrue(controllerManager.isNicknameTaken("player1"));
    }

    @Test
    void onMessageReceived0() {
        Controller controller = mock(Controller.class);
        Message message = mock(Message.class);

        String clientId = "client1";
        String gameId = "game-1";

        when(message.getClientId()).thenReturn(clientId);
        controllerManager.getPlayerToGameMapTest().put(clientId, gameId);
        controllerManager.getActiveGamesTest().put(gameId, controller);
        controllerManager.onMessageReceived(message);

        verify(controller).onMessageReceived(message);
    }

    @Test
    void onMessageReceived1() {
        Message message = mock(Message.class);
        when(message.getClientId()).thenReturn("unknownClient");

        controllerManager.onMessageReceived(message);
    }

    @Test
    void getGameFromClient() {
        String clientId = "client1";
        String gameId = "game-1";

        controllerManager.getPlayerToGameMapTest().put(clientId, gameId);
        assertEquals(gameId, controllerManager.getGameFromClient(clientId));

    }

    @Test
    void removeGame0() {
        controllerManager.getActiveGamesTest().put("game-1", null);

        controllerManager.getPlayerToGameMapTest().put("client1", "game-1");
        controllerManager.getPlayerToGameMapTest().put("client2", "game-2");

        controllerManager.getNicknamesToIdMapTest().put("player1", "client1");
        controllerManager.getNicknamesToIdMapTest().put("player2", "client2");

        controllerManager.addNickname("player1");
        controllerManager.addNickname("player2");

        controllerManager.removeGame("game-1");

        assertFalse(controllerManager.getActiveGamesTest().containsKey("game-1"));
        assertFalse(controllerManager.getPlayerToGameMapTest().containsKey("client1"));
        assertFalse(controllerManager.getNicknamesToIdMapTest().containsKey("player1"));
        assertFalse(controllerManager.isNicknameTaken("player1"));

        assertTrue(controllerManager.getPlayerToGameMapTest().containsKey("client2"));
        assertTrue(controllerManager.getNicknamesToIdMapTest().containsKey("player2"));
        assertTrue(controllerManager.isNicknameTaken("player2"));
    }

    @Test
    void removeGame1() {
        controllerManager.getActiveGamesTest().put("game-1", null);

        controllerManager.getPlayerToGameMapTest().put("client1", "game-1");
        controllerManager.getPlayerToGameMapTest().put("client2", "game-2");

        controllerManager.getNicknamesToIdMapTest().put("player1", "client1");
        controllerManager.getNicknamesToIdMapTest().put("player2", "client2");

        controllerManager.addNickname("player1");
        controllerManager.addNickname("player2");

        controllerManager.removeGame("unknown-game");

        assertTrue(controllerManager.getActiveGamesTest().containsKey("game-1"));
        assertTrue(controllerManager.getPlayerToGameMapTest().containsKey("client1"));
        assertTrue(controllerManager.getNicknamesToIdMapTest().containsKey("player1"));
        assertTrue(controllerManager.isNicknameTaken("player1"));
    }

    @Test
    void getClientIdsFromGame0() {
        String gameId1 = "game-1";
        String gameId2 = "game-2";

        controllerManager.getPlayerToGameMapTest().put("client1", gameId1);
        controllerManager.getPlayerToGameMapTest().put("client2", gameId1);
        controllerManager.getPlayerToGameMapTest().put("client3", gameId2);


        assertEquals(2, controllerManager.getClientIdsFromGame(gameId1).size());
        assertTrue(controllerManager.getClientIdsFromGame(gameId1).contains("client1"));
        assertTrue(controllerManager.getClientIdsFromGame(gameId1).contains("client2"));
    }

    @Test
    void getClientIdsFromGame1() {
        assertTrue(controllerManager.getClientIdsFromGame("game-1").isEmpty());
    }
}