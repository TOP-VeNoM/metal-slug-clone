import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) {
        try {
            UITheme.init();

            GameEngine.GAME_SPEED = 1.8;
            MainMenu mainMenu = new MainMenu(stage);
            Scene menuScene = new Scene(mainMenu);

            stage.setScene(menuScene);
            stage.setTitle("Metal Slug Clone");
            stage.setResizable(true);
            stage.setWidth(1000);
            stage.setHeight(625);
            stage.setMinWidth(800);
            stage.setMinHeight(500);

            stage.show();

            menuScene.getRoot().requestFocus();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
