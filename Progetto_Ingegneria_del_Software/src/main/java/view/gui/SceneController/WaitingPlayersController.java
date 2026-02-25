package view.gui.SceneController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.gui.Gui;

/**
 * This class represents the waiting players Controller, which controls the scenes where a player that has created a game has to wait for other players.
 */
public class WaitingPlayersController {
    @FXML private StackPane rootPane;
    @FXML private Pane blackBackground;
    @FXML private ImageView backgroundImage;
    @FXML private AnchorPane uiPane;
    @FXML private ImageView logoImage;
    @FXML private VBox inputBox;
    @FXML private Label waitingPlayerLabel;
    @FXML private ProgressIndicator spinner;

    private final Gui gui;

    public WaitingPlayersController(Gui gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
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

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setManaged(false);

        logoImage.setPreserveRatio(true);
        logoImage.setManaged(false);

        if (spinner != null) {
            spinner.setVisible(true);
            spinner.setProgress(-1);
            spinner.setCache(true);
        }

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

        double logoWidth = scaledWidth * 0.3;
        logoImage.setFitWidth(logoWidth);
        double logoX = (windowWidth - logoWidth) / 2;
        double logoY = (windowHeight - scaledHeight) / 2 + scaledHeight * 0.13;
        logoImage.setLayoutX(logoX);
        logoImage.setLayoutY(logoY);

        double inputWidth = scaledWidth * 0.4;
        inputBox.setPrefWidth(inputWidth);
        double inputX = (windowWidth - inputWidth) / 2;
        double inputY = (windowHeight - scaledHeight) / 2 + scaledHeight * 0.5;
        inputBox.setLayoutX(inputX);
        inputBox.setLayoutY(inputY);

        rootPane.requestLayout();
    }


    public void stopSpinner() {
        if (spinner != null) {
            spinner.setStyle("-fx-progress-color: transparent; -fx-opacity: 0;");
            spinner.setProgress(0);
            spinner.setVisible(false);
            spinner.setDisable(true);
            spinner.setManaged(false);
            spinner = null;
        }
        if (rootPane != null) {
            rootPane.setVisible(false);
            rootPane.getChildren().clear();
            rootPane = null;
        }
        if (backgroundImage != null) {
            backgroundImage.setImage(null);
            backgroundImage = null;
        }
        if (logoImage != null) {
            logoImage.setImage(null);
            logoImage = null;
        }
        if (uiPane != null) {
            uiPane.getChildren().clear();
            uiPane = null;
        }
        if (inputBox != null) {
            inputBox.getChildren().clear();
            inputBox = null;
        }
        if (waitingPlayerLabel != null) {
            waitingPlayerLabel = null;
        }
        if (blackBackground != null) {
            blackBackground = null;
        }
    }
}