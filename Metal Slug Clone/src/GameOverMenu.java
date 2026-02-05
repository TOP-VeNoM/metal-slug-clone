import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameOverMenu extends VBox {
    private final Stage stage;

    public GameOverMenu(Stage stage) {
        this.stage = stage;

        setPrefSize(1200, 750);
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a1a, #0d0d0d);");

        Text title = new Text("GAME OVER");
        title.setFont(UITheme.boldFont(120));
        title.setFill(Color.web("#ffcc00"));
        title.setStroke(Color.web("#ff8800"));
        title.setStrokeWidth(8);
        title.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        title.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(10);
        shadow.setOffsetX(10);
        shadow.setColor(Color.web("#662200"));
        shadow.setRadius(15);
        shadow.setSpread(0.3);
        title.setEffect(shadow);

        Text subtitle = new Text("MISSION FAILED");
        subtitle.setFont(UITheme.boldFont(32));
        subtitle.setFill(Color.web("#ffaa00"));
        subtitle.setStroke(Color.web("#ff8800"));
        subtitle.setStrokeWidth(3);
        subtitle.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        subtitle.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);

        Button mainMenuButton = createStyledButton("MAIN MENU");
        mainMenuButton.setOnAction(e -> goToMainMenu());

        Button exitButton = createStyledButton("EXIT");
        exitButton.setOnAction(e -> System.exit(0));

        getChildren().addAll(title, subtitle, mainMenuButton, exitButton);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(300, 60);
        button.setFont(UITheme.boldFont(22));

        String defaultStyle = "-fx-background-color: linear-gradient(to bottom, #ffaa00, #ff8800); " +
                "-fx-text-fill: #1a1a1a; " +
                "-fx-border-color: #ff6600; " +
                "-fx-border-width: 4px; " +
                "-fx-border-radius: 0px; " +
                "-fx-background-radius: 0px; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10px;";

        String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #ffcc00, #ffaa00); " +
                "-fx-text-fill: #000000; " +
                "-fx-border-color: #ffff00; " +
                "-fx-border-width: 4px; " +
                "-fx-border-radius: 0px; " +
                "-fx-background-radius: 0px; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10px;";

        button.setStyle(defaultStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));

        return button;
    }

    private void goToMainMenu() {
        BGMManager.stop();
        MainMenu mainMenu = new MainMenu(stage);
        Scene menuScene = new Scene(mainMenu);
        stage.setScene(menuScene);
        menuScene.getRoot().requestFocus();
    }
}
