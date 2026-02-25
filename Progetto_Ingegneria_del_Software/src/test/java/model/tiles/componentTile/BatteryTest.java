
package model.tiles.componentTile;

import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BatteryTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the number of batteries in the component is set correctly
     */
    @Test
    void getNumMaxBatteries() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals(2, battery.getNumMaxBatteries());

        Battery battery1= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 1);
        assertEquals(1, battery1.getNumMaxBatteries());
    }

    /**
     * This test verifies that the number of batteries available is set correctly
     */
    @Test
    void getNumBatteriesInUse() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals(2, battery.getNumBatteriesInUse());
    }


    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals(0, battery.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        battery.setId(23);
        assertEquals(23, battery.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals(TileName.BATTERY, battery.getName());
        assertNotEquals(TileName.CANNON, battery.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals(3, battery.getConnectorsOnSide().get("up"));
        assertEquals(2, battery.getConnectorsOnSide().get("right"));
        assertEquals(1, battery.getConnectorsOnSide().get("down"));
        assertEquals(3, battery.getConnectorsOnSide().get("left"));

        assertNotEquals(2, battery.getConnectorsOnSide().get("up"));

    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals("nord", battery.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertEquals(true, battery.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        assertTrue(battery.isFaceDown(), "The tile should be face down at the beginning");

        battery.setFaceUp();
        assertFalse(battery.isFaceDown(), "The tile should be face up");

        battery.setFaceDown();
        assertTrue(battery.isFaceDown(), "The tile should be face down");
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        battery.setFaceUp();
        assertFalse(battery.isFaceDown());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        Battery battery= new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 2);
        battery.setDirection("nord");
        assertEquals("nord", battery.getDirection());

        battery.setDirection("ovest");
        assertEquals("ovest", battery.getDirection());

        battery.setDirection("sud");
        assertEquals("sud", battery.getDirection());

        battery.setDirection("est");
        assertEquals("est", battery.getDirection());
    }

    @Test
    void decreaseNumOfBatteries(){
        Battery battery=new Battery(TileName.BATTERY, "08.jpg", 3, 2, 1, 3, 3);
        battery.decreaseNumBatteriesInUse();
        assertEquals(2, battery.getNumBatteriesInUse());
    }
}
