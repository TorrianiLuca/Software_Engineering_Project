
package model.card.cardsType;

import enumerations.CardName;
import enumerations.Color;
import enumerations.GameState;
import model.GameModel;
import model.card.cardsType.ForReadJson.Meteor;
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

class PiratesTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCounter() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(0, pirates.getCounter());
        pirates.incrementCounter();
        assertEquals(1, pirates.getCounter());
    }

    @Test
    void incrementCounter() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(0, pirates.getCounter());
        pirates.incrementCounter();
        assertEquals(1, pirates.getCounter());
    }

    @Test
    void getEnemyStrength() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(10, pirates.getEnemyStrength());
    }

    @Test
    void getShotsPowerArray() {
        ArrayList<Integer> power = new ArrayList<Integer>(Arrays.asList(2,1,2));
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, power,2,12);
        assertEquals(power, pirates.getShotsPowerArray());
    }

    @Test
    void getLoseFlightDays() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(2, pirates.getLoseFlightDays());
    }

    @Test
    void getNumOfCreditsTaken() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(12, pirates.getNumOfCreditsTaken());
    }

    @Test
    void getCardType() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(CardName.PIRATES, pirates.getCardType(), "The type of the card should be PIRATES");
    }

    @Test
    void getLevel() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(2, pirates.getLevel());

    }

    @Test
    void getSide() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(0, pirates.getSide(), "The side of the card should be set to 0");

    }

    @Test
    void setCardSide() {
        Pirates pirates = new Pirates(CardName.PIRATES, 2, "pirates_url.jpg", 10, new ArrayList<Integer>(Arrays.asList(2,1,2)),2,12);
        assertEquals(0, pirates.getSide());
        pirates.setCardSide(1);
        assertEquals(1, pirates.getSide(), "The side of the card should be set to 1");
        pirates.setCardSide(0);
        assertEquals(0, pirates.getSide(), "The side of the card should be set to 0");
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        ArrayList<Integer> shots = new ArrayList<>(Arrays.asList(2,1,2));
        Pirates pirates = new Pirates(CardName.PIRATES, 1, "pirates_url.jpg", 3,shots,4, 5);

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
        pirates.onPickUp(gameModel, sender);

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
        verify(gameModel).setGameState(GameState.PIRATES);
    }
}
