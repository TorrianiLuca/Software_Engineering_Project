package view.gui.SceneController;

import enumerations.FlightType;
import enumerations.TileName;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import javafx.scene.input.MouseEvent;
import model.card.Card;
import model.card.CardPile;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.tiles.componentTile.Battery;
import model.tiles.componentTile.Cabine;
import model.tiles.componentTile.Cargo;
import model.tiles.componentTile.StartingCabine;
import support.GameInfo;
import support.Quadruple;
import view.gui.Gui;
import view.tui.Font;
import view.tui.Tui;

import java.util.HashMap;
import java.util.Stack;

/**
 * This class represents the show tile Controller, which controls the first section of the game scenes.
 * The first section of the game starts when the game starts, and ends when the players have finished building their ships.
 */
public class ShowTilesController {
    @FXML private StackPane rootPane;
    @FXML private Pane blackBackground;
    @FXML private ImageView backgroundImage;
    @FXML private StackPane uiPane;
    @FXML private StackPane bottomPane;
    @FXML private StackPane bottomPane1;
    @FXML private StackPane bottomPane2;
    @FXML private GridPane imageGrid;
    @FXML private GridPane buttonGrid;
    @FXML private GridPane shipGrid;
    @FXML private StackPane section2;
    @FXML private StackPane section3;
    @FXML private Label errorShipLabel;
    @FXML private StackPane section21;
    @FXML private StackPane section22;
    @FXML private StackPane section23;
    @FXML private ColumnConstraints buttonBack;
    @FXML private GridPane gridPane;
    @FXML private GridPane gridPane1;
    @FXML private Label timerLabel;
    @FXML private Label positionsLabel;
    int remainingPositions=2;

    private GridPane tempGrid;
    private StackPane tilesPane;
    private GridPane shipsPane = new GridPane();
    private Label label5 = new Label();

    private GameTile gameTile;
    private FlightType flightType;
    private ShipBoard shipBoard;

    private boolean isViewingSpecificTile = false;
    private boolean fromShip = false;
    private boolean isViewingShips = false;
    private boolean isViewingCardsPiles = false;
    private boolean hasFinishedBuilding = false;
    private boolean repair = false;
    private boolean finish = false;
    private boolean populating = false;

    private String figure;

    private final Gui gui;

    public ShowTilesController(Gui gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> rootPane.requestFocus());

        tilesPane = new StackPane();
        tilesPane.setAlignment(Pos.CENTER);
        gridPane1.getChildren().add(tilesPane);

        try {
            String bgPath = "/images/bg.jpg";
            Image bgImage = new Image(getClass().getResourceAsStream(bgPath));
            backgroundImage.setImage(bgImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setManaged(false);

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0 && rootPane.getHeight() > 0) {
                updateLayout();
                displayTiles(gameTile);
            }
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0 && rootPane.getWidth() > 0) {
                updateLayout();
                displayTiles(gameTile);
            }
        });

        Platform.runLater(() -> {
            if (rootPane.getWidth() > 0 && rootPane.getHeight() > 0) {
                updateLayout();
                displayTiles(gameTile);
            }
        });
    }


    public void createButtons(FlightType flightType) {
        if(flightType == FlightType.FIRST_FLIGHT) {
            timerLabel.setVisible(false);
            positionsLabel.setVisible(false);
        }

        Platform.runLater(() -> {
            if(flightType == FlightType.FIRST_FLIGHT) {
                buttonGrid.getChildren().clear();

                Button button1 = new Button("Show ships");
                button1.getStyleClass().add("my-button1");
                GridPane.setHgrow(button1, Priority.ALWAYS);
                GridPane.setVgrow(button1, Priority.ALWAYS);
                GridPane.setFillWidth(button1, true);
                GridPane.setFillHeight(button1, true);
                GridPane.setValignment(button1, VPos.BOTTOM);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    errorShipLabel.setVisible(false);
                    isViewingShips = true;
                    gui.notifyObserver(obs -> obs.onUpdateShowShips());
                });
                buttonGrid.add(button1, 0, 0);

                Button button2 = new Button("Finished build");
                button2.getStyleClass().add("my-button2");
                GridPane.setHgrow(button2, Priority.ALWAYS);
                GridPane.setVgrow(button2, Priority.ALWAYS);
                GridPane.setFillWidth(button2, true);
                GridPane.setFillHeight(button2, true);
                GridPane.setValignment(button2, VPos.BOTTOM);
                button2.setMaxWidth(Double.MAX_VALUE);
                button2.setOnAction(event -> {
                    errorShipLabel.setVisible(false);
                    isViewingShips = true;
                    hasFinishedBuilding = true;
                    gui.notifyObserver(obs->obs.onUpdateFinishedBuild());
                });
                buttonGrid.add(button2, 1, 0);
            }
            else {
                buttonGrid.getChildren().clear();

                Button button1 = new Button("Turn timer");
                button1.getStyleClass().add("my-button1");
                GridPane.setHgrow(button1, Priority.ALWAYS);
                GridPane.setVgrow(button1, Priority.ALWAYS);
                GridPane.setFillWidth(button1, true);
                GridPane.setFillHeight(button1, true);
                GridPane.setValignment(button1, VPos.BOTTOM);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    errorShipLabel.setVisible(false);
                    gui.notifyObserver(viewObserver -> viewObserver.onUpdateTimerMessage());
                });
                buttonGrid.add(button1, 0, 0);

                Button button2 = new Button("Show ships");
                button2.getStyleClass().add("my-button1");
                GridPane.setHgrow(button2, Priority.ALWAYS);
                GridPane.setVgrow(button2, Priority.ALWAYS);
                GridPane.setFillWidth(button2, true);
                GridPane.setFillHeight(button2, true);
                GridPane.setValignment(button2, VPos.BOTTOM);
                button2.setMaxWidth(Double.MAX_VALUE);
                button2.setOnAction(event -> {
                    if(this.remainingPositions == 2) {
                        errorShipLabel.setVisible(true);
                        errorShipLabel.setText("You have to turn the timer before taking any action");
                    }
                    else {
                        errorShipLabel.setVisible(false);
                        isViewingShips = true;
                        gui.notifyObserver(obs -> obs.onUpdateShowShips());

                    }
                });
                buttonGrid.add(button2, 1, 0);

                Button button3 = new Button("Finished build");
                button3.getStyleClass().add("my-button2");
                GridPane.setHgrow(button3, Priority.ALWAYS);
                GridPane.setVgrow(button3, Priority.ALWAYS);
                GridPane.setFillWidth(button3, true);
                GridPane.setFillHeight(button3, true);
                GridPane.setValignment(button3, VPos.BOTTOM);
                button3.setMaxWidth(Double.MAX_VALUE);
                button3.setOnAction(event -> {
                    if(this.remainingPositions == 2) {
                        errorShipLabel.setVisible(true);
                        errorShipLabel.setText("You have to turn the timer before taking any action");
                    }
                    else {
                        errorShipLabel.setVisible(false);
                        isViewingShips = true;
                        hasFinishedBuilding = true;
                        gui.notifyObserver(obs->obs.onUpdateFinishedBuild());
                    }
                });
                buttonGrid.add(button3, 2, 0);
            }
        });
    }

    public void createCardPiles() {
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                section21.getChildren().clear();
                section22.getChildren().clear();
                section22.getChildren().clear();

                double cellWidth21 = (section21.getWidth());
                double cellHeight21 = (section21.getHeight());
                double cellWidth22 = (section22.getWidth());
                double cellHeight22 = (section22.getHeight());
                double cellWidth23 = (section23.getWidth());
                double cellHeight23 = (section23.getHeight());

                StackPane cellContainer21 = new StackPane();
                cellContainer21.setMaxSize(cellHeight21, cellWidth21);
                cellContainer21.setPrefSize(cellHeight21, cellWidth21);

                StackPane cellContainer22 = new StackPane();
                cellContainer22.setMaxSize(cellHeight22, cellWidth22);
                cellContainer22.setPrefSize(cellHeight22, cellWidth22);

                StackPane cellContainer23 = new StackPane();
                cellContainer23.setMaxSize(cellHeight23, cellWidth23);
                cellContainer23.setPrefSize(cellHeight23, cellWidth23);

                ImageView imageView211, imageView212, imageView213, imageView221, imageView222, imageView223, imageView231, imageView232, imageView233;
                Image cardImage211, cardImage212, cardImage213, cardImage221, cardImage222, cardImage223, cardImage231, cardImage232, cardImage233;

                cardImage211 = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                imageView211 = new ImageView(cardImage211);
                cardImage221 = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                imageView221 = new ImageView(cardImage211);
                cardImage231 = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                imageView231 = new ImageView(cardImage211);

                double ratio211 = cardImage211.getWidth() / cardImage211.getHeight();
                double ratio221 = cardImage221.getWidth() / cardImage221.getHeight();
                double ratio231 = cardImage231.getWidth() / cardImage231.getHeight();

                imageView211.setFitHeight(cellHeight21);
                imageView211.setFitWidth(cellHeight21*ratio211);
                imageView211.setPreserveRatio(true);
                imageView211.setSmooth(true);
                imageView211.setTranslateX(20);
                imageView221.setFitHeight(cellHeight21);
                imageView221.setFitWidth(cellHeight21*ratio221);
                imageView221.setPreserveRatio(true);
                imageView221.setSmooth(true);
                imageView221.setTranslateX(20);
                imageView231.setFitHeight(cellHeight21);
                imageView231.setFitWidth(cellHeight21*ratio231);
                imageView231.setPreserveRatio(true);
                imageView231.setSmooth(true);
                imageView231.setTranslateX(20);

                Rectangle clip211 = new Rectangle(cellHeight21*ratio211, cellHeight21);
                clip211.setArcWidth(20.0);
                clip211.setArcHeight(20.0);
                imageView211.setClip(clip211);
                Rectangle clip221 = new Rectangle(cellHeight22*ratio221, cellHeight22);
                clip221.setArcWidth(20.0);
                clip221.setArcHeight(20.0);
                imageView221.setClip(clip221);
                Rectangle clip231 = new Rectangle(cellHeight23*ratio231, cellHeight23);
                clip231.setArcWidth(20.0);
                clip231.setArcHeight(20.0);
                imageView231.setClip(clip231);

                StackPane borderContainer211 = new StackPane();
                borderContainer211.getStyleClass().add("cell-border1");
                borderContainer211.setMaxHeight(cellHeight21);
                borderContainer211.setMaxWidth(cellHeight21*ratio211);
                borderContainer211.setTranslateX(20);
                StackPane borderContainer221 = new StackPane();
                borderContainer221.getStyleClass().add("cell-border1");
                borderContainer221.setMaxHeight(cellHeight21);
                borderContainer221.setMaxWidth(cellHeight21*ratio221);
                borderContainer221.setTranslateX(20);
                StackPane borderContainer231 = new StackPane();
                borderContainer231.getStyleClass().add("cell-border1");
                borderContainer231.setMaxHeight(cellHeight21);
                borderContainer231.setMaxWidth(cellHeight21*ratio231);
                borderContainer231.setTranslateX(20);

                VBox cellContainer211 = new VBox(borderContainer211);
                cellContainer211.setMaxHeight(cellHeight21);
                cellContainer211.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer211, Priority.ALWAYS);
                VBox.setVgrow(borderContainer211, Priority.ALWAYS);
                VBox cellContainer221 = new VBox(borderContainer221);
                cellContainer221.setMaxHeight(cellHeight21);
                cellContainer221.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer221, Priority.ALWAYS);
                VBox.setVgrow(borderContainer221, Priority.ALWAYS);
                VBox cellContainer231 = new VBox(borderContainer231);
                cellContainer231.setMaxHeight(cellHeight21);
                cellContainer231.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer231, Priority.ALWAYS);
                VBox.setVgrow(borderContainer231, Priority.ALWAYS);

                cardImage212 = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                imageView212 = new ImageView(cardImage211);
                cardImage222 = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                imageView222 = new ImageView(cardImage211);
                cardImage232 = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                imageView232 = new ImageView(cardImage211);

                double ratio212 = cardImage212.getWidth() / cardImage212.getHeight();
                double ratio222 = cardImage222.getWidth() / cardImage222.getHeight();
                double ratio232 = cardImage232.getWidth() / cardImage232.getHeight();

                imageView212.setFitHeight(cellHeight21);
                imageView212.setFitWidth(cellHeight21*ratio212);
                imageView212.setPreserveRatio(true);
                imageView212.setSmooth(true);
                imageView222.setFitHeight(cellHeight21);
                imageView222.setFitWidth(cellHeight21*ratio222);
                imageView222.setPreserveRatio(true);
                imageView222.setSmooth(true);
                imageView232.setFitHeight(cellHeight21);
                imageView232.setFitWidth(cellHeight21*ratio232);
                imageView232.setPreserveRatio(true);
                imageView232.setSmooth(true);

                Rectangle clip212 = new Rectangle(cellHeight21*ratio212, cellHeight21);
                clip212.setArcWidth(20.0);
                clip212.setArcHeight(20.0);
                imageView212.setClip(clip212);
                Rectangle clip222 = new Rectangle(cellHeight22*ratio222, cellHeight22);
                clip222.setArcWidth(20.0);
                clip222.setArcHeight(20.0);
                imageView222.setClip(clip222);
                Rectangle clip232 = new Rectangle(cellHeight23*ratio232, cellHeight23);
                clip232.setArcWidth(20.0);
                clip232.setArcHeight(20.0);
                imageView232.setClip(clip232);

                StackPane borderContainer212 = new StackPane();
                borderContainer212.getStyleClass().add("cell-border1");
                borderContainer212.setMaxHeight(cellHeight21);
                borderContainer212.setMaxWidth(cellHeight21*ratio211);
                StackPane borderContainer222 = new StackPane();
                borderContainer222.getStyleClass().add("cell-border1");
                borderContainer222.setMaxHeight(cellHeight21);
                borderContainer222.setMaxWidth(cellHeight21*ratio222);
                StackPane borderContainer232 = new StackPane();
                borderContainer232.getStyleClass().add("cell-border1");
                borderContainer232.setMaxHeight(cellHeight21);
                borderContainer232.setMaxWidth(cellHeight21*ratio232);

                VBox cellContainer212 = new VBox(borderContainer212);
                cellContainer212.setMaxHeight(cellHeight21);
                cellContainer212.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer212, Priority.ALWAYS);
                VBox.setVgrow(borderContainer212, Priority.ALWAYS);
                VBox cellContainer222 = new VBox(borderContainer222);
                cellContainer222.setMaxHeight(cellHeight21);
                cellContainer222.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer222, Priority.ALWAYS);
                VBox.setVgrow(borderContainer222, Priority.ALWAYS);
                VBox cellContainer232 = new VBox(borderContainer232);
                cellContainer232.setMaxHeight(cellHeight21);
                cellContainer232.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer232, Priority.ALWAYS);
                VBox.setVgrow(borderContainer232, Priority.ALWAYS);

                cardImage213 = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                imageView213 = new ImageView(cardImage213);
                cardImage223 = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                imageView223 = new ImageView(cardImage213);
                cardImage233 = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                imageView233 = new ImageView(cardImage213);

                double ratio213 = cardImage213.getWidth() / cardImage213.getHeight();
                double ratio223 = cardImage223.getWidth() / cardImage223.getHeight();
                double ratio233 = cardImage233.getWidth() / cardImage233.getHeight();

                imageView213.setFitHeight(cellHeight21);
                imageView213.setFitWidth(cellHeight21*ratio213);
                imageView213.setPreserveRatio(true);
                imageView213.setSmooth(true);
                imageView213.setTranslateX(-20);
                imageView223.setFitHeight(cellHeight21);
                imageView223.setFitWidth(cellHeight21*ratio223);
                imageView223.setPreserveRatio(true);
                imageView223.setSmooth(true);
                imageView223.setTranslateX(-20);
                imageView233.setFitHeight(cellHeight21);
                imageView233.setFitWidth(cellHeight21*ratio233);
                imageView233.setPreserveRatio(true);
                imageView233.setSmooth(true);
                imageView233.setTranslateX(-20);

                Rectangle clip213 = new Rectangle(cellHeight21*ratio213, cellHeight21);
                clip213.setArcWidth(20.0);
                clip213.setArcHeight(20.0);
                imageView213.setClip(clip213);
                Rectangle clip223 = new Rectangle(cellHeight22*ratio223, cellHeight22);
                clip223.setArcWidth(20.0);
                clip223.setArcHeight(20.0);
                imageView223.setClip(clip223);
                Rectangle clip233 = new Rectangle(cellHeight23*ratio233, cellHeight23);
                clip233.setArcWidth(20.0);
                clip233.setArcHeight(20.0);
                imageView233.setClip(clip233);

                StackPane borderContainer213 = new StackPane();
                borderContainer213.getStyleClass().add("cell-border1");
                borderContainer213.setMaxHeight(cellHeight21);
                borderContainer213.setMaxWidth(cellHeight21*ratio211);
                borderContainer213.setTranslateX(-20);
                StackPane borderContainer223 = new StackPane();
                borderContainer223.getStyleClass().add("cell-border1");
                borderContainer223.setMaxHeight(cellHeight21);
                borderContainer223.setMaxWidth(cellHeight21*ratio223);
                borderContainer223.setTranslateX(-20);
                StackPane borderContainer233 = new StackPane();
                borderContainer233.getStyleClass().add("cell-border1");
                borderContainer233.setMaxHeight(cellHeight21);
                borderContainer233.setMaxWidth(cellHeight21*ratio233);
                borderContainer233.setTranslateX(-20);

                VBox cellContainer213 = new VBox(borderContainer213);
                cellContainer213.setMaxHeight(cellHeight21);
                cellContainer213.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer213, Priority.ALWAYS);
                VBox.setVgrow(borderContainer213, Priority.ALWAYS);
                VBox cellContainer223 = new VBox(borderContainer223);
                cellContainer223.setMaxHeight(cellHeight21);
                cellContainer223.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer223, Priority.ALWAYS);
                VBox.setVgrow(borderContainer223, Priority.ALWAYS);
                VBox cellContainer233 = new VBox(borderContainer233);
                cellContainer233.setMaxHeight(cellHeight21);
                cellContainer233.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer233, Priority.ALWAYS);
                VBox.setVgrow(borderContainer233, Priority.ALWAYS);

                StackPane borderContainer214 = new StackPane();
                borderContainer214.getStyleClass().add("cell-border2");
                borderContainer214.setMaxHeight(cellHeight21);
                borderContainer214.setMaxWidth(cellHeight21*ratio211 + 20 + 20);
                StackPane borderContainer224 = new StackPane();
                borderContainer224.getStyleClass().add("cell-border2");
                borderContainer224.setMaxHeight(cellHeight21);
                borderContainer224.setMaxWidth(cellHeight21*ratio221 + 20 + 20);
                StackPane borderContainer234 = new StackPane();
                borderContainer234.getStyleClass().add("cell-border2");
                borderContainer234.setMaxHeight(cellHeight21);
                borderContainer234.setMaxWidth(cellHeight21*ratio231 + 20 + 20);

                VBox cellContainer214 = new VBox(borderContainer214);
                cellContainer214.setMaxHeight(cellHeight21);
                cellContainer214.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer214, Priority.ALWAYS);
                VBox.setVgrow(borderContainer214, Priority.ALWAYS);
                VBox cellContainer224 = new VBox(borderContainer224);
                cellContainer224.setMaxHeight(cellHeight21);
                cellContainer224.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer224, Priority.ALWAYS);
                VBox.setVgrow(borderContainer224, Priority.ALWAYS);
                VBox cellContainer234 = new VBox(borderContainer234);
                cellContainer234.setMaxHeight(cellHeight21);
                cellContainer234.setAlignment(Pos.CENTER);
                GridPane.setHgrow(cellContainer234, Priority.ALWAYS);
                VBox.setVgrow(borderContainer234, Priority.ALWAYS);

                cellContainer214.setOnMouseClicked(event -> {
                    if(this.remainingPositions == 2) {
                        errorShipLabel.setVisible(true);
                        errorShipLabel.setText("You have to turn the timer before taking any action");
                    }
                    else {
                        errorShipLabel.setVisible(false);
                        gui.notifyObserver(obs->obs.onUpdatePickCardPile(1));
                    }
                });
                cellContainer224.setOnMouseClicked(event -> {
                    if(this.remainingPositions == 2) {
                        errorShipLabel.setVisible(true);
                        errorShipLabel.setText("You have to turn the timer before taking any action");
                    }
                    else {
                        errorShipLabel.setVisible(false);
                        gui.notifyObserver(obs->obs.onUpdatePickCardPile(2));
                    }
                });
                cellContainer234.setOnMouseClicked(event -> {
                    if(this.remainingPositions == 2) {
                        errorShipLabel.setVisible(true);
                        errorShipLabel.setText("You have to turn the timer before taking any action");
                    }
                    else {
                        errorShipLabel.setVisible(false);
                        gui.notifyObserver(obs->obs.onUpdatePickCardPile(3));
                    }
                });

                cellContainer21.getChildren().addAll(imageView211, cellContainer211, imageView212, cellContainer212, imageView213, cellContainer213, cellContainer214);
                cellContainer21.setAlignment(Pos.CENTER);
                cellContainer22.getChildren().addAll(imageView221, cellContainer221, imageView222, cellContainer222, imageView223, cellContainer223, cellContainer224);
                cellContainer22.setAlignment(Pos.CENTER);
                cellContainer23.getChildren().addAll(imageView231, cellContainer231, imageView232, cellContainer232, imageView233, cellContainer233, cellContainer234);
                cellContainer23.setAlignment(Pos.CENTER);

                section21.getChildren().clear();
                section21.getChildren().add(cellContainer21);
                section21.setAlignment(Pos.CENTER);

                section22.getChildren().clear();
                section22.getChildren().add(cellContainer22);
                section22.setAlignment(Pos.CENTER);

                section23.getChildren().clear();
                section23.getChildren().add(cellContainer23);
                section23.setAlignment(Pos.CENTER);
            });
        });
    }


    public void updateLayout() {
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

        double windowWidth1 = backgroundImage.getFitWidth();
        double windowHeight1 = backgroundImage.getFitHeight();

        gridPane.setPrefWidth(windowWidth1 * 0.98);
        gridPane.setMaxWidth(windowWidth1 * 0.98);
        gridPane.setPrefHeight(windowHeight1);
        gridPane.setMaxHeight(windowHeight1);

        rootPane.requestLayout();

        timerLabel.setText("Timer: 0 seconds remaining");
        positionsLabel.setText("Remaining positions: " + this.remainingPositions);
        timerLabel.getStyleClass().add("my-timer1");
        positionsLabel.getStyleClass().add("my-timer1");

        GridPane.setHgrow(timerLabel, Priority.ALWAYS);
        GridPane.setVgrow(timerLabel, Priority.ALWAYS);
        GridPane.setFillWidth(timerLabel, true);
        GridPane.setFillHeight(timerLabel, true);
        GridPane.setValignment(timerLabel, VPos.TOP);
        timerLabel.setMaxWidth(Double.MAX_VALUE);

        GridPane.setHgrow(positionsLabel, Priority.ALWAYS);
        GridPane.setVgrow(positionsLabel, Priority.ALWAYS);
        GridPane.setFillWidth(positionsLabel, true);
        GridPane.setFillHeight(positionsLabel, true);
        GridPane.setValignment(positionsLabel, VPos.TOP);
        positionsLabel.setMaxWidth(Double.MAX_VALUE);

        errorShipLabel.setVisible(false);
    }


    public void displayTiles(GameTile tiles) {
        Platform.runLater(() -> {
            this.gameTile = tiles;
            if(gameTile == null) {
                return;
            }

            if (rootPane.getWidth() < 500  || rootPane.getWidth() > 1700) {
                Platform.runLater(() -> {
                    if (rootPane.getWidth() > 500 && rootPane.getWidth() < 1700) {
                        updateLayout();
                        displayTiles(gameTile);
                    }
                });
                return;
            }

            this.createButtons(flightType);

            gridPane1.setVisible(true);
            section2.setVisible(true);
            section3.setVisible(true);
            tilesPane.setVisible(false);

            if(tiles.getFlightType() == FlightType.FIRST_FLIGHT) {
                double upperHalfHeight = (gridPane.getPrefHeight() * 0.432);
                double cellWidth = (gridPane.getPrefWidth() - gridPane.getHgap() * 26 - gridPane.getPadding().getLeft() - gridPane.getPadding().getRight()) / 27;
                double cellHeight = (upperHalfHeight - gridPane.getVgap() * 5 - gridPane.getPadding().getTop() - gridPane.getPadding().getBottom()) / 6;
                double val = Math.min(cellWidth, cellHeight);

                gridPane1.getChildren().clear();

                int k = 0;
                for (int i = 0; i < 6 && k < 140; i++) {
                    for (int j = 0; j < 27 && k < 140; j++) {
                        ComponentTile componentTile = tiles.getComponentTile(k);

                        if (componentTile == null) {
                            k++;
                        }
                        else {
                            StackPane cellContainer = new StackPane();
                            cellContainer.setMaxSize(val, val);
                            cellContainer.setPrefSize(val, val);
                            cellContainer.setAlignment(Pos.CENTER);

                            StackPane tileContainer;

                            if(componentTile.isFaceDown()) {
                                Image image = new Image(getClass().getResourceAsStream("/images/tiles/157.jpg"));
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(val);
                                imageView.setFitHeight(val);

                                tileContainer = new StackPane();
                                tileContainer.getChildren().add(imageView);

                                Rectangle clip = new Rectangle(val, val);
                                clip.setArcWidth(10.0);
                                clip.setArcHeight(10.0);
                                tileContainer.setClip(clip);
                            }
                            else {
                                tileContainer = createTileImage(componentTile, val);
                            }

                            StackPane borderContainer = new StackPane();
                            borderContainer.getStyleClass().add("cell-border");
                            borderContainer.setMaxSize(val, val);
                            borderContainer.setPrefSize(val, val);

                            Rectangle borderClip = new Rectangle(val, val);
                            borderClip.setArcWidth(10.0);
                            borderClip.setArcHeight(10.0);
                            borderContainer.setClip(borderClip);

                            cellContainer.getChildren().addAll(tileContainer, borderContainer);

                            int tileId = k;
                            cellContainer.setOnMouseClicked(event -> {
                                gui.notifyObserver(obs->obs.onUpdatePickTile(tileId));
                                errorShipLabel.setVisible(false);
                            });

                            gridPane1.add(cellContainer, j, i);
                            k++;
                        }
                    }
                }
            }
            else {
                double upperHalfHeight = (gridPane.getPrefHeight() * 0.432);
                double cellWidth = (gridPane.getPrefWidth() - gridPane.getHgap() * 26 - gridPane.getPadding().getLeft() - gridPane.getPadding().getRight()) / 27;
                double cellHeight = (upperHalfHeight - gridPane.getVgap() * 5 - gridPane.getPadding().getTop() - gridPane.getPadding().getBottom()) / 6;
                double val = Math.min(cellWidth, cellHeight);

                gridPane1.getChildren().clear();

                int k = 0;
                for (int i = 0; i < 6 && k < 152; i++) {
                    for (int j = 0; j < 27 && k < 152; j++) {
                        ComponentTile componentTile = tiles.getComponentTile(k);

                        if (componentTile == null) {
                            k++;
                        }
                        else {
                            StackPane cellContainer = new StackPane();
                            cellContainer.setMaxSize(val, val);
                            cellContainer.setPrefSize(val, val);
                            cellContainer.setAlignment(Pos.CENTER);

                            StackPane tileContainer;

                            if(componentTile.isFaceDown()) {
                                Image image = new Image(getClass().getResourceAsStream("/images/tiles/157.jpg"));
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(val);
                                imageView.setFitHeight(val);

                                tileContainer = new StackPane();
                                tileContainer.getChildren().add(imageView);

                                Rectangle clip = new Rectangle(val, val);
                                clip.setArcWidth(10.0);
                                clip.setArcHeight(10.0);
                                tileContainer.setClip(clip);
                            }
                            else {
                                tileContainer = createTileImage(componentTile, val);
                            }

                            StackPane borderContainer = new StackPane();
                            borderContainer.getStyleClass().add("cell-border");
                            borderContainer.setMaxSize(val, val);
                            borderContainer.setPrefSize(val, val);

                            Rectangle borderClip = new Rectangle(val, val);
                            borderClip.setArcWidth(10.0);
                            borderClip.setArcHeight(10.0);
                            borderContainer.setClip(borderClip);

                            cellContainer.getChildren().addAll(tileContainer, borderContainer);

                            int tileId = k;
                            cellContainer.setOnMouseClicked(event -> {
                                if (this.remainingPositions == 2) {
                                    errorShipLabel.setVisible(true);
                                    errorShipLabel.setText("You have to turn the timer before taking any action");
                                } else {
                                    gui.notifyObserver(obs -> obs.onUpdatePickTile(tileId));
                                    errorShipLabel.setVisible(false);
                                }
                            });

                            gridPane1.add(cellContainer, j, i);
                            k++;
                        }
                    }
                }
            }
        });
    }

    private StackPane createTileImage(ComponentTile tile, double val) {
        String url = tile.getUrl();
        Image image = new Image(getClass().getResourceAsStream("/images/tiles/" + url));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(val);
        imageView.setFitHeight(val);

        StackPane tileContainer = new StackPane();
        tileContainer.getChildren().add(imageView);

        if(tile.getName() == TileName.CARGO) {
            double squareSizeFraction = 0.25;
            double strokeWidthFraction = 0.03;
            double arcSizeFraction = 0.3;
            double marginFraction = 0.05;
            double horizontalOffsetFraction = 0.18;
            double verticalOffsetFraction = 0.18;

            Cargo cargo = (Cargo) tile;
            if(cargo.getNumMaxCargos() == 1) {
                if(cargo.getNumOccupiedCargos() == 1) {
                    Rectangle square1 = new Rectangle();
                    switch(cargo.getCargosIn().get(0)) {
                        case RED -> square1.setFill(Color.RED);
                        case BLUE -> square1.setFill(Color.BLUE);
                        case GREEN -> square1.setFill(Color.GREEN);
                        case YELLOW -> square1.setFill(Color.YELLOW);
                    }
                    square1.setStroke(Color.BLACK);
                    square1.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                    square1.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                    square1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                    square1.arcWidthProperty().bind(Bindings.multiply(square1.widthProperty(), arcSizeFraction));
                    square1.arcHeightProperty().bind(Bindings.multiply(square1.heightProperty(), arcSizeFraction));
                    StackPane.setAlignment(square1, Pos.CENTER);
                    StackPane.setMargin(square1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                    tileContainer.getChildren().addAll(square1);
                }
            }
            else if(cargo.getNumMaxCargos() == 2) {
                if(cargo.getNumOccupiedCargos() >= 1) {
                    Rectangle square1 = new Rectangle();
                    switch(cargo.getCargosIn().get(0)) {
                        case RED -> square1.setFill(Color.RED);
                        case BLUE -> square1.setFill(Color.BLUE);
                        case GREEN -> square1.setFill(Color.GREEN);
                        case YELLOW -> square1.setFill(Color.YELLOW);
                    }
                    square1.setStroke(Color.BLACK);
                    square1.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                    square1.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                    square1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                    square1.arcWidthProperty().bind(Bindings.multiply(square1.widthProperty(), arcSizeFraction));
                    square1.arcHeightProperty().bind(Bindings.multiply(square1.heightProperty(), arcSizeFraction));
                    square1.translateYProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), verticalOffsetFraction));

                    StackPane.setAlignment(square1, Pos.CENTER);
                    StackPane.setMargin(square1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                    tileContainer.getChildren().addAll(square1);

                    if(cargo.getNumOccupiedCargos() == 2) {
                        Rectangle square2 = new Rectangle();
                        switch(cargo.getCargosIn().get(1)) {
                            case RED -> square2.setFill(Color.RED);
                            case BLUE -> square2.setFill(Color.BLUE);
                            case GREEN -> square2.setFill(Color.GREEN);
                            case YELLOW -> square2.setFill(Color.YELLOW);
                        }
                        square2.setStroke(Color.BLACK);
                        square2.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                        square2.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                        square2.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                        square2.arcWidthProperty().bind(Bindings.multiply(square2.widthProperty(), arcSizeFraction));
                        square2.arcHeightProperty().bind(Bindings.multiply(square2.heightProperty(), arcSizeFraction));
                        square2.translateYProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -verticalOffsetFraction));
                        StackPane.setAlignment(square2, Pos.CENTER);
                        StackPane.setMargin(square2, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                        tileContainer.getChildren().addAll(square2);
                    }
                }
            }
            else {
                if(cargo.getNumOccupiedCargos() >= 1) {
                    Rectangle square1 = new Rectangle();
                    switch(cargo.getCargosIn().get(0)) {
                        case RED -> square1.setFill(Color.RED);
                        case BLUE -> square1.setFill(Color.BLUE);
                        case GREEN -> square1.setFill(Color.GREEN);
                        case YELLOW -> square1.setFill(Color.YELLOW);
                    }
                    square1.setStroke(Color.BLACK);
                    square1.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                    square1.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                    square1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                    square1.arcWidthProperty().bind(Bindings.multiply(square1.widthProperty(), arcSizeFraction));
                    square1.arcHeightProperty().bind(Bindings.multiply(square1.heightProperty(), arcSizeFraction));
                    square1.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -horizontalOffsetFraction));
                    StackPane.setAlignment(square1, Pos.CENTER);
                    StackPane.setMargin(square1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                    tileContainer.getChildren().addAll(square1);

                    if(cargo.getNumOccupiedCargos() >= 2) {
                        Rectangle square2 = new Rectangle();
                        switch(cargo.getCargosIn().get(1)) {
                            case RED -> square2.setFill(Color.RED);
                            case BLUE -> square2.setFill(Color.BLUE);
                            case GREEN -> square2.setFill(Color.GREEN);
                            case YELLOW -> square2.setFill(Color.YELLOW);
                        }
                        square2.setStroke(Color.BLACK);
                        square2.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                        square2.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                        square2.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                        square2.arcWidthProperty().bind(Bindings.multiply(square2.widthProperty(), arcSizeFraction));
                        square2.arcHeightProperty().bind(Bindings.multiply(square2.heightProperty(), arcSizeFraction));
                        square2.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +horizontalOffsetFraction));
                        square2.translateYProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -verticalOffsetFraction));
                        StackPane.setAlignment(square2, Pos.CENTER);
                        StackPane.setMargin(square2, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                        tileContainer.getChildren().addAll(square2);

                        if(cargo.getNumOccupiedCargos() == 3) {
                            Rectangle square3 = new Rectangle();
                            switch(cargo.getCargosIn().get(1)) {
                                case RED -> square3.setFill(Color.RED);
                                case BLUE -> square3.setFill(Color.BLUE);
                                case GREEN -> square3.setFill(Color.GREEN);
                                case YELLOW -> square3.setFill(Color.YELLOW);
                            }
                            square3.setStroke(Color.BLACK);
                            square3.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                            square3.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), squareSizeFraction));
                            square3.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                            square3.arcWidthProperty().bind(Bindings.multiply(square3.widthProperty(), arcSizeFraction));
                            square3.arcHeightProperty().bind(Bindings.multiply(square3.heightProperty(), arcSizeFraction));
                            square3.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +horizontalOffsetFraction));
                            square3.translateYProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +verticalOffsetFraction));
                            StackPane.setAlignment(square3, Pos.CENTER);
                            StackPane.setMargin(square3, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                            tileContainer.getChildren().addAll(square3);
                        }
                    }
                }
            }
        }
        else if(tile.getName() == TileName.CABINE) {
            double circleRadiusFraction = 0.15;
            double strokeWidthFraction = 0.03;
            double marginFraction = 0.05;
            double horizontalOffsetFraction = 0.18;

            Cabine cabine = (Cabine) tile;

            if(cabine.getNumFigures() >= 1) {
                Circle circle1 = new Circle();
                circle1.setFill(Color.WHITE);
                circle1.setStroke(Color.BLACK);
                circle1.setStroke(Color.BLACK);
                circle1.radiusProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), circleRadiusFraction));
                circle1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                StackPane.setAlignment(circle1, Pos.CENTER);
                StackPane.setMargin(circle1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                circle1.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -horizontalOffsetFraction));
                tileContainer.getChildren().addAll(circle1);

                if(cabine.getNumFigures() == 2) {
                    Circle circle2 = new Circle();
                    circle2.setFill(Color.WHITE);
                    circle2.setStroke(Color.BLACK);
                    circle2.setStroke(Color.BLACK);
                    circle2.radiusProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), circleRadiusFraction));
                    circle2.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                    StackPane.setAlignment(circle2, Pos.CENTER);
                    StackPane.setMargin(circle2, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                    circle2.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +horizontalOffsetFraction));
                    tileContainer.getChildren().addAll(circle2);
                }
            }
            if(cabine.getHasBrownAlien()) {
                Circle circle1 = new Circle();
                circle1.setFill(Color.GOLD);
                circle1.setStroke(Color.BLACK);
                circle1.setStroke(Color.BLACK);
                circle1.radiusProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), circleRadiusFraction));
                circle1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                StackPane.setAlignment(circle1, Pos.CENTER);
                StackPane.setMargin(circle1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                tileContainer.getChildren().addAll(circle1);
            }
            if(cabine.getHasPurpleAlien()) {
                Circle circle1 = new Circle();
                circle1.setFill(Color.PURPLE);
                circle1.setStroke(Color.BLACK);
                circle1.setStroke(Color.BLACK);
                circle1.radiusProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), circleRadiusFraction));
                circle1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                StackPane.setAlignment(circle1, Pos.CENTER);
                StackPane.setMargin(circle1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                tileContainer.getChildren().addAll(circle1);
            }
        }
        else if(tile.getName() == TileName.STARTING_CABINE) {
            double circleRadiusFraction = 0.15;
            double strokeWidthFraction = 0.03;
            double marginFraction = 0.05;
            double horizontalOffsetFraction = 0.18;

            StartingCabine startingCabine = (StartingCabine) tile;

            if(startingCabine.getNumFigures() >= 1) {
                Circle circle1 = new Circle();
                circle1.setFill(Color.WHITE);
                circle1.setStroke(Color.BLACK);
                circle1.setStroke(Color.BLACK);
                circle1.radiusProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), circleRadiusFraction));
                circle1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                StackPane.setAlignment(circle1, Pos.CENTER);
                StackPane.setMargin(circle1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                circle1.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -horizontalOffsetFraction));
                tileContainer.getChildren().addAll(circle1);

                if(startingCabine.getNumFigures() == 2) {
                    Circle circle2 = new Circle();
                    circle2.setFill(Color.WHITE);
                    circle2.setStroke(Color.BLACK);
                    circle2.setStroke(Color.BLACK);
                    circle2.radiusProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), circleRadiusFraction));
                    circle2.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                    StackPane.setAlignment(circle2, Pos.CENTER);
                    StackPane.setMargin(circle2, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                    circle2.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +horizontalOffsetFraction));
                    tileContainer.getChildren().addAll(circle2);
                }
            }

        }
        else if(tile.getName() == TileName.BATTERY) {
            Battery battery = (Battery) tile;
            if(battery.getNumMaxBatteries() == 2) {
                double r1SizeFraction = 0.18;
                double r2SizeFraction = 0.53;
                double strokeWidthFraction = 0.02;
                double arcSizeFraction = 0.15;
                double marginFraction = 0.05;
                double horizontalOffsetFraction = 0.10;

                Rectangle rectangle1 = new Rectangle();
                Rectangle rectangle2 = new Rectangle();

                rectangle1.setStroke(Color.BLACK);
                rectangle1.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r1SizeFraction));
                rectangle1.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r2SizeFraction));
                rectangle1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                rectangle1.arcWidthProperty().bind(Bindings.multiply(rectangle1.widthProperty(), arcSizeFraction));
                rectangle1.arcHeightProperty().bind(Bindings.multiply(rectangle1.heightProperty(), arcSizeFraction));
                rectangle1.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -horizontalOffsetFraction));

                rectangle2.setStroke(Color.BLACK);
                rectangle2.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r1SizeFraction));
                rectangle2.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r2SizeFraction));
                rectangle2.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                rectangle2.arcWidthProperty().bind(Bindings.multiply(rectangle2.widthProperty(), arcSizeFraction));
                rectangle2.arcHeightProperty().bind(Bindings.multiply(rectangle2.heightProperty(), arcSizeFraction));
                rectangle2.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +horizontalOffsetFraction));

                if(battery.getNumBatteriesInUse() == 2) {
                    rectangle1.setFill(Color.LIMEGREEN);
                    rectangle2.setFill(Color.LIMEGREEN);
                }
                else if (battery.getNumBatteriesInUse() == 1) {
                    rectangle1.setFill(Color.LIMEGREEN);
                    rectangle2.setFill(Color.DARKGREY);
                }
                else {
                    rectangle1.setFill(Color.DARKGREY);
                    rectangle2.setFill(Color.DARKGREY);
                }

                StackPane.setAlignment(rectangle1, Pos.CENTER);
                StackPane.setMargin(rectangle1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                tileContainer.getChildren().addAll(rectangle1, rectangle2);
            }
            else {
                double r1SizeFraction = 0.18;
                double r2SizeFraction = 0.53;
                double strokeWidthFraction = 0.02;
                double arcSizeFraction = 0.15;
                double marginFraction = 0.05;
                double horizontalOffsetFraction = 0.19;

                Rectangle rectangle1 = new Rectangle();
                Rectangle rectangle2 = new Rectangle();
                Rectangle rectangle3 = new Rectangle();

                rectangle1.setStroke(Color.BLACK);
                rectangle1.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r1SizeFraction));
                rectangle1.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r2SizeFraction));
                rectangle1.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                rectangle1.arcWidthProperty().bind(Bindings.multiply(rectangle1.widthProperty(), arcSizeFraction));
                rectangle1.arcHeightProperty().bind(Bindings.multiply(rectangle1.heightProperty(), arcSizeFraction));
                rectangle1.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), -horizontalOffsetFraction));

                rectangle2.setStroke(Color.BLACK);
                rectangle2.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r1SizeFraction));
                rectangle2.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r2SizeFraction));
                rectangle2.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                rectangle2.arcWidthProperty().bind(Bindings.multiply(rectangle2.widthProperty(), arcSizeFraction));
                rectangle2.arcHeightProperty().bind(Bindings.multiply(rectangle2.heightProperty(), arcSizeFraction));

                rectangle3.setStroke(Color.BLACK);
                rectangle3.widthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r1SizeFraction));
                rectangle3.heightProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), r2SizeFraction));
                rectangle3.strokeWidthProperty().bind(Bindings.multiply(imageView.fitWidthProperty(), strokeWidthFraction));
                rectangle3.arcWidthProperty().bind(Bindings.multiply(rectangle3.widthProperty(), arcSizeFraction));
                rectangle3.arcHeightProperty().bind(Bindings.multiply(rectangle3.heightProperty(), arcSizeFraction));
                rectangle3.translateXProperty().bind(Bindings.multiply(imageView.fitHeightProperty(), +horizontalOffsetFraction));

                StackPane.setAlignment(rectangle1, Pos.CENTER);
                StackPane.setMargin(rectangle1, new Insets(Bindings.multiply(imageView.fitWidthProperty(), marginFraction).get()));
                tileContainer.getChildren().addAll(rectangle1, rectangle2, rectangle3);

                if(battery.getNumBatteriesInUse() == 3) {
                    rectangle1.setFill(Color.LIMEGREEN);
                    rectangle2.setFill(Color.LIMEGREEN);
                    rectangle3.setFill(Color.LIMEGREEN);

                }
                else if(battery.getNumBatteriesInUse() == 2) {
                    rectangle1.setFill(Color.LIMEGREEN);
                    rectangle2.setFill(Color.LIMEGREEN);
                    rectangle3.setFill(Color.DARKGREY);
                }
                else if (battery.getNumBatteriesInUse() == 1) {
                    rectangle1.setFill(Color.LIMEGREEN);
                    rectangle2.setFill(Color.DARKGREY);
                    rectangle3.setFill(Color.DARKGREY);
                }
                else {
                    rectangle1.setFill(Color.DARKGREY);
                    rectangle2.setFill(Color.DARKGREY);
                    rectangle3.setFill(Color.DARKGREY);
                }
            }
        }


        if(tile.getDirection().equals("est")) {
            tileContainer.setRotate(90);
        }
        else if(tile.getDirection().equals("sud")) {
            tileContainer.setRotate(180);
        }
        else if(tile.getDirection().equals("ovest")) {
            tileContainer.setRotate(270);
        }

        Rectangle clip = new Rectangle(val, val);
        clip.setArcWidth(10.0);
        clip.setArcHeight(10.0);
        tileContainer.setClip(clip);

        return tileContainer;
    }


    public void updateShipBoard(ShipBoard shipBoard) {
        this.shipBoard = shipBoard;

        double cellHeight = Math.floor(bottomPane.getHeight());
        double cellWidth = Math.floor(bottomPane1.getWidth());

        StackPane cellContainer = new StackPane();
        cellContainer.setMaxSize(cellHeight, cellWidth);
        cellContainer.setPrefSize(cellHeight, cellWidth);

        Image tileImage = new Image(getClass().getResourceAsStream(flightType == FlightType.FIRST_FLIGHT ? "/images/cardboard/cardboard_first.png" : "/images/cardboard/cardboard_standard.jpg"));
        ImageView imageView = new ImageView(tileImage);

        double ratio = tileImage.getWidth() / tileImage.getHeight();
        imageView.setFitHeight(cellHeight);
        imageView.setFitWidth(Math.floor(cellHeight * ratio));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Rectangle clip = new Rectangle(Math.floor(cellHeight * ratio), cellHeight);
        clip.setArcWidth(20.0);
        clip.setArcHeight(20.0);
        imageView.setClip(clip);

        StackPane borderContainer = new StackPane();
        borderContainer.getStyleClass().add("cell-border1");
        borderContainer.setMaxSize(Math.floor(cellHeight * ratio), cellHeight);
        borderContainer.setPrefSize(Math.floor(cellHeight * ratio), cellHeight);
        Rectangle borderClip = new Rectangle(Math.floor(cellHeight * ratio), cellHeight);
        borderClip.setArcWidth(20.0);
        borderClip.setArcHeight(20.0);
        borderContainer.setClip(borderClip);

        cellContainer.getChildren().addAll(imageView, borderContainer);
        cellContainer.setAlignment(Pos.BOTTOM_LEFT);

        bottomPane1.getChildren().clear();
        bottomPane1.getChildren().add(cellContainer);
        bottomPane1.setAlignment(Pos.BOTTOM_LEFT);

        shipGrid.setMaxHeight(cellHeight);
        shipGrid.setMaxWidth(Math.floor(cellHeight * ratio));

        double shipGridHeight = Math.floor(shipGrid.getMaxHeight() * 0.915);
        double shipGridWidth = Math.floor(shipGrid.getMaxWidth() * 0.93);
        double cellShipHeight = Math.floor(shipGridHeight / 5);
        double cellShipWidth = Math.floor(shipGridWidth / 7);
        double val = Math.floor(Math.min(cellShipWidth, cellShipHeight));

        shipGrid.getChildren().clear();

        if (isViewingSpecificTile) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    StackPane cellShipContainer = new StackPane();
                    cellShipContainer.setMaxSize(val, val);
                    cellShipContainer.setPrefSize(val, val);
                    cellShipContainer.setAlignment(Pos.CENTER);

                    ComponentTile componentTile = shipBoard.getSpace(i, j).getComponent();
                    StackPane borderShipContainer = new StackPane();
                    borderShipContainer.setMaxSize(val, val);
                    borderShipContainer.setPrefSize(val, val);
                    Rectangle borderShipClip = new Rectangle(val, val);
                    borderShipClip.setArcWidth(10.0);
                    borderShipClip.setArcHeight(10.0);
                    borderShipContainer.setClip(borderShipClip);

                    if (componentTile != null) {
                        StackPane tileContainer = createTileImage(componentTile, val);
                        borderShipContainer.getStyleClass().add("cell-border1");
                        cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                    } else {
                        borderShipContainer.getStyleClass().add("cell-border");
                        cellShipContainer.getChildren().add(borderShipContainer);

                        final int row = i;
                        final int col = j;
                        cellShipContainer.setOnMouseClicked(event -> {
                            if (!isViewingSpecificTile) {
                                errorShipLabel.setVisible(true);
                                errorShipLabel.setText("You cannot put the tile in the ship");
                                return;
                            }
                            if (flightType == FlightType.STANDARD_FLIGHT && remainingPositions == 2) {
                                errorShipLabel.setVisible(true);
                                errorShipLabel.setText("You have to turn the timer before taking any action");
                                return;
                            }
                            errorShipLabel.setVisible(false);
                            gui.notifyObserver(obs -> obs.onUpdatePutTileInShip(row + 5, col + 4));
                            fromShip = false;
                            isViewingSpecificTile = false;
                            gridPane1.setVisible(true);
                            section2.setVisible(true);
                            section3.setVisible(true);
                            tilesPane.getChildren().clear();
                            tilesPane.setVisible(false);
                        });
                    }
                    shipGrid.add(cellShipContainer, j, i);
                }
            }
            bottomPane1.getChildren().add(shipGrid);
        }
        else if(repair) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    StackPane cellShipContainer = new StackPane();
                    cellShipContainer.setMaxSize(val, val);
                    cellShipContainer.setPrefSize(val, val);
                    cellShipContainer.setAlignment(Pos.CENTER);

                    ComponentTile componentTile = shipBoard.getSpace(i, j).getComponent();
                    StackPane borderShipContainer = new StackPane();
                    borderShipContainer.setMaxSize(val, val);
                    borderShipContainer.setPrefSize(val, val);
                    Rectangle borderShipClip = new Rectangle(val, val);
                    borderShipClip.setArcWidth(10.0);
                    borderShipClip.setArcHeight(10.0);
                    borderShipContainer.setClip(borderShipClip);

                    if (componentTile != null) {
                        StackPane tileContainer = createTileImage(componentTile, val);
                        borderShipContainer.getStyleClass().add("cell-border");
                        cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);

                        final int row = i;
                        final int col = j;
                        cellShipContainer.setOnMouseClicked(event -> {
                            errorShipLabel.setVisible(false);
                            gui.notifyObserver(obs -> obs.onUpdateRemoveTile(row + 5, col + 4));
                            fromShip = false;
                            tilesPane.getChildren().clear();
                            tilesPane.setVisible(false);
                        });
                    }
                    shipGrid.add(cellShipContainer, j, i);
                }
            }
            bottomPane1.getChildren().add(shipGrid);

        }
        else if(populating) {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    StackPane cellShipContainer = new StackPane();
                    cellShipContainer.setMaxSize(val, val);
                    cellShipContainer.setPrefSize(val, val);
                    cellShipContainer.setAlignment(Pos.CENTER);

                    ComponentTile componentTile = shipBoard.getSpace(i, j).getComponent();
                    StackPane borderShipContainer = new StackPane();
                    borderShipContainer.setMaxSize(val, val);
                    borderShipContainer.setPrefSize(val, val);
                    Rectangle borderShipClip = new Rectangle(val, val);
                    borderShipClip.setArcWidth(10.0);
                    borderShipClip.setArcHeight(10.0);
                    borderShipContainer.setClip(borderShipClip);

                    if (componentTile != null) {
                        if(componentTile.getName() == TileName.CABINE || componentTile.getName() == TileName.STARTING_CABINE) {
                            StackPane tileContainer = createTileImage(componentTile, val);
                            borderShipContainer.getStyleClass().add("cell-border");
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);

                            final int row = i;
                            final int col = j;
                            cellShipContainer.setOnMouseClicked(event -> {
                                if(figure == null) {
                                    label5.setText("You have to chose what to put in this cabine!!!");
                                    label5.setVisible(true);
                                }
                                else {
                                    label5.setVisible(false);
                                    if(flightType == FlightType.FIRST_FLIGHT) {
                                        gui.notifyObserver(obs->obs.onUpdatePutAstronautInShip(row + 5, col + 4));
                                    }
                                    else {
                                        if(figure.equals("astronaut")) {
                                            gui.notifyObserver(obs->obs.onUpdatePutAstronautInShip(row + 5, col + 4));
                                        }
                                        else if(figure.equals("brown alien")){
                                            gui.notifyObserver(obs->obs.onUpdatePutBrownInShip(row + 5, col + 4));
                                        }
                                        else if(figure.equals("purple alien")){
                                            gui.notifyObserver(obs->obs.onUpdatePutPurpleInShip(row + 5, col + 4));
                                        }

                                    }
                                }
                                errorShipLabel.setVisible(false);
                                fromShip = false;
                                tilesPane.getChildren().clear();
                                tilesPane.setVisible(false);
                            });
                        }
                        else {
                            StackPane tileContainer = createTileImage(componentTile, val);
                            borderShipContainer.getStyleClass().add("cell-border1");
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                    }
                    shipGrid.add(cellShipContainer, j, i);
                }
            }
            bottomPane1.getChildren().add(shipGrid);
        }
        else {
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    StackPane cellShipContainer = new StackPane();
                    cellShipContainer.setMaxSize(val, val);
                    cellShipContainer.setPrefSize(val, val);
                    cellShipContainer.setAlignment(Pos.CENTER);

                    ComponentTile componentTile = shipBoard.getSpace(i, j).getComponent();
                    StackPane borderShipContainer = new StackPane();
                    borderShipContainer.setMaxSize(val, val);
                    borderShipContainer.setPrefSize(val, val);
                    Rectangle borderShipClip = new Rectangle(val, val);
                    borderShipClip.setArcWidth(10.0);
                    borderShipClip.setArcHeight(10.0);
                    borderShipContainer.setClip(borderShipClip);

                    if (componentTile != null) {
                        StackPane tileContainer = createTileImage(componentTile, val);
                        borderShipContainer.getStyleClass().add("cell-border1");

                        if(flightType == FlightType.STANDARD_FLIGHT) {
                            if(i==0 && j==5) {
                                borderShipContainer.getStyleClass().clear();
                                borderShipContainer.getStyleClass().add("cell-border");
                                cellShipContainer.setOnMouseClicked(event -> {
                                    if(this.remainingPositions == 2) {
                                        errorShipLabel.setVisible(true);
                                        errorShipLabel.setText("You have to turn the timer before taking any action");
                                    }
                                    else {
                                        gui.notifyObserver(obs -> obs.onUpdatePickTileFromShip(5, 9));
                                        errorShipLabel.setVisible(false);
                                        fromShip = true;
                                    }
                                });
                            }
                            else if (i==0 && j==6) {
                                borderShipContainer.getStyleClass().clear();
                                borderShipContainer.getStyleClass().add("cell-border");
                                cellShipContainer.setOnMouseClicked(event -> {
                                    if(this.remainingPositions == 2) {
                                        errorShipLabel.setVisible(true);
                                        errorShipLabel.setText("You have to turn the timer before taking any action");
                                    }
                                    else {
                                        gui.notifyObserver(obs -> obs.onUpdatePickTileFromShip(5, 10));
                                        errorShipLabel.setVisible(false);
                                        fromShip = true;
                                    }
                                });
                            }
                        }
                        cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                    }
                    shipGrid.add(cellShipContainer, j, i);
                }
            }
            bottomPane1.getChildren().add(shipGrid);
        }
    }


    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }


    public void updateTimerLabel(int time) {
        Platform.runLater(() -> {
            if(time == 10) {
                this.remainingPositions--;
                timerLabel.getStyleClass().clear();
                timerLabel.getStyleClass().add("my-timer");
            }
            if(time == 0) {
                timerLabel.getStyleClass().clear();
                timerLabel.getStyleClass().add("my-timer1");
            }
            if (timerLabel != null) {
                timerLabel.setText("Timer: " + time + " seconds remaining");
            }
            if (positionsLabel != null) {
                positionsLabel.setText("Remaining positions: " + remainingPositions);
            }
        });
    }


    public void setShipErrorLabel(String error) {
        errorShipLabel.setText(error);
        errorShipLabel.setVisible(true);

    }


    public void setErrorLabel5(String error) {
        label5.setText(error);
        label5.setVisible(true);
    }


    public boolean isViewingSpecificTile() {
        return isViewingSpecificTile;
    }


    public boolean isViewingShips() {
        return isViewingShips;
    }


    public boolean isViewingCardsPiles() {
        return isViewingCardsPiles;
    }


    public void showSpecificTile(ComponentTile componentTile) {
        if(!fromShip) {
            Platform.runLater(() -> {
                isViewingSpecificTile = true;
                gridPane1.setVisible(false);
                section2.setVisible(false);
                section3.setVisible(false);

                if (!gridPane.getChildren().contains(tilesPane)) {
                    gridPane.getChildren().add(tilesPane);
                }

                tilesPane.getChildren().clear();
                tilesPane.setMaxWidth(gridPane.getWidth());
                tilesPane.setMaxHeight(gridPane.getHeight() * 0.432);
                tilesPane.setPrefWidth(gridPane.getWidth());
                tilesPane.setPrefHeight(gridPane.getHeight() * 0.432);
                tilesPane.setAlignment(Pos.TOP_CENTER);

                Label label = new Label("This is the tile you picked. You can put it in your ship, rotate it, ore put it back in the deck (facing up)...");
                label.getStyleClass().add("my-button3");
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER);
                VBox.setMargin(label, new Insets(10, 0, 10, 0));

                double val = Math.min(gridPane.getPrefWidth() * 0.12, gridPane.getPrefHeight() * 0.12);

                StackPane tileContainer = createTileImage(componentTile, val);

                Button rotateLeftButton = new Button();
                rotateLeftButton.getStyleClass().add("rotate-button");
                rotateLeftButton.setPrefSize(40, 40);
                rotateLeftButton.setOnAction(event -> {
                    tileContainer.setRotate(tileContainer.getRotate() - 90);
                    String direction;
                    if(componentTile.getDirection().equals("nord")) {
                        direction = "ovest";
                    }
                    else if(componentTile.getDirection().equals("est")) {
                        direction = "nord";
                    }
                    else if(componentTile.getDirection().equals("sud")) {
                        direction = "est";
                    }
                    else {
                        direction = "sud";
                    }
                    gui.notifyObserver(obs -> obs.onUpdateSetDirection(direction));
                    errorShipLabel.setVisible(false);
                });

                Button rotateRightButton = new Button();
                rotateRightButton.getStyleClass().add("rotate-button");
                rotateRightButton.setPrefSize(40, 40);
                rotateRightButton.setOnAction(event -> {
                    tileContainer.setRotate(tileContainer.getRotate() + 90);
                    String direction;
                    if(componentTile.getDirection().equals("nord")) {
                        direction = "est";
                    }
                    else if(componentTile.getDirection().equals("est")) {
                        direction = "sud";
                    }
                    else if(componentTile.getDirection().equals("sud")) {
                        direction = "ovest";
                    }
                    else {
                        direction = "nord";
                    }
                    gui.notifyObserver(obs -> obs.onUpdateSetDirection(direction));
                    errorShipLabel.setVisible(false);
                });

                HBox tileContainer1 = new HBox(20, rotateLeftButton, tileContainer, rotateRightButton);
                tileContainer1.setAlignment(Pos.CENTER);
                VBox.setMargin(tileContainer1, new Insets(0, 0, 10, 0));

                Button backButton = new Button("Put back");
                backButton.getStyleClass().add("my-button1");
                backButton.setOnAction(event -> {
                    isViewingSpecificTile = false;
                    gridPane1.setVisible(true);
                    section2.setVisible(true);
                    section3.setVisible(true);
                    tilesPane.getChildren().clear();
                    tilesPane.setVisible(false);
                    gui.notifyObserver(obs -> obs.onUpdatePutTileInDeck());
                });
                VBox.setMargin(backButton, new Insets(0, 0, 10, 0));

                VBox contentContainer = new VBox(10, label, tileContainer1, backButton);
                contentContainer.setAlignment(Pos.TOP_CENTER);
                contentContainer.setPadding(new Insets(10));

                tilesPane.getChildren().add(contentContainer);
                tilesPane.setVisible(true);
                this.updateShipBoard(shipBoard);
            });
        }
        else {
            Platform.runLater(() -> {
                isViewingSpecificTile = true;
                gridPane1.setVisible(false);
                section2.setVisible(false);
                section3.setVisible(false);

                if (!gridPane.getChildren().contains(tilesPane)) {
                    gridPane.getChildren().add(tilesPane);
                }

                tilesPane.getChildren().clear();
                tilesPane.setMaxWidth(gridPane.getWidth());
                tilesPane.setMaxHeight(gridPane.getHeight() * 0.432);
                tilesPane.setPrefWidth(gridPane.getWidth());
                tilesPane.setPrefHeight(gridPane.getHeight() * 0.432);
                tilesPane.setAlignment(Pos.TOP_CENTER);

                Label label = new Label("This is the tile you picked from your ship. You only can put it in your ship or rotate it. You can't put it back in the deck...");
                label.getStyleClass().add("my-button3");
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER);
                VBox.setMargin(label, new Insets(10, 0, 10, 0));

                double val = Math.min(gridPane.getPrefWidth() * 0.12, gridPane.getPrefHeight() * 0.12);

                StackPane tileContainer = createTileImage(componentTile, val);

                Button rotateLeftButton = new Button();
                rotateLeftButton.getStyleClass().add("rotate-button");
                rotateLeftButton.setPrefSize(40, 40);
                rotateLeftButton.setOnAction(event -> {
                    tileContainer.setRotate(tileContainer.getRotate() - 90);
                    String direction;
                    if(componentTile.getDirection().equals("nord")) {
                        direction = "ovest";
                    }
                    else if(componentTile.getDirection().equals("est")) {
                        direction = "nord";
                    }
                    else if(componentTile.getDirection().equals("sud")) {
                        direction = "est";
                    }
                    else {
                        direction = "sud";
                    }
                    gui.notifyObserver(obs -> obs.onUpdateSetDirection(direction));
                    errorShipLabel.setVisible(false);
                });

                Button rotateRightButton = new Button();
                rotateRightButton.getStyleClass().add("rotate-button");
                rotateRightButton.setPrefSize(40, 40);
                rotateRightButton.setOnAction(event -> {
                    tileContainer.setRotate(tileContainer.getRotate() + 90);
                    String direction;
                    if(componentTile.getDirection().equals("nord")) {
                        direction = "est";
                    }
                    else if(componentTile.getDirection().equals("est")) {
                        direction = "sud";
                    }
                    else if(componentTile.getDirection().equals("sud")) {
                        direction = "ovest";
                    }
                    else {
                        direction = "nord";
                    }
                    gui.notifyObserver(obs -> obs.onUpdateSetDirection(direction));
                    errorShipLabel.setVisible(false);
                });

                HBox tileContainer1 = new HBox(20, rotateLeftButton, tileContainer, rotateRightButton);
                tileContainer1.setAlignment(Pos.CENTER);
                VBox.setMargin(tileContainer1, new Insets(0, 0, 10, 0));

                VBox contentContainer = new VBox(10, label, tileContainer1);
                contentContainer.setAlignment(Pos.TOP_CENTER);
                contentContainer.setPadding(new Insets(10));

                tilesPane.getChildren().add(contentContainer);
                tilesPane.setVisible(true);
                this.updateShipBoard(shipBoard);
            });
        }
    }


    public void showShips(HashMap<String, ShipBoard> shipBoards) {
        if(isViewingShips) {
            Platform.runLater(() -> {
                gridPane1.setVisible(false);
                section2.setVisible(false);
                section3.setVisible(false);

                if (!gridPane.getChildren().contains(shipsPane)) {
                    gridPane.getChildren().add(shipsPane);
                }

                shipsPane.getChildren().clear();
                shipsPane.setMaxWidth(gridPane.getWidth());
                shipsPane.setMaxHeight(gridPane.getHeight() * 0.432);
                shipsPane.setPrefWidth(gridPane.getWidth());
                shipsPane.setPrefHeight(gridPane.getHeight() * 0.432);
                shipsPane.setAlignment(Pos.TOP_CENTER);

                GridPane mainShipGrid = new GridPane();
                shipsPane.getChildren().add(mainShipGrid);
                GridPane.setHgrow(mainShipGrid, Priority.ALWAYS);
                GridPane.setVgrow(mainShipGrid, Priority.ALWAYS);
                mainShipGrid.setAlignment(Pos.TOP_CENTER);
                mainShipGrid.setMaxWidth(gridPane.getWidth());
                mainShipGrid.setMaxHeight(gridPane.getHeight() * 0.432);
                mainShipGrid.setPrefWidth(gridPane.getWidth());
                mainShipGrid.setPrefHeight(gridPane.getHeight() * 0.432);
                mainShipGrid.setAlignment(Pos.TOP_CENTER);

                for (int i = 0; i < 3; i++) {
                    ColumnConstraints col = new ColumnConstraints();
                    col.setPercentWidth(100.0 / 3);
                    col.setMaxWidth(Double.MAX_VALUE);
                    col.setPrefWidth(Double.MAX_VALUE);
                    mainShipGrid.getColumnConstraints().add(col);
                }
                RowConstraints row = new RowConstraints();
                row.setPercentHeight(100);
                mainShipGrid.getRowConstraints().add(row);

                int k = 0;
                for (HashMap.Entry<String, ShipBoard> entry : shipBoards.entrySet()) {
                    String nickname = entry.getKey();
                    ShipBoard shipBoard = entry.getValue();

                    Label label = new Label();
                    label.setAlignment(Pos.CENTER);
                    label.getStyleClass().add("my-button4");
                    label.setText(nickname);
                    mainShipGrid.add(label,k,0);

                    GridPane.setValignment(label, VPos.TOP);
                    GridPane.setHalignment(label, HPos.CENTER);
                    GridPane playerShipGrid = new GridPane();
                    mainShipGrid.add(playerShipGrid, k, 0);

                    playerShipGrid.getChildren().clear();
                    playerShipGrid.setMaxWidth(Math.floor(mainShipGrid.getMaxWidth()/3) - 52);
                    playerShipGrid.setMaxHeight(gridPane.getMaxHeight() * 0.400);
                    playerShipGrid.setPrefWidth(mainShipGrid.getMaxWidth() - 20);
                    playerShipGrid.setPrefHeight(gridPane.getMaxHeight() * 0.400);

                    GridPane subGrid = createSubShipGrid();
                    GridPane.setHgrow(subGrid, Priority.ALWAYS);
                    GridPane.setVgrow(subGrid, Priority.ALWAYS);
                    GridPane.setValignment(playerShipGrid, VPos.BOTTOM);
                    GridPane.setHalignment(playerShipGrid, HPos.CENTER);
                    GridPane.setValignment(subGrid, VPos.BOTTOM);
                    GridPane.setHalignment(subGrid, HPos.CENTER);

                    mainShipGrid.add(subGrid, k, 0);

                    double cellHeight = Math.floor(playerShipGrid.getMaxHeight());
                    double cellWidth = Math.floor(playerShipGrid.getMaxWidth());
                    StackPane cellContainer = new StackPane();
                    cellContainer.setMaxSize(cellHeight, cellWidth);
                    cellContainer.setPrefSize(cellHeight, cellWidth);

                    Image tileImage = new Image(getClass().getResourceAsStream(
                            flightType == FlightType.FIRST_FLIGHT ? "/images/cardboard/cardboard_first.png" : "/images/cardboard/cardboard_standard.jpg"));
                    ImageView imageView = new ImageView(tileImage);
                    double ratio = tileImage.getWidth() / tileImage.getHeight();
                    imageView.setFitHeight(cellWidth/ratio);
                    imageView.setFitWidth(Math.floor(cellWidth));
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    Rectangle clip = new Rectangle(Math.floor(cellWidth), cellWidth/ratio);
                    clip.setArcWidth(20.0);
                    clip.setArcHeight(20.0);
                    imageView.setClip(clip);

                    StackPane borderContainer = new StackPane();
                    borderContainer.getStyleClass().add("cell-border1");
                    borderContainer.setMaxSize(Math.floor(cellWidth), Math.floor(cellWidth/ratio));
                    borderContainer.setPrefSize(Math.floor(cellWidth), Math.floor(cellWidth/ratio));
                    Rectangle borderClip = new Rectangle(Math.floor(cellWidth), Math.floor(cellWidth/ratio));
                    borderClip.setArcWidth(20.0);
                    borderClip.setArcHeight(20.0);
                    borderContainer.setClip(borderClip);

                    cellContainer.getChildren().addAll(imageView, borderContainer);
                    cellContainer.setAlignment(Pos.CENTER);

                    playerShipGrid.getChildren().add(cellContainer);

                    subGrid.setMaxHeight(cellWidth/ratio);
                    subGrid.setMaxWidth(Math.floor(cellWidth));

                    double shipGridHeight = Math.floor(subGrid.getMaxHeight() * 0.915);
                    double shipGridWidth = Math.floor(subGrid.getMaxWidth() * 0.93);
                    double cellShipHeight = Math.floor(shipGridHeight / 5);
                    double cellShipWidth = Math.floor(shipGridWidth / 7);
                    double val = Math.floor(Math.min(cellShipWidth, cellShipHeight));
                    double value = Math.floor(Math.floor(subGrid.getMaxWidth()) - shipGridWidth);
                    value = Math.floor(value/2 * 0.93);

                    subGrid.setTranslateY(-value);

                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 7; j++) {
                            StackPane cellShipContainer = new StackPane();
                            cellShipContainer.setMaxSize(val, val);
                            cellShipContainer.setPrefSize(val, val);
                            cellShipContainer.setAlignment(Pos.CENTER);

                            ComponentTile componentTile = shipBoard.getSpace(i, j).getComponent();
                            StackPane borderShipContainer = new StackPane();
                            borderShipContainer.setMaxSize(val, val);
                            borderShipContainer.setPrefSize(val, val);
                            Rectangle borderShipClip = new Rectangle(val, val);
                            borderShipClip.setArcWidth(10.0);
                            borderShipClip.setArcHeight(10.0);
                            borderShipContainer.setClip(borderShipClip);

                            if (componentTile != null) {
                                StackPane tileContainer = createTileImage(componentTile, val);
                                borderShipContainer.getStyleClass().add("cell-border1");
                                cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                            }
                            subGrid.add(cellShipContainer, j, i);
                        }
                    }
                    k++;
                }
                shipsPane.setVisible(true);

                if(!hasFinishedBuilding) {
                    buttonGrid.getChildren().clear();
                    section3.setVisible(true);
                    buttonGrid.setVisible(true);
                    Button button1 = new Button("Back");
                    button1.getStyleClass().add("my-button1");
                    GridPane.setHgrow(button1, Priority.ALWAYS);
                    GridPane.setVgrow(button1, Priority.ALWAYS);
                    GridPane.setFillWidth(button1, true);
                    GridPane.setFillHeight(button1, true);
                    GridPane.setValignment(button1, VPos.BOTTOM);
                    button1.setMaxWidth(Double.MAX_VALUE);
                    button1.setOnAction(event -> {
                        errorShipLabel.setVisible(false);
                        isViewingShips = false;
                        shipsPane.setVisible(false);
                        gui.notifyObserver(obs -> obs.onUpdateStopWatchingShips(null));
                    });

                    buttonGrid.add(button1, 0, 0);
                }
                else {
                    section3.getChildren().clear();
                    Label label = new Label("You have finished building your ship. Wait the other players...");
                    label.getStyleClass().add("my-button3");
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    section3.getChildren().add(label);
                    section3.setVisible(true);
                }
            });
        }
    }


    private GridPane createSubShipGrid() {
        GridPane subGrid = new GridPane();
        subGrid.setAlignment(Pos.CENTER);
        subGrid.setScaleX(0.93);
        subGrid.setScaleY(0.915);

        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(14.285);
            col.setHalignment(HPos.CENTER);
            subGrid.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(20);
            subGrid.getRowConstraints().add(row);
        }
        return subGrid;
    }


    public void showCardsPiles(CardPile cardPile){
        Platform.runLater(() -> {
            isViewingCardsPiles = true;
            gridPane1.setVisible(false);
            section2.setVisible(false);
            section3.setVisible(false);

            if (!gridPane.getChildren().contains(tilesPane)) {
                gridPane.getChildren().add(tilesPane);
            }

            tilesPane.getChildren().clear();
            tilesPane.setMaxWidth(gridPane.getWidth());
            tilesPane.setMaxHeight(gridPane.getHeight() * 0.432);
            tilesPane.setPrefWidth(gridPane.getWidth());
            tilesPane.setPrefHeight(gridPane.getHeight() * 0.432);
            tilesPane.setAlignment(Pos.TOP_CENTER);

            Label label = new Label("These are the cards contained in the cards pile you picked...");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));


            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            ArrayList<Card> cards = cardPile.getCards();
            StackPane cardsContainer = new StackPane();

            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            ImageView imageView1, imageView2, imageView3;
            Image cardImage1, cardImage2, cardImage3;

            cardImage1 = new Image(getClass().getResourceAsStream("/images/cards/" + cards.get(0).getUrl()));
            imageView1 = new ImageView(cardImage1);
            cardImage2 = new Image(getClass().getResourceAsStream("/images/cards/" + cards.get(1).getUrl()));
            imageView2 = new ImageView(cardImage2);
            cardImage3 = new Image(getClass().getResourceAsStream("/images/cards/" + cards.get(2).getUrl()));
            imageView3 = new ImageView(cardImage3);
            double ratio1 = cardImage1.getWidth() / cardImage1.getHeight();
            double ratio2 = cardImage2.getWidth() / cardImage2.getHeight();
            double ratio3 = cardImage3.getWidth() / cardImage3.getHeight();

            imageView1.setFitHeight(val);
            imageView1.setFitWidth(val*ratio1);
            imageView1.setPreserveRatio(true);
            imageView1.setSmooth(true);
            imageView1.setTranslateX(-200);

            imageView2.setFitHeight(val);
            imageView2.setFitWidth(val*ratio2);
            imageView2.setPreserveRatio(true);
            imageView2.setSmooth(true);
            imageView2.setTranslateX(0);

            imageView3.setFitHeight(val);
            imageView3.setFitWidth(val*ratio3);
            imageView3.setPreserveRatio(true);
            imageView3.setSmooth(true);
            imageView3.setTranslateX(200);

            Rectangle clip1 = new Rectangle(val*ratio1, val);
            clip1.setArcWidth(20.0);
            clip1.setArcHeight(20.0);
            imageView1.setClip(clip1);

            Rectangle clip2 = new Rectangle(val*ratio2, val);
            clip2.setArcWidth(20.0);
            clip2.setArcHeight(20.0);
            imageView2.setClip(clip2);

            Rectangle clip3 = new Rectangle(val*ratio3, val);
            clip3.setArcWidth(20.0);
            clip3.setArcHeight(20.0);
            imageView3.setClip(clip3);

            StackPane borderContainer1 = new StackPane();
            borderContainer1.getStyleClass().add("cell-border1");
            borderContainer1.setMaxHeight(val);
            borderContainer1.setMaxWidth(val*ratio1);
            borderContainer1.setTranslateX(-200);

            StackPane borderContainer2 = new StackPane();
            borderContainer2.getStyleClass().add("cell-border1");
            borderContainer2.setMaxHeight(val);
            borderContainer2.setMaxWidth(val*ratio2);
            borderContainer2.setTranslateX(0);

            StackPane borderContainer3 = new StackPane();
            borderContainer3.getStyleClass().add("cell-border1");
            borderContainer3.setMaxHeight(val);
            borderContainer3.setMaxWidth(val*ratio3);
            borderContainer3.setTranslateX(200);

            VBox cellContainer1 = new VBox(borderContainer1);
            cellContainer1.setMaxHeight(val);
            cellContainer1.setAlignment(Pos.CENTER);
            GridPane.setHgrow(cellContainer1, Priority.ALWAYS);
            VBox.setVgrow(borderContainer1, Priority.ALWAYS);

            VBox cellContainer2 = new VBox(borderContainer2);
            cellContainer2.setMaxHeight(val);
            cellContainer2.setAlignment(Pos.CENTER);
            GridPane.setHgrow(cellContainer2, Priority.ALWAYS);
            VBox.setVgrow(borderContainer2, Priority.ALWAYS);

            VBox cellContainer3 = new VBox(borderContainer3);
            cellContainer3.setMaxHeight(val);
            cellContainer3.setAlignment(Pos.CENTER);
            GridPane.setHgrow(cellContainer3, Priority.ALWAYS);
            VBox.setVgrow(borderContainer3, Priority.ALWAYS);

            cardsContainer.getChildren().addAll(imageView1, cellContainer1);
            cardsContainer.setAlignment(Pos.CENTER);

            cardsContainer.getChildren().addAll(imageView2, cellContainer2);
            cardsContainer.setAlignment(Pos.CENTER);

            cardsContainer.getChildren().addAll(imageView3, cellContainer3);
            cardsContainer.setAlignment(Pos.CENTER);

            HBox tileContainer1 = new HBox(20, cardsContainer);
            tileContainer1.setAlignment(Pos.CENTER);

            buttonGrid.getChildren().clear();
            section3.setVisible(true);
            buttonGrid.setVisible(true);

            Button button1 = new Button("Back");
            button1.getStyleClass().add("my-button1");
            GridPane.setHgrow(button1, Priority.ALWAYS);
            GridPane.setVgrow(button1, Priority.ALWAYS);
            GridPane.setFillWidth(button1, true);
            GridPane.setFillHeight(button1, true);
            GridPane.setValignment(button1, VPos.BOTTOM);
            button1.setMaxWidth(Double.MAX_VALUE);
            button1.setOnAction(event -> {
                errorShipLabel.setVisible(false);
                isViewingCardsPiles= false;
                shipsPane.setVisible(false);
                gui.notifyObserver(obs -> obs.onUpdateStopWatchingCardPile());
            });

            buttonGrid.add(button1, 0, 0);

            VBox contentContainer = new VBox(label, tileContainer1);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            contentContainer.setPadding(new Insets(10));

            tilesPane.getChildren().add(contentContainer);
            tilesPane.setVisible(true);
            this.updateShipBoard(shipBoard);
        });
    }


    public void showTempPositions(ArrayList<Player> tempPositions) {
        Platform.runLater(() -> {
            if(!repair && !finish) {
                tempGrid = new GridPane();
                tempGrid.setAlignment(Pos.CENTER_LEFT);
                tempGrid.setMaxWidth(bottomPane2.getWidth());
                tempGrid.setMaxHeight(bottomPane2.getHeight());
                tempGrid.setPrefWidth(bottomPane2.getWidth());
                tempGrid.setPrefHeight(bottomPane2.getHeight());

                RowConstraints row1 = new RowConstraints();
                row1.setPercentHeight(15);
                RowConstraints row2 = new RowConstraints();
                row2.setPercentHeight(60);
                RowConstraints row3 = new RowConstraints();
                row3.setPercentHeight(25);
                tempGrid.getRowConstraints().addAll(row1, row2, row3);

                ColumnConstraints col = new ColumnConstraints();
                col.setPercentWidth(100);
                tempGrid.getColumnConstraints().add(col);

                bottomPane2.getChildren().clear();
                bottomPane2.setAlignment(Pos.TOP_LEFT);
                bottomPane2.getChildren().add(tempGrid);
            }

            String text = "Starting positions:   ";
            int i = 1;
            for(Player player : tempPositions){
                if(i == tempPositions.size()) {
                    text = text + player.getNickname() + " ";
                }
                else {
                    text = text + player.getNickname() + ", ";
                }
            }

            Label label = new Label(text);
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.TOP_LEFT);
            tempGrid.add(label, 0, 0);
            bottomPane2.setVisible(true);
        });
    }


    public void showShipErrors(ArrayList<String> errors) {
        Platform.runLater(() -> {
            tempGrid = new GridPane();
            tempGrid.setAlignment(Pos.CENTER_LEFT);
            tempGrid.setMaxWidth(bottomPane2.getWidth());
            tempGrid.setMaxHeight(bottomPane2.getHeight());
            tempGrid.setPrefWidth(bottomPane2.getWidth());
            tempGrid.setPrefHeight(bottomPane2.getHeight());

            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(15);
            row1.setValignment(VPos.TOP);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(60);
            row2.setValignment(VPos.TOP);
            RowConstraints row3 = new RowConstraints();
            row3.setPercentHeight(25);
            row3.setValignment(VPos.BOTTOM);
            tempGrid.getRowConstraints().addAll(row1, row2, row3);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(100);
            tempGrid.getColumnConstraints().add(col1);

            bottomPane2.getChildren().clear();
            repair = true;

            bottomPane2.setAlignment(Pos.TOP_LEFT);

            String text = "Your ship board is not correct. You have to remove some components!!\n";
            int k = 1;
            for(String error : errors){
                text = text + k + ". " + error + "\n";
                k++;
            }

            Label label = new Label(text);
            label.getStyleClass().add("my-button5");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setMaxWidth(Double.MAX_VALUE);
            tempGrid.add(label, 0, 1);
            bottomPane2.getChildren().add(tempGrid);
            bottomPane2.setVisible(true);

            double cellHeight = Math.floor(bottomPane.getHeight());
            double cellWidth = Math.floor(bottomPane1.getWidth());

            StackPane cellContainer = new StackPane();
            cellContainer.setMaxSize(cellHeight, cellWidth);
            cellContainer.setPrefSize(cellHeight, cellWidth);

            Image tileImage = new Image(getClass().getResourceAsStream(flightType == FlightType.FIRST_FLIGHT ? "/images/cardboard/cardboard_first.png" : "/images/cardboard/cardboard_standard.jpg"));
            ImageView imageView = new ImageView(tileImage);

            double ratio = tileImage.getWidth() / tileImage.getHeight();
            imageView.setFitHeight(cellHeight);
            imageView.setFitWidth(Math.floor(cellHeight * ratio));
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            Rectangle clip = new Rectangle(Math.floor(cellHeight * ratio), cellHeight);
            clip.setArcWidth(20.0);
            clip.setArcHeight(20.0);
            imageView.setClip(clip);

            StackPane borderContainer = new StackPane();
            borderContainer.getStyleClass().add("cell-border1");
            borderContainer.setMaxSize(Math.floor(cellHeight * ratio), cellHeight);
            borderContainer.setPrefSize(Math.floor(cellHeight * ratio), cellHeight);
            Rectangle borderClip = new Rectangle(Math.floor(cellHeight * ratio), cellHeight);
            borderClip.setArcWidth(20.0);
            borderClip.setArcHeight(20.0);
            borderContainer.setClip(borderClip);

            cellContainer.getChildren().addAll(imageView, borderContainer);
            cellContainer.setAlignment(Pos.BOTTOM_LEFT);

            bottomPane1.getChildren().clear();
            bottomPane1.getChildren().add(cellContainer);
            bottomPane1.setAlignment(Pos.BOTTOM_LEFT);

            shipGrid.setMaxHeight(cellHeight);
            shipGrid.setMaxWidth(Math.floor(cellHeight * ratio));

            double shipGridHeight = Math.floor(shipGrid.getMaxHeight() * 0.915);
            double shipGridWidth = Math.floor(shipGrid.getMaxWidth() * 0.93);
            double cellShipHeight = Math.floor(shipGridHeight / 5);
            double cellShipWidth = Math.floor(shipGridWidth / 7);
            double val = Math.floor(Math.min(cellShipWidth, cellShipHeight));

            shipGrid.getChildren().clear();

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    StackPane cellShipContainer = new StackPane();
                    cellShipContainer.setMaxSize(val, val);
                    cellShipContainer.setPrefSize(val, val);
                    cellShipContainer.setAlignment(Pos.CENTER);

                    ComponentTile componentTile = shipBoard.getSpace(i, j).getComponent();
                    StackPane borderShipContainer = new StackPane();
                    borderShipContainer.setMaxSize(val, val);
                    borderShipContainer.setPrefSize(val, val);
                    Rectangle borderShipClip = new Rectangle(val, val);
                    borderShipClip.setArcWidth(10.0);
                    borderShipClip.setArcHeight(10.0);
                    borderShipContainer.setClip(borderShipClip);

                    if (componentTile != null) {
                        StackPane tileContainer = createTileImage(componentTile, val);
                        borderShipContainer.getStyleClass().add("cell-border");
                        cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);

                        final int row = i;
                        final int col = j;
                        cellShipContainer.setOnMouseClicked(event -> {
                            errorShipLabel.setVisible(false);
                            gui.notifyObserver(obs -> obs.onUpdateRemoveTile(row + 5, col + 4));
                            fromShip = false;
                            tilesPane.getChildren().clear();
                            tilesPane.setVisible(false);
                        });
                    }
                    shipGrid.add(cellShipContainer, j, i);
                }
            }
            bottomPane1.getChildren().add(shipGrid);

            Button button = new Button();
            button.getStyleClass().add("my-button2");
            button.setText("Finish repairing");
            button.setWrapText(true);
            button.setAlignment(Pos.CENTER);
            button.setOnMouseClicked(event -> {
                repair = false;
                gui.notifyObserver(obs->obs.onUpdateFinishedBuild());

            });
            button.setMaxWidth(Double.MAX_VALUE);
            tempGrid.add(button, 0, 2);
            bottomPane2.setVisible(true);
        });
    }


    public void showWaitPlayers(String message) {
        Platform.runLater(() -> {
            tempGrid = new GridPane();
            tempGrid.setAlignment(Pos.CENTER_LEFT);
            tempGrid.setMaxWidth(bottomPane2.getWidth());
            tempGrid.setMaxHeight(bottomPane2.getHeight());
            tempGrid.setPrefWidth(bottomPane2.getWidth());
            tempGrid.setPrefHeight(bottomPane2.getHeight());

            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(15);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(60);
            RowConstraints row3 = new RowConstraints();
            row3.setPercentHeight(25);
            tempGrid.getRowConstraints().addAll(row1, row2, row3);

            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100);
            tempGrid.getColumnConstraints().add(col);

            bottomPane2.getChildren().clear();
            finish = true;
            bottomPane2.setAlignment(Pos.TOP_LEFT);

            Label label = new Label(message);
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER_LEFT);
            tempGrid.add(label, 0, 1);
            bottomPane2.getChildren().add(tempGrid);
            bottomPane2.setVisible(true);
        });
    }


    public void showWaitPopulate(String message) {
        Platform.runLater(() -> {
            tempGrid = new GridPane();
            tempGrid.setAlignment(Pos.CENTER_LEFT);
            tempGrid.setMaxWidth(bottomPane2.getWidth());
            tempGrid.setMaxHeight(bottomPane2.getHeight());
            tempGrid.setPrefWidth(bottomPane2.getWidth());
            tempGrid.setPrefHeight(bottomPane2.getHeight());

            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(15);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(60);
            RowConstraints row3 = new RowConstraints();
            row3.setPercentHeight(25);
            tempGrid.getRowConstraints().addAll(row1, row2, row3);

            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100);
            tempGrid.getColumnConstraints().add(col);

            bottomPane2.getChildren().clear();
            finish = true;
            bottomPane2.setAlignment(Pos.TOP_LEFT);

            Label label = new Label(message);
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER_LEFT);
            tempGrid.add(label, 0, 1);
            bottomPane2.getChildren().add(tempGrid);
            bottomPane2.setVisible(true);
        });
    }


    public void setPopulate() {
        this.populating = true;

        Platform.runLater(() -> {
            tempGrid = new GridPane();
            tempGrid.setAlignment(Pos.CENTER_LEFT);
            tempGrid.setMaxWidth(bottomPane2.getWidth());
            tempGrid.setMaxHeight(bottomPane2.getHeight());
            tempGrid.setPrefWidth(bottomPane2.getWidth());
            tempGrid.setPrefHeight(bottomPane2.getHeight());

            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(20);
            row1.setValignment(VPos.TOP);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(15);
            row2.setValignment(VPos.TOP);
            RowConstraints row5 = new RowConstraints();
            row5.setPercentHeight(25);
            row5.setValignment(VPos.TOP);

            RowConstraints row3 = new RowConstraints();
            row3.setPercentHeight(25);
            row3.setValignment(VPos.TOP);
            RowConstraints row4 = new RowConstraints();
            row4.setPercentHeight(15);
            row4.setValignment(VPos.BOTTOM);
            tempGrid.getRowConstraints().addAll(row1, row2, row5, row3, row4);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(33.33);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(33.33);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPercentWidth(33.34);
            tempGrid.getColumnConstraints().addAll(col1, col2, col3);

            bottomPane2.getChildren().clear();
            finish = true;
            bottomPane2.setAlignment(Pos.TOP_LEFT);

            String text1;
            String text2;
            if(flightType == FlightType.FIRST_FLIGHT) {
                text1 = "You have to populate your own ship!!!";
                text2 = "INSTRUCTIONS:\nYou can put maximum two astronauts for every cabine of your ship.";
            }
            else {
                text1 = "You have to populate your own ship. If you don't insert astronauts, you will be automatically eliminated from the flight!!!";
                text2 = "INSTRUCTIONS:\nA cabin that is not joined to a life support system gets 2 humans.\n" +
                        "A cabin joined to a life support system gets 2 humans or 1 alien of the corresponding color.\n" +
                        "A cabin joined to one life support system of each color gets either 2 humans or 1 purple alien or 1 brown alien.\n" +
                        "Your board can have no more than 1 alien of each color.\n" +
                        "You cannot put an alien in your starting cabine.";
            }

            Label label1 = new Label(text1);
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setMaxWidth(Double.MAX_VALUE);
            label1.setAlignment(Pos.TOP_LEFT);

            GridPane.setColumnSpan(label1, 3);
            tempGrid.add(label1, 0, 0);

            label5.getStyleClass().add("my-button8");
            label5.setWrapText(true);
            label5.setAlignment(Pos.TOP_LEFT);
            label5.setVisible(false);
            GridPane.setColumnSpan(label5, 3);
            tempGrid.add(label5, 0, 2);

            Label label2 = new Label(text2);
            label2.getStyleClass().add("my-button6");
            label2.setWrapText(true);
            label2.setAlignment(Pos.CENTER_LEFT);
            GridPane.setColumnSpan(label2, 3);
            tempGrid.add(label2, 0, 3);
            bottomPane2.getChildren().add(tempGrid);
            bottomPane2.setAlignment(Pos.TOP_LEFT);
            bottomPane2.setVisible(true);

            Button button1 = new Button();
            button1.getStyleClass().add("my-button2");
            button1.setText("Finish populating");
            button1.setWrapText(true);
            button1.setMaxWidth(Double.MAX_VALUE);
            button1.setAlignment(Pos.CENTER);
            button1.setOnMouseClicked(event -> {
               gui.notifyObserver(obs->obs.onUpdateFinishedPopulate());

            });
            GridPane.setColumnSpan(button1, 3);
            tempGrid.add(button1, 0, 4);

            if(flightType == FlightType.FIRST_FLIGHT) {
                ToggleGroup figureGroup = new ToggleGroup();
                ToggleButton button2 = new ToggleButton();
                button2.getStyleClass().add("my-button7");
                button2.setText("Put astronaut");
                button2.setWrapText(true);
                button2.setToggleGroup(figureGroup);
                button2.setMaxWidth(bottomPane2.getWidth()/3 - 10);
                button2.setAlignment(Pos.CENTER);
                button2.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        this.figure = "astronaut";
                        label5.setVisible(false);
                    }
                    else {
                        this.figure = null;
                        label5.setVisible(false);
                    }
                });
                tempGrid.add(button2, 0, 1);
            }
            else {
                ToggleGroup figureGroup = new ToggleGroup();
                ToggleButton button2 = new ToggleButton();
                button2.getStyleClass().add("my-button7");
                button2.setText("Put astronaut");
                button2.setWrapText(true);
                button2.setToggleGroup(figureGroup);
                button2.setMaxWidth(bottomPane2.getWidth()/3 - 10);
                button2.setAlignment(Pos.CENTER);
                button2.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        this.figure = "astronaut";
                        label5.setVisible(false);
                    }
                    else {
                        this.figure = null;
                        label5.setVisible(false);
                    }
                });
                tempGrid.add(button2, 0, 1);

                ToggleButton button3 = new ToggleButton();
                button3.getStyleClass().add("my-button7");
                button3.setText("Put brown alien");
                button3.setWrapText(true);
                button3.setToggleGroup(figureGroup);
                button3.setMaxWidth(bottomPane2.getWidth()/3 - 10);
                button3.setAlignment(Pos.CENTER);
                button3.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        this.figure = "brown alien";
                        label5.setVisible(false);
                    }
                    else {
                        this.figure = null;
                        label5.setVisible(false);
                    }
                });
                tempGrid.add(button3, 1, 1);

                ToggleButton button4 = new ToggleButton();
                button4.getStyleClass().add("my-button7");
                button4.setText("Put purple alien");
                button4.setWrapText(true);
                button4.setToggleGroup(figureGroup);
                button4.setMaxWidth(bottomPane2.getWidth()/3 - 10);
                button4.setAlignment(Pos.CENTER);
                button4.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        label5.setVisible(false);
                        this.figure = "purple alien";
                    }
                    else {
                        this.figure = null;
                        label5.setVisible(false);
                    }
                });
                tempGrid.add(button4, 2, 1);
            }
        });
    }


    public void finishBuilding() {
        if (this.remainingPositions == 0) {
            errorShipLabel.setVisible(false);
            tilesPane.setVisible(false);
            isViewingShips = true;
            hasFinishedBuilding = true;
            isViewingSpecificTile = false;
            isViewingCardsPiles = false;
            gui.notifyObserver(obs -> obs.onUpdateFinishedBuild());
        }
    }
}