package ui.fxSinTabbedPane;

import game.GameManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainMenuControl {

    @FXML private Button btnTorneo;
    @FXML private Button btnHabilidades;
    @FXML private Button btnPartido;
    @FXML private Button btnSalir;
    @FXML private Label lblJugadorInfo;

    private GameManager gameManager;

    public void setGameManager(GameManager gm) {
        this.gameManager = gm;

        lblJugadorInfo.setText(
                "Jugador: " + gm.getJugador().getNombre() +
                        "  |  Nivel: " + gm.getJugador().getNivel() +
                        "  |  Dinero: $" + gm.getJugador().getDinero()
        );
    }

    @FXML
    private void initialize() {
        btnSalir.setOnAction(e -> System.exit(0));
        // Los otros botones se conectarán cuando integres los paneles de torneo, habilidades, etc.
    }
}
