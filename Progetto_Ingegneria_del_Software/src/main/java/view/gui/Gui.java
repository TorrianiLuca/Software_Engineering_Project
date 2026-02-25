package view.gui;

import controller.ClientController;
import enumerations.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.card.Card;
import model.card.CardPile;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import network.messages.Message;
import observer.ViewObserver;
import support.Quadruple;
import view.View;
import view.gui.SceneController.*;
import view.tui.Font;
import view.tui.Tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Graphical interface (GUI) for the game.
 */
public class Gui extends Application implements View {
    private static boolean isSocket;
    private Stage primaryStage;
    private ClientController clientController;
    private final List<ViewObserver> observers = new ArrayList<>();
    private StackPane rootPane;
    private Scene mainScene;

    private NicknameController nicknameController = new NicknameController(this);
    private ShowGameController showGameController = new ShowGameController(this);
    private WaitingPlayersController waitingPlayersController = new WaitingPlayersController(this);
    private ShowTilesController showTilesController = new ShowTilesController(this);
    private NextCardController nextCardController = new NextCardController(this);
    private WinnerController winnerController = new WinnerController(this);

    private StackPane connectionPane;
    private StackPane nicknamePane;
    private StackPane showGamePane;
    private StackPane waitingPlayersPane;
    private StackPane showTilesPane;
    private StackPane nextCardPane;
    private StackPane winnerPane;

    private String clientId;
    private String nickname;
    private ShipBoard shipBoard;
    private FlightType flightType;
    private HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games;

    private final BlockingQueue<Message> serverMessageQueue = new LinkedBlockingQueue<>();

    public static void setIsSocket(boolean isSocket) {
        Gui.isSocket = isSocket;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Galaxy Trucker");

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setResizable(false);

        this.clientController = new ClientController(this, isSocket);
        this.addObserver(clientController);

        rootPane = new StackPane();
        mainScene = new Scene(rootPane, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(mainScene);

        Thread serverMessageProcessor = new Thread(this::processServerMessages);
        serverMessageProcessor.setDaemon(true);
        serverMessageProcessor.start();

        askServerInfo();
    }

    /**
     * This method will add an observer to the list of the view observers
     * @param observer is the new view observer
     */
    public void addObserver(ViewObserver observer) {
        observers.add(observer);
    }

    /**
     * This method will notify a specific view observer that will accept the provided function
     * @param action is the function that the view observer has to satisfy
     */
    public void notifyObserver(Consumer<ViewObserver> action) {
        for (ViewObserver observer : observers) {
            action.accept(observer);
        }
    }

    @Override
    public void addServerMessage(Message message) {
        serverMessageQueue.offer(message);
    }

    /**
     * Method used to process the messages that are in the queue.
     */
    private void processServerMessages() {
        while (true) {
            try {
                Message message = serverMessageQueue.take();
                message.updateClient(this);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Method that initializes the gui.
     */
    public void askServerInfo() {
        Platform.runLater(() -> {
            if (connectionPane == null) {
                try {
                    FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("/fxml/connection.fxml"));
                    if (connectionLoader.getLocation() == null) {
                        throw new IOException("connection.fxml not found");
                    }
                    connectionLoader.setController(new ConnectionController(isSocket, this));
                    connectionPane = connectionLoader.load();
                    rootPane.getChildren().add(connectionPane);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            showPane(connectionPane);
            primaryStage.show();
        });
    }

    @Override
    public void askNickname(String clientId) {
        this.clientId = clientId;
        Platform.runLater(() -> {
            if (nicknamePane == null) {
                try {
                    FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("/fxml/nickname.fxml"));
                    if (connectionLoader.getLocation() == null) {
                        throw new IOException("connection.fxml not found");
                    }
                    connectionLoader.setController(nicknameController);
                    nicknamePane = connectionLoader.load();
                    rootPane.getChildren().add(nicknamePane);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            showPane(nicknamePane);
            primaryStage.show();
        });
    }

    @Override
    public void showAvailableGames(HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games) {
        this.games = games;

        Platform.runLater(() -> {
            if (showGamePane == null) {
                try {
                    FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("/fxml/showGame.fxml"));
                    if (connectionLoader.getLocation() == null) {
                        throw new IOException("connection.fxml not found");
                    }
                    connectionLoader.setController(showGameController);
                    showGamePane = connectionLoader.load();
                    rootPane.getChildren().add(showGamePane);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Error: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            showPane(showGamePane);
            showGameController.updateGameList(games);

            primaryStage.show();
        });
    }

    @Override
    public void askMaxPlayerAndFlightType() {
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();

            VBox content = new VBox(10);
            Spinner<Integer> playerSpinner = new Spinner<>(2, 4, 2);
            playerSpinner.setEditable(true);
            ToggleGroup flightGroup = new ToggleGroup();
            RadioButton firstFlight = new RadioButton("First Flight");
            firstFlight.setToggleGroup(flightGroup);
            firstFlight.setSelected(true);
            RadioButton standardFlight = new RadioButton("Standard Flight");
            standardFlight.setToggleGroup(flightGroup);

            content.getChildren().addAll(new Label("Numero di Giocatori:"), playerSpinner, new Label("Tipo di Volo:"), firstFlight, standardFlight);
            dialog.getDialogPane().setContent(content);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int maxPlayers = playerSpinner.getValue();
                    FlightType flightType = firstFlight.isSelected() ? FlightType.FIRST_FLIGHT : FlightType.STANDARD_FLIGHT;
                    notifyObserver(obs -> obs.onUpdateMaxPlayerAndFlightType(maxPlayers, flightType));
                }
            });
        });
    }

    @Override
    public void showGenericError(String error) {
        Platform.runLater(() -> {
            if(error.equals("Username is already taken.")) {
                nicknameController.setErrorLabel(error);
            }
            if(error.equals("The game is already full. Try again")) {
                showGameController.setErrorLabel2(error);
            }
            if(error.startsWith("The tile with ID") && error.endsWith("is already occupied")) {
                showTilesController.setShipErrorLabel(error);
            }
            if(error.equals("The time of the previous flip is not finished. You have to wait before flip the timer again")) {
                showTilesController.setShipErrorLabel(error);
            }
            if(error.startsWith("The pile with ID") && error.endsWith("is already occupied")) {
                showTilesController.setShipErrorLabel(error);
            }
            if (error.equals("You cannot insert more than two astronauts in a cabine!")
                || error.equals("You cannot insert more crew in the cabine because there is already an alien in it!")
                || error.equals("You cannot put astronauts and aliens in the same cabine!")
                || error.equals("You cannot insert more crew in the cabin because there is already an alien in it!")
                || error.equals("You ship already has a brown alien!")
                || error.equals("You cannot insert a brown alien because your cabin isn't connected to a brown support system!")
                || error.equals("You cannot insert an alien in this cabin because it isn't linked to a support system!")
                || error.equals("You cannot put aliens in the starting cabin!")
                || error.equals("You ship already has a purple alien!")
                || error.equals("You cannot insert a purple alien because your cabin isn't connected to a purple support system!")) {
                showTilesController.setErrorLabel5(error);
            }
            if(error.equals("You have no batteries") || error.equals("You don't have enough figures") || error.equals("The cargo cannot fit the goods block considered") || error.equals("This planet choice is not correct")) {
                nextCardController.setErrorLabel(error);
            }
            if(error.startsWith("The cargo in coordinates") && error.endsWith("doesn't contain the rarest goods block in the ship")) {
                nextCardController.setErrorLabel(error);
            }
        });

    }

    @Override
    public void showShipBoard(ShipBoard shipBoard) {
        this.shipBoard = shipBoard;
        if (showTilesLatch != null) {
            try {
                if (!showTilesLatch.await(1, TimeUnit.SECONDS)) {
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        Platform.runLater(() -> {
            try {
                if (showTilesPane != null && showTilesPane.isVisible()) {
                    if (showTilesController == null) {
                        return;
                    }
                    showTilesController.updateShipBoard(shipBoard);
                    showPane(showTilesPane);
                }
                else if (nextCardPane != null && nextCardPane.isVisible()) {
                    if (nextCardController == null) {
                        return;
                    }
                    nextCardController.updateShipBoard(shipBoard);
                    showPane(nextCardPane);
                }
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Errore nel caricamento di showShipBoard: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    @Override
    public void showTimer(int time) {
        showTilesController.updateTimerLabel(time);
    }

    @Override public void proceed2() {
        showTilesController.setPopulate();
    }

    @Override public void showTempPositions(ArrayList<Player> tempPositions) {
        showTilesController.showTempPositions(tempPositions);
    }

    @Override public void showCardPile(CardPile cardPile) {
        showTilesController.showCardsPiles(cardPile);
    }

    @Override public void showAllShipBoards(HashMap<String, ShipBoard> shipBoards) {
        shipBoards.remove(this.nickname);
        if (showTilesPane != null && showTilesPane.isVisible()) {
            showTilesController.showShips(shipBoards);
        }
        if (nextCardPane != null && nextCardPane.isVisible()) {
            nextCardController.showShips(shipBoards);
        }
    }

    @Override public void showLeader(int numCards, FlightBoard flightBoard) {
        for(Player player : flightBoard.getPlayersMap().keySet()) {
            if(player.getId().equals(clientId)) {
                nextCardController.showLeader(flightBoard, player);
            }
        }
    }

    @Override public void showNotLeader(int numCards, FlightBoard flightBoard) {
        for(Player player : flightBoard.getPlayersMap().keySet()) {
            if(player.getId().equals(clientId)) {
                nextCardController.showNotLeader(flightBoard, player);
            }
        }
    }

    @Override public void showNotLeader2(int numCards, FlightBoard flightBoard, ArrayList<Player> players) {
        for(Player player : players) {
            if(player.getId().equals(this.clientId)) {
                nextCardController.showRetired(flightBoard, player);
            }
        }
    }

    @Override public void setCardInUse(Card card) {
        nextCardController.setCardInUse(card);
    }

    @Override public void showCard(boolean inTurn) {
        nextCardController.showCard(inTurn);
    }

    @Override public void showCard2() {
        nextCardController.showCardRetired();
    }

    @Override public void setRollDice(Card card, boolean inTurn, boolean proceed) {
        nextCardController.setRollDice(card, inTurn, proceed);
    }

    @Override public void showMeteorHit(Card card, boolean isHit, int sum) {
        nextCardController.showMeteorHit(card, isHit, sum);
    }

    @Override public void showGoods(Card card, ArrayList<Color> tempGoodsBlock) {
        nextCardController.showGoods(card, tempGoodsBlock);
    }

    @Override public void showWait(Card card) {
        nextCardController.showWait(card);
    }

    @Override public void proceedToNextCard(Card card, int num, ArrayList<Card> cardsToPlay) {
        Platform.runLater(() -> {
            if (nextCardPane == null) {
                try {
                    FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("/fxml/nextCard.fxml"));
                    if (connectionLoader.getLocation() == null) {
                        throw new IOException("connection.fxml not found");
                    }
                    connectionLoader.setController(nextCardController);
                    nextCardPane = connectionLoader.load();
                    rootPane.getChildren().add(nextCardPane);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Errore nel caricamento di connection.fxml: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            showPane(nextCardPane);

            primaryStage.show();
            nextCardController.setFlightType(this.flightType);
            if(card == null) {
                nextCardController.showCardsToPlay(cardsToPlay);
            }
            else {
                if(num == 1) {
                    nextCardController.proceedToNextCard(card,cardsToPlay);
                }
                else if (num == 2) {
                    nextCardController.proceedToNextPhase(card);
                }
            }
            this.showShipBoard(shipBoard);
        });
    }

    @Override public void notProceed() {
        nextCardController.waitPlayersToProceed();
    }

    @Override public void changeState(int num) {
        if(num == 2 || num == 3) {
            nextCardController.changeState();
        }
    }

    @Override public void setRemoveBattery() {
        nextCardController.setRemoveBattery();
    }

    @Override public void setRemoveGood() {
        nextCardController.setRemoveGood();
    }

    @Override public void setRemoveFigure() {
        nextCardController.setRemoveFigure();
    }

    @Override public void updatePlayerParametres(FlightBoard flightBoard) {
        for(Player player : flightBoard.getPlayersMap().keySet()) {
            if(player.getId().equals(clientId)) {
                nextCardController.updatePlayerParametres(flightBoard, player);
            }
        }
    }

    @Override public void showWinner(ArrayList<Player> players, boolean winner) {
        Platform.runLater(() -> {
            if (winnerPane == null) {
                try {
                    FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("/fxml/winner.fxml"));
                    if (connectionLoader.getLocation() == null) {
                        throw new IOException("connection.fxml not found");
                    }
                    connectionLoader.setController(winnerController);
                    winnerPane = connectionLoader.load();
                    rootPane.getChildren().add(winnerPane);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Errore nel caricamento di connection.fxml: " + e.getMessage());
                    alert.showAndWait();
                    return;
                }
            }
            showPane(winnerPane);
            primaryStage.show();
        });
        winnerController.showWinner(players, winner);
    }

    @Override public void disconnected(String disconnectionError) {
        System.out.print("\n\n" + Font.RED + disconnectionError + Font.RESET);

        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void showTiles(GameTile gameTile) {
        if (!showTilesController.isViewingSpecificTile() && !showTilesController.isViewingShips() && !showTilesController.isViewingCardsPiles()) {
            this.flightType = gameTile.getFlightType();
            showTilesController.setFlightType(gameTile.getFlightType());

            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                if (showTilesPane == null) {
                    try {
                        if (waitingPlayersController != null) {
                            try {
                                waitingPlayersController.stopSpinner();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            waitingPlayersController = null;
                        }
                        if (waitingPlayersPane != null) {
                            rootPane.getChildren().remove(waitingPlayersPane);
                            waitingPlayersPane = null;
                            rootPane.getChildren().clear();
                            rootPane = new StackPane();
                            mainScene = new Scene(rootPane, 800, 600);
                            primaryStage.setScene(mainScene);
                        }

                        if (showTilesPane == null) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/showTiles.fxml"));
                                if (loader.getLocation() == null) {
                                    throw new IOException("showTiles.fxml non found");
                                }
                                loader.setController(showTilesController);
                                showTilesPane = loader.load();
                                rootPane.getChildren().add(showTilesPane);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.showAndWait();
                                return;
                            }
                        }
                        showPane(showTilesPane);
                        showTilesController.createButtons(gameTile.getFlightType());
                        showTilesController.displayTiles(gameTile);

                        if (gameTile.getFlightType() == FlightType.STANDARD_FLIGHT) {
                            showTilesController.createCardPiles();
                        }
                        primaryStage.show();
                    } finally {
                        latch.countDown();
                    }
                } else {
                    showTilesController.displayTiles(gameTile);

                    showPane(showTilesPane);
                    primaryStage.show();
                }
            });
            this.showTilesLatch = latch;
        }
    }
    private volatile CountDownLatch showTilesLatch;

    @Override public void showComponentTile(ComponentTile componentTile) {
        Platform.runLater(() -> {
            showTilesController.showSpecificTile(componentTile);
        });
    }

    @Override public void showShipErrors(ArrayList<String> errors) {
        if (showTilesPane != null && showTilesPane.isVisible()) {
            showTilesController.showShipErrors(errors);
        }
        if (nextCardPane != null && nextCardPane.isVisible()) {
            nextCardController.showShipErrors(errors);
        }
    }

    @Override public void showGenericMessage(String message) {
        if(message.equals("Added to the game. Waiting for other players to start the game...")) {
            Platform.runLater(() -> {
                if (waitingPlayersPane == null) {
                    try {
                        FXMLLoader connectionLoader = new FXMLLoader(getClass().getResource("/fxml/waitingPlayers.fxml"));
                        if (connectionLoader.getLocation() == null) {
                            throw new IOException("connection.fxml not found");
                        }
                        connectionLoader.setController(waitingPlayersController);
                        waitingPlayersPane = connectionLoader.load();
                        rootPane.getChildren().add(waitingPlayersPane);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Errore nel caricamento di connection.fxml: " + e.getMessage());
                        alert.showAndWait();
                        return;
                    }
                }
                showPane(waitingPlayersPane);
            });
        }
        else if (message.equals("Your ship board is correct. You have to wait the other players to finish correcting their ship.") || message.equals("Waiting for other players to finish building their ships.")) {
            showTilesController.showWaitPlayers(message);
        }
        else if (message.equals("Waiting for other players to finish populate their ships.")) {
            showTilesController.showWaitPopulate(message);
        }
        else if(message.equals("You are not in the flight anymore. Watch the other player!!")) {
            nextCardController.showRetireMessage(message);
        }
        else if(message.startsWith("The dice rolled returned") && message.endsWith("Your ship board is not hit.")) {
            nextCardController.showMeteorRoll(message);
        }
        else if(message.equals("\nYour ship board is correct, but there are other players that need to fix their ship. You have to wait the other players...")) {
            nextCardController.showShipCorrect(message);
        }
        else if(message.equals("Waiting for other players to finish repairing their ships.")) {
            nextCardController.showWaitRepaired(message);
        }
    }

    @Override public void finishBuilding() {
        showTilesController.finishBuilding();
    }

    @Override public void clearPage() {}
    @Override public void sendMessageToClient(String clientId, Message message) {}

    /**
     * Method used to launch JavaFX
     * @param args are the arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Method used to show the current pane.
     * @param paneToShow is the pane that has to be shown.
     */
    private void showPane(StackPane paneToShow) {
        for (Node node : rootPane.getChildren()) {
            node.setVisible(false);
        }
        paneToShow.setVisible(true);
    }

    /**
     * Method used to set the nickname.
     * @param nickname is the nickname that has to be set.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}