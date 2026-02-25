package model.player;

import enumerations.*;
import model.card.Card;
import model.card.CardPile;
import model.card.cardsType.AbandonedShip;
import model.shipBoard.ShipBoard;
import model.shipBoard.ShipBoardSpace;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.tiles.componentTile.AlienCabine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer","11", Color.RED, FlightType.FIRST_FLIGHT);
    }

    @Test
    void testPlayerInitialization() {
        assertEquals("TestPlayer", player.getNickname());
        assertEquals(Color.RED, player.getColor());
        assertEquals(0, player.getCosmicCredit());
        assertEquals(0, player.getPositionOnFlightBoard());
        assertEquals(0, player.getLapCounter());
    }

    @Test
    void testIncrementCosmicCredit() {
        player.incrementCosmicCredit(10);
        assertEquals(10, player.getCosmicCredit());

        player.incrementCosmicCredit(5);
        assertEquals(15, player.getCosmicCredit());
    }

    @Test
    void testTakeCardInHand() {
        Card card = new AbandonedShip(CardName.ABANDONED_SHIP,1,"",1,1,0);
        player.takeCardInHand(card);

        assertEquals(card, player.getCard());
    }

    @Test
    void testTakeTileInHand() {
        ComponentTile tile = new AlienCabine(TileName.CABINE,"r",1,1,1,1,"fff");
        player.addTileInHand(tile);

    }

    @Test
    void testPutTileBack() {
        ComponentTile tile =  new AlienCabine(TileName.CABINE,"r",1,1,1,1,"fff");
        player.addTileInHand(tile);

        player.removeTileInHand();

        assertNull(player.getTileInHand());
    }

    @Test
    void testSetPositionOnFlightBoard() {
        player.setPositionOnFlightBoard(5);
        assertEquals(5, player.getPositionOnFlightBoard());
    }

    @Test
    void testSetLapCounter() {
        player.setLapCounter(3);
        assertEquals(3, player.getLapCounter());
    }

    @Test
    void testSetProceed() {
        player.setProceed(true);
        assertTrue(player.getProceed());
        player.setProceed(false);
        assertFalse(player.getProceed());
    }

    @Test
    void getId(){
        Player player = new Player("TestPlayer","34", Color.RED, FlightType.FIRST_FLIGHT);
        assertEquals("34", player.getId());
    }

    @Test
    void setNickname(){
        player.setNickname("Player1");
        assertEquals("Player1", player.getNickname());
    }

    @Test
    void setColor(){
        player.setColor(Color.RED);
        assertEquals(Color.RED, player.getColor());
    }

    @Test
    void getHasFinishedBuilding(){
        assertFalse(player.getHasFinishedBuilding());
    }

    @Test
    void setHasFinishedBuilding(){
        player.setHasFinishedBuilding(true);
        assertTrue(player.getHasFinishedBuilding());

        player.setHasFinishedBuilding(false);
        assertFalse(player.getHasFinishedBuilding());
    }

    @Test
    void getShipBoard(){
        Player player = new Player("Player1", "11", Color.BLUE, FlightType.STANDARD_FLIGHT);
        ShipBoard shipBoard = player.getShipBoard();
        assertNotNull(shipBoard, "ShipBoard should not be null");
    }

    @Test
    void decreaseCosmicCredit(){
        player.incrementCosmicCredit(10);
        assertEquals(10, player.getCosmicCredit());
        player.decreaseCosmicCredit(5);
        assertEquals(5, player.getCosmicCredit());
    }

    @Test
    void getCardPileInHand(){
        assertNull(player.getCardPileInHand());
    }

    @Test
    void addCardPileInHand(){
        CardPile cardPile=mock(CardPile.class);
        player.addCardPileInHand(cardPile);
        assertEquals(cardPile, player.getCardPileInHand());
    }

    @Test
    void removeCardPileInHand(){
        CardPile cardPile=mock(CardPile.class);
        player.addCardPileInHand(cardPile);
        assertEquals(cardPile, player.getCardPileInHand());
        player.removeCardPileInHand();
        assertNull(player.getCardPileInHand());
    }

    @Test
    void getCardPile(){
        assertNull(player.getCardPile());
    }

    @Test
    void getTempGoodsBlock(){
        player.insertGoodsBlock(Color.BLUE);
        assertTrue(player.getTempGoodsBlock().contains(Color.BLUE));
        player.insertGoodsBlock(Color.RED);
        assertTrue(player.getTempGoodsBlock().contains(Color.RED));
    }

    @Test
    void insertGoodsBlock(){
        player.insertGoodsBlock(Color.BLUE);
        assertTrue(player.getTempGoodsBlock().contains(Color.BLUE));
    }

    @Test
    void getPenaltyGoods(){
        assertEquals(0, player.getPenaltyGoods());
    }

    @Test
    void setPenaltyGoods(){
        player.setPenaltyGoods(5);
        assertEquals(5, player.getPenaltyGoods());
    }

    @Test
    void decrementPenaltyGoods(){
        player.setPenaltyGoods(5);
        assertEquals(5, player.getPenaltyGoods());
        player.decrementPenaltyGoods();
        assertEquals(4, player.getPenaltyGoods());
    }

    @Test
    void getPenaltyEquipment(){
        assertEquals(0, player.getPenaltyEquipment());
    }

    @Test
    void setPenaltyEquipment(){
        player.setPenaltyEquipment(5);
        assertEquals(5, player.getPenaltyEquipment());
    }

    @Test
    void decrementPenaltyEquipment(){
        player.setPenaltyEquipment(5);
        assertEquals(5, player.getPenaltyEquipment());
        player.decrementPenaltyEquipment();
        assertEquals(4, player.getPenaltyEquipment());
    }

    @Test
    void getProceed(){
        assertFalse(player.getProceed());
    }

    @Test
    void setProceed(){
        player.setProceed(true);
        assertTrue(player.getProceed());
        player.setProceed(false);
        assertFalse(player.getProceed());
    }

    @Test
    void getPlayerState(){
        assertEquals(PlayerState.IDLE, player.getPlayerState());
    }

    @Test
    void setPlayerState(){
        player.setPlayerState(PlayerState.PLAY);
        assertEquals(PlayerState.PLAY, player.getPlayerState());
    }

    @Test
    void getToBeRetiredFlag(){
        assertFalse(player.getToBeRetiredFlag());
    }

    @Test
    void setToBeRetiredFlag(){
        player.setToBeRetiredFlag(true);
        assertTrue(player.getToBeRetiredFlag());
        player.setToBeRetiredFlag(false);
        assertFalse(player.getToBeRetiredFlag());
    }

}