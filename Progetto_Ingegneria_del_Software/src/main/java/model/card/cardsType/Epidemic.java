/*
* EPIDEMIC'S CLASS
 * This class extends the class Card
 *
 * Card description:
 * Epidemic makes you remove
 * 1 crew member (human or alien)
 * from every occupied cabin that is
 * joined to another occupied cabin.
 * The safe thing to do is to build
 * your ship so that no two cabins are
 * joined. If you do have joined
 * cabins, maybe you can find an
 * adventure that will allow you to
 * empty one of them before the epidemic strikes.
 * Sabotage destroys a random
* */

package model.card.cardsType;

import enumerations.CardName;
import enumerations.GameState;
import model.GameModel;
import model.card.Card;
import model.player.Player;
import network.messages.*;

import java.util.function.Consumer;

/**
 * This class represents the epidemic card. It extends Card.
 */
public class Epidemic extends Card {
    public Epidemic(CardName cardType, int level, String url) {
        super(cardType, level, url);
    }

    @Override
    public void onPickUp(GameModel gameModel, Consumer<Message> sender) {
        // The players have no choice for this card. The controller automatically updates the model.
        sender.accept(new DrawnCardMessage(gameModel.getPlayerInTurn().getNickname(), this, true));
        gameModel.setGameState(GameState.EPIDEMIC);

        for (Player player : gameModel.getPlayersPosition()) {
            player.getShipBoard().applyEpidemic();
        }

        for (Player player : gameModel.getPlayersPosition()) {
            player.getShipBoard().removeEpidemicFigures();
        }

        for(Player player : gameModel.getPlayers()) {
            sender.accept(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
        }

        gameModel.refreshPlayersPosition();
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

        for(Player player : gameModel.getPlayers()) {
            if(!gameModel.getRetiredPlayers().contains(player)) {
                sender.accept(new ChangeTuiStateMessage(player.getId(), 1));
                sender.accept(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
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
