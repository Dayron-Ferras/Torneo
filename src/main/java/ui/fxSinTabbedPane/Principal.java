package ui.fxSinTabbedPane;

import game.GameManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Jugador;

public class Principal extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        GameManager game = new GameManager(new Jugador("Futbolista Pro"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uiSinTabbedPane/mainMenu.fxml"));
        Parent root = loader.load();

        MainMenuControl controller = loader.getController();
        controller.setGameManager(game);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/ui/panels.css").toExternalForm());

        stage.setTitle("Penalty Tournament Evolution ⚽");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
