package controller;

import enumerations.*;
import exceptions.MultipleValidationErrorsException;
import model.GameModel;
import model.card.Card;
import model.card.CardPile;
import model.card.cardsType.*;
import model.card.cardsType.ForReadJson.Meteor;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.shipBoard.ShipBoardSpace;
import model.tiles.ComponentTile;
import model.tiles.componentTile.Battery;
import model.tiles.componentTile.Cabine;
import model.tiles.componentTile.Cargo;
import model.tiles.componentTile.StartingCabine;
import network.messages.*;
import network.structure.NetworkView;
import network.structure.ServerMain;
import observer.Observer;
import support.Couple;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static enumerations.PlayerState.*;

/**
 * This class controls the game: it receives a message from the clients (network) and updates the model, then sends
 * the updated model to the clients.
 * For now it is an offline controller
 */
public class Controller implements Observer, GameEventListener {
    private final NetworkView networkView;
    private GameModel gameModel;
    private HashMap<String, Integer> playersCredits = new HashMap<>();
    private ArrayList<String> winners = new ArrayList<>();
    private HashMap<String, ShipBoard> playersShipBoards = new HashMap<>();
    private Random random = new Random();
    private boolean piratesFight = false;
    private boolean combatZoneFight = false;


    /**
     * Constructor for the controller.
     * @throws IOException if it can't reach the server
     */
    public Controller(ServerMain server) throws IOException {
        this.gameModel = new GameModel();
        gameModel.addObserver(this);
        this.networkView = new NetworkView(server);
        gameModel.setGameState(GameState.LOGIN);
    }


    /**
     * Handles a received message by delegating processing to the message itself.
     * @param receivedMessage is the message received from the client
     */
    public void onMessageReceived(Message receivedMessage) {
        if (receivedMessage.getClass().equals(FinishedBuildingMessage.class)) {
            FinishedBuildingMessage finishedBuildingMessage = (FinishedBuildingMessage) receivedMessage;
            finishedBuildingMessage.setListener(this);
        }
        if (receivedMessage.getClass().equals(PickUpCardMessage.class)) {
            PickUpCardMessage pickUpCardMessage = (PickUpCardMessage) receivedMessage;
            pickUpCardMessage.setListener(this);
        }
        if (receivedMessage.getClass().equals(RetireMessage.class)) {
            RetireMessage retireMessage = (RetireMessage) receivedMessage;
            retireMessage.setListener(this);
        }
        if (receivedMessage.getClass().equals(FinishedPopulateMessage.class)) {
            FinishedPopulateMessage finishedPopulateMessage = (FinishedPopulateMessage) receivedMessage;
            finishedPopulateMessage.setListener(this);
        }
        if (receivedMessage.getClass().equals(WaitProceedMessage.class)) {
            WaitProceedMessage waitProceedMessage = (WaitProceedMessage) receivedMessage;
            waitProceedMessage.setListener(this);
        }
        receivedMessage.process(this);
    }

    @Override
    public void onPlayerCountReached() {}

    @Override
    public void onPlayerShipBoardCorrect() {
        initFlight(this.gameModel);
    }

    @Override
    public void onPutFiguresInShip() {
        putFigureInShip();
    }

    @Override
    public void finishGame() {
        endGame(this.gameModel);
    }

    /**
     * This method initializes the game controlled by this controller. It can be called only after all the required players (2,3 or 4) have been added to the game.
     */
    public void initGame(HashMap<String, String> nicknamesToIdMap) {
        for(int i = 0; i < gameModel.getConnectedPlayers(); i++){
            Color playerColor;
            switch(i) {
                case 0: playerColor= Color.GREEN;
                    break;
                case 1: playerColor= Color.BLUE;
                    break;
                case 2: playerColor= Color.RED;
                    break;
                case 3: playerColor= Color.YELLOW;
                    break;
                default: playerColor= Color.YELLOW;
                    break;
            }
            gameModel.addPlayer(new Player(gameModel.getNicknames().get(i), gameModel.getIds().get(i), playerColor, gameModel.getFlightType()));
        }
        gameModel.setGameTile();
        gameModel.setCardsPiles();
        gameModel.setFlightBoard();

        for (Player player : gameModel.getPlayers()) {
            player.addObserver(this);
        }
        sendOtherPlayersShipboard(gameModel);
        for (Player player : gameModel.getPlayers()) {
            player.setPlayerState(PlayerState.IDLE);
        }

        gameModel.setGameState(GameState.BUILDING);

        for (Player player : gameModel.getPlayers()) {
            this.update(new ClearPageMessage(player.getId()));
            this.update(new ShowTilesDeckMessage(player.getId(), gameModel.getGameTile()));
            this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
        }

    }

    /**
     * This method initializes the second phase of the game controlled by this controller. It can be called only after all the players have assembled their ship board correctly.
     * @param gameModel is the game controlled by this controller.
     */
    private void initFlight(GameModel gameModel) {
        FlightBoard flightBoard = gameModel.getFlightBoard();
        flightBoard.setPlayersInitialPositions(gameModel.getTempPositions());
        gameModel.setCardsToPlay();
        gameModel.setGameState(GameState.PLAYING);

        for (Player player : gameModel.getPlayers()) {
            player.setPlayerState(PlayerState.PLAY);
        }

        gameModel.refreshPlayersPosition();
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

        for (Player player : gameModel.getPlayers()) {
            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                this.update(new ProceedNextCardMessage(player.getId(), null, gameModel.getCardsToPlay()));
            }
            else {
                this.update(new ProceedNextCardMessage(player.getId(), null, gameModel.getCardsToPlay()));
            }
        }
    }

    /**
     * This method allows the players to put a figure in the ship
     */
    private void putFigureInShip() {
        for (Player player : gameModel.getPlayers()) {
            this.update(new ClearPageMessage(player.getId()));

            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
            for(Player player1 : gameModel.getPlayers()) {
                shipBoards.put(player1.getNickname(), player1.getShipBoard());
            }
            for(Player otherPlayer : gameModel.getPlayers()) {
                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
            }
            this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
        }
    }

    /**
     * This method sends the players' shipboards to the current player, so he can check what his opponents are doing.
     * @param gameModel is the game controlled by this controller.
     */
    private void sendOtherPlayersShipboard(GameModel gameModel) {
        for (Player player : gameModel.getPlayers()) {
            playersShipBoards.put(player.getNickname(), player.getShipBoard());
        }
    }

    /**
     * This method ends the game (if there are no more cards), calculates the point earned by each player, and declares the winner.
     * @param gameModel is the game controlled by this controller.
     * @return {@code true} if the game ended, {@code false} otherwise.
     */
    private void endGame(GameModel gameModel) {
        gameModel.setGameState(GameState.END);

        if (gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
            int positionCredits = 4;
            for (Player player : gameModel.getPlayersPosition()) {
                player.incrementCosmicCredit(positionCredits);
                positionCredits--;
            }

            int minExposedConnectors = gameModel.getPlayersPosition().stream()
                    .mapToInt(p -> p.getShipBoard().numExposedConnectors())
                    .min().orElse(0);

            ArrayList<Player> playersWithMinExposedConnectors = gameModel.getPlayersPosition().stream()
                    .filter(p -> p.getShipBoard().numExposedConnectors() == minExposedConnectors)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (Player player : playersWithMinExposedConnectors) {
                player.incrementCosmicCredit(2);
            }
        }

        if (gameModel.getFlightType() == FlightType.STANDARD_FLIGHT) {
            int positionCredits = 8;
            for (Player player : gameModel.getPlayersPosition()) {
                player.incrementCosmicCredit(positionCredits);
                positionCredits--;
                positionCredits--;
            }

            int minExposedConnectors = gameModel.getPlayersPosition().stream()
                    .mapToInt(p -> p.getShipBoard().numExposedConnectors())
                    .min().orElse(0);

            ArrayList<Player> playersWithMinExposedConnectors = gameModel.getPlayersPosition().stream()
                    .filter(p -> p.getShipBoard().numExposedConnectors() == minExposedConnectors)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (Player player : playersWithMinExposedConnectors) {
                player.incrementCosmicCredit(4);
            }
        }

        for (Player player : gameModel.getPlayers()) {
            ArrayList<Color> goodsBlockOnShipBoard = player.getShipBoard().getGoodsBlockOnShipBoard();
            int totCredits = 0;
            for (Color color : goodsBlockOnShipBoard) {
                switch (color) {
                    case RED:
                        totCredits = totCredits + 4;
                        break;
                    case YELLOW:
                        totCredits = totCredits + 3;
                        break;
                    case GREEN:
                        totCredits = totCredits + 2;
                        break;
                    case BLUE:
                        totCredits = totCredits + 1;
                        break;
                    default:
                        break;
                }
            }
            if(gameModel.getRetiredPlayers().contains(player)) {
                totCredits = (int) Math.ceil((double) totCredits/2);
            }

            player.incrementCosmicCredit(totCredits);
            player.decreaseCosmicCredit(player.getShipBoard().getNumLostTiles());
        }

        getNicknameAndCredits();

        for (String nickname : playersCredits.keySet()) {
            if (playersCredits.get(nickname) >= 1) {
                this.winners.add(nickname);
            }
        }

        ArrayList<Player> players = new ArrayList<>();
        for(Player player : gameModel.getPlayers()) {
            players.add(player);
        }

        players.sort(Comparator.comparing(Player::getCosmicCredit).reversed());
        for(Player player : gameModel.getPlayers()) {
            this.update(new ClearPageMessage(player.getId()));
            if(player.getCosmicCredit() > 0) {
                this.update(new WinnerMessage(player.getId(), true, players));
            }
            else {
                this.update(new WinnerMessage(player.getId(), false, players));
            }
        }
    }

    /**
     * This method inserts the nickname of the players and his points in the playerCredits.
     */
    private void getNicknameAndCredits() {
        for(Player player : gameModel.getPlayers()){
            playersCredits.put(player.getNickname(), player.getCosmicCredit());
        }
    }








    
    
    
/**
 * This method manages the choice of the player on the abandoned ship card
 * @param clientId is the id of the client
 * @param card is the card considered
 * @param choice is the choice of the player
 */


    public void handleAbandonedShipChoiceMessage(String clientId, Card card, String choice) {
        if (gameModel.getGameState() == GameState.ABANDONED_SHIP) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                AbandonedShip abandonedShipCard = (AbandonedShip) card;
                if(choice.toLowerCase().equals("dock")) {
                    boolean isGood = abandonedShipCard.processAbandonedShipChoice(gameModel.getPlayerInTurn());
                    if(isGood) {
                        gameModel.setGameState(GameState.REMOVE_FIGURES);
                        gameModel.getFlightBoard().movePlayerBackward(gameModel.getPlayerInTurn(), abandonedShipCard.getLoseFlightDays()); // move the player on the flightboard
                        gameModel.getPlayerInTurn().setPenaltyEquipment(abandonedShipCard.getNumOfLoseFigures());
                        gameModel.getPlayerInTurn().incrementCosmicCredit(abandonedShipCard.getNumOfCreditsTaken());

                        for(Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    player.setPlayerState(PlayerState.REMOVE_FIGURE);
                                    this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                    this.update(new AskRemoveFigureMessage(player.getId()));
                                }
                                else {
                                    player.setPlayerState(PlayerState.WAIT);
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                }
                            }
                        }
                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                        }

                        gameModel.refreshPlayersPosition();
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "You don't have enough figures"));
                    }
                }
                else if (choice.toLowerCase().equals("skip")){
                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.PLAYING);
                        gameModel.refreshPlayersPosition();

                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice is not correct"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: ABANDONED SHIP CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manages the choice of the player on the abandoned station card
     * @param clientId is the id of the client
     * @param card is the card considered
     * @param choice is the choice of the player
     */

    public void handleAbandonedStationChoiceMessage(String clientId, Card card, String choice) {
        if (gameModel.getGameState() == GameState.ABANDONED_STATION) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                AbandonedStation abandonedStationCard = (AbandonedStation) card;
                if(choice.toLowerCase().equals("dock")) {
                    boolean isGood = abandonedStationCard.processAbandonedStationChoice(gameModel.getPlayerInTurn());
                    if(isGood) {

                        gameModel.setGameState(GameState.GAIN_GOODS);
                        gameModel.getFlightBoard().movePlayerBackward(gameModel.getPlayerInTurn(), abandonedStationCard.getLoseFlightDays()); // move the player on the flightboard

                        ArrayList<Color> temp = abandonedStationCard.getColorOfGoodsTaken();
                        for(Color goodsColor : temp) {
                            gameModel.getPlayerInTurn().insertGoodsBlock(goodsColor);
                        }

                        for(Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    player.setPlayerState(PlayerState.GAIN_GOOD);
                                    this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                    this.update(new GainedGoodsMessage(player.getId(), card, player.getTempGoodsBlock()));
                                }
                                else {
                                    player.setPlayerState(PlayerState.WAIT);
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                }
                            }
                        }

                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                        }

                        gameModel.refreshPlayersPosition();
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "You don't have enough figures"));
                    }
                }
                else if (choice.toLowerCase().equals("skip")){
                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.PLAYING);
                        gameModel.refreshPlayersPosition();

                        gameModel.setGameState(GameState.PLAYING);
                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice is not correct"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: ABANDONED STATION CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows the player to activate cannons when possible
     * @param clientId is the id of the client
     * @param card is the card considered
     */

    public void handleActivateCannonMessage(String clientId, Card card, int col, int row) {
        if (gameModel.getGameState() == GameState.SMUGGLERS || gameModel.getGameState() == GameState.SLAVERS
                || (gameModel.getGameState() == GameState.FASE_3 && card.getLevel() == 1) || (gameModel.getGameState() == GameState.FASE_1 && card.getLevel() == 2)
                || gameModel.getGameState() == GameState.METEOR_SWARM || gameModel.getGameState() == GameState.PIRATES) {
            if (gameModel.getGameState() == GameState.SLAVERS || gameModel.getGameState() == GameState.PIRATES || gameModel.getGameState() == GameState.SMUGGLERS || (gameModel.getGameState() == GameState.FASE_3 && card.getLevel() == 1) || (gameModel.getGameState() == GameState.FASE_1 && card.getLevel() == 2) ) {
                if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                    if (gameModel.getPlayerInTurn().getShipBoard().getNumBatteries() == 0) {
                        this.update(new GenericErrorMessage(clientId, "You have no batteries"));
                    }
                    else {
                        // 1 if ok, 2 if already active, 3 if coordinates not correct
                        int choice = gameModel.getPlayerInTurn().getShipBoard().activateCannon(row, col);
                        if (choice == 1) {

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for(Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for(Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if(otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new AskRemoveBatteryMessage(clientId));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        }
                        else if (choice == 2) {
                            this.update(new GenericErrorMessage(clientId, "The cannon in space ("+ row + ","+ col + ") is already active"));
                        }
                        else {
                            this.update(new GenericErrorMessage(clientId, "The tile in coordinates ("+ row+ ","+ col + ") is not a cannon"));
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
                }
            }
            else {
                if (gameModel.getGameState() == GameState.METEOR_SWARM) {
                    MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;
                    if (meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter() - 1).getPower() == 1) {
                        this.update(new GenericErrorMessage(clientId, "Can't activate cannon because meteor size is 1"));
                        return;
                    }

                    Player p = null;
                    for (Player otherPlayer : gameModel.getPlayers()) {
                        if (otherPlayer.getId().equals(clientId)) {
                            p = otherPlayer;
                        }
                    }

                    if (p.getShipBoard().getNumBatteries() == 0) {
                        this.update(new GenericErrorMessage(p.getId(), "You have no batteries"));
                    } else {
                        // 1 if ok, 2 if already active, 3 if coordinates not correct
                        int choice = p.getShipBoard().activateCannon(row, col);
                        if (choice == 1) {
                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new AskRemoveBatteryMessage(clientId));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        } else if (choice == 2) {
                            this.update(new GenericErrorMessage(clientId, "The cannon in space (" + row + "," + col + ") is already active"));
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a cannon"));
                        }
                    }
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: ACTIVATE CANNON is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows the player to activate engines when possible
     * @param clientId is the id of the client
     * @param card is the card considered
     */

    public void handleActivateEngineMessage(String clientId, Card card, int col, int row) {
        if (gameModel.getGameState() == GameState.OPEN_SPACE || (gameModel.getGameState() == GameState.FASE_2)) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {

                if (gameModel.getPlayerInTurn().getShipBoard().getNumBatteries() == 0) {
                    this.update(new GenericErrorMessage(clientId, "You have no batteries"));
                } else {
                    // 1 if ok, 2 if already active, 3 if coordinates not correct
                    int choice = gameModel.getPlayerInTurn().getShipBoard().activateEngine(row, col);
                    if (choice == 1) {

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if(otherPlayer.getId().equals(clientId)) {
                                otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        this.update(new DrawnCardMessage(clientId, card, true));
                        this.update(new AskRemoveBatteryMessage(clientId));
                        this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                    } else if (choice == 2) {
                        this.update(new GenericErrorMessage(clientId, "The engine in space ("+ row + ","+ col + ") is already active"));
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates ("+ row+ ","+ col + ") is not an engine"));}
                }
            } else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: " + gameModel.getPlayerInTurn().getNickname()));
            }
        } else {
            this.update(new GenericErrorMessage(clientId, "This message type: ACTIVATE ENGINE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows the player to activate shields when possible
     * @param clientId is the id of the client
     * @param card is the card considered
     */

    public void handleActivateShieldMessage(String clientId, Card card, int col, int row) {
        if (gameModel.getGameState() == GameState.METEOR_SWARM || gameModel.getGameState() == GameState.COMBAT_ZONE || gameModel.getGameState() == GameState.PIRATES) {
            if (gameModel.getGameState() == GameState.METEOR_SWARM) {
                MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;
                if (meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter() - 1).getPower() == 2) {
                    this.update(new GenericErrorMessage(clientId, "Can't activate shield because meteor size is large"));
                    return;
                }

                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p.getShipBoard().getNumBatteries() == 0) {
                    this.update(new GenericErrorMessage(clientId, "You have no batteries"));
                } else {
                    // 1 if ok, 2 if already active, 3 if coordinates not correct
                    int choice = p.getShipBoard().activateShield(row, col);
                    if (choice == 1) {

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for (Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for (Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if (otherPlayer.getId().equals(clientId)) {
                                otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        this.update(new DrawnCardMessage(clientId, card, true));
                        this.update(new AskRemoveBatteryMessage(clientId));
                        this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                    } else if (choice == 2) {
                        this.update(new GenericErrorMessage(clientId, "The shield in space (" + row + "," + col + ") is already active"));
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a shield"));
                    }
                }
            } else if (gameModel.getGameState() == GameState.COMBAT_ZONE) {
                if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                    if (gameModel.getPlayerInTurn().getShipBoard().getNumBatteries() == 0) {
                        this.update(new GenericErrorMessage(clientId, "You have no batteries"));
                    }
                    else {
                        // 1 if ok, 2 if already active, 3 if coordinates not correct
                        int choice = gameModel.getPlayerInTurn().getShipBoard().activateShield(row, col);
                        if (choice == 1) {
                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new AskRemoveBatteryMessage(clientId));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                        }
                        else if (choice == 2) {
                            this.update(new GenericErrorMessage(clientId, "The shield in space (" + row + "," + col + ") is already active"));
                        }
                        else {
                            this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a shield"));
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
                }
            }
            else if (gameModel.getGameState() == GameState.PIRATES) {
                Pirates piratesCard = (Pirates) card;
                if (piratesCard.getShotsPowerArray().get(piratesCard.getCounter()-1) == 2) {
                    this.update(new GenericErrorMessage(clientId, "Can't activate shield because cannon fire is heavy"));
                    return;
                }

                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p.getShipBoard().getNumBatteries() == 0) {
                    this.update(new GenericErrorMessage(clientId, "You have no batteries"));
                } else {
                    // 1 if ok, 2 if already active, 3 if coordinates not correct
                    int choice = p.getShipBoard().activateShield(row, col);
                    if (choice == 1) {

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for (Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for (Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if (otherPlayer.getId().equals(clientId)) {
                                otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        this.update(new DrawnCardMessage(clientId, card, true));
                        this.update(new AskRemoveBatteryMessage(clientId));
                        this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                    }
                    else if (choice == 2) {
                        this.update(new GenericErrorMessage(clientId, "The shield in space (" + row + "," + col + ") is already active"));
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a shield"));
                    }
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: ACTIVATE SHIELD is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows the player to set the direction of the card in the ship board
     * @param clientId is the id of the client
     * @param direction is the direction chosen
     */

    public void handleChangeTileDirectionMessage(String clientId, String direction) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PICK UP TILE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if (!(direction.toLowerCase().equals("nord")) && !(direction.toLowerCase().equals("sud")) && !(direction.toLowerCase().equals("est")) && !(direction.toLowerCase().equals("ovest"))) {
            this.update(new GenericErrorMessage(clientId, "Incorrect direction"));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                if (player.getTileInHand() == null) {
                    this.update(new GenericErrorMessage(clientId, "You don't have a tile in hand"));
                    return;
                }

                player.getTileInHand().setDirection(direction);
                this.update(new PickedTileMessage(clientId, player.getTileInHand()));
                return;
            }
        }
    }

    /**
     * This method allows the player to choose the flight type for the game
     * @param clientId is the id of the client
     * @param flightType is the type of flight chosen
     */

    public void handleChooseFlightTypeMessage(String clientId, FlightType flightType) {
        if (gameModel.getGameState() == GameState.LOGIN) {
            if (gameModel.getFlightType() != null) {
                this.update(new GenericErrorMessage(clientId, "The flight type for this game is already set."));
            } else {
                gameModel.setFlightType(flightType);
                this.update(new ChooseFlightTypeMessage(clientId, flightType));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: CHOOSE_FLIGHT_TYPE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manage the combat zone card
     * @param clientId is the id of the client
     * @param card is the type of card drawn from the deck
     * @param sum is the sum of the strength
     */

    public void handleCombatZoneChoiceMessage(String clientId, Card card, int sum) {
        if (gameModel.getGameState() == GameState.COMBAT_ZONE) {
            combatZoneFight = false;
            CombatZone combatZoneCard = (CombatZone) card;
            ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];

            if(meteors.get(combatZoneCard.getCounter() - 1).getPower() == 1) {
                if (gameModel.getPlayerInTurn().getShipBoard().isHit()) {
                    String direction = meteors.get(combatZoneCard.getCounter() - 1).getDirection();
                    if (!gameModel.getPlayerInTurn().getShipBoard().getCoveredDirection().get(direction)) {
                        gameModel.getPlayerInTurn().getShipBoard().removeComponent(direction, sum);
                    }

                    for(Player player1 : gameModel.getPlayersPosition()) {
                        player1.getShipBoard().defineAlienCabine();
                    }

                    gameModel.getPlayerInTurn().getShipBoard().restoreShields();
                    gameModel.getPlayerInTurn().getShipBoard().restoreCannons();
                    this.update(new ShowShipBoardMessage(gameModel.getPlayerInTurn().getId(), gameModel.getPlayerInTurn().getShipBoard()));
                    gameModel.getPlayerInTurn().getShipBoard().setHit(false);
                }
            }
            else {
                String direction = meteors.get(combatZoneCard.getCounter() - 1).getDirection();
                gameModel.getPlayerInTurn().getShipBoard().removeComponent(direction, sum);

                for(Player player1 : gameModel.getPlayersPosition()) {
                    player1.getShipBoard().defineAlienCabine();
                }

                this.update(new ShowShipBoardMessage(gameModel.getPlayerInTurn().getId(), gameModel.getPlayerInTurn().getShipBoard()));
                gameModel.getPlayerInTurn().getShipBoard().setHit(false);
            }

            boolean allValid = true;
            for (Player player : gameModel.getPlayersPosition()) {
                try {
                    player.getShipBoard().validateShipBoard();
                } catch (MultipleValidationErrorsException e) {
                    allValid = false;
                    this.update(new ShowShipErrorsMessage(player.getId(), e.getErrorMessages()));
                    player.setHasFinishedBuilding(false);
                }
            }

            if (allValid) {
                if (combatZoneCard.getCounter() == meteors.size()) {
                    gameModel.setGameState(GameState.PLAYING);

                    for (Player player : gameModel.getPlayers()) {
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                        }
                    }
                }
                else {
                    gameModel.setGameState(GameState.ROLL_DICE);
                    this.update(new SetCardInUseMessage(gameModel.getPlayerInTurn().getId(), card));
                    this.update(new UpdateParametresMessage(gameModel.getPlayerInTurn().getId(), gameModel.getFlightBoard()));
                    this.update(new AskRollDiceMessage(gameModel.getPlayerInTurn().getId(), card, true, false));
                }
            }
            else {
                gameModel.setGameState(GameState.REPAIRING);
                this.update(new ChangeTuiStateMessage(gameModel.getPlayerInTurn().getId(),2));

                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                for(Player player1 : gameModel.getPlayers()) {
                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                }

                for(Player otherPlayer : gameModel.getPlayers()) {
                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                }

                if(gameModel.getPlayerInTurn().getShipBoard().getCorrectShip()) {
                    gameModel.getPlayerInTurn().setHasFinishedBuilding(true);
                }
                else {
                    gameModel.getPlayerInTurn().setPlayerState(REPAIR);
                }

                for (Player player : gameModel.getPlayersPosition()) {
                    player.setProceed(false);
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: COMBAT ZONE CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manage the defeat pirates card
     * @param clientId is the id of the client
     * @param card is the type of card drawn from the deck
     * @param sum is the sum of the strength
     */


    public void handleDefeatedPiratesMessage(String clientId, Card card, int sum) {
        if (gameModel.getGameState() == GameState.PIRATES) {
            for (Player player : gameModel.getPlayers()) {
                if(player.getId().equals(clientId) && !gameModel.getDefeatedPlayers().contains(player)) {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
                    return;
                }
            }

            for (Player player : gameModel.getDefeatedPlayers()) {
                if(player.getId().equals(clientId)) {
                    player.setProceed(true);
                }
            }

            boolean proceed = true;
            for(Player player : gameModel.getDefeatedPlayers()) {
                if(!player.getProceed()) {
                    proceed = false;
                    break;
                }
            }

            if(proceed) {
                Pirates pirates = (Pirates) card;
                if(pirates.getShotsPowerArray().get(pirates.getCounter() - 1) == 1) {
                    for(Player player : gameModel.getDefeatedPlayers()) {
                        if (!player.getShipBoard().getCoveredDirection().get("nord")) {
                            player.getShipBoard().removeComponent("nord", sum);
                        }

                        for(Player player1 : gameModel.getPlayersPosition()) {
                            player1.getShipBoard().defineAlienCabine();
                        }

                        player.getShipBoard().restoreShields();
                        player.getShipBoard().restoreCannons();
                        this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                        player.getShipBoard().setHit(false);
                    }
                }
                else {
                    for(Player player : gameModel.getDefeatedPlayers()) {
                        player.getShipBoard().removeComponent("nord", sum);

                        for(Player player1 : gameModel.getPlayersPosition()) {
                            player1.getShipBoard().defineAlienCabine();
                        }

                        this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                        player.getShipBoard().setHit(false);
                    }
                }

                boolean allValid = true;
                for (Player player : gameModel.getPlayersPosition()) {
                    try {
                        player.getShipBoard().validateShipBoard();
                    } catch (MultipleValidationErrorsException e) {
                        allValid = false;
                        this.update(new ShowShipErrorsMessage(player.getId(), e.getErrorMessages()));
                        player.setHasFinishedBuilding(false);
                    }
                }

                if(allValid) {
                    if(pirates.getCounter() == pirates.getShotsPowerArray().size()) {
                        gameModel.setGameState(GameState.PLAYING);

                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.ROLL_DICE);
                        for(Player player : gameModel.getDefeatedPlayers()) {
                            this.update(new SetCardInUseMessage(player.getId(), card));
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));

                            gameModel.setPlayerInTurn(gameModel.getDefeatedPlayers().get(0));
                            if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                            } else {
                                this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                            }
                        }
                    }
                } else {
                    gameModel.setGameState(GameState.REPAIRING);

                    for (Player player : gameModel.getDefeatedPlayers()) {
                        if (player.getShipBoard().getCorrectShip()) {
                            this.update(new ChangeTuiStateMessage(player.getId(),3));
                        }
                        else {
                            this.update(new ChangeTuiStateMessage(player.getId(),2));
                        }
                    }

                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                    for(Player player1 : gameModel.getPlayers()) {
                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                    }

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                    }

                    for (Player player : gameModel.getDefeatedPlayers()) {
                        if (player.getShipBoard().getCorrectShip()) {
                            player.setHasFinishedBuilding(true);
                            player.setPlayerState(WAIT);
                            this.update(new GenericMessage2(player.getId(), "\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players..."));
                        } else {
                            player.setPlayerState(REPAIR);
                        }
                    }
                }

                for (Player player : gameModel.getPlayersPosition()) {
                    player.setProceed(false);
                }
            }
            else {
                for(Player player : gameModel.getPlayers()) {
                    if(player.getId().equals(clientId)) {
                        if(player.getShipBoard().isHit()) {
                            this.update(new DrawnCardMessage(clientId, card, false));
                        }
                    }
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: DEFEATED PLAYERS is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manage the first phase of the card, setting the player in turn
     * @param clientId is the id of the client
     * @param card is the type of card drawn from the deck
     */


    public void handleFase1ChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() != GameState.FASE_1) {
            this.update(new GenericErrorMessage(clientId, "This message type: FASE 1 CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if (!(clientId.equals(gameModel.getPlayerInTurn().getId()))) {
            this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: " + gameModel.getPlayerInTurn().getNickname()));
            return;
        }

        if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
            gameModel.setPlayerInTurn(gameModel.getNextPlayer());
            for(Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new SetCardInUseMessage(player.getId(), card));
                    this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                }
                else {
                    this.update(new SetCardInUseMessage(player.getId(), card));
                    this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                }
            }
        }
        else {
            Optional<Player> minPlayerFireStrength = gameModel.getPlayersPosition().stream().reduce((p1, p2) -> p1.getShipBoard().calculateFireStrength() <= p2.getShipBoard().calculateFireStrength() ? p1 : p2);
            Player player = minPlayerFireStrength.orElse(null);

            CombatZone combatZoneCard = (CombatZone) card;
            int lostFlightDays = (int) combatZoneCard.getFaseOne()[2];

            gameModel.getFlightBoard().movePlayerBackward(player, lostFlightDays);
            gameModel.refreshPlayersPosition();
            gameModel.setGameState(GameState.FASE_2);

            for (Player otherPlayer : gameModel.getPlayers()) {
                if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                    this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                    this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                }
            }
            gameModel.refreshPlayersPosition();
        }
    }

    /**
     * This method manage the second phase of the card, setting the player in turn
     * @param clientId is the id of the client
     * @param card is the type of card drawn from the deck
     */

    public void handleFase2ChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() != GameState.FASE_2) {
            this.update(new GenericErrorMessage(clientId, "This message type: FASE 2 CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if (!(clientId.equals(gameModel.getPlayerInTurn().getId()))) {
            this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: " + gameModel.getPlayerInTurn().getNickname()));
            return;
        }

        if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
            gameModel.setPlayerInTurn(gameModel.getNextPlayer());
            for(Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new SetCardInUseMessage(player.getId(), card));
                    this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                }
                else {
                    this.update(new SetCardInUseMessage(player.getId(), card));
                    this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                }
            }
        }
        else {
            Optional<Player> minPlayerEngineStrength = gameModel.getPlayersPosition().stream().reduce((p1,p2) -> p1.getShipBoard().calculateEngineStrength() <= p2.getShipBoard().calculateEngineStrength() ? p1 : p2);
            Player player = minPlayerEngineStrength.orElse(null);
            gameModel.setPlayerInTurn(player);

            CombatZone combatZoneCard = (CombatZone) card;
            if(combatZoneCard.getLevel() == 1) {

                if(player.getShipBoard().getNumFigures() == 0 && !player.getShipBoard().getHasPurpleAlien() && !player.getShipBoard().getHasBrownAlien()) {
                    player.setPenaltyEquipment(0);
                    gameModel.setGameState(GameState.FASE_3);
                    player.setPlayerState(PlayerState.PLAY);

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                            this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                            this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                        }
                    }
                    return;
                }

                player.setPenaltyEquipment((int) combatZoneCard.getFaseTwo()[2]);
                gameModel.setGameState(GameState.REMOVE_FIGURES);

                for(Player otherPlayer : gameModel.getPlayersPosition()) {
                    if(player.getId().equals(otherPlayer.getId())) {
                        this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                        this.update(new AskRemoveFigureMessage(otherPlayer.getId()));
                        otherPlayer.setPlayerState(PlayerState.REMOVE_FIGURE);
                    }
                    else {
                        otherPlayer.setPlayerState(PlayerState.WAIT);
                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                    }
                    this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                }
            }
            else {
                if (player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                    player.setPenaltyGoods(0);
                    gameModel.setGameState(GameState.FASE_3);
                    player.setPlayerState(PlayerState.PLAY);

                    for (Player otherPlayer : gameModel.getPlayers()) {
                        if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                            this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                            this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                        }
                    }
                    return;
                }

                player.setPenaltyGoods((int) combatZoneCard.getFaseTwo()[2]);
                gameModel.setGameState(GameState.REMOVE_GOODS);

                if (player.getShipBoard().rarestGoodsBlock() == null) {
                    for (Player otherPlayer : gameModel.getPlayersPosition()) {
                        if (player.getId().equals(otherPlayer.getId())) {
                            this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                            this.update(new AskRemoveBatteryMessage(otherPlayer.getId()));
                            otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                        } else {
                            otherPlayer.setPlayerState(PlayerState.WAIT);
                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                        }
                        this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                    }
                    return;
                }

                for (Player otherPlayer : gameModel.getPlayersPosition()) {
                    if (player.getId().equals(otherPlayer.getId())) {
                        this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                        this.update(new AskRemoveGoodMessage(otherPlayer.getId()));
                        otherPlayer.setPlayerState(PlayerState.REMOVE_GOOD);
                    } else {
                        otherPlayer.setPlayerState(PlayerState.WAIT);
                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                    }
                    this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                }
            }
        }
    }

    /**
     * This method manage the third phase of the card, setting the player in turn
     * @param clientId is the id of the client
     * @param card is the type of card drawn from the deck
     */

    public void handleFase3ChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() != GameState.FASE_3){
            this.update(new GenericErrorMessage(clientId, "This message type: FASE 3 CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if (!(clientId.equals(gameModel.getPlayerInTurn().getId()))) {
            this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: " + gameModel.getPlayerInTurn().getNickname()));
            return;
        }

        if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
            gameModel.setPlayerInTurn(gameModel.getNextPlayer());

            for(Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new SetCardInUseMessage(player.getId(), card));
                    this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                }
                else {
                    this.update(new SetCardInUseMessage(player.getId(), card));
                    this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                }
            }
        }
        else {
            Optional<Player> minPlayerFireStrength = gameModel.getPlayersPosition().stream().reduce((p1,p2) -> p1.getShipBoard().calculateFireStrength() <= p2.getShipBoard().calculateFireStrength() ? p1 : p2);
            gameModel.setPlayerInTurn(minPlayerFireStrength.orElse(null));
            gameModel.setGameState(GameState.ROLL_DICE);

            for(Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new SetCardInUseMessage(player.getId(),card));
                    this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                }
                else {
                    this.update(new SetCardInUseMessage(player.getId(),card));
                    this.update(new DrawnCardMessage(player.getId(), card, false));
                }
            }
        }
    }

    /**
     * This method manage the events after the player has finished to build the ship board
     * @param clientId is the id of the client
     * @param listener notify the controller
     */

    public void handleFinishedBuildingMessage(String clientId, GameEventListener listener) {
        if(gameModel.getGameState() == GameState.BUILDING || gameModel.getGameState() == GameState.FIXING || gameModel.getGameState() == GameState.FINISHING) {
            Player p = null;
            for(Player player : gameModel.getPlayers()) {
                if(player.getId().equals(clientId)) {
                    p = player;
                }
            }

            if(p != null && p.getPlayerState() == WAIT) {
                this.update(new GenericErrorMessage(clientId, "You are not in turn"));
                return;
            }

            p.setHasFinishedBuilding(true);
            gameModel.addTempPosition(p);
            if(p.getTileInHand() != null) {
                p.getShipBoard().incrementNumLostTiles();
            }

            if(gameModel.getFlightType() == FlightType.STANDARD_FLIGHT) {
                if(p.getShipBoard().getSpace(0,5).getComponent() != null) {
                    p.getShipBoard().incrementNumLostTiles();
                    p.getShipBoard().getSpace(0,5).insertComponent(null);
                }
                if(p.getShipBoard().getSpace(0,6).getComponent() != null) {
                    p.getShipBoard().incrementNumLostTiles();
                    p.getShipBoard().getSpace(0,6).insertComponent(null);
                }
            }

            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
            for(Player player1 : gameModel.getPlayers()) {
                shipBoards.put(player1.getNickname(), player1.getShipBoard());
            }

            for(Player otherPlayer : gameModel.getPlayers()) {
                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                if(otherPlayer.getId().equals(clientId)) {
                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                }
            }

            boolean proceed = true;
            for(Player player : gameModel.getPlayers()) {
                if(!player.getHasFinishedBuilding()) {
                    proceed = false;
                }
            }

            if(proceed) {
                for(Player player : gameModel.getPlayers()) {
                    this.update(new Proceed2Message(player.getId()));
                }

                boolean allValid = true;
                for (Player player : gameModel.getPlayers()) {
                    try {
                        player.getShipBoard().validateShipBoard();
                    } catch (MultipleValidationErrorsException e) {
                        allValid = false;
                        this.update(new ShowShipErrorsMessage(player.getId(), e.getErrorMessages()));
                        player.setHasFinishedBuilding(false);
                    }
                }

                if (allValid) {
                    if(gameModel.getFlightType()==FlightType.STANDARD_FLIGHT)
                    {
                        for(Player player : gameModel.getPlayers()) {
                            player.getShipBoard().defineAlienCabine();
                        }
                    }
                    gameModel.setGameState(GameState.POPULATE);
                    for(Player player : gameModel.getPlayers()) {
                        player.setPlayerState(PlayerState.POPULATE);
                    }
                    listener.onPutFiguresInShip();
                }
                else {
                    gameModel.setGameState(GameState.FIXING);
                    for(Player player : gameModel.getPlayers()) {
                        if(player.getShipBoard().getCorrectShip()) {
                            player.setHasFinishedBuilding(true);
                            player.setPlayerState(WAIT);
                            this.update(new GenericMessage2(player.getId(), "Your ship board is correct. You have to wait the other players to finish correcting their ship."));
                        }
                        else {
                            player.setPlayerState(FIX);
                            gameModel.removeTempPosition(player);
                        }
                    }

                    for(Player player : gameModel.getPlayers()) {
                        this.update(new TempPositionMessage(player.getId(), gameModel.getTempPositions()));
                    }
                }
            }
            else {
                this.update(new GenericMessage2(clientId, "Waiting for other players to finish building their ships."));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId,"This message type: FINISHED BUILDING is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }
    /**
     * This method manages next step when all the players have finished to build the ship
     * @param clientId is the client id
     * @param listener notify the controller
     */

    public void handleFinishedPopulateMessage(String clientId, GameEventListener listener) {
        if(gameModel.getGameState() == GameState.POPULATE) {
            Player p = null;
            for(Player player : gameModel.getPlayers()) {
                if(player.getId().equals(clientId)) {
                    p = player;
                }
            }

            if(p != null && p.getPlayerState() == WAIT) {
                this.update(new GenericErrorMessage(clientId, "You are not in turn"));
                return;
            }

            p.setPlayerState(WAIT);
            boolean proceed = true;
            for(Player player : gameModel.getPlayers()) {
                if(player.getPlayerState() != WAIT) {
                    proceed = false;
                }
            }

            if(proceed) {
                listener.onPlayerShipBoardCorrect();
            }
            else {
                this.update(new GenericMessage2(clientId, "Waiting for other players to finish populate their ships."));
            }
        }
        else{
            this.update(new GenericErrorMessage(clientId,"This message type: FINISHED POPULATE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allow players to set properly the gains on the ship
     * @param clientId is the id client
     * @param choice is te position where the gains will be set
     **/

    public void handleGainGoodMessage(String clientId, String choice, Card card, int row, int col) {
        if (gameModel.getGameState() == GameState.GAIN_GOODS) {
            if (card.getCardType() == CardName.SMUGGLERS) {

                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.GAIN_GOOD) {
                    if (choice.equals("put")) {
                        if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CARGO) {
                            Cargo cargo = (Cargo) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                            Color goodsBlock = p.getTempGoodsBlock().get(0);
                            boolean value = cargo.putCargoIn(goodsBlock);
                            if (value) {
                                p.getTempGoodsBlock().remove(0);

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for(Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for(Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    if(otherPlayer.getId().equals(clientId)) {
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        if(player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                                            player.setPenaltyGoods(0);
                                        }
                                    }
                                }

                                if (p.getTempGoodsBlock().size() == 0) {
                                    boolean proceed = false;
                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        if (otherPlayer.getPenaltyGoods() > 0) {
                                            proceed = true;
                                        }
                                    }
                                    if (proceed) {
                                        gameModel.setGameState(GameState.REMOVE_GOODS);
                                        for (Player player : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                                if (player.getPenaltyGoods() > 0) {
                                                    if(player.getShipBoard().rarestGoodsBlock() == null) {
                                                        if(player.getShipBoard().getNumBatteries() == 0) {
                                                            player.setPenaltyGoods(0);
                                                            player.setPlayerState(PlayerState.WAIT);
                                                            this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                            this.update(new DrawnCardMessage(player.getId(), card, false));
                                                        }
                                                        else {
                                                            player.setPlayerState(PlayerState.REMOVE_BATTERY);
                                                            this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                            this.update(new DrawnCardMessage(player.getId(), card, false));
                                                            this.update(new AskRemoveBatteryMessage(player.getId()));
                                                        }
                                                    }
                                                    else {
                                                        player.setPlayerState(PlayerState.REMOVE_GOOD);
                                                        this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                                        this.update(new AskRemoveGoodMessage(player.getId()));
                                                    }
                                                } else {
                                                    player.setPlayerState(PlayerState.WAIT);
                                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                                }
                                            }
                                        }
                                    } else {
                                        gameModel.setGameState(GameState.PLAYING);
                                        for (Player otherPlayer : gameModel.getPlayers()) {
                                            otherPlayer.setPlayerState(PlayerState.PLAY);
                                        }

                                        for (Player player : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                            }
                                        }
                                    }
                                } else {
                                    this.update(new GainedGoodsMessage(clientId, card, p.getTempGoodsBlock()));
                                }
                            } else {
                                this.update(new GenericErrorMessage(clientId, "The cargo cannot fit the goods block considered"));
                            }
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row +","+ col + ") cannot contain a goods block"));
                        }
                    } else if (choice.equals("skip")) {
                        p.getTempGoodsBlock().remove(0);

                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                if(player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                                    player.setPenaltyGoods(0);
                                }
                            }
                        }

                        if (p.getTempGoodsBlock().size() == 0) {
                            boolean val = false;
                            for (Player otherPlayer : gameModel.getPlayers()) {
                                if (otherPlayer.getPenaltyGoods() > 0) {
                                    val = true;
                                }
                            }

                            if (val) {
                                gameModel.setGameState(GameState.REMOVE_GOODS);
                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        if (player.getPenaltyGoods() > 0) {
                                            if(player.getShipBoard().rarestGoodsBlock() == null) {
                                                if(player.getShipBoard().getNumBatteries() == 0) {
                                                    player.setPenaltyGoods(0);
                                                    player.setPlayerState(PlayerState.WAIT);
                                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                                }
                                                else {
                                                    player.setPlayerState(PlayerState.REMOVE_BATTERY);
                                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                                    this.update(new AskRemoveBatteryMessage(player.getId()));
                                                }
                                            }
                                            else {
                                                player.setPlayerState(PlayerState.REMOVE_GOOD);
                                                this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                this.update(new DrawnCardMessage(player.getId(), card, false));
                                                this.update(new AskRemoveGoodMessage(player.getId()));
                                            }
                                        } else {
                                            player.setPlayerState(PlayerState.WAIT);
                                            this.update(new DrawnCardMessage(player.getId(), card, false));
                                        }
                                    }
                                }
                            } else {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            }
                        } else {
                            this.update(new GainedGoodsMessage(clientId, card, p.getTempGoodsBlock()));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "This choice is not correct"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn"));
                }
            }

            else if (card.getCardType() == CardName.PLANETS) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.GAIN_GOOD) {
                    if (choice.equals("put")) {
                        if (p.getShipBoard().getSpace(row - 5, col - 4) != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CARGO) {
                            Cargo cargo = (Cargo) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                            Color goodsBlock = p.getTempGoodsBlock().get(0);
                            boolean value = cargo.putCargoIn(goodsBlock);
                            if (value) {
                                p.getTempGoodsBlock().remove(0);

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for(Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for(Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    if(otherPlayer.getId().equals(clientId)) {
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }

                                if (p.getTempGoodsBlock().size() == 0) {
                                    p.setPlayerState(PlayerState.WAIT);

                                    boolean proceed = true;
                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                            if (!otherPlayer.getTempGoodsBlock().isEmpty()) {
                                                proceed = false;
                                            }
                                        }
                                    }

                                    if (proceed) {
                                        gameModel.setGameState(GameState.PLAYING);
                                        for (Player otherPlayer : gameModel.getPlayers()) {
                                            otherPlayer.setPlayerState(PlayerState.PLAY);
                                        }

                                        for (Player player : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                            }
                                        }
                                    } else {
                                        this.update(new ChangeTuiStateMessage(clientId,1));
                                        this.update(new WaitMessage(clientId, card));
                                    }
                                } else {
                                    this.update(new GainedGoodsMessage(clientId, card, p.getTempGoodsBlock()));
                                }
                            } else {
                                this.update(new GenericErrorMessage(clientId, "The cargo cannot fit the goods block considered"));
                            }
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row+ "," + col + ") cannot contain a goods block"));
                        }
                    } else if (choice.equals("skip")) {
                        p.getTempGoodsBlock().remove(0);

                        if (p.getTempGoodsBlock().size() == 0) {
                            p.setPlayerState(PlayerState.WAIT);

                            boolean proceed = true;
                            for (Player otherPlayer : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                    if (!otherPlayer.getTempGoodsBlock().isEmpty()) {
                                        proceed = false;
                                    }
                                }
                            }

                            if (proceed) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            } else {
                                this.update(new ChangeTuiStateMessage(clientId,1));
                                this.update(new WaitMessage(clientId, card));
                            }
                        } else {
                            this.update(new GainedGoodsMessage(clientId, card, p.getTempGoodsBlock()));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "This choice is not correct"));
                    }
                }
            }

            else if (card.getCardType() == CardName.ABANDONED_STATION) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.GAIN_GOOD) {
                    if (choice.equals("put")) {
                        if (p.getShipBoard().getSpace(row - 5, col - 4) != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CARGO) {
                            Cargo cargo = (Cargo) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                            Color goodsBlock = p.getTempGoodsBlock().get(0);
                            boolean value = cargo.putCargoIn(goodsBlock);
                            if (value) {
                                p.getTempGoodsBlock().remove(0);

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for(Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for(Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    if(otherPlayer.getId().equals(clientId)) {
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }

                                if (p.getTempGoodsBlock().size() == 0) {
                                    gameModel.setGameState(GameState.PLAYING);
                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        otherPlayer.setPlayerState(PlayerState.PLAY);
                                        if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                            this.update(new ProceedNextCardMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                        }
                                    }
                                } else {
                                    this.update(new GainedGoodsMessage(clientId, card, p.getTempGoodsBlock()));
                                }
                            } else {
                                this.update(new GenericErrorMessage(clientId, "The cargo cannot fit the goods block considered"));
                            }
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row+ "," + col + ") cannot contain a goods block"));
                        }
                    } else if (choice.equals("skip")) {
                        p.getTempGoodsBlock().remove(0);

                        if (p.getTempGoodsBlock().size() == 0) {
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player otherPlayer : gameModel.getPlayers()) {
                                otherPlayer.setPlayerState(PlayerState.PLAY);
                                if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                    this.update(new ProceedNextCardMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        } else {
                            this.update(new GainedGoodsMessage(clientId, card, p.getTempGoodsBlock()));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "This choice is not correct"));
                    }
                }
            }
        } else {
            this.update(new GenericErrorMessage(clientId, "This message type: GAIN GOODS is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method sets the maximum players for the game
     * @param clientId is the client id
     * @param maxPlayers the max player for the game
     */

    public void handleMaxPlayersForGameMessage(String clientId, int maxPlayers) {
        if (gameModel.getGameState() == GameState.LOGIN) {
            if(gameModel.getMaxPlayers() > 1) {
                this.update(new GenericErrorMessage(clientId, "The players for this game are already set."));
            }
            else if(gameModel.isInBound(maxPlayers) == false) {
                this.update(new GenericErrorMessage(clientId, "The number of players for this game is out of bounds: it must be between 2 and "+ gameModel.MAX_PLAYERS));
            }
            else {
                gameModel.setMaxPlayers(maxPlayers);
                ArrayList<String> players = new ArrayList<>();
                for (String string : gameModel.getNicknames()) {
                    players.add(string);
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manages the meteor swarm card
     * @param clientId is the client id
     * @param card is the card drawn
     * @param sum is the sum of the cannons strength for each player's ship board
     **/

    public void handleMeteorSwarmChoiceMessage(String clientId, Card card, int sum) {
        if (gameModel.getGameState() == GameState.METEOR_SWARM) {
            boolean proc = true;

            for (Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(clientId)) {
                    player.setProceed(true);
                }
            }

            for (Player player : gameModel.getPlayersPosition()) {
                if (player.getProceed() == false) {
                    proc = false;
                }
            }

            if (proc == true) {
                MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;
                // if meteor is little
                if(meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getPower() == 1) {
                    for(Player player : gameModel.getPlayersPosition()) {
                        if(player.getShipBoard().isHit()) {
                            String direction = meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getDirection();
                            if(!player.getShipBoard().getCoveredDirection().get(direction)) {
                                player.getShipBoard().removeComponent(direction, sum);
                            }

                            for(Player player1 : gameModel.getPlayersPosition()) {
                                player1.getShipBoard().defineAlienCabine();
                            }

                            player.getShipBoard().restoreShields();
                            player.getShipBoard().restoreCannons();
                            this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                            player.getShipBoard().setHit(false);
                        }
                    }
                }
                else {
                    for(Player player : gameModel.getPlayersPosition()) {
                        if(player.getShipBoard().isHit()) {
                            String direction = meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getDirection();
                            boolean bool = player.getShipBoard().checkDoubleCannonMeteor(direction, sum);
                            if(!bool) {
                                player.getShipBoard().removeComponent(direction, sum);
                            }

                            for(Player player1 : gameModel.getPlayersPosition()) {
                                player1.getShipBoard().defineAlienCabine();
                            }

                            player.getShipBoard().restoreShields();
                            player.getShipBoard().restoreCannons();
                            this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                            player.getShipBoard().setHit(false);
                        }
                    }
                }

                boolean allValid = true;
                for (Player player : gameModel.getPlayersPosition()) {
                    try {
                        player.getShipBoard().validateShipBoard();
                    } catch (MultipleValidationErrorsException e) {
                        allValid = false;
                        this.update(new ShowShipErrorsMessage(player.getId(), e.getErrorMessages()));
                        player.setHasFinishedBuilding(false);
                    }
                }

                if (allValid) {
                    if(meteorSwarmCard.getCounter() == meteorSwarmCard.getMeteor().size()) {
                        gameModel.setGameState(GameState.PLAYING);

                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.ROLL_DICE);

                        for(Player player : gameModel.getPlayersPosition()) {
                            this.update(new SetCardInUseMessage(player.getId(), card));
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));

                            if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                            } else {
                                this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                            }
                        }
                    }
                }
                else {
                    gameModel.setGameState(GameState.REPAIRING);

                    for (Player player : gameModel.getPlayersPosition()) {
                        if (player.getShipBoard().getCorrectShip()) {
                            this.update(new ChangeTuiStateMessage(player.getId(),3));
                        }
                        else {
                            this.update(new ChangeTuiStateMessage(player.getId(),2));
                        }
                    }

                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                    for(Player player1 : gameModel.getPlayers()) {
                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                    }

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                    }

                    for (Player player : gameModel.getPlayersPosition()) {
                        if (player.getShipBoard().getCorrectShip()) {
                            player.setHasFinishedBuilding(true);
                            player.setPlayerState(WAIT);
                            this.update(new GenericMessage2(player.getId(), "\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players..."));
                        } else {
                            player.setPlayerState(REPAIR);
                        }
                    }
                }

                for (Player player : gameModel.getPlayersPosition()) {
                    player.setProceed(false);
                }
            }
            else {
                for(Player player : gameModel.getPlayersPosition()) {
                    if(player.getId().equals(clientId)) {
                        if(player.getShipBoard().isHit()) {
                            this.update(new DrawnCardMessage(clientId, card, false));
                        }
                    }
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: METEOR SWARM CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manages the choice of the player for the open space card
     * @param clientId is the client id
     * @param card is the card drawn
     **/

    public void handleOpenSpaceChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() == GameState.OPEN_SPACE) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                int engineStrenght = gameModel.getPlayerInTurn().getShipBoard().calculateEngineStrength();

                if(gameModel.getFlightType() == FlightType.STANDARD_FLIGHT) {
                    if(engineStrenght == 0) {
                        gameModel.getPlayerInTurn().setToBeRetiredFlag(true);
                    }
                }
                gameModel.getFlightBoard().movePlayerForward(gameModel.getPlayerInTurn(), engineStrenght); // move the player on the flightboard

                for(Player player : gameModel.getPlayers()) {
                    this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                }

                if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                    gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                    for(Player player : gameModel.getPlayersPosition()) {
                        if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                            this.update(new SetCardInUseMessage(player.getId(), card));
                            this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                        }
                        else {
                            this.update(new SetCardInUseMessage(player.getId(), card));
                            this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                        }
                    }
                }
                else {
                    for (Player player : gameModel.getPlayers()) {
                        player.getShipBoard().restoreEngines();
                    }

                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                    for(Player player1 : gameModel.getPlayers()) {
                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                    }

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                        if(otherPlayer.getId().equals(clientId)) {
                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                        }
                    }

                    for(Player player : gameModel.getPlayers()) {
                        this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                    }

                    for (Player player : gameModel.getPlayers()) {
                        player.setPlayerState(PlayerState.PLAY);
                    }
                    gameModel.setGameState(GameState.PLAYING);
                    for (Player player : gameModel.getPlayers()) {
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                        }
                    }
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: OPEN SPACE CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows to pick tile from the ship when possible
     * @param clientId is the client id
     **/

    public void handlePickTileFromShipMessage(String clientId, int row, int col) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PICK UP TILE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if(row != 5) {
            this.update(new GenericErrorMessage(clientId, "Incorrect space"));
            return;
        }
        if(col < 9 || col > 10) {
            this.update(new GenericErrorMessage(clientId, "Incorrect space"));
            return;
        }

        for(Player player : gameModel.getPlayers()) {
            if(player.getId().equals(clientId)) {
                ComponentTile componentTile = player.getShipBoard().pickUpObjectFrom(row-5, col-4);
                if(componentTile == null) {
                    this.update(new GenericErrorMessage(clientId, "The space is empty"));
                    return;
                }

                if (player.getTileInHand() != null) {
                    this.update(new GenericErrorMessage(clientId, "You have already a tile in hand"));
                    return;
                }

                player.addTileInHand(componentTile);
                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                for(Player player1 : gameModel.getPlayers()) {
                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                }

                for(Player otherPlayer : gameModel.getPlayers()) {
                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                    if(otherPlayer.getId().equals(clientId)) {
                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                    }
                }
                this.update(new PickedTileMessage(clientId, componentTile));
            }
        }
    }
    /**
     * This method allows to pick up card from the deck
     * @param clientId is the client id
     * @param listener the sender is the client
     **/

    /**
     * This method allows to pick up card from the deck
     * @param clientId is the client id
     * @param listener the sender is the client
     **/
    public void handlePickUpCardMessage(String clientId, GameEventListener listener) {
        if (gameModel.getGameState() == GameState.PLAYING) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {

                if(gameModel.getCardsToPlay().size() > 0) {
                    Card tempCard = gameModel.getCardsToPlay().get(0);
                    gameModel.removeCardFromPlay();

                    while(tempCard.getCardType() == CardName.COMBAT_ZONE && gameModel.getPlayersPosition().size() == 1) {
                        tempCard = gameModel.getCardsToPlay().get(0);
                        gameModel.removeCardFromPlay();
                    }
                    tempCard.onPickUp(gameModel, this::update);
                }
                else {
                    this.update(new GenericMessage("There are no more cards. The game is finished"));
                    listener.finishGame();
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: PICK_UP_CARD is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows to pick up card pile from the deck
     * @param clientId is the client id
     * @param numCardPile is the number of card pile
     **/

    public void handlePickUpCardPileMessage(String clientId, int numCardPile) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PICK UP CARD PILE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if (numCardPile < 1 || numCardPile > 3) {
            this.update(new GenericErrorMessage(clientId, "Wrong pile number"));
            return;
        }

        CardPile cardPile = gameModel.pickCardPile(numCardPile);
        if (cardPile == null) {
            this.update(new GenericErrorMessage(clientId, "The pile with ID  " + numCardPile + " is already occupied"));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                if (player.getCardPileInHand() != null) {
                    this.update(new GenericErrorMessage(clientId, "You have already a card pile in hand"));
                    return;
                }
                player.addCardPileInHand(cardPile);
            }
        }
        this.update(new PickedCardPileMessage(clientId, cardPile));
    }

    /**
     * This method allows to pick up card tile from the deck
     * @param clientId is the client id
     * @param tileId is the id of the tile
     **/

    public void handlePickUpTileMessage(String clientId, int tileId) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PICK UP TILE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        if (tileId < 0 || tileId >= gameModel.getNumTiles()) {
            this.update(new GenericErrorMessage(clientId, "INCORRECT ID"));
            return;
        }

        ComponentTile componentTile = gameModel.pickTile(tileId);
        if (componentTile == null) {
            for (Player player : gameModel.getPlayers()) {
                if (player.getId().equals(clientId)) {
                    this.update(new GenericErrorMessage(clientId, "The tile with ID  " + tileId + " is already occupied"));
                    return;
                }
            }
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                if (player.getTileInHand() != null) {
                    this.update(new GenericErrorMessage(clientId, "You have already a tile in hand"));
                    return;
                }
                player.addTileInHand(componentTile);
            }
        }
        this.update(new PickedTileMessage(clientId, componentTile));
    }

    /**
     * This method manages the choice of the player for the pirates card
     * @param clientId is the client id
     * @param card is the card drawn
     **/

    public void handlePiratesChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() == GameState.PIRATES) {
            piratesFight = false;
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                Pirates pirates = (Pirates) card;
                double fireStrength = gameModel.getPlayerInTurn().getShipBoard().calculateFireStrength();

                if(fireStrength > pirates.getEnemyStrength()) {
                    gameModel.getFlightBoard().movePlayerBackward(gameModel.getPlayerInTurn(), pirates.getLoseFlightDays()); // move the player on the flightboard
                    gameModel.getPlayerInTurn().incrementCosmicCredit(pirates.getNumOfCreditsTaken());

                    for(Player player : gameModel.getPlayers()) {
                        player.getShipBoard().restoreCannons();
                    }

                    for(Player player : gameModel.getPlayers()) {
                        this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                        }
                    }

                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                    for(Player player1 : gameModel.getPlayers()) {
                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                    }

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                        if(otherPlayer.getId().equals(clientId)) {
                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                        }
                    }

                    if(gameModel.getDefeatedPlayers().size() > 0) {
                        gameModel.setGameState(GameState.ROLL_DICE);
                        gameModel.setPlayerInTurn(gameModel.getDefeatedPlayers().get(0));

                        for (Player player : gameModel.getPlayersPosition()) {
                            if (gameModel.getDefeatedPlayers().contains(player)) {

                                if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    this.update(new SetCardInUseMessage(player.getId(), card));
                                    this.update(new AskRollDiceMessage(player.getId(), card, true, false));
                                } else {
                                    this.update(new SetCardInUseMessage(player.getId(), card));
                                    this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                                }
                            } else {
                                player.setPlayerState(PlayerState.WAIT);
                                this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                this.update(new DrawnCardMessage(player.getId(), card, false));
                            }
                        }
                    }
                    else {
                        for(Player player : gameModel.getPlayers()) {
                            player.setPlayerState(PlayerState.PLAY);
                        }
                        gameModel.setGameState(GameState.PLAYING);
                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                    }
                    gameModel.refreshPlayersPosition();
                }
                else if(fireStrength == pirates.getEnemyStrength()) {
                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        for (Player player : gameModel.getPlayers()) {
                            player.getShipBoard().restoreCannons();
                        }

                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                            }
                        }

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if(otherPlayer.getId().equals(clientId)) {
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        if(gameModel.getDefeatedPlayers().size() > 0) {
                            gameModel.setGameState(GameState.ROLL_DICE);
                            gameModel.setPlayerInTurn(gameModel.getDefeatedPlayers().get(0));

                            for (Player player : gameModel.getPlayersPosition()) {
                                if (gameModel.getDefeatedPlayers().contains(player)) {
                                    if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                        this.update(new SetCardInUseMessage(player.getId(), card));
                                        this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                                    } else {
                                        this.update(new SetCardInUseMessage(player.getId(), card));
                                        this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                                    }

                                } else {
                                    player.setPlayerState(PlayerState.WAIT);
                                    this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                }
                            }
                        }
                        else {
                            for(Player player : gameModel.getPlayers()) {
                                player.setPlayerState(PlayerState.PLAY);
                            }
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                        gameModel.refreshPlayersPosition();
                    }
                }
                else {
                    gameModel.addDefeatedPlayers(gameModel.getPlayerInTurn());

                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        for (Player player : gameModel.getPlayers()) {
                            player.getShipBoard().restoreCannons();
                        }

                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                            }
                        }

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if(otherPlayer.getId().equals(clientId)) {
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        if(gameModel.getDefeatedPlayers().size() > 0) {
                            gameModel.setGameState(GameState.ROLL_DICE);
                            gameModel.setPlayerInTurn(gameModel.getDefeatedPlayers().get(0));

                            for (Player player : gameModel.getPlayersPosition()) {
                                if (gameModel.getDefeatedPlayers().contains(player)) {
                                    if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                        this.update(new SetCardInUseMessage(player.getId(), card));
                                        this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                                    } else {
                                        this.update(new SetCardInUseMessage(player.getId(), card));
                                        this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                                    }

                                } else {
                                    player.setPlayerState(PlayerState.WAIT);
                                    this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                }
                            }
                        }
                        gameModel.refreshPlayersPosition();
                    }
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: PRATES CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manages the choice of the player for the planet card
     * @param clientId is the id of the client
     * @param card is the card drawn
     * @param choice is the choice of the player
     * @param numPlanet is the number of planets in the card
     */

    public void handlePlanetChoiceMessage(String clientId, Card card, String choice, int numPlanet) {
        if (gameModel.getGameState() == GameState.PLANETS) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                Planets planetCard = (Planets) card;
                if(choice.toLowerCase().equals("land")) {
                    boolean isGood = planetCard.processPlanetChoice(gameModel.getPlayerInTurn(), numPlanet);

                    if(isGood) {
                        if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0))) && (planetCard.getNumOccupiedPlanets() < planetCard.getNumberOfPlanets())) {
                            gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                            for(Player player : gameModel.getPlayersPosition()) {
                                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    this.update(new SetCardInUseMessage(player.getId(), card));
                                    this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                                }
                                else {
                                    this.update(new SetCardInUseMessage(player.getId(), card));
                                    this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                                }
                            }
                        }
                        else {
                            ArrayList<Player> reversePlayers = gameModel.getPlayersPosition();
                            Collections.reverse(reversePlayers);

                            for(Player player : gameModel.getPlayersPosition()) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // true if the player is in turn
                            }

                            boolean proceed = false;
                            for(Player player : reversePlayers) {
                                int numPlanet1 = planetCard.numOfPlanet(player.getNickname());
                                if(numPlanet1 != 0) {
                                    proceed = true;
                                    player.setPlayerState(PlayerState.GAIN_GOOD);
                                    ArrayList<Color> temp = planetCard.choosePlanetGoods(numPlanet1);
                                    for(Color goodsColor : temp) {
                                        player.insertGoodsBlock(goodsColor);
                                    }
                                    gameModel.getFlightBoard().movePlayerBackward(player, planetCard.getLoseFlightDays()); // move the player on the flightboard
                                }
                                else {
                                    player.setPlayerState(PlayerState.WAIT);
                                }
                            }

                            for(Player player : gameModel.getPlayers()) {
                                this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                            }

                            gameModel.refreshPlayersPosition();
                            gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

                            if(proceed) { // if there are players that have landed on a planet, they need to gain goods
                                gameModel.setGameState(GameState.GAIN_GOODS);


                                for(Player player : gameModel.getPlayersPosition()) {
                                    if(player.getPlayerState() == PlayerState.WAIT) {
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                    }
                                    else if (player.getPlayerState() == PlayerState.GAIN_GOOD) {
                                        this.update(new ChangeTuiStateMessage(player.getId(),1));
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                        this.update(new GainedGoodsMessage(player.getId(), card, player.getTempGoodsBlock()));
                                    }
                                }
                            }
                            else {
                                for(Player player : gameModel.getPlayers()) {
                                    player.setPlayerState(PlayerState.PLAY);
                                }
                                gameModel.setGameState(GameState.PLAYING);

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            }
                        }
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "This planet choice is not correct"));
                    }
                }
                else if (choice.toLowerCase().equals("skip")) {
                    if (!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    } else {
                        ArrayList<Player> reversePlayers = gameModel.getPlayersPosition();
                        Collections.reverse(reversePlayers);

                        boolean proceed = false;
                        for(Player player : reversePlayers) {
                            int numPlanet1 = planetCard.numOfPlanet(player.getNickname());
                            if(numPlanet1 != 0) {
                                proceed = true;
                                player.setPlayerState(PlayerState.GAIN_GOOD);
                                ArrayList<Color> temp = planetCard.choosePlanetGoods(numPlanet1);
                                for(Color goodsColor : temp) {
                                    player.insertGoodsBlock(goodsColor);
                                }
                                gameModel.getFlightBoard().movePlayerBackward(player, planetCard.getLoseFlightDays()); // move the player on the flightboard
                            }
                            else {
                                player.setPlayerState(PlayerState.WAIT);
                            }
                        }

                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                        }

                        gameModel.refreshPlayersPosition();
                        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

                        if(proceed) { // if there are players that have landed on a planet, they need to gain goods
                            gameModel.setGameState(GameState.GAIN_GOODS);

                            for(Player player : gameModel.getPlayersPosition()) {
                                if(player.getPlayerState() == PlayerState.WAIT) {
                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                    this.update(new WaitMessage(player.getId(), card));
                                }
                                else if (player.getPlayerState() == PlayerState.GAIN_GOOD) {
                                    this.update(new DrawnCardMessage(player.getId(), card, true));
                                    this.update(new GainedGoodsMessage(player.getId(), card, player.getTempGoodsBlock()));
                                }
                            }
                        }
                        else {
                            for(Player player : gameModel.getPlayers()) {
                                player.setPlayerState(PlayerState.PLAY);
                            }
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if (!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice is not correct"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: PLANET CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows to put the brown alien in the cabin on the ship board
     * @param clientId is the client id
     **/

    public void handlePutBrownInShipMessage(String clientId, int row, int col) {
        if (gameModel.getGameState() != GameState.POPULATE) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT BROWN IN SHIP is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }
        if (gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT BROWN IN SHIP is not available for this flight type: " + gameModel.getFlightType()));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                ShipBoard shipBoard = player.getShipBoard();

                if(row < 5 || row > 9) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }
                if(col < 4 || col > 10) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }

                ShipBoardSpace space = shipBoard.getSpace(row-5, col-4);
                ComponentTile componentTile = space.getComponent();

                if(componentTile != null && (componentTile.getName() == TileName.CABINE || componentTile.getName() == TileName.STARTING_CABINE)) {
                    if(componentTile.getName() == TileName.CABINE) {
                        Cabine cabine = (Cabine) componentTile;
                        if (cabine.getNumFigures() > 0) {
                            this.update(new GenericErrorMessage(clientId, "You cannot put astronauts and aliens in the same cabine!"));
                        }
                        else {
                            if (cabine.isConnectedWithAlienCabine()) {
                                boolean found = cabine.getAlienCabineConnected().stream().anyMatch(c -> c.getColor()==Color.YELLOW);
                                if(found) {
                                    if (cabine.getHasPurpleAlien() || cabine.getHasBrownAlien()) {
                                        this.update(new GenericErrorMessage(clientId, "You cannot insert more crew in the cabin because there is already an alien in it!"));
                                    }
                                    else if(shipBoard.getHasBrownAlien()) {
                                        this.update(new GenericErrorMessage(clientId, "You ship already has a brown alien!"));
                                    }
                                    else{
                                        shipBoard.setHasBrownAlien(true);
                                        cabine.setHasBrownAlien(true);

                                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                        for(Player player1 : gameModel.getPlayers()) {
                                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                        }

                                        for(Player otherPlayer : gameModel.getPlayers()) {
                                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        }

                                        for (Player otherPlayer : gameModel.getPlayers()) {
                                            if (otherPlayer.getId().equals(clientId)) {
                                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                            }
                                        }
                                    }
                                }
                                else {
                                    this.update(new GenericErrorMessage(clientId, "You cannot insert a brown alien because your cabin isn't connected to a brown support system!"));
                                }
                            } else {
                                this.update(new GenericErrorMessage(clientId, "You cannot insert an alien in this cabin because it isn't linked to a support system!"));
                            }
                        }

                    }
                    else if(componentTile.getName() == TileName.STARTING_CABINE) {
                        this.update(new GenericErrorMessage(clientId, "You cannot put aliens in the starting cabin!"));
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "The component that you have selected is not a cabin!"));
                    return;
                }
            }
        }
    }

    /**
     * This method allows to put back the card pile
     * @param clientId is the client id
     **/

    public void handlePutCardPileBackMessage(String clientId) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT CARD PILE BACK is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                CardPile cardPileInHand = player.getCardPileInHand();
                if(cardPileInHand == null) {
                    this.update(new GenericErrorMessage(clientId, "You can't put a card pile back because you have no card pile in hand"));
                    return;
                }

                boolean value = gameModel.putCardPileBack(cardPileInHand);
                if(value) {
                    player.removeCardPileInHand();
                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ShowTilesDeckMessage(otherPlayer.getId(), gameModel.getGameTile()));
                        if(otherPlayer.getId().equals(clientId)) {
                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "Can't put the tile back"));
                }
            }
        }
    }

    /**
     * This method allows to put the component on the ship
     * @param clientId is the client id
     **/

    public void handlePutFigureInShipMessage(String clientId, int row, int col) {
        if (gameModel.getGameState() != GameState.POPULATE) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT FIGURE IN SHIP is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                ShipBoard shipBoard = player.getShipBoard();

                if(row < 5 || row > 9) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }
                if(col < 4 || col > 10) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }

                ShipBoardSpace space = shipBoard.getSpace(row-5, col-4);
                ComponentTile componentTile = space.getComponent();

                if(componentTile != null && (componentTile.getName() == TileName.CABINE || componentTile.getName() == TileName.STARTING_CABINE)) {
                    if(componentTile.getName() == TileName.CABINE) {
                        if (gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
                            Cabine cabine = (Cabine) componentTile;
                            if (cabine.getNumFigures() == 2) {
                                this.update(new GenericErrorMessage(clientId, "You cannot insert more than two astronauts in a cabine!"));
                            }
                            else {
                                cabine.setNumFigures(cabine.getNumFigures() + 1);

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for(Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for(Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                }

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    if (otherPlayer.getId().equals(clientId)) {
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }
                            }
                        }
                        else {
                            Cabine cabine = (Cabine) componentTile;
                            if (cabine.getNumFigures() == 2) {
                                this.update(new GenericErrorMessage(clientId, "You cannot insert more than two astronauts in a cabine!"));
                            }
                            else {
                                if (cabine.getHasBrownAlien() || cabine.getHasPurpleAlien()) {
                                    if (cabine.getHasPurpleAlien() || cabine.getHasBrownAlien()) {
                                        this.update(new GenericErrorMessage(clientId, "You cannot insert more crew in the cabine because there is already an alien in it!"));
                                    }
                                }
                                else {
                                    cabine.setNumFigures(cabine.getNumFigures() + 1);
                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for(Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for(Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(componentTile.getName() == TileName.STARTING_CABINE) {
                        if (gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
                            StartingCabine cabine = (StartingCabine) componentTile;
                            if (cabine.getNumFigures() == 2) {
                                this.update(new GenericErrorMessage(clientId, "You cannot insert more than two astronauts in a cabine!"));
                            }
                            else {
                                cabine.setNumFigures(cabine.getNumFigures() + 1);

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for(Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for(Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                }

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    if (otherPlayer.getId().equals(clientId)) {
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }
                            }
                        }
                        else {
                            StartingCabine cabine = (StartingCabine) componentTile;
                            if (cabine.getNumFigures() == 2) {
                                this.update(new GenericErrorMessage(clientId, "You cannot insert more than two astronauts in a cabine!"));
                            }
                            else {
                                cabine.setNumFigures(cabine.getNumFigures() + 1);
                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for(Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for(Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                }

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    if (otherPlayer.getId().equals(clientId)) {
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    this.update(new GenericErrorMessage(clientId, "The component that you have selected is not a cabine!"));
                    return;
                }
            }
        }
    }

    /**
     * This method allows to put the purple alien in the cabin on the ship board
     * @param clientId is the client id
     **/

    public void handlePutPurpleInShipMessage(String clientId, int row, int col) {
        if (gameModel.getGameState() != GameState.POPULATE) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT PURPLE IN SHIP is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }
        if (gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT PURPLE IN SHIP is not available for this flight type: " + gameModel.getFlightType()));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                ShipBoard shipBoard = player.getShipBoard();

                if(row < 5 || row > 9) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }
                if(col < 4 || col > 10) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }

                ShipBoardSpace space = shipBoard.getSpace(row-5, col-4);
                ComponentTile componentTile = space.getComponent();

                if(componentTile != null && (componentTile.getName() == TileName.CABINE || componentTile.getName() == TileName.STARTING_CABINE)) {
                    if(componentTile.getName() == TileName.CABINE) {
                        Cabine cabine = (Cabine) componentTile;
                        if (cabine.getNumFigures() > 0) {
                            this.update(new GenericErrorMessage(clientId, "You cannot put astronauts and aliens in the same cabine!"));
                        }
                        else {
                            if (cabine.isConnectedWithAlienCabine()) {
                                boolean found = cabine.getAlienCabineConnected().stream().anyMatch(c -> c.getColor()==Color.PURPLE);
                                if(found) {
                                    if (cabine.getHasPurpleAlien() || cabine.getHasBrownAlien()) {
                                        this.update(new GenericErrorMessage(clientId, "You cannot insert more crew in the cabin because there is already an alien in it!"));
                                    }
                                    else if(shipBoard.getHasPurpleAlien()) {
                                        this.update(new GenericErrorMessage(clientId, "You ship already has a purple alien!"));
                                    }
                                    else{
                                        shipBoard.setHasPurpleAlien(true);
                                        cabine.setHasPurpleAlien(true);

                                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                        for(Player player1 : gameModel.getPlayers()) {
                                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                        }

                                        for(Player otherPlayer : gameModel.getPlayers()) {
                                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        }

                                        for (Player otherPlayer : gameModel.getPlayers()) {
                                            if (otherPlayer.getId().equals(clientId)) {
                                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                            }
                                        }
                                    }
                                }
                                else {
                                    this.update(new GenericErrorMessage(clientId, "You cannot insert a purple alien because your cabin isn't connected to a purple support system!"));
                                }
                            } else {
                                this.update(new GenericErrorMessage(clientId, "You cannot insert an alien in this cabin because it isn't linked to a support system!"));
                            }
                        }

                    }
                    else if(componentTile.getName() == TileName.STARTING_CABINE) {
                        this.update(new GenericErrorMessage(clientId, "You cannot put aliens in the starting cabin!"));
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "The component that you have selected is not a cabin!"));
                    return;
                }
            }
        }
    }

    /**
     * This method allows to put back the tile
     * @param clientId is the client id
     **/

    public void handlePutTileBackMessage(String clientId) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT TILE BACK is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                ComponentTile tileInHand = player.getTileInHand();
                if(tileInHand == null) {
                    this.update(new GenericErrorMessage(clientId, "You can't put a tile back because you have no tile in hand"));
                    return;
                }

                ShipBoard shipBoard = player.getShipBoard();
                if((shipBoard.getSpace(0,5).getComponent() != null && shipBoard.getSpace(0,5).getComponent().equals(tileInHand))
                        || (shipBoard.getSpace(0,6).getComponent() != null && shipBoard.getSpace(0,6).getComponent().equals(tileInHand))) {
                    this.update(new GenericErrorMessage(clientId, "You can't put the tile back because it is reserved for this ship"));
                    return;
                }

                boolean value = gameModel.putTileBack(tileInHand);
                if(value) {
                    tileInHand.setFaceUp();
                    player.removeTileInHand();

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ShowTilesDeckMessage(otherPlayer.getId(), gameModel.getGameTile()));
                        if(otherPlayer.getId().equals(clientId)) {
                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                        }
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "Can't put the tile back"));
                }
            }
        }
    }

    /**
     * This method allows to put tile on the ship board
     * @param clientId is the client id
     **/

    /**
     * This method allows to put tile on the ship board
     * @param clientId is the client id
     **/

    public void handlePutTileInShipMessage(String clientId, int row, int col) {
        if (gameModel.getGameState() != GameState.BUILDING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PUT TILE IN SHIP is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        for (Player player : gameModel.getPlayers()) {
            if (player.getId().equals(clientId)) {
                ComponentTile tileInHand = player.getTileInHand();
                if(tileInHand == null) {
                    this.update(new GenericErrorMessage(clientId, "You can't put a tile back because you have no tile in hand"));
                    return;
                }

                ShipBoard shipBoard = player.getShipBoard();
                if(row < 5 || row > 9) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }
                if(col < 4 || col > 10) {
                    this.update(new GenericErrorMessage(clientId, "The coordinates ("+row+","+col+") are not valid"));
                    return;
                }

                ComponentTile componentTile = shipBoard.getSpace(row-5,col-4).getComponent();
                if(componentTile != null) {
                    this.update(new GenericErrorMessage(clientId, "The space ("+row+","+col+") already contains a component"));
                    return;
                }

                int tileId = tileInHand.getId();
                shipBoard.getSpace(row-5,col-4).insertComponent(tileInHand);
                gameModel.getGameTile().removeComponentTile(tileId);
                player.removeTileInHand();

                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                for(Player player1 : gameModel.getPlayers()) {
                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                }

                for(Player otherPlayer : gameModel.getPlayers()) {
                    this.update(new ShowTilesDeckMessage(otherPlayer.getId(), gameModel.getGameTile()));
                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                    if(otherPlayer.getId().equals(clientId)) {
                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                    }
                }
            }
        }
    }

    /**
     * This method allows to remove battery from the ship board
     * @param clientId is the client id
     * @param card is the card drawn
     * @param sum is the sum of the battery
     **/

    public void handleRemoveBatteryMessage(String clientId, Card card, int row, int col, int sum) {
        if(gameModel.getGameState() == GameState.SMUGGLERS) {
            if (card.getCardType() == CardName.SMUGGLERS) {

                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for(Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for(Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if(otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "Card not correct for this game state"));
            }
        }

        if(gameModel.getGameState() == GameState.SLAVERS) {
            if (card.getCardType() == CardName.SLAVERS) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for(Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for(Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if(otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "Card not correct for this game state"));
            }
        }

        if(gameModel.getGameState() == GameState.PIRATES) {
            if (card.getCardType() == CardName.PIRATES) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for(Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for(Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if(otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));

                                    if(!piratesFight) {
                                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, true)); // true if the player is in turn
                                    }
                                    else {
                                        this.update(new MeteorHitMessage(clientId, card, otherPlayer.getShipBoard().isHit(), sum));
                                    }
                                }
                            }
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "Card not correct for this game state"));
            }
        }

        if(gameModel.getGameState() == GameState.OPEN_SPACE) {
            if (card.getCardType() == CardName.OPEN_SPACE) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for(Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for(Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if(otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "Card not correct for this game state"));
            }
        }

        if(gameModel.getGameState() == GameState.METEOR_SWARM) {
            if (card.getCardType() == CardName.METEOR_SWARM) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for(Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for(Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if(otherPlayer.getId().equals(clientId)) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    this.update(new MeteorHitMessage(clientId, card, otherPlayer.getShipBoard().isHit(), sum));
                                }
                            }
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "Card not correct for this game state"));
            }
        }

        if(gameModel.getGameState() != GameState.REMOVE_GOODS) {
            if (card.getCardType() == CardName.COMBAT_ZONE) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        CombatZone combatZoneCard = (CombatZone) card;
                        if (combatZoneCard.getFaseCounter() == 1 && combatZoneCard.getLevel() == 2) {
                            if (battery.getNumBatteriesInUse() > 0) {
                                battery.decreaseNumBatteriesInUse();

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for (Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    if (otherPlayer.getId().equals(clientId)) {
                                        otherPlayer.setPlayerState(PlayerState.PLAY);
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }
                                this.update(new DrawnCardMessage(clientId, card, true));
                                this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                            } else {
                                this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                            }
                        } else if (combatZoneCard.getFaseCounter() == 2) {
                            if (battery.getNumBatteriesInUse() > 0) {
                                battery.decreaseNumBatteriesInUse();

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for (Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    if (otherPlayer.getId().equals(clientId)) {
                                        otherPlayer.setPlayerState(PlayerState.PLAY);
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }
                                this.update(new DrawnCardMessage(clientId, card, true));
                                this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                            }
                            else {
                                this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                            }
                        }
                        else if (combatZoneCard.getFaseCounter() == 3) {
                            if (battery.getNumBatteriesInUse() > 0) {
                                battery.decreaseNumBatteriesInUse();

                                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                for (Player player1 : gameModel.getPlayers()) {
                                    shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                }

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                    if (otherPlayer.getId().equals(clientId)) {
                                        otherPlayer.setPlayerState(PlayerState.PLAY);
                                        this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                    }
                                }
                                if(!combatZoneFight) {
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                }
                                else {
                                    this.update(new MeteorHitMessage(clientId, card, p.getShipBoard().isHit(), sum));
                                }
                                this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));
                            } else {
                                this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                            }
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }
        }

        if(gameModel.getGameState() == GameState.REMOVE_GOODS) {
            if (card.getCardType() == CardName.SMUGGLERS) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_BATTERY) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();
                            p.decrementPenaltyGoods();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    if(player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                                        player.setPenaltyGoods(0);
                                    }
                                }
                            }

                            boolean val = false;
                            for (Player otherPlayer : gameModel.getPlayers()) {
                                if (otherPlayer.getPenaltyGoods() > 0) {
                                    val = true;
                                }
                            }

                            if (val) {
                                gameModel.setGameState(GameState.REMOVE_GOODS);
                                for (Player player : gameModel.getPlayers()) {
                                    if(player.getId().equals(clientId)) {
                                        if (player.getPenaltyGoods() > 0) {
                                            if (player.getShipBoard().getNumBatteries() == 0) {
                                                player.setPenaltyGoods(0);
                                                player.setPlayerState(PlayerState.WAIT);
                                                this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                                this.update(new WaitMessage(player.getId(), card));
                                            } else {
                                                player.setPlayerState(PlayerState.REMOVE_BATTERY);
                                                this.update(new AskRemoveBatteryMessage(player.getId()));
                                            }
                                        } else {
                                            player.setPlayerState(PlayerState.WAIT);
                                            this.update(new ChangeTuiStateMessage(player.getId(), 1));
                                            this.update(new WaitMessage(player.getId(), card));
                                        }
                                    }
                                }
                            } else {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            }
                        }
                        else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                }
            }

            if (card.getCardType() == CardName.COMBAT_ZONE) {
                if(!(gameModel.getPlayerInTurn().getId().equals(clientId))) {
                    this.update(new GenericErrorMessage(clientId, "This choice can't be made by this player"));
                    return;
                }

                if ((gameModel.getPlayerInTurn() != null && gameModel.getPlayerInTurn().getPlayerState() == PlayerState.REMOVE_BATTERY)) {
                    if (gameModel.getPlayerInTurn().getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && gameModel.getPlayerInTurn().getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.BATTERY) {
                        Battery battery = (Battery) gameModel.getPlayerInTurn().getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (battery.getNumBatteriesInUse() > 0) {
                            battery.decreaseNumBatteriesInUse();
                            gameModel.getPlayerInTurn().decrementPenaltyGoods();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }

                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if((gameModel.getPlayerInTurn().getShipBoard().rarestGoodsBlock() == null && gameModel.getPlayerInTurn().getShipBoard().getNumBatteries() == 0) || gameModel.getPlayerInTurn().getPenaltyGoods() == 0) {
                                gameModel.getPlayerInTurn().setPenaltyGoods(0);
                                gameModel.setGameState(GameState.FASE_3);
                                gameModel.getPlayerInTurn().setPlayerState(PlayerState.PLAY);

                                for (Player otherPlayer : gameModel.getPlayersPosition()) {
                                    this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                    this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                }
                                return;
                            }

                            for (Player otherPlayer : gameModel.getPlayersPosition()) {
                                if (gameModel.getPlayerInTurn().getId().equals(otherPlayer.getId())) {
                                    this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                    this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                    this.update(new AskRemoveBatteryMessage(otherPlayer.getId()));
                                    otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                } else {
                                    otherPlayer.setPlayerState(PlayerState.WAIT);
                                    this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                }
                                this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                            }
                        }
                        else {
                            this.update(new GenericErrorMessage(clientId, "The batteries in this tile are already finished"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a battery"));
                    }
                }
            }
        }
    }

    /**
     * This method allows to remove component from the ship
     * @param clientId is the client id
     * @param card is the card to remove
     **/

    public void handleRemoveFigureMessage(String clientId, Card card, int row, int col) {
        if (gameModel.getGameState() == GameState.REMOVE_FIGURES) {
            if (card.getCardType() == CardName.ABANDONED_SHIP) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_FIGURE) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CABINE) {
                        Cabine cabine = (Cabine) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        if (cabine.getNumFigures() > 0) {
                            p.decrementPenaltyEquipment();
                            cabine.decrementNumFigures();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if (p.getPenaltyEquipment() == 0) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player player : gameModel.getPlayers()) {
                                    player.setPlayerState(PlayerState.PLAY);
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            }
                            else {
                                this.update(new AskRemoveFigureMessage(clientId));
                            }
                        } else {
                            if(gameModel.getFlightType()==FlightType.STANDARD_FLIGHT) {
                                if(cabine.getHasPurpleAlien()) {
                                    p.decrementPenaltyEquipment();
                                    p.getShipBoard().setHasPurpleAlien(false);
                                    cabine.setHasPurpleAlien(false);

                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for (Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                    this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                                    if (p.getPenaltyEquipment() == 0) {
                                        gameModel.setGameState(GameState.PLAYING);
                                        for (Player player : gameModel.getPlayers()) {
                                            player.setPlayerState(PlayerState.PLAY);
                                        }

                                        for (Player player : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                            }
                                        }
                                    }
                                    else {
                                        this.update(new AskRemoveFigureMessage(clientId));
                                    }
                                }
                                else if(cabine.getHasBrownAlien()) {
                                    p.decrementPenaltyEquipment();
                                    p.getShipBoard().setHasBrownAlien(false);
                                    cabine.setHasBrownAlien(false);

                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for (Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                    this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                                    if (p.getPenaltyEquipment() == 0) {
                                        gameModel.setGameState(GameState.PLAYING);
                                        for (Player player : gameModel.getPlayers()) {
                                            player.setPlayerState(PlayerState.PLAY);
                                        }

                                        for (Player player : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                            }
                                        }
                                    } else {
                                        this.update(new AskRemoveFigureMessage(clientId));
                                    }
                                }
                                else{
                                    this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                                }
                            }
                            else {
                                this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                            }
                        }
                    }
                    else if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.STARTING_CABINE) {
                        StartingCabine startingCabine = (StartingCabine) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        if (startingCabine.getNumFigures() > 0) {
                            p.decrementPenaltyEquipment();
                            startingCabine.decrementNumFigures();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if (p.getPenaltyEquipment() == 0) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player player : gameModel.getPlayers()) {
                                    player.setPlayerState(PlayerState.PLAY);
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            } else {
                                this.update(new AskRemoveFigureMessage(clientId));
                            }
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                        }
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a cabine"));
                    }

                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn"));
                }
            }

            if (card.getCardType() == CardName.SLAVERS) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_FIGURE) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CABINE) {
                        Cabine cabine = (Cabine) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        if (cabine.getNumFigures() > 0) {
                            p.decrementPenaltyEquipment();
                            cabine.decrementNumFigures();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if(p.getPenaltyEquipment() == 0 || (p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasPurpleAlien() && !p.getShipBoard().getHasBrownAlien())) {
                                p.setPenaltyEquipment(0);
                                p.setPlayerState(PlayerState.WAIT);
                                this.update(new ChangeTuiStateMessage(p.getId(),1));
                                this.update(new DrawnCardMessage(p.getId(), card, false));

                                boolean removeFigures = false;
                                for(Player player : gameModel.getPlayersPosition()) {
                                    if(player.getPenaltyEquipment() > 0) {
                                        removeFigures = true;
                                    }
                                }
                                if(!removeFigures) {
                                    gameModel.setGameState(GameState.PLAYING);
                                    for (Player player : gameModel.getPlayers()) {
                                        if(!gameModel.getRetiredPlayers().contains(player)) {
                                            this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                        }
                                    }
                                }
                            }
                            else {
                                p.setPlayerState(PlayerState.REMOVE_FIGURE);
                                this.update(new ChangeTuiStateMessage(p.getId(),1));
                                this.update(new DrawnCardMessage(p.getId(), card, false));
                                this.update(new AskRemoveFigureMessage(p.getId()));
                            }
                        } else {
                            if(gameModel.getFlightType()==FlightType.STANDARD_FLIGHT) {
                                if(cabine.getHasPurpleAlien()) {
                                    p.decrementPenaltyEquipment();
                                    p.getShipBoard().setHasPurpleAlien(false);
                                    cabine.setHasPurpleAlien(false);

                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for (Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                    this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                                    if(p.getPenaltyEquipment() == 0 || (p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasPurpleAlien() && !p.getShipBoard().getHasBrownAlien())) {
                                        p.setPenaltyEquipment(0);
                                        p.setPlayerState(PlayerState.WAIT);
                                        this.update(new ChangeTuiStateMessage(p.getId(),1));
                                        this.update(new DrawnCardMessage(p.getId(), card, false));

                                        boolean removeFigures = false;
                                        for(Player player : gameModel.getPlayersPosition()) {
                                            if(player.getPenaltyEquipment() > 0) {
                                                removeFigures = true;
                                            }
                                        }
                                        if(!removeFigures) {
                                            gameModel.setGameState(GameState.PLAYING);
                                            for (Player player : gameModel.getPlayers()) {
                                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        p.setPlayerState(PlayerState.REMOVE_FIGURE);
                                        this.update(new ChangeTuiStateMessage(p.getId(),1));
                                        this.update(new DrawnCardMessage(p.getId(), card, false));
                                        this.update(new AskRemoveFigureMessage(p.getId()));
                                    }
                                }
                                else if(cabine.getHasBrownAlien()) {
                                    p.decrementPenaltyEquipment();
                                    p.getShipBoard().setHasBrownAlien(false);
                                    cabine.setHasBrownAlien(false);

                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for (Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                    this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                                    if(p.getPenaltyEquipment() == 0 || (p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasPurpleAlien() && !p.getShipBoard().getHasBrownAlien())) {
                                        p.setPenaltyEquipment(0);
                                        p.setPlayerState(PlayerState.WAIT);
                                        this.update(new ChangeTuiStateMessage(p.getId(),1));
                                        this.update(new DrawnCardMessage(p.getId(), card, false));


                                        boolean removeFigures = false;
                                        for(Player player : gameModel.getPlayersPosition()) {
                                            if(player.getPenaltyEquipment() > 0) {
                                                removeFigures = true;
                                            }
                                        }
                                        if(!removeFigures) {
                                            gameModel.setGameState(GameState.PLAYING);
                                            for (Player player : gameModel.getPlayers()) {
                                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        p.setPlayerState(PlayerState.REMOVE_FIGURE);
                                        this.update(new ChangeTuiStateMessage(p.getId(),1));
                                        this.update(new DrawnCardMessage(p.getId(), card, false));
                                        this.update(new AskRemoveFigureMessage(p.getId()));
                                    }
                                }
                                else{
                                    this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                                }
                            }
                            else {
                                this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                            }
                        }

                    }
                    else if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.STARTING_CABINE) {
                        StartingCabine startingCabine = (StartingCabine) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        if (startingCabine.getNumFigures() > 0) {
                            p.decrementPenaltyEquipment();
                            startingCabine.decrementNumFigures();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if(p.getPenaltyEquipment() == 0 || (p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasPurpleAlien() && !p.getShipBoard().getHasBrownAlien())) {
                                p.setPenaltyEquipment(0);
                                p.setPlayerState(PlayerState.WAIT);
                                this.update(new ChangeTuiStateMessage(p.getId(),1));
                                this.update(new DrawnCardMessage(p.getId(), card, false));

                                boolean removeFigures = false;
                                for(Player player : gameModel.getPlayersPosition()) {
                                    if(player.getPenaltyEquipment() > 0) {
                                        removeFigures = true;
                                    }
                                }
                                if(!removeFigures) {
                                    gameModel.setGameState(GameState.PLAYING);
                                    for (Player player : gameModel.getPlayers()) {
                                        if(!gameModel.getRetiredPlayers().contains(player)) {
                                            this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                        }
                                    }
                                }
                            }
                            else {
                                p.setPlayerState(PlayerState.REMOVE_FIGURE);
                                this.update(new ChangeTuiStateMessage(p.getId(),1));
                                this.update(new DrawnCardMessage(p.getId(), card, false));
                                this.update(new AskRemoveFigureMessage(p.getId()));
                            }
                        }
                        else {
                            this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                        }
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a cabine"));
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn"));
                }
            }

            if (card.getCardType() == CardName.COMBAT_ZONE) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_FIGURE) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CABINE) {
                        Cabine cabine = (Cabine) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        if (cabine.getNumFigures() > 0) {
                            p.decrementPenaltyEquipment();
                            cabine.decrementNumFigures();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if ((p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasBrownAlien() && !p.getShipBoard().getHasPurpleAlien()) || p.getPenaltyEquipment() == 0) {
                                p.setPenaltyEquipment(0);
                                gameModel.setGameState(GameState.FASE_3);
                                p.setPlayerState(PlayerState.PLAY);

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                        this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                        this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                                return;
                            }

                            for(Player otherPlayer : gameModel.getPlayersPosition()) {
                                if(p.getId().equals(otherPlayer.getId())) {
                                    otherPlayer.setPlayerState(PlayerState.REMOVE_FIGURE);
                                    this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                    this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                    this.update(new AskRemoveFigureMessage(otherPlayer.getId()));
                                }
                                else {
                                    otherPlayer.setPlayerState(PlayerState.WAIT);
                                    this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                }
                                this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                            }
                        } else {
                            if(gameModel.getFlightType()==FlightType.STANDARD_FLIGHT) {
                                if(cabine.getHasPurpleAlien()) {
                                    p.decrementPenaltyEquipment();
                                    p.getShipBoard().setHasPurpleAlien(false);
                                    cabine.setHasPurpleAlien(false);

                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for (Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                    this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                                    if ((p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasBrownAlien() && !p.getShipBoard().getHasPurpleAlien()) || p.getPenaltyEquipment() == 0) {
                                        p.setPenaltyEquipment(0);
                                        gameModel.setGameState(GameState.FASE_3);
                                        p.setPlayerState(PlayerState.PLAY);

                                        for (Player otherPlayer : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                                this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                                this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                            }
                                        }
                                        return;
                                    }

                                    for(Player otherPlayer : gameModel.getPlayersPosition()) {
                                        if(p.getId().equals(otherPlayer.getId())) {
                                            otherPlayer.setPlayerState(PlayerState.REMOVE_FIGURE);
                                            this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                            this.update(new AskRemoveFigureMessage(otherPlayer.getId()));
                                        }
                                        else {
                                            otherPlayer.setPlayerState(PlayerState.WAIT);
                                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                        }
                                        this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                    }
                                }
                                else if(cabine.getHasBrownAlien()) {
                                    p.decrementPenaltyEquipment();
                                    p.getShipBoard().setHasBrownAlien(false);
                                    cabine.setHasBrownAlien(false);

                                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                                    for (Player player1 : gameModel.getPlayers()) {
                                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                                    }

                                    for (Player otherPlayer : gameModel.getPlayers()) {
                                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                        if (otherPlayer.getId().equals(clientId)) {
                                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                        }
                                    }
                                    this.update(new DrawnCardMessage(clientId, card, true));
                                    this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                                    if ((p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasBrownAlien() && !p.getShipBoard().getHasPurpleAlien()) || p.getPenaltyEquipment() == 0) {
                                        p.setPenaltyEquipment(0);
                                        gameModel.setGameState(GameState.FASE_3);
                                        p.setPlayerState(PlayerState.PLAY);

                                        for (Player otherPlayer : gameModel.getPlayers()) {
                                            if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                                this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                                this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                            }
                                        }
                                        return;
                                    }

                                    for(Player otherPlayer : gameModel.getPlayersPosition()) {
                                        if(p.getId().equals(otherPlayer.getId())) {
                                            otherPlayer.setPlayerState(PlayerState.REMOVE_FIGURE);
                                            this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                            this.update(new AskRemoveFigureMessage(otherPlayer.getId()));
                                        }
                                        else {
                                            otherPlayer.setPlayerState(PlayerState.WAIT);
                                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                        }
                                        this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                    }
                                }
                                else{
                                    this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                                }
                            }
                            else {
                                this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                            }
                        }

                    }
                    else if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.STARTING_CABINE) {
                        StartingCabine cabine = (StartingCabine) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();

                        if (cabine.getNumFigures() > 0) {
                            p.decrementPenaltyEquipment();
                            cabine.decrementNumFigures();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if (p.getShipBoard().getNumFigures() == 0 || (p.getShipBoard().getNumFigures() == 0 && !p.getShipBoard().getHasPurpleAlien() && !p.getShipBoard().getHasBrownAlien())) {
                                p.setPenaltyEquipment(0);
                                gameModel.setGameState(GameState.FASE_3);
                                p.setPlayerState(PlayerState.PLAY);

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                        this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                        this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                                return;
                            }

                            for(Player otherPlayer : gameModel.getPlayersPosition()) {
                                if(p.getId().equals(otherPlayer.getId())) {
                                    otherPlayer.setPlayerState(PlayerState.REMOVE_FIGURE);
                                    this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                    this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                    this.update(new AskRemoveFigureMessage(otherPlayer.getId()));
                                }
                                else {
                                    otherPlayer.setPlayerState(PlayerState.WAIT);
                                    this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                }
                                this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                            }

                        } else {
                            this.update(new GenericErrorMessage(clientId, "The cabine is empty"));
                        }
                    }
                    else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not a cabine"));
                    }

                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn"));
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: REMOVE FIGURES is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows to remove goods from the ship when the card drawn implements this functions
     * @param clientId is the client id
     * @param card is the card to remove
     **/

    public void handleRemoveGoodMessage(String clientId, Card card, int row, int col) {
        if (gameModel.getGameState() == GameState.REMOVE_GOODS) {
            if (card.getCardType() == CardName.SMUGGLERS) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_GOOD) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CARGO) {
                        Cargo cargo = (Cargo) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (cargo.rarestCargoIn() != null && cargo.rarestCargoIn() == p.getShipBoard().rarestGoodsBlock()) {
                            cargo.removeCargo(cargo.rarestCargoIn());
                            p.decrementPenaltyGoods();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    if(player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                                        player.setPenaltyGoods(0);
                                    }
                                }
                            }

                            boolean val = false;
                            for (Player otherPlayer : gameModel.getPlayers()) {
                                if (otherPlayer.getPenaltyGoods() > 0) {
                                    val = true;
                                }
                            }

                            if (val) {
                                gameModel.setGameState(GameState.REMOVE_GOODS);
                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        if (player.getPenaltyGoods() > 0) {
                                            if(player.getShipBoard().rarestGoodsBlock() == null) {
                                                if(player.getShipBoard().getNumBatteries() == 0) {
                                                    player.setPenaltyGoods(0);
                                                    player.setPlayerState(PlayerState.WAIT);
                                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                                }
                                                else {
                                                    player.setPlayerState(PlayerState.REMOVE_BATTERY);
                                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                                    this.update(new AskRemoveBatteryMessage(player.getId()));
                                                }
                                            }
                                            else {
                                                player.setPlayerState(PlayerState.REMOVE_GOOD);
                                                this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                this.update(new DrawnCardMessage(player.getId(), card, false));
                                                this.update(new AskRemoveGoodMessage(player.getId()));
                                            }
                                        } else {
                                            player.setPlayerState(PlayerState.WAIT);
                                            this.update(new DrawnCardMessage(player.getId(), card, false));
                                        }
                                    }
                                }
                            } else {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    otherPlayer.setPlayerState(PlayerState.PLAY);
                                }

                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            }
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The cargo in coordinates (" + row + "," + col + ") doesn't contain the rarest goods block in the ship"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not right"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn"));
                }
            }
            else if (card.getCardType() == CardName.COMBAT_ZONE) {
                Player p = null;
                for (Player player : gameModel.getPlayers()) {
                    if (player.getId().equals(clientId)) {
                        p = player;
                    }
                }

                if (p != null && p.getPlayerState() == PlayerState.REMOVE_GOOD) {
                    if (p.getShipBoard().getSpace(row - 5, col - 4).getComponent() != null && p.getShipBoard().getSpace(row - 5, col - 4).getComponent().getName() == TileName.CARGO) {
                        Cargo cargo = (Cargo) p.getShipBoard().getSpace(row - 5, col - 4).getComponent();
                        if (cargo.rarestCargoIn() != null && cargo.rarestCargoIn() == p.getShipBoard().rarestGoodsBlock()) {
                            cargo.removeCargo(cargo.rarestCargoIn());
                            p.decrementPenaltyGoods();

                            HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                            for (Player player1 : gameModel.getPlayers()) {
                                shipBoards.put(player1.getNickname(), player1.getShipBoard());
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                                if (otherPlayer.getId().equals(clientId)) {
                                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                                }
                            }
                            this.update(new DrawnCardMessage(clientId, card, true));
                            this.update(new UpdateParametresMessage(clientId, gameModel.getFlightBoard()));

                            if((p.getShipBoard().rarestGoodsBlock() == null && p.getShipBoard().getNumBatteries() == 0) || p.getPenaltyGoods() == 0) {
                                p.setPenaltyGoods(0);
                                gameModel.setGameState(GameState.FASE_3);
                                p.setPlayerState(PlayerState.PLAY);

                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                    if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                        this.update(new ProceedNextPhaseMessage(otherPlayer.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                                return;
                            }

                            gameModel.setGameState(GameState.REMOVE_GOODS);
                            if (p.getShipBoard().rarestGoodsBlock() == null) {
                                for (Player otherPlayer : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                        if (p.getId().equals(otherPlayer.getId())) {
                                            this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                            this.update(new AskRemoveBatteryMessage(otherPlayer.getId()));
                                            otherPlayer.setPlayerState(PlayerState.REMOVE_BATTERY);
                                        } else {
                                            otherPlayer.setPlayerState(PlayerState.WAIT);
                                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                        }
                                    }
                                    this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                                }
                                return;
                            }

                            for (Player otherPlayer : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                                    if (p.getId().equals(otherPlayer.getId())) {
                                        this.update(new ChangeTuiStateMessage(otherPlayer.getId(), 1));
                                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                        this.update(new AskRemoveGoodMessage(otherPlayer.getId()));
                                        otherPlayer.setPlayerState(PlayerState.REMOVE_GOOD);
                                    } else {
                                        otherPlayer.setPlayerState(PlayerState.WAIT);
                                        this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                                    }
                                }
                                this.update(new UpdateParametresMessage(otherPlayer.getId(), gameModel.getFlightBoard()));
                            }
                        } else {
                            this.update(new GenericErrorMessage(clientId, "The cargo in coordinates (" + row + "," + col + ") doesn't contain the rarest goods block in the ship"));
                        }
                    } else {
                        this.update(new GenericErrorMessage(clientId, "The tile in coordinates (" + row + "," + col + ") is not right"));
                    }
                } else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn"));
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: REMOVE GOODS is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows to remove tiles from the ship when players need to fix or repair their ship
     * @param clientId is the client id
     **/

    public void handleRemoveTileMessage(String clientId, int row, int col) {
        if (gameModel.getGameState() != GameState.FIXING && gameModel.getGameState() != GameState.REPAIRING) {
            this.update(new GenericErrorMessage(clientId, "This message type: PICK UP TILE is not available for this game state: " + gameModel.getGameState().toString()));
            return;
        }

        for(Player player : gameModel.getPlayers()) {
            if(player.getId().equals(clientId)) {
                if(player.getPlayerState() != PlayerState.FIX && player.getPlayerState() != PlayerState.REPAIR) {
                    this.update(new GenericErrorMessage(clientId, "Player not in turn"));
                    return;
                }

                if (row < 5 || row > 9) {
                    this.update(new GenericErrorMessage(clientId, "Wrong space"));
                    return;
                }

                if (col < 4 || col > 10) {
                    this.update(new GenericErrorMessage(clientId, "Wrong space"));
                    return;
                }

                ComponentTile componentTile = player.getShipBoard().getSpace(row-5,col-4).getComponent();
                if(componentTile == null) {
                    this.update(new GenericErrorMessage(clientId, "Cannot remove a component from an empty space"));
                    return;
                }

                player.getShipBoard().getSpace(row-5,col-4).insertComponent(null);
                player.getShipBoard().incrementNumLostTiles();
                this.update(new ShowShipBoardMessage(clientId, player.getShipBoard()));

                HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                for(Player otherPlayer : gameModel.getPlayers()) {
                    shipBoards.put(otherPlayer.getNickname(), otherPlayer.getShipBoard());
                }
                for(Player otherPlayer : gameModel.getPlayers()) {
                    this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                }
            }
        }
    }

    /**
     * This method checks whether the player's ship has been repaired correctly
     * @param clientId is the client id
     * @param card is the card drawn
     **/

    public void handleRepairingShipMessage(String clientId, Card card) {
        if(gameModel.getGameState() == GameState.REPAIRING) {
            Player p = null;
            for(Player player : gameModel.getPlayers()) {
                if(player.getId().equals(clientId)) {
                    p = player;
                }
            }

            if(p != null && p.getPlayerState() == WAIT) {
                this.update(new GenericErrorMessage(clientId, "You are not in turn"));
                return;
            }
            p.setHasFinishedBuilding(true);

            boolean proceed = true;
            for(Player player : gameModel.getPlayersPosition()) {
                if(!player.getHasFinishedBuilding()) {
                    proceed = false;
                }
            }

            if(proceed) {
                boolean allValid = true;
                for (Player player : gameModel.getPlayersPosition()) {
                    try {
                        player.getShipBoard().validateShipBoard();
                    } catch (MultipleValidationErrorsException e) {
                        allValid = false;
                        this.update(new ShowShipErrorsMessage(player.getId(), e.getErrorMessages()));
                        player.setHasFinishedBuilding(false);
                    }
                }

                if(card.getCardType() == CardName.METEOR_SWARM) {
                    MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;

                    if (allValid) {
                        if(meteorSwarmCard.getCounter() == meteorSwarmCard.getMeteor().size()) {
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new SetCardInUseMessage(player.getId(), card));
                                    this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                                    this.update(new DrawnCardMessage(player.getId(), card,true));
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                        else {
                            gameModel.setGameState(GameState.ROLL_DICE);
                            for(Player player : gameModel.getPlayersPosition()) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));

                                if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    this.update(new AskRollDiceMessage(player.getId(), card, true, true));
                                } else {
                                    this.update(new AskRollDiceMessage(player.getId(), card, false, true));
                                }
                            }
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.REPAIRING);

                        for (Player player : gameModel.getPlayersPosition()) {
                            if (player.getShipBoard().getCorrectShip()) {
                                this.update(new ChangeTuiStateMessage(player.getId(),3));
                            }
                            else {
                                this.update(new ChangeTuiStateMessage(player.getId(),2));
                            }
                        }

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                        }

                        for (Player player : gameModel.getPlayersPosition()) {
                            if (player.getShipBoard().getCorrectShip()) {
                                player.setHasFinishedBuilding(true);
                                player.setPlayerState(WAIT);
                                this.update(new GenericMessage2(player.getId(), "\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players..."));
                            } else {
                                player.setPlayerState(REPAIR);
                            }
                        }
                    }
                }

                if(card.getCardType() == CardName.COMBAT_ZONE) {
                    CombatZone combatZoneCard = (CombatZone) card;
                    ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];

                    if (allValid) {
                        if(combatZoneCard.getCounter() == meteors.size()) {
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayersPosition()) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                                this.update(new DrawnCardMessage(player.getId(), card,false));
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                        else {
                            gameModel.setGameState(GameState.ROLL_DICE);
                            this.update(new SetCardInUseMessage(gameModel.getPlayerInTurn().getId(), card));
                            this.update(new UpdateParametresMessage(gameModel.getPlayerInTurn().getId(), gameModel.getFlightBoard()));
                            this.update(new AskRollDiceMessage(gameModel.getPlayerInTurn().getId(), card, true, true));
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.REPAIRING);

                        this.update(new ChangeTuiStateMessage(gameModel.getPlayerInTurn().getId(),3));
                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                        }

                        if (gameModel.getPlayerInTurn().getShipBoard().getCorrectShip()) {
                            gameModel.getPlayerInTurn().setHasFinishedBuilding(true);
                            gameModel.getPlayerInTurn().setPlayerState(WAIT);
                            this.update(new GenericMessage2(gameModel.getPlayerInTurn().getId(), "\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players..."));
                        } else {
                            gameModel.getPlayerInTurn().setPlayerState(REPAIR);
                        }
                    }
                }

                if(card.getCardType() == CardName.PIRATES) {
                    Pirates piratesCard = (Pirates) card;

                    if (allValid) {
                        if(piratesCard.getCounter() == piratesCard.getShotsPowerArray().size()) {
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayersPosition()) {
                                this.update(new SetCardInUseMessage(player.getId(), card));

                                this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                                this.update(new DrawnCardMessage(player.getId(), card,true));
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                        else {
                            gameModel.setGameState(GameState.ROLL_DICE);
                            for(Player player : gameModel.getDefeatedPlayers()) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));

                                if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    this.update(new AskRollDiceMessage(player.getId(), card, true, true));
                                } else {
                                    this.update(new AskRollDiceMessage(player.getId(), card, false, true));
                                }
                            }
                        }
                    }
                    else {
                        gameModel.setGameState(GameState.REPAIRING);

                        for (Player player : gameModel.getDefeatedPlayers()) {
                            if (player.getShipBoard().getCorrectShip()) {
                                this.update(new ChangeTuiStateMessage(player.getId(),3));
                            }
                            else {
                                this.update(new ChangeTuiStateMessage(player.getId(),2));
                            }
                        }

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                        }

                        for (Player player : gameModel.getDefeatedPlayers()) {
                            if (player.getShipBoard().getCorrectShip()) {
                                player.setHasFinishedBuilding(true);
                                player.setPlayerState(WAIT);
                                this.update(new GenericMessage2(player.getId(), "\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players..."));
                            } else {
                                player.setPlayerState(REPAIR);
                            }
                        }
                    }
                }
            }
            else {
                this.update(new GenericMessage2(clientId, "Waiting for other players to finish repairing their ships."));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId,"This message type: REPAIRING SHIP is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     *This message contains who players have retired from the game
     * @param clientId is the id of the client
     * @param listener the sender is the client
     **/

    public void handleRetireMessage(String clientId, GameEventListener listener) {
        for(Player player : gameModel.getPlayers()) {
            if(player.getId().equals(clientId)) {
                gameModel.addRetiredPlayer(player);
                gameModel.getPlayersPosition().remove(player);
                gameModel.getFlightBoard().removePlayer(player);
                player.setProceed(true);
            }
        }

        this.update(new GenericMessage2(clientId, "You are not in the flight anymore. Watch the other player!!"));

        if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
            listener.finishGame();
            return;
        }
        else {
            boolean proceed = true;
            for (Player otherPlayer : gameModel.getPlayers()) {
                if (otherPlayer.getProceed() == false) {
                    proceed = false;
                }
            }

            if (proceed) {
                for(Player player : gameModel.getPlayers()) {
                    if(player.getToBeRetiredFlag()) {
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            gameModel.addRetiredPlayer(player);
                            gameModel.getPlayersPosition().remove(player);
                            gameModel.getFlightBoard().removePlayer(player);
                            player.setProceed(true);
                            this.update(new GenericMessage2(player.getId(), "You are not in the flight anymore. Watch the other player!!"));
                        }
                    }
                }

                if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
                    listener.finishGame();
                    return;
                }

                for (Player player : gameModel.getPlayers()) {
                    if(gameModel.getFlightType()== FlightType.STANDARD_FLIGHT) {
                        if(player.getShipBoard().getNumFigures()==0) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                gameModel.addRetiredPlayer(player);
                                gameModel.getPlayersPosition().remove(player);
                                gameModel.getFlightBoard().removePlayer(player);
                                player.setProceed(true);
                                this.update(new GenericMessage2(player.getId(), "You are not in the flight anymore. Watch the other player!!"));
                            }
                        }
                    }
                }

                if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
                    listener.finishGame();
                    return;
                }

                for (Player player : gameModel.getPlayers()) {
                    if(gameModel.getFlightType()== FlightType.STANDARD_FLIGHT) {
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            Couple<Integer,Integer> couple=gameModel.getFlightBoard().getPlayerPosition(player);
                            int position=couple.getFirst();
                            int lap=couple.getSecond();

                            for(Player player2 : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player2)) {
                                    Couple<Integer,Integer> couple2=gameModel.getFlightBoard().getPlayerPosition(player2);
                                    int position2=couple2.getFirst();
                                    int lap2=couple2.getSecond();

                                    if(position2>position && lap2>lap) {
                                        if(!gameModel.getRetiredPlayers().contains(player)) {
                                            gameModel.addRetiredPlayer(player);
                                            gameModel.getPlayersPosition().remove(player);
                                            gameModel.getFlightBoard().removePlayer(player);
                                            player.setProceed(true);
                                            this.update(new GenericMessage2(player.getId(), "You are not in the flight anymore. Watch the other player!!"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
                    listener.finishGame();
                    return;
                }

                if(gameModel.getCardsToPlay().size() == 0) {
                    listener.finishGame();
                    return;
                }

                gameModel.refreshPlayersPosition();
                gameModel.getDefeatedPlayers().clear();
                gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

                for (Player player : gameModel.getPlayers()) {
                    if(!gameModel.getRetiredPlayers().contains(player)) {
                        player.setProceed(false);
                    }
                    else {
                        player.setProceed(true);
                    }

                    if(!gameModel.getRetiredPlayers().contains(player)) {
                        if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                            this.update(new LeaderMessage(player.getId(), gameModel.getCardsToPlay().size(), gameModel.getFlightBoard()));
                        }
                        else {
                            this.update(new NotLeaderMessage(player.getId(), gameModel.getCardsToPlay().size(), gameModel.getFlightBoard()));
                        }
                    }
                    else {
                        this.update(new NotLeader2Message(player.getId(), gameModel.getCardsToPlay().size(), gameModel.getFlightBoard(), gameModel.getPlayers()));
                    }
                }
            }
        }
    }

    /**
     * This method is used when player need to roll dice in the game
     * @param clientId is the id of the client
     * @param card is the card which required the dice: meteor-swarm, pirates, combat-zone: to checks if a meteor hit the ship
     **/

    public void handleRollDiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() == GameState.ROLL_DICE) {
            if(card.getCardType().equals(CardName.METEOR_SWARM)) {
                if (clientId.equals(gameModel.getPlayersPosition().get(0).getId())) {
                    int number1 = random.nextInt(6) + 1;
                    int number2 = random.nextInt(6) + 1;
                    int sum = number1 + number2;

                    MeteorSwarm meteorSwarmCard = (MeteorSwarm) card;
                    meteorSwarmCard.incrementCounter();

                    if(gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
                        if (sum <= 4 || sum >= 10) {
                            for (Player player : gameModel.getPlayersPosition()) {
                                this.update(new GenericMessage2(player.getId(), "The dice rolled returned " + sum + ". Your ship board is not hit."));
                            }

                            if (meteorSwarmCard.getCounter() == meteorSwarmCard.getMeteor().size()) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            } else {
                                gameModel.setGameState(GameState.ROLL_DICE);
                                for (Player player : gameModel.getPlayersPosition()) {
                                    if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                        this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                                    } else {
                                        this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                                    }
                                }
                            }
                            return;
                        }
                    }

                    if(gameModel.getFlightType() == FlightType.STANDARD_FLIGHT) {
                        if(((meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getDirection().equals("nord") || meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getDirection().equals("sud")) && (sum <= 3 || sum >= 11))
                                || ((meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getDirection().equals("est") || meteorSwarmCard.getMeteor().get(meteorSwarmCard.getCounter()-1).getDirection().equals("ovest")) && (sum <= 4 || sum >= 10))) {

                            for (Player player : gameModel.getPlayersPosition()) {
                                this.update(new GenericMessage2(player.getId(), "The dice rolled returned " + sum + ". Your ship board is not hit."));
                            }

                            if(meteorSwarmCard.getCounter() == meteorSwarmCard.getMeteor().size()) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            }
                            else {
                                gameModel.setGameState(GameState.ROLL_DICE);

                                for (Player player : gameModel.getPlayersPosition()) {
                                    if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                        this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                                    } else {
                                        this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                                    }
                                }
                            }
                            return;
                        }
                    }

                    gameModel.setGameState(GameState.METEOR_SWARM);
                    for (Player player : gameModel.getPlayersPosition()) {
                        player.getShipBoard().calculateIfHit(meteorSwarmCard, sum);
                    }

                    for (Player player : gameModel.getPlayersPosition()) {
                        this.update(new MeteorHitMessage(player.getId(), card,  player.getShipBoard().isHit(), sum));
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
                }
            }

            if(card.getCardType().equals(CardName.COMBAT_ZONE)) {
                combatZoneFight = true;
                if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                    int number1 = random.nextInt(6) + 1;
                    int number2 = random.nextInt(6) + 1;
                    int sum = number1 + number2;

                    CombatZone combatZoneCard = (CombatZone) card;
                    combatZoneCard.incrementCounter();
                    ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];

                    if(gameModel.getFlightType() == FlightType.FIRST_FLIGHT) {
                        if (sum <= 4 || sum >= 10) {
                            this.update(new GenericMessage2(clientId, "The dice rolled returned " + sum + ". Your ship board is not hit."));

                            if (combatZoneCard.getCounter() == meteors.size()) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            } else {
                                gameModel.setGameState(GameState.ROLL_DICE);
                                this.update(new AskRollDiceMessage(clientId, card, true, false));
                            }
                            return;
                        }
                    }
                    if(gameModel.getFlightType() == FlightType.STANDARD_FLIGHT) {
                        if (((meteors.get(combatZoneCard.getCounter() - 1).getDirection().equals("nord") || meteors.get(combatZoneCard.getCounter() - 1).getDirection().equals("sud")) && (sum <= 3 || sum >= 11))
                                || ((meteors.get(combatZoneCard.getCounter() - 1).getDirection().equals("est") || meteors.get(combatZoneCard.getCounter() - 1).getDirection().equals("ovest")) && (sum <= 4 || sum >= 10))) {

                            this.update(new GenericMessage2(clientId, "The dice rolled returned " + sum + ". Your ship board is not hit."));

                            if (combatZoneCard.getCounter() == meteors.size()) {
                                gameModel.setGameState(GameState.PLAYING);
                                for (Player player : gameModel.getPlayers()) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                    }
                                }
                            } else {
                                gameModel.setGameState(GameState.ROLL_DICE);
                                this.update(new AskRollDiceMessage(clientId, card, true, false));
                            }
                            return;
                        }
                    }

                    gameModel.getPlayerInTurn().getShipBoard().calculateIfHit(combatZoneCard, sum);
                    gameModel.setGameState(GameState.COMBAT_ZONE);
                    this.update(new MeteorHitMessage(gameModel.getPlayerInTurn().getId(), card,  gameModel.getPlayerInTurn().getShipBoard().isHit(), sum));
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
                }
            }

            if(card.getCardType().equals(CardName.PIRATES)) {
                piratesFight = true;
                if (clientId.equals(gameModel.getDefeatedPlayers().get(0).getId())) {
                    int number1 = random.nextInt(6) + 1;
                    int number2 = random.nextInt(6) + 1;
                    int sum = number1 + number2;

                    Pirates piratesCard = (Pirates) card;
                    piratesCard.incrementCounter();

                    if (sum <= 3 || sum >= 11) {
                        for (Player player : gameModel.getDefeatedPlayers()) {
                            this.update(new GenericMessage2(player.getId(), "The dice rolled returned " + sum + ". Your ship board is not hit."));
                        }

                        if (piratesCard.getCounter() == piratesCard.getShotsPowerArray().size()) {
                            gameModel.setGameState(GameState.PLAYING);

                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        } else {
                            gameModel.setGameState(GameState.ROLL_DICE);
                            gameModel.setPlayerInTurn(gameModel.getDefeatedPlayers().get(0));

                            for (Player player : gameModel.getDefeatedPlayers()) {
                                if (player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                    this.update(new AskRollDiceMessage(player.getId(), card, true, false));

                                } else {
                                    this.update(new AskRollDiceMessage(player.getId(), card, false, false));
                                }
                            }
                        }
                        return;
                    }

                    gameModel.setGameState(GameState.PIRATES);
                    for (Player player : gameModel.getDefeatedPlayers()) {
                        player.getShipBoard().calculateIfHit(piratesCard, sum);
                    }

                    for (Player player : gameModel.getDefeatedPlayers()) {
                        this.update(new MeteorHitMessage(player.getId(), card,  player.getShipBoard().isHit(), sum));
                    }
                }
                else {
                    this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
                }
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: ROLL_DICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method provides to see the other player's ship
     * @param clientId is the client id
     **/

    public void handleShowAllShipBoardsMessage(String clientId) {
        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
        for(Player player : gameModel.getPlayers()) {
            shipBoards.put(player.getNickname(), player.getShipBoard());
        }
        this.update(new ReturnAllShipBoardsMessage(clientId, shipBoards));
    }

    /**
     * This method manages the Slavers card: player has to calculate the fire strength to avoid losses on his ship
     * @param clientId is the id of the client
     * @param card is the card drawn
     **/

    public void handleSlaversChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() == GameState.SLAVERS) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                Slavers salversCard = (Slavers) card;
                double fireStrength = gameModel.getPlayerInTurn().getShipBoard().calculateFireStrength();

                if(fireStrength > salversCard.getEnemyStrength()) {
                    gameModel.getFlightBoard().movePlayerBackward(gameModel.getPlayerInTurn(), salversCard.getLoseFlightDays()); // move the player on the flightboard
                    gameModel.getPlayerInTurn().incrementCosmicCredit(salversCard.getNumOfCreditsTaken());

                    for(Player player : gameModel.getPlayers()) {
                        player.getShipBoard().restoreCannons();
                    }

                    for(Player player : gameModel.getPlayers()) {
                        this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                        }
                    }

                    HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                    for(Player player1 : gameModel.getPlayers()) {
                        shipBoards.put(player1.getNickname(), player1.getShipBoard());
                    }

                    for(Player otherPlayer : gameModel.getPlayers()) {
                        this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                        if(otherPlayer.getId().equals(clientId)) {
                            this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                        }
                    }

                    for (Player player : gameModel.getPlayersPosition()) {
                        if(player.getShipBoard().getNumFigures() == 0 && !player.getShipBoard().getHasPurpleAlien() && !player.getShipBoard().getHasBrownAlien()) {
                            player.setPenaltyEquipment(0);
                        }
                    }

                    boolean removeFigures = false;
                    for(Player player : gameModel.getPlayersPosition()) {
                        if(player.getPenaltyEquipment() > 0) {
                            removeFigures = true;
                        }
                    }

                    if(removeFigures) {
                        gameModel.setGameState(GameState.REMOVE_FIGURES);
                        for(Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                if(player.getPenaltyEquipment() > 0) {
                                    player.setPlayerState(PlayerState.REMOVE_FIGURE);
                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                    this.update(new AskRemoveFigureMessage(player.getId()));
                                }
                                else {
                                    player.setPlayerState(PlayerState.WAIT);
                                    this.update(new ChangeTuiStateMessage(player.getId(),1));
                                    this.update(new DrawnCardMessage(player.getId(), card, false));
                                }
                            }
                        }
                    }
                    else {
                        for(Player player : gameModel.getPlayers()) {
                            player.setPlayerState(PlayerState.PLAY);
                        }
                        gameModel.setGameState(GameState.PLAYING);
                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                            }
                        }
                    }
                    gameModel.refreshPlayersPosition();
                }

                else if(fireStrength == salversCard.getEnemyStrength()) {
                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        for(Player player : gameModel.getPlayers()) {
                            player.getShipBoard().restoreCannons();
                        }

                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                            }
                        }

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if(otherPlayer.getId().equals(clientId)) {
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        for (Player player : gameModel.getPlayersPosition()) {
                            if(player.getShipBoard().getNumFigures() == 0 && !player.getShipBoard().getHasPurpleAlien() && !player.getShipBoard().getHasBrownAlien()) {
                                player.setPenaltyEquipment(0);
                            }
                        }

                        boolean removeFigures = false;
                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getPenaltyEquipment() > 0) {
                                removeFigures = true;
                            }
                        }

                        if(removeFigures) {
                            gameModel.setGameState(GameState.REMOVE_FIGURES);
                            for(Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    if(player.getPenaltyEquipment() > 0) {
                                        player.setPlayerState(PlayerState.REMOVE_FIGURE);
                                        this.update(new ChangeTuiStateMessage(player.getId(),1));
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                        this.update(new AskRemoveFigureMessage(player.getId()));
                                    }
                                    else {
                                        player.setPlayerState(PlayerState.WAIT);
                                        this.update(new ChangeTuiStateMessage(player.getId(),1));
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                    }
                                }
                            }
                        }
                        else {
                            for(Player player : gameModel.getPlayers()) {
                                player.setPlayerState(PlayerState.PLAY);
                            }
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                        gameModel.refreshPlayersPosition();
                    }
                }
                else {
                    gameModel.getPlayerInTurn().setPenaltyEquipment(salversCard.getNumOfLoseFigures());
                    gameModel.getPlayerInTurn().setPlayerState(PlayerState.REMOVE_FIGURE);

                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        for(Player player : gameModel.getPlayers()) {
                            player.getShipBoard().restoreCannons();
                        }

                        for(Player player : gameModel.getPlayers()) {
                            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
                            }
                        }

                        HashMap<String, ShipBoard> shipBoards = new HashMap<>();
                        for(Player player1 : gameModel.getPlayers()) {
                            shipBoards.put(player1.getNickname(), player1.getShipBoard());
                        }

                        for(Player otherPlayer : gameModel.getPlayers()) {
                            this.update(new ReturnAllShipBoardsMessage(otherPlayer.getId(), shipBoards));
                            if(otherPlayer.getId().equals(clientId)) {
                                this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                            }
                        }

                        for (Player player : gameModel.getPlayersPosition()) {
                            if(player.getShipBoard().getNumFigures() == 0 && !player.getShipBoard().getHasPurpleAlien() && !player.getShipBoard().getHasBrownAlien()) {
                                player.setPenaltyEquipment(0);
                            }
                        }

                        boolean removeFigures = false;
                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getPenaltyEquipment() > 0) {
                                removeFigures = true;
                            }
                        }

                        if(removeFigures) {
                            gameModel.setGameState(GameState.REMOVE_FIGURES);
                            for(Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    if(player.getPenaltyEquipment() > 0) {
                                        player.setPlayerState(PlayerState.REMOVE_FIGURE);
                                        this.update(new ChangeTuiStateMessage(player.getId(),1));
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                        this.update(new AskRemoveFigureMessage(player.getId()));
                                    }
                                    else {
                                        player.setPlayerState(PlayerState.WAIT);
                                        this.update(new ChangeTuiStateMessage(player.getId(),1));
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                    }
                                }
                            }
                        }
                        else {
                            for(Player player : gameModel.getPlayers()) {
                                player.setPlayerState(PlayerState.PLAY);
                            }
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                        gameModel.refreshPlayersPosition();
                    }
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: SLAVERS CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method manages the smugglers card, player has to calculate the fire strength to avoid losses on his ship
     * @param clientId is the client id
     * @param card is the card drawn
     **/

    public void handleSmugglersChoiceMessage(String clientId, Card card) {
        if (gameModel.getGameState() == GameState.SMUGGLERS) {
            if (clientId.equals(gameModel.getPlayerInTurn().getId())) {
                Smugglers smugglersCard = (Smugglers) card;
                double fireStrength = gameModel.getPlayerInTurn().getShipBoard().calculateFireStrength();

                if(fireStrength > smugglersCard.getEnemyStrength()) {
                    gameModel.getFlightBoard().movePlayerBackward(gameModel.getPlayerInTurn(), smugglersCard.getLoseFlightDays()); // move the player on the flightboard

                    ArrayList<Color> temp = smugglersCard.getColorOfGoodsTaken();
                    for(Color goodsColor : temp) {
                        gameModel.getPlayerInTurn().insertGoodsBlock(goodsColor);
                    }

                    for(Player player : gameModel.getPlayers()) {
                        player.getShipBoard().restoreCannons();
                    }

                    for(Player player : gameModel.getPlayers()) {
                        this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
                    }

                    gameModel.setGameState(GameState.GAIN_GOODS);
                    for(Player player : gameModel.getPlayers()) {
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            if(gameModel.getPlayerInTurn().getId().equals(player.getId())) {
                                player.setPlayerState(PlayerState.GAIN_GOOD);
                                this.update(new DrawnCardMessage(player.getId(), card, true));
                                this.update(new GainedGoodsMessage(player.getId(), card, player.getTempGoodsBlock()));
                            }
                            else {
                                player.setPlayerState(PlayerState.WAIT);
                                this.update(new WaitMessage(player.getId(), card));
                            }
                        }
                    }
                    gameModel.refreshPlayersPosition();
                }
                else if(fireStrength == smugglersCard.getEnemyStrength()) {
                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        for(Player player : gameModel.getPlayers()) {
                            player.getShipBoard().restoreCannons();
                        }

                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                if(player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                                    player.setPenaltyGoods(0);
                                }
                            }
                        }

                        boolean removeGoods = false;
                        for(Player player : gameModel.getPlayers()) {
                            if(player.getPenaltyGoods() > 0) {
                                removeGoods = true;
                            }
                        }

                        if(removeGoods) {
                            gameModel.setGameState(GameState.REMOVE_GOODS);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    if (player.getPenaltyGoods() > 0) {
                                        if(player.getShipBoard().rarestGoodsBlock() == null) {
                                            if(player.getShipBoard().getNumBatteries() == 0) {
                                                player.setPenaltyGoods(0);
                                                player.setPlayerState(PlayerState.WAIT);
                                                this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                this.update(new DrawnCardMessage(player.getId(), card, false));
                                            }
                                            else {
                                                player.setPlayerState(PlayerState.REMOVE_BATTERY);
                                                this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                this.update(new DrawnCardMessage(player.getId(), card, false));
                                                this.update(new AskRemoveBatteryMessage(player.getId()));
                                            }
                                        }
                                        else {
                                            player.setPlayerState(PlayerState.REMOVE_GOOD);
                                            this.update(new ChangeTuiStateMessage(player.getId(),1));
                                            this.update(new DrawnCardMessage(player.getId(), card, false));
                                            this.update(new AskRemoveGoodMessage(player.getId()));
                                        }
                                    } else {
                                        player.setPlayerState(PlayerState.WAIT);
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                    }
                                }
                            }
                        }
                        else {
                            for(Player player : gameModel.getPlayers()) {
                                player.setPlayerState(PlayerState.PLAY);
                            }
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                    }
                }
                else {
                    gameModel.getPlayerInTurn().setPenaltyGoods(smugglersCard.getGoodsLose());

                    if(!(gameModel.getNextPlayer().equals(gameModel.getPlayersPosition().get(0)))) {
                        gameModel.setPlayerInTurn(gameModel.getNextPlayer());

                        for(Player player : gameModel.getPlayersPosition()) {
                            if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                            }
                            else {
                                this.update(new SetCardInUseMessage(player.getId(), card));
                                this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                            }
                        }
                    }
                    else {
                        for(Player player : gameModel.getPlayers()) {
                            player.getShipBoard().restoreCannons();
                        }

                        for (Player player : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player)) {
                                if(player.getShipBoard().rarestGoodsBlock() == null && player.getShipBoard().getNumBatteries() == 0) {
                                    player.setPenaltyGoods(0);
                                }
                            }
                        }

                        boolean removeGoods = false;
                        for(Player player : gameModel.getPlayers()) {
                            if(player.getPenaltyGoods() > 0) {
                                removeGoods = true;
                            }
                        }

                        if(removeGoods) {
                            gameModel.setGameState(GameState.REMOVE_GOODS);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    if (player.getPenaltyGoods() > 0) {
                                        if(player.getShipBoard().rarestGoodsBlock() == null) {
                                            if(player.getShipBoard().getNumBatteries() == 0) {
                                                player.setPenaltyGoods(0);
                                                player.setPlayerState(PlayerState.WAIT);
                                                this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                this.update(new DrawnCardMessage(player.getId(), card, false));
                                            }
                                            else {
                                                player.setPlayerState(PlayerState.REMOVE_BATTERY);
                                                this.update(new ChangeTuiStateMessage(player.getId(),1));
                                                this.update(new DrawnCardMessage(player.getId(), card, false));
                                                this.update(new AskRemoveBatteryMessage(player.getId()));
                                            }
                                        }
                                        else {
                                            player.setPlayerState(PlayerState.REMOVE_GOOD);
                                            this.update(new ChangeTuiStateMessage(player.getId(),1));
                                            this.update(new DrawnCardMessage(player.getId(), card, false));
                                            this.update(new AskRemoveGoodMessage(player.getId()));
                                        }
                                    } else {
                                        player.setPlayerState(PlayerState.WAIT);
                                        this.update(new DrawnCardMessage(player.getId(), card, false));
                                    }
                                }
                            }
                        }
                        else {
                            for(Player player : gameModel.getPlayers()) {
                                player.setPlayerState(PlayerState.PLAY);
                            }
                            gameModel.setGameState(GameState.PLAYING);
                            for (Player player : gameModel.getPlayers()) {
                                if(!gameModel.getRetiredPlayers().contains(player)) {
                                    this.update(new ProceedNextCardMessage(player.getId(), card, gameModel.getCardsToPlay()));
                                }
                            }
                        }
                    }
                }
            }
            else {
                this.update(new GenericErrorMessage(clientId, "This choice can be made only by the player in turn: "+ gameModel.getPlayerInTurn().getNickname()));
            }
        }
        else {
            this.update(new GenericErrorMessage(clientId, "This message type: SMUGGLERS CHOICE is not available for this game state: " + gameModel.getGameState().toString()));
        }
    }

    /**
     * This method allows players to finish watching other ships
     * @param clientId is the id of the client
     * @param card is the card drawn
     **/

    public void handleStopWatchingShipsMessage(String clientId, Card card) {
        if(gameModel.getGameState() == GameState.BUILDING) {
            for(Player otherPlayer : gameModel.getPlayers()) {
                this.update(new ShowTilesDeckMessage(otherPlayer.getId(), gameModel.getGameTile()));
                if(otherPlayer.getId().equals(clientId)) {
                    this.update(new ShowShipBoardMessage(otherPlayer.getId(), otherPlayer.getShipBoard()));
                }
            }
        }
        else {
            if(card != null && card.getCardType() == CardName.COMBAT_ZONE) {
                Player p = null;
                for(Player otherPlayer : gameModel.getPlayers()) {
                    if(otherPlayer.getId().equals(clientId)) {
                        p = otherPlayer;
                    }
                }

                if(p.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new DrawnCardMessage(p.getId(), card, true));
                }
                else {
                    this.update(new ChangeTuiStateMessage(p.getId(),1));
                    this.update(new DrawnCardMessage(p.getId(), card, false));
                }
            }
            else {
                for(Player otherPlayer : gameModel.getPlayers()) {
                    if(otherPlayer.getId().equals(clientId)) {
                        if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                            this.update(new ChangeTuiStateMessage(otherPlayer.getId(),1));
                            this.update(new DrawnCardMessage(otherPlayer.getId(), card, false));
                        }
                        else {
                            this.update(new DrawnCardMessage2(otherPlayer.getId(), card));
                        }
                    }
                }
            }
        }
    }

    /**
     * This method manages the hourglass and its implementation
     * @param clientId is the id of the client
     **/

    public void handleTimerMessage(String clientId, int time) {
        if(gameModel.getGameState() == GameState.BUILDING && gameModel.getFlightType()== FlightType.STANDARD_FLIGHT) {
            if (gameModel.getTimerPosition() == 0) {
                this.update(new GenericErrorMessage(clientId, "It's not possible to flip the timer anymore"));
                return;
            }

            if (!gameModel.activateTimer()) {
                this.update(new GenericErrorMessage(clientId, "The time of the previous flip is not finished. You have to wait before flip the timer again"));
                return;
            }

            long startTime = System.nanoTime();
            int totalTime = time;
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(); //This creates a timer in background and meanwhile the game can continue
            Runnable updateTask = () -> {
                long currentTime = System.nanoTime();
                long elapsedNanos = currentTime - startTime;
                long elapsedSeconds = TimeUnit.NANOSECONDS.toSeconds(elapsedNanos);
                long remaining = totalTime - elapsedSeconds;

                if (remaining > 0) {
                    for (Player player : gameModel.getPlayers()) {
                        this.update(new RemainingTimeMessage(player.getId(), (int) remaining));
                    }
                } else {
                    if (gameModel.getTimerPosition() == 2) {
                        for (Player player : gameModel.getPlayers()) {
                            this.update(new TimerExpiredMessage(player.getId()));
                        }
                        gameModel.setTimerPosition(1);
                    } else if (gameModel.getTimerPosition() == 1) {
                        for (Player player : gameModel.getPlayers()) {
                            this.update(new TimerExpiredMessage(player.getId()));
                        }
                        gameModel.setTimerPosition(0);
                        gameModel.setGameState(GameState.FINISHING);
                    }
                    gameModel.setTimerActive(false);
                    scheduler.shutdownNow();
                }
            };
            scheduler.scheduleAtFixedRate(updateTask, 0, 1, TimeUnit.SECONDS);
        }
        else {
            if(gameModel.getGameState() != GameState.BUILDING) {
                this.update(new GenericErrorMessage(clientId, "This message type: TIMER is not available for this game state: " + gameModel.getGameState().toString()));
            }

            if(gameModel.getFlightType()!= FlightType.STANDARD_FLIGHT) {
                this.update(new GenericErrorMessage(clientId, "This message type: TIMER is not available for this flight type: " + gameModel.getFlightType().toString()));
            }
        }
    }

    public void handleUsernameMessage(String clientId, String nickname) {}

    /**
     * This method allows the player to wait for other players' turns to end
     * @param clientId is the client id
     * @param card is the card drawn
     **/
    public void handleWaitNextPhaseMessage(String clientId, Card card) {
        Player p = null;

        for (Player player : gameModel.getPlayersPosition()) {
            if(player.getId().equals(clientId)) {
                p = player;
            }
        }

        p.setProceed(true);
        boolean proceed = true;
        for (Player otherPlayer : gameModel.getPlayersPosition()) {
            if (otherPlayer.getProceed() == false) {
                proceed = false;
            }
        }

        gameModel.refreshPlayersPosition();
        gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

        if(!proceed) {
            this.update(new NotProceedMessage(clientId));
            return;
        }

        for (Player otherPlayer : gameModel.getPlayers()) {
            if(!gameModel.getRetiredPlayers().contains(otherPlayer)) {
                otherPlayer.setProceed(false);
            }
            else {
                otherPlayer.setProceed(true);
            }
        }

        for(Player player : gameModel.getPlayers()) {
            player.getShipBoard().restoreEngines();
            player.getShipBoard().restoreCannons();
            player.getShipBoard().restoreShields();
            if(!gameModel.getRetiredPlayers().contains(player)) {
                this.update(new ShowShipBoardMessage(player.getId(), player.getShipBoard()));
            }
            this.update(new UpdateParametresMessage(player.getId(), gameModel.getFlightBoard()));
        }

        ((CombatZone) card).incrementFaseCounter();
        if(((CombatZone) card).getFaseCounter()==1) {
            gameModel.setGameState(GameState.FASE_1);
        }
        else if(((CombatZone) card).getFaseCounter()==2) {
            gameModel.setGameState(GameState.FASE_2);
        }
        else if(((CombatZone) card).getFaseCounter()==3) {
            gameModel.setGameState(GameState.FASE_3);
        }

        if(card.getLevel() == 2 && ((CombatZone) card).getFaseCounter() == 3) {
            Optional<Player> minPlayerEquipment = gameModel.getPlayersPosition().stream().reduce((p1,p2) -> {
                int count1 = p1.getShipBoard().getNumFigures()
                        + (p1.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                        + (p1.getShipBoard().getHasBrownAlien() ? 1 : 0);

                int count2 = p2.getShipBoard().getNumFigures()
                        + (p2.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                        + (p2.getShipBoard().getHasBrownAlien() ? 1 : 0);

                return count1 <= count2 ? p1 : p2;
            });
            gameModel.setPlayerInTurn(minPlayerEquipment.orElse(null));
            gameModel.setGameState(GameState.ROLL_DICE);

            for(Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new SetCardInUseMessage(player.getId(),card));
                    this.update(new AskRollDiceMessage(player.getId(), card, true, false));
                }
                else {
                    this.update(new SetCardInUseMessage(player.getId(),card));
                    this.update(new DrawnCardMessage(player.getId(), card, false));
                }
            }
        }
        else {
            gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

            for(Player player : gameModel.getPlayersPosition()) {
                if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                    this.update(new SetCardInUseMessage(player.getId(),card));
                    this.update(new DrawnCardMessage(player.getId(), card, true)); // true if the player is in turn
                }
                else {
                    this.update(new SetCardInUseMessage(player.getId(),card));
                    this.update(new DrawnCardMessage(player.getId(), card, false)); // false if the player is not in turn
                }
            }
        }
    }

    /**
     * This method allows to proceed at the next phase
     * @param clientId is the client id
     * @param listener the sender is the client
     **/

    public void handleWaitProceedMessage(String clientId, GameEventListener listener) {
        Player p = null;

        for (Player player : gameModel.getPlayers()) {
            if(player.getId().equals(clientId)) {
                p = player;
            }
        }

        p.setProceed(true);
        boolean proceed = true;
        for (Player otherPlayer : gameModel.getPlayers()) {
            if (otherPlayer.getProceed() == false) {
                proceed = false;
            }
        }

        if (proceed) {
            for(Player player : gameModel.getPlayers()) {
                if(player.getToBeRetiredFlag()) {
                    if(!gameModel.getRetiredPlayers().contains(player)) {
                        gameModel.addRetiredPlayer(player);
                        gameModel.getPlayersPosition().remove(player);
                        gameModel.getFlightBoard().removePlayer(player);
                        player.setProceed(true);
                        this.update(new GenericMessage2(player.getId(), "You are not in the flight anymore. Watch the other player!!"));
                    }
                }
            }

            if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
                listener.finishGame();
                return;
            }

            for (Player player : gameModel.getPlayers()) {
                if(gameModel.getFlightType()== FlightType.STANDARD_FLIGHT) {
                    if(player.getShipBoard().getNumFigures()==0) {
                        if(!gameModel.getRetiredPlayers().contains(player)) {
                            gameModel.addRetiredPlayer(player);
                            gameModel.getPlayersPosition().remove(player);
                            gameModel.getFlightBoard().removePlayer(player);
                            player.setProceed(true);
                            this.update(new GenericMessage2(player.getId(), "You are not in the flight anymore. Watch the other player!!"));
                        }
                    }
                }
            }

            if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
                listener.finishGame();
                return;
            }

            for (Player player : gameModel.getPlayers()) {
                if(gameModel.getFlightType()== FlightType.STANDARD_FLIGHT) {
                    if(!gameModel.getRetiredPlayers().contains(player)) {
                        Couple<Integer,Integer> couple=gameModel.getFlightBoard().getPlayerPosition(player);
                        int position=couple.getFirst();
                        int lap=couple.getSecond();

                        for(Player player2 : gameModel.getPlayers()) {
                            if(!gameModel.getRetiredPlayers().contains(player2)) {
                                Couple<Integer,Integer> couple2=gameModel.getFlightBoard().getPlayerPosition(player2);
                                int position2=couple2.getFirst();
                                int lap2=couple2.getSecond();

                                if(position2>position && lap2>lap) {
                                    if(!gameModel.getRetiredPlayers().contains(player)) {
                                        gameModel.addRetiredPlayer(player);
                                        gameModel.getPlayersPosition().remove(player);
                                        gameModel.getFlightBoard().removePlayer(player);
                                        player.setProceed(true);
                                        this.update(new GenericMessage2(player.getId(), "You are not in the flight anymore. Watch the other player!!"));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(gameModel.getPlayers().size() == gameModel.getRetiredPlayers().size()) {
                listener.finishGame();
                return;
            }

            if(gameModel.getCardsToPlay().size() == 0) {
                listener.finishGame();
            }

            gameModel.refreshPlayersPosition();
            gameModel.getDefeatedPlayers().clear();
            gameModel.setPlayerInTurn(gameModel.getPlayersPosition().get(0));

            for (Player player : gameModel.getPlayers()) {
                if(!gameModel.getRetiredPlayers().contains(player)) {
                    player.setProceed(false);
                }
                else {
                    player.setProceed(true);
                }

                if(!gameModel.getRetiredPlayers().contains(player)) {
                    if(player.getId().equals(gameModel.getPlayerInTurn().getId())) {
                        this.update(new LeaderMessage(player.getId(), gameModel.getCardsToPlay().size(), gameModel.getFlightBoard()));
                    }
                    else {
                        this.update(new NotLeaderMessage(player.getId(), gameModel.getCardsToPlay().size(), gameModel.getFlightBoard()));
                    }
                }
                else {
                    this.update(new NotLeader2Message(player.getId(), gameModel.getCardsToPlay().size(), gameModel.getFlightBoard(), gameModel.getPlayers()));
                }
            }
        }
        else {
            this.update(new NotProceedMessage(clientId));
        }
    }

    /**
     * This method handles the events notified by the game.
     * @param message is the message used to share information.
     */
    @Override
    public void update(Message message) {
        message.updateController(gameModel, this.networkView);
    }

    /**
     * This method returns the game controlled by this controller.
     * @return the game controlled by controller.
     */
     public GameModel getGameModel() {
        return gameModel;
     }
}