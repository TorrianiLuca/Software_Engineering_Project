package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import network.messages.DrawnCardMessage;
import network.messages.DrawnCardMessage2;
import network.messages.Message;
import network.messages.SetCardInUseMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class AbandonedShipTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNumOfLoseFigures() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(3, abandonedShip.getNumOfLoseFigures());
    }

    @Test
    void getNumOfCreditsTaken() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(4, abandonedShip.getNumOfCreditsTaken());
    }

    @Test
    void getLoseFlightDays() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(1, abandonedShip.getLoseFlightDays());
    }

    @Test
    void processAbandonedShipChoice() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        ShipBoard shipBoard1 = mock(ShipBoard.class);
        ShipBoard shipBoard2=mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);

        when(shipBoard1.getNumFigures()).thenReturn(2);
        when(shipBoard2.getNumFigures()).thenReturn(3);

        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(false, abandonedShip.processAbandonedShipChoice(player1));
        assertEquals(true, abandonedShip.processAbandonedShipChoice(player2));
    }

    @Test
    void processAbandonedShipChoice1() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        ShipBoard shipBoard1 = mock(ShipBoard.class);
        ShipBoard shipBoard2=mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);

        when(shipBoard1.getNumFigures()).thenReturn(2);
        when(shipBoard2.getNumFigures()).thenReturn(2);
        when(shipBoard1.getHasBrownAlien()).thenReturn(true);
        when(shipBoard2.getHasPurpleAlien()).thenReturn(true);

        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(true, abandonedShip.processAbandonedShipChoice(player1));
        assertEquals(true, abandonedShip.processAbandonedShipChoice(player2));
    }

    @Test
    void getCardType() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(CardName.ABANDONED_SHIP, abandonedShip.getCardType());
    }

    @Test
    void getLevel() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(1, abandonedShip.getLevel());
    }

    @Test
    void getSide() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(0, abandonedShip.getSide());
    }

    @Test
    void setCardSide() {
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);
        assertEquals(0, abandonedShip.getSide());
        abandonedShip.setCardSide(1);
        assertEquals(1, abandonedShip.getSide(), "The side of the ship should be set to 1");
        abandonedShip.setCardSide(0);
        assertEquals(0, abandonedShip.getSide(), "The side of the ship should be set to a 0");
    }


    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        // Create card
        AbandonedShip abandonedShip = new AbandonedShip(CardName.ABANDONED_SHIP, 1, "abandoned_station_url.jpg", 3,1,4);

        // Stub methods
        when(player1.getId()).thenReturn("id1");
        when(player2.getId()).thenReturn("id2");
        when(player3.getId()).thenReturn("id3");
        when(player1.getNickname()).thenReturn("Player1");

        // players list and retired players list
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(player1, player2, player3));
        ArrayList<Player> retiredPlayers = new ArrayList<>(new ArrayList<>(Arrays.asList( player2)));

        when(gameModel.getPlayers()).thenReturn(players);
        when(gameModel.getPlayerInTurn()).thenReturn(player1);
        when(gameModel.getRetiredPlayers()).thenReturn(retiredPlayers);
        when(gameModel.getPlayersPosition()).thenReturn(new ArrayList<>(Arrays.asList(player1, player2, player3)));

        // Act
        abandonedShip.onPickUp(gameModel, sender);

        // Verify player in turn was set
        verify(gameModel).setPlayerInTurn(player1);

        // Capture messages sent
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeast(1)).accept(messageCaptor.capture());

        List<Message> capturedMessages = messageCaptor.getAllValues();

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && (m).getClientId().equals("id1")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage dm && dm.getClientId().equals("id1") && dm.isInTurn()));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && (m).getClientId().equals("id3")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage dm && dm.getClientId().equals("id3")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && (m).getClientId().equals("id2")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage2 dm2 && dm2.getClientId().equals("id2")));

        // Verify game state update
        verify(gameModel).setGameState(GameState.ABANDONED_SHIP);
    }

}