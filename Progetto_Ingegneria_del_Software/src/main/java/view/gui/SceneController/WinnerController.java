package view.gui.SceneController;

import controller.ClientController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.player.Player;
import view.gui.Gui;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents the winner Controller, which controls the winner scenes.
 */
public class WinnerController {
    @FXML private StackPane rootPane;
    @FXML private Pane blackBackground;
    @FXML private ImageView backgroundImage;
    @FXML private AnchorPane uiPane;
    @FXML private Label winnerLabel;
    @FXML private StackPane topPane;
    @FXML private StackPane bottomPane;
    @FXML private GridPane gridPane;

    private final Gui gui;

    public WinnerController(Gui gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> rootPane.requestFocus());

        try {
            String bgPath = "/images/final_bg.jpg";
            if (getClass().getResource(bgPath) == null) {
                throw new IllegalStateException("Image not found: " + bgPath);
            }
            Image bgImage = new Image(getClass().getResourceAsStream(bgPath));
            if (bgImage.isError()) {
                throw new IllegalStateException("Error");
            }
            backgroundImage.setImage(bgImage);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setManaged(false);

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::updateLayout);
        });
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::updateLayout);
        });

        Platform.runLater(() -> {
            updateLayout();
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

        double windowWidth1 = backgroundImage.getFitWidth();
        double windowHeight1 = backgroundImage.getFitHeight();

        gridPane.setPrefWidth(windowWidth1 * 0.98);
        gridPane.setMaxWidth(windowWidth1 * 0.98);
        gridPane.setPrefHeight(windowHeight1);
        gridPane.setMaxHeight(windowHeight1);

        rootPane.requestLayout();
    }


    public void showWinner(ArrayList<Player> players, boolean winner) {
        Platform.runLater(() -> {
            if (!gridPane.getChildren().contains(topPane)) {
                gridPane.getChildren().add(topPane);
            }

            if (!gridPane.getChildren().contains(bottomPane)) {
                gridPane.getChildren().add(bottomPane);
            }

            topPane.getChildren().clear();
            topPane.setMaxWidth(Double.MAX_VALUE);
            topPane.setMaxHeight(gridPane.getHeight() * 0.5);
            topPane.setPrefHeight(gridPane.getHeight() * 0.5);
            topPane.setAlignment(Pos.TOP_CENTER);

            bottomPane.getChildren().clear();
            bottomPane.setMaxWidth(Double.MAX_VALUE);
            bottomPane.setPrefWidth(gridPane.getWidth() * 0.9);
            bottomPane.setMaxHeight(gridPane.getHeight() * 0.45);
            bottomPane.setPrefHeight(gridPane.getHeight() * 0.45);
            bottomPane.setAlignment(Pos.CENTER);
            GridPane.setHalignment(bottomPane, HPos.CENTER);

            Label winnerLabel;
            if(winner) {
               winnerLabel = new Label("Winner");
            }
            else {
                winnerLabel = new Label("Not winner");
            }

            winnerLabel.getStyleClass().add("my-button10");
            winnerLabel.setWrapText(true);
            winnerLabel.setAlignment(Pos.CENTER);
            StackPane.setAlignment(winnerLabel, Pos.CENTER);
            VBox.setMargin(winnerLabel, new Insets(0, 0, 10, 0));
            topPane.getChildren().add(winnerLabel);

            Label label = new Label("This are the players and their cosmic credits gained (or lost) during the game...");
            label.getStyleClass().add("my-button3");
            label.setWrapText(true);
            label.setAlignment(Pos.CENTER);
            StackPane.setAlignment(label, Pos.CENTER);
            VBox.setMargin(label, new Insets(0, 0, 20, 0));

            if(players.size() == 2) {
                String text1 = "Player:  " +  players.get(0).getNickname() + "\t\tCosmic credits: " + players.get(0).getCosmicCredit();
                Label label1 = new Label(text1);
                label1.getStyleClass().add("my-button4");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label1, Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 5, 0));

                String text2 = "Player:  " +  players.get(1).getNickname() + "\t\tCosmic credits: " + players.get(1).getCosmicCredit();
                Label label2 = new Label(text2);
                label2.getStyleClass().add("my-button4");
                label2.setWrapText(true);
                label2.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label2, Pos.CENTER);
                VBox.setMargin(label2, new Insets(0, 0, 5, 0));

                VBox container1 = new VBox(label, label1, label2);
                StackPane.setAlignment(container1, Pos.CENTER);
                container1.setAlignment(Pos.CENTER);
                bottomPane.getChildren().add(container1);
            }
            else if(players.size() == 3) {
                String text1 = "Player:  " +  players.get(0).getNickname() + "\t\tCosmic credits: " + players.get(0).getCosmicCredit();
                Label label1 = new Label(text1);
                label1.getStyleClass().add("my-button4");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label1, Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 5, 0));

                String text2 = "Player:  " +  players.get(1).getNickname() + "\t\tCosmic credits: " + players.get(1).getCosmicCredit();
                Label label2 = new Label(text2);
                label2.getStyleClass().add("my-button4");
                label2.setWrapText(true);
                label2.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label2, Pos.CENTER);
                VBox.setMargin(label2, new Insets(0, 0, 5, 0));

                String text3 = "Player:  " +  players.get(2).getNickname() + "\t\tCosmic credits: " + players.get(2).getCosmicCredit();
                Label label3 = new Label(text3);
                label3.getStyleClass().add("my-button4");
                label3.setWrapText(true);
                label3.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label3, Pos.CENTER);
                VBox.setMargin(label3, new Insets(0, 0, 5, 0));

                VBox container1 = new VBox(label, label1, label2, label3);
                StackPane.setAlignment(container1, Pos.CENTER);
                container1.setAlignment(Pos.CENTER);
                bottomPane.getChildren().add(container1);
            }
            else if (players.size() == 4) {
                String text1 = "Player:  " + players.get(0).getNickname() + "\t\tCosmic credits: " + players.get(0).getCosmicCredit();
                Label label1 = new Label(text1);
                label1.getStyleClass().add("my-button4");
                label1.setWrapText(true);
                label1.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label1, Pos.CENTER);
                VBox.setMargin(label1, new Insets(0, 0, 5, 0));

                String text2 = "Player:  " + players.get(1).getNickname() + "\t\tCosmic credits: " + players.get(1).getCosmicCredit();
                Label label2 = new Label(text2);
                label2.getStyleClass().add("my-button4");
                label2.setWrapText(true);
                label2.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label2, Pos.CENTER);
                VBox.setMargin(label2, new Insets(0, 0, 5, 0));

                String text3 = "Player:  " + players.get(2).getNickname() + "\t\tCosmic credits: " + players.get(2).getCosmicCredit();
                Label label3 = new Label(text3);
                label3.getStyleClass().add("my-button4");
                label3.setWrapText(true);
                label3.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label3, Pos.CENTER);
                VBox.setMargin(label3, new Insets(0, 0, 5, 0));

                String text4 = "Player:  " + players.get(3).getNickname() + "\t\tCosmic credits: " + players.get(3).getCosmicCredit();
                Label label4 = new Label(text3);
                label4.getStyleClass().add("my-button4");
                label4.setWrapText(true);
                label4.setAlignment(Pos.CENTER);
                StackPane.setAlignment(label4, Pos.CENTER);
                VBox.setMargin(label4, new Insets(0, 0, 5, 0));

                VBox container1 = new VBox(label, label1, label2, label3, label4);
                StackPane.setAlignment(container1, Pos.CENTER);
                container1.setAlignment(Pos.CENTER);

                bottomPane.getChildren().add(container1);
            }
            rootPane.requestLayout();
        });
    }
}