/*
* COMBAT ZONE'S CLASSE
* This class extends the class Card
*
* Card description:
* The true test of any spaceship is to fly
* it through a combat zone. The Combat
* Zone card has 3 lines which are evaluated
* in succession. Each line gives a criterion
 * and a penalty for the player who is weakest in that area.
* */
package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import network.messages.*;

import java.util.*;
import java.util.function.Consumer;


/**
 * This class represents the combat zone card. It extends Card.
 */
public class CombatZone extends Card {
    private Object[] faseOne;
    private Object[] faseTwo;
    private Object[] faseThree;
    private int counter;
    private int faseCounter;

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     * @param faseOne represents the phase one of the card.
     * @param faseTwo represents the phase two of the card.
     * @param faseThree represents the phase three of the card.
     */
    public CombatZone(CardName cardType, int level, String url, Object[] faseOne, Object[] faseTwo, Object[] faseThree) {
        super(cardType, level, url);
        this.faseOne = faseOne;
        this.faseTwo = faseTwo;
        this.faseThree = faseThree;
        this.counter = 0;
        this.faseCounter = 1;
    }

    /**
     * @return the counter for the meteor shots of phase three
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @return the phase counter
     */
    public int getFaseCounter() {
        return faseCounter;
    }

    /**
     * Setter method that increments the shots counter
     */
    public void incrementCounter() {
        counter++;
    }

    /**
     * Setter method that increments the phase counter
     */
    public void incrementFaseCounter() {
        faseCounter++;
    }

    /**
     * @return phase one
     */
    public Object[] getFaseOne() {
        return faseOne;
    }

    /**
     * @return phase two
     */
    public Object[] getFaseTwo() {
        return faseTwo;
    }

    /**
     * @return phase three
     */
    public Object[] getFaseThree() {
        return faseThree;
    }

    @Override
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {
        // If the combat zone card is level 1, for the first phase the players have no choice
        gameModel.setGameState(GameState.FASE_1);

        for(Player player : gameModel.getPlayers()) {
            if(!gameModel.getRetiredPlayers().contains(player)) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    sender.accept(new SetCardInUseMessage(player.getId(),this));
                    sender.accept(new DrawnCardMessage(player.getId(), this, true)); // true if the player is in turn
                }
                else {
                    sender.accept(new SetCardInUseMessage(player.getId(),this));
                    sender.accept(new DrawnCardMessage(player.getId(), this, false)); // false if the player is not in turn
                }
            }
            else {
                sender.accept(new SetCardInUseMessage(player.getId(),this));
                sender.accept(new DrawnCardMessage2(player.getId(), this));
            }

        }

        if (this.getLevel() == 1) {
            Optional<Player> minPlayerEquipment = gameModel.getPlayersPosition().stream().reduce((p1,p2) -> {
                int count1 = p1.getShipBoard().getNumFigures()
                        + (p1.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                        + (p1.getShipBoard().getHasBrownAlien() ? 1 : 0);

                int count2 = p2.getShipBoard().getNumFigures()
                        + (p2.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                        + (p2.getShipBoard().getHasBrownAlien() ? 1 : 0);

                return count1 <= count2 ? p1 : p2;
            });
            Player player = minPlayerEquipment.orElse(null);
            int lostFlightDays = (int) faseOne[2];
            gameModel.getFlightBoard().movePlayerBackward(player, lostFlightDays);
            gameModel.refreshPlayersPosition();
            gameModel.setGameState(GameState.FASE_2);

            for(Player otherPlayer : gameModel.getPlayers()) {
                if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                    sender.accept(new ProceedNextPhaseMessage(otherPlayer.getId(), this, gameModel.getCardsToPlay()));
                }
                sender.accept(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
            }

            gameModel.refreshPlayersPosition();
        }

        // Sets the leader as the player in turn
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));
    }
}