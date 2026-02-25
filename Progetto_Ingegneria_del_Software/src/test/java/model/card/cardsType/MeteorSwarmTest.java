
package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.cardsType.ForReadJson.Meteor;
import model.player.Player;
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

class MeteorSwarmTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getMeteor() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        ArrayList<Meteor> meteors = new ArrayList<>(Arrays.asList(meteor1, meteor2, meteor3));
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", meteors);
        assertEquals(meteors, meteorSwarm.getMeteor());
    }

    @Test
    void getCounter() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3)));
        assertEquals(0, meteorSwarm.getCounter());
    }

    @Test
    void incrementCounter() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3)));
        assertEquals(0, meteorSwarm.getCounter());
        meteorSwarm.incrementCounter();
        assertEquals(1, meteorSwarm.getCounter());
    }

    @Test
    void getCardType() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3)));
        assertEquals(CardName.METEOR_SWARM, meteorSwarm.getCardType());
    }

    @Test
    void getLevel() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3)));
        assertEquals(1, meteorSwarm.getLevel());
    }

    @Test
    void getSide() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3)));
        assertEquals(0, meteorSwarm.getSide());
    }

    @Test
    void setCardSide() {
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Meteor meteor3 = mock(Meteor.class);
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "meteor_swarm.jpg", new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3)));
        assertEquals(0, meteorSwarm.getSide());
        meteorSwarm.setCardSide(1);
        assertEquals(1, meteorSwarm.getSide(), "The side of the card should be set to 1");
        meteorSwarm.setCardSide(0);
        assertEquals(0, meteorSwarm.getSide(), "The side of the card should be set to 0");
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3 = mock(Player.class);
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        // Create card
        ArrayList<Meteor> meteors = new ArrayList<>(Arrays.asList(meteor1, meteor2));
        MeteorSwarm meteorSwarm = new MeteorSwarm(CardName.METEOR_SWARM, 1, "abandoned_station_url.jpg", meteors);

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
        meteorSwarm.onPickUp(gameModel, sender);

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
                m instanceof AskRollDiceMessage dm && dm.getClientId().equals("id1")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && (m).getClientId().equals("id3")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof AskRollDiceMessage dm && dm.getClientId().equals("id3")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && (m).getClientId().equals("id2")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage2 dm2 && dm2.getClientId().equals("id2")));

        // Verify game state update
        verify(gameModel).setGameState(GameState.ROLL_DICE);
    }
}
