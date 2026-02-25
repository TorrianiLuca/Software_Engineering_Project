
package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.cardsType.ForReadJson.Meteor;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class CombatZoneTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCounter() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(0, combatZone.getCounter());
    }

    @Test
    void incrementCounter() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(0, combatZone.getCounter());
        combatZone.incrementCounter();
        assertEquals(1, combatZone.getCounter());
        combatZone.incrementCounter();
        assertEquals(2, combatZone.getCounter());
    }

    @Test
    void getFaseOne() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(object1, combatZone.getFaseOne());
    }

    @Test
    void getFaseTwo() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(object2, combatZone.getFaseTwo());
    }

    @Test
    void getFaseThree() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(object3, combatZone.getFaseThree());
    }

    @Test
    void getCardType() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(CardName.COMBAT_ZONE, combatZone.getCardType());
    }

    @Test
    void getLevel() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(2, combatZone.getLevel());
    }

    @Test
    void getSide() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(0, combatZone.getSide());
    }

    @Test
    void setCardSide() {
        Object[] object1 = new Object[]{};
        Object[] object2 = new Object[]{};
        Object[] object3 = new Object[]{};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 2, "combat_zone.jpg", object1, object2, object3);
        assertEquals(0, combatZone.getSide());
        combatZone.setCardSide(1);
        assertEquals(1, combatZone.getSide(), "The side of the ship should be set to 1");
        combatZone.setCardSide(0);
        assertEquals(0, combatZone.getSide(), "The side of the ship should be set to 0");
    }


    @Test
    void onPickUp0() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);
        ShipBoard shipBoard1=mock(ShipBoard.class);
        ShipBoard shipBoard2=mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);
        FlightBoard flightBoard1=mock(FlightBoard.class);
        when(gameModel.getFlightBoard()).thenReturn(flightBoard1);

        // Create card
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        ArrayList<Meteor> meteors = new ArrayList<>();
        meteors.add(meteor1);
        meteors.add(meteor2);
        Object[] object1 = new Object[]{"equipment", "lost_flight_days", 3};
        Object[] object2 = new Object[]{"power", "equipment", 2};
        Object[] object3 = new Object[]{"cannon", "meteor", meteors};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 1, "combat_zone_url.jpg", object1, object2, object3);

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
        combatZone.onPickUp(gameModel, sender);

        // Verify player in turn was set
        verify(gameModel).setPlayerInTurn(player1);

        // Capture messages sent
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeast(1)).accept(messageCaptor.capture());

        List<Message> capturedMessages = messageCaptor.getAllValues();

        // Check that messages were sent to both players
        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && ((SetCardInUseMessage) m).getClientId().equals("id1")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage dm && dm.getClientId().equals("id1") && dm.isInTurn()));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof SetCardInUseMessage && ((SetCardInUseMessage) m).getClientId().equals("id2")));

        assertTrue(capturedMessages.stream().anyMatch(m ->
                m instanceof DrawnCardMessage2 dm2 && dm2.getClientId().equals("id2")));

    }

    @Test
    void onPickUp1() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);
        ShipBoard shipBoard1=mock(ShipBoard.class);
        ShipBoard shipBoard2=mock(ShipBoard.class);
        when(player1.getShipBoard()).thenReturn(shipBoard1);
        when(player2.getShipBoard()).thenReturn(shipBoard2);
        FlightBoard flightBoard1=mock(FlightBoard.class);
        when(gameModel.getFlightBoard()).thenReturn(flightBoard1);

        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        ArrayList<Meteor> meteors = new ArrayList<>();
        meteors.add(meteor1);
        meteors.add(meteor2);
        Object[] object1 = new Object[]{"equipment", "lost_flight_days", 3};
        Object[] object2 = new Object[]{"power", "equipment", 2};
        Object[] object3 = new Object[]{"cannon", "meteor", meteors};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 1, "combat_zone_url.jpg", object1, object2, object3);

        // Stub methods
        when(player1.getId()).thenReturn("id1");
        when(player2.getId()).thenReturn("id2");
        when(player1.getNickname()).thenReturn("Player1");
        when(gameModel.getPlayerInTurn()).thenReturn(player1);

        when(shipBoard1.getNumFigures()).thenReturn(1);
        when(shipBoard1.getHasPurpleAlien()).thenReturn(false);
        when(shipBoard1.getHasBrownAlien()).thenReturn(false);

        when(shipBoard2.getNumFigures()).thenReturn(2);
        when(shipBoard2.getHasPurpleAlien()).thenReturn(true);
        when(shipBoard2.getHasBrownAlien()).thenReturn(false);

        ArrayList<Player> players = new ArrayList<>(Arrays.asList(player1, player2));
        ArrayList<Player> retiredPlayers = new ArrayList<>();
        when(gameModel.getPlayers()).thenReturn(players);
        when(gameModel.getPlayersPosition()).thenReturn(players);
        when(gameModel.getRetiredPlayers()).thenReturn(retiredPlayers);

        combatZone.onPickUp(gameModel, sender);

        verify(flightBoard1).movePlayerBackward(eq(player1), eq(3));

        verify(gameModel).setGameState(GameState.FASE_2);

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(sender, atLeast(1)).accept(captor.capture());

        List<Message> messages = captor.getAllValues();

        assertTrue(messages.stream().anyMatch(m ->
                m instanceof ProceedNextPhaseMessage && (m).getClientId().equals("id1")));
        assertTrue(messages.stream().anyMatch(m ->
                m instanceof ProceedNextPhaseMessage && (m).getClientId().equals("id2")));
        assertTrue(messages.stream().anyMatch(m ->
                m instanceof UpdateParametresMessage && (m).getClientId().equals("id1")));
        assertTrue(messages.stream().anyMatch(m ->
                m instanceof UpdateParametresMessage && (m).getClientId().equals("id2")));

        verify(gameModel).setPlayerInTurn(player1);

    }

    @Test
    void incrementFaseCounter(){
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        ArrayList<Meteor> meteors = new ArrayList<>();
        meteors.add(meteor1);
        meteors.add(meteor2);
        Object[] object1 = new Object[]{"equipment", "lost_flight_days", 3};
        Object[] object2 = new Object[]{"power", "equipment", 2};
        Object[] object3 = new Object[]{"cannon", "meteor", meteors};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 1, "combat_zone_url.jpg", object1, object2, object3);
        combatZone.incrementFaseCounter();
        assertEquals(2,combatZone.getFaseCounter());
    }

    @Test
    void getFaseCounter(){
        Meteor meteor1 = mock(Meteor.class);
        Meteor meteor2 = mock(Meteor.class);
        ArrayList<Meteor> meteors = new ArrayList<>();
        meteors.add(meteor1);
        meteors.add(meteor2);
        Object[] object1 = new Object[]{"equipment", "lost_flight_days", 3};
        Object[] object2 = new Object[]{"power", "equipment", 2};
        Object[] object3 = new Object[]{"cannon", "meteor", meteors};
        CombatZone combatZone = new CombatZone(CardName.COMBAT_ZONE, 1, "combat_zone_url.jpg", object1, object2, object3);

        assertEquals(1, combatZone.getFaseCounter());
        combatZone.incrementFaseCounter();
        assertEquals(2,combatZone.getFaseCounter());
    }
}
