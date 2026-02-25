package model.card;/*
* CARD'S CLASS
* A card has a type and a level
* A card can be face up while in use or face down when is not observed by the players (1: in use, 0: not observed)
* */

import enumerations.CardName;
import model.GameModel;
import network.messages.Message;

import java.io.Serializable;
import java.util.function.Consumer;


public abstract class Card implements Serializable {
    private CardName cardType;
    private int level;
    private int side; //Interfaccia per cambiarlo
    private String url;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     */
    public Card(CardName cardType, int level, String url) {
        this.cardType = cardType;
        this.level = level;
        this.url = url;
        this.side = 0;

    }

    /**
     * @return card's type
     * */
    public CardName getCardType() {
        return cardType;
    }


    /**
     * @return card's level
     * */
    public int getLevel() {
        return level;
    }

    /**
     * @return card's url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return card's side
     * */
    public int getSide() {
        return side;
    }

    /**
     *
     * @param side sets the side of the card
     */
    public void setCardSide(int side) {
        this.side = side;
    }

    /**
     * Processes the message pick_up_card
     * @param gameModel is the gameModel to interact with
     * @param sender is the sender of the message
     */
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {};

}
