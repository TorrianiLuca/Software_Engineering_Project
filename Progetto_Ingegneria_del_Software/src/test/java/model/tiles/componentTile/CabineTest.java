
package model.tiles.componentTile;

import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CabineTest {

    private Cabine cabine;

    @BeforeEach
    void setUp() {
        cabine = new Cabine(TileName.CABINE, "08.jpg", 3, 2, 1, 3);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the default value of connectedWithAlienCabine is false
     */
    @Test
    void isConnectedWithAlienCabine() {
        assertFalse(cabine.isConnectedWithAlienCabine());
    }

    /**
     * This test verifies that the method sets connectedWithAlienCabine to true
     */
    @Test
    void setConnectedWithAlienCabine() {
        cabine.setConnectedWithAlienCabine();
        assertTrue(cabine.isConnectedWithAlienCabine());
    }

    /**
     * This test verifies that the method sets connectedWithNormalCabine back to false
     */
    @Test
    void unsetConnectedWithAlienCabine() {
        cabine.setConnectedWithAlienCabine();
        assertTrue(cabine.isConnectedWithAlienCabine());

        cabine.unsetConnectedWithAlienCabine();
        assertFalse(cabine.isConnectedWithAlienCabine());
    }

    /**
     * This test verifies that the default value of connectedWithOccupiedCabine is false
     */
    @Test
    void isConnectedWithOccupiedCabine() {
        assertFalse(cabine.isConnectedWithOccupiedCabine());
    }

    /**
     * This test verifies that the method sets connectedWithOccupiedCabine to the correct value
     */
    @Test
    void setConnectedWithOccupiedCabine() {
        cabine.setConnectedWithOccupiedCabine(true);
        assertTrue(cabine.isConnectedWithOccupiedCabine());

        cabine.setConnectedWithOccupiedCabine(false);
        assertFalse(cabine.isConnectedWithOccupiedCabine());
    }

    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        assertEquals(0, cabine.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        cabine.setId(20);
        assertEquals(20, cabine.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        assertEquals(TileName.CABINE, cabine.getName());
    }


    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        assertEquals(3, cabine.getConnectorsOnSide().get("up"));
        assertEquals(2, cabine.getConnectorsOnSide().get("right"));
        assertEquals(1, cabine.getConnectorsOnSide().get("down"));
        assertEquals(3, cabine.getConnectorsOnSide().get("left"));
    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        assertEquals("nord", cabine.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        assertTrue(cabine.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        cabine.setFaceUp();
        assertFalse(cabine.isFaceDown());

        cabine.setFaceDown();
        assertTrue(cabine.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        cabine.setFaceUp();
        assertFalse(cabine.isFaceDown());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        assertEquals("nord", cabine.getDirection());
        cabine.setDirection("est");

        assertEquals("est", cabine.getDirection());
        cabine.setDirection("ovest");
        assertEquals("ovest", cabine.getDirection());

        cabine.setDirection("sud");
        assertEquals("sud", cabine.getDirection());
    }

    @Test
    void getNumOfFigures() {
        assertEquals(0, cabine.getNumFigures());
    }

    @Test
    void setNumOfFigures() {
        cabine.setNumFigures(2);
        assertEquals(2, cabine.getNumFigures());
    }

    @Test
    void decreaseNumOfFigures() {
        cabine.setNumFigures(2);
        cabine.decrementNumFigures();
        assertEquals(1, cabine.getNumFigures());
    }

    @Test
    void getAlienCabineConnected(){
        assertTrue(cabine.getAlienCabineConnected().isEmpty());
    }

    @Test
    void setAlienCabineConnected(){
        AlienCabine alienCabine=mock(AlienCabine.class);
        cabine.setAlienCabineConnected(alienCabine);
        assertTrue(cabine.getAlienCabineConnected().contains(alienCabine));

        cabine.clearListAlien();
        assertTrue(cabine.getAlienCabineConnected().isEmpty());
    }

    @Test
    void getHasPurpleAlien(){
        assertFalse(cabine.getHasPurpleAlien());
    }

    @Test
    void getHasBrownAlien(){
        assertFalse(cabine.getHasBrownAlien());
    }

    @Test
    void setHasPurpleAlien(){
        cabine.setHasPurpleAlien(true);
        assertTrue(cabine.getHasPurpleAlien());
        cabine.setHasPurpleAlien(false);
        assertFalse(cabine.getHasPurpleAlien());
    }

    @Test
    void setHasBrownAlien(){
        cabine.setHasBrownAlien(true);
        assertTrue(cabine.getHasBrownAlien());
        cabine.setHasBrownAlien(false);
        assertFalse(cabine.getHasBrownAlien());
    }
}
