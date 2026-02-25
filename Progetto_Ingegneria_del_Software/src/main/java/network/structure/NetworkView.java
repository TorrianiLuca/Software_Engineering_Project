package network.structure;

import enumerations.Color;
import enumerations.FlightType;
import model.card.Card;
import model.card.CardPile;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.shipBoard.ShipBoard;
import network.messages.*;
import support.Quadruple;
import view.View;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class used to hide network details from the controller and manage communication
 * between the server and the connected clients by sending game model updates.
 */
public class NetworkView implements View {
    private final ServerMain server;

    /**
     * Constructor of NetworkView.
     * @param server is the server that handles the client connection.
     */
    public NetworkView(ServerMain server) {
        this.server = server;
    }

    /**
     * @return the server.
     */
    public ServerMain getServer(){
        return server;
    }

    @Override
    public void askNickname(String clientId) {
        server.sendMessageToClient(clientId, new AskNicknameMessage(clientId));
    }

    @Override
    public void askMaxPlayerAndFlightType() {}

    @Override
    public void sendMessageToClient(String clientId, Message message) {
        server.sendMessageToClient(clientId, message);
    }

    @Override
    public void showAvailableGames(HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> availableGames) {}

    @Override
    public void clearPage() {}

    @Override
    public void showGenericMessage(String message) {}

    @Override
    public void showShipBoard(ShipBoard shipBoard) {}

    @Override
    public void showTiles(GameTile gameTile) {}

    @Override
    public void showComponentTile(ComponentTile componentTile) {}

    @Override
    public void showAllShipBoards(HashMap<String, ShipBoard> shipBoards) {}

    @Override
    public void showTimer(int time) {}

    @Override
    public void showTempPositions(ArrayList<Player> tempPositions) {}

    @Override
    public void showShipErrors(ArrayList<String> errors) {}

    @Override
    public void showCardPile(CardPile carPile) {}

    @Override
    public void finishBuilding() {}

    @Override
    public void proceed2() {}

    @Override
    public void showLeader(int numCards, FlightBoard flightBoard) {}

    @Override
    public void showNotLeader(int numCards, FlightBoard flightBoard) {}

    @Override
    public void showNotLeader2(int numCards, FlightBoard flightBoard, ArrayList<Player> players) {}

    @Override
    public void showCard(boolean inTurn) {}

    @Override
    public void showCard2() {}

    @Override
    public void showWait(Card card) {}

    @Override
    public void showGoods(Card card, ArrayList<Color> tempGoodsBlock) {}

    @Override
    public void proceedToNextCard(Card card, int num, ArrayList<Card> cardsToPlay) {}

    @Override
    public void notProceed() {}

    @Override
    public void changeState(int num) {}

    @Override
    public void setCardInUse(Card card) {}

    @Override
    public void setRemoveBattery() {}

    @Override
    public void setRemoveGood() {}

    @Override
    public void setRemoveFigure() {}

    @Override
    public void setRollDice(Card card, boolean inTurn, boolean proceed) {}

    @Override
    public void showMeteorHit(Card card, boolean isHit, int sum) {}

    @Override
    public void updatePlayerParametres(FlightBoard flightBoard) {}

    @Override
    public void showWinner(ArrayList<Player> players, boolean winner) {}

    @Override
    public void disconnected(String disconnectionError) {}

    @Override
    public void showGenericError(String error) {}

    @Override
    public void addServerMessage(Message message) {}
}
