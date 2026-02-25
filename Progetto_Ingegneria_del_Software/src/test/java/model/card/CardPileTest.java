package model.card;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CardPileTest {

    @Test
    void getCards(){
        ArrayList<Card> cards = new ArrayList<>();
        CardPile cardPile=new CardPile(1,cards);
        assertEquals(cards,cardPile.getCards());
    }

    @Test
    void tryObserved0() {
        ArrayList<Card> cardsDeck = new ArrayList<>();
        CardPile cardPile=new CardPile(1,cardsDeck);

        assertTrue(cardPile.tryObserved());

        cardPile.tryObserved();
        assertFalse(cardPile.tryObserved());
    }

    @Test
    void tryRelease0() {
        ArrayList<Card> cardsDeck = new ArrayList<>();
        CardPile cardPile=new CardPile(1,cardsDeck);
        cardPile.tryObserved();
        assertTrue(cardPile.tryRelease());
    }

    @Test
    void tryRelease1() {
        ArrayList<Card> cardsDeck = new ArrayList<>();
        CardPile cardPile=new CardPile(1,cardsDeck);
        assertFalse(cardPile.tryRelease());
    }
}