package model.tiles.componentTile;

import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {

    Cannon cannon;

    @BeforeEach
    void setUp() {
        cannon=new Cannon(TileName.CANNON, "10.jpg", 5, 2, 2, 1, 1);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the flag isDouble is set correctly
     */
    @Test
    void isDouble() {
        assertFalse(cannon.isDouble());

        Cannon cannon1=new Cannon(TileName.CANNON, "10.jpg", 5, 2, 0, 1, 2);
        assertTrue(cannon1.isDouble());
    }

    /**
     * This test verifies that the flag isActive is true if the number of component is 1 and false if the number of component is 2
     */
    @Test
    void getActive() {
        assertTrue(cannon.getActive());

        Cannon cannon1=new Cannon(TileName.CANNON, "10.jpg", 5, 2, 0, 1, 2);
        assertFalse(cannon1.getActive());

    }

    /**
     * This test verifies that the cannons are activated correctly
     */
    @Test
    void setActive() {
        Cannon cannon1=new Cannon(TileName.CANNON, "10.jpg", 5, 2, 0, 1, 2);
        assertFalse(cannon1.getActive());

        cannon1.setActive(true);
        assertTrue(cannon1.getActive());

        cannon1.setActive(false);
        assertFalse(cannon1.getActive());
    }

    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        assertEquals(0, cannon.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        cannon.setId(2);
        assertEquals(2, cannon.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        assertEquals(TileName.CANNON, cannon.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        assertEquals(5, cannon.getConnectorsOnSide().get("up"));
        assertEquals(2, cannon.getConnectorsOnSide().get("right"));
        assertEquals(2, cannon.getConnectorsOnSide().get("down"));
        assertEquals(1, cannon.getConnectorsOnSide().get("left"));
    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        assertEquals("nord", cannon.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        assertTrue(cannon.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        cannon.setFaceUp();
        assertFalse(cannon.isFaceDown());
        cannon.setFaceDown();
        assertTrue(cannon.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        cannon.setFaceUp();
        assertFalse(cannon.isFaceDown());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        cannon.setDirection("ovest");
        assertEquals("ovest", cannon.getDirection());

        cannon.setDirection("nord");
        assertEquals("nord", cannon.getDirection());

        cannon.setDirection("sud");
        assertEquals("sud", cannon.getDirection());

        cannon.setDirection("est");
        assertEquals("est", cannon.getDirection());

    }
}