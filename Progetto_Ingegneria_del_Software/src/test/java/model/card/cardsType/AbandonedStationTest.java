
package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;
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

class AbandonedStationTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNumOfFigureRequired() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(5, abandonedStation.getNumOfFigureRequired());
    }

    @Test
    void getLoseFlightDays() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(1, abandonedStation.getLoseFlightDays());
    }

    @Test
    void getColorOfGoodsTaken() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(goodsColor, abandonedStation.getColorOfGoodsTaken());
    }

    @Test
    void processAbandonedStationChoice() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        ShipBoard shipBoard1 = mock(ShipBoard.class);
        ShipBoard shipBoard2 = mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);
        when(player1.getShipBoard().getNumFigures()).thenReturn(4);
        when(player2.getShipBoard().getNumFigures()).thenReturn(6);

        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(false, abandonedStation.processAbandonedStationChoice(player1));
        assertEquals(true, abandonedStation.processAbandonedStationChoice(player2));
    }

    @Test
    void processAbandonedStationChoice1() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);

        ShipBoard shipBoard1 = mock(ShipBoard.class);
        ShipBoard shipBoard2 = mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);
        when(player1.getShipBoard().getNumFigures()).thenReturn(4);
        when(player2.getShipBoard().getNumFigures()).thenReturn(4);
        when(player1.getShipBoard().getHasBrownAlien()).thenReturn(true);
        when(player2.getShipBoard().getHasPurpleAlien()).thenReturn(true);

        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(true, abandonedStation.processAbandonedStationChoice(player1));
        assertEquals(true, abandonedStation.processAbandonedStationChoice(player2));
    }

    @Test
    void getCardType() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(CardName.ABANDONED_STATION, abandonedStation.getCardType());
    }

    @Test
    void getLevel() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(1, abandonedStation.getLevel());
    }

    @Test
    void getSide() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(0, abandonedStation.getSide());
    }

    @Test
    void setCardSide() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 5, 1, goodsColor);
        assertEquals(0, abandonedStation.getSide());
        abandonedStation.setCardSide(1);
        assertEquals(1, abandonedStation.getSide());
        abandonedStation.setCardSide(0);
        assertEquals(0, abandonedStation.getSide());
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        // Create card
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.YELLOW, Color.GREEN));
        AbandonedStation abandonedStation = new AbandonedStation(CardName.ABANDONED_STATION, 1, "abandoned_station_url.jpg", 3,1, goodsColor);

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
        abandonedStation.onPickUp(gameModel, sender);

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
                m instanceof DrawnCardMessage dm && dm.getClientId().equals("id1")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && ( m).getClientId().equals("id2")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage2 dm2 && dm2.getClientId().equals("id2")));

        // Verify game state update
        verify(gameModel).setGameState(GameState.ABANDONED_STATION);
    }
}
