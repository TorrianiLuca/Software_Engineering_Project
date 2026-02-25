
package model.shipBoard;

import enumerations.*;
import exceptions.MultipleValidationErrorsException;
import model.GameModel;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.card.cardsType.MeteorSwarm;
import model.card.cardsType.Pirates;
import model.tiles.ComponentTile;
import model.tiles.componentTile.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static enumerations.Color.*;
import static enumerations.TileName.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShipBoardTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void initialiseShip(){
        ShipBoard shipBoard1=new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        assertEquals(STARTING_CABINE, shipBoard1.getSpace(2,3).getComponent().getName());
        StartingCabine startingCabine=(StartingCabine) shipBoard1.getSpace(2,3).getComponent();
        assertEquals(RED, startingCabine.getColor());

        ShipBoard shipBoard2=new ShipBoard(FlightType.FIRST_FLIGHT, YELLOW);
        assertEquals(STARTING_CABINE, shipBoard2.getSpace(2,3).getComponent().getName());
        StartingCabine startingCabine2=(StartingCabine) shipBoard2.getSpace(2,3).getComponent();
        assertEquals(YELLOW, startingCabine2.getColor());

        ShipBoard shipBoard3=new ShipBoard(FlightType.FIRST_FLIGHT, BLUE);
        assertEquals(STARTING_CABINE, shipBoard3.getSpace(2,3).getComponent().getName());
        StartingCabine startingCabine3=(StartingCabine) shipBoard3.getSpace(2,3).getComponent();
        assertEquals(BLUE, startingCabine3.getColor());

        ShipBoard shipBoard4=new ShipBoard(FlightType.FIRST_FLIGHT, GREEN);
        assertEquals(STARTING_CABINE, shipBoard4.getSpace(2,3).getComponent().getName());
        StartingCabine startingCabine4=(StartingCabine) shipBoard4.getSpace(2,3).getComponent();
        assertEquals(GREEN, startingCabine4.getColor());
    }

    @Test
    void getSpace() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(0, 0).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(0, 1).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(0, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(0, 3).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(0, 4).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(0, 5).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(0, 6).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(1, 0).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(1, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(1, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(1, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(1, 4).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(1, 5).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(1, 6).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(2, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(2, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(2, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(2, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(2, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(2, 5).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(2, 6).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(3, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(3, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(3, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(3, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(3, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(3, 5).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(3, 6).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(4, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(4, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(4, 2).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(4, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(4, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard.getSpace(4, 5).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard.getSpace(4, 6).getTypeSpace());

        ShipBoard shipBoard2 = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        assertSame(TypeSpace.UNUSABLE, shipBoard2.getSpace(0, 0).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard2.getSpace(0, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(0, 2).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard2.getSpace(0, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(0, 4).getTypeSpace());
        assertSame(TypeSpace.RESERVE, shipBoard2.getSpace(0, 5).getTypeSpace());
        assertSame(TypeSpace.RESERVE, shipBoard2.getSpace(0, 6).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard2.getSpace(1, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(1, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(1, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(1, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(1, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(1, 5).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard2.getSpace(1, 6).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 5).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(2, 6).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 2).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 5).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(3, 6).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(4, 0).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(4, 1).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(4, 2).getTypeSpace());
        assertSame(TypeSpace.UNUSABLE, shipBoard2.getSpace(4, 3).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(4, 4).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(4, 5).getTypeSpace());
        assertSame(TypeSpace.USABLE, shipBoard2.getSpace(4, 6).getTypeSpace());
    }

    @Test
    void putObjectIn() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace usableSpace = shipBoard.getSpace(0, 3); // (0,3) is USABLE in FIRST_FLIGHT
        ComponentTile tile = new Cabine(TileName.CABINE, "01.png", 2, 1,3, 2);
        shipBoard.putObjectIn(usableSpace, tile);
        assertEquals(tile, usableSpace.getComponent(), "The component should be insert in the usable space");
        ComponentTile tile2 = new Cabine(CANNON, "01.png", 2, 1,3, 2);
        shipBoard.putObjectIn(usableSpace, tile2);
        assertEquals(tile, usableSpace.getComponent(), "The component should not be insert in the usable space");
        ShipBoardSpace usableSpace2 = shipBoard.getSpace(1, 3); // (0,3) is USABLE in FIRST_FLIGHT
        shipBoard.putObjectIn(usableSpace2, tile2);
        assertEquals(tile2, usableSpace2.getComponent(), "The component should not be insert in the usable space");


        ShipBoardSpace unusableSpace = shipBoard.getSpace(0, 0); // (0,0) is UNUSABLE
        ComponentTile anotherTile = new Cabine(TileName.CABINE, "02.png", 2, 3,3, 2);
        shipBoard.putObjectIn(unusableSpace, anotherTile);
        assertNull(unusableSpace.getComponent(), "It should not be insert a component in an unusable space");
    }

    @Test
    void pickUpObjectFrom() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace reserveSpace = shipBoard.getSpace(0, 5); // (0,5) is RESERVE in FIRST_FLIGHT
        ComponentTile tile = new Cabine(TileName.CABINE, "01.png", 2, 1,3, 2);
        reserveSpace.insertComponent(tile);
        ComponentTile picked = shipBoard.pickUpObjectFrom(0, 5);
        assertEquals(tile, picked, "The component should be removed from the reserve space");
        assertNull(reserveSpace.getComponent(), "The reserve space should be empty after the remove");

        ShipBoardSpace usableSpace = shipBoard.getSpace(1, 2);
        usableSpace.insertComponent(tile);
        ComponentTile pickedFromUsable = shipBoard.pickUpObjectFrom(1, 2);
        assertNull(pickedFromUsable, "It should not be permitted to remove a tile from a usable space");
        assertEquals(tile, usableSpace.getComponent(), "");
    }

    @Test
    void isHit() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        assertFalse(shipBoard.isHit(), "The ship should not be hit at the start");

        ShipBoard shipBoard2 = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        assertFalse(shipBoard2.isHit(), "he ship should not be hit at the start");
    }

    @Test
    void setHit() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        shipBoard.setHit(true);
        assertTrue(shipBoard.isHit(), "The ship should be marked as hit after setHit(true)");

        shipBoard.setHit(false);
        assertFalse(shipBoard.isHit(), "The ship should be marked as not hit after setHit(false)");
    }


    @Test
    void numExposedConnectors0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(4, exposed);
    }

    @Test
    void numExposedConnectors1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cabine cabine = new Cabine(TileName.CABINE, "01.png", 2, 1,3, 2);
        cabine.setDirection("nord");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(6, exposed);
    }

    @Test
    void numExposedConnectors2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cabine cabine = new Cabine(TileName.CABINE, "01.png", 2, 0,0, 2);
        cabine.setDirection("nord");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(5, exposed);
    }

    @Test
    void numExposedConnectors4() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(2, 4);
        Cabine cabine1 = new Cabine(TileName.CABINE, "01.png", 1, 0,2, 1);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 2, 2,1, 2);
        Cabine cabine3 = new Cabine(TileName.CABINE, "03.png", 1, 0,1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 1, 1,1, 2);
        shipBoard.putObjectIn(space1, cabine1);
        shipBoard.putObjectIn(space2, cabine2);
        shipBoard.putObjectIn(space3, cabine3);
        shipBoard.putObjectIn(space4, cabine4);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(8, exposed);
    }

    @Test
    void numExposedConnectors5() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(2, 4);
        Cannon cannon1 = new Cannon(CANNON, "01.png", 5, 0,2, 1, 2);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 2, 2,1, 2);
        Cabine cabine3 = new Cabine(TileName.CABINE, "03.png", 1, 0,1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 1, 1,1, 2);
        shipBoard.putObjectIn(space1, cannon1);
        shipBoard.putObjectIn(space2, cabine2);
        shipBoard.putObjectIn(space3, cabine3);
        shipBoard.putObjectIn(space4, cabine4);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(7, exposed);
    }

    @Test
    void numExposedConnectors6() { // es. pag 8 of the instructions
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"blue");
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Battery battery8 = new Battery(TileName.BATTERY, "08.png", 2, 2,1, 0, 3);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, battery8);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(8, exposed);
    }

    @Test
    void numExposedConnectors7() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cabine cabine = new Cabine(TileName.CABINE, "01.png", 2, 0,0, 2);
        cabine.setDirection("sud");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(4, exposed);
    }

    @Test
    void numExposedConnectors8() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cabine cabine = new Cabine(TileName.CABINE, "01.png", 2, 1,1, 2);
        cabine.setDirection("est");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(6, exposed);
    }

    @Test
    void numExposedConnectors9() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cabine cabine = new Cabine(TileName.CABINE, "01.png", 0, 0,2, 0);
        cabine.setDirection("nord");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(3, exposed);
    }

    @Test
    void numExposedConnectors11() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cabine cabine = new Cabine(TileName.CABINE, "01.png", 1, 0,2, 3);
        cabine.setDirection("ovest");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(5, exposed);
    }

    @Test
    void numExposedConnectors12() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cannon cannon = new Cannon(TileName.CABINE, "01.png", 5, 2,1, 3, 1);
        shipBoard.putObjectIn(space, cannon);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(5, exposed);
    }

    @Test
    void numExposedConnectors13() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cannon cannon = new Cannon(TileName.CABINE, "01.png", 5, 2,1, 3, 1);
        cannon.setDirection("est");
        shipBoard.putObjectIn(space, cannon);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(5, exposed);
    }

    @Test
    void numExposedConnectors14() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cannon cannon = new Cannon(TileName.CABINE, "01.png", 5, 2,1, 3, 1);
        cannon.setDirection("ovest");
        shipBoard.putObjectIn(space, cannon);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(5, exposed);
    }

    @Test
    void numExposedConnectors15() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(3, 3);
        Cannon cannon = new Cannon(TileName.CABINE, "01.png", 5, 2,1, 3, 1);
        cannon.setDirection("sud");
        shipBoard.putObjectIn(space, cannon);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(5, exposed);
    }

    @Test
    void numExposedConnectors16() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(3, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2,4);
        Cabine cabine  = new Cabine(TileName.CABINE, "01.png", 2, 2,1, 3);
        Cabine cabine2=new Cabine(TileName.CABINE, "02.png", 2, 0,0, 3);
        cabine.setDirection("sud");
        cabine2.setDirection("ovest");
        shipBoard.putObjectIn(space, cabine);
        int exposed = shipBoard.numExposedConnectors();
        assertEquals(6, exposed);
    }

    @Test
    void activateCannon0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cannon cannon = new Cannon(CANNON, "01.png", 5, 0,2, 3, 1); // Cannone singolo
        space.insertComponent(cannon);
        assertTrue(cannon.getActive(), "The cannon should be already active");
        int result = shipBoard.activateCannon(6, 7);
        assertEquals(2, result, "The activation doesn't succeed because it's already active");
        assertTrue(cannon.getActive(), "The cannon should be active");
    }

    @Test
    void activateCannon1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Cannon cannon = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2);
        space.insertComponent(cannon);
        assertFalse(cannon.getActive(), "The cannon shouldn't be active");
        int result = shipBoard.activateCannon(6, 7);
        assertEquals(1, result, "The activation should succeed");
        assertTrue(cannon.getActive(), "The cannon should be active");
        int result2 = shipBoard.activateCannon(6, 7);
        assertEquals(2, result2, "The cannon should be active");
        assertTrue(cannon.getActive(), "The cannon should be active");
    }

    @Test
    void activateCannon2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        Cannon cannon1 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 1, 2,2, 3);
        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);

        int result = shipBoard.activateCannon(6, 8);
        assertEquals(3, result, "It should not be possible to activate a double cannon");
    }

    @Test
    void activateCannon3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        Cannon cannon1 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 1, 2,2, 3);
        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);

        int result = shipBoard.activateCannon(5, 4);
        assertEquals(3, result, "It should not be possible to activate a non-cannon");
        int result2 = shipBoard.activateCannon(5, 7);
        assertEquals(3, result2, "It should not be possible to activate a non-cannon");
    }

    @Test
    void calculateFireStrength() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(2, 4);
        Cannon cannon1 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 1); // Single
        Cannon cannon2 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 1); // Single
        Cannon cannon3 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2); // Double
        Cannon cannon4 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2); // Double
        cannon4.setDirection("est");
        space1.insertComponent(cannon1);
        space2.insertComponent(cannon2);
        space3.insertComponent(cannon3);
        space4.insertComponent(cannon4);
        double strength = shipBoard.calculateFireStrength();
        assertEquals(2.0, strength, "Fire strength: 1 (single cannon nord) + 1 (single cannon nord)");

        shipBoard.activateCannon(6, 8);
        double strength2 = shipBoard.calculateFireStrength();
        assertEquals(4.0, strength2, "Fire strength: 1 (single cannon nord) + 1 (single cannon nord) + 2 (single cannon nord) + 3 (single cannon nord)");

        shipBoard.activateCannon(6, 8);
        shipBoard.activateCannon(7, 8);
        double strength3 = shipBoard.calculateFireStrength();
        assertEquals(5.0, strength3, "Fire strength: 1 (single cannon nordd) + 1 (single cannon nord) + 2 (single cannon nord) + 1 (double cannon est)");

        ShipBoardSpace space5 = shipBoard.getSpace(4, 1);
        Cannon cannon5 = new Cannon(CANNON, "05.png", 5, 0,2, 3, 1); // Single
        cannon5.setDirection("sud");
        space5.insertComponent(cannon5);
        double strength4 = shipBoard.calculateFireStrength();
        assertEquals(5.5, strength4, "Forza di fuoco: 1 (single cannon nord) + 1 (single cannon nord) + 2 (single cannon nord) + 1 (double cannon est) + 0.5 (single cannon sud)");
    }

    @Test
    void calculateFireStrength2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        Cannon cannon1 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 1);
        space1.insertComponent(cannon1);

        shipBoard.setHasPurpleAlien(true);
        double strength = shipBoard.calculateFireStrength();
        assertEquals(3, strength);

        ShipBoard shipBoard2 = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        shipBoard2.setHasPurpleAlien(true);
        double strength2 = shipBoard2.calculateFireStrength();
        assertEquals(0, strength2);

    }

    @Test
    void restoreCannons() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space5 = shipBoard.getSpace(4, 1);

        Cannon cannon1 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 1); // Single
        Cannon cannon2 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 1); // Single
        Cannon cannon3 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2); // Double
        Cannon cannon4 = new Cannon(CANNON, "01.png", 5, 0,2, 3, 2); // Double
        Cannon cannon5 = new Cannon(CANNON, "05.png", 5, 0,2, 3, 1); // Single
        cannon4.setDirection("est");
        cannon5.setDirection("sud");
        space1.insertComponent(cannon1);
        space2.insertComponent(cannon2);
        space3.insertComponent(cannon3);
        space4.insertComponent(cannon4);
        space5.insertComponent(cannon5);

        shipBoard.restoreCannons();
        assertTrue(cannon1.getActive(), "The cannon should be active");
        assertTrue(cannon2.getActive(), "The cannon should be active");
        assertFalse(cannon3.getActive(), "The double cannon shouldn't be active");
        assertFalse(cannon4.getActive(), "The double cannon shouldn't be active");
        assertTrue(cannon5.getActive(), "The cannon should be active");
    }

    @Test
    void activateEngine0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Engine engine = new Engine(TileName.ENGINE, "01.png", 1, 0,5, 3, 1); // Single engine
        space.insertComponent(engine);
        assertTrue(engine.getActive(), "The engine should be active");
        int result = shipBoard.activateEngine(6, 7);
        assertEquals(2, result, "The activation doesn't succeed because it's already active");
        assertTrue(engine.getActive(), "The engine should be active");
    }

    @Test
    void activateEngine1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 2);
        Engine engine = new Engine(TileName.ENGINE, "01.png", 5, 0,2, 3, 2); // Double engine
        space.insertComponent(engine);
        assertFalse(engine.getActive(), "The double engine shouldn't be active");
        int result = shipBoard.activateEngine(6, 6);
        assertEquals(1, result, "The activation should succeed");
        assertTrue(engine.getActive(), "The engine should be active");
        int result2 = shipBoard.activateEngine(6, 6);
        assertEquals(2, result2, "The engine should be already active");
        assertTrue(engine.getActive(), "The engine should be active");
    }

    @Test
    void activateEngine2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        Engine engine1 = new Engine(CANNON, "01.png", 5, 0,2, 3, 2);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 1, 2,2, 3);
        space1.insertComponent(engine1);
        space2.insertComponent(cabine2);

        int result = shipBoard.activateEngine(6, 8);
        assertEquals(3, result, "It should not be possible activate a non-cannon");
    }

    @Test
    void activateEngine3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        Cannon cannon1 = new Cannon(TileName.ENGINE, "01.png", 5, 0,2, 3, 2);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 1, 2,2, 3);
        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);

        int result = shipBoard.activateEngine(5, 4);
        assertEquals(3, result, "It should not be possible activate a non-engine");
        int result2 = shipBoard.activateEngine(5, 7);
        assertEquals(3, result2, "It should not be possible activate a non-engine");
    }


    @Test
    void calculateEngineStrength() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 4);
        Engine engine1 = new Engine(TileName.ENGINE, "01.png", 0, 0,5, 3, 1);
        Engine engine2 = new Engine(TileName.ENGINE, "02.png", 0, 0,5, 3, 2);
        Engine engine3 = new Engine(TileName.ENGINE, "03.png", 0, 0,5, 3, 2);
        space1.insertComponent(engine1);
        space2.insertComponent(engine2);
        space3.insertComponent(engine3);
        int strength = shipBoard.calculateEngineStrength();
        assertEquals(1, strength, "Engine strength: 1 (single)");

        shipBoard.activateEngine(8, 7);
        int strength2 = shipBoard.calculateEngineStrength();
        assertEquals(3, strength2, "Engine strength: 1 (single) + 2 (double)");

        shipBoard.activateEngine(8, 8);
        int strength3 = shipBoard.calculateEngineStrength();
        assertEquals(5, strength3, "Engine strength: 1 (single) + 2 (double) + 2 (double)");
    }

    @Test
    void
    calculateEngineStrength2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 2);
        Engine engine1 = new Engine(TileName.ENGINE, "01.png", 0, 0,5, 3, 1);
        space1.insertComponent(engine1);

        shipBoard.setHasBrownAlien(true);
        assertEquals(3, shipBoard.calculateEngineStrength());

        ShipBoard shipBoard2 = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        shipBoard2.setHasBrownAlien(true);
        double strength2 = shipBoard2.calculateEngineStrength();
        assertEquals(0, strength2);
    }

    @Test
    void restoreEngines() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 4);
        Engine engine1 = new Engine(TileName.ENGINE, "01.png", 0, 0,5, 3, 1);
        Engine engine2 = new Engine(TileName.ENGINE, "02.png", 0, 0,5, 3, 2);
        Engine engine3 = new Engine(TileName.ENGINE, "03.png", 0, 0,5, 3, 2);
        space1.insertComponent(engine1);
        space2.insertComponent(engine2);
        space3.insertComponent(engine3);
        shipBoard.activateEngine(8, 7);
        shipBoard.activateEngine(8, 8);

        shipBoard.restoreEngines();
        assertTrue(engine1.getActive(), "The double engine should be active");
        assertFalse(engine2.getActive(), "The double engine should be deactivated");
        assertFalse(engine3.getActive(), "The double engine should be deactivated");
    }

    @Test
    void activateShield0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 3);
        Shield shield = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        space.insertComponent(shield);
        assertFalse(shield.getActive(), "The shield shouldn't be active");
        int result = shipBoard.activateShield(6, 7);
        assertEquals(1, result);
        assertTrue(shield.getActive(), "The shield should be active");

        int result2 = shipBoard.activateShield(6, 7);
        assertEquals(2, result2, "The shield should be already active");
        assertTrue(shield.getActive(), "The shield should be already active");
    }

    @Test
    void activateShield1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        Shield shield1 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        Cabine cabine2 = new Cabine(TileName.CABINE, "02.png", 1, 2,2, 3);
        space1.insertComponent(shield1);
        space2.insertComponent(cabine2);

        int result = shipBoard.activateShield(6, 8);
        assertEquals(3, result, "It should not be possible activate a non-engine");
    }

    @Test
    void restoreShields() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 4);
        Shield shield1 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        Shield shield2 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        Shield shield3 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        space1.insertComponent(shield1);
        space2.insertComponent(shield2);
        space3.insertComponent(shield3);
        shipBoard.activateShield(8, 7);
        shipBoard.activateShield(8, 8);
        assertFalse(shield1.getActive(), "The shield should be deactivated");
        assertTrue(shield2.getActive(), "The shield should be active");
        assertTrue(shield3.getActive(), "The shield should be active");
        assertSame(true, shipBoard.getCoveredDirection().get("nord"));
        assertSame(true, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));

        shipBoard.restoreShields();
        assertFalse(shield1.getActive(), "The shield should be deactivated");
        assertFalse(shield2.getActive(), "The shield should be deactivated");
        assertFalse(shield3.getActive(), "The shield should be deactivated");
        assertSame(false, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
    }

    @Test
    void getCoveredDirection0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(0, 3);
        Shield shield1 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        shield1.setDirection("nord");
        space.insertComponent(shield1);
        assertSame(false, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
        shipBoard.activateShield(5, 7);

        assertSame(true, shipBoard.getCoveredDirection().get("nord"));
        assertSame(true, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
    }


    @Test
    void getCoveredDirection1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(4, 2);
        Shield shield1 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        Shield shield2 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        shield1.setDirection("nord");
        shield2.setDirection("est");
        space1.insertComponent(shield1);
        space2.insertComponent(shield2);
        assertSame(false, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
        shipBoard.activateShield(5, 7);
        assertSame(true, shipBoard.getCoveredDirection().get("nord"));
        assertSame(true, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
        shipBoard.activateShield(9, 6);
        assertSame(true, shipBoard.getCoveredDirection().get("nord"));
        assertSame(true, shipBoard.getCoveredDirection().get("est"));
        assertSame(true, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
    }

    @Test
    void getCoveredDirection2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(4, 2);
        Shield shield1 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        Shield shield2 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        shield1.setDirection("ovest");
        shield2.setDirection("est");
        space1.insertComponent(shield1);
        space2.insertComponent(shield2);
        assertSame(false, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
        shipBoard.activateShield(5, 7);
        assertSame(true, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(true, shipBoard.getCoveredDirection().get("ovest"));
        shipBoard.activateShield(9, 6);
        assertSame(true, shipBoard.getCoveredDirection().get("nord"));
        assertSame(true, shipBoard.getCoveredDirection().get("est"));
        assertSame(true, shipBoard.getCoveredDirection().get("sud"));
        assertSame(true, shipBoard.getCoveredDirection().get("ovest"));
    }

    @Test
    void getCoveredDirection3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(0, 3);
        Shield shield1 = new Shield(TileName.SHIELD, "01.png", 5, 0,2, 3);
        shield1.setDirection("sud");
        space.insertComponent(shield1);
        assertSame(false, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(false, shipBoard.getCoveredDirection().get("sud"));
        assertSame(false, shipBoard.getCoveredDirection().get("ovest"));
        shipBoard.activateShield(5, 7);

        assertSame(false, shipBoard.getCoveredDirection().get("nord"));
        assertSame(false, shipBoard.getCoveredDirection().get("est"));
        assertSame(true, shipBoard.getCoveredDirection().get("sud"));
        assertSame(true, shipBoard.getCoveredDirection().get("ovest"));
    }

    @Test
    void removeComponent0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ComponentTile tile1 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        space1.insertComponent(tile1);
        shipBoard.removeComponent("nord", 7);
        assertNull(space1.getComponent(), "The tile should be removed");
        assertEquals(1, shipBoard.getNumLostTiles(), "The component should be in the reserve");
    }

    @Test
    void removeComponent1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 3);
        ComponentTile tile1 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        space1.insertComponent(tile1);
        shipBoard.removeComponent("sud", 7);
        assertNull(space1.getComponent(), "The tile should be removed");
        assertEquals(1, shipBoard.getNumLostTiles(), "The component should be in the reserve");
    }


    @Test
    void removeComponent2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ComponentTile tile1 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile2 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        space1.insertComponent(tile1);
        space2.insertComponent(tile2);
        shipBoard.removeComponent("est", 6);
        assertNull(space2.getComponent(), "The tile should be removed");
        assertEquals(1, shipBoard.getNumLostTiles(), "The component should be in the reserve");
        shipBoard.removeComponent("est", 6);
        assertNull(space1.getComponent(), "The tile should be removed");
        assertNull(space2.getComponent(), "The tile should be removed");
        assertEquals(2, shipBoard.getNumLostTiles(), "The tile should be removed");

    }

    @Test
    void removeComponent3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ComponentTile tile1 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile2 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        space1.insertComponent(tile1);
        space2.insertComponent(tile2);
        shipBoard.removeComponent("ovest", 6);
        assertNull(space1.getComponent(), "The tile should be removed");
        assertEquals(1, shipBoard.getNumLostTiles(), "The tile should be removed");
    }

    @Test
    void removeComponent4() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 2);
        ComponentTile tile1 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile2 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile3 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile4 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        space1.insertComponent(tile1);
        space2.insertComponent(tile2);
        space3.insertComponent(tile3);
        space4.insertComponent(tile4);
        shipBoard.removeComponent("nord", 5);
        assertNull(space1.getComponent(), "The tile should be removed");
        assertEquals(1, shipBoard.getNumLostTiles(), "The component should be in the reserve");
        shipBoard.removeComponent("ovest", 7);
        assertNull(space2.getComponent(), "The tile should be removed");
        assertEquals(2, shipBoard.getNumLostTiles(), "The component should be in the reserve");
    }

    @Test
    void removeComponent5() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(4, 5);
        ComponentTile tile1 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile2 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile3 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        ComponentTile tile4 = new Cabine(TileName.CABINE, "01.png", 2, 0,2, 3);
        space1.insertComponent(tile1);
        space2.insertComponent(tile2);
        space3.insertComponent(tile3);
        space4.insertComponent(tile4);
        shipBoard.removeComponent("ovest", 9);
        assertNull(space1.getComponent(), "The tile should be removed");
        assertEquals(1, shipBoard.getNumLostTiles(), "The component should be in the reserve");
        shipBoard.removeComponent("ovest", 9);
        assertNull(space2.getComponent(), "The tile should be removed");
        assertEquals(2, shipBoard.getNumLostTiles(), "The component should be in the reserve");
        shipBoard.removeComponent("ovest", 9);
        assertNull(space3.getComponent(), "The tile should be removed");
        assertEquals(3, shipBoard.getNumLostTiles(), "The component should be in the reserve");
        shipBoard.removeComponent("ovest", 9);
        assertNull(space4.getComponent(), "The tile should be removed");
        assertEquals(4, shipBoard.getNumLostTiles(), "The component should be in the reserve");
    }


    @Test
    void checkDoubleCannonMeteor0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        Cannon cannon1 = new Cannon(CANNON, "0", 5, 1, 1, 1, 2);
        Cannon cannon2 = new Cannon(CANNON, "0", 5, 1, 1, 1, 2);
        Cannon cannon3 = new Cannon(CANNON, "0", 5, 1, 1, 1, 2);
        cannon1.setDirection("nord");
        cannon2.setDirection("sud");
        cannon3.setDirection("sud");
        space1.insertComponent(cannon1);
        space2.insertComponent(cannon2);
        space3.insertComponent(cannon3);

        boolean result = shipBoard.checkDoubleCannonMeteor("nord", 6);
        assertFalse(result, "It should not be a double cannon active in nord direction");

        shipBoard.activateCannon(9, 6);
        boolean result2 = shipBoard.checkDoubleCannonMeteor("nord", 6);
        assertTrue(result2, "It should not be a double cannon active in nord direction");

        shipBoard.activateCannon(9, 5);
        boolean result3 = shipBoard.checkDoubleCannonMeteor("nord", 5);
        assertFalse(result3, "It should not be a double cannon active in nord direction");

        boolean result4 = shipBoard.checkDoubleCannonMeteor("sud", 5);
        assertTrue(result4, "It should be a double cannon active in sud direction");

        boolean result5 = shipBoard.checkDoubleCannonMeteor("sud", 6);
        assertTrue(result5, "It should be a double cannon active in sud direction");

        boolean result6 = shipBoard.checkDoubleCannonMeteor("sud", 7);
        assertFalse(result6, "It should not be a double cannon active in sud direction");

        shipBoard.activateCannon(6, 8);
        boolean result7 = shipBoard.checkDoubleCannonMeteor("sud", 7);
        assertTrue(result7, "It should be a double cannon active in sud direction");
    }

    @Test
    void checkDoubleCannonMeteor1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 5);
        Cannon cannon1 = new Cannon(CANNON, "0", 5, 1, 1, 1, 2); // Double
        Cannon cannon2 = new Cannon(CANNON, "0", 5, 1, 1, 1, 2); // Double
        Cannon cannon3 = new Cannon(CANNON, "0", 5, 1, 1, 1, 2); // Double
        cannon1.setDirection("est");
        cannon2.setDirection("ovest");
        cannon3.setDirection("est");
        space1.insertComponent(cannon1);
        space2.insertComponent(cannon2);
        space3.insertComponent(cannon3);

        boolean result = shipBoard.checkDoubleCannonMeteor("est", 6);
        assertFalse(result, "It should not be a double cannon active in est direction");
        shipBoard.activateCannon(6, 6);
        result = shipBoard.checkDoubleCannonMeteor("est", 6);
        assertTrue(result, "It should be a double cannon active in est direction");
        result = shipBoard.checkDoubleCannonMeteor("est", 5);
        assertTrue(result, "It should be a double cannon active in est direction");
        result = shipBoard.checkDoubleCannonMeteor("est", 7);
        assertTrue(result, "It should be a double cannon active in est direction");
        result = shipBoard.checkDoubleCannonMeteor("est", 8);
        assertFalse(result, "It should not be a double cannon active in est direction");
        shipBoard.activateCannon(8, 9);
        result = shipBoard.checkDoubleCannonMeteor("est", 8);
        assertTrue(result, "It should be a double cannon active in est direction");
        result = shipBoard.checkDoubleCannonMeteor("est", 9);
        assertTrue(result, "It should be a double cannon active in est direction");

        result = shipBoard.checkDoubleCannonMeteor("ovest", 8);
        assertFalse(result, "It should not be a double cannon active in nord direction");
        shipBoard.activateCannon(8, 8);
        result = shipBoard.checkDoubleCannonMeteor("ovest", 8);
        assertTrue(result, "It should be a double cannon active in ovest direction");
        result = shipBoard.checkDoubleCannonMeteor("ovest", 9);
        assertTrue(result, "It should be a double cannon active in ovest direction");
        result = shipBoard.checkDoubleCannonMeteor("ovest", 7);
        assertTrue(result, "It should be a double cannon active in ovest direction");
        result = shipBoard.checkDoubleCannonMeteor("ovest", 6);
        assertFalse(result, "It should not be a double cannon active in ovest direction");
    }

    @Test
    void getSpacePositions() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(2, 2);
        int[] position = shipBoard.getSpacePositions(space);
        assertEquals(2, position[0], "The row should be 0");
        assertEquals(2, position[1], "The colum should be 1");
        space = shipBoard.getSpace(0, 0);
        position = shipBoard.getSpacePositions(space);
        assertEquals(0, position[0], "The row should be 0");
        assertEquals(0, position[1], "The colum should be 1 0");
        space = shipBoard.getSpace(0, 6);
        position = shipBoard.getSpacePositions(space);
        assertEquals(0, position[0], "The row should be 0");
        assertEquals(6, position[1], "The colum should be 1e 6");
    }

    @Test
    void calculateIfHit0() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        Meteor meteor1 = new Meteor("nord", 1);
        Meteor meteor2 = new Meteor("est", 1);
        Meteor meteor3 = new Meteor("sud", 1);
        Meteor meteor4 = new Meteor("ovest", 1);

        when(card.getMeteor()).thenReturn(new ArrayList<>(Arrays.asList(meteor1, meteor2, meteor3, meteor4)));

        Cabine cabine1 = mock(Cabine.class);
        when(cabine1.getConnector("up")).thenReturn(1);
        when(cabine1.getDirection()).thenReturn("nord");
        Cabine cabine2 = mock(Cabine.class);
        when(cabine2.getConnector("left")).thenReturn(1);
        when(cabine2.getDirection()).thenReturn("sud");
        Cabine cabine3 = mock(Cabine.class);
        when(cabine3.getConnector("right")).thenReturn(1);
        when(cabine3.getDirection()).thenReturn("est");
        Cabine cabine4 = mock(Cabine.class);
        when(cabine4.getConnector("down")).thenReturn(1);
        when(cabine4.getDirection()).thenReturn("est");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);
        space4.insertComponent(cabine4);

        when(card.getCounter()).thenReturn(1);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(2);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(3);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(4);
        shipBoard.calculateIfHit(card, 8);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit1() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("nord", 1);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cabine cabine = mock(Cabine.class);
        when(cabine.getConnector("left")).thenReturn(0);
        when(cabine.getDirection()).thenReturn("est");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 2);
        space.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 6);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit2() {
        // Mock della carta MeteorSwarm
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("sud", 1);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Engine engine = mock(Engine.class);
        when(engine.getConnector("down")).thenReturn(5);
        when(engine.getDirection()).thenReturn("nord");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 2);
        space.insertComponent(engine);

        shipBoard.calculateIfHit(card, 6);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit3() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("nord", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("nord");
        when(cannon.getName()).thenReturn(CANNON);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(2, 6);
        space.insertComponent(cannon);

        shipBoard.calculateIfHit(card, 10);

        assertFalse(shipBoard.isHit(), "The ship should not be hit thanks to the cannon");
    }

    @Test
    void calculateIfHit4() {
        // Mock della carta MeteorSwarm
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("est", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("nord");
        when(cannon.getName()).thenReturn(CANNON);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(0, 2);
        space.insertComponent(cannon);

        shipBoard.calculateIfHit(card, 5);

        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }


    @Test
    void calculateIfHit5() {
        // Mock della carta MeteorSwarm
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("sud", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("sud");
        when(cannon.getName()).thenReturn(CANNON);
        Cabine cabine = mock(Cabine.class);
        when(cabine.getName()).thenReturn(CABINE);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 1);
        space1.insertComponent(cannon);
        space2.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 6);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");

        shipBoard.setHit(false);
        shipBoard.calculateIfHit(card, 5);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit6() {
        // Mock della carta MeteorSwarm
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("est", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("est");
        when(cannon.getName()).thenReturn(CANNON);
        Cabine cabine = mock(Cabine.class);
        when(cabine.getName()).thenReturn(CABINE);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 5);
        space1.insertComponent(cannon);
        space2.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 5);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");

        shipBoard.setHit(false);
        shipBoard.calculateIfHit(card, 6);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit7() {
        // Mock della carta MeteorSwarm
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("ovest", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("ovest");
        when(cannon.getName()).thenReturn(CANNON);
        Cabine cabine = mock(Cabine.class);
        when(cabine.getName()).thenReturn(CABINE);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 5);
        space1.insertComponent(cannon);
        space2.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 5);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");

        shipBoard.setHit(false);
        shipBoard.calculateIfHit(card, 6);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit8() {
        CombatZone card = mock(CombatZone.class);
        when(card.getCardType()).thenReturn(CardName.COMBAT_ZONE);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor1 = new Meteor("nord", 2);
        Meteor meteor2 = new Meteor("est", 2);
        Meteor meteor3 = new Meteor("sud", 2);
        Meteor meteor4 = new Meteor("ovest", 2);
        Object[] faseThree = new Object[3];
        when(card.getFaseThree()).thenReturn(faseThree);
        faseThree[2] = new ArrayList<Meteor>(Arrays.asList(meteor1, meteor2, meteor3, meteor4));

        Cabine cabine = mock(Cabine.class);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 1);
        space.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 5);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
        shipBoard.setHit(false);
        when(card.getCounter()).thenReturn(2);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
        shipBoard.setHit(false);
        when(card.getCounter()).thenReturn(3);
        shipBoard.calculateIfHit(card, 5);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
        shipBoard.setHit(false);
        when(card.getCounter()).thenReturn(4);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit9() {
        CombatZone card = mock(CombatZone.class);
        when(card.getCardType()).thenReturn(CardName.COMBAT_ZONE);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor1 = new Meteor("nord", 2);
        Object[] faseThree = new Object[3];
        when(card.getFaseThree()).thenReturn(faseThree);
        faseThree[2] = new ArrayList<Meteor>(Arrays.asList(meteor1));

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);

        shipBoard.calculateIfHit(card, 5);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }


    @Test
    void calculateIfHit10() { // Pirates
        Pirates card = mock(Pirates.class);
        when(card.getCardType()).thenReturn(CardName.PIRATES);
        when(card.getCounter()).thenReturn(1);

        Cabine cabine = mock(Cabine.class);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 1);
        space.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 5);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit11() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        Meteor meteor1 = new Meteor("nord", 1);
        Meteor meteor2 = new Meteor("nord", 1);
        Meteor meteor3 = new Meteor("nord", 1);
        Meteor meteor4 = new Meteor("nord", 1);

        when(card.getMeteor()).thenReturn(new ArrayList<>(Arrays.asList(meteor1, meteor2, meteor3, meteor4)));

        // Mock of component with exposed connectors
        Cabine cabine1 = mock(Cabine.class);
        when(cabine1.getConnector("up")).thenReturn(1);
        when(cabine1.getDirection()).thenReturn("nord");
        Cabine cabine2 = mock(Cabine.class);
        when(cabine2.getConnector("down")).thenReturn(1);
        when(cabine2.getDirection()).thenReturn("sud");
        Cabine cabine3 = mock(Cabine.class);
        when(cabine3.getConnector("left")).thenReturn(1);
        when(cabine3.getDirection()).thenReturn("est");
        Cabine cabine4 = mock(Cabine.class);
        when(cabine4.getConnector("right")).thenReturn(1);
        when(cabine4.getDirection()).thenReturn("ovest");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);
        space4.insertComponent(cabine4);

        when(card.getCounter()).thenReturn(1);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(2);
        shipBoard.calculateIfHit(card, 7);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(3);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(4);
        shipBoard.calculateIfHit(card, 9);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit12() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        Meteor meteor1 = new Meteor("est", 1);
        Meteor meteor2 = new Meteor("est", 1);
        Meteor meteor3 = new Meteor("est", 1);
        Meteor meteor4 = new Meteor("est", 1);

        when(card.getMeteor()).thenReturn(new ArrayList<>(Arrays.asList(meteor1, meteor2, meteor3, meteor4)));

        Cabine cabine1 = mock(Cabine.class);
        when(cabine1.getConnector("right")).thenReturn(1);
        when(cabine1.getDirection()).thenReturn("nord");
        Cabine cabine2 = mock(Cabine.class);
        when(cabine2.getConnector("left")).thenReturn(1);
        when(cabine2.getDirection()).thenReturn("sud");
        Cabine cabine3 = mock(Cabine.class);
        when(cabine3.getConnector("up")).thenReturn(1);
        when(cabine3.getDirection()).thenReturn("est");
        Cabine cabine4 = mock(Cabine.class);
        when(cabine4.getConnector("down")).thenReturn(1);
        when(cabine4.getDirection()).thenReturn("ovest");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);
        space4.insertComponent(cabine4);

        when(card.getCounter()).thenReturn(1);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(2);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(3);
        shipBoard.calculateIfHit(card, 9);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(4);
        shipBoard.calculateIfHit(card, 8);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit13() {  //Case meteors from sud with power=1
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        Meteor meteor1 = new Meteor("sud", 1);
        Meteor meteor2 = new Meteor("sud", 1);
        Meteor meteor3 = new Meteor("sud", 1);
        Meteor meteor4 = new Meteor("sud", 1);

        when(card.getMeteor()).thenReturn(new ArrayList<>(Arrays.asList(meteor1, meteor2, meteor3, meteor4)));

        // Mock of component with exposed connectors
        Cabine cabine1 = mock(Cabine.class);
        when(cabine1.getConnector("down")).thenReturn(1);
        when(cabine1.getDirection()).thenReturn("nord");
        Cabine cabine2 = mock(Cabine.class);
        when(cabine2.getConnector("up")).thenReturn(1);
        when(cabine2.getDirection()).thenReturn("sud");
        Cabine cabine3 = mock(Cabine.class);
        when(cabine3.getConnector("right")).thenReturn(1);
        when(cabine3.getDirection()).thenReturn("est");
        Cabine cabine4 = mock(Cabine.class);
        when(cabine4.getConnector("left")).thenReturn(1);
        when(cabine4.getDirection()).thenReturn("ovest");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);
        space4.insertComponent(cabine4);

        when(card.getCounter()).thenReturn(1);
        shipBoard.calculateIfHit(card, 5);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(2);
        shipBoard.calculateIfHit(card, 8);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(3);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(4);
        shipBoard.calculateIfHit(card, 9);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit14() {  //Case meteors from ovest with power=1
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        Meteor meteor1 = new Meteor("ovest", 1);
        Meteor meteor2 = new Meteor("ovest", 1);
        Meteor meteor3 = new Meteor("ovest", 1);
        Meteor meteor4 = new Meteor("ovest", 1);

        when(card.getMeteor()).thenReturn(new ArrayList<>(Arrays.asList(meteor1, meteor2, meteor3, meteor4)));

        // Mock of component with exposed connectors
        Cabine cabine1 = mock(Cabine.class);
        when(cabine1.getConnector("left")).thenReturn(1);
        when(cabine1.getDirection()).thenReturn("nord");
        Cabine cabine2 = mock(Cabine.class);
        when(cabine2.getConnector("right")).thenReturn(1);
        when(cabine2.getDirection()).thenReturn("sud");
        Cabine cabine3 = mock(Cabine.class);
        when(cabine3.getConnector("down")).thenReturn(1);
        when(cabine3.getDirection()).thenReturn("est");
        Cabine cabine4 = mock(Cabine.class);
        when(cabine4.getConnector("up")).thenReturn(1);
        when(cabine4.getDirection()).thenReturn("ovest");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);
        space4.insertComponent(cabine4);

        when(card.getCounter()).thenReturn(1);
        shipBoard.calculateIfHit(card, 6);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(2);
        shipBoard.calculateIfHit(card, 7);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(3);
        shipBoard.calculateIfHit(card, 9);
        assertTrue(shipBoard.isHit(), "The ship should be hit");

        shipBoard.setHit(false);

        when(card.getCounter()).thenReturn(4);
        shipBoard.calculateIfHit(card, 8);
        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit15() { // With exposed connectors
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("nord", 1);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cabine cabine = mock(Cabine.class);
        when(cabine.getConnector("left")).thenReturn(1);
        when(cabine.getDirection()).thenReturn("est");

        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(1, 2);
        space.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 6);

        assertTrue(shipBoard.isHit(), "The ship should be hit");
    }

    @Test
    void calculateIfHit16() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("sud", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("sud");
        when(cannon.getName()).thenReturn(CANNON);
        Cabine cabine = mock(Cabine.class);
        when(cabine.getName()).thenReturn(CABINE);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 0);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 1);
        space1.insertComponent(cannon);
        space2.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 5);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");

        shipBoard.setHit(false);
        shipBoard.calculateIfHit(card, 5);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit17() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("est", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("est");
        when(cannon.getName()).thenReturn(CANNON);
        Cabine cabine = mock(Cabine.class);
        when(cabine.getName()).thenReturn(CABINE);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        space1.insertComponent(cannon);
        space2.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 8);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");

        shipBoard.setHit(false);
        shipBoard.calculateIfHit(card,6);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void calculateIfHit18() {
        MeteorSwarm card = mock(MeteorSwarm.class);
        when(card.getCardType()).thenReturn(CardName.METEOR_SWARM);
        when(card.getCounter()).thenReturn(1);
        Meteor meteor = new Meteor("ovest", 2);
        when(card.getMeteor()).thenReturn(new ArrayList<Meteor>(Arrays.asList(meteor)));

        Cannon cannon = mock(Cannon.class);
        when(cannon.isDouble()).thenReturn(false);
        when(cannon.getDirection()).thenReturn("ovest");
        when(cannon.getName()).thenReturn(CANNON);
        Cabine cabine = mock(Cabine.class);
        when(cabine.getName()).thenReturn(CABINE);

        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 2);
        space1.insertComponent(cannon);
        space2.insertComponent(cabine);

        shipBoard.calculateIfHit(card, 5);

        assertFalse(shipBoard.isHit(), "The ship should not be hit");

        shipBoard.setHit(false);
        shipBoard.calculateIfHit(card, 6);
        assertFalse(shipBoard.isHit(), "The ship should not be hit");
    }

    @Test
    void applyEpidemic0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine1.setNumFigures(2);
        space1.insertComponent(cabine1);

        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine2.setHasBrownAlien(true);
        space2.insertComponent(cabine2);

        ShipBoardSpace space3 = shipBoard.getSpace(1, 3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine3.setHasPurpleAlien(true);
        space3.insertComponent(cabine3);

        StartingCabine startingCabine = (StartingCabine) shipBoard.getSpace(2,3).getComponent();
        startingCabine.setNumFigures(2);
        shipBoard.applyEpidemic();
        assertTrue(cabine1.isConnectedWithOccupiedCabine(), "The cabine 1 should be connected to another occupied cabine");
        assertTrue(cabine2.isConnectedWithOccupiedCabine(), "The cabine 2 should be connected to another occupied cabine");
        assertTrue(cabine3.isConnectedWithOccupiedCabine(), "The cabine 3 should be connected to another occupied cabine");
        assertTrue(startingCabine.isConnectedWithOccupiedCabine());
    }

    @Test
    void applyEpidemic1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine1.setNumFigures(2);
        space1.insertComponent(cabine1);

        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine2.setNumFigures(0);
        space2.insertComponent(cabine2);

        StartingCabine startingCabine = (StartingCabine) shipBoard.getSpace(2,3).getComponent();
        startingCabine.setNumFigures(0);
        shipBoard.applyEpidemic();
        assertFalse(cabine1.isConnectedWithOccupiedCabine(), "The cabine 1 should not be connected to another occupied cabine");
        assertFalse(cabine2.isConnectedWithOccupiedCabine(), "The cabine 2 should not be connected to another occupied cabine");
        assertFalse(startingCabine.isConnectedWithOccupiedCabine());
    }

    @Test
    void applyEpidemic2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 4);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine1.setHasPurpleAlien(true);
        space1.insertComponent(cabine1);

        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine2.setHasBrownAlien(true);
        space2.insertComponent(cabine2);

        ShipBoardSpace space3 = shipBoard.getSpace(3, 4);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine3.setHasBrownAlien(true);
        space3.insertComponent(cabine3);

        StartingCabine startingCabine = (StartingCabine) shipBoard.getSpace(2,3).getComponent();
        startingCabine.setNumFigures(2);
        shipBoard.applyEpidemic();
        assertTrue(cabine1.isConnectedWithOccupiedCabine(), "The cabine 1 should be connected to another occupied cabine");
        assertTrue(cabine2.isConnectedWithOccupiedCabine(), "The cabine 2 should be connected to another occupied cabine");
        assertTrue(cabine3.isConnectedWithOccupiedCabine(), "The cabine 3 should be connected to another occupied cabine");
    }

    @Test
    void applyEpidemic3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);

        ShipBoardSpace space2 = shipBoard.getSpace(3, 3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine2.setHasPurpleAlien(true);
        space2.insertComponent(cabine2);
        StartingCabine startingCabine = (StartingCabine) shipBoard.getSpace(2,3).getComponent();
        startingCabine.setNumFigures(2);
        shipBoard.applyEpidemic();
        assertTrue(cabine2.isConnectedWithOccupiedCabine(), "The cabine 2 should be connected to another occupied cabine");
        assertTrue(startingCabine.isConnectedWithOccupiedCabine());
    }



    @Test
    void removeEpidemicFigures0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(2, 1);
        Cabine cabine = new Cabine(CABINE, "00", 3,3,3,3);
        cabine.setNumFigures(2);
        cabine.setConnectedWithOccupiedCabine(true);
        space.insertComponent(cabine);

        StartingCabine startingCabine = (StartingCabine) shipBoard.getSpace(2,3).getComponent();
        startingCabine.setNumFigures(2);
        startingCabine.setConnectedWithOccupiedCabine(true);

        shipBoard.removeEpidemicFigures();
        assertEquals(1, cabine.getNumFigures(), "The figures should be removed");
        assertFalse(cabine.isConnectedWithOccupiedCabine(), "The flag should be reset");
        assertEquals(1, startingCabine.getNumFigures(), "The figures should be removed");
        assertFalse(startingCabine.isConnectedWithOccupiedCabine(), "The flag should be reset");
    }

    @Test
    void removeEpidemicFigures1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space = shipBoard.getSpace(2, 1);
        Cabine cabine = new Cabine(CABINE, "00", 3,3,3,3);
        cabine.setHasPurpleAlien(true);
        space.insertComponent(cabine);
        cabine.setConnectedWithOccupiedCabine(true);

        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine2.setHasBrownAlien(true);
        space2.insertComponent(cabine2);
        cabine2.setConnectedWithOccupiedCabine(true);

        shipBoard.removeEpidemicFigures();
        assertFalse(cabine.isConnectedWithOccupiedCabine(), "The flag should be reset");
        assertFalse(cabine.getHasPurpleAlien());
        assertFalse(cabine2.isConnectedWithOccupiedCabine());
        assertFalse(cabine2.getHasBrownAlien());
        assertFalse(shipBoard.getHasBrownAlien());
        assertFalse(shipBoard.getHasBrownAlien());

    }



    @Test
    void checkShipBoardParts0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        space1.insertComponent(cabine1);

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(0, space2.getCheck(), "The space (2,3) should be marked as not checked");
    }

    @Test
    void checkShipBoardParts1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine3.setDirection("est");
        cabine4.setDirection("ovest");
        cabine5.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,0,3,3);
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(0, space3.getCheck(), "The space (2,3) should be marked as not checked");
    }


    @Test
    void checkShipBoardParts3() { // es. pag. 8 of instructions
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space7 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space8 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space9 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space10 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space11 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space12 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space13 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space14 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space15 = shipBoard.getSpace(4, 4);

        Cannon cannon1 = new Cannon(CANNON, "00", 5,2,0,1,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 1,2,1,2);
        Cannon cannon3 = new Cannon(CANNON, "00", 5,0,1,0,2);
        Engine engine4 = new Engine(ENGINE, "00", 2,0,5,0,2);
        engine4.setDirection("est");
        Cabine cabine5 = new Cabine(CABINE, "00", 0,1,0,3);
        Shield shield6 = new Shield(SHIELD, "00", 1,2,1,2);
        Engine engine7 = new Engine(ENGINE, "00", 0,0,5,3,1);
        Cargo cargo8 = new Cargo(CARGO, "00", 0,0,1,0,3, "blue");
        Cannon cannon9 = new Cannon(CANNON, "00", 5,1,0,0,1);
        Cargo cargo10 = new Cargo(CARGO, "00", 0,3,0,3,1, "red");
        StructuralModule structuralModule11 = new StructuralModule(STRUCTURAL_MODULE, "00", 3,1,2,3);
        Cannon cannon12 = new Cannon(CANNON, "00", 5,0,1,0,1);
        cannon12.setDirection("est");
        Engine engine13 = new Engine(ENGINE, "00", 1,1,5,0,1);
        Cannon cannon14 = new Cannon(CANNON, "00", 5,3,0,0,2);
        cannon14.setDirection("sud");
        Engine engine15 = new Engine(ENGINE, "00", 1,0,5,0,1);

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cannon3);
        space4.insertComponent(engine4);
        space5.insertComponent(cabine5);
        space6.insertComponent(shield6);
        space7.insertComponent(engine7);
        space8.insertComponent(cargo8);
        space9.insertComponent(cannon9);
        space10.insertComponent(cargo10);
        space11.insertComponent(structuralModule11);
        space12.insertComponent(cannon12);
        space13.insertComponent(engine13);
        space14.insertComponent(cannon14);
        space15.insertComponent(engine15);

        ShipBoardSpace space16 = shipBoard.getSpace(2, 3);

        shipBoard.checkShipBoardParts(1, 2);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space16.getCheck());
        assertEquals(1, space6.getCheck());
        assertEquals(1, space7.getCheck());
        assertEquals(0, space8.getCheck());
        assertEquals(1, space9.getCheck());
        assertEquals(1, space10.getCheck());
        assertEquals(1, space11.getCheck());
        assertEquals(1, space12.getCheck());
        assertEquals(0, space13.getCheck());
        assertEquals(0, space14.getCheck());
        assertEquals(1, space15.getCheck());
    }

    @Test
    void checkShipBoardParts4() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 0);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 0);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space7 = shipBoard.getSpace(4, 0);
        ShipBoardSpace space8 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space9 = shipBoard.getSpace(4, 2);

        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cannon cannon3 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Engine engine4 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine5 = new Cabine(CABINE, "00", 0,0,0,0);
        Shield shield6 = new Shield(SHIELD, "00", 3,3,3,3);
        Engine engine7 = new Engine(ENGINE, "00", 3,3,5,3,1);
        Cargo cargo8 = new Cargo(CARGO, "00", 3,3,3,3,3, "blue");
        Cannon cannon9 = new Cannon(CANNON, "00", 5,3,3,3,1);

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cannon3);
        space4.insertComponent(engine4);
        space5.insertComponent(cabine5);
        space6.insertComponent(shield6);
        space7.insertComponent(engine7);
        space8.insertComponent(cargo8);
        space9.insertComponent(cannon9);

        ShipBoardSpace space10 = shipBoard.getSpace(2, 3);

        shipBoard.checkShipBoardParts(2, 0);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(0, space5.getCheck());
        assertEquals(1, space6.getCheck());
        assertEquals(0, space7.getCheck());
        assertEquals(0, space8.getCheck());
        assertEquals(0, space9.getCheck());
        assertEquals(1, space10.getCheck());
    }

    @Test
    void checkShipBoardParts5() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 0);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 0);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space7 = shipBoard.getSpace(4, 0);
        ShipBoardSpace space8 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space9 = shipBoard.getSpace(4, 2);

        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cannon cannon3 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Engine engine4 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine5 = new Cabine(CABINE, "00", 0,0,0,0);
        Shield shield6 = new Shield(SHIELD, "00", 3,3,3,3);
        Engine engine7 = new Engine(ENGINE, "00", 3,3,5,3,1);
        Cargo cargo8 = new Cargo(CARGO, "00", 3,3,3,3,3, "blue");
        Cargo cargo9 = new Cargo(CARGO, "00", 3,3,3,3,1, "red");

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cannon3);
        space4.insertComponent(engine4);
        space5.insertComponent(cabine5);
        space6.insertComponent(shield6);
        space7.insertComponent(engine7);
        space8.insertComponent(cargo8);
        space9.insertComponent(cargo9);

        ShipBoardSpace space10 = shipBoard.getSpace(2, 3);

        shipBoard.checkShipBoardParts(2, 0);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(0, space5.getCheck());
        assertEquals(1, space6.getCheck());
        assertEquals(1, space7.getCheck());
        assertEquals(1, space8.getCheck());
        assertEquals(1, space9.getCheck());
        assertEquals(1, space10.getCheck());
    }

    @Test
    void checkShipBoardParts6() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 0);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 0);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space7 = shipBoard.getSpace(4, 0);
        ShipBoardSpace space8 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space9 = shipBoard.getSpace(4, 2);


        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cannon cannon3 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Engine engine4 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine5 = new Cabine(CABINE, "00", 0,0,0,0);
        Shield shield6 = new Shield(SHIELD, "00", 3,3,3,3);
        Engine engine7 = new Engine(ENGINE, "00", 3,3,5,3,1);
        Cargo cargo8 = new Cargo(CARGO, "00", 3,3,3,3,3, "blue");
        Cannon cannon9 = new Cannon(CANNON, "00", 5,3,3,3,1);

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cannon3);
        space4.insertComponent(engine4);
        space5.insertComponent(cabine5);
        space6.insertComponent(shield6);
        space7.insertComponent(engine7);
        space8.insertComponent(cargo8);
        space9.insertComponent(cannon9);

        ShipBoardSpace space10 = shipBoard.getSpace(2, 3);

        shipBoard.checkShipBoardParts(3, 1);
        assertEquals(0, space1.getCheck());
        assertEquals(0, space2.getCheck());
        assertEquals(0, space3.getCheck());
        assertEquals(0, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(0, space6.getCheck());
        assertEquals(0, space7.getCheck());
        assertEquals(0, space8.getCheck());
        assertEquals(0, space9.getCheck());
        assertEquals(0, space10.getCheck());
    }

    @Test
    void checkShipBoardParts7() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine3.setDirection("sud");
        cabine4.setDirection("est");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts8() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine3.setDirection("ovest");
        cabine4.setDirection("sud");
        cabine5.setDirection("ovest");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts9() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("ovest");
        cabine4.setDirection("sud");
        cabine5.setDirection("ovest");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts10() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("sud");
        cabine4.setDirection("est");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts11() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("nord");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts12() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine1.setDirection("est");
        cabine3.setDirection("est");
        cabine4.setDirection("ovest");
        cabine5.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts13() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("est");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts14() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("est");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts15() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine1.setDirection("sud");
        cabine3.setDirection("est");
        cabine4.setDirection("ovest");
        cabine5.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts16() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("ovest");
        cabine4.setDirection("sud");
        cabine5.setDirection("ovest");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts17() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("sud");
        cabine4.setDirection("est");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts18() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("nord");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts19() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("est");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts20() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        cabine1.setDirection("ovest");
        cabine3.setDirection("est");
        cabine4.setDirection("ovest");
        cabine5.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts21() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("sud");
        cabine5.setDirection("ovest");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts22() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("sud");
        cabine4.setDirection("est");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts23() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(1,1);
        ShipBoardSpace space5 = shipBoard.getSpace(3,1);
        ShipBoardSpace space6 = shipBoard.getSpace(2,0);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("nord");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(2, 1);
        assertEquals(1, space1.getCheck(), "The space (2,1) should be marked as checked");
        assertEquals(1, space2.getCheck(), "The space (2,2) should be marked as checked");
        assertEquals(1, space3.getCheck(), "The space (2,3) should be marked as checked");
        assertEquals(1, space4.getCheck(), "The space (1,1) should be marked as checked");
        assertEquals(1, space5.getCheck(), "The space (3,3) should be marked as checked");
        assertEquals(1, space6.getCheck(), "The space (2,0) should be marked as checked");
    }

    @Test
    void checkShipBoardParts24() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("sud");
        cabine4.setDirection("sud");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("nord");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts25() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("est");
        cabine4.setDirection("est");
        cabine5.setDirection("est");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("nord");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts26() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("nord");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts27() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("sud");
        cabine4.setDirection("sud");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts28() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("est");
        cabine4.setDirection("est");
        cabine5.setDirection("est");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts29() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts30() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts31() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("sud");
        cabine4.setDirection("sud");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts32() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("est");
        cabine4.setDirection("est");
        cabine5.setDirection("est");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts33() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts34() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts35() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("sud");
        cabine3.setDirection("sud");
        cabine4.setDirection("sud");
        cabine5.setDirection("sud");
        cabine2.setDirection("sud");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("ovest");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts36() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("est");
        cabine3.setDirection("est");
        cabine4.setDirection("est");
        cabine5.setDirection("est");
        cabine2.setDirection("est");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("ovest");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts37() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("ovest");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts38() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("ovest");
        cabine3.setDirection("ovest");
        cabine4.setDirection("ovest");
        cabine5.setDirection("ovest");
        cabine2.setDirection("ovest");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("ovest");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts39() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("nord");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("nord");
        cabine2.setDirection("nord");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("ovest");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts40() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("nord");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("nord");
        cabine2.setDirection("nord");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("sud");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void checkShipBoardParts41() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(4, 3);
        Cabine cabine1 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine4 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine5 = new Cabine(CABINE, "00", 3,3,3,3);
        StartingCabine startingCabine = (StartingCabine) space3.getComponent();
        cabine1.setDirection("nord");
        cabine3.setDirection("nord");
        cabine4.setDirection("nord");
        cabine5.setDirection("nord");
        cabine2.setDirection("nord");
        space1.insertComponent(cabine1);
        space2.insertComponent(cabine2);
        space4.insertComponent(cabine3);
        space5.insertComponent(cabine4);
        space6.insertComponent(cabine5);
        startingCabine.setDirection("est");

        shipBoard.checkShipBoardParts(1, 3);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(1, space5.getCheck());
        assertEquals(1, space6.getCheck());
    }

    @Test
    void resetCheck() {
        ShipBoard shipBoard = new ShipBoard(FlightType.FIRST_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 0);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 0);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space7 = shipBoard.getSpace(4, 0);
        ShipBoardSpace space8 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space9 = shipBoard.getSpace(4, 2);

        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cannon cannon3 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Engine engine4 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine5 = new Cabine(CABINE, "00", 0,0,0,0);
        Shield shield6 = new Shield(SHIELD, "00", 3,3,3,3);
        Engine engine7 = new Engine(ENGINE, "00", 3,3,5,3,1);
        Cargo cargo8 = new Cargo(CARGO, "00", 3,3,3,3,3, "blue");
        Cargo cargo9 = new Cargo(CARGO, "00", 3,3,3,3,1, "red");

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cannon3);
        space4.insertComponent(engine4);
        space5.insertComponent(cabine5);
        space6.insertComponent(shield6);
        space7.insertComponent(engine7);
        space8.insertComponent(cargo8);
        space9.insertComponent(cargo9);

        ShipBoardSpace space10 = shipBoard.getSpace(2, 3);

        shipBoard.checkShipBoardParts(2, 0);
        assertEquals(1, space1.getCheck());
        assertEquals(1, space2.getCheck());
        assertEquals(1, space3.getCheck());
        assertEquals(1, space4.getCheck());
        assertEquals(0, space5.getCheck());
        assertEquals(1, space6.getCheck());
        assertEquals(1, space7.getCheck());
        assertEquals(1, space8.getCheck());
        assertEquals(1, space9.getCheck());
        assertEquals(1, space10.getCheck());

        shipBoard.resetCheck();
        assertEquals(0, space1.getCheck());
        assertEquals(0, space2.getCheck());
        assertEquals(0, space3.getCheck());
        assertEquals(0, space4.getCheck());
        assertEquals(0, space5.getCheck());
        assertEquals(0, space6.getCheck());
        assertEquals(0, space7.getCheck());
        assertEquals(0, space8.getCheck());
        assertEquals(0, space9.getCheck());
        assertEquals(0, space10.getCheck());
    }


    /**
     * Ship with disconnetted parts
     */
    @Test
    void validateShipBoard0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 0);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(0, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(0, 6);

        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);

        Engine engine = new Engine(ENGINE, "00", 3,3,5,3,2);
        Battery battery=new Battery(BATTERY, "00", 3,2,5,1,1);


        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(engine);
        space4.insertComponent(battery);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

        assertSame(1, exception.getErrorMessages().size());
        assertEquals(2, shipBoard.getNumLostTiles());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
    }

    @Test
    void validateShipBoard1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 0);
        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        space1.insertComponent(cannon1);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

        assertSame(2, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The space (6,4) is unusable. You have to remove the tile."));
    }

    @Test
    void validateShipBoard2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

        assertSame(1, exception.getErrorMessages().size());
        assertFalse(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The space (5,7) is unusable. You have to remove the tile."));
    }

    /**
     * Engine with wrong direction
     */
    @Test
    void validateShipBoard3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        Engine engine1 = new Engine(ENGINE, "00", 3,3,5,3,2);
        engine1.setDirection("est");


        space1.insertComponent(engine1);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });


        assertSame(1, exception.getErrorMessages().size());
        assertFalse(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) is in the wrong direction."));
    }

    /**
     *  Engine with wrong direction and a component behind
     */
    @Test
    void validateShipBoard4() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        Engine engine1 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        engine1.setDirection("est");
        space1.insertComponent(engine1);
        space2.insertComponent(cabine2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });


        assertSame(3, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) is in the wrong direction."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) has a component behind it."));
    }

    /**
     * Cannon with a component in front
     */
    @Test
    void validateShipBoard5() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 3);
        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        cannon1.setDirection("sud");
        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

        assertSame(1, exception.getErrorMessages().size());
        assertFalse(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The cannon at (7,6) has a component in front of it."));
    }

    /**
     * Wrong connectors
     */
    @Test
    void validateShipBoard6() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space2 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 4);

        Shield shield1 = new Shield(SHIELD, "00", 1, 2, 1, 2);
        Cargo cargo2 = new Cargo(CARGO, "00", 3, 0, 3, 0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 3, 3, 1, 1);
        cargo2.setDirection("est");
        structuralModule3.setDirection("ovest");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });


        assertSame(1, exception.getErrorMessages().size());
        assertFalse(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The tile at (7,7) has mismatched connectors with the tile below it."));
    }

    @Test
    void validateShipBoard7() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space3 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space4 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space5 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space6 = shipBoard.getSpace(2, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(2, 4);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space9 = shipBoard.getSpace(3, 1);
        ShipBoardSpace space10 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space11 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space12 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space13 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space14 = shipBoard.getSpace(4, 1);
        ShipBoardSpace space15 = shipBoard.getSpace(4, 2);
        ShipBoardSpace space16 = shipBoard.getSpace(4, 4);

        Cannon cannon1 = new Cannon(CANNON, "00", 5, 2, 0, 1, 2);
        Cabine cabine2 = new Cabine(CABINE, "00", 2, 1, 2, 1);
        cabine2.setDirection("ovest");
        Cannon cannon3 = new Cannon(CANNON, "00", 5, 0, 1, 0, 2);
        Engine engine4 = new Engine(ENGINE, "00", 2, 0, 5, 0, 2);
        engine4.setDirection("est");
        Cabine cabine5 = new Cabine(CABINE, "00", 0, 3, 0, 1);
        cabine5.setDirection("sud");
        Shield shield7 = new Shield(SHIELD, "00", 1,2,1,2);
        Engine engine8 = new Engine(ENGINE, "00", 0, 0, 5, 3, 1);
        Cargo cargo9 = new Cargo(CARGO, "00", 0,1,0,0, 3, "blue");
        cargo9.setDirection("est");
        Cannon cannon10 = new Cannon(CANNON, "00", 5, 1, 0, 0, 1);
        Cargo cargo11 = new Cargo(CARGO, "00", 3,0,3,0, 1, "red");
        cargo11.setDirection("ovest");
        StructuralModule structuralModule12 = new StructuralModule(STRUCTURAL_MODULE, "00", 3, 3, 1, 2);
        structuralModule12.setDirection("ovest");
        Cannon cannon13 = new Cannon(CANNON, "00", 5, 0, 1, 0, 1);
        cannon13.setDirection("est");
        Engine engine14 = new Engine(ENGINE, "00", 1, 1, 5, 0, 1);
        Cannon cannon15 = new Cannon(CANNON, "00", 5, 3, 0, 0, 1);
        cannon15.setDirection("sud");
        Engine engine16 = new Engine(ENGINE, "00", 1, 0, 5, 0, 2);

        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cannon3);
        space4.insertComponent(engine4);
        space5.insertComponent(cabine5);
        space7.insertComponent(shield7);
        space8.insertComponent(engine8);
        space9.insertComponent(cargo9);
        space10.insertComponent(cannon10);
        space11.insertComponent(cargo11);
        space12.insertComponent(structuralModule12);
        space13.insertComponent(cannon13);
        space14.insertComponent(engine14);
        space15.insertComponent(cannon15);
        space16.insertComponent(engine16);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });


        assertSame(8, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The tile at (6,7) has mismatched connectors with the tile to its right."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,5) is in the wrong direction."));
        assertTrue(exception.getErrorMessages().contains("The tile at (7,7) has mismatched connectors with the tile below it."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,9) has a component behind it."));
        assertTrue(exception.getErrorMessages().contains("The cannon at (8,6) has a component in front of it."));
        assertTrue(exception.getErrorMessages().contains("The tile at (8,8) has mismatched connectors with the tile below it."));
        assertTrue(exception.getErrorMessages().contains("The tile at (9,8) has mismatched connectors with the tile above it."));
    }

    @Test
    void validateShipBoard8() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"blue");
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Battery battery8 = new Battery(TileName.BATTERY, "08.png", 2, 2,1, 0, 3);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, battery8);

        assertDoesNotThrow(() -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard9() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 2);
        Engine engine1 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        engine1.setDirection("sud");
        space1.insertComponent(engine1);
        space2.insertComponent(cabine2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });


        assertSame(3, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) is in the wrong direction."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) has a component behind it."));
    }


    @Test
    void validateShipBoard10() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 3);
        Engine engine1 = new Engine(ENGINE, "00", 3,3,5,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        engine1.setDirection("ovest");
        space1.insertComponent(engine1);
        space2.insertComponent(cabine2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

        assertSame(3, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) is in the wrong direction."));
        assertTrue(exception.getErrorMessages().contains("The engine at (7,6) has a component behind it."));
    }

    @Test
    void validateShipBoard11() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 3);
        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        cannon1.setDirection("est");
        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });


        assertSame(2, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The cannon at (7,6) has a component in front of it."));
    }

    @Test
    void validateShipBoard12() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 1);
        ShipBoardSpace space3 = shipBoard.getSpace(3, 3);
        Cannon cannon1 = new Cannon(CANNON, "00", 5,3,3,3,2);
        Cabine cabine2 = new Cabine(CABINE, "00", 3,3,3,3);
        Cabine cabine3 = new Cabine(CABINE, "00", 3,3,3,3);
        cannon1.setDirection("ovest");
        space1.insertComponent(cannon1);
        space2.insertComponent(cabine2);
        space3.insertComponent(cabine3);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

        assertSame(2, exception.getErrorMessages().size());
        assertTrue(exception.getErrorMessages().contains("Your ship board has a part not attached to the rest."));
        assertTrue(exception.getErrorMessages().contains("The cannon at (7,6) has a component in front of it."));
    }

    @Test
    void validateShipBoard13() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(0, 2);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 2);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 2);
        ShipBoardSpace space4 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space5 = shipBoard.getSpace(1,4 );
        ShipBoardSpace space6 = shipBoard.getSpace(2,4 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,5 );
        ShipBoardSpace space8 = shipBoard.getSpace(1,5 );
        ShipBoardSpace space9 = shipBoard.getSpace(0,4 );
        ShipBoardSpace space10 = shipBoard.getSpace(1,5);
        ShipBoardSpace space11 = shipBoard.getSpace(1,6);
        ShipBoardSpace space12 = shipBoard.getSpace(2,6);



        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 1, 0, 0, 0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 2, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0, 2, 1, 1);
        Shield shield3 = new Shield(SHIELD, "00", 0, 0, 1, 1);
        Shield shield4 = new Shield(SHIELD, "00", 0, 1, 2, 0);
        Battery battery1=new Battery(BATTERY, "00",0,2,1,0,2);
        Battery battery2=new Battery(BATTERY, "00",1,1,1,1,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",1,1,1,1,1);
        Cannon cannon3=new Cannon(CANNON,"00",2,2,2,2, 2);
        Engine engine1=new Engine(ENGINE, "00", 1,1,1,1,1);
        Engine engine2=new Engine(ENGINE, "00", 1,1,1,1,1);

        cargo2.setDirection("sud");
        structuralModule3.setDirection("ovest");
        shield3.setDirection("est");
        shield4.setDirection("est");
        battery1.setDirection("ovest");
        battery2.setDirection("sud");
        cannon3.setDirection("sud");
        engine1.setDirection("ovest");
        engine2.setDirection("ovest");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);
        space11.insertComponent(cannon3);
        space12.insertComponent(engine1);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });

    }

    @Test
    void validateShipBoard15() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("est");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard16() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("ovest");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("sud");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard17() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("nord");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("nord");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard18() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("est");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("nord");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard19() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("est");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("est");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard20() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("sud");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("sud");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard21() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("sud");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("ovest");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard22() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Cargo cargo2 = new Cargo(CARGO, "00", 2, 2, 2, 2, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Shield shield3 = new Shield(SHIELD, "00", 2, 2, 2, 2);

        shield1.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Battery battery1=new Battery(BATTERY, "00",2,2,2,2,2);
        Battery battery2=new Battery(BATTERY, "00",2,2,2,2,2);
        Cannon cannon1=new Cannon(CANNON,"00",2,2,2,2, 2);
        Cannon cannon2=new Cannon(CANNON,"00",2,2,2,2,1);

        shield4.setDirection("ovest");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard23() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0, 0, 0, 0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 0, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0, 0, 0, 0);
        Shield shield3 = new Shield(SHIELD, "00", 0, 0, 0, 0);


        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("est");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard24() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0,0,0,0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 2, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0,0,0,0);
        Shield shield3 = new Shield(SHIELD, "00", 0,0,0,0);

        shield1.setDirection("ovest");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("sud");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard25() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0, 0, 0, 0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 0, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0, 0, 0, 0);
        Shield shield3 = new Shield(SHIELD, "00", 0, 0, 0, 0);

        shield1.setDirection("est");


        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("nord");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard26() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0,0,0,0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 2, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0,0,0,0);
        Shield shield3 = new Shield(SHIELD, "00", 0,0,0,0);

        shield1.setDirection("nord");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("nord");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard27() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0, 0, 0, 0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 0, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0, 0, 0, 0);
        Shield shield3 = new Shield(SHIELD, "00", 0, 0, 0, 0);

        shield1.setDirection("sud");


        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("sud");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard28() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0,0,0,0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 2, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0,0,0,0);
        Shield shield3 = new Shield(SHIELD, "00", 0,0,0,0);

        shield1.setDirection("est");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("est");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard29() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0, 0, 0, 0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 0, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0, 0, 0, 0);
        Shield shield3 = new Shield(SHIELD, "00", 0, 0, 0, 0);

        shield1.setDirection("ovest");


        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("ovest");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard30() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Cargo cargo2 = new Cargo(CARGO, "00", 0,0,0,0, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 2, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 0,0,0,0);
        Shield shield3 = new Shield(SHIELD, "00", 0,0,0,0);

        shield1.setDirection("sud");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Battery battery1=new Battery(BATTERY, "00",0,0,0,0,2);
        Battery battery2=new Battery(BATTERY, "00",0,0,0,0,2);
        Cannon cannon1=new Cannon(CANNON,"00",0,0,0,0, 2);
        Cannon cannon2=new Cannon(CANNON,"00",0,0,0,0,1);

        shield4.setDirection("ovest");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard31() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Cargo cargo2 = new Cargo(CARGO, "00", 1, 1, 1, 1, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 1, 1, 1, 1);
        Shield shield2 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Shield shield3 = new Shield(SHIELD, "00", 1, 1, 1, 1);

        Shield shield4 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Battery battery1=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Battery battery2=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Cannon cannon1=new Cannon(CANNON,"00",1, 1, 1, 1, 2);
        Cannon cannon2=new Cannon(CANNON,"00",1, 1, 1, 1,1);

        shield4.setDirection("est");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard32() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Cargo cargo2 = new Cargo(CARGO, "00", 1, 1, 1, 1, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 1, 1, 1, 1);
        Shield shield2 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Shield shield3 = new Shield(SHIELD, "00", 1, 1, 1, 1);

        shield1.setDirection("ovest");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Battery battery1=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Battery battery2=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Cannon cannon1=new Cannon(CANNON,"00",1, 1, 1, 1, 2);
        Cannon cannon2=new Cannon(CANNON,"00",1, 1, 1, 1,1);

        shield4.setDirection("sud");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard33() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Shield shield1 = new Shield(SHIELD, "00", 0, 0, 0, 0);
        Cargo cargo2 = new Cargo(CARGO, "00", 3, 3, 3, 3, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 3, 3, 3, 3);
        Shield shield2 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Shield shield3 = new Shield(SHIELD, "00", 3, 3, 3, 3);

        shield1.setDirection("ovest");


        Shield shield4 = new Shield(SHIELD, "00", 0,0,0,0);
        Battery battery1=new Battery(BATTERY, "00",3, 3, 3, 3,2);
        Battery battery2=new Battery(BATTERY, "00",3, 3, 3, 3,2);
        Cannon cannon1=new Cannon(CANNON,"00",3, 3, 3, 3, 2);
        Cannon cannon2=new Cannon(CANNON,"00",3, 3, 3, 3,1);

        shield4.setDirection("ovest");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard34() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 0,0,0,0);
        Cargo cargo2 = new Cargo(CARGO, "00", 3, 3, 3, 3, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 0, 2, 0, 0);
        Shield shield2 = new Shield(SHIELD, "00", 3, 3, 3, 3);
        Shield shield3 = new Shield(SHIELD, "00", 3, 3, 3, 3);

        shield1.setDirection("sud");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 0,0,0,0);
        Battery battery1=new Battery(BATTERY, "00",3, 3, 3, 3,2);
        Battery battery2=new Battery(BATTERY, "00",3, 3, 3, 3,2);
        Cannon cannon1=new Cannon(CANNON,"00",3, 3, 3, 3, 2);
        Cannon cannon2=new Cannon(CANNON,"00",3, 3, 3, 3,1);

        shield4.setDirection("ovest");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void validateShipBoard35() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 2,2,2,2);
        Cargo cargo2 = new Cargo(CARGO, "00", 1, 1, 1, 1, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Shield shield3 = new Shield(SHIELD, "00", 1, 1, 1, 1);

        shield1.setDirection("est");

        Shield shield4 = new Shield(SHIELD, "00", 2,2,2,2);
        Battery battery1=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Battery battery2=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Cannon cannon1=new Cannon(CANNON,"00",1, 1, 1, 1, 2);
        Cannon cannon2=new Cannon(CANNON,"00",1, 1, 1, 1,1);

        shield4.setDirection("nord");
        battery1.setDirection("est");
        battery2.setDirection("est");
        cannon1.setDirection("est");
        cannon2.setDirection("est");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }


    @Test
    void validateShipBoard36() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);


        Shield shield1 = new Shield(SHIELD, "00", 2, 2, 2, 2);
        Cargo cargo2 = new Cargo(CARGO, "00", 1, 1, 1, 1, 1, "red");
        StructuralModule structuralModule3 = new StructuralModule(STRUCTURAL_MODULE, "00", 2, 2, 2, 2);
        Shield shield2 = new Shield(SHIELD, "00", 1, 1, 1, 1);
        Shield shield3 = new Shield(SHIELD, "00", 1, 1, 1, 1);

        shield1.setDirection("nord");
        cargo2.setDirection("ovest");
        structuralModule3.setDirection("ovest");
        shield2.setDirection("ovest");
        shield3.setDirection("ovest");

        Shield shield4 = new Shield(SHIELD, "00", 2,2,2,2);
        Battery battery1=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Battery battery2=new Battery(BATTERY, "00",1, 1, 1, 1,2);
        Cannon cannon1=new Cannon(CANNON,"00",1, 1, 1, 1, 2);
        Cannon cannon2=new Cannon(CANNON,"00",1, 1, 1, 1,1);

        shield4.setDirection("nord");
        battery1.setDirection("sud");
        battery2.setDirection("sud");
        cannon1.setDirection("sud");
        cannon2.setDirection("sud");

        space1.insertComponent(shield1);
        space2.insertComponent(cargo2);
        space3.insertComponent(structuralModule3);
        space4.insertComponent(shield2);
        space5.insertComponent(shield3);
        space6.insertComponent(shield4);
        space7.insertComponent(battery1);
        space8.insertComponent(battery2);
        space9.insertComponent(cannon1);
        space10.insertComponent(cannon2);

        MultipleValidationErrorsException exception = assertThrows(MultipleValidationErrorsException.class, () -> {
            shipBoard.validateShipBoard();
        });
    }

    @Test
    void rarestGoodsBlock0() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"red");
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Battery battery8 = new Battery(TileName.BATTERY, "08.png", 2, 2,1, 0, 3);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, battery8);
        cargo1.putCargoIn(Color.RED);

        assertEquals(Color.RED, shipBoard.rarestGoodsBlock());
    }

    @Test
    void rarestGoodsBlock1() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"blue");
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Battery battery8 = new Battery(TileName.BATTERY, "08.png", 2, 2,1, 0, 3);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, battery8);

        assertEquals(null, shipBoard.rarestGoodsBlock());
    }

    @Test
    void rarestGoodsBlock2() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"blue");
        cargo1.putCargoIn(BLUE);
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Cargo cargo8 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"blue");
        cargo8.putCargoIn(Color.GREEN);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, cargo8);

        assertEquals(Color.GREEN, shipBoard.rarestGoodsBlock());
    }

    @Test
    void rarestGoodsBlock3() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"red");
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Battery battery8 = new Battery(TileName.BATTERY, "08.png", 2, 2,1, 0, 3);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, battery8);
        cargo1.putCargoIn(Color.YELLOW);

        assertEquals(Color.YELLOW, shipBoard.rarestGoodsBlock());
    }

    @Test
    void rarestGoodsBlock4() {
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(1, 3);
        ShipBoardSpace space2 = shipBoard.getSpace(1, 4);
        ShipBoardSpace space3 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space5 = shipBoard.getSpace(3, 4);
        ShipBoardSpace space6 = shipBoard.getSpace(3, 3);
        ShipBoardSpace space7 = shipBoard.getSpace(3, 2);
        ShipBoardSpace space8 = shipBoard.getSpace(2, 2);
        Cargo cargo1 = new Cargo(TileName.CARGO, "01.png", 3, 1,2, 1, 2,"red");
        Cannon cannon2 = new Cannon(CANNON, "02.png", 5, 0,2, 3, 1);
        Cannon cannon3 = new Cannon(CANNON, "03.png", 5, 1,2, 1, 2);
        Cabine cabine4 = new Cabine(TileName.CABINE, "04.png", 3, 0,0, 2);
        Cabine cabine5 = new Cabine(TileName.CABINE, "05.png", 0, 3,2, 2);
        Engine engine6 = new Engine(TileName.ENGINE, "06.png", 2, 2,5, 2, 2);
        Engine engine7 = new Engine(TileName.ENGINE, "07.png", 3, 2,5, 0, 1);
        Battery battery8 = new Battery(TileName.BATTERY, "08.png", 2, 2,1, 0, 3);
        shipBoard.putObjectIn(space1, cargo1);
        shipBoard.putObjectIn(space2, cannon2);
        shipBoard.putObjectIn(space3, cannon3);
        shipBoard.putObjectIn(space4, cabine4);
        shipBoard.putObjectIn(space5, cabine5);
        shipBoard.putObjectIn(space6, engine6);
        shipBoard.putObjectIn(space7, engine7);
        shipBoard.putObjectIn(space8, battery8);
        cargo1.putCargoIn(Color.BLUE);

        assertEquals(Color.BLUE, shipBoard.rarestGoodsBlock());
    }


    @Test
    void getHasPurpleAlien(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        assertFalse(shipBoard.getHasPurpleAlien());
    }

    @Test
    void getHasBrownAlien(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        assertFalse(shipBoard.getHasBrownAlien());
    }

    @Test
    void setHasPurpleAlien(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        shipBoard.setHasPurpleAlien(true);
        assertTrue(shipBoard.getHasPurpleAlien());

        shipBoard.setHasPurpleAlien(false);
        assertFalse(shipBoard.getHasPurpleAlien());
    }

    @Test
    void setHasBrownAlien(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        shipBoard.setHasBrownAlien(true);
        assertTrue(shipBoard.getHasBrownAlien());

        shipBoard.setHasBrownAlien(false);
        assertFalse(shipBoard.getHasBrownAlien());
    }

    @Test
    void increaseNumOfLostTiles(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        shipBoard.incrementNumLostTiles();
        shipBoard.incrementNumLostTiles();
        assertEquals(2, shipBoard.getNumLostTiles());
    }

    @Test
    void getNumFigures(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        StartingCabine startingCabine =(StartingCabine) shipBoard.getSpace(2,3).getComponent();
        startingCabine.setNumFigures(2);

        Cabine cabine=new Cabine(CABINE, "00", 1,1,1,1);
        shipBoard.getSpace(1,3).insertComponent(cabine);
        cabine.setNumFigures(1);
        assertEquals(3, shipBoard.getNumFigures());
    }

    @Test
    void getCorrectShip(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        assertFalse(shipBoard.getCorrectShip());
    }

    @Test
    void getGoodsBlockOnShipBoard(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        Cargo cargo=new Cargo(CARGO, "00", 1,1,1,1,2, "blu");

        shipBoard.getSpace(1,3).insertComponent(cargo);
        cargo.putCargoIn(GREEN);
        cargo.putCargoIn(BLUE);

        ArrayList<Color> temp = shipBoard.getGoodsBlockOnShipBoard();

        assertEquals(2, temp.size());
        assertTrue(temp.contains(GREEN));
        assertTrue(temp.contains(BLUE));
    }

    @Test
    void getNumBatteries(){
        ShipBoard shipBoard=new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        Battery battery=new Battery(BATTERY, "00", 1,1,1,1,2);
        shipBoard.getSpace(1,3).insertComponent(battery);

        assertEquals(2, shipBoard.getNumBatteries());
    }

    @Test
    void defineAlienCabine0(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");



        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("est");
        alienCabine6.setDirection("est");
        alienCabine7.setDirection("est");
        alienCabine8.setDirection("est");
        cabine2.setDirection("est");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine1(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("sud");
        alienCabine1.setDirection("sud");
        alienCabine2.setDirection("sud");
        alienCabine3.setDirection("sud");
        alienCabine4.setDirection("sud");


        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("ovest");
        alienCabine6.setDirection("ovest");
        alienCabine7.setDirection("ovest");
        alienCabine8.setDirection("ovest");
        cabine2.setDirection("ovest");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine2(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("nord");
        alienCabine1.setDirection("sud");
        alienCabine2.setDirection("sud");
        alienCabine3.setDirection("sud");
        alienCabine4.setDirection("sud");


        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("ovest");
        alienCabine6.setDirection("ovest");
        alienCabine7.setDirection("ovest");
        alienCabine8.setDirection("ovest");
        cabine2.setDirection("nord");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine3(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("est");

        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("est");
        alienCabine6.setDirection("est");
        alienCabine7.setDirection("est");
        alienCabine8.setDirection("est");
        cabine2.setDirection("nord");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine4(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("est");
        alienCabine1.setDirection("sud");
        alienCabine2.setDirection("sud");
        alienCabine3.setDirection("sud");
        alienCabine4.setDirection("sud");


        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("ovest");
        alienCabine6.setDirection("ovest");
        alienCabine7.setDirection("ovest");
        alienCabine8.setDirection("ovest");
        cabine2.setDirection("est");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine5(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("sud");

        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("est");
        alienCabine6.setDirection("est");
        alienCabine7.setDirection("est");
        alienCabine8.setDirection("est");
        cabine2.setDirection("sud");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine6(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("ovest");
        alienCabine1.setDirection("sud");
        alienCabine2.setDirection("sud");
        alienCabine3.setDirection("sud");
        alienCabine4.setDirection("sud");


        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("ovest");
        alienCabine6.setDirection("ovest");
        alienCabine7.setDirection("ovest");
        alienCabine8.setDirection("ovest");
        cabine2.setDirection("sud");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine7(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        ShipBoardSpace space3 = shipBoard.getSpace(4, 5);
        ShipBoardSpace space4 = shipBoard.getSpace(3, 6);
        ShipBoardSpace space5 = shipBoard.getSpace(3,4 );

        ShipBoardSpace space6 = shipBoard.getSpace(3,1 );
        ShipBoardSpace space7 = shipBoard.getSpace(2,1 );
        ShipBoardSpace space8 = shipBoard.getSpace(4,1 );
        ShipBoardSpace space9 = shipBoard.getSpace(3,0 );
        ShipBoardSpace space10 = shipBoard.getSpace(3,2);

        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine2=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine3=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine4=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        cabine1.setDirection("ovest");

        Cabine cabine2 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine5=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine6=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine7=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        AlienCabine alienCabine8=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");

        alienCabine5.setDirection("est");
        alienCabine6.setDirection("est");
        alienCabine7.setDirection("est");
        alienCabine8.setDirection("est");
        cabine2.setDirection("ovest");

        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine2);
        space3.insertComponent(alienCabine3);
        space4.insertComponent(alienCabine1);
        space5.insertComponent(alienCabine4);
        space6.insertComponent(cabine2);
        space7.insertComponent(alienCabine5);
        space8.insertComponent(alienCabine6);
        space9.insertComponent(alienCabine7);
        space10.insertComponent(alienCabine8);

        shipBoard.defineAlienCabine();

        assertTrue(cabine1.isConnectedWithAlienCabine());
        assertTrue(cabine2.isConnectedWithAlienCabine());
    }

    @Test
    void defineAlienCabine8(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        cabine1.setHasPurpleAlien(true);
        shipBoard.setHasPurpleAlien(true);
        space1.insertComponent(cabine1);

        shipBoard.defineAlienCabine();
        assertFalse(shipBoard.getHasPurpleAlien());
        assertFalse(cabine1.getHasPurpleAlien());
    }

    @Test
    void defineAlienCabine9(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        cabine1.setHasBrownAlien(true);
        shipBoard.setHasBrownAlien(true);
        space1.insertComponent(cabine1);

        shipBoard.defineAlienCabine();
        assertFalse(shipBoard.getHasBrownAlien());
        assertFalse(cabine1.getHasBrownAlien());
    }

    @Test
    void defineAlienCabine10(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"yellow");
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        cabine1.setHasPurpleAlien(true);
        shipBoard.setHasPurpleAlien(true);
        cabine1.setAlienCabineConnected(alienCabine1);
        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine1);

        shipBoard.defineAlienCabine();
        assertFalse(shipBoard.getHasPurpleAlien());
        assertFalse(cabine1.getHasPurpleAlien());
    }

    @Test
    void defineAlienCabine11(){
        ShipBoard shipBoard = new ShipBoard(FlightType.STANDARD_FLIGHT, RED);
        Cabine cabine1 = new Cabine(CABINE, "00", 1,1,1,1);
        AlienCabine alienCabine1=new AlienCabine(ALIEN_CABINE, "00",1,1,1,1,"purple");
        ShipBoardSpace space1 = shipBoard.getSpace(3, 5);
        ShipBoardSpace space2 = shipBoard.getSpace(2, 5);
        cabine1.setHasBrownAlien(true);
        shipBoard.setHasBrownAlien(true);
        cabine1.setAlienCabineConnected(alienCabine1);
        space1.insertComponent(cabine1);
        space2.insertComponent(alienCabine1);

        shipBoard.defineAlienCabine();
        assertFalse(shipBoard.getHasBrownAlien());
        assertFalse(cabine1.getHasBrownAlien());
    }

}
