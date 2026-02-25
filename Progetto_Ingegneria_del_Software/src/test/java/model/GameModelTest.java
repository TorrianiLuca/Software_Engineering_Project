package model;

import enumerations.*;
import model.card.Card;
import model.card.CardPile;
import model.card.CardsDeck;
import model.card.cardsType.OpenSpace;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.shipBoard.ShipBoardSpace;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.tiles.componentTile.Cabine;
import model.tiles.componentTile.Cannon;
import network.messages.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.Couple;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameModelTest {
    GameModel gameModel;
    Player[] players;
    ComponentTile[] tiles;
    ShipBoardSpace shipBoardSpace;
    ShipBoard shipBoard;

    @BeforeEach
    void setUp() {
        gameModel = new GameModel();
        shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, Color.RED);

        players = new Player[3];
        players[0] = new Player("player1", "11", Color.RED,FlightType.FIRST_FLIGHT);
        players[1] = new Player("player2","11", Color.BLUE,FlightType.FIRST_FLIGHT);
        players[2] = new Player("player3","11", Color.GREEN,FlightType.FIRST_FLIGHT);

        tiles = new ComponentTile[3];
        tiles[0] = new ComponentTile(TileName.CANNON, "aaa", 2, 3, 4, 5) {};
        tiles[1] = new ComponentTile(TileName.BATTERY, "bbb", 1, 3, 4, 3) {};

        gameModel.setFlightType(FlightType.FIRST_FLIGHT);
        gameModel.setCardsToPlay();
        gameModel.setCardsPiles();
    }

    @Test
    void setNickname(){
        players[0].setNickname("player1");
        players[1].setNickname("player2");
        players[2].setNickname("player3");
    }

    @Test
    void getNickname() {
        assertEquals("player1", players[0].getNickname());
        assertEquals("player2", players[1].getNickname());
        assertEquals("player3", players[2].getNickname());
    }

    @Test
    void addPlayer0() {
        gameModel.setMaxPlayers(3);
        ArrayList<Player> ListOfplayers;

        gameModel.addPlayer(players[0]);
        ListOfplayers = gameModel.getPlayers();
        assertEquals(1, ListOfplayers.size(),"i expect player's list is 1 now");

        gameModel.addPlayer(players[1]);
        ListOfplayers = gameModel.getPlayers();
        assertEquals(2, ListOfplayers.size(),"i expect player's list is 2 now");

        gameModel.addPlayer(players[2]);
        ListOfplayers = gameModel.getPlayers();
        assertEquals(3, ListOfplayers.size(),"i expect player's list is 3 now");

    }

    @Test
    void addPlayer1() {
        gameModel.setMaxPlayers(3);
        Player player=mock(Player.class);
        when(player.getNickname()).thenReturn(null);

        assertThrows(NullPointerException.class, () -> {gameModel.addPlayer(player);});
    }

    @Test
    void isBound(){
        assertTrue(gameModel.isInBound(3));
        assertTrue(gameModel.isInBound(4));
        assertFalse(gameModel.isInBound(5));
        assertFalse(gameModel.isInBound(0));
    }

    @Test
    void getMaxPlayers(){
        gameModel.setMaxPlayers(3);
        assertEquals(3, gameModel.getMaxPlayers());
    }

    @Test
    void getFlightType(){
        gameModel.setFlightType(FlightType.FIRST_FLIGHT);
        assertEquals(FlightType.FIRST_FLIGHT, gameModel.getFlightType());
    }

    @Test
    void setGameState(){
        gameModel.setGameState(GameState.ROLL_DICE);
        assertEquals(GameState.ROLL_DICE, gameModel.getGameState());
    }

    @Test
    void setGameTile(){
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);
        gameModel.setGameTile();
        assertNotNull(gameModel.getGameTile());
        assertEquals(FlightType.STANDARD_FLIGHT, gameModel.getGameTile().getFlightType());
    }

    @Test
    void setFlightBoard(){
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);
        gameModel.setFlightBoard();
        assertNotNull(gameModel.getFlightBoard());
    }

    @Test
    void setCardsPiles(){
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);
        gameModel.setCardsPiles();

        assertEquals(4, gameModel.getCardsPile().size());

        for (CardPile pile : gameModel.getCardsPile()) {
            assertEquals(3, pile.getCards().size());
        }
    }

    @Test
    void setCardsToPlay(){
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);

        OpenSpace openSpace1 = new OpenSpace(CardName.OPEN_SPACE, 2, "00");
        OpenSpace openSpace2 = new OpenSpace(CardName.OPEN_SPACE, 2, "00");
        OpenSpace openSpace3 = new OpenSpace(CardName.OPEN_SPACE, 2, "00");
        ArrayList<Card> cardsToPlay = new ArrayList<>();
        cardsToPlay.add(openSpace1);
        cardsToPlay.add(openSpace2);
        cardsToPlay.add(openSpace3);
        CardPile pile1 = new CardPile(1, cardsToPlay);
        gameModel.getCardsPile().add(pile1);

        gameModel.setCardsToPlay();

        assertTrue(gameModel.getCardsToPlay().contains(openSpace1));
        assertTrue(gameModel.getCardsToPlay().contains(openSpace2));
        assertTrue(gameModel.getCardsToPlay().contains(openSpace3));

    }

    @Test
    void removeCardFromPlay(){
        gameModel.setFlightType(FlightType.FIRST_FLIGHT);
        gameModel.setCardsToPlay();
        int startSize = gameModel.getCardsToPlay().size();
        gameModel.removeCardFromPlay();

        assertEquals(startSize-1,gameModel.getCardsToPlay().size());
    }

    @Test
    void setPlayerInTurn(){
        Player player=mock(Player.class);
        gameModel.setPlayerInTurn(player);
        assertEquals(player,gameModel.getPlayerInTurn());
    }

    @Test
    void addDefeatedPlayers(){
        Player player=mock(Player.class);
        gameModel.addDefeatedPlayers(player);
        assertEquals(1,gameModel.getDefeatedPlayers().size());
    }

    @Test
    void refreshPlayersPosition(){
        gameModel.setFlightBoard();
        FlightBoard flightBoard = gameModel.getFlightBoard();

        Player p1 = mock(Player.class);
        Player p2 = mock(Player.class);
        Player p3 = mock(Player.class);

        Couple<Integer, Integer> posLap1 = new Couple<>(0, 2);
        Couple<Integer, Integer> posLap2 = new Couple<>(0, 3);
        Couple<Integer, Integer> posLap3 = new Couple<>(0, 4);  // This should be the player in turn


        flightBoard.getPlayersMap().put(p1, posLap1);
        flightBoard.getPlayersMap().put(p2, posLap2);
        flightBoard.getPlayersMap().put(p3, posLap3);

        gameModel.refreshPlayersPosition();

        ArrayList<Player> newPositions=new ArrayList<>();
        newPositions.add(p3);
        newPositions.add(p2);
        newPositions.add(p1);

        assertEquals(p3, gameModel.getPlayerInTurn());
        assertEquals(newPositions,gameModel.getPlayersPosition());
        assertEquals(p2, gameModel.getNextPlayer());
    }

    @Test
    void getNumTiles(){
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);
        assertEquals(152,gameModel.getNumTiles());

        gameModel.setFlightType(FlightType.FIRST_FLIGHT);
        assertEquals(140,gameModel.getNumTiles());
    }


    @Test
    void pickTile0(){
        GameTile gameTile=mock(GameTile.class);
        gameModel.setGameTileTest(gameTile);
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);
        ComponentTile componentTile=mock(ComponentTile.class);
        when(gameTile.getComponentTile(5)).thenReturn(componentTile);
        when(componentTile.tryOccupy()).thenReturn(true, false);

        assertEquals(componentTile,gameModel.pickTile(5));
    }

    @Test
    void pickTile1(){
        GameTile gameTile=mock(GameTile.class);
        gameModel.setGameTileTest(gameTile);
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);
        ComponentTile componentTile=mock(ComponentTile.class);
        when(gameTile.getComponentTile(5)).thenReturn(componentTile);
        when(componentTile.tryOccupy()).thenReturn(false, true);

        assertNull(gameModel.pickTile(5));
    }

    @Test
    void pickTile2(){
        GameTile gameTile=mock(GameTile.class);
        gameModel.setGameTileTest(gameTile);
        gameModel.setFlightType(FlightType.STANDARD_FLIGHT);

        assertNull(gameModel.pickTile(5));
    }

    @Test
    void putTileBack0(){
        ComponentTile componentTile=mock(ComponentTile.class);
        when(componentTile.tryRelease()).thenReturn(false, true);
        assertFalse(gameModel.putTileBack(componentTile));
    }

    @Test
    void putTileBack1(){
        ComponentTile componentTile=mock(ComponentTile.class);
        when(componentTile.tryRelease()).thenReturn(true, false);
        assertTrue(gameModel.putTileBack(componentTile));
    }

    @Test
    void addTempPosition(){
        Player player1=mock(Player.class);
        Player player2=mock(Player.class);
        gameModel.addTempPosition(player1);
        gameModel.addTempPosition(player2);

        assertTrue(gameModel.getTempPositions().contains(player1));
        assertTrue(gameModel.getTempPositions().contains(player2));
    }

    @Test
    void removeTempPosition(){
        Player player1=mock(Player.class);
        Player player2=mock(Player.class);
        gameModel.addTempPosition(player1);
        gameModel.addTempPosition(player2);

        assertTrue(gameModel.getTempPositions().contains(player1));
        assertTrue(gameModel.getTempPositions().contains(player2));

        gameModel.removeTempPosition(player1);
        gameModel.removeTempPosition(player2);
        assertFalse(gameModel.getTempPositions().contains(player1));
        assertFalse(gameModel.getTempPositions().contains(player2));
    }

    @Test
    void pickCardPile0(){
        CardPile cardPile=mock(CardPile.class);
        ArrayList<CardPile> cardPiles=new ArrayList<>();
        cardPiles.add(cardPile);
        when(cardPile.tryObserved()).thenReturn(true);
        gameModel.setCardsPileTest(cardPiles);

        assertEquals(cardPile, gameModel.pickCardPile(1));
    }

    @Test
    void pickCardPile1(){
        CardPile cardPile=mock(CardPile.class);
        ArrayList<CardPile> cardPiles=new ArrayList<>();
        cardPiles.add(cardPile);
        when(cardPile.tryObserved()).thenReturn(false);
        gameModel.setCardsPileTest(cardPiles);

        assertNull(gameModel.pickCardPile(1));
    }

    @Test
    void pickCardPileBack0(){
        CardPile cardPile=mock(CardPile.class);
        ArrayList<CardPile> cardPiles=new ArrayList<>();
        cardPiles.add(cardPile);
        when(cardPile.tryRelease()).thenReturn(false);
        gameModel.setCardsPileTest(cardPiles);

        assertFalse(gameModel.putCardPileBack(cardPile));
    }

    @Test
    void pickCardPileBack1(){
        CardPile cardPile=mock(CardPile.class);
        ArrayList<CardPile> cardPiles=new ArrayList<>();
        cardPiles.add(cardPile);
        when(cardPile.tryRelease()).thenReturn(true);
        gameModel.setCardsPileTest(cardPiles);

        assertTrue(gameModel.putCardPileBack(cardPile));
    }

    @Test
    void incrementConnectedPlayers(){
        gameModel.incrementConnectedPlayers();
        gameModel.incrementConnectedPlayers();
        assertEquals(2, gameModel.getConnectedPlayers());
    }

    @Test
    void addNickname(){
        gameModel.addNickname("player1","1");
        gameModel.addNickname("player2","2");

        ArrayList<String> nicknames=new ArrayList<>();
        nicknames.add("player1");
        nicknames.add("player2");

        ArrayList<String> ids=new ArrayList<>();
        ids.add("1");
        ids.add("2");

        assertEquals(nicknames, gameModel.getNicknames());
        assertEquals(ids, gameModel.getIds());
    }

    @Test
    void addRetiredPlayer(){
        Player player1=mock(Player.class);
        Player player2=mock(Player.class);
        gameModel.addRetiredPlayer(player1);
        gameModel.addRetiredPlayer(player2);
        assertTrue(gameModel.getRetiredPlayers().contains(player1));
        assertTrue(gameModel.getRetiredPlayers().contains(player2));
    }

    /**
     * This test verifies that the default value for timerPosition is 2
     */
    @Test
    void getTimerPosition(){
        assertEquals(2, gameModel.getTimerPosition());
    }

    /**
     * This test verifies that timerPosition is set to the correct value
     */
    @Test
    void setTimerPosition(){
        gameModel.setTimerPosition(1);
        assertEquals(1, gameModel.getTimerPosition());

        gameModel.setTimerPosition(0);
        assertEquals(0, gameModel.getTimerPosition());
    }

    /**
     * This test verifies that the default value for timerActive is false
     */
    @Test
    void getTimerActive(){
        assertFalse(gameModel.getTimerActive());
    }

    /**
     * This test verifies that timerActive is set in the correct way
     */
    @Test
    void setTimerActive(){
        gameModel.setTimerActive(true);
        assertTrue(gameModel.getTimerActive());

        gameModel.setTimerActive(false);
        assertFalse(gameModel.getTimerActive());
    }

    /**
     * This test verifies that the timer is activated correctly and only in the right situation
     */
    @Test
    void activateTimer(){
        assertTrue(gameModel.activateTimer(), "First activation should return true");

        assertFalse(gameModel.activateTimer(), "Second activation should return false");

        gameModel.setTimerActive(false);
        assertTrue(gameModel.activateTimer(), "Third activation should return true");

        assertFalse(gameModel.activateTimer(), "Fourth activation should return false");
    }

}