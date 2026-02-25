package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
import enumerations.FlightType;
import enumerations.GameState;
import model.GameModel;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import network.messages.*;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StardustTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCardType() {
        Stardust stardust = new Stardust(CardName.STARDUST, 1, "stardust_url.jpg");
        assertEquals(CardName.STARDUST, stardust.getCardType());

        stardust = new Stardust(CardName.SMUGGLERS, 1, "stardust_url.jpg");
        assertNotEquals(CardName.STARDUST, stardust.getCardType());
    }

    @Test
    void getLevel() {
        Stardust stardust = new Stardust(CardName.STARDUST, 1, "stardust_url.jpg");
        assertEquals(1, stardust.getLevel());

        stardust = new Stardust(CardName.STARDUST, 2, "stardust_url.jpg");
        assertEquals(2, stardust.getLevel());

    }

    @Test
    void getSide() {
        Stardust stardust = new Stardust(CardName.STARDUST, 1, "stardust_url.jpg");
        assertEquals(0, stardust.getSide());
    }

    @Test
    void setCardSide() {
        Stardust stardust = new Stardust(CardName.STARDUST, 1, "stardust_url.jpg");
        stardust.setCardSide(1);
        assertEquals(1, stardust.getSide());
        stardust.setCardSide(0);
        assertEquals(0, stardust.getSide());
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        ShipBoard shipBoard1= mock(ShipBoard.class);
        ShipBoard shipBoard2= mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);
        when(shipBoard1.numExposedConnectors()).thenReturn(2);
        when(shipBoard2.numExposedConnectors()).thenReturn(1);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);
        FlightBoard flightBoard = mock(FlightBoard.class);

        // Create card
        Stardust stardust = new Stardust(CardName.STARDUST, 1, "stardust_url.jpg");

        // Stub methods
        when(player1.getId()).thenReturn("id1");
        when(player2.getId()).thenReturn("id2");
        when(player1.getNickname()).thenReturn("Player1");

        // players list and retired players list
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(player1, player2));
        ArrayList<Player> retiredPlayers = new ArrayList<>(new ArrayList<>(Arrays.asList( player2)));

        when(gameModel.getPlayers()).thenReturn(players);
        when(gameModel.getPlayerInTurn()).thenReturn(player1);
        when(gameModel.getRetiredPlayers()).thenReturn(retiredPlayers);
        when(gameModel.getPlayersPosition()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2)));
        when(gameModel.getFlightBoard()).thenReturn(flightBoard);

        // Act
        stardust.onPickUp(gameModel, sender);

        ArgumentCaptor<Message> messageCaptor1 = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeastOnce()).accept(messageCaptor1.capture());

        List<Message> sentMessages =messageCaptor1.getAllValues();

        assertTrue(sentMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage dm && dm.getClientId().equals("Player1") && dm.isInTurn()));

        verify(gameModel).setGameState(GameState.STARDUST);

        verify(flightBoard).movePlayerBackward(player2, 1);
        verify(flightBoard).movePlayerBackward(player1, 2);

        assertTrue(sentMessages.stream().anyMatch(m -> m instanceof UpdateParametresMessage up && up.getClientId().equals("id1")));
        assertTrue(sentMessages.stream().anyMatch(m -> m instanceof UpdateParametresMessage up && up.getClientId().equals("id2")));

        verify(gameModel).refreshPlayersPosition();

        verify(gameModel).setPlayerInTurn(player2);

        // Capture messages sent
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeast(1)).accept(messageCaptor.capture());

        List<Message> capturedMessages = messageCaptor.getAllValues();

        // Check that messages were sent to both players
        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof ProceedNextCardMessage && (m).getClientId().equals("id1")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && ((SetCardInUseMessage) m).getClientId().equals("id2")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage2 dm2 && dm2.getClientId().equals("id2")));

        // Verify game state update
        verify(gameModel).setGameState(GameState.PLAYING);
    }

}