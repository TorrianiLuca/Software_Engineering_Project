package view.gui.SceneController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.gui.Gui;

/**
 * This class represents the nickname Controller, which controls the nickname scenes.
 */
public class NicknameController {
    @FXML private StackPane rootPane;
    @FXML private Pane blackBackground;
    @FXML private ImageView backgroundImage;
    @FXML private AnchorPane uiPane;
    @FXML private ImageView logoImage;
    @FXML private VBox inputBox;
    @FXML private TextField nicknameField;
    @FXML private Button enterNicknameButton;
    @FXML private Label errorLabel;

    private final Gui gui;

    public NicknameController(Gui gui) {
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        Platform.runLater(() -> rootPane.requestFocus());

        try {
            String bgPath = "/images/lobby_bg.jpg";
            if (getClass().getResource(bgPath) == null) {
                throw new IllegalStateException("Image not found: " + bgPath);
            }
            Image bgImage = new Image(getClass().getResourceAsStream(bgPath));
            if (bgImage.isError()) {
                throw new IllegalStateException("Error");
            }
            backgroundImage.setImage(bgImage);

            String logoPath = "/images/galaxy_logo.png";
            if (getClass().getResource(logoPath) == null) {
                throw new IllegalStateException("Image not found: " + logoPath);
            }
            Image logo = new Image(getClass().getResourceAsStream(logoPath));
            if (logo.isError()) {
                throw new IllegalStateException("Error");
            }
            logoImage.setImage(logo);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        backgroundImage.setManaged(false);

        logoImage.setPreserveRatio(true);
        logoImage.setManaged(false);

        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::updateLayout);
        });

        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::updateLayout);
        });

        Platform.runLater(() -> {
            updateLayout();
        });

        enterNicknameButton.setOnAction(e -> {
            String nickname = nicknameField.getText().trim();
            gui.setNickname(nickname);
            if(nickname.equals("Controller") || nickname.equals("Model") || nickname.equals("Client") || nickname.equals("Server") || nickname.equals("")) {
                errorLabel.setVisible(true);
                errorLabel.setText("Nickname not permitted!");
            }  else {
                gui.notifyObserver(viewObserver -> viewObserver.onUpdateNickname(nickname));
            }
        });
    }


    private void updateLayout() {
        if (backgroundImage.getImage() == null) {
            return;
        }

        double windowWidth = rootPane.getWidth();
        double windowHeight = rootPane.getHeight();
        if (windowWidth <= 0 || windowHeight <= 0) {
            return;
        }

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


    public void setErrorLabel(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);
        updateLayout();
    }
}