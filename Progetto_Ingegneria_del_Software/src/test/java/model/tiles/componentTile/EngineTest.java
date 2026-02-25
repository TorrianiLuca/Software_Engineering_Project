package model.tiles.componentTile;

import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    Engine engine;

    @BeforeEach
    void setUp() {
        engine= new Engine(TileName.ENGINE, "09.jpg", 2, 1, 5,3, 1);
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the flag isDouble is set correctly
     */
    @Test
    void isDouble() {
        assertFalse(engine.isDouble());

        Engine engine1= new Engine(TileName.ENGINE, "09.jpg", 2, 1, 5,3, 2);
        assertTrue(engine1.isDouble());
    }

    /**
     * This test verifies that the flag isActive is true if the number of component is 1 and false if the number of component is 2
     */
    @Test
    void getActive() {
        assertTrue(engine.getActive());

        Engine engine1= new Engine(TileName.ENGINE, "09.jpg", 2, 0, 5,3, 2);
        assertFalse(engine1.getActive());
    }

    /**
     * This test verifies that the engines are activated correctly
     */
    @Test
    void setActive() {
        engine.setActive(true);
        assertTrue(engine.getActive());

        engine.setActive(false);
        assertFalse(engine.getActive());
    }

    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        assertEquals(0, engine.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        engine.setId(17);
        assertEquals(17, engine.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        assertEquals(TileName.ENGINE, engine.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        assertEquals(2, engine.getConnectorsOnSide().get("up"));
        assertEquals(1, engine.getConnectorsOnSide().get("right"));
        assertEquals(5, engine.getConnectorsOnSide().get("down"));
        assertEquals(3, engine.getConnectorsOnSide().get("left"));

    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        assertEquals("nord", engine.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        assertTrue(engine.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        engine.setFaceUp();
        assertFalse(engine.isFaceDown());
        engine.setFaceDown();
        assertTrue(engine.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        engine.setFaceUp();
        assertFalse(engine.isFaceDown());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        engine.setDirection("ovest");
        assertEquals("ovest", engine.getDirection());

        engine.setDirection("sud");
        assertEquals("sud", engine.getDirection());

        engine.setDirection("est");
        assertEquals("est", engine.getDirection());

        engine.setDirection("nord");
        assertEquals("nord", engine.getDirection());
    }
}