package controller;

import enumerations.CardName;
import enumerations.FlightType;
import model.card.Card;
import model.card.cardsType.*;
import network.messages.*;
import network.structure.Client;
import network.structure.ClientRMI;
import network.structure.ClientSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import view.View;

import java.io.IOException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientControllerTest {

    private View mockView;
    private Client mockClient;
    private ClientController controller;
    private ClientController clientController;
    private HashMap serverInfo = new HashMap<>();

    @BeforeEach
    public void setUp() throws Exception {
        mockView = mock(View.class);
        mockClient = mock(Client.class);

        controller = new ClientController(mockView, true);

        Field clientField = ClientController.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(controller, mockClient);


        clientController = new ClientController(mockView, true); // Socket
        serverInfo.put("address", "localhost");
        serverInfo.put("port", "8080");
    }


//    @Test
//    void testOnUpdateServerInfo_RMIConnection_RemoteException() throws Exception {
//        // Arrange
//        clientController = new ClientController(mockView, false); // RMI
//        PowerMockito.whenNew(ClientRMI.class)
//                .withArguments("localhost", 8080)
//                .thenThrow(new RemoteException("Cannot create the client"));
//
//        // Act & Assert
//        // Poiché il metodo chiama System.exit(1), dobbiamo gestire questa chiamata
//        // Nota: PowerMock non supporta facilmente System.exit in JUnit 5
//        // Per semplicità, verifichiamo che il flusso non proceda
//        assertThrows(Exception.class, () -> {
//            clientController.onUpdateServerInfo(serverInfo);
//        }, "RemoteException");
//    }




//    // Test per onUpdateServerInfo
//    @Test
//    public void testOnUpdateServerInfo() throws Exception {
//        HashMap<String, String> serverInfo = new HashMap<>();
//        serverInfo.put("address", "192.168.1.1");
//        serverInfo.put("port", "8080");
//
//        // Reset del mock client per testare la creazione del client
//        Field clientField = ClientController.class.getDeclaredField("client");
//        clientField.setAccessible(true);
//        clientField.set(controller, null);
//
//        controller.onUpdateServerInfo(serverInfo);
//
//        // Verifica che il client sia stato creato e che ping sia stato chiamato
//        Client actualClient = (Client) clientField.get(controller);
//        assertNotNull(actualClient);
//        verify(actualClient).ping();
//    }

    @Test
    public void testOnUpdateNickname() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        String nickname = "testNick";
        controller.onUpdateNickname(nickname);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof UsernameMessage);
        UsernameMessage usernameMessage = (UsernameMessage) sentMessage;
        assertEquals(clientId, usernameMessage.getClientId());
        assertEquals(nickname, usernameMessage.getNickname());
    }

    @Test
    public void testOnUpdateNicknameWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        String nickname = "testNick";

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateNickname(nickname),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof UsernameMessage);
        UsernameMessage usernameMessage = (UsernameMessage) sentMessage;
        assertEquals(clientId, usernameMessage.getClientId());
        assertEquals(nickname, usernameMessage.getNickname());
    }


    @Test
    public void testCreateGame() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.createGame();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof CreateGameMessage);
        CreateGameMessage createGameMessage = (CreateGameMessage) sentMessage;
        assertEquals(clientId, createGameMessage.getClientId());

    }

    @Test
    public void testCreateGameWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        String nickname = "testNick";

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.createGame(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof CreateGameMessage);
        CreateGameMessage createGameMessage = (CreateGameMessage) sentMessage;
        assertEquals(clientId, createGameMessage.getClientId());
    }


    @Test
    public void testJoinGame() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);
        String gameId = "testGameId";

        controller.joinGame(gameId);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof JoinGameMessage);
        JoinGameMessage joinGameMessage = (JoinGameMessage) sentMessage;
        assertEquals(clientId, joinGameMessage.getClientId());

    }

    @Test
    public void testJoinGameWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);
        String gameId = "testGameId";

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.joinGame(gameId),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof JoinGameMessage);
        JoinGameMessage joinGameMessage = (JoinGameMessage) sentMessage;
        assertEquals(clientId, joinGameMessage.getClientId());
    }


    @Test
    public void testRefreshGameOnServer() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);
        String gameId = "testGameId";

        controller.refreshGameOnServer();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RefreshGameOnServerMessage);
        RefreshGameOnServerMessage refreshGameOnServerMessage = (RefreshGameOnServerMessage) sentMessage;
        assertEquals(clientId, refreshGameOnServerMessage.getClientId());

    }

    @Test
    public void testRefreshGameOnServerWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);
        String gameId = "testGameId";

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.refreshGameOnServer(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RefreshGameOnServerMessage);
        RefreshGameOnServerMessage refreshGameOnServerMessage = (RefreshGameOnServerMessage) sentMessage;
        assertEquals(clientId, refreshGameOnServerMessage.getClientId());
    }


    @Test
    public void testOnUpdateMaxPlayerAndFlightType() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int maxPlayers = 4;
        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateMaxPlayerAndFlightType(maxPlayers, flightType);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient, times(2)).sendMessage(messageCaptor.capture());

        // Verifica i due messaggi inviati
        Message sentMessage1 = messageCaptor.getAllValues().get(0);
        assertTrue(sentMessage1 instanceof MaxPlayersForGameMessage);
        MaxPlayersForGameMessage maxPlayersMessage = (MaxPlayersForGameMessage) sentMessage1;
        assertEquals(maxPlayers, maxPlayersMessage.getMaxPlayers());
        assertEquals(clientId, maxPlayersMessage.getClientId());

        Message sentMessage2 = messageCaptor.getAllValues().get(1);
        assertTrue(sentMessage2 instanceof ChooseFlightTypeMessage);
        ChooseFlightTypeMessage flightTypeMessage = (ChooseFlightTypeMessage) sentMessage2;
        assertEquals(flightType, flightTypeMessage.getFlightType());
        assertEquals(clientId, flightTypeMessage.getClientId());
    }

    @Test
    public void testOnUpdateMaxPlayerAndFlightTypeWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int maxPlayers = 4;
        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateMaxPlayerAndFlightType(maxPlayers, flightType);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient, times(2)).sendMessage(messageCaptor.capture());

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateMaxPlayerAndFlightType(maxPlayers, flightType),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        Message sentMessage1 = messageCaptor.getAllValues().get(0);
        assertTrue(sentMessage1 instanceof MaxPlayersForGameMessage);
        MaxPlayersForGameMessage maxPlayersMessage = (MaxPlayersForGameMessage) sentMessage1;
        assertEquals(maxPlayers, maxPlayersMessage.getMaxPlayers());
        assertEquals(clientId, maxPlayersMessage.getClientId());

        Message sentMessage2 = messageCaptor.getAllValues().get(1);
        assertTrue(sentMessage2 instanceof ChooseFlightTypeMessage);
        ChooseFlightTypeMessage flightTypeMessage = (ChooseFlightTypeMessage) sentMessage2;
        assertEquals(flightType, flightTypeMessage.getFlightType());
        assertEquals(clientId, flightTypeMessage.getClientId());
    }


    @Test
    public void testOnUpdatePickTile() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int tileId = 42;
        controller.onUpdatePickTile(tileId);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickUpTileMessage);
        PickUpTileMessage pickUpTileMessage = (PickUpTileMessage) sentMessage;
        assertEquals(tileId, pickUpTileMessage.getTileId());
        assertEquals(clientId, pickUpTileMessage.getClientId());
    }

    @Test
    public void testOnUpdatePickTileWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int tileId = 42;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePickTile(tileId),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickUpTileMessage);
        PickUpTileMessage pickUpTileMessage = (PickUpTileMessage) sentMessage;
        assertEquals(tileId, pickUpTileMessage.getTileId());
        assertEquals(clientId, pickUpTileMessage.getClientId());
    }


    @Test
    public void testOnUpdatePickTileFromShip() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;
        controller.onUpdatePickTileFromShip(numRow, numCol);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickTileFromShipMessage);
        PickTileFromShipMessage pickTileFromShipMessage = (PickTileFromShipMessage) sentMessage;
        assertEquals(numRow, pickTileFromShipMessage.getRow());
        assertEquals(numCol, pickTileFromShipMessage.getCol());
        assertEquals(clientId, pickTileFromShipMessage.getClientId());
    }

    @Test
    public void testOnUpdatePickTileFromShipWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePickTileFromShip(numRow, numCol),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickTileFromShipMessage);
        PickTileFromShipMessage pickTileFromShipMessage = (PickTileFromShipMessage) sentMessage;
        assertEquals(numRow, pickTileFromShipMessage.getRow());
        assertEquals(numCol, pickTileFromShipMessage.getCol());
        assertEquals(clientId, pickTileFromShipMessage.getClientId());
    }


    @Test
    public void testOnUpdateShowShips() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdateShowShips();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ShowAllShipBoardsMessage);
        ShowAllShipBoardsMessage showAllShipBoardsMessage = (ShowAllShipBoardsMessage) sentMessage;
        assertEquals(clientId, showAllShipBoardsMessage.getClientId());
    }

    @Test
    public void testOnUpdateOnUpdateShowShipsWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int tileId = 42;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateShowShips(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ShowAllShipBoardsMessage);
        ShowAllShipBoardsMessage showAllShipBoardsMessage = (ShowAllShipBoardsMessage) sentMessage;
        assertEquals(clientId, showAllShipBoardsMessage.getClientId());
    }


    @Test
    public void testOnUpdateFinishedBuild() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int maxPlayers = 4;
        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateFinishedBuild();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient, times(2)).sendMessage(messageCaptor.capture());

        Message sentMessage1 = messageCaptor.getAllValues().get(0);
        assertTrue(sentMessage1 instanceof ShowAllShipBoardsMessage);
        ShowAllShipBoardsMessage showAllShipBoardsMessage1 = (ShowAllShipBoardsMessage) sentMessage1;
        assertEquals(clientId, showAllShipBoardsMessage1.getClientId());

        Message sentMessage2 = messageCaptor.getAllValues().get(1);
        assertTrue(sentMessage2 instanceof FinishedBuildingMessage);
        FinishedBuildingMessage finishedBuildingMessage1 = (FinishedBuildingMessage) sentMessage2;
        assertEquals(clientId, finishedBuildingMessage1.getClientId());
    }

    @Test
    public void testOnUpdateFinishedBuildWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int maxPlayers = 4;
        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateFinishedBuild();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient, times(2)).sendMessage(messageCaptor.capture());

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateFinishedBuild(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        Message sentMessage1 = messageCaptor.getAllValues().get(0);
        assertTrue(sentMessage1 instanceof ShowAllShipBoardsMessage);
        ShowAllShipBoardsMessage showAllShipBoardsMessage1 = (ShowAllShipBoardsMessage) sentMessage1;
        assertEquals(clientId, showAllShipBoardsMessage1.getClientId());

        Message sentMessage2 = messageCaptor.getAllValues().get(1);
        assertTrue(sentMessage2 instanceof FinishedBuildingMessage);
        FinishedBuildingMessage finishedBuildingMessage1 = (FinishedBuildingMessage) sentMessage2;
        assertEquals(clientId, finishedBuildingMessage1.getClientId());
    }


    @Test
    public void testOnUpdateFinishedPopulate() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int maxPlayers = 4;
        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateFinishedPopulate();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());

        Message sentMessage1 = messageCaptor.getAllValues().get(0);
        assertTrue(sentMessage1 instanceof FinishedPopulateMessage);
        FinishedPopulateMessage finishedPopulateMessage1 = (FinishedPopulateMessage) sentMessage1;
        assertEquals(clientId, finishedPopulateMessage1.getClientId());
    }

    @Test
    public void testOnUpdateFinishedPopulateWithSendMessageException() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int maxPlayers = 4;
        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateFinishedPopulate();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateFinishedPopulate(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        Message sentMessage1 = messageCaptor.getAllValues().get(0);
        assertTrue(sentMessage1 instanceof FinishedPopulateMessage);
        FinishedPopulateMessage finishedPopulateMessage1 = (FinishedPopulateMessage) sentMessage1;
        assertEquals(clientId, finishedPopulateMessage1.getClientId());
    }


    @Test
    public void testOnUpdateRemoveTile() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;
        controller.onUpdateRemoveTile(numRow, numCol);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveTileMessage);
        RemoveTileMessage removeTileMessage1 = (RemoveTileMessage) sentMessage;
        assertEquals(clientId, removeTileMessage1.getClientId());
        assertEquals(numRow, removeTileMessage1.getRow());
        assertEquals(numCol, removeTileMessage1.getColumn());
    }

    @Test
    public void testOnUpdateRemoveTileWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRemoveTile(numRow, numCol),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveTileMessage);
        RemoveTileMessage removeTileMessage1 = (RemoveTileMessage) sentMessage;
        assertEquals(clientId, removeTileMessage1.getClientId());
        assertEquals(numRow, removeTileMessage1.getRow());
        assertEquals(numCol, removeTileMessage1.getColumn());
    }


    @Test
    public void testOnUpdateTimerMessage() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdateTimerMessage();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof TimerMessage);
        TimerMessage timerMessage1 = (TimerMessage) sentMessage;
        assertEquals(clientId, timerMessage1.getClientId());
    }

    @Test
    public void testOnUpdateTimerMessageWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateTimerMessage(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof TimerMessage);
        TimerMessage timerMessage1 = (TimerMessage) sentMessage;
        assertEquals(clientId, timerMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePutTileInShip() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;
        controller.onUpdatePutTileInShip(numRow, numCol);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutTileInShipMessage);
        PutTileInShipMessage putTileInShipMessage1 = (PutTileInShipMessage) sentMessage;
        assertEquals(clientId, putTileInShipMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePutTileInShipWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePutTileInShip(numRow, numCol),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutTileInShipMessage);
        PutTileInShipMessage putTileInShipMessage1 = (PutTileInShipMessage) sentMessage;
        assertEquals(clientId, putTileInShipMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePutAstronautInShip() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;
        controller.onUpdatePutAstronautInShip(numRow, numCol);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutFigureInShipMessage);
        PutFigureInShipMessage putFigureInShipMessage1 = (PutFigureInShipMessage) sentMessage;
        assertEquals(clientId, putFigureInShipMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePutAstronautInShipWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePutAstronautInShip(numRow, numCol),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutFigureInShipMessage);
        PutFigureInShipMessage putFigureInShipMessage1 = (PutFigureInShipMessage) sentMessage;
        assertEquals(clientId, putFigureInShipMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePutPurpleInShip() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;
        controller.onUpdatePutPurpleInShip(numRow, numCol);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutPurpleInShipMessage);
        PutPurpleInShipMessage putPurpleInShipMessage1 = (PutPurpleInShipMessage) sentMessage;
        assertEquals(clientId, putPurpleInShipMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePutPurpleInShipWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePutPurpleInShip(numRow, numCol),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutPurpleInShipMessage);
        PutPurpleInShipMessage putPurpleInShipMessage1 = (PutPurpleInShipMessage) sentMessage;
        assertEquals(clientId, putPurpleInShipMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePutBrownInShip() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;
        controller.onUpdatePutBrownInShip(numRow, numCol);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutBrownInShipMessage);
        PutBrownInShipMessage putBrownInShipMessage1 = (PutBrownInShipMessage) sentMessage;
        assertEquals(clientId, putBrownInShipMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePutBrownInShipWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int numRow = 4;
        int numCol = 4;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePutBrownInShip(numRow, numCol),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutBrownInShipMessage);
        PutBrownInShipMessage putBrownInShipMessage1 = (PutBrownInShipMessage) sentMessage;
        assertEquals(clientId, putBrownInShipMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePickCardPile() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int num = 2;
        controller.onUpdatePickCardPile(num);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickUpCardPileMessage);
        PickUpCardPileMessage pickUpCardPileMessage1 = (PickUpCardPileMessage) sentMessage;
        assertEquals(clientId, pickUpCardPileMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePickCardPileWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        int num = 2;

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePickCardPile(num),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickUpCardPileMessage);
        PickUpCardPileMessage pickUpCardPileMessage1 = (PickUpCardPileMessage) sentMessage;
        assertEquals(clientId, pickUpCardPileMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePickCard() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdatePickCard();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickUpCardMessage);
        PickUpCardMessage pickUpCardMessage1 = (PickUpCardMessage) sentMessage;
        assertEquals(clientId, pickUpCardMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePickCardWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePickCard(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PickUpCardMessage);
        PickUpCardMessage pickUpCardMessage1 = (PickUpCardMessage) sentMessage;
        assertEquals(clientId, pickUpCardMessage1.getClientId());
    }


    @Test
    public void testOnUpdatePutTileInDeck() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdatePutTileInDeck();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutTileBackMessage);
        PutTileBackMessage putTileBackMessage1 = (PutTileBackMessage) sentMessage;
        assertEquals(clientId, putTileBackMessage1.getClientId());
    }

    @Test
    public void testOnUpdatePutTileInDeckWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePutTileInDeck(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutTileBackMessage);
        PutTileBackMessage putTileBackMessage1 = (PutTileBackMessage) sentMessage;
        assertEquals(clientId, putTileBackMessage1.getClientId());
    }


    @Test
    public void testOnUpSetDirection() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        String direction = "nord";

        controller.onUpdateSetDirection(direction);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ChangeTileDirectionMessage);
        ChangeTileDirectionMessage changeTileDirectionMessage1 = (ChangeTileDirectionMessage) sentMessage;
        assertEquals(direction, changeTileDirectionMessage1.getDirection());
        assertEquals(clientId, changeTileDirectionMessage1.getClientId());
    }

    @Test
    public void testOnUpdateSetDirectionSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        String direction = "nord";

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateSetDirection(direction),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ChangeTileDirectionMessage);
        ChangeTileDirectionMessage changeTileDirectionMessage1 = (ChangeTileDirectionMessage) sentMessage;
        assertEquals(direction, changeTileDirectionMessage1.getDirection());
        assertEquals(clientId, changeTileDirectionMessage1.getClientId());
    }


    @Test
    public void testOnUpdateStopWatchingShips() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Card card = new Slavers(CardName.SLAVERS, 1, "1", 1, 1,1,1);

        controller.onUpdateStopWatchingShips(card);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof StopWatchingShipsMessage);
        StopWatchingShipsMessage stopWatchingShipsMessage = (StopWatchingShipsMessage) sentMessage;
        assertEquals(clientId, stopWatchingShipsMessage.getClientId());
    }

    @Test
    public void testOnUpdateStopWatchingShipsWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Card card = new Slavers(CardName.SLAVERS, 1, "1", 1, 1,1,1);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateStopWatchingShips(card),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof StopWatchingShipsMessage);
        StopWatchingShipsMessage stopWatchingShipsMessage = (StopWatchingShipsMessage) sentMessage;
        assertEquals(clientId, stopWatchingShipsMessage.getClientId());
    }


    @Test
    public void testOnUpdateStopWatchingCardPile() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdateStopWatchingCardPile();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutCardPileBackMessage);
        PutCardPileBackMessage putCardPileBackMessage = (PutCardPileBackMessage) sentMessage;
        assertEquals(clientId, putCardPileBackMessage.getClientId());
    }

    @Test
    public void testOnUpdateStopWatchingCardPileWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateStopWatchingCardPile(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PutCardPileBackMessage);
        PutCardPileBackMessage putCardPileBackMessage = (PutCardPileBackMessage) sentMessage;
        assertEquals(clientId, putCardPileBackMessage.getClientId());
    }


    @Test
    public void testOnUpdateFlightType() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        FlightType flightType = FlightType.STANDARD_FLIGHT;
        controller.onUpdateFlightType(flightType);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ChooseFlightTypeMessage);
        ChooseFlightTypeMessage chooseFlightTypeMessage = (ChooseFlightTypeMessage) sentMessage;
        assertEquals(clientId, chooseFlightTypeMessage.getClientId());
        assertEquals(flightType, chooseFlightTypeMessage.getFlightType());
    }

    @Test
    public void testOnUpdateFlightTypeWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        FlightType flightType = FlightType.STANDARD_FLIGHT;
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateFlightType(flightType),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ChooseFlightTypeMessage);
        ChooseFlightTypeMessage chooseFlightTypeMessage = (ChooseFlightTypeMessage) sentMessage;
        assertEquals(clientId, chooseFlightTypeMessage.getClientId());
        assertEquals(flightType, chooseFlightTypeMessage.getFlightType());
    }


    @Test
    public void testOnUpdatePlanetChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Planets planets = mock(Planets.class);
        controller.onUpdatePlanetChoice(planets, "ok", 1 );

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PlanetChoiceMessage);
        PlanetChoiceMessage planetChoiceMessage = (PlanetChoiceMessage) sentMessage;
        assertEquals(clientId, planetChoiceMessage.getClientId());
        assertEquals("ok", planetChoiceMessage.getChoice());
        assertEquals(1, planetChoiceMessage.getNumPlanet());
    }

    @Test
    public void testOnUpdatePlanetChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Planets planets = mock(Planets.class);

        FlightType flightType = FlightType.STANDARD_FLIGHT;
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePlanetChoice(planets, "ok", 1 ),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PlanetChoiceMessage);
        PlanetChoiceMessage planetChoiceMessage = (PlanetChoiceMessage) sentMessage;
        assertEquals(clientId, planetChoiceMessage.getClientId());
        assertEquals("ok", planetChoiceMessage.getChoice());
        assertEquals(1, planetChoiceMessage.getNumPlanet());
    }


    @Test
    public void testOnUpdateSmugglersChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Smugglers smugglers = mock(Smugglers.class);
        controller.onUpdateSmugglersChoice(smugglers);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof SmugglersChoiceMessage);
        SmugglersChoiceMessage smugglersChoiceMessage = (SmugglersChoiceMessage) sentMessage;
        assertEquals(clientId, smugglersChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdateSmugglersChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Smugglers smugglers = mock(Smugglers.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateSmugglersChoice(smugglers),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof SmugglersChoiceMessage);
        SmugglersChoiceMessage smugglersChoiceMessage = (SmugglersChoiceMessage) sentMessage;
        assertEquals(clientId, smugglersChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateSlaversChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Slavers slavers = mock(Slavers.class);
        controller.onUpdateSlaversChoice(slavers);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof SlaversChoiceMessage);
        SlaversChoiceMessage slaversChoiceMessage = (SlaversChoiceMessage) sentMessage;
        assertEquals(clientId, slaversChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdateSlaversChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Slavers slavers = mock(Slavers.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateSlaversChoice(slavers),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof SlaversChoiceMessage);
        SlaversChoiceMessage slaversChoiceMessage = (SlaversChoiceMessage) sentMessage;
        assertEquals(clientId, slaversChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdatePiratesChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Pirates pirates = mock(Pirates.class);
        controller.onUpdatePiratesChoice(pirates);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PiratesChoiceMessage);
        PiratesChoiceMessage piratesChoiceMessage = (PiratesChoiceMessage) sentMessage;
        assertEquals(clientId, piratesChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdatePiratesChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Pirates pirates = mock(Pirates.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () ->  controller.onUpdatePiratesChoice(pirates),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof PiratesChoiceMessage);
        PiratesChoiceMessage piratesChoiceMessage = (PiratesChoiceMessage) sentMessage;
        assertEquals(clientId, piratesChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateAbandonedStationChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        AbandonedStation abandonedStation = mock(AbandonedStation.class);
        controller.onUpdateAbandonedStationChoice(abandonedStation, "ok");

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof AbandonedStationChoiceMessage);
        AbandonedStationChoiceMessage abandonedStationChoiceMessage = (AbandonedStationChoiceMessage) sentMessage;
        assertEquals(clientId, abandonedStationChoiceMessage.getClientId());
        assertEquals("ok", abandonedStationChoiceMessage.getChoice());
    }

    @Test
    public void testOnUpdateAbandonedStationChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        AbandonedStation abandonedStation = mock(AbandonedStation.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () ->  controller.onUpdateAbandonedStationChoice(abandonedStation, "ok"),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof AbandonedStationChoiceMessage);
        AbandonedStationChoiceMessage abandonedStationChoiceMessage = (AbandonedStationChoiceMessage) sentMessage;
        assertEquals(clientId, abandonedStationChoiceMessage.getClientId());
        assertEquals("ok", abandonedStationChoiceMessage.getChoice());
    }


    @Test
    public void testOnUpdateAbandonedShipChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        AbandonedShip abandonedShip = mock(AbandonedShip.class);
        controller.onUpdateAbandonedShipChoice(abandonedShip, "ok");

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof AbandonedShipChoiceMessage);
        AbandonedShipChoiceMessage abandonedShipChoiceMessage = (AbandonedShipChoiceMessage) sentMessage;
        assertEquals(clientId, abandonedShipChoiceMessage.getClientId());
        assertEquals("ok", abandonedShipChoiceMessage.getChoice());
    }

    @Test
    public void testOnUpdateAbandonedShipChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        AbandonedShip abandonedShip = mock(AbandonedShip.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () ->  controller.onUpdateAbandonedShipChoice(abandonedShip, "ok"),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof AbandonedShipChoiceMessage);
        AbandonedShipChoiceMessage abandonedShipChoiceMessage = (AbandonedShipChoiceMessage) sentMessage;
        assertEquals(clientId, abandonedShipChoiceMessage.getClientId());
        assertEquals("ok", abandonedShipChoiceMessage.getChoice());
    }


    @Test
    public void testOnUpdateOpenSpaceChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        OpenSpace openSpace = mock(OpenSpace.class);
        controller.onUpdateOpenSpaceChoice(openSpace);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof OpenSpaceChoiceMessage);
        OpenSpaceChoiceMessage openSpaceChoiceMessage = (OpenSpaceChoiceMessage) sentMessage;
        assertEquals(clientId, openSpaceChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdateOpenSpaceChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        OpenSpace openSpace = mock(OpenSpace.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () ->  controller.onUpdateOpenSpaceChoice(openSpace),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof OpenSpaceChoiceMessage);
        OpenSpaceChoiceMessage openSpaceChoiceMessage = (OpenSpaceChoiceMessage) sentMessage;
        assertEquals(clientId, openSpaceChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateMeteorSwarmChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        MeteorSwarm meteorSwarm = mock(MeteorSwarm.class);
        controller.onUpdateMeteorSwarmChoice(meteorSwarm, 1);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof MeteorSwarmChoiceMessage);
        MeteorSwarmChoiceMessage meteorSwarmChoiceMessage = (MeteorSwarmChoiceMessage) sentMessage;
        assertEquals(clientId, meteorSwarmChoiceMessage.getClientId());
    }

    @Test
    public void testOnMeteorSwarmChoiceChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        MeteorSwarm meteorSwarm = mock(MeteorSwarm.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () ->  controller.onUpdateMeteorSwarmChoice(meteorSwarm, 1),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof MeteorSwarmChoiceMessage);
        MeteorSwarmChoiceMessage meteorSwarmChoiceMessage = (MeteorSwarmChoiceMessage) sentMessage;
        assertEquals(clientId, meteorSwarmChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateCombatZoneChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateCombatZoneChoice(combatZone, 4);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof CombatZoneChoiceMessage);
        CombatZoneChoiceMessage combatZoneChoiceMessage = (CombatZoneChoiceMessage) sentMessage;
        assertEquals(clientId, combatZoneChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdateCombatZoneChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () ->   controller.onUpdateCombatZoneChoice(combatZone, 4),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof CombatZoneChoiceMessage);
        CombatZoneChoiceMessage combatZoneChoiceMessage = (CombatZoneChoiceMessage) sentMessage;
        assertEquals(clientId, combatZoneChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateDefeatedPiratesChoice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Pirates pirates = mock(Pirates.class);
        controller.onUpdateDefeatedPiratesChoice(pirates, 4);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof DefeatedPiratesMessage);
        DefeatedPiratesMessage defeatedPiratesMessage = (DefeatedPiratesMessage) sentMessage;
        assertEquals(clientId, defeatedPiratesMessage.getClientId());
    }

    @Test
    public void testOnUpdateDefeatedPiratesChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        Pirates pirates = mock(Pirates.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateDefeatedPiratesChoice(pirates, 4),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof DefeatedPiratesMessage);
        DefeatedPiratesMessage defeatedPiratesMessage = (DefeatedPiratesMessage) sentMessage;
        assertEquals(clientId, defeatedPiratesMessage.getClientId());
    }


    @Test
    public void testOnUpdatePhase1Choice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdatePhase1Choice(combatZone);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof Fase1ChoiceMessage);
        Fase1ChoiceMessage fase1ChoiceMessage = (Fase1ChoiceMessage) sentMessage;
        assertEquals(clientId, fase1ChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdatePhase1ChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePhase1Choice(combatZone),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof Fase1ChoiceMessage);
        Fase1ChoiceMessage fase1ChoiceMessage = (Fase1ChoiceMessage) sentMessage;
        assertEquals(clientId, fase1ChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdatePhase2Choice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdatePhase2Choice(combatZone);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof Fase2ChoiceMessage);
        Fase2ChoiceMessage fase2ChoiceMessage = (Fase2ChoiceMessage) sentMessage;
        assertEquals(clientId, fase2ChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdatePhase2ChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePhase2Choice(combatZone),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof Fase2ChoiceMessage);
        Fase2ChoiceMessage fase2ChoiceMessage = (Fase2ChoiceMessage) sentMessage;
        assertEquals(clientId, fase2ChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdatePhase3Choice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdatePhase3Choice(combatZone);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof Fase3ChoiceMessage);
        Fase3ChoiceMessage fase3ChoiceMessage = (Fase3ChoiceMessage) sentMessage;
        assertEquals(clientId, fase3ChoiceMessage.getClientId());
    }

    @Test
    public void testOnUpdatePhase3ChoiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdatePhase3Choice(combatZone),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof Fase3ChoiceMessage);
        Fase3ChoiceMessage fase3ChoiceMessage = (Fase3ChoiceMessage) sentMessage;
        assertEquals(clientId, fase3ChoiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateGainGood() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateGainGood(combatZone, "ok", 4, 3);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof GainGoodMessage);
        GainGoodMessage gainGoodMessage = (GainGoodMessage) sentMessage;
        assertEquals(clientId, gainGoodMessage.getClientId());
    }

    @Test
    public void testOnUpdateGainGoodWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateGainGood(combatZone, "ok", 4, 3),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof GainGoodMessage);
        GainGoodMessage gainGoodMessage = (GainGoodMessage) sentMessage;
        assertEquals(clientId, gainGoodMessage.getClientId());
    }


    @Test
    public void testOnUpdateProceed() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdateProceed();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof WaitProceedMessage);
        WaitProceedMessage waitProceedMessage = (WaitProceedMessage) sentMessage;
        assertEquals(clientId, waitProceedMessage.getClientId());
    }

    @Test
    public void testOnUpdateProceedWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateProceed(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof WaitProceedMessage);
        WaitProceedMessage waitProceedMessage = (WaitProceedMessage) sentMessage;
        assertEquals(clientId, waitProceedMessage.getClientId());
    }


    @Test
    public void testOnUpdateRetire() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        controller.onUpdateRetire();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RetireMessage);
        RetireMessage retireMessage = (RetireMessage) sentMessage;
        assertEquals(clientId, retireMessage.getClientId());
    }

    @Test
    public void testOnUpdateRetireWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRetire(),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RetireMessage);
        RetireMessage retireMessage = (RetireMessage) sentMessage;
        assertEquals(clientId, retireMessage.getClientId());
    }


    @Test
    public void testOnUpdateNextPhase() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateNextPhase(combatZone);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof WaitNextPhaseMessage);
        WaitNextPhaseMessage waitNextPhaseMessage = (WaitNextPhaseMessage) sentMessage;
        assertEquals(clientId, waitNextPhaseMessage.getClientId());
    }

    @Test
    public void testOnUpdateNextPhaseWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateNextPhase(combatZone),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof WaitNextPhaseMessage);
        WaitNextPhaseMessage waitNextPhaseMessage = (WaitNextPhaseMessage) sentMessage;
        assertEquals(clientId, waitNextPhaseMessage.getClientId());
    }


    @Test
    public void testOnUpdateActivateCannon() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateActivateCannon(combatZone, 4, 3);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ActivateCannonMessage);
        ActivateCannonMessage activateCannonMessage = (ActivateCannonMessage) sentMessage;
        assertEquals(clientId, activateCannonMessage.getClientId());
        assertEquals(3, activateCannonMessage.getCol());
        assertEquals(4, activateCannonMessage.getRow());
    }

    @Test
    public void testOnUpdateActivateCannonWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateActivateCannon(combatZone, 4, 3),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ActivateCannonMessage);
        ActivateCannonMessage activateCannonMessage = (ActivateCannonMessage) sentMessage;
        assertEquals(clientId, activateCannonMessage.getClientId());
        assertEquals(3, activateCannonMessage.getCol());
        assertEquals(4, activateCannonMessage.getRow());
    }


    @Test
    public void testOnUpdateActivateEngine() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateActivateEngine(combatZone, 4, 3);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ActivateEngineMessage);
        ActivateEngineMessage activateEngineMessage = (ActivateEngineMessage) sentMessage;
        assertEquals(clientId, activateEngineMessage.getClientId());
        assertEquals(3, activateEngineMessage.getCol());
        assertEquals(4, activateEngineMessage.getRow());
    }

    @Test
    public void testOnUpdateActivateEngineWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateActivateEngine(combatZone, 4, 3),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ActivateEngineMessage);
        ActivateEngineMessage activateEngineMessage = (ActivateEngineMessage) sentMessage;
        assertEquals(clientId, activateEngineMessage.getClientId());
        assertEquals(3, activateEngineMessage.getCol());
        assertEquals(4, activateEngineMessage.getRow());
    }


    @Test
    public void testOnUpdateActivateShield() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateActivateShield(combatZone, 4, 3);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ActivateShieldMessage);
        ActivateShieldMessage activateShieldMessage = (ActivateShieldMessage) sentMessage;
        assertEquals(clientId, activateShieldMessage.getClientId());
        assertEquals(3, activateShieldMessage.getCol());
        assertEquals(4, activateShieldMessage.getRow());
    }

    @Test
    public void testOnUpdateActivateShieldWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateActivateShield(combatZone, 4, 3),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof ActivateShieldMessage);
        ActivateShieldMessage activateShieldMessage = (ActivateShieldMessage) sentMessage;
        assertEquals(clientId, activateShieldMessage.getClientId());
        assertEquals(3, activateShieldMessage.getCol());
        assertEquals(4, activateShieldMessage.getRow());
    }


    @Test
    public void testOnUpdateRemoveBattery() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateRemoveBattery(combatZone, 4, 3, 2);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveBatteryMessage);
        RemoveBatteryMessage removeBatteryMessage = (RemoveBatteryMessage) sentMessage;
        assertEquals(clientId, removeBatteryMessage.getClientId());
        assertEquals(3, removeBatteryMessage.getCol());
        assertEquals(4, removeBatteryMessage.getRow());
    }

    @Test
    public void testOnUpdateRemoveBatteryWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRemoveBattery(combatZone, 4, 3, 2),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveBatteryMessage);
        RemoveBatteryMessage removeBatteryMessage = (RemoveBatteryMessage) sentMessage;
        assertEquals(clientId, removeBatteryMessage.getClientId());
        assertEquals(3, removeBatteryMessage.getCol());
        assertEquals(4, removeBatteryMessage.getRow());
    }


    @Test
    public void testOnUpdateRemoveGood() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateRemoveGood(combatZone, 4, 3);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveGoodMessage);
        RemoveGoodMessage removeGoodMessage = (RemoveGoodMessage) sentMessage;
        assertEquals(clientId, removeGoodMessage.getClientId());
        assertEquals(3, removeGoodMessage.getCol());
        assertEquals(4, removeGoodMessage.getRow());
    }

    @Test
    public void testOnUpdateRemoveGoodWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRemoveGood(combatZone, 4, 3),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveGoodMessage);
        RemoveGoodMessage removeGoodMessage = (RemoveGoodMessage) sentMessage;
        assertEquals(clientId, removeGoodMessage.getClientId());
        assertEquals(3, removeGoodMessage.getCol());
        assertEquals(4, removeGoodMessage.getRow());
    }


    @Test
    public void testOnUpdateRemoveFigure() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateRemoveFigure(combatZone, 4, 3);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveFigureMessage);
        RemoveFigureMessage removeFigureMessage = (RemoveFigureMessage) sentMessage;
        assertEquals(clientId, removeFigureMessage.getClientId());
        assertEquals(3, removeFigureMessage.getCol());
        assertEquals(4, removeFigureMessage.getRow());
    }

    @Test
    public void testOnUpdateRemoveFigureWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRemoveFigure(combatZone, 4, 3),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RemoveFigureMessage);
        RemoveFigureMessage removeFigureMessage = (RemoveFigureMessage) sentMessage;
        assertEquals(clientId, removeFigureMessage.getClientId());
        assertEquals(3, removeFigureMessage.getCol());
        assertEquals(4, removeFigureMessage.getRow());
    }


    @Test
    public void testOnUpdateRollDice() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateRollDice(combatZone);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RollDiceMessage);
        RollDiceMessage rollDiceMessage = (RollDiceMessage) sentMessage;
        assertEquals(clientId, rollDiceMessage.getClientId());
    }

    @Test
    public void testOnUpdateRollDiceWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRollDice(combatZone),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RollDiceMessage);
        RollDiceMessage rollDiceMessage = (RollDiceMessage) sentMessage;
        assertEquals(clientId, rollDiceMessage.getClientId());
    }


    @Test
    public void testOnUpdateRepaired() throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        controller.onUpdateRepaired(combatZone);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RepairingShipMessage);
        RepairingShipMessage repairingShipMessage = (RepairingShipMessage) sentMessage;
        assertEquals(clientId, repairingShipMessage.getClientId());
    }

    @Test
    public void testOnUpdateRepairedWithSendMessageException()  throws Exception {
        String clientId = "testClientId";
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        clientIdField.set(controller, clientId);

        CombatZone combatZone = mock(CombatZone.class);
        doThrow(new RemoteException("Test exception")).when(mockClient).sendMessage(any(Message.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.onUpdateRepaired(combatZone),
                "Expected RuntimeException to be thrown"
        );

        assertEquals(RemoteException.class, thrown.getCause().getClass());
        assertEquals("Test exception", thrown.getCause().getMessage());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockClient).sendMessage(messageCaptor.capture());
        Message sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage instanceof RepairingShipMessage);
        RepairingShipMessage repairingShipMessage = (RepairingShipMessage) sentMessage;
        assertEquals(clientId, repairingShipMessage.getClientId());
    }


    @Test
    void testUpdate_ClientDisconnectedMessage_RemoteException() throws NoSuchFieldException, IllegalAccessException, RemoteException {
        // Arrange
        ClientDisconnectedMessage message = mock(ClientDisconnectedMessage.class);
        Client mockClient = mock(Client.class);
        doThrow(new RemoteException("Disconnection error")).when(mockClient).disconnect(false);

        Field clientField = ClientController.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(clientController, mockClient);

        // Act
        clientController.update(message);

        // Assert
        verify(mockClient, times(1)).disconnect(false);
        verify(mockView, times(1)).addServerMessage(message);
    }








    // Test per update con AskNicknameMessage
    @Test
    public void testUpdateWithAskNicknameMessage() throws Exception {
        String clientId = "testClientId";
        Message message = new AskNicknameMessage(clientId);
        controller.update(message);

        verify(mockView).addServerMessage(message);

        // Verifica che clientId sia stato impostato
        Field clientIdField = ClientController.class.getDeclaredField("clientId");
        clientIdField.setAccessible(true);
        String setClientId = (String) clientIdField.get(controller);
        assertEquals(clientId, setClientId);
    }

    // Test per update con ClientDisconnectedMessage
    @Test
    public void testUpdateWithClientDisconnectedMessage() throws Exception {
        Message message = new ClientDisconnectedMessage();
        controller.update(message);

        verify(mockView).addServerMessage(message);
        verify(mockClient).disconnect(false);
    }

    // Test per update con AskMaxPlayerAndFlightTypeMessage
    @Test
    public void testUpdateWithAskMaxPlayerAndFlightTypeMessage() {
        Message message = new AskMaxPlayerAndFlightTypeMessage("testClientId");
        controller.update(message);

        verify(mockView).addServerMessage(message);
        verify(mockView).askMaxPlayerAndFlightType();
    }

    // Test per isAddressValid
    @Test
    public void testIsAddressValid() {
        assertTrue(ClientController.isAddressValid("192.168.1.1"));
        assertTrue(ClientController.isAddressValid("255.255.255.255"));
        assertFalse(ClientController.isAddressValid("256.256.256.256"));
        assertFalse(ClientController.isAddressValid("abc.def.ghi.jkl"));
        assertFalse(ClientController.isAddressValid("192.168.1"));
        assertFalse(ClientController.isAddressValid("192.168.1.1.1"));
    }

    // Test per isPortValid
    @Test
    public void testIsPortValid() {
        assertTrue(ClientController.isPortValid("8080"));
        assertTrue(ClientController.isPortValid("1"));
        assertTrue(ClientController.isPortValid("65535"));
        assertFalse(ClientController.isPortValid("0"));
        assertFalse(ClientController.isPortValid("65536"));
        assertFalse(ClientController.isPortValid("abc"));
        assertFalse(ClientController.isPortValid(""));
    }
}