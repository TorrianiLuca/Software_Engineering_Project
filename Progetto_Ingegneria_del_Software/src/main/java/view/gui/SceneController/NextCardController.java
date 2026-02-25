package view.gui.SceneController;

import enumerations.CardName;
import enumerations.FlightType;
import enumerations.TileName;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.*;

import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import model.card.Card;
import model.card.CardPile;
import model.card.cardsType.CombatZone;
import model.card.cardsType.ForReadJson.Meteor;
import model.card.cardsType.MeteorSwarm;
import model.card.cardsType.Pirates;
import model.card.cardsType.Planets;
import model.flightBoard.FlightBoard;
import model.player.Player;
import model.shipBoard.ShipBoard;
import model.tiles.ComponentTile;
import model.tiles.GameTile;
import model.tiles.componentTile.*;
import support.Couple;
import support.GameInfo;
import support.Quadruple;
import view.gui.Gui;
import view.tui.Font;
import view.tui.Tui;

/**
 * This class represents the next card Controller, which controls the second section of the game scenes.
 * The second section of the game starts when the players have finished to build their ships and have to pick up the cards.
 */
public class NextCardController {
    @FXML private StackPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private StackPane bottomPane;
    @FXML private StackPane bottomPane1;
    @FXML private StackPane bottomPane2;
    @FXML private StackPane topPane1;
    @FXML private StackPane topPane2;
    @FXML private GridPane buttonGrid;
    @FXML private GridPane shipGrid;
    @FXML private GridPane gridPane;
    @FXML private GridPane gridPane1;

    private Button buttonBack;
    private GridPane tempGrid = new GridPane();
    private StackPane tilesPane;
    private GridPane shipsPane = new GridPane();
    private Label errorLabel = new Label();

    private FlightType flightType;
    private ShipBoard shipBoard;
    private Card cardInUse;
    private ArrayList<Card> cardsToPlay;

    private boolean isViewingShips = false;
    private boolean retired = false;
    private boolean notClearTopPane2 = false;
    private boolean shipHit = false;
    private boolean watchShips = false;
    private boolean notRepaired = false;

    private ArrayList<ArrayList<Integer>> positionsFirst;
    private ArrayList<ArrayList<Integer>> positionsStandard;

    private String state;
    private int finSum = 0;

    private final Gui gui;

    public NextCardController(Gui gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> rootPane.requestFocus());

        this.populateArray();

        errorLabel.getStyleClass().add("my-button8");
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);

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
            }
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0 && rootPane.getWidth() > 0) {
                updateLayout();
            }
        });

        Platform.runLater(() -> {
            if (rootPane.getWidth() > 0 && rootPane.getHeight() > 0) {
                updateLayout();
            }
        });
    }

    public void showCardsToPlay(ArrayList<Card> cardsToPlay) {
        int num = cardsToPlay.size();
        this.cardsToPlay = cardsToPlay;
        isViewingShips = false;
        watchShips = false;

        Platform.runLater(() -> {
            gridPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane1)) {
                gridPane1.getChildren().add(topPane1);
            }

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane1.getChildren().clear();
            topPane1.setMaxWidth(Double.MAX_VALUE);
            topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane1.setAlignment(Pos.TOP_CENTER);

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label = new Label("These are the cards.");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label, Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));

            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            StackPane cardsContainer = new StackPane();
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            int i = 1;
            for(Card card : cardsToPlay) {
                Image cardImage;
                ImageView imageView;
                if(card.getLevel()==1) {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                }
                else {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                }

                double ratio = cardImage.getWidth() / cardImage.getHeight();
                imageView = new ImageView(cardImage);
                imageView.setFitHeight(val);
                imageView.setFitWidth(val*ratio);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setTranslateX(10*num+10-i*20);

                Rectangle clip = new Rectangle(val*ratio, val);
                clip.setArcWidth(20.0);
                clip.setArcHeight(20.0);
                imageView.setClip(clip);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");
                borderContainer.setMaxHeight(val);
                borderContainer.setMaxWidth(val*ratio);
                borderContainer.setTranslateX(10*num+10-i*20);

                VBox cellContainer = new VBox(borderContainer);
                cellContainer.setMaxHeight(val);
                cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                VBox.setVgrow(borderContainer, Priority.ALWAYS);

                cardsContainer.getChildren().addAll(imageView, cellContainer);
                cardsContainer.setAlignment(Pos.BOTTOM_CENTER);
                i++;
            }
            VBox contentContainer = new VBox(label, cardsContainer);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            topPane1.getChildren().add(contentContainer);
            topPane1.setVisible(true);

            if(flightType == FlightType.FIRST_FLIGHT) {
                Label label1 = new Label("You can proceed with the next card...");
                label1.getStyleClass().add("my-button3");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                Button button1 = new Button("Proceed");
                button1.getStyleClass().add("my-button1");
                button1.setAlignment(Pos.CENTER);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateProceed());
                });

                VBox container = new VBox(label1, button1);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);
            }
            else {
                Label label1 = new Label("You can proceed with the next card, or retire from the game...");
                label1.getStyleClass().add("my-button3");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                Button button1 = new Button("Proceed");
                button1.getStyleClass().add("my-button1");
                button1.setAlignment(Pos.CENTER);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateProceed());

                });
                VBox.setMargin(button1, new Insets(0, 0, 100, 0));

                Button button2 = new Button("Retire");
                button2.getStyleClass().add("my-button2");
                button2.setAlignment(Pos.CENTER);
                button2.setMaxWidth(gridPane.getWidth()/5);
                button2.setPrefWidth(gridPane.getWidth()/5);
                button2.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateRetire());
                    retired = true;
                });

                VBox container = new VBox(label1, button1, button2);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);
            }
        });
    }


    public void proceedToNextCard(Card card, ArrayList<Card> cardsToPlay) {
        this.cardsToPlay = cardsToPlay;

        Platform.runLater(() -> {
            isViewingShips = false;
            if(buttonBack!=null) {
                buttonBack.setVisible(false);
            }
            shipsPane.getChildren().clear();
            shipsPane.setVisible(false);
            gridPane1.setVisible(true);
            topPane1.setVisible(true);
            topPane2.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane1)) {
                gridPane1.getChildren().add(topPane1);
            }

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane1.getChildren().clear();
            topPane1.setMaxWidth(Double.MAX_VALUE);
            topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane1.setAlignment(Pos.TOP_CENTER);

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label = new Label("This is the card picked.");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label, Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));

            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            StackPane cardsContainer = new StackPane();
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            Image cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + card.getUrl()));
            ImageView imageView;
            double ratio = cardImage.getWidth() / cardImage.getHeight();
            imageView = new ImageView(cardImage);
            imageView.setFitHeight(val);
            imageView.setFitWidth(val*ratio);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            Rectangle clip = new Rectangle(val*ratio, val);
            clip.setArcWidth(20.0);
            clip.setArcHeight(20.0);
            imageView.setClip(clip);

            StackPane borderContainer = new StackPane();
            borderContainer.getStyleClass().add("cell-border1");
            borderContainer.setMaxHeight(val);
            borderContainer.setMaxWidth(val*ratio);

            VBox cellContainer = new VBox(borderContainer);
            cellContainer.setMaxHeight(val);
            cellContainer.setAlignment(Pos.BOTTOM_CENTER);
            GridPane.setHgrow(cellContainer, Priority.ALWAYS);
            VBox.setVgrow(borderContainer, Priority.ALWAYS);

            cardsContainer.getChildren().addAll(imageView, cellContainer);
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            VBox contentContainer = new VBox(label, cardsContainer);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            topPane1.getChildren().add(contentContainer);
            topPane1.setVisible(true);
            if(shipBoard!=null) {
                this.updateShipBoard(shipBoard);
            }

            if(flightType == FlightType.FIRST_FLIGHT) {
                Label label2 = new Label("The effect of the card is finished...");
                label2.getStyleClass().add("my-button3");
                label2.setWrapText(true);
                label2.setAlignment(Pos.CENTER);
                VBox.setMargin(label, new Insets(0, 0, 10, 0));

                Label label1 = new Label("You can proceed with the next card...");
                label1.getStyleClass().add("my-button3");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                Button button1 = new Button("Proceed");
                button1.getStyleClass().add("my-button1");
                button1.setAlignment(Pos.CENTER);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateProceed());
                });

                VBox container = new VBox(label2, label1, button1);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);
            }
            else {
                Label label2 = new Label("The effect of the card is finished...");
                label2.getStyleClass().add("my-button3");
                label2.setWrapText(true);
                label2.setAlignment(Pos.CENTER);
                VBox.setMargin(label, new Insets(0, 0, 10, 0));

                Label label1 = new Label("You can proceed with the next card, or retire from the game...");
                label1.getStyleClass().add("my-button3");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                Button button1 = new Button("Proceed");
                button1.getStyleClass().add("my-button1");
                button1.setAlignment(Pos.CENTER);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateProceed());

                });
                VBox.setMargin(button1, new Insets(0, 0, 100, 0));

                Button button2 = new Button("Retire");
                button2.getStyleClass().add("my-button2");
                button2.setAlignment(Pos.CENTER);
                button2.setMaxWidth(gridPane.getWidth()/5);
                button2.setPrefWidth(gridPane.getWidth()/5);
                button2.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateRetire());
                    retired = true;
                });

                VBox container = new VBox(label2, label1, button1, button2);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);
            }
        });
    }


    public void proceedToNextPhase(Card card) {
        this.cardInUse = card;

        Platform.runLater(() -> {
            isViewingShips = false;
            if(buttonBack!=null) {
                buttonBack.setVisible(false);
            }
            shipsPane.getChildren().clear();
            shipsPane.setVisible(false);
            gridPane1.setVisible(true);
            topPane1.setVisible(true);
            topPane2.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane1)) {
                gridPane1.getChildren().add(topPane1);
            }

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane1.getChildren().clear();
            topPane1.setMaxWidth(Double.MAX_VALUE);
            topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane1.setAlignment(Pos.TOP_CENTER);

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label = new Label("This is the card picked.");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label, Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));

            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            StackPane cardsContainer = new StackPane();
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            Image cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + card.getUrl()));
            ImageView imageView;
            double ratio = cardImage.getWidth() / cardImage.getHeight();
            imageView = new ImageView(cardImage);
            imageView.setFitHeight(val);
            imageView.setFitWidth(val*ratio);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            Rectangle clip = new Rectangle(val*ratio, val);
            clip.setArcWidth(20.0);
            clip.setArcHeight(20.0);
            imageView.setClip(clip);

            StackPane borderContainer = new StackPane();
            borderContainer.getStyleClass().add("cell-border1");
            borderContainer.setMaxHeight(val);
            borderContainer.setMaxWidth(val*ratio);

            VBox cellContainer = new VBox(borderContainer);
            cellContainer.setMaxHeight(val);
            cellContainer.setAlignment(Pos.BOTTOM_CENTER);
            GridPane.setHgrow(cellContainer, Priority.ALWAYS);
            VBox.setVgrow(borderContainer, Priority.ALWAYS);

            cardsContainer.getChildren().addAll(imageView, cellContainer);
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            VBox contentContainer = new VBox(label, cardsContainer);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            topPane1.getChildren().add(contentContainer);
            topPane1.setVisible(true);
            if(shipBoard!=null) {
                this.updateShipBoard(shipBoard);
            }

            Label label2 = new Label("The phase is finished...");
            label2.getStyleClass().add("my-button3");
            label2.setWrapText(true);
            label2.setAlignment(Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));

            Label label1 = new Label("You can proceed with the next phase...");
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

            Button button1 = new Button("Next phase");
            button1.getStyleClass().add("my-button1");
            button1.setAlignment(Pos.CENTER);
            button1.setMaxWidth(Double.MAX_VALUE);
            button1.setOnAction(event -> {
                gui.notifyObserver(obs -> obs.onUpdateNextPhase(cardInUse));
            });

            VBox container = new VBox(label2, label1, button1);
            container.setAlignment(Pos.CENTER);

            topPane2.getChildren().addAll(container);
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
                            switch(cargo.getCargosIn().get(2)) {
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
        else if(tile.getName() == TileName.ENGINE) {
            Engine engine = (Engine) tile;
            if(engine.getActive()) {
                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(20);
                innerShadow.setColor(Color.GREEN);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);
            }
        }
        else if(tile.getName() == TileName.CANNON) {
            Cannon cannon = (Cannon) tile;
            if(cannon.getActive()) {
                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(20);
                innerShadow.setColor(Color.GREEN);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);
            }
        }
        else if(tile.getName() == TileName.SHIELD) {
            Shield shield = (Shield) tile;
            if(shield.getActive()) {
                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(20);
                innerShadow.setColor(Color.GREEN);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);
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
        clip.setArcWidth(20.0);
        clip.setArcHeight(20.0);
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

        if(notRepaired) {
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
                            gui.notifyObserver(obs -> obs.onUpdateRemoveTile(row + 5, col + 4));
                            tilesPane.getChildren().clear();
                            tilesPane.setVisible(false);
                        });
                    }
                    shipGrid.add(cellShipContainer, j, i);
                }
            }
            bottomPane1.getChildren().add(shipGrid);
        }
        else {
            if (this.state == null) {
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
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("open space") || this.state.equals("phase 2 level 1") || this.state.equals("phase 2 level 2")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.ENGINE) {
                                Engine engine = (Engine) componentTile;
                                if (!engine.getActive()) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateActivateEngine(cardInUse, row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("smugglers") || this.state.equals("meteor swarm large") || this.state.equals("slavers") || this.state.equals("pirates") || this.state.equals("phase 3 level 1") || this.state.equals("phase 1 level 2")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.CANNON) {
                                Cannon cannon = (Cannon) componentTile;
                                if (!cannon.getActive()) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateActivateCannon(cardInUse, row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("meteor swarm small") || this.state.equals("cannon fire light")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.SHIELD) {
                                Shield shield = (Shield) componentTile;
                                if (!shield.getActive()) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateActivateShield(cardInUse, row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("remove battery")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.BATTERY) {
                                Battery battery = (Battery) componentTile;
                                if (battery.getNumBatteriesInUse() > 0) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateRemoveBattery(cardInUse, row + 5, col + 4, finSum));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("remove figure")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.CABINE) {
                                Cabine cabine = (Cabine) componentTile;
                                if (cabine.getNumFigures() > 0 || cabine.getHasPurpleAlien() || cabine.getHasBrownAlien()) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateRemoveFigure(cardInUse, row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else if (componentTile.getName() == TileName.STARTING_CABINE) {
                                StartingCabine startingCabine = (StartingCabine) componentTile;
                                if (startingCabine.getNumFigures() > 0) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateRemoveFigure(cardInUse, row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("remove good")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.CARGO) {
                                Cargo cargo = (Cargo) componentTile;
                                if (cargo.getNumOccupiedCargos() > 0) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateRemoveGood(cardInUse, row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else if (this.state.equals("gain good")) {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);

                            if (componentTile.getName() == TileName.CARGO) {
                                Cargo cargo = (Cargo) componentTile;
                                if (cargo.getNumOccupiedCargos() < cargo.getNumMaxCargos()) {
                                    borderShipContainer.getStyleClass().add("cell-border");
                                    final int row = i;
                                    final int col = j;
                                    cellShipContainer.setOnMouseClicked(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdateGainGood(cardInUse, "put", row + 5, col + 4));
                                        this.state = null;
                                        errorLabel.setVisible(false);
                                    });
                                } else {
                                    borderShipContainer.getStyleClass().add("cell-border1");
                                }
                            } else {
                                borderShipContainer.getStyleClass().add("cell-border1");
                            }
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            } else {
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
                            StackPane tileContainer;
                            tileContainer = createTileImage(componentTile, val);
                            borderShipContainer.getStyleClass().add("cell-border1");
                            cellShipContainer.getChildren().addAll(tileContainer, borderShipContainer);
                        }
                        shipGrid.add(cellShipContainer, j, i);
                    }
                }
                bottomPane1.getChildren().add(shipGrid);
            }
        }
    }


    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public void showShips(HashMap<String, ShipBoard> shipBoards) {
        if(isViewingShips) {
            Platform.runLater(() -> {
                if(buttonBack != null) {
                    buttonBack.setVisible(false);
                }
                gridPane1.setVisible(false);

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

                buttonBack = new Button("Back");
                buttonBack.getStyleClass().add("my-button1");
                GridPane.setHgrow(buttonBack, Priority.ALWAYS);
                GridPane.setVgrow(buttonBack, Priority.ALWAYS);
                GridPane.setFillWidth(buttonBack, true);
                GridPane.setFillHeight(buttonBack, true);
                StackPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
                buttonBack.setMaxWidth(gridPane.getWidth()/5);
                buttonBack.setOnAction(event -> {
                    isViewingShips = false;
                    shipsPane.setVisible(false);
                    buttonBack.setVisible(false);
                    gui.notifyObserver(obs -> obs.onUpdateStopWatchingShips(cardInUse));
                });
                bottomPane2.getChildren().add(buttonBack);
            });
        }
        else if (watchShips) {
            Platform.runLater(() -> {
                if(buttonBack != null) {
                    buttonBack.setVisible(false);
                }
                gridPane1.setVisible(false);

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


    public void showShipErrors(ArrayList<String> errors) {
        if(buttonBack != null) {
            buttonBack.setVisible(false);
        }
        this.notRepaired = true;

        Platform.runLater(() -> {
            topPane2.getChildren().clear();

            tempGrid.getChildren().clear();
            tempGrid = new GridPane();
            tempGrid.setAlignment(Pos.CENTER_LEFT);
            tempGrid.setMaxWidth(bottomPane2.getWidth());
            tempGrid.setMaxHeight(bottomPane2.getHeight());
            tempGrid.setPrefWidth(bottomPane2.getWidth());
            tempGrid.setPrefHeight(bottomPane2.getHeight());

            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(75);
            row1.setValignment(VPos.TOP);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(25);
            row2.setValignment(VPos.BOTTOM);
            tempGrid.getRowConstraints().addAll(row1, row2);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(100);
            tempGrid.getColumnConstraints().add(col1);

            bottomPane2.getChildren().clear();
            bottomPane2.setAlignment(Pos.TOP_LEFT);

            String text = "Your ship board is not correct. Repair your ship!!\n";
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
            tempGrid.add(label, 0, 0);
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
                            gui.notifyObserver(obs -> obs.onUpdateRemoveTile(row + 5, col + 4));
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
                this.notRepaired = false;
                gui.notifyObserver(obs->obs.onUpdateRepaired(cardInUse));
            });
            button.setMaxWidth(Double.MAX_VALUE);
            tempGrid.add(button, 0, 1);
            bottomPane2.setVisible(true);
        });
    }


    public void showRetireMessage(String message) {
        retired = true;

        Platform.runLater(() -> {
            gridPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefWidth(gridPane.getWidth()/2);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label1 = new Label(message);
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            topPane2.getChildren().addAll(label1);
        });
    }


    public void waitPlayersToProceed() {
        Platform.runLater(() -> {
            gridPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefWidth(gridPane.getWidth()/2);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label1 = new Label("Waiting for other players to proceed...");
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);

            topPane2.getChildren().addAll(label1);
        });
    }


    public void showRetired(FlightBoard flightBoard, Player player) {
        retired = true;
        bottomPane2.setVisible(true);
        int num1 = cardsToPlay.size();
        this.cardInUse = null;

        Platform.runLater(() -> {
            double cellHeight = Math.floor(bottomPane2.getHeight());
            double cellWidth = Math.floor(bottomPane2.getWidth()*0.82);

            StackPane cellContainer = new StackPane();
            cellContainer.setMaxSize(cellHeight, cellWidth);
            cellContainer.setPrefSize(cellHeight, cellWidth);

            if(flightType == FlightType.FIRST_FLIGHT) {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_first.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsFirst.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/355);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }
            else {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_standard.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsStandard.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/388);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                 bottomPane2.getChildren().clear();
                 bottomPane2.getChildren().add(cellContainer);
                 StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }

            int count = player.getShipBoard().getNumFigures()
                    + (player.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                    + (player.getShipBoard().getHasBrownAlien() ? 1 : 0);

            String text = "These are your parametres:\n" +
                    "  Color:  " + player.getColor() + "\n" +
                    "  Cosmic credits:  " + player.getCosmicCredit() + "\n" +
                    "  Number of figures:  " + count + "\n" +
                    "  Number of batteries:  " + player.getShipBoard().getNumBatteries() + "\n" +
                    "  Cannon strength:  " + player.getShipBoard().calculateFireStrength() + "\n" +
                    "  Engine strength:  " + player.getShipBoard().calculateEngineStrength() + "\n" +
                    "  Exposed Connectors:  " + player.getShipBoard().numExposedConnectors() + "\n" +
                    "  Lost tiles:  " + player.getShipBoard().getNumLostTiles();

            Label label = new Label(text);
            label.getStyleClass().add("my-button4");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);

            StackPane.setAlignment(label, Pos.TOP_LEFT);
            bottomPane2.getChildren().add(label);

            if (!gridPane1.getChildren().contains(topPane1)) {
                gridPane1.getChildren().add(topPane1);
            }

            topPane1.getChildren().clear();
            topPane1.setMaxWidth(Double.MAX_VALUE);
            topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane1.setAlignment(Pos.TOP_CENTER);

            Label label4 = new Label("These are the cards.");
            label4.getStyleClass().add("my-button3");
            label4.setWrapText(true);
            label4.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label4, Pos.CENTER);
            VBox.setMargin(label4, new Insets(0, 0, 10, 0));

            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            StackPane cardsContainer = new StackPane();
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            int i = 1;
            for(Card card : cardsToPlay) {
                Image cardImage;
                ImageView imageView;
                if(card.getLevel()==1) {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                }
                else {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                }

                double ratio = cardImage.getWidth() / cardImage.getHeight();
                imageView = new ImageView(cardImage);
                imageView.setFitHeight(val);
                imageView.setFitWidth(val*ratio);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setTranslateX(10*num1+10-i*20);

                Rectangle clip = new Rectangle(val*ratio, val);
                clip.setArcWidth(20.0);
                clip.setArcHeight(20.0);
                imageView.setClip(clip);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");
                borderContainer.setMaxHeight(val);
                borderContainer.setMaxWidth(val*ratio);
                borderContainer.setTranslateX(10*num1+10-i*20);

                VBox cellContainer1 = new VBox(borderContainer);
                cellContainer1.setMaxHeight(val);
                cellContainer1.setAlignment(Pos.BOTTOM_CENTER);
                GridPane.setHgrow(cellContainer1, Priority.ALWAYS);
                VBox.setVgrow(borderContainer, Priority.ALWAYS);

                cardsContainer.getChildren().addAll(imageView, cellContainer1);
                cardsContainer.setAlignment(Pos.BOTTOM_CENTER);
                i++;
            }
            VBox contentContainer = new VBox(label4, cardsContainer);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            topPane1.getChildren().add(contentContainer);
            topPane1.setVisible(true);

            topPane2.getChildren().clear();
            topPane2.setVisible(true);

            Label label1 = new Label("You are not in the flight anymore. Watch the other player!!");
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

            Button button1 = new Button("Show ships");
            button1.getStyleClass().add("my-button1");
            button1.setAlignment(Pos.CENTER);
            button1.setMaxWidth(Double.MAX_VALUE);
            button1.setOnAction(event -> {
                gui.notifyObserver(obs -> obs.onUpdateShowShips());
                errorLabel.setVisible(false);
                this.isViewingShips = true;
                this.state = "show ships";
            });

            VBox.setMargin(button1, new Insets(0, 0, 20, 0));
            VBox container = new VBox(label1, button1, errorLabel);
            container.setAlignment(Pos.CENTER);
            topPane2.getChildren().addAll(container);

            if(isViewingShips) {
                buttonBack = new Button("Back");
                buttonBack.getStyleClass().add("my-button1");
                GridPane.setHgrow(buttonBack, Priority.ALWAYS);
                GridPane.setVgrow(buttonBack, Priority.ALWAYS);
                GridPane.setFillWidth(buttonBack, true);
                GridPane.setFillHeight(buttonBack, true);
                StackPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
                buttonBack.setMaxWidth(gridPane.getWidth()/5);
                buttonBack.setOnAction(event -> {
                    isViewingShips = false;
                    shipsPane.setVisible(false);
                    buttonBack.setVisible(false);
                    gui.notifyObserver(obs -> obs.onUpdateStopWatchingShips(cardInUse));
                });
                bottomPane2.getChildren().add(buttonBack);
            }
        });
    }


    public void showLeader(FlightBoard flightBoard, Player player) {
        bottomPane2.setVisible(true);
        int num1 = cardsToPlay.size();

        Platform.runLater(() -> {
            gridPane1.setVisible(true);
            gridPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane1)) {
                gridPane1.getChildren().add(topPane1);
            }

            topPane1.getChildren().clear();
            topPane1.setMaxWidth(Double.MAX_VALUE);
            topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane1.setAlignment(Pos.TOP_CENTER);

            Label label = new Label("These are the cards.");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label, Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));

            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            StackPane cardsContainer = new StackPane();
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            int i = 1;
            for(Card card : cardsToPlay) {
                Image cardImage;
                ImageView imageView;
                if(card.getLevel()==1) {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                }
                else {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                }

                double ratio = cardImage.getWidth() / cardImage.getHeight();
                imageView = new ImageView(cardImage);
                imageView.setFitHeight(val);
                imageView.setFitWidth(val*ratio);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setTranslateX(10*num1+10-i*20);

                Rectangle clip = new Rectangle(val*ratio, val);
                clip.setArcWidth(20.0);
                clip.setArcHeight(20.0);
                imageView.setClip(clip);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");
                borderContainer.setMaxHeight(val);
                borderContainer.setMaxWidth(val*ratio);
                borderContainer.setTranslateX(10*num1+10-i*20);

                VBox cellContainer = new VBox(borderContainer);
                cellContainer.setMaxHeight(val);
                cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                VBox.setVgrow(borderContainer, Priority.ALWAYS);

                cardsContainer.getChildren().addAll(imageView, cellContainer);
                cardsContainer.setAlignment(Pos.BOTTOM_CENTER);
                i++;
            }
            VBox contentContainer = new VBox(label, cardsContainer);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            topPane1.getChildren().add(contentContainer);
            topPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefWidth(gridPane.getWidth()/2);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label1 = new Label("You are the leader. You have to pick up a card!!!");
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

            Button button = new Button("Pick up card");
            button.getStyleClass().add("my-button1");
            button.setAlignment(Pos.CENTER);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> {
                gui.notifyObserver(obs->obs.onUpdatePickCard());
                topPane1.setVisible(false);
                topPane2.setVisible(false);
            });

            VBox container = new VBox(label1, button);
            container.setAlignment(Pos.CENTER);

            topPane2.getChildren().addAll(container);

            double cellHeight = Math.floor(bottomPane2.getHeight());
            double cellWidth = Math.floor(bottomPane2.getWidth()*0.82);

            StackPane cellContainer = new StackPane();
            cellContainer.setMaxSize(cellHeight, cellWidth);
            cellContainer.setPrefSize(cellHeight, cellWidth);

            if(flightType == FlightType.FIRST_FLIGHT) {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_first.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsFirst.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/355);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }
            else {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_standard.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsStandard.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/388);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }

            int count = player.getShipBoard().getNumFigures()
                    + (player.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                    + (player.getShipBoard().getHasBrownAlien() ? 1 : 0);

            String text = "These are your parametres:\n" +
                    "  Color:  " + player.getColor() + "\n" +
                    "  Cosmic credits:  " + player.getCosmicCredit() + "\n" +
                    "  Number of figures:  " + count + "\n" +
                    "  Number of batteries:  " + player.getShipBoard().getNumBatteries() + "\n" +
                    "  Cannon strength:  " + player.getShipBoard().calculateFireStrength() + "\n" +
                    "  Engine strength:  " + player.getShipBoard().calculateEngineStrength() + "\n" +
                    "  Exposed Connectors:  " + player.getShipBoard().numExposedConnectors() + "\n" +
                    "  Lost tiles:  " + player.getShipBoard().getNumLostTiles();

            Label label3 = new Label(text);
            label3.getStyleClass().add("my-button4");
            label3.setWrapText(true);
            label3.setAlignment(Pos.CENTER);

            StackPane.setAlignment(label3, Pos.TOP_LEFT);
            bottomPane2.getChildren().add(label3);
        });
    }


    public void showNotLeader(FlightBoard flightBoard, Player player) {
        bottomPane2.setVisible(true);
        int num1 = cardsToPlay.size();

        Platform.runLater(() -> {
            gridPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane1)) {
                gridPane1.getChildren().add(topPane1);
            }

            topPane1.getChildren().clear();
            topPane1.setMaxWidth(Double.MAX_VALUE);
            topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane1.setAlignment(Pos.TOP_CENTER);

            Label label = new Label("These are the cards.");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label, Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 10, 0));

            double val = Math.floor(gridPane.getPrefWidth() * 0.18);
            StackPane cardsContainer = new StackPane();
            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

            int i = 1;
            for(Card card : cardsToPlay) {
                Image cardImage;
                ImageView imageView;
                if(card.getLevel()==1) {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                }
                else {
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                }

                double ratio = cardImage.getWidth() / cardImage.getHeight();
                imageView = new ImageView(cardImage);
                imageView.setFitHeight(val);
                imageView.setFitWidth(val*ratio);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setTranslateX(10*num1+10-i*20);

                Rectangle clip = new Rectangle(val*ratio, val);
                clip.setArcWidth(20.0);
                clip.setArcHeight(20.0);
                imageView.setClip(clip);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");
                borderContainer.setMaxHeight(val);
                borderContainer.setMaxWidth(val*ratio);
                borderContainer.setTranslateX(10*num1+10-i*20);

                VBox cellContainer = new VBox(borderContainer);
                cellContainer.setMaxHeight(val);
                cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                VBox.setVgrow(borderContainer, Priority.ALWAYS);

                cardsContainer.getChildren().addAll(imageView, cellContainer);
                cardsContainer.setAlignment(Pos.BOTTOM_CENTER);
                i++;
            }
            VBox contentContainer = new VBox(label, cardsContainer);
            contentContainer.setAlignment(Pos.TOP_CENTER);
            topPane1.getChildren().add(contentContainer);
            topPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefWidth(gridPane.getWidth()/2);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label1 = new Label("You are not the leader. You have to wait the leader to pick up the card!!");
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            VBox.setMargin(label1, new Insets(0, 0, 10, 0));
            topPane2.getChildren().addAll(label1);

            double cellHeight = Math.floor(bottomPane2.getHeight());
            double cellWidth = Math.floor(bottomPane2.getWidth()*0.82);

            StackPane cellContainer = new StackPane();
            cellContainer.setMaxSize(cellHeight, cellWidth);
            cellContainer.setPrefSize(cellHeight, cellWidth);

            if(flightType == FlightType.FIRST_FLIGHT) {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_first.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsFirst.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/355);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }
            else {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_standard.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();
                    ArrayList<Integer> values = positionsStandard.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/388);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }

            int count = player.getShipBoard().getNumFigures()
                    + (player.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                    + (player.getShipBoard().getHasBrownAlien() ? 1 : 0);

            String text = "These are your parametres:\n" +
                    "  Color:  " + player.getColor() + "\n" +
                    "  Cosmic credits:  " + player.getCosmicCredit() + "\n" +
                    "  Number of figures:  " + count + "\n" +
                    "  Number of batteries:  " + player.getShipBoard().getNumBatteries() + "\n" +
                    "  Cannon strength:  " + player.getShipBoard().calculateFireStrength() + "\n" +
                    "  Engine strength:  " + player.getShipBoard().calculateEngineStrength() + "\n" +
                    "  Exposed Connectors:  " + player.getShipBoard().numExposedConnectors() + "\n" +
                    "  Lost tiles:  " + player.getShipBoard().getNumLostTiles();

            Label label3 = new Label(text);
            label3.getStyleClass().add("my-button4");
            label3.setWrapText(true);
            label3.setAlignment(Pos.CENTER);

            StackPane.setAlignment(label3, Pos.TOP_LEFT);
            bottomPane2.getChildren().add(label3);
        });
    }


    public void updatePlayerParametres(FlightBoard flightBoard, Player player) {
        bottomPane2.setVisible(true);

        Platform.runLater(() -> {
            double cellHeight = Math.floor(bottomPane2.getHeight());
            double cellWidth = Math.floor(bottomPane2.getWidth()*0.82);

            StackPane cellContainer = new StackPane();
            cellContainer.setMaxSize(cellHeight, cellWidth);
            cellContainer.setPrefSize(cellHeight, cellWidth);

            if(flightType == FlightType.FIRST_FLIGHT) {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_first.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsFirst.get(num);

                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/355);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);

                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }
            else {
                Image tileImage = new Image(getClass().getResourceAsStream("/images/cardboard/flightboard_standard.png"));
                ImageView imageView = new ImageView(tileImage);
                double ratio = tileImage.getWidth() / tileImage.getHeight();
                imageView.setFitHeight(Math.floor(cellWidth / ratio));
                imageView.setFitWidth(Math.floor(cellWidth));
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                StackPane borderContainer = new StackPane();
                borderContainer.getStyleClass().add("cell-border1");

                InnerShadow innerShadow = new InnerShadow();
                innerShadow.setRadius(4);
                innerShadow.setColor(Color.BLACK);
                innerShadow.setOffsetX(0);
                innerShadow.setOffsetY(0);
                innerShadow.setChoke(0.5);
                imageView.setEffect(innerShadow);

                Group imageGroup = new Group(imageView);
                HashMap<Player, Couple<Integer, Integer>> map = flightBoard.getPlayersMap();
                for (Player p : map.keySet()) {
                    int num = map.get(p).getFirst();

                    ArrayList<Integer> values = positionsStandard.get(num);
                    Circle circle = new Circle();
                    circle.setCenterX(values.get(0)*imageView.getFitWidth()/641);
                    circle.setCenterY(values.get(1)*imageView.getFitHeight()/388);
                    circle.setRadius(10);
                    switch (p.getColor()) {
                        case RED -> circle.setFill(Color.RED);
                        case BLUE -> circle.setFill(Color.BLUE);
                        case GREEN -> circle.setFill(Color.GREEN);
                        case YELLOW -> circle.setFill(Color.YELLOW);
                    }
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(2);
                    imageGroup.getChildren().add(circle);
                }

                cellContainer.getChildren().addAll(imageGroup);
                cellContainer.setAlignment(Pos.BOTTOM_RIGHT);

                bottomPane2.getChildren().clear();
                bottomPane2.getChildren().add(cellContainer);
                StackPane.setAlignment(cellContainer, Pos.BOTTOM_RIGHT);
            }

            int count = player.getShipBoard().getNumFigures()
                    + (player.getShipBoard().getHasPurpleAlien() ? 1 : 0)
                    + (player.getShipBoard().getHasBrownAlien() ? 1 : 0);

            String text = "These are your parametres:\n" +
                    "  Color:  " + player.getColor() + "\n" +
                    "  Cosmic credits:  " + player.getCosmicCredit() + "\n" +
                    "  Number of figures:  " + count + "\n" +
                    "  Number of batteries:  " + player.getShipBoard().getNumBatteries() + "\n" +
                    "  Cannon strength:  " + player.getShipBoard().calculateFireStrength() + "\n" +
                    "  Engine strength:  " + player.getShipBoard().calculateEngineStrength() + "\n" +
                    "  Exposed Connectors:  " + player.getShipBoard().numExposedConnectors() + "\n" +
                    "  Lost tiles:  " + player.getShipBoard().getNumLostTiles();

            Label label = new Label(text);
            label.getStyleClass().add("my-button4");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);

            StackPane.setAlignment(label, Pos.TOP_LEFT);
            bottomPane2.getChildren().add(label);

            if(isViewingShips) {
                buttonBack = new Button("Back");
                buttonBack.getStyleClass().add("my-button1");
                GridPane.setHgrow(buttonBack, Priority.ALWAYS);
                GridPane.setVgrow(buttonBack, Priority.ALWAYS);
                GridPane.setFillWidth(buttonBack, true);
                GridPane.setFillHeight(buttonBack, true);
                StackPane.setAlignment(buttonBack, Pos.BOTTOM_LEFT);
                buttonBack.setMaxWidth(gridPane.getWidth()/5);
                buttonBack.setOnAction(event -> {
                    isViewingShips = false;
                    shipsPane.setVisible(false);
                    buttonBack.setVisible(false);
                    gui.notifyObserver(obs -> obs.onUpdateStopWatchingShips(cardInUse));
                });
                bottomPane2.getChildren().add(buttonBack);
            }
        });
    }


    private void populateArray() {
        positionsFirst = new ArrayList<>();
        ArrayList<Integer> tempPosition1f = new ArrayList<>(List.of(170,80));
        ArrayList<Integer> tempPosition2f = new ArrayList<>(List.of(229,61));
        ArrayList<Integer> tempPosition3f = new ArrayList<>(List.of(289,53));
        ArrayList<Integer> tempPosition4f = new ArrayList<>(List.of(349,53));
        ArrayList<Integer> tempPosition5f = new ArrayList<>(List.of(409,62));
        ArrayList<Integer> tempPosition6f = new ArrayList<>(List.of(468,82));
        ArrayList<Integer> tempPosition7f = new ArrayList<>(List.of(522,118));
        ArrayList<Integer> tempPosition8f = new ArrayList<>(List.of(550,184));
        ArrayList<Integer> tempPosition9f = new ArrayList<>(List.of(518,243));
        ArrayList<Integer> tempPosition10f = new ArrayList<>(List.of(464,277));
        ArrayList<Integer> tempPosition11f = new ArrayList<>(List.of(406,295));
        ArrayList<Integer> tempPosition12f = new ArrayList<>(List.of(345,304));
        ArrayList<Integer> tempPosition13f = new ArrayList<>(List.of(285,303));
        ArrayList<Integer> tempPosition14f = new ArrayList<>(List.of(224,295));
        ArrayList<Integer> tempPosition15f = new ArrayList<>(List.of(165,273));
        ArrayList<Integer> tempPosition16f = new ArrayList<>(List.of(111,238));
        ArrayList<Integer> tempPosition17f = new ArrayList<>(List.of(84,172));
        ArrayList<Integer> tempPosition18f = new ArrayList<>(List.of(117,113));

        positionsFirst.add(tempPosition1f);
        positionsFirst.add(tempPosition2f);
        positionsFirst.add(tempPosition3f);
        positionsFirst.add(tempPosition4f);
        positionsFirst.add(tempPosition5f);
        positionsFirst.add(tempPosition6f);
        positionsFirst.add(tempPosition7f);
        positionsFirst.add(tempPosition8f);
        positionsFirst.add(tempPosition9f);
        positionsFirst.add(tempPosition10f);
        positionsFirst.add(tempPosition11f);
        positionsFirst.add(tempPosition12f);
        positionsFirst.add(tempPosition13f);
        positionsFirst.add(tempPosition14f);
        positionsFirst.add(tempPosition15f);
        positionsFirst.add(tempPosition16f);
        positionsFirst.add(tempPosition17f);
        positionsFirst.add(tempPosition18f);

        positionsStandard = new ArrayList<>();
        ArrayList<Integer> tempPosition1s = new ArrayList<>(List.of(155,91));
        ArrayList<Integer> tempPosition2s = new ArrayList<>(List.of(202,74));
        ArrayList<Integer> tempPosition3s = new ArrayList<>(List.of(248,64));
        ArrayList<Integer> tempPosition4s = new ArrayList<>(List.of(295,59));
        ArrayList<Integer> tempPosition5s = new ArrayList<>(List.of(344,59));
        ArrayList<Integer> tempPosition6s = new ArrayList<>(List.of(392,65));
        ArrayList<Integer> tempPosition7s = new ArrayList<>(List.of(438,76));
        ArrayList<Integer> tempPosition8s = new ArrayList<>(List.of(484,95));
        ArrayList<Integer> tempPosition9s = new ArrayList<>(List.of(527,125));
        ArrayList<Integer> tempPosition10s = new ArrayList<>(List.of(556,170));
        ArrayList<Integer> tempPosition11s = new ArrayList<>(List.of(552,227));
        ArrayList<Integer> tempPosition12s = new ArrayList<>(List.of(520,268));
        ArrayList<Integer> tempPosition13s = new ArrayList<>(List.of(478,295));
        ArrayList<Integer> tempPosition14s = new ArrayList<>(List.of(432,313));
        ArrayList<Integer> tempPosition15s = new ArrayList<>(List.of(385,324));
        ArrayList<Integer> tempPosition16s = new ArrayList<>(List.of(337,329));
        ArrayList<Integer> tempPosition17s = new ArrayList<>(List.of(289,328));
        ArrayList<Integer> tempPosition18s = new ArrayList<>(List.of(240,322));
        ArrayList<Integer> tempPosition19s = new ArrayList<>(List.of(193,310));
        ArrayList<Integer> tempPosition20s = new ArrayList<>(List.of(148,291));
        ArrayList<Integer> tempPosition21s = new ArrayList<>(List.of(107,262));
        ArrayList<Integer> tempPosition22s = new ArrayList<>(List.of(77,215));
        ArrayList<Integer> tempPosition23s = new ArrayList<>(List.of(81,160));
        ArrayList<Integer> tempPosition24s = new ArrayList<>(List.of(114,119));

        positionsStandard.add(tempPosition1s);
        positionsStandard.add(tempPosition2s);
        positionsStandard.add(tempPosition3s);
        positionsStandard.add(tempPosition4s);
        positionsStandard.add(tempPosition5s);
        positionsStandard.add(tempPosition6s);
        positionsStandard.add(tempPosition7s);
        positionsStandard.add(tempPosition8s);
        positionsStandard.add(tempPosition9s);
        positionsStandard.add(tempPosition10s);
        positionsStandard.add(tempPosition11s);
        positionsStandard.add(tempPosition12s);
        positionsStandard.add(tempPosition13s);
        positionsStandard.add(tempPosition14s);
        positionsStandard.add(tempPosition15s);
        positionsStandard.add(tempPosition16s);
        positionsStandard.add(tempPosition17s);
        positionsStandard.add(tempPosition18s);
        positionsStandard.add(tempPosition19s);
        positionsStandard.add(tempPosition20s);
        positionsStandard.add(tempPosition21s);
        positionsStandard.add(tempPosition22s);
        positionsStandard.add(tempPosition23s);
        positionsStandard.add(tempPosition24s);
    }


    public void setCardInUse(Card card) {
        this.cardInUse = card;
    }


    public void showCard(boolean inTurn) {
        Platform.runLater(() -> {
            if(!retired) {
                if(inTurn) {
                    isViewingShips = false;

                    if(buttonBack != null) {
                        buttonBack.setVisible(false);
                    }

                    if(gridPane.getChildren().contains(shipsPane)) {
                        gridPane.getChildren().remove(shipsPane);
                    }

                    if (!gridPane1.getChildren().contains(topPane1)) {
                        gridPane1.getChildren().add(topPane1);
                    }

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    gridPane1.setVisible(true);

                    topPane1.getChildren().clear();
                    topPane1.setMaxWidth(Double.MAX_VALUE);
                    topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane1.setPrefWidth(gridPane.getWidth() * 0.43);
                    topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane1.setAlignment(Pos.TOP_CENTER);

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    topPane1.setVisible(true);
                    topPane2.setVisible(true);

                    Label label = new Label("This is the card picked.");
                    label.getStyleClass().add("my-button3");
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(label, Pos.CENTER);
                    VBox.setMargin(label, new Insets(0, 0, 10, 0));

                    double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                    StackPane cardsContainer = new StackPane();
                    cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                    Image cardImage;
                    ImageView imageView;
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                    double ratio = cardImage.getWidth() / cardImage.getHeight();
                    imageView = new ImageView(cardImage);
                    imageView.setFitHeight(val);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);

                    Group imageGroup = new Group(imageView);
                    if(cardInUse.getCardType() == CardName.PLANETS) {
                        Planets planets = (Planets) cardInUse;
                        switch (cardInUse.getUrl()) {
                            case "12.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(13);
                                    circle1.setCenterY(68);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(137);
                                    circle2.setCenterY(122);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                if(planets.playerOnPlanet(3) != null) {
                                    Circle circle3 = new Circle();
                                    circle3.setCenterX(27);
                                    circle3.setCenterY(158);
                                    circle3.setRadius(13);
                                    circle3.setStroke(Color.BLACK);
                                    circle3.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(3)) {
                                        case RED -> circle3.setFill(Color.RED);
                                        case GREEN -> circle3.setFill(Color.GREEN);
                                        case YELLOW -> circle3.setFill(Color.YELLOW);
                                        case BLUE -> circle3.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle3);
                                }

                                if(planets.playerOnPlanet(4) != null) {
                                    Circle circle4 = new Circle();
                                    circle4.setCenterX(53);
                                    circle4.setCenterY(215);
                                    circle4.setRadius(13);
                                    circle4.setStroke(Color.BLACK);
                                    circle4.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(4)) {
                                        case RED -> circle4.setFill(Color.RED);
                                        case GREEN -> circle4.setFill(Color.GREEN);
                                        case YELLOW -> circle4.setFill(Color.YELLOW);
                                        case BLUE -> circle4.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle4);
                                }

                                break;
                            case "13.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(146);
                                    circle1.setCenterY(64);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(29);
                                    circle2.setCenterY(120);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                if(planets.playerOnPlanet(3) != null) {
                                    Circle circle3 = new Circle();
                                    circle3.setCenterX(122);
                                    circle3.setCenterY(172);
                                    circle3.setRadius(13);
                                    circle3.setStroke(Color.BLACK);
                                    circle3.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(3)) {
                                        case RED -> circle3.setFill(Color.RED);
                                        case GREEN -> circle3.setFill(Color.GREEN);
                                        case YELLOW -> circle3.setFill(Color.YELLOW);
                                        case BLUE -> circle3.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle3);
                                }

                                break;
                            case "14.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(149);
                                    circle1.setCenterY(67);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(38);
                                    circle2.setCenterY(134);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                break;
                            case "15.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(149);
                                    circle1.setCenterY(67);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(36);
                                    circle2.setCenterY(120);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                if(planets.playerOnPlanet(3) != null) {
                                    Circle circle3 = new Circle();
                                    circle3.setCenterX(135);
                                    circle3.setCenterY(172);
                                    circle3.setRadius(13);
                                    circle3.setStroke(Color.BLACK);
                                    circle3.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(3)) {
                                        case RED -> circle3.setFill(Color.RED);
                                        case GREEN -> circle3.setFill(Color.GREEN);
                                        case YELLOW -> circle3.setFill(Color.YELLOW);
                                        case BLUE -> circle3.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle3);
                                }

                                break;
                            case "33.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(149);
                                    circle1.setCenterY(67);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(31);
                                    circle2.setCenterY(119);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                if(planets.playerOnPlanet(3) != null) {
                                    Circle circle3 = new Circle();
                                    circle3.setCenterX(13);
                                    circle3.setCenterY(188);
                                    circle3.setRadius(13);
                                    circle3.setStroke(Color.BLACK);
                                    circle3.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(3)) {
                                        case RED -> circle3.setFill(Color.RED);
                                        case GREEN -> circle3.setFill(Color.GREEN);
                                        case YELLOW -> circle3.setFill(Color.YELLOW);
                                        case BLUE -> circle3.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle3);
                                }

                                break;
                            case "34.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(149);
                                    circle1.setCenterY(67);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(25);
                                    circle2.setCenterY(119);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }
                                break;
                            case "35.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(33);
                                    circle1.setCenterY(66);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(143);
                                    circle2.setCenterY(122);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                if(planets.playerOnPlanet(3) != null) {
                                    Circle circle3 = new Circle();
                                    circle3.setCenterX(37);
                                    circle3.setCenterY(161);
                                    circle3.setRadius(13);
                                    circle3.setStroke(Color.BLACK);
                                    circle3.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(3)) {
                                        case RED -> circle3.setFill(Color.RED);
                                        case GREEN -> circle3.setFill(Color.GREEN);
                                        case YELLOW -> circle3.setFill(Color.YELLOW);
                                        case BLUE -> circle3.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle3);
                                }

                                if(planets.playerOnPlanet(4) != null) {
                                    Circle circle4 = new Circle();
                                    circle4.setCenterX(43);
                                    circle4.setCenterY(216);
                                    circle4.setRadius(13);
                                    circle4.setStroke(Color.BLACK);
                                    circle4.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(4)) {
                                        case RED -> circle4.setFill(Color.RED);
                                        case GREEN -> circle4.setFill(Color.GREEN);
                                        case YELLOW -> circle4.setFill(Color.YELLOW);
                                        case BLUE -> circle4.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle4);
                                }
                                break;
                            case "36.png":
                                if(planets.playerOnPlanet(1) != null) {
                                    Circle circle1 = new Circle();
                                    circle1.setCenterX(151);
                                    circle1.setCenterY(69);
                                    circle1.setRadius(13);
                                    circle1.setStroke(Color.BLACK);
                                    circle1.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(1)) {
                                        case RED -> circle1.setFill(Color.RED);
                                        case GREEN -> circle1.setFill(Color.GREEN);
                                        case YELLOW -> circle1.setFill(Color.YELLOW);
                                        case BLUE -> circle1.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle1);
                                }

                                if(planets.playerOnPlanet(2) != null) {
                                    Circle circle2 = new Circle();
                                    circle2.setCenterX(30);
                                    circle2.setCenterY(113);
                                    circle2.setRadius(13);
                                    circle2.setStroke(Color.BLACK);
                                    circle2.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(2)) {
                                        case RED -> circle2.setFill(Color.RED);
                                        case GREEN -> circle2.setFill(Color.GREEN);
                                        case YELLOW -> circle2.setFill(Color.YELLOW);
                                        case BLUE -> circle2.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle2);
                                }

                                if(planets.playerOnPlanet(3) != null) {
                                    Circle circle3 = new Circle();
                                    circle3.setCenterX(144);
                                    circle3.setCenterY(171);
                                    circle3.setRadius(13);
                                    circle3.setStroke(Color.BLACK);
                                    circle3.setStrokeWidth(2);

                                    switch(planets.playerOnPlanet(3)) {
                                        case RED -> circle3.setFill(Color.RED);
                                        case GREEN -> circle3.setFill(Color.GREEN);
                                        case YELLOW -> circle3.setFill(Color.YELLOW);
                                        case BLUE -> circle3.setFill(Color.BLUE);
                                    }
                                    imageGroup.getChildren().add(circle3);
                                }
                                break;
                        }
                    }

                    Rectangle clip = new Rectangle(val*ratio, val);
                    clip.setArcWidth(20.0);
                    clip.setArcHeight(20.0);
                    imageView.setClip(clip);

                    StackPane borderContainer = new StackPane();
                    borderContainer.getStyleClass().add("cell-border1");
                    borderContainer.setMaxHeight(val);
                    borderContainer.setMaxWidth(val*ratio);

                    VBox cellContainer = new VBox(borderContainer);
                    cellContainer.setMaxHeight(val);
                    cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                    GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                    VBox.setVgrow(borderContainer, Priority.ALWAYS);

                    cardsContainer.getChildren().addAll(imageGroup, cellContainer);
                    cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                    VBox contentContainer = new VBox(label, cardsContainer);
                    contentContainer.setAlignment(Pos.TOP_CENTER);
                    StackPane.setAlignment(contentContainer, Pos.TOP_CENTER);

                    topPane1.getChildren().add(contentContainer);
                    topPane1.setVisible(true);

                    switch (cardInUse.getCardType()) {
                        case OPEN_SPACE:
                            Label label1 = new Label();

                            if(flightType == FlightType.FIRST_FLIGHT) {
                                label1.setText("It's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength, or proceed in the open space with only your single engines. Then you move that many empty (the declared engine strength) space forward...");
                            }
                            else {
                                label1.setText("It's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength, or proceed in the open space with only your single engines. Then you move that many empty (the declared engine strength) space forward..." +
                                        "\nIf your declared engine strength is 0, you will be removed from the flight!!");
                            }

                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            Button button1 = new Button("Move");
                            button1.getStyleClass().add("my-button1");
                            button1.setAlignment(Pos.CENTER);
                            button1.setMaxWidth(Double.MAX_VALUE);
                            button1.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateOpenSpaceChoice(cardInUse));
                                errorLabel.setVisible(false);
                            });

                            VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                            VBox container = new VBox(label1, button1, errorLabel);
                            container.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container);

                            this.state = "open space";
                            this.updateShipBoard(shipBoard);

                            break;

                        case ABANDONED_SHIP:
                            Label label2 = new Label();
                            label2.setText("It's your turn. You can decide to dock with the abandoned ship (if you have enough figures) gaining goods, loosing flight days and figures, or skip the opportunity...");

                            label2.getStyleClass().add("my-button4");
                            label2.setWrapText(true);
                            label2.setAlignment(Pos.CENTER);
                            VBox.setMargin(label2, new Insets(0, 0, 10, 0));

                            Button button2 = new Button("Dock");
                            button2.getStyleClass().add("my-button1");
                            button2.setAlignment(Pos.CENTER);
                            button2.setMaxWidth(Double.MAX_VALUE);
                            button2.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateAbandonedShipChoice(cardInUse, "dock"));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button2, new Insets(0, 0, 10, 0));

                            Button button3 = new Button("Skip");
                            button3.getStyleClass().add("my-button1");
                            button3.setAlignment(Pos.CENTER);
                            button3.setMaxWidth(Double.MAX_VALUE);
                            button3.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateAbandonedShipChoice(cardInUse, "skip"));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button3, new Insets(0, 0, 20, 0));

                            VBox container1 = new VBox(label2, button2, button3, errorLabel);
                            container1.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container1);

                            this.state = "abandoned ship";
                            this.updateShipBoard(shipBoard);

                            break;

                        case ABANDONED_STATION:
                            Label label3 = new Label();
                            label3.setText("It's your turn. You can decide to dock with the space station (if you have enough figures) gaining goods and loosing flight days, or skip the opportunity...");

                            label3.getStyleClass().add("my-button4");
                            label3.setWrapText(true);
                            label3.setAlignment(Pos.CENTER);
                            VBox.setMargin(label3, new Insets(0, 0, 10, 0));

                            Button button4 = new Button("Dock");
                            button4.getStyleClass().add("my-button1");
                            button4.setAlignment(Pos.CENTER);
                            button4.setMaxWidth(Double.MAX_VALUE);
                            button4.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateAbandonedStationChoice(cardInUse, "dock"));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button4, new Insets(0, 0, 10, 0));

                            Button button5 = new Button("Skip");
                            button5.getStyleClass().add("my-button1");
                            button5.setAlignment(Pos.CENTER);
                            button5.setMaxWidth(Double.MAX_VALUE);
                            button5.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateAbandonedStationChoice(cardInUse, "skip"));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button5, new Insets(0, 0, 20, 0));

                            VBox container2 = new VBox(label3, button4, button5, errorLabel);
                            container2.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container2);

                            this.state = "abandoned station";
                            this.updateShipBoard(shipBoard);

                            break;

                        case SMUGGLERS:
                            Label label4 = new Label();
                            label4.setText("It's your turn. You can decide to activate the double cannons to fight the smugglers (if you don't have enough fire strength), or proceed in the fight...");

                            label4.getStyleClass().add("my-button4");
                            label4.setWrapText(true);
                            label4.setAlignment(Pos.CENTER);
                            VBox.setMargin(label4, new Insets(0, 0, 10, 0));

                            Button button6 = new Button("Fight");
                            button6.getStyleClass().add("my-button1");
                            button6.setAlignment(Pos.CENTER);
                            button6.setMaxWidth(Double.MAX_VALUE);
                            button6.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateSmugglersChoice(cardInUse));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button6, new Insets(0, 0, 10, 0));

                            VBox container3 = new VBox(label4, button6, errorLabel);
                            container3.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container3);

                            this.state = "smugglers";
                            this.updateShipBoard(shipBoard);

                            break;

                        case SLAVERS:
                            Label label5 = new Label();
                            label5.setText("It's your turn. You can decide to activate the double cannons to fight the slavers (if you don't have enough fire strength), or proceed in the fight...");

                            label5.getStyleClass().add("my-button4");
                            label5.setWrapText(true);
                            label5.setAlignment(Pos.CENTER);
                            VBox.setMargin(label5, new Insets(0, 0, 10, 0));

                            Button button7 = new Button("Fight");
                            button7.getStyleClass().add("my-button1");
                            button7.setAlignment(Pos.CENTER);
                            button7.setMaxWidth(Double.MAX_VALUE);
                            button7.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateSlaversChoice(cardInUse));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button7, new Insets(0, 0, 10, 0));

                            VBox container4 = new VBox(label5, button7, errorLabel);
                            container4.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container4);

                            this.state = "slavers";
                            this.updateShipBoard(shipBoard);

                            break;

                        case PIRATES:
                            Label label6 = new Label();
                            label6.setText("It's your turn. You can decide to activate the double cannons to fight the pirates (if you don't have enough fire strength), or proceed in the fight...");

                            label6.getStyleClass().add("my-button4");
                            label6.setWrapText(true);
                            label6.setAlignment(Pos.CENTER);
                            VBox.setMargin(label6, new Insets(0, 0, 10, 0));

                            Button button8 = new Button("Fight");
                            button8.getStyleClass().add("my-button1");
                            button8.setAlignment(Pos.CENTER);
                            button8.setMaxWidth(Double.MAX_VALUE);
                            button8.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdatePiratesChoice(cardInUse));
                                errorLabel.setVisible(false);
                            });
                            VBox.setMargin(button8, new Insets(0, 0, 10, 0));

                            VBox container5 = new VBox(label6, button8, errorLabel);
                            container5.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container5);

                            this.state = "pirates";
                            this.updateShipBoard(shipBoard);

                            break;

                        case COMBAT_ZONE:
                            if(cardInUse.getLevel() == 1) {
                                CombatZone combatZone = (CombatZone) cardInUse;
                                if(combatZone.getFaseCounter()==1) {}
                                else if(combatZone.getFaseCounter()==2) {
                                    Label label7 = new Label();
                                    label7.setText("It's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength. The player with the lowest engine strength will lose 2 crew figures...");
                                    label7.getStyleClass().add("my-button4");
                                    label7.setWrapText(true);
                                    label7.setAlignment(Pos.CENTER);
                                    VBox.setMargin(label7, new Insets(0, 0, 10, 0));

                                    Button button9 = new Button("Proceed");
                                    button9.getStyleClass().add("my-button1");
                                    button9.setAlignment(Pos.CENTER);
                                    button9.setMaxWidth(Double.MAX_VALUE);
                                    button9.setOnAction(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdatePhase2Choice(cardInUse));
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button9, new Insets(0, 0, 10, 0));

                                    Button button10 = new Button("Show ships");
                                    button10.getStyleClass().add("my-button1");
                                    button10.setAlignment(Pos.CENTER);
                                    button10.setMaxWidth(Double.MAX_VALUE);
                                    button10.setOnAction(event -> {
                                        isViewingShips = true;
                                        gui.notifyObserver(obs -> obs.onUpdateShowShips());
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button10, new Insets(0, 0, 10, 0));

                                    VBox container6 = new VBox(label7, button9, button10, errorLabel);
                                    container6.setAlignment(Pos.CENTER);
                                    topPane2.getChildren().addAll(container6);

                                    this.state = "phase 2 level 1";
                                    this.updateShipBoard(shipBoard);
                                }
                                else if(combatZone.getFaseCounter()==3) {
                                    Label label7 = new Label();
                                    label7.setText("It's your turn. You can decide to activate the double cannons (if you have them) to increment your power strength. The player with the lowest power strength is threatened by some dangerous cannon fire...");
                                    label7.getStyleClass().add("my-button4");
                                    label7.setWrapText(true);
                                    label7.setAlignment(Pos.CENTER);
                                    VBox.setMargin(label7, new Insets(0, 0, 10, 0));

                                    Button button9 = new Button("Proceed");
                                    button9.getStyleClass().add("my-button1");
                                    button9.setAlignment(Pos.CENTER);
                                    button9.setMaxWidth(Double.MAX_VALUE);
                                    button9.setOnAction(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdatePhase3Choice(cardInUse));
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button9, new Insets(0, 0, 10, 0));

                                    Button button10 = new Button("Show ships");
                                    button10.getStyleClass().add("my-button1");
                                    button10.setAlignment(Pos.CENTER);
                                    button10.setMaxWidth(Double.MAX_VALUE);
                                    button10.setOnAction(event -> {
                                        isViewingShips = true;
                                        gui.notifyObserver(obs -> obs.onUpdateShowShips());
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button10, new Insets(0, 0, 10, 0));

                                    VBox container6 = new VBox(label7, button9, button10, errorLabel);
                                    container6.setAlignment(Pos.CENTER);
                                    topPane2.getChildren().addAll(container6);

                                    this.state = "phase 3 level 1";
                                    this.updateShipBoard(shipBoard);
                                }
                            }
                            else {
                                CombatZone combatZone = (CombatZone) cardInUse;
                                if(combatZone.getFaseCounter()==1) {
                                    Label label7 = new Label();
                                    label7.setText("It's your turn. You can decide to activate the double cannons (if you have them) to increment your power strength. The player with the lowest power strength will lose flight days...");
                                    label7.getStyleClass().add("my-button4");
                                    label7.setWrapText(true);
                                    label7.setAlignment(Pos.CENTER);
                                    VBox.setMargin(label7, new Insets(0, 0, 10, 0));

                                    Button button9 = new Button("Proceed");
                                    button9.getStyleClass().add("my-button1");
                                    button9.setAlignment(Pos.CENTER);
                                    button9.setMaxWidth(Double.MAX_VALUE);
                                    button9.setOnAction(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdatePhase1Choice(cardInUse));
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button9, new Insets(0, 0, 10, 0));

                                    Button button10 = new Button("Show ships");
                                    button10.getStyleClass().add("my-button1");
                                    button10.setAlignment(Pos.CENTER);
                                    button10.setMaxWidth(Double.MAX_VALUE);
                                    button10.setOnAction(event -> {
                                        isViewingShips = true;
                                        gui.notifyObserver(obs -> obs.onUpdateShowShips());
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button10, new Insets(0, 0, 10, 0));

                                    VBox container6 = new VBox(label7, button9, button10, errorLabel);
                                    container6.setAlignment(Pos.CENTER);
                                    topPane2.getChildren().addAll(container6);

                                    this.state = "phase 1 level 2";
                                    this.updateShipBoard(shipBoard);
                                }
                                else if(combatZone.getFaseCounter()==2) {
                                    Label label7 = new Label();
                                    label7.setText("It's your turn. You can decide to activate the double engines (if you have them) to increment your engine strength. The player with the lowest engine strength will lose 3 goods blocks...");
                                    label7.getStyleClass().add("my-button4");
                                    label7.setWrapText(true);
                                    label7.setAlignment(Pos.CENTER);
                                    VBox.setMargin(label7, new Insets(0, 0, 10, 0));

                                    Button button9 = new Button("Proceed");
                                    button9.getStyleClass().add("my-button1");
                                    button9.setAlignment(Pos.CENTER);
                                    button9.setMaxWidth(Double.MAX_VALUE);
                                    button9.setOnAction(event -> {
                                        gui.notifyObserver(obs -> obs.onUpdatePhase2Choice(cardInUse));
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button9, new Insets(0, 0, 10, 0));

                                    Button button10 = new Button("Show ships");
                                    button10.getStyleClass().add("my-button1");
                                    button10.setAlignment(Pos.CENTER);
                                    button10.setMaxWidth(Double.MAX_VALUE);
                                    button10.setOnAction(event -> {
                                        isViewingShips = true;
                                        gui.notifyObserver(obs -> obs.onUpdateShowShips());
                                        errorLabel.setVisible(false);
                                    });
                                    VBox.setMargin(button10, new Insets(0, 0, 10, 0));

                                    VBox container6 = new VBox(label7, button9, button10, errorLabel);
                                    container6.setAlignment(Pos.CENTER);
                                    topPane2.getChildren().addAll(container6);

                                    this.state = "phase 2 level 2";
                                    this.updateShipBoard(shipBoard);
                                }
                                else if(combatZone.getFaseCounter()==3) {
                                }
                            }

                            break;

                        case PLANETS:
                            Label label7 = new Label("It's your turn. You can decide to land on a planet and lose the indicated number of flight days, or you can skip the planet and not receive goods...");
                            label7.getStyleClass().add("my-button4");
                            label7.setWrapText(true);
                            label7.setAlignment(Pos.CENTER);
                            VBox.setMargin(label7, new Insets(0, 0, 10, 0));

                            HBox hBox = new HBox();
                            Planets planets = (Planets) cardInUse;
                            if(planets.getNumberOfPlanets() == 2) {
                                Button button9 = new Button("Land 1");
                                button9.getStyleClass().add("my-button1");
                                button9.setAlignment(Pos.CENTER);
                                button9.setMaxWidth(Double.MAX_VALUE);
                                button9.setPrefWidth(topPane2.getWidth()/2);
                                button9.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 1));
                                    errorLabel.setVisible(false);
                                });

                                Button button10 = new Button("Land 2");
                                button10.getStyleClass().add("my-button1");
                                button10.setAlignment(Pos.CENTER);
                                button10.setMaxWidth(Double.MAX_VALUE);
                                button10.setPrefWidth(topPane2.getWidth()/2);
                                button10.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 2));
                                    errorLabel.setVisible(false);
                                });

                                hBox.getChildren().addAll(button9, button10);
                                hBox.setSpacing(5);
                                VBox.setMargin(hBox, new Insets(0, 0, 10, 0));
                            }
                            else if(planets.getNumberOfPlanets() == 3) {
                                Button button9 = new Button("Land 1");
                                button9.getStyleClass().add("my-button1");
                                button9.setAlignment(Pos.CENTER);
                                button9.setMaxWidth(Double.MAX_VALUE);
                                button9.setPrefWidth(topPane2.getWidth()/3);
                                button9.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 1));
                                    errorLabel.setVisible(false);
                                });

                                Button button10 = new Button("Land 2");
                                button10.getStyleClass().add("my-button1");
                                button10.setAlignment(Pos.CENTER);
                                button10.setMaxWidth(Double.MAX_VALUE);
                                button10.setPrefWidth(topPane2.getWidth()/3);
                                button10.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 2));
                                    errorLabel.setVisible(false);
                                });

                                Button button11 = new Button("Land 3");
                                button11.getStyleClass().add("my-button1");
                                button11.setAlignment(Pos.CENTER);
                                button11.setMaxWidth(Double.MAX_VALUE);
                                button11.setPrefWidth(topPane2.getWidth()/3);
                                button11.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 3));
                                    errorLabel.setVisible(false);
                                });
                                hBox.getChildren().addAll(button9, button10, button11);
                                hBox.setSpacing(5);
                                VBox.setMargin(hBox, new Insets(0, 0, 10, 0));

                            }
                            else {
                                Button button9 = new Button("Land 1");
                                button9.getStyleClass().add("my-button1");
                                button9.setAlignment(Pos.CENTER);
                                button9.setMaxWidth(Double.MAX_VALUE);
                                button9.setPrefWidth(topPane2.getWidth()/4);
                                button9.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 1));
                                    errorLabel.setVisible(false);
                                });

                                Button button10 = new Button("Land 2");
                                button10.getStyleClass().add("my-button1");
                                button10.setAlignment(Pos.CENTER);
                                button10.setMaxWidth(Double.MAX_VALUE);
                                button10.setPrefWidth(topPane2.getWidth()/4);
                                button10.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 2));
                                    errorLabel.setVisible(false);
                                });

                                Button button11 = new Button("Land 3");
                                button11.getStyleClass().add("my-button1");
                                button11.setAlignment(Pos.CENTER);
                                button11.setMaxWidth(Double.MAX_VALUE);
                                button11.setPrefWidth(topPane2.getWidth()/4);
                                button11.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 3));
                                    errorLabel.setVisible(false);
                                });

                                Button button12 = new Button("Land 4");
                                button12.getStyleClass().add("my-button1");
                                button12.setAlignment(Pos.CENTER);
                                button12.setMaxWidth(Double.MAX_VALUE);
                                button12.setPrefWidth(topPane2.getWidth()/4);
                                button12.setOnAction(event -> {
                                    gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "land", 4));
                                    errorLabel.setVisible(false);
                                });

                                hBox.getChildren().addAll(button9, button10, button11, button12);
                                hBox.setSpacing(5);
                                VBox.setMargin(hBox, new Insets(0, 0, 10, 0));
                            }
                            Button button13 = new Button("Skip");
                            button13.getStyleClass().add("my-button1");
                            button13.setAlignment(Pos.CENTER);
                            button13.setMaxWidth(Double.MAX_VALUE);
                            button13.setPrefWidth(topPane2.getWidth()/2);
                            button13.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdatePlanetChoice(cardInUse, "Skip", -1));
                                errorLabel.setVisible(false);
                            });

                            VBox.setMargin(button13, new Insets(0, 0, 20, 0));
                            VBox container6 = new VBox(label7, hBox, button13, errorLabel);
                            container6.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container6);

                            this.state = "planets";
                            this.updateShipBoard(shipBoard);

                            break;
                    }
                }
                else {
                    if(!isViewingShips) {
                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        if(!notClearTopPane2) {
                            topPane2.getChildren().clear();
                        }
                        notClearTopPane2 = false;
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Group imageGroup = new Group(imageView);
                        if(cardInUse.getCardType() == CardName.PLANETS) {
                            Planets planets = (Planets) cardInUse;

                            switch (cardInUse.getUrl()) {
                                case "12.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(13);
                                        circle1.setCenterY(68);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(137);
                                        circle2.setCenterY(122);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    if(planets.playerOnPlanet(3) != null) {
                                        Circle circle3 = new Circle();
                                        circle3.setCenterX(27);
                                        circle3.setCenterY(158);
                                        circle3.setRadius(13);
                                        circle3.setStroke(Color.BLACK);
                                        circle3.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(3)) {
                                            case RED -> circle3.setFill(Color.RED);
                                            case GREEN -> circle3.setFill(Color.GREEN);
                                            case YELLOW -> circle3.setFill(Color.YELLOW);
                                            case BLUE -> circle3.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle3);
                                    }

                                    if(planets.playerOnPlanet(4) != null) {
                                        Circle circle4 = new Circle();
                                        circle4.setCenterX(53);
                                        circle4.setCenterY(215);
                                        circle4.setRadius(13);
                                        circle4.setStroke(Color.BLACK);
                                        circle4.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(4)) {
                                            case RED -> circle4.setFill(Color.RED);
                                            case GREEN -> circle4.setFill(Color.GREEN);
                                            case YELLOW -> circle4.setFill(Color.YELLOW);
                                            case BLUE -> circle4.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle4);
                                    }

                                    break;
                                case "13.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(146);
                                        circle1.setCenterY(64);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(29);
                                        circle2.setCenterY(120);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    if(planets.playerOnPlanet(3) != null) {
                                        Circle circle3 = new Circle();
                                        circle3.setCenterX(122);
                                        circle3.setCenterY(172);
                                        circle3.setRadius(13);
                                        circle3.setStroke(Color.BLACK);
                                        circle3.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(3)) {
                                            case RED -> circle3.setFill(Color.RED);
                                            case GREEN -> circle3.setFill(Color.GREEN);
                                            case YELLOW -> circle3.setFill(Color.YELLOW);
                                            case BLUE -> circle3.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle3);
                                    }

                                    break;
                                case "14.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(149);
                                        circle1.setCenterY(67);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(38);
                                        circle2.setCenterY(134);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    break;
                                case "15.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(149);
                                        circle1.setCenterY(67);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(36);
                                        circle2.setCenterY(120);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    if(planets.playerOnPlanet(3) != null) {
                                        Circle circle3 = new Circle();
                                        circle3.setCenterX(135);
                                        circle3.setCenterY(172);
                                        circle3.setRadius(13);
                                        circle3.setStroke(Color.BLACK);
                                        circle3.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(3)) {
                                            case RED -> circle3.setFill(Color.RED);
                                            case GREEN -> circle3.setFill(Color.GREEN);
                                            case YELLOW -> circle3.setFill(Color.YELLOW);
                                            case BLUE -> circle3.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle3);
                                    }

                                    break;
                                case "33.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(149);
                                        circle1.setCenterY(67);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(31);
                                        circle2.setCenterY(119);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    if(planets.playerOnPlanet(3) != null) {
                                        Circle circle3 = new Circle();
                                        circle3.setCenterX(13);
                                        circle3.setCenterY(188);
                                        circle3.setRadius(13);
                                        circle3.setStroke(Color.BLACK);
                                        circle3.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(3)) {
                                            case RED -> circle3.setFill(Color.RED);
                                            case GREEN -> circle3.setFill(Color.GREEN);
                                            case YELLOW -> circle3.setFill(Color.YELLOW);
                                            case BLUE -> circle3.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle3);
                                    }

                                    break;
                                case "34.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(149);
                                        circle1.setCenterY(67);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(25);
                                        circle2.setCenterY(119);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }
                                    break;
                                case "35.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(33);
                                        circle1.setCenterY(66);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(143);
                                        circle2.setCenterY(122);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    if(planets.playerOnPlanet(3) != null) {
                                        Circle circle3 = new Circle();
                                        circle3.setCenterX(37);
                                        circle3.setCenterY(161);
                                        circle3.setRadius(13);
                                        circle3.setStroke(Color.BLACK);
                                        circle3.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(3)) {
                                            case RED -> circle3.setFill(Color.RED);
                                            case GREEN -> circle3.setFill(Color.GREEN);
                                            case YELLOW -> circle3.setFill(Color.YELLOW);
                                            case BLUE -> circle3.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle3);
                                    }

                                    if(planets.playerOnPlanet(4) != null) {
                                        Circle circle4 = new Circle();
                                        circle4.setCenterX(43);
                                        circle4.setCenterY(216);
                                        circle4.setRadius(13);
                                        circle4.setStroke(Color.BLACK);
                                        circle4.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(4)) {
                                            case RED -> circle4.setFill(Color.RED);
                                            case GREEN -> circle4.setFill(Color.GREEN);
                                            case YELLOW -> circle4.setFill(Color.YELLOW);
                                            case BLUE -> circle4.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle4);
                                    }
                                    break;
                                case "36.png":
                                    if(planets.playerOnPlanet(1) != null) {
                                        Circle circle1 = new Circle();
                                        circle1.setCenterX(151);
                                        circle1.setCenterY(69);
                                        circle1.setRadius(13);
                                        circle1.setStroke(Color.BLACK);
                                        circle1.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(1)) {
                                            case RED -> circle1.setFill(Color.RED);
                                            case GREEN -> circle1.setFill(Color.GREEN);
                                            case YELLOW -> circle1.setFill(Color.YELLOW);
                                            case BLUE -> circle1.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle1);
                                    }

                                    if(planets.playerOnPlanet(2) != null) {
                                        Circle circle2 = new Circle();
                                        circle2.setCenterX(30);
                                        circle2.setCenterY(113);
                                        circle2.setRadius(13);
                                        circle2.setStroke(Color.BLACK);
                                        circle2.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(2)) {
                                            case RED -> circle2.setFill(Color.RED);
                                            case GREEN -> circle2.setFill(Color.GREEN);
                                            case YELLOW -> circle2.setFill(Color.YELLOW);
                                            case BLUE -> circle2.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle2);
                                    }

                                    if(planets.playerOnPlanet(3) != null) {
                                        Circle circle3 = new Circle();
                                        circle3.setCenterX(144);
                                        circle3.setCenterY(171);
                                        circle3.setRadius(13);
                                        circle3.setStroke(Color.BLACK);
                                        circle3.setStrokeWidth(2);

                                        switch(planets.playerOnPlanet(3)) {
                                            case RED -> circle3.setFill(Color.RED);
                                            case GREEN -> circle3.setFill(Color.GREEN);
                                            case YELLOW -> circle3.setFill(Color.YELLOW);
                                            case BLUE -> circle3.setFill(Color.BLUE);
                                        }
                                        imageGroup.getChildren().add(circle3);
                                    }
                                    break;
                            }
                        }

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageGroup, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        Label label1 = new Label("You are not in turn. Wait the other players...");
                        label1.getStyleClass().add("my-button3");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Show ships");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateShowShips());
                            errorLabel.setVisible(false);
                            this.isViewingShips = true;
                            this.state = "show ships";

                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));

                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);

                        topPane2.getChildren().addAll(container);
                    }
                }
            }
        });
    }


    public void showCardRetired() {
        Platform.runLater(() -> {
            if(retired && !isViewingShips) {
                if(cardInUse != null) {
                    if(gridPane.getChildren().contains(shipsPane)) {
                        gridPane.getChildren().remove(shipsPane);
                    }

                    if (!gridPane1.getChildren().contains(topPane1)) {
                        gridPane1.getChildren().add(topPane1);
                    }

                    gridPane1.setVisible(true);

                    topPane1.getChildren().clear();
                    topPane1.setMaxWidth(Double.MAX_VALUE);
                    topPane1.setMaxHeight(gridPane.getHeight() * 0.432);

                    topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane1.setAlignment(Pos.TOP_CENTER);

                    topPane1.setVisible(true);
                    topPane2.setVisible(true);

                    Label label = new Label("This is the card picked.");
                    label.getStyleClass().add("my-button3");
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(label, Pos.CENTER);
                    VBox.setMargin(label, new Insets(0, 0, 10, 0));

                    double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                    StackPane cardsContainer = new StackPane();
                    cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                    Image cardImage;
                    ImageView imageView;
                    cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));

                    double ratio = cardImage.getWidth() / cardImage.getHeight();
                    imageView = new ImageView(cardImage);
                    imageView.setFitHeight(val);
                    imageView.setFitWidth(val*ratio);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);

                    Rectangle clip = new Rectangle(val*ratio, val);
                    clip.setArcWidth(20.0);
                    clip.setArcHeight(20.0);
                    imageView.setClip(clip);

                    StackPane borderContainer = new StackPane();
                    borderContainer.getStyleClass().add("cell-border1");
                    borderContainer.setMaxHeight(val);
                    borderContainer.setMaxWidth(val*ratio);

                    VBox cellContainer = new VBox(borderContainer);
                    cellContainer.setMaxHeight(val);
                    cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                    GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                    VBox.setVgrow(borderContainer, Priority.ALWAYS);

                    cardsContainer.getChildren().addAll(imageView, cellContainer);
                    cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                    VBox contentContainer = new VBox(label, cardsContainer);
                    contentContainer.setAlignment(Pos.TOP_CENTER);
                    topPane1.getChildren().add(contentContainer);
                    topPane1.setVisible(true);
                }
                else {
                    int num = cardsToPlay.size();

                    Platform.runLater(() -> {
                        gridPane1.setVisible(true);

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        topPane1.setVisible(true);
                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        Label label = new Label("These are the cards.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        int i = 1;
                        for (Card card : cardsToPlay) {
                            Image cardImage;
                            ImageView imageView;
                            if (card.getLevel() == 1) {
                                cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_first.jpg"));
                            } else {
                                cardImage = new Image(getClass().getResourceAsStream("/images/cards/card_standard.jpg"));
                            }

                            double ratio = cardImage.getWidth() / cardImage.getHeight();
                            imageView = new ImageView(cardImage);
                            imageView.setFitHeight(val);
                            imageView.setFitWidth(val * ratio);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setTranslateX(10 * num + 10 - i * 20);

                            Rectangle clip = new Rectangle(val * ratio, val);
                            clip.setArcWidth(20.0);
                            clip.setArcHeight(20.0);
                            imageView.setClip(clip);

                            StackPane borderContainer = new StackPane();
                            borderContainer.getStyleClass().add("cell-border1");
                            borderContainer.setMaxHeight(val);
                            borderContainer.setMaxWidth(val * ratio);
                            borderContainer.setTranslateX(10 * num + 10 - i * 20);

                            VBox cellContainer = new VBox(borderContainer);
                            cellContainer.setMaxHeight(val);
                            cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                            GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                            VBox.setVgrow(borderContainer, Priority.ALWAYS);

                            cardsContainer.getChildren().addAll(imageView, cellContainer);
                            cardsContainer.setAlignment(Pos.BOTTOM_CENTER);
                            i++;
                        }
                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);
                    });
                }
            }
        });
    }


    public void setRemoveBattery() {
        Platform.runLater(() -> {
            if(!retired) {
                this.isViewingShips = false;
                gridPane1.setVisible(true);

                if(buttonBack != null) {
                    buttonBack.setVisible(false);
                }

                if (!gridPane1.getChildren().contains(topPane2)) {
                    gridPane1.getChildren().add(topPane2);
                }

                if(gridPane.getChildren().contains(shipsPane)) {
                    gridPane.getChildren().remove(shipsPane);
                }

                topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);
                    topPane2.setVisible(true);

                    Label label = new Label("You have to remove a battery...");
                    label.getStyleClass().add("my-button3");
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(label, Pos.CENTER);
                    VBox.setMargin(label, new Insets(0, 0, 20, 0));

                    VBox container = new VBox(label, errorLabel);
                    container.setAlignment(Pos.CENTER);

                    topPane2.getChildren().addAll(container);

                    this.state = "remove battery";
                    this.updateShipBoard(shipBoard);
            }
        });
    }


    public void setRemoveFigure() {
        Platform.runLater(() -> {
            if(!retired) {
                this.isViewingShips = false;

                if (!gridPane1.getChildren().contains(topPane2)) {
                    gridPane1.getChildren().add(topPane2);
                }

                if(gridPane.getChildren().contains(shipsPane)) {
                    gridPane.getChildren().remove(shipsPane);
                }

                gridPane1.setVisible(true);
                if(buttonBack != null) {
                    buttonBack.setVisible(false);
                }

                topPane2.getChildren().clear();
                topPane2.setMaxWidth(Double.MAX_VALUE);
                topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                topPane2.setAlignment(Pos.CENTER);
                topPane2.setVisible(true);

                Label label = new Label("You have to remove a figure...");
                label.getStyleClass().add("my-button3");
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label, Pos.CENTER);
                VBox.setMargin(label, new Insets(0, 0, 20, 0));

                VBox container = new VBox(label, errorLabel);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);

                this.state = "remove figure";
                this.updateShipBoard(shipBoard);
            }
        });
    }

    public void setRemoveGood() {
        Platform.runLater(() -> {
            if(!retired) {
                this.isViewingShips = false;

                if(gridPane.getChildren().contains(shipsPane)) {
                    gridPane.getChildren().remove(shipsPane);
                }

                if (!gridPane1.getChildren().contains(topPane2)) {
                    gridPane1.getChildren().add(topPane2);
                }

                gridPane1.setVisible(true);
                if(buttonBack != null) {
                    buttonBack.setVisible(false);
                }

                topPane2.getChildren().clear();
                topPane2.setMaxWidth(Double.MAX_VALUE);
                topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                topPane2.setAlignment(Pos.CENTER);
                topPane2.setVisible(true);

                Label label = new Label("You have to remove from your ship the rarest goods block...");
                label.getStyleClass().add("my-button3");
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label, Pos.CENTER);
                VBox.setMargin(label, new Insets(0, 0, 20, 0));

                VBox container = new VBox(label, errorLabel);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);

                this.state = "remove good";
                this.updateShipBoard(shipBoard);
            }
        });
    }


    public void showGoods(Card card, ArrayList<enumerations.Color> tempGoodsBlock) {
        Platform.runLater(() -> {
            if(!retired) {
                if (!gridPane1.getChildren().contains(topPane2)) {
                    gridPane1.getChildren().add(topPane2);
                }

                if(gridPane.getChildren().contains(shipsPane)) {
                    gridPane.getChildren().remove(shipsPane);
                }

                gridPane1.setVisible(true);
                if(buttonBack != null) {
                    buttonBack.setVisible(false);
                }

                topPane2.getChildren().clear();
                topPane2.setMaxWidth(Double.MAX_VALUE);
                topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                topPane2.setAlignment(Pos.CENTER);
                topPane2.setVisible(true);

                Label label = new Label("You can now decide to put every block in your ship board one by one.");
                label.getStyleClass().add("my-button3");
                label.setWrapText(true);
                label.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label, Pos.CENTER);
                VBox.setMargin(label, new Insets(0, 0, 10, 0));

                Label newLabel = new Label();
                newLabel.getStyleClass().add("my-button3");
                newLabel.setWrapText(true);
                newLabel.setAlignment(Pos.CENTER);
                newLabel.setTextAlignment(TextAlignment.CENTER);

                Rectangle square = new Rectangle(20, 20);
                square.setStroke(Color.BLACK);
                square.setStrokeWidth(2);
                square.setArcWidth(10);
                square.setArcHeight(10);
                square.setStroke(Color.BLACK);

                switch(tempGoodsBlock.get(0)) {
                    case RED -> square.setFill(Color.RED);
                    case BLUE -> square.setFill(Color.BLUE);
                    case GREEN -> square.setFill(Color.GREEN);
                    case YELLOW -> square.setFill(Color.YELLOW);
                }

                HBox labelContent = new HBox(10);
                labelContent.setAlignment(Pos.CENTER);
                Label textPart = new Label("Goods block received: ");
                textPart.getStyleClass().add("my-button9");
                labelContent.getChildren().addAll(textPart, square);
                newLabel.setGraphic(labelContent);
                VBox.setMargin(newLabel, new Insets(10, 0, 10, 0));

                Button button1 = new Button("Skip");
                button1.getStyleClass().add("my-button1");
                button1.setAlignment(Pos.CENTER);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateGainGood(cardInUse, "skip", -1, -1));
                    errorLabel.setVisible(false);
                    this.isViewingShips = false;
                    this.state = "show ships";

                });
                VBox.setMargin(newLabel, new Insets(0, 0, 10, 0));

                VBox container = new VBox(label, newLabel, button1, errorLabel);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);

                this.state = "gain good";
                this.updateShipBoard(shipBoard);
            }
        });
    }


    public void setRollDice(Card card, boolean inTurn, boolean proceed) {
        this.cardInUse = card;

        Platform.runLater(() -> {
            if(!retired) {
                if(card.getCardType() == CardName.METEOR_SWARM) {
                    if(((MeteorSwarm) cardInUse).getCounter() == 0 || proceed) {
                        isViewingShips = false;

                        if(buttonBack != null) {
                            buttonBack.setVisible(false);
                        }

                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        topPane2.getChildren().clear();
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageView, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        if(inTurn) {
                            Label label1 = new Label();
                            String text;
                            MeteorSwarm meteorSwarm = (MeteorSwarm) cardInUse;
                            Meteor meteor = (Meteor) meteorSwarm.getMeteor().get(meteorSwarm.getCounter());
                            String power;
                            if(meteor.getPower() == 1) {
                                power = new String("small");
                            }
                            else {
                                power = new String("large");
                            }

                            if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                                text = "It's your turn. You have to roll the dice to decide which number of column the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }
                            else {
                                text = "It's your turn. You have to roll the dice to decide which number of row the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }

                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            Button button1 = new Button("Roll dice");
                            button1.getStyleClass().add("my-button1");
                            button1.setAlignment(Pos.CENTER);
                            button1.setMaxWidth(Double.MAX_VALUE);
                            button1.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                                errorLabel.setVisible(false);
                            });

                            VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                            VBox container = new VBox(label1, button1, errorLabel);
                            container.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container);

                            this.state = "roll dice";
                            this.updateShipBoard(shipBoard);
                        }
                        else {
                            Label label1 = new Label();
                            String text;
                            MeteorSwarm meteorSwarm = (MeteorSwarm) cardInUse;
                            Meteor meteor = (Meteor) meteorSwarm.getMeteor().get(meteorSwarm.getCounter());
                            String power;
                            if(meteor.getPower() == 1) {
                                power = new String("small");
                            }
                            else {
                                power = new String("large");
                            }

                            if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                                text = "Wait for the leader to roll the dice to decide which number of column the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }
                            else {
                                text = "Wait for the leader to roll the dice to decide which number of row the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }

                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            topPane2.getChildren().addAll(label1);
                        }
                    }
                    else {
                        isViewingShips = false;

                        if(buttonBack != null) {
                            buttonBack.setVisible(false);
                        }

                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        if(shipHit) {
                            topPane2.getChildren().clear();
                            shipHit = false;
                        }
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageView, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        if(inTurn) {
                            Label label1 = new Label();
                            String text;
                            MeteorSwarm meteorSwarm = (MeteorSwarm) cardInUse;
                            Meteor meteor = (Meteor) meteorSwarm.getMeteor().get(meteorSwarm.getCounter());
                            String power;
                            if(meteor.getPower() == 1) {
                                power = new String("small");
                            }
                            else {
                                power = new String("large");
                            }

                            if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                                text = "It's your turn. You have to roll the dice to decide which number of column the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }
                            else {
                                text = "It's your turn. You have to roll the dice to decide which number of row the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }

                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            Button button1 = new Button("Roll dice");
                            button1.getStyleClass().add("my-button1");
                            button1.setAlignment(Pos.CENTER);
                            button1.setMaxWidth(Double.MAX_VALUE);
                            button1.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                                errorLabel.setVisible(false);
                            });

                            VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                            VBox container = new VBox(label1, button1, errorLabel);
                            container.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container);

                            this.state = "roll dice";
                            this.updateShipBoard(shipBoard);
                        }
                        else {
                            Label label1 = new Label();
                            String text;
                            MeteorSwarm meteorSwarm = (MeteorSwarm) cardInUse;
                            Meteor meteor = (Meteor) meteorSwarm.getMeteor().get(meteorSwarm.getCounter());
                            String power;
                            if(meteor.getPower() == 1) {
                                power = new String("small");
                            }
                            else {
                                power = new String("large");
                            }

                            if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                                text = "Wait for the leader to roll the dice to decide which number of column the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }
                            else {
                                text = "Wait for the leader to roll the dice to decide which number of row the meteor will hit. The meteor is coming from " +meteor.getDirection() + " and is a " + power + " meteor...";
                            }

                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            topPane2.getChildren().addAll(label1);
                        }
                    }
                }
                else if(card.getCardType() == CardName.PIRATES) {
                    if(((Pirates) cardInUse).getCounter() == 0 || proceed) {
                        isViewingShips = false;

                        if(buttonBack != null) {
                            buttonBack.setVisible(false);
                        }

                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        topPane2.getChildren().clear();
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageView, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        if(inTurn) {
                            Label label1 = new Label();
                            String text;

                            Pirates pirates = (Pirates) cardInUse;
                            int pow = pirates.getShotsPowerArray().get(pirates.getCounter());

                            String power;
                            if(pow == 1) {
                                power = new String("light");
                            }
                            else {
                                power = new String("heavy");
                            }

                            text = "It's your turn. You have to roll the dice to decide which number of column the cannon fire will hit. The cannon fire is coming from nord and is a " + power + " cannon fire...";
                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            Button button1 = new Button("Roll dice");
                            button1.getStyleClass().add("my-button1");
                            button1.setAlignment(Pos.CENTER);
                            button1.setMaxWidth(Double.MAX_VALUE);
                            button1.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                                errorLabel.setVisible(false);
                            });

                            VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                            VBox container = new VBox(label1, button1, errorLabel);
                            container.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container);

                            this.state = "roll dice";
                            this.updateShipBoard(shipBoard);
                        }
                        else {
                            Label label1 = new Label();
                            String text;

                            Pirates pirates = (Pirates) cardInUse;
                            int pow = pirates.getShotsPowerArray().get(pirates.getCounter());

                            String power;
                            if(pow == 1) {
                                power = new String("light");
                            }
                            else {
                                power = new String("heavy");
                            }

                            text = "Wait for the leader to roll the dice to decide which number of column the meteor will hit. The cannon fire is coming from nord and is a " + power + " cannon fire...";
                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            topPane2.getChildren().addAll(label1);
                        }
                    }
                    else {
                        isViewingShips = false;

                        if(buttonBack != null) {
                            buttonBack.setVisible(false);
                        }

                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        if(shipHit) {
                            topPane2.getChildren().clear();
                            shipHit = false;
                        }
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageView, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        if(inTurn) {
                            Label label1 = new Label();
                            String text;

                            Pirates pirates = (Pirates) cardInUse;
                            int pow = pirates.getShotsPowerArray().get(pirates.getCounter());

                            String power;
                            if(pow == 1) {
                                power = new String("light");
                            }
                            else {
                                power = new String("heavy");
                            }

                            text = "It's your turn. You have to roll the dice to decide which number of column the cannon fire will hit. The cannon fire is coming from nord and is a " + power + " cannon fire...";
                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            Button button1 = new Button("Roll dice");
                            button1.getStyleClass().add("my-button1");
                            button1.setAlignment(Pos.CENTER);
                            button1.setMaxWidth(Double.MAX_VALUE);
                            button1.setOnAction(event -> {
                                gui.notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                                errorLabel.setVisible(false);
                            });

                            VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                            VBox container = new VBox(label1, button1, errorLabel);
                            container.setAlignment(Pos.CENTER);
                            topPane2.getChildren().addAll(container);

                            this.state = "roll dice";
                            this.updateShipBoard(shipBoard);
                        }
                        else {
                            Label label1 = new Label();
                            String text;

                            Pirates pirates = (Pirates) cardInUse;
                            int pow = pirates.getShotsPowerArray().get(pirates.getCounter());

                            String power;
                            if(pow == 1) {
                                power = new String("light");
                            }
                            else {
                                power = new String("heavy");
                            }

                            text = "Wait for the leader to roll the dice to decide which number of column the meteor will hit. The cannon fire is coming from nord and is a " + power + " cannon fire...";
                            label1.setText(text);
                            label1.getStyleClass().add("my-button4");
                            label1.setWrapText(true);
                            label1.setAlignment(Pos.CENTER);
                            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                            topPane2.getChildren().addAll(label1);
                        }
                    }
                }
                else if(card.getCardType() == CardName.COMBAT_ZONE) {
                    if(((CombatZone) cardInUse).getCounter() == 0 || proceed) {
                        isViewingShips = false;

                        if(buttonBack != null) {
                            buttonBack.setVisible(false);
                        }

                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        topPane2.getChildren().clear();
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageView, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        Label label1 = new Label();
                        String text;

                        CombatZone combatZone = (CombatZone) cardInUse;
                        ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZone.getFaseThree()[2];
                        Meteor meteor = meteors.get(combatZone.getCounter());

                        String power;
                        if(meteor.getPower() == 1) {
                            power = new String("light");
                        }
                        else {
                            power = new String("heavy");
                        }

                        if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                            text = "It's your turn. You have to roll the dice to decide which number of column the cannon fire will hit. The cannon fire is coming from " +meteor.getDirection() + " and is a " + power + " cannon fire...";
                        }
                        else {
                            text = "It's your turn. You have to roll the dice to decide which number of row the cannon fire will hit. The cannon fire is coming from " +meteor.getDirection() + " and is a " + power + " cannon fire...";
                        }

                        label1.setText(text);
                        label1.getStyleClass().add("my-button4");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Roll dice");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                            errorLabel.setVisible(false);
                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);
                        topPane2.getChildren().addAll(container);

                        this.state = "roll dice";
                        this.updateShipBoard(shipBoard);
                    }
                    else {
                        isViewingShips = false;

                        if(buttonBack != null) {
                            buttonBack.setVisible(false);
                        }

                        if(gridPane.getChildren().contains(shipsPane)) {
                            gridPane.getChildren().remove(shipsPane);
                        }

                        if (!gridPane1.getChildren().contains(topPane1)) {
                            gridPane1.getChildren().add(topPane1);
                        }

                        if (!gridPane1.getChildren().contains(topPane2)) {
                            gridPane1.getChildren().add(topPane2);
                        }

                        gridPane1.setVisible(true);

                        topPane1.getChildren().clear();
                        topPane1.setMaxWidth(Double.MAX_VALUE);
                        topPane1.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane1.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane1.setAlignment(Pos.TOP_CENTER);

                        if(shipHit) {
                            topPane2.getChildren().clear();
                            shipHit = false;
                        }
                        topPane2.setMaxWidth(Double.MAX_VALUE);
                        topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                        topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                        topPane2.setAlignment(Pos.CENTER);

                        topPane1.setVisible(true);
                        topPane2.setVisible(true);

                        Label label = new Label("This is the card picked.");
                        label.getStyleClass().add("my-button3");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        StackPane.setAlignment(label, Pos.CENTER);
                        VBox.setMargin(label, new Insets(0, 0, 10, 0));

                        double val = Math.floor(gridPane.getPrefWidth() * 0.18);
                        StackPane cardsContainer = new StackPane();
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        Image cardImage;
                        ImageView imageView;
                        cardImage = new Image(getClass().getResourceAsStream("/images/cards/" + cardInUse.getUrl()));
                        double ratio = cardImage.getWidth() / cardImage.getHeight();
                        imageView = new ImageView(cardImage);
                        imageView.setFitHeight(val);
                        imageView.setFitWidth(val*ratio);
                        imageView.setPreserveRatio(true);
                        imageView.setSmooth(true);

                        Rectangle clip = new Rectangle(val*ratio, val);
                        clip.setArcWidth(20.0);
                        clip.setArcHeight(20.0);
                        imageView.setClip(clip);

                        StackPane borderContainer = new StackPane();
                        borderContainer.getStyleClass().add("cell-border1");
                        borderContainer.setMaxHeight(val);
                        borderContainer.setMaxWidth(val*ratio);

                        VBox cellContainer = new VBox(borderContainer);
                        cellContainer.setMaxHeight(val);
                        cellContainer.setAlignment(Pos.BOTTOM_CENTER);
                        GridPane.setHgrow(cellContainer, Priority.ALWAYS);
                        VBox.setVgrow(borderContainer, Priority.ALWAYS);

                        cardsContainer.getChildren().addAll(imageView, cellContainer);
                        cardsContainer.setAlignment(Pos.BOTTOM_CENTER);

                        VBox contentContainer = new VBox(label, cardsContainer);
                        contentContainer.setAlignment(Pos.TOP_CENTER);
                        topPane1.getChildren().add(contentContainer);
                        topPane1.setVisible(true);

                        Label label1 = new Label();
                        String text;

                        CombatZone combatZone = (CombatZone) cardInUse;
                        ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZone.getFaseThree()[2];
                        Meteor meteor = meteors.get(combatZone.getCounter());

                        String power;
                        if(meteor.getPower() == 1) {
                            power = new String("light");
                        }
                        else {
                            power = new String("heavy");
                        }

                        if(meteor.getDirection().equals("nord") || meteor.getDirection().equals("sud")) {
                            text = "It's your turn. You have to roll the dice to decide which number of column the cannon fire will hit. The cannon fire is coming from " +meteor.getDirection() + " and is a " + power + " cannon fire...";
                        }
                        else {
                            text = "It's your turn. You have to roll the dice to decide which number of row the cannon fire will hit. The cannon fire is coming from " +meteor.getDirection() + " and is a " + power + " cannon fire...";
                        }

                        label1.setText(text);
                        label1.getStyleClass().add("my-button4");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Roll dice");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateRollDice(cardInUse));
                            errorLabel.setVisible(false);
                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);
                        topPane2.getChildren().addAll(container);

                        this.state = "roll dice";
                        this.updateShipBoard(shipBoard);
                    }
                }
            }
        });
    }


    public void showMeteorRoll(String message) {
        this.watchShips = false;
        Platform.runLater(() -> {
            this.shipHit = false;

            gridPane1.setVisible(true);

            if (!gridPane1.getChildren().contains(topPane2)) {
                gridPane1.getChildren().add(topPane2);
            }

            topPane2.getChildren().clear();
            topPane2.setMaxWidth(Double.MAX_VALUE);
            topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
            topPane2.setPrefWidth(gridPane.getWidth()/2);
            topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
            topPane2.setAlignment(Pos.CENTER);

            Label label1 = new Label(message);
            label1.getStyleClass().add("my-button3");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label1, Pos.TOP_CENTER);
            topPane2.getChildren().addAll(label1);
        });
    }


    public void showMeteorHit(Card card, boolean isHit, int sum) {
        this.cardInUse = card;
        this.watchShips = false;
        Platform.runLater(() -> {
            if(card.getCardType() == CardName.METEOR_SWARM) {
                if(isHit == false) {
                    this.shipHit = false;
                    this.notClearTopPane2 = false;
                    gridPane1.setVisible(true);

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefWidth(gridPane.getWidth()/2);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    String text = "The meteor is coming from " + sum + " and it will not hit your shipboard. Wait the other players...";
                    Label label1 = new Label(text);
                    label1.getStyleClass().add("my-button3");
                    label1.setWrapText(true);
                    label1.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(label1, Pos.TOP_CENTER);

                    topPane2.getChildren().addAll(label1);
                    gui.notifyObserver(obs -> obs.onUpdateMeteorSwarmChoice(cardInUse, sum));
                }
                else {
                    this.notClearTopPane2 = false;
                    this.shipHit = true;
                    gridPane1.setVisible(true);

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefWidth(gridPane.getWidth()/2);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    Meteor meteor = ((MeteorSwarm) cardInUse).getMeteor().get(((MeteorSwarm) cardInUse).getCounter()-1);
                    if(meteor.getPower() == 1) {
                        String text;
                        String direction = meteor.getDirection();
                        if (direction.equals("nord") || direction.equals("sud")) {
                            text = "The meteor will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...";
                        }
                        else {
                            text = "The meteor will hit your shipboard at row number " + sum + " from " + meteor.getDirection() + ". You can activate the shield in this direction (if you have it) to prevent the catastrophe...";
                        }

                        Label label1 = new Label(text);
                        label1.getStyleClass().add("my-button4");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Proceed");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        this.finSum = sum;
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateMeteorSwarmChoice(cardInUse, finSum));
                            errorLabel.setVisible(false);
                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);
                        topPane2.getChildren().addAll(container);

                        this.state = "meteor swarm small";
                        this.updateShipBoard(shipBoard);
                    }
                    else if(meteor.getPower() == 2) {
                        String text;
                        String direction = meteor.getDirection();
                        if (direction.equals("nord")) {
                            text = "The meteor will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". If you have a double cannon in column " + sum + " pointing in the direction of the meteor you can activate it to prevent the catastrophe...";
                        }
                        else if(direction.equals("sud")) {
                            text = "The meteor will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". If you have a double cannon in column " + (sum-1) + " or " + sum + " or " + (sum+1) + " pointing in the direction of the meteor you can activate it to prevent the catastrophe...";
                        }
                        else {
                            text = "The meteor will hit your shipboard at row number " + sum + " from " + meteor.getDirection() + ". If you have a double cannon in row " + (sum-1) + " or " + sum + " or " + (sum+1) + " pointing in the direction of the meteor you can activate it to prevent the catastrophe...";
                        }

                        Label label1 = new Label(text);
                        label1.getStyleClass().add("my-button4");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Proceed");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        this.finSum = sum;
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateMeteorSwarmChoice(cardInUse, finSum));
                            errorLabel.setVisible(false);
                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);
                        topPane2.getChildren().addAll(container);

                        this.state = "meteor swarm large";
                        this.updateShipBoard(shipBoard);
                    }
                }
            }
            else if(card.getCardType() == CardName.PIRATES) {
                if(isHit == false) {
                    this.shipHit = false;
                    this.notClearTopPane2 = false;
                    gridPane1.setVisible(true);

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefWidth(gridPane.getWidth()/2);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    String text = "The cannon fire is coming from " + sum + " and it will not hit your shipboard...";
                    Label label1 = new Label(text);
                    label1.getStyleClass().add("my-button3");
                    label1.setWrapText(true);
                    label1.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(label1, Pos.TOP_CENTER);

                    topPane2.getChildren().addAll(label1);
                    gui.notifyObserver(obs -> obs.onUpdateDefeatedPiratesChoice(cardInUse, sum));
                }
                else {
                    this.notClearTopPane2 = false;
                    this.shipHit = true;
                    gridPane1.setVisible(true);

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefWidth(gridPane.getWidth()/2);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    Pirates piratesCard = (Pirates) card;
                    int pow = piratesCard.getShotsPowerArray().get(piratesCard.getCounter()-1);

                    if(pow == 1) {
                        String text = "The cannon fire will hit your shipboard at column number " + sum + " from nord. You can activate the shield in this direction (if you have it) to prevent the catastrophe...";
                        Label label1 = new Label(text);
                        label1.getStyleClass().add("my-button4");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Proceed");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        this.finSum = sum;
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateDefeatedPiratesChoice(cardInUse, sum));
                            errorLabel.setVisible(false);
                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);
                        topPane2.getChildren().addAll(container);

                        this.state = "cannon fire light";
                        this.updateShipBoard(shipBoard);
                    }
                    else {
                        this.state = "cannon fire heavy";
                        gui.notifyObserver(obs -> obs.onUpdateDefeatedPiratesChoice(cardInUse, sum));
                    }
                }
            }
            else if(card.getCardType() == CardName.COMBAT_ZONE) {
                if(isHit == false) {
                    this.shipHit = false;
                    this.notClearTopPane2 = false;
                    gridPane1.setVisible(true);

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefWidth(gridPane.getWidth()/2);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    String text = "The cannon fire is coming from " + sum + " and it will not hit your shipboard...";
                    Label label1 = new Label(text);
                    label1.getStyleClass().add("my-button3");
                    label1.setWrapText(true);
                    label1.setAlignment(Pos.CENTER);
                    StackPane.setAlignment(label1, Pos.TOP_CENTER);

                    topPane2.getChildren().addAll(label1);
                    gui.notifyObserver(obs -> obs.onUpdateCombatZoneChoice(cardInUse, sum));
                }
                else {
                    this.notClearTopPane2 = false;
                    this.shipHit = true;
                    gridPane1.setVisible(true);

                    if (!gridPane1.getChildren().contains(topPane2)) {
                        gridPane1.getChildren().add(topPane2);
                    }

                    topPane2.getChildren().clear();
                    topPane2.setMaxWidth(Double.MAX_VALUE);
                    topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                    topPane2.setPrefWidth(gridPane.getWidth()/2);
                    topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                    topPane2.setAlignment(Pos.CENTER);

                    CombatZone combatZoneCard = (CombatZone) card;
                    ArrayList<Meteor> meteors = (ArrayList<Meteor>) combatZoneCard.getFaseThree()[2];
                    Meteor meteor = meteors.get(combatZoneCard.getCounter() - 1);

                    if(meteor.getPower() == 1) {
                        String direction = meteor.getDirection();

                        String text;
                        if (direction.equals("nord") || direction.equals("sud")) {
                            text = "The cannon fire will hit your shipboard at column number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...";
                        }
                        else {
                            text = "The cannon fire will hit your shipboard at row number " + sum + " from " + meteor.getDirection() +". You can activate the shield in this direction (if you have it) to prevent the catastrophe...";
                        }

                        Label label1 = new Label(text);
                        label1.getStyleClass().add("my-button4");
                        label1.setWrapText(true);
                        label1.setAlignment(Pos.CENTER);
                        VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                        Button button1 = new Button("Proceed");
                        button1.getStyleClass().add("my-button1");
                        button1.setAlignment(Pos.CENTER);
                        button1.setMaxWidth(Double.MAX_VALUE);
                        this.finSum = sum;
                        button1.setOnAction(event -> {
                            gui.notifyObserver(obs -> obs.onUpdateCombatZoneChoice(cardInUse, sum));
                            errorLabel.setVisible(false);
                        });

                        VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                        VBox container = new VBox(label1, button1, errorLabel);
                        container.setAlignment(Pos.CENTER);
                        topPane2.getChildren().addAll(container);

                        this.state = "cannon fire light";
                        this.updateShipBoard(shipBoard);
                    }
                    else {
                        gui.notifyObserver(obs -> obs.onUpdateCombatZoneChoice(cardInUse, sum));
                    }
                }
            }
        });
    }


    public void showShipCorrect(String message) {
        Platform.runLater(() -> {
            bottomPane2.getChildren().clear();
            if(buttonBack!=null) {
                buttonBack.setVisible(false);
            }

            Label label1 = new Label("Your ship board is correct, but there are other players that need to fix their ship. You have to wait the other players...");
            label1.getStyleClass().add("my-button4");
            label1.setWrapText(true);
            label1.setAlignment(Pos.CENTER);
            VBox.setMargin(label1, new Insets(0, 0, 10, 0));

            bottomPane2.getChildren().addAll(label1);
        });
    }


    public void showWaitRepaired(String message) {
        Platform.runLater(() -> {
            bottomPane2.getChildren().clear();
            if(bottomPane2.getChildren().contains(tempGrid)) {
                bottomPane2.getChildren().remove(tempGrid);
            }

            Label label = new Label(message);
            StackPane.setAlignment(label,Pos.CENTER);
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            bottomPane2.getChildren().add(label);
            bottomPane2.setVisible(true);
            bottomPane2.toFront();
        });
    }


    public void showWait(Card card) {
        Platform.runLater(() -> {
            if (!isViewingShips) {
                if (gridPane.getChildren().contains(shipsPane)) {
                    gridPane.getChildren().remove(shipsPane);
                }

                if (!gridPane1.getChildren().contains(topPane2)) {
                    gridPane1.getChildren().add(topPane2);
                }

                gridPane1.setVisible(true);

                topPane2.getChildren().clear();
                topPane2.setMaxWidth(Double.MAX_VALUE);
                topPane2.setMaxHeight(gridPane.getHeight() * 0.432);
                topPane2.setPrefHeight(gridPane.getHeight() * 0.432);
                topPane2.setAlignment(Pos.CENTER);
                topPane2.setVisible(true);

                Label label1 = new Label("You are not in turn. Wait the other players...");
                label1.getStyleClass().add("my-button3");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 10, 0));

                Button button1 = new Button("Show ships");
                button1.getStyleClass().add("my-button1");
                button1.setAlignment(Pos.CENTER);
                button1.setMaxWidth(Double.MAX_VALUE);
                button1.setOnAction(event -> {
                    gui.notifyObserver(obs -> obs.onUpdateShowShips());
                    errorLabel.setVisible(false);
                    this.isViewingShips = true;
                    this.state = "show ships";

                });

                VBox.setMargin(button1, new Insets(0, 0, 20, 0));
                VBox container = new VBox(label1, button1, errorLabel);
                container.setAlignment(Pos.CENTER);

                topPane2.getChildren().addAll(container);
            }
        });
    }


    public void setErrorLabel(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
    }

    public void changeState() {
        isViewingShips = false;
        this.watchShips = true;
    }
}