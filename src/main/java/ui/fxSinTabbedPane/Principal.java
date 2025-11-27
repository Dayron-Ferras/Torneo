package ui.fxSinTabbedPane;

import game.GameManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Jugador;

public class Principal extends Application {

    private BorderPane rootPane;
    private GameManager gameManager;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uiSinTabbedPane/rootLayout.fxml"));
        rootPane = loader.load();

        Scene scene = new Scene(rootPane, 1200, 700);
        stage.setScene(scene);
        stage.setTitle("Penalty Tournament Evolution");
        stage.show();

        this.gameManager = new GameManager(new Jugador("FutbolistaPro"));

        loadMainMenu();
    }

    public void loadMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uiSinTabbedPane/mainMenu.fxml"));
            Parent view = loader.load();

            MainMenuControl ctrl = loader.getController();
            ctrl.setMainApp(this);
            ctrl.setGameManager(gameManager);

            rootPane.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScreen(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent view = loader.load();

            Object controller = loader.getController();

            if (controller instanceof MainMenuControl c) {
                c.setMainApp(this);
                c.setGameManager(gameManager);
            }
            if (controller instanceof HabilidadesControl c) {
                c.setMainApp(this);
                c.setGameManager(gameManager);
            }
            if (controller instanceof SeleccionTorneoControl c) {
                c.setMainApp(this);
                c.setGameManager(gameManager);
            }
            if (controller instanceof PartidoControl c) {
                c.setMainApp(this);
                c.setGameManager(gameManager);
            }

            rootPane.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BorderPane getRootPane() {
        return rootPane;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
