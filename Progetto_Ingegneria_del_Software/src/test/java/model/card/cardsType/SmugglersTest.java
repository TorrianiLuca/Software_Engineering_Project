package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
import enumerations.GameState;
import model.GameModel;
import model.flightBoard.FlightBoard;
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

class SmugglersTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getEnemyStrength() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(4, smugglers.getEnemyStrength());
    }

    @Test
    void getGoodsLose() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(2, smugglers.getGoodsLose());
    }

    @Test
    void getLoseFlightDays() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(1, smugglers.getLoseFlightDays());
    }

    @Test
    void getColorOfGoodsTaken() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(goodsColor, smugglers.getColorOfGoodsTaken());
    }

    @Test
    void getCardType() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(CardName.SMUGGLERS, smugglers.getCardType(), "The type of the card should be SMUGGLERS");

        smugglers = new Smugglers(CardName.STARDUST, 1, "stardust_url.jpg", 4,2, 1, goodsColor);
        assertNotEquals(CardName.SMUGGLERS, smugglers.getCardType(), "The type of the card shouldn't be SMUGGLERS");
    }

    @Test
    void getLevel() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(1, smugglers.getLevel());

        smugglers = new Smugglers(CardName.SMUGGLERS, 2, "smugglers_url.jpg", 4,2, 1, goodsColor);
        assertEquals(2, smugglers.getLevel());

    }

    @Test
    void getSide() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(0, smugglers.getSide());
    }

    @Test
    void setCardSide() {
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 4, 2, 1, goodsColor);
        assertEquals(0, smugglers.getSide());
        smugglers.setCardSide(1);
        assertEquals(1, smugglers.getSide(), "The side of the card should be set to 1");
        smugglers.setCardSide(0);
        assertEquals(0, smugglers.getSide(), "The side of the card should be set to 0");
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3= mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        // Create card
        ArrayList<Color> goodsColor = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.GREEN));
        Smugglers smugglers = new Smugglers(CardName.SMUGGLERS, 1, "smugglers_url.jpg", 3,1,4, goodsColor);

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
        smugglers.onPickUp(gameModel, sender);

        // Verify player in turn was set
        verify(gameModel).setPlayerInTurn(player1);

        // Capture messages sent
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeast(1)).accept(messageCaptor.capture());

        List<Message> capturedMessages = messageCaptor.getAllValues();

        // Check that messages were sent to both players
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
        verify(gameModel).setGameState(GameState.SMUGGLERS);
    }
}