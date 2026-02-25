package model.tiles.componentTile;

import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ShieldTest {

    Shield shield;

    @BeforeEach
    void setUp() {
        shield=new Shield(TileName.SHIELD, "037.jpg", 1, 2, 3,3);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the default value of the flag active is false
     */
    @Test
    void getActive() {
        assertFalse(shield.getActive());
    }

    /**
     * This test verifies that the shields are activated correctly
     */
    @Test
    void setActive() {
        shield.setActive(true);
        assertTrue(shield.getActive());

        shield.setActive(false);
        assertFalse(shield.getActive());
    }

    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        assertEquals(0, shield.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        shield.setId(28);
        assertEquals(28, shield.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        assertEquals(TileName.SHIELD, shield.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        assertEquals(1, shield.getConnectorsOnSide().get("up"));
        assertEquals(2, shield.getConnectorsOnSide().get("right"));
        assertEquals(3, shield.getConnectorsOnSide().get("down"));
        assertEquals(3, shield.getConnectorsOnSide().get("left"));

    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        assertEquals("nord", shield.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        assertTrue(shield.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        shield.setFaceUp();
        assertFalse(shield.isFaceDown());

        shield.setFaceDown();
        assertTrue(shield.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        shield.setFaceUp();
        assertFalse(shield.isFaceDown());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        shield.setDirection("est");
        assertEquals("est", shield.getDirection());

        shield.setDirection("nord");
        assertEquals("nord", shield.getDirection());

        shield.setDirection("sud");
        assertEquals("sud", shield.getDirection());

        shield.setDirection("ovest");
        assertEquals("ovest", shield.getDirection());
    }
}