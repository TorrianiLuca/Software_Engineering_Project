package model.tiles.componentTile;

import enumerations.Color;
import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class StartingCabineTest {

    StartingCabine startingCabine;

    @BeforeEach
    void setUp() {
        startingCabine=new StartingCabine(TileName.STARTING_CABINE, "037.jpg", 3, 3, 3,3, Color.RED);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        assertEquals(0, startingCabine.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        startingCabine.setId(28);
        assertEquals(28, startingCabine.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        assertEquals(TileName.STARTING_CABINE, startingCabine.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        assertEquals(3, startingCabine.getConnectorsOnSide().get("up"));
        assertEquals(3, startingCabine.getConnectorsOnSide().get("right"));
        assertEquals(3, startingCabine.getConnectorsOnSide().get("down"));
        assertEquals(3, startingCabine.getConnectorsOnSide().get("left"));
    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        assertEquals("nord", startingCabine.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        assertTrue(startingCabine.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        startingCabine.setFaceUp();
        assertFalse(startingCabine.isFaceDown());

        startingCabine.setFaceDown();
        assertTrue(startingCabine.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        startingCabine.setFaceUp();
        assertFalse(startingCabine.isFaceDown());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        startingCabine.setDirection("est");
        assertEquals("est", startingCabine.getDirection());

        startingCabine.setDirection("nord");
        assertEquals("nord", startingCabine.getDirection());

        startingCabine.setDirection("ovest");
        assertEquals("ovest", startingCabine.getDirection());

        startingCabine.setDirection("sud");
        assertEquals("sud", startingCabine.getDirection());
    }


    /**
     * This test verifies that the default value for the flag connectedWithOccupiedCabine is false
     */
    @Test
    void isConnectedWithOccupiedCabine() {
        assertFalse(startingCabine.isConnectedWithOccupiedCabine());
    }

    /**
     * This test verifies that the flag connectedWithOccupiedCabine is set correctly
     */
    @Test
    void setConnectedWithOccupiedCabine() {
        startingCabine.setConnectedWithOccupiedCabine(true);
        assertTrue(startingCabine.isConnectedWithOccupiedCabine());

        startingCabine.setConnectedWithOccupiedCabine(false);
        assertFalse(startingCabine.isConnectedWithOccupiedCabine());
    }

    @Test
    void getColor() {
        assertEquals(Color.RED, startingCabine.getColor());
    }

    @Test
    void getNumFigures() {
        assertEquals(0, startingCabine.getNumFigures());
    }

    @Test
    void setNumFigures() {
        startingCabine.setNumFigures(2);
        assertEquals(2, startingCabine.getNumFigures());
    }

    @Test
    void decrementNumFigures() {
        startingCabine.setNumFigures(2);
        assertEquals(2, startingCabine.getNumFigures());
        startingCabine.decrementNumFigures();
        assertEquals(1, startingCabine.getNumFigures());
    }
}