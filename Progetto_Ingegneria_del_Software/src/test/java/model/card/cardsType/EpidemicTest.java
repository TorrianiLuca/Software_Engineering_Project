package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import network.messages.*;
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

class EpidemicTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCardType() {
        Epidemic epidemic = new Epidemic(CardName.EPIDEMIC, 2, "epidemic.jpg");
        assertEquals(CardName.EPIDEMIC, epidemic.getCardType(), "The type of the card should be EPIDEMIC");
    }

    @Test
    void getLevel() {
        Epidemic epidemic = new Epidemic(CardName.EPIDEMIC, 1, "epidemic_url.jpg");
        assertEquals(1, epidemic.getLevel());
        epidemic = new Epidemic(CardName.EPIDEMIC, 2, "planets_url.jpg");
        assertEquals(2, epidemic.getLevel());
    }

    @Test
    void getSide() {
        Epidemic epidemic = new Epidemic(CardName.EPIDEMIC, 1, "epidemic_url.jpg");
        assertEquals(0, epidemic.getSide());
    }

    @Test
    void setCardSide() {
        Epidemic epidemic = new Epidemic(CardName.EPIDEMIC, 1, "epidemic_url.jpg");
        assertEquals(0, epidemic.getSide());
        epidemic.setCardSide(1);
        assertEquals(1, epidemic.getSide(), "The side of the card should be set to 1");
        epidemic.setCardSide(0);
        assertEquals(0, epidemic.getSide(), "The side of the card should be set to 0");
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        ShipBoard shipBoard1 = mock(ShipBoard.class);
        ShipBoard shipBoard2 = mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);
        FlightBoard flightBoard = mock(FlightBoard.class);

        // Create card
        Epidemic epidemic = new Epidemic(CardName.EPIDEMIC, 1, "epidemic_url.jpg");

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

        // Act
        epidemic.onPickUp(gameModel, sender);

        ArgumentCaptor<Message> messageCaptor1 = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeastOnce()).accept(messageCaptor1.capture());

        List<Message> messages = messageCaptor1.getAllValues();
        assertTrue(messages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage dm && dm.getClientId().equals("Player1") && dm.isInTurn()));


        verify(shipBoard1).applyEpidemic();
        verify(shipBoard2).applyEpidemic();
        verify(shipBoard1).removeEpidemicFigures();
        verify(shipBoard2).removeEpidemicFigures();

        assertTrue(messages.stream().anyMatch(m ->
                m instanceof UpdateParametresMessage && (m).getClientId().equals("id1")));
        assertTrue(messages.stream().anyMatch(m -> m instanceof UpdateParametresMessage && (m).getClientId().equals("id2")));

        verify(gameModel).refreshPlayersPosition();

        verify(gameModel).setPlayerInTurn(player1);

        // Capture messages sent
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeast(1)).accept(messageCaptor.capture());

        List<Message> capturedMessages = messageCaptor.getAllValues();

        // Check that messages were sent to both players
        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof ChangeTuiStateMessage && (m).getClientId().equals("id1")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof ShowShipBoardMessage dm && dm.getClientId().equals("id1")));

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