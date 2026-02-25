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

class PlanetsTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getLoseFlightDays() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(2, planets.getLoseFlightDays());
    }

    @Test
    void getNumberOfPlanets() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(3, planets.getNumberOfPlanets());
    }

    @Test
    void getAllPlanetsGoods() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(planetsGoods, planets.getAllPlanetsGoods());
    }

    @Test
    void choosePlanetGoods() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(color1, planets.choosePlanetGoods(1));
        assertEquals(color2, planets.choosePlanetGoods(2));
        assertEquals(color3, planets.choosePlanetGoods(3));
    }

    @Test
    void getNumOccupiedPlanets() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(0, planets.getNumOccupiedPlanets());
        planets.incrementNumOccupiedPlanets();
        assertEquals(1, planets.getNumOccupiedPlanets());
    }

    @Test
    void incrementNumOccupiedPlanets() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(0, planets.getNumOccupiedPlanets());
        planets.incrementNumOccupiedPlanets();
        assertEquals(1, planets.getNumOccupiedPlanets());
        planets.incrementNumOccupiedPlanets();
        assertEquals(2, planets.getNumOccupiedPlanets());
    }

    @Test
    void processPlanetChoice() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));
        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        Player player1=mock(Player.class);
        Player player2=mock(Player.class);
        Player player3=mock(Player.class);

        boolean value = planets.processPlanetChoice(player1, -1);
        assertFalse(value);
        value = planets.processPlanetChoice(player1, 2);
        assertTrue(value);
        value = planets.processPlanetChoice(player1, 3);
        assertFalse(value);
        value = planets.processPlanetChoice(player2, 3);
        assertTrue(value);
        value = planets.processPlanetChoice(player3, 3);
        assertFalse(value);
        value = planets.processPlanetChoice(player3, 1);
        assertTrue(value);
    }

    @Test
    void numOfPlanet() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));
        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        Player player1=mock(Player.class);
        when(player1.getNickname()).thenReturn("Player1");
        Player player2=mock(Player.class);
        when(player2.getNickname()).thenReturn("Player2");
        Player player3=mock(Player.class);
        when(player3.getNickname()).thenReturn("Player3");

        planets.processPlanetChoice(player1, 3);
        planets.processPlanetChoice(player2, 1);
        planets.processPlanetChoice(player3, 2);


        int num = planets.numOfPlanet("Player1");
        assertEquals(3, num);
        num = planets.numOfPlanet("Player2");
        assertEquals(1, num);
        num = planets.numOfPlanet("Player3");
        assertEquals(2, num);
        num = planets.numOfPlanet("Player4");
        assertEquals(0, num);
    }

    @Test
    void getCardType() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 2, "planets.jpg", 2, planetsGoods);
        assertEquals(CardName.PLANETS, planets.getCardType(), "The type of the card should be PLANETS");
    }

    @Test
    void getLevel() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 1, "planets_url.jpg", 2, planetsGoods);
        assertEquals(1, planets.getLevel());
        planets = new Planets(CardName.PLANETS, 2, "planets_url.jpg", 2, planetsGoods);
        assertEquals(2, planets.getLevel());
    }

    @Test
    void getSide() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 1, "planets_url.jpg", 2, planetsGoods);
        assertEquals(0, planets.getSide());
    }

    @Test
    void setCardSide() {
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<Color> color3 = new ArrayList<>(Arrays.asList(Color.YELLOW));
        ArrayList<ArrayList<Color>> planetsGoods = new ArrayList<>(Arrays.asList(color1, color2, color3));

        Planets planets = new Planets(CardName.PLANETS, 1, "planets_url.jpg", 2, planetsGoods);
        assertEquals(0, planets.getSide());
        planets.setCardSide(1);
        assertEquals(1, planets.getSide(), "The side of the card should be set to 1");
        planets.setCardSide(0);
        assertEquals(0, planets.getSide(), "The side of the card should be set to 0");
    }

    @Test
    void onPickUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        Player player3= mock(Player.class);
        Consumer<Message> sender = mock(Consumer.class);
        GameModel gameModel = mock(GameModel.class);

        // Create card
        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<ArrayList<Color>> goodColors = new ArrayList<>(Arrays.asList(color1, color2));
        Planets planets = new Planets(CardName.PLANETS, 1, "planets_url.jpg", 3,goodColors);

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
        planets.onPickUp(gameModel, sender);

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
        verify(gameModel).setGameState(GameState.PLANETS);
    }

    @Test
    void playerOnPlanet() {
        Player player1 = mock(Player.class);
        when(player1.getColor()).thenReturn(Color.RED);


        ArrayList<Color> color1 = new ArrayList<>(Arrays.asList(Color.RED, Color.RED));
        ArrayList<Color> color2 = new ArrayList<>(Arrays.asList(Color.RED, Color.BLUE, Color.BLUE));
        ArrayList<ArrayList<Color>> goodColors = new ArrayList<>(Arrays.asList(color1, color2));
        Planets planets = new Planets(CardName.PLANETS, 1, "planets_url.jpg", 3,goodColors);

        boolean planetLand = planets.processPlanetChoice(player1, 2);
        assertEquals(Color.RED, planets.playerOnPlanet(2));

        assertNull(planets.playerOnPlanet(1));
    }
}