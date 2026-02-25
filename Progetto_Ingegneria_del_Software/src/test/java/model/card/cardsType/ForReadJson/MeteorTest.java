package model.card.cardsType.ForReadJson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeteorTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getDirection() {
        Meteor meteor1 = new Meteor("nord", 1);
        Meteor meteor2 = new Meteor("est", 2);
        assertEquals("nord", meteor1.getDirection());
        assertEquals("est", meteor2.getDirection());
    }

    @Test
    void getPower() {
        Meteor meteor1 = new Meteor("nord", 1);
        Meteor meteor2 = new Meteor("est", 2);
        assertEquals(1, meteor1.getPower());
        assertEquals(2, meteor2.getPower());
    }
}