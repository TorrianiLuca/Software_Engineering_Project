
package model.shipBoard;

import enumerations.TypeSpace;
import model.tiles.ComponentTile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ShipBoardSpaceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    /**
     * This test verifies that the type of the space is the one expected
     */
    @Test
    void getTypeSpace() {
        ShipBoardSpace shipBoardSpace=new ShipBoardSpace(TypeSpace.USABLE);
        assertEquals(TypeSpace.USABLE,shipBoardSpace.getTypeSpace());

        ShipBoardSpace shipBoardSpace1=new ShipBoardSpace(TypeSpace.RESERVE);
        assertEquals(TypeSpace.RESERVE,shipBoardSpace1.getTypeSpace());

        ShipBoardSpace shipBoardSpace2=new ShipBoardSpace(TypeSpace.UNUSABLE);
        assertEquals(TypeSpace.UNUSABLE,shipBoardSpace2.getTypeSpace());
    }

    /**
     * This test verifies the content of the space is not null after the insertion of a component
     */
    @Test
    void insertComponent() {
        ShipBoardSpace shipBoardSpace=new ShipBoardSpace(TypeSpace.USABLE);
        ComponentTile componentContained=mock(ComponentTile.class);

        assertNull(shipBoardSpace.getComponent());

        shipBoardSpace.insertComponent(componentContained);

        assertNotNull(shipBoardSpace.getComponent());
    }

    /**
     * This test verifies the insertion of the correct component
     */
    @Test
    void getComponent() {
        ShipBoardSpace shipBoardSpace=new ShipBoardSpace(TypeSpace.USABLE);
        ComponentTile componentContained=mock(ComponentTile.class);

        shipBoardSpace.insertComponent(componentContained);
        assertEquals(componentContained, shipBoardSpace.getComponent());
    }

    /**
     * This test verifies that a component is removed correctly
     */
    @Test
    void removeComponent() {
        ShipBoardSpace shipBoardSpace= new ShipBoardSpace(TypeSpace.USABLE);
        ComponentTile componentContained=mock(ComponentTile.class);
        shipBoardSpace.insertComponent(componentContained);
        shipBoardSpace.removeComponent();
        assertNull(shipBoardSpace.getComponent());
    }

    /**
     * This test verifies that the default value for check is 0
     */
    @Test
    void getCheck() {
        ShipBoardSpace shipBoardSpace= new ShipBoardSpace(TypeSpace.USABLE);
        assertEquals(0, shipBoardSpace.getCheck());
    }

    /**
     * This test verifies the change of the variable check
     */
    @Test
    void setCheck() {
        ShipBoardSpace shipBoardSpace= new ShipBoardSpace(TypeSpace.USABLE);
        shipBoardSpace.setCheck(1);
        assertEquals(1, shipBoardSpace.getCheck());
    }

}
