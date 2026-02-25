
package model.tiles.componentTile;

import enumerations.Color;
import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlienCabineTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies the correct conversion of the colors form String to Color
     */
    @Test
    void getColor() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 0, 0, 0, 3, "purple");
        assertEquals(Color.PURPLE, alienCabine.getColor());

        AlienCabine alienCabine2 = new AlienCabine(TileName.CABINE, "05.jpg", 0, 0, 0, 3, "yellow");
        assertEquals(Color.YELLOW, alienCabine2.getColor());
    }

    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 0, 0, 0, 3, "purple");
        assertEquals(0, alienCabine.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 0, 0, 0, 3, "purple");
        alienCabine.setId(12);

        assertEquals(12, alienCabine.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 0, 0, 0, 3, "purple");

        assertEquals(TileName.CABINE, alienCabine.getName());
        assertNotEquals(TileName.CANNON, alienCabine.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 1, 2, 3, 0, "purple");

        assertEquals(1, alienCabine.getConnectorsOnSide().get("up"));
        assertEquals(2, alienCabine.getConnectorsOnSide().get("right"));
        assertEquals(3, alienCabine.getConnectorsOnSide().get("down"));
        assertEquals(0, alienCabine.getConnectorsOnSide().get("left"));

    }

    /**
     * This test verifies that this method returns the correct value of direction
     */
    @Test
    void getDirection() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 1, 2, 3, 0, "purple");
        alienCabine.setDirection("sud");

        assertEquals(alienCabine.getDirection(), "sud");
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 1, 2, 3, 0, "purple");
        assertTrue(alienCabine.isFaceDown(), "The tile should be face down at the beginning");
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 1, 2, 3, 0, "purple");
        assertTrue(alienCabine.isFaceDown(), "The tile should be face down at the beginning");

        alienCabine.setFaceUp();
        assertFalse(alienCabine.isFaceDown(), "The tile should be face up");

        alienCabine.setFaceDown();
        assertTrue(alienCabine.isFaceDown(), "The tile should be face down");
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 1, 2, 3, 0, "purple");

        alienCabine.setFaceUp();
        assertFalse(alienCabine.isFaceDown(), "The tile should be face up");
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times and that nord is the default direction
     */
    @Test
    void setDirection() {
        AlienCabine alienCabine = new AlienCabine(TileName.CABINE, "05.jpg", 1, 2, 3, 0, "purple");

        assertEquals("nord", alienCabine.getDirection());

        alienCabine.setDirection("ovest");
        assertEquals("ovest", alienCabine.getDirection());

        alienCabine.setDirection("est");
        assertEquals("est", alienCabine.getDirection());

        alienCabine.setDirection("sud");
        assertEquals("sud", alienCabine.getDirection());

        alienCabine.setDirection("nord");
        assertEquals("nord", alienCabine.getDirection());

    }
}
