/*
* METEOR SWARM'S CLASS
* * This class extends the class Card
 *
 * Card description:
 * A Meteor Swarm can really mess with your
 * paint job. The card depicts several large
 * and/or small meteors and the directions
 * they come from. Deal with meteors one
 * at a time, top to bottom. They affect all
 * players simultaneously.
 * For each meteor, the leader rolls two dice. The roll
 * determines which row or column the meteor can
 * impact. The rows and columns are numbered on the
 * edge of your ship board. Each player receives their own
 * personal meteor and checks to see if it hits or misses
 * their ship.
* */

package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.card.cardsType.ForReadJson.Meteor;
import model.player.Player;
import network.messages.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class represents the meteor swarm card. It extends Card.
 */
public class MeteorSwarm extends Card {
    private ArrayList<Meteor> meteor;
    private int counter;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param meteor is the list of the meteor shots.
     */
    public MeteorSwarm(CardName cardType, int level, String url, ArrayList<Meteor> meteor) {
        super(cardType, level, url);

        this.meteor = meteor;
        this.counter = 0;
    }

    /**
     * @return the list of the meteors
     */
    public ArrayList<Meteor> getMeteor() {
        return meteor;
    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Setter method that increments the counter
     */
    public void incrementCounter() {
        counter++;
    }

    @Override
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {
        // Sets the leader as the player in turn
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

        for(Player player : gameModel.getPlayers()) {
            if(!gameModel.getRetiredPlayers().contains(player)) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    sender.accept(new SetCardInUseMessage(player.getId(),this));
                    sender.accept(new AskRollDiceMessage(player.getId(), this, true, false));

                }
                else {
                    sender.accept(new SetCardInUseMessage(player.getId(),this));
                    sender.accept(new AskRollDiceMessage(player.getId(), this, false, false));
                }
            }
            else {
                sender.accept(new SetCardInUseMessage(player.getId(),this));
                sender.accept(new DrawnCardMessage2(player.getId(), this));
            }

        }

        gameModel.setGameState(GameState.ROLL_DICE);
    }
}
