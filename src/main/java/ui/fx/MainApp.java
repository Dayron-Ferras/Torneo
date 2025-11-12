package ui.fx;

import game.GameManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import model.Jugador;

public class MainApp extends Application {

    private GameManager gameManager;

    @Override
    public void start(Stage stage) throws Exception {
        this.gameManager = new GameManager(new Jugador("Futbolista Pro"));

        // Partido
        FXMLLoader fPartido = new FXMLLoader(getClass().getResource("/ui/partido.fxml"));
        Parent pPartido = fPartido.load();
        PartidoController cPartido = fPartido.getController();
        cPartido.setGameManager(gameManager);

        // Habilidades
        FXMLLoader fHabs = new FXMLLoader(getClass().getResource("/ui/habilidades.fxml"));
        Parent pHabs = fHabs.load();
        HabilidadesController cHabs = fHabs.getController();
        cHabs.setGameManager(gameManager);

        // Torneo
        FXMLLoader fTorneo = new FXMLLoader(getClass().getResource("/ui/torneo.fxml"));
        Parent pTorneo = fTorneo.load();
        TorneoController cTorneo = fTorneo.getController();
        cTorneo.setGameManager(gameManager);

        TabPane tabPane = new TabPane();
        Tab t1 = new Tab("Partido", pPartido);
        Tab t2 = new Tab("Habilidades", pHabs);
        Tab t3 = new Tab("Torneos", pTorneo);
        t1.setClosable(false); t2.setClosable(false); t3.setClosable(false);
        tabPane.getTabs().addAll(t1, t2, t3);

        Scene scene = new Scene(tabPane, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/ui/panels.css").toExternalForm());

        stage.setTitle("Desafío de Clubes - Penalty Tournament Evolution (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}