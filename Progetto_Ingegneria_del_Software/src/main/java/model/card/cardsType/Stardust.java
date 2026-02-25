/*
* STARDUST'S CLASS
* This class extends the class Card
 *
 * Card description:
 * Yellow cards are special events. In your
 * learning flight, the only special event is
 * Stardust. Every player loses 1 flight day for
 * every exposed connector. (Each exposed
 * connector only counts once, regardless
 * of whether it is one pipe, two pipes, or
 * universal.) In reverse order, starting with the last player,
 * each player counts exposed connectors and moves
 * back that many empty spaces.
* */

package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import network.messages.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * This class represents the stardust card. It extends Card.
 */
public class Stardust extends Card {

    public Stardust(CardName cardType, int level, String url) {
        super(cardType, level, url);
    }

    @Override
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {
        // The players have no choice for this card. The controller automatically updates the model.
        sender.accept(new DrawnCardMessage(gameModel.getPlayerInTurn().getNickname(), this, true));
        gameModel.setGameState(GameState.STARDUST);

        ArrayList<Player> reversePlayers = gameModel.getPlayersPosition();
        Collections.reverse(reversePlayers);
        for (Player player : reversePlayers) {
            gameModel.getFlightBoard().movePlayerBackward(player, player.getShipBoard().numExposedConnectors()); // move the player backwards on the flightboard given the number of exposed connector that he has
        }

        for(Player player : gameModel.getPlayers()) {
            sender.accept(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
        }

        gameModel.refreshPlayersPosition();
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

        for(Player player : gameModel.getPlayers()) {
            if(!gameModel.getRetiredPlayers().contains(player)) {
                sender.accept(new ProceedNextCardMessage(player.getId(), this, gameModel.getCardsToPlay()));
            }
            else {
                sender.accept(new SetCardInUseMessage(player.getId(),this));
                sender.accept(new DrawnCardMessage2(player.getId(), this));
            }

        }

        gameModel.setGameState(GameState.PLAYING);
    }
}
