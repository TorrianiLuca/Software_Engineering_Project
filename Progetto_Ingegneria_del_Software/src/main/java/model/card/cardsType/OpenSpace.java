/*
* OPEN SPACE'S CLASS
* This class extends the class Card
 *
 * Card description:
 * Open Space is like a wide-open drag strip.
 * Each player will have a chance to gain
 * flight days.
 * In turn, each player declares their engine
 * strength, beginning with the leader, and continuing in
 * the order shown by the rockets on the flight board.
 * You must decide whether to spend battery tokens
 * on any double engines when it is your turn to declare
 * engine strength (see page 11). Then you immediately
 * move your rocket marker that many empty spaces
 * forward. This may allow you to pass players ahead of
 * you (occupied spaces are skipped) and perhaps even
 * take the lead.
* */

package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import network.messages.DrawnCardMessage;
import network.messages.DrawnCardMessage2;
import network.messages.Message;
import network.messages.SetCardInUseMessage;

import java.util.function.Consumer;

/**
 * This class represents the open space card. It extends Card.
 */
public class OpenSpace extends Card {

    /**
     * Constructor.
     * @param cardType is the name of the card.
     * @param level is the card level.
     * @param url is the url of the image associated to the card.
     */
    public OpenSpace(CardName cardType, int level, String url) {
        super(cardType, level, url);
    }

    @Override
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {
        // Sets the leader as the player in turn
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

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

        gameModel.setGameState(GameState.OPEN_SPACE);
    }
}
