
package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
import enumerations.GameState;
import model.GameModel;
import model.player.Player;
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

class SlaversTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getEnemyStrength() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(7, slavers.getEnemyStrength());
    }

    @Test
    void getNumOfLoseFigures() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(4, slavers.getNumOfLoseFigures());
    }

    @Test
    void getLoseFlightDays() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(2, slavers.getLoseFlightDays());
    }

    @Test
    void getNumOfCreditsTaken() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(8, slavers.getNumOfCreditsTaken());
    }

    @Test
    void getCardType() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(CardName.SLAVERS, slavers.getCardType(), "The type of the card should be SLAVERS");
    }

    @Test
    void getLevel() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(2, slavers.getLevel());
    }

    @Test
    void getSide() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(0, slavers.getSide());
    }

    @Test
    void setCardSide() {
        Slavers slavers = new Slavers(CardName.SLAVERS, 2, "slavers_url.jpg", 7, 4, 2, 8);
        assertEquals(0, slavers.getSide());
        slavers.setCardSide(1);
        assertEquals(1, slavers.getSide(), "The side of the card should be set to 1");
        slavers.setCardSide(0);
        assertEquals(0, slavers.getSide(), "The side of the card should be set to 0");
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        // Create card
        Slavers slavers = new Slavers(CardName.SLAVERS, 1, "slavers_url.jpg", 3,1,4, 5);

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
        slavers.onPickUp(gameModel, sender);

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
        verify(gameModel).setGameState(GameState.SLAVERS);
    }
}
