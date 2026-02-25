package view.gui.SceneController;

import enumerations.FlightType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;

import javafx.scene.input.MouseEvent;
import support.GameInfo;
import support.Quadruple;
import view.gui.Gui;

import java.util.HashMap;

/**
 * This class represents the game Controller, which controls the scenes containing the create game button or join game button.
 */
public class ShowGameController {
    @FXML private StackPane rootPane;
    @FXML private Pane blackBackground;
    @FXML private ImageView backgroundImage;
    @FXML private StackPane uiPane;
    @FXML private ImageView logoImage;
    @FXML private ComboBox flightComboBox;
    @FXML private ComboBox numPlayersComboBox;
    @FXML private Button createGameButton;
    @FXML private Button refreshGameButton;
    @FXML private Label errorLabel;
    @FXML private Label errorLabel2;
    @FXML private GridPane gridPane;
    @FXML private ListView<GameInfo> gameListView;

    private final Gui gui;

    public ShowGameController(Gui gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel2.setVisible(false);

        Platform.runLater(() -> rootPane.requestFocus());

        try {
            String bgPath = "/images/lobby_bg.jpg";
            Image bgImage = new Image(getClass().getResourceAsStream(bgPath));
            backgroundImage.setImage(bgImage);

            String logoPath = "/images/galaxy_logo.png";
            Image logo = new Image(getClass().getResourceAsStream(logoPath));
            logoImage.setImage(logo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        flightComboBox.setMaxWidth(Double.MAX_VALUE);
        flightComboBox.getItems().addAll("First flight", "Standard flight");

        numPlayersComboBox.setMaxWidth(Double.MAX_VALUE);
        numPlayersComboBox.getItems().addAll("2", "3", "4");

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setManaged(false);

        logoImage.setPreserveRatio(true);
        logoImage.setManaged(false);

        Platform.runLater(() -> {
            updateLayout();
        });

        createGameButton.setOnAction(e -> {
            String flightType = (String) flightComboBox.getValue();
            String numPlayers = (String) numPlayersComboBox.getValue();

            if(flightType == null || numPlayers == null) {
                errorLabel.setVisible(true);
                errorLabel.setText("You have to select a flight type and a number of players!");
            }
            else {
                if(flightType.equals("First flight")) {
                    gui.notifyObserver(obs -> obs.createGame());
                    gui.notifyObserver(viewObserver -> viewObserver.onUpdateMaxPlayerAndFlightType(Integer.parseInt(numPlayers), FlightType.FIRST_FLIGHT));
                }
                else {
                    gui.notifyObserver(obs -> obs.createGame());
                    gui.notifyObserver(viewObserver -> viewObserver.onUpdateMaxPlayerAndFlightType(Integer.parseInt(numPlayers), FlightType.STANDARD_FLIGHT));
                }
            }
        });

        refreshGameButton.setOnAction(e -> {
            gui.notifyObserver(obs1 -> obs1.refreshGameOnServer());
        });

        gameListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                gui.notifyObserver(obs2 -> obs2.joinGame(newVal.getGameId()));
            }
        });

        gameListView.setCellFactory(param -> new ListCell<GameInfo>() {
            @Override
            protected void updateItem(GameInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    vbox.setSpacing(5);
                    vbox.getStyleClass().add("game-cell");

                    HBox infoRow = new HBox();
                    infoRow.getStyleClass().add("info-row");

                    Label gameIdLabel = new Label(item.getGameId());
                    gameIdLabel.getStyleClass().add("info-label");
                    HBox.setMargin(gameIdLabel, new Insets(0, 30, 0, 0));

                    Label flightTypeLabel = new Label(item.getFlightType().toString());
                    flightTypeLabel.getStyleClass().add("info-label1");
                    HBox.setMargin(flightTypeLabel, new Insets(0, 30, 0, 0));

                    Label numPlayersLabel = new Label("" + item.getNumConnectedPlayers());
                    numPlayersLabel.getStyleClass().add("info-label2");

                    Label numMaxPlayersLabel = new Label("" +item.getNumMaxPlayers());
                    numMaxPlayersLabel.getStyleClass().add("info-label3");

                    infoRow.getChildren().addAll(gameIdLabel, flightTypeLabel, numPlayersLabel, numMaxPlayersLabel);

                    HBox playersRow = new HBox();
                    playersRow.setSpacing(10);
                    playersRow.getStyleClass().add("players-row");

                    Label playersLabel = new Label("Connected Players: ");
                    playersLabel.getStyleClass().add("players-label");

                    Label playersNames = new Label(String.join(", ", item.getConnectedPlayers()));
                    playersNames.getStyleClass().add("players-names");

                    playersRow.getChildren().addAll(playersLabel, playersNames);

                    vbox.getChildren().addAll(infoRow, playersRow);
                    setGraphic(vbox);
                }
            }
        });
    }


    public void updateGameList(HashMap<String, Quadruple<FlightType, Integer, Integer, ArrayList<String>>> games) {
        ArrayList<GameInfo> gameInfos = new ArrayList<>();

        if(games != null) {
            for (String game: games.keySet()) {
                String gameId = game;
                Quadruple<FlightType, Integer, Integer, ArrayList<String>> value = games.get(game);
                FlightType flightType = value.getFirst();
                int numPlayers = value.getSecond();
                int maxPlayers = value.getThird();
                ArrayList<String> connectedPlayers = value.getFourth();
                gameInfos.add(new GameInfo(gameId, flightType, numPlayers, maxPlayers, connectedPlayers));
            }
        }

        Platform.runLater(() -> {
            gameListView.getItems().setAll(gameInfos);
        });
    }


    private void updateLayout() {
        double windowWidth = rootPane.getWidth();
        double windowHeight = rootPane.getHeight();

        double imgWidth = backgroundImage.getImage().getWidth();
        double imgHeight = backgroundImage.getImage().getHeight();
        double scale = Math.min(windowWidth / imgWidth, windowHeight / imgHeight);
        double scaledWidth = imgWidth * scale;
        double scaledHeight = imgHeight * scale;

        backgroundImage.setFitWidth(scaledWidth);
        backgroundImage.setFitHeight(scaledHeight);
        backgroundImage.setX((windowWidth - scaledWidth) / 2);
        backgroundImage.setY((windowHeight - scaledHeight) / 2);

        double inputWidth = scaledWidth * 0.4;

        gridPane.setPrefWidth(windowWidth * 0.7);
        gridPane.setMaxWidth(windowWidth * 0.7);
        gridPane.setPrefHeight(windowHeight * 0.7);
        gridPane.setMaxHeight(windowHeight * 0.7);

        double logoWidth = scaledWidth * 0.3;
        logoImage.setFitWidth(logoWidth);
        double logoX = (windowWidth - logoWidth) / 2 - 0.25*windowWidth * 0.7;
        double logoY = (windowHeight - scaledHeight) / 2 + scaledHeight * 0.21;
        logoImage.setLayoutX(logoX);
        logoImage.setLayoutY(logoY);

        rootPane.requestLayout();
    }


    public void setErrorLabel2(String error) {
        errorLabel2.setText(error);
        errorLabel2.setVisible(true);
        updateLayout();
    }
}