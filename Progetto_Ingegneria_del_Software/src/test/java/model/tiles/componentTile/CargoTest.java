
package model.tiles.componentTile;

import enumerations.Color;
import enumerations.TileName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CargoTest {

    Cargo cargo;

    @BeforeEach
    void setUp() {
        cargo= new Cargo(TileName.CARGO, "09.jpg", 2, 1, 2,3, 3, "Red");
    }

    @AfterEach
    void tearDown() {
    }


    /**
     * This test verifies that the default value for the id of the card is 0
     */
    @Test
    void getId() {
        assertEquals(0, cargo.getId());
    }

    /**
     * This test verifies that the card id is set in the correct way
     */
    @Test
    void setId() {
        cargo.setId(17);
        assertEquals(17, cargo.getId());
    }

    /**
     * This test verifies that the method returns the correct name of the card
     */
    @Test
    void getName() {
        assertEquals(TileName.CARGO, cargo.getName());
    }

    /**
     * This test verifies that the connectors are associated to the correct side
     */
    @Test
    void getConnectorsOnSide() {
        assertEquals(2, cargo.getConnectorsOnSide().get("up"));
        assertEquals(1, cargo.getConnectorsOnSide().get("right"));
        assertEquals(2, cargo.getConnectorsOnSide().get("down"));
        assertEquals(3, cargo.getConnectorsOnSide().get("left"));

    }

    /**
     * This test verifies that the method returns the correct connector type that is on the side given
     */
    @Test
    void getConnector() {
        assertEquals(2, cargo.getConnector("up"));
        assertEquals(1, cargo.getConnector("right"));
        assertEquals(2, cargo.getConnector("down"));
        assertEquals(3, cargo.getConnector("left"));
    }

    /**
     * This test verifies that the default direction of the component is nord
     */
    @Test
    void getDirection() {
        assertEquals("nord", cargo.getDirection());
    }

    /**
     * This test is used to verify that the direction of a tile con be changed more times
     */
    @Test
    void setDirection() {
        cargo.setDirection("ovest");
        assertEquals("ovest", cargo.getDirection());

        cargo.setDirection("sud");
        assertEquals("sud", cargo.getDirection());

        cargo.setDirection("est");
        assertEquals("est", cargo.getDirection());

        cargo.setDirection("nord");
        assertEquals("nord", cargo.getDirection());
    }

    /**
     * This test is used to verify that the default value of faceDown is true
     */
    @Test
    void isFaceDown() {
        assertTrue(cargo.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown back to true
     */
    @Test
    void setFaceDown() {
        cargo.setFaceUp();
        assertFalse(cargo.isFaceDown());
        cargo.setFaceDown();
        assertTrue(cargo.isFaceDown());
    }

    /**
     * This test is used to verify that this method sets isFaceDown to false
     */
    @Test
    void setFaceUp() {
        cargo.setFaceUp();
        assertFalse(cargo.isFaceDown());
    }

    /**
     * This test verifies that the method marks the tile as occupied
     */
    @Test
    void tryOccupy() {
        assertTrue(cargo.tryOccupy(), "First occupy should succeed");
        assertFalse(cargo.tryOccupy(), "Second occupy should fail");
    }

    /**
     * This test verifies that the tile is set as unoccupied when it is released
     */
    @Test
    void tryRelease() {
        cargo.tryOccupy();
        assertTrue(cargo.tryRelease(), "Release should succeed after occupying");
        assertFalse(cargo.tryRelease(), "Cannot release when already free");
    }

    /**
     * This test verifies the correct conversion of the colors form String to Color
     */
    @Test
    void getColor() {
        assertEquals(Color.RED, cargo.getColor());
        cargo= new Cargo(TileName.CARGO, "09.jpg", 2, 1, 2,3, 1, "Blue");
        assertEquals(Color.BLUE, cargo.getColor());
    }

    /**
     * This test verifies that the number of boxes of the component is set correctly
     */
    @Test
    void getNumMaxCargos() {
        assertEquals(3, cargo.getNumMaxCargos());
    }

    /**
     * This test verifies that the default value for the occupied boxes is 0
     */
    @Test
    void getNumOccupiedCargos() {
        assertEquals(0, cargo.getNumOccupiedCargos());
    }

    /**
     * This test verifies that the number of occupied boxes is increased correctly
     */
    @Test
    void increaseNumOccupiedCargos() {
        cargo.increaseNumOccupiedCargos();
        assertEquals(1, cargo.getNumOccupiedCargos());
        cargo.increaseNumOccupiedCargos();
        assertEquals(2, cargo.getNumOccupiedCargos());
    }

    /**
     * This test verifies that the number of occupied boxes is decreased correctly
     */
    @Test
    void decreaseNumOccupiedCargos() {
        cargo.increaseNumOccupiedCargos();
        assertEquals(1, cargo.getNumOccupiedCargos());
        cargo.decreaseNumOccupiedCargos();
        assertEquals(0, cargo.getNumOccupiedCargos());
    }

    /**
     * This test verifies that the goods are insert correctly in the boxes and that the method return false if there isn't any space left for other goods
     */
    @Test
    void putCargoIn1() {
        boolean temp1= cargo.putCargoIn(Color.RED);
        assertTrue(temp1);
        assertEquals(1, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.RED));

        boolean temp2= cargo.putCargoIn(Color.BLUE);
        assertTrue(temp2);
        assertEquals(2, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.BLUE));

        boolean temp3= cargo.putCargoIn(Color.RED);
        assertTrue(temp3);
        assertEquals(3, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.RED));

        boolean temp4= cargo.putCargoIn(Color.RED);
        assertFalse(temp4);
        assertEquals(3, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.RED));
    }

    /**
     * This test verifies that the red boxes can contain all the goods colors
     */
    @Test
    void putCargoIn2() {

        boolean temp1= cargo.putCargoIn(Color.YELLOW);
        assertTrue(temp1);
        assertEquals(1, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.YELLOW));

        boolean temp2= cargo.putCargoIn(Color.BLUE);
        assertTrue(temp2);
        assertEquals(2, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.BLUE));

        boolean temp3= cargo.putCargoIn(Color.GREEN);
        assertTrue(temp3);
        assertEquals(3, cargo.getNumOccupiedCargos());
        assertTrue(cargo.getCargosIn().contains(Color.GREEN));

        boolean temp4= cargo.putCargoIn(Color.RED);
        assertFalse(temp4);
        assertEquals(3, cargo.getNumOccupiedCargos());
        assertFalse(cargo.getCargosIn().contains(Color.RED));
    }

    /**
     * This test verifies that blue boxes can contain blu, yellow and green goods
     */
    @Test
    void putCargoIn3() {

        Cargo cargo1= new Cargo(TileName.CARGO, "09.jpg", 2, 1, 2,3, 3, "Blue");

        boolean temp1= cargo1.putCargoIn(Color.YELLOW);
        assertTrue(temp1);
        assertEquals(1, cargo1.getNumOccupiedCargos());
        assertTrue(cargo1.getCargosIn().contains(Color.YELLOW));

        boolean temp2= cargo1.putCargoIn(Color.BLUE);
        assertTrue(temp2);
        assertEquals(2, cargo1.getNumOccupiedCargos());
        assertTrue(cargo1.getCargosIn().contains(Color.BLUE));

        boolean temp3= cargo1.putCargoIn(Color.GREEN);
        assertTrue(temp3);
        assertEquals(3, cargo1.getNumOccupiedCargos());
        assertTrue(cargo1.getCargosIn().contains(Color.GREEN));
    }

    /**
     * This test verifies that blue cargos cannot accept red goods
     */
    @Test
    void putCargoIn4() {

        Cargo cargo2= new Cargo(TileName.CARGO, "09.jpg", 2, 1, 2,3, 3, "Blue");

        boolean temp1= cargo2.putCargoIn(Color.YELLOW);
        assertTrue(temp1);
        assertEquals(1, cargo2.getNumOccupiedCargos());
        assertTrue(cargo2.getCargosIn().contains(Color.YELLOW));

        boolean temp2= cargo2.putCargoIn(Color.BLUE);
        assertTrue(temp2);
        assertEquals(2, cargo2.getNumOccupiedCargos());
        assertTrue(cargo2.getCargosIn().contains(Color.BLUE));

        boolean temp3= cargo2.putCargoIn(Color.RED);
        assertFalse(temp3);
        assertEquals(2, cargo2.getNumOccupiedCargos());
        assertFalse(cargo2.getCargosIn().contains(Color.RED));
    }

    /**
     * This test verifies that the goods are removed correctly from the boxes
     */
    @Test
    void removeCargo() {
        cargo.putCargoIn(Color.RED);
        assertEquals(1, cargo.getNumOccupiedCargos());
        cargo.putCargoIn(Color.BLUE);
        assertEquals(2, cargo.getNumOccupiedCargos());
        cargo.putCargoIn(Color.GREEN);
        assertEquals(3, cargo.getNumOccupiedCargos());

        cargo.removeCargo(Color.GREEN);
        assertEquals(2, cargo.getNumOccupiedCargos());
        cargo.removeCargo(Color.RED);
        assertEquals(1, cargo.getNumOccupiedCargos());
        cargo.removeCargo(Color.YELLOW);
        assertEquals(1, cargo.getNumOccupiedCargos());

        assertFalse(cargo.getCargosIn().contains(Color.RED));
        assertFalse(cargo.getCargosIn().contains(Color.GREEN));
        assertTrue(cargo.getCargosIn().contains(Color.BLUE));
    }

    /**
     * This test verifies that the rarest good in the cargo is set correctly
     */
    @Test
    void rarestCargoIn1() {
        Cargo cargo= new Cargo(TileName.CARGO, "09.jpg", 2, 1, 2,3, 5, "Red");
        cargo.putCargoIn(Color.BLUE);
        assertEquals(Color.BLUE, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.GREEN);
        assertEquals(Color.GREEN, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.YELLOW);
        assertEquals(Color.YELLOW, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.GREEN);
        assertEquals(Color.YELLOW, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.RED);
        assertEquals(Color.RED, cargo.rarestCargoIn());
    }

    /**
     * This test verifies that the rarest good in the cargo is set correctly and that the red goods are not counted because is a blue cargo
     */
    @Test
    void rarestCargoIn2() {
        Cargo cargo= new Cargo(TileName.CARGO, "09.jpg", 2, 1, 2,3, 5, "Blue");
        cargo.putCargoIn(Color.BLUE);
        assertEquals(Color.BLUE, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.GREEN);
        assertEquals(Color.GREEN, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.YELLOW);
        assertEquals(Color.YELLOW, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.GREEN);
        assertEquals(Color.YELLOW, cargo.rarestCargoIn());

        cargo.putCargoIn(Color.RED);
        assertEquals(Color.YELLOW, cargo.rarestCargoIn());
    }

    @Test
    void rarestCargoIn3() {
        assertNull(cargo.rarestCargoIn());
    }

    /**
     * This test verifies that the good blocks contain in the cargo are returned correctly
     */
    @Test
    void getCargosIn() {
        cargo.putCargoIn(Color.BLUE);
        cargo.putCargoIn(Color.GREEN);
        ArrayList<Color> cargos = cargo.getCargosIn();

        assertEquals(2, cargos.size());
        assertTrue(cargos.contains(Color.BLUE));
        assertTrue(cargos.contains(Color.GREEN));
    }
}
