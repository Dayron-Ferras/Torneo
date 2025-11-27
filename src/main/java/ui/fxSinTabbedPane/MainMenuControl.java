package ui.fxSinTabbedPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import game.GameManager;

public class MainMenuControl {

    private Principal principal;
    private GameManager gameManager;

    @FXML private Label lblPlayerName;
    @FXML private Label lblLevel;
    @FXML private Label lblDinero;

    @FXML private Button btnTorneo;
    @FXML private Button btnHabilidades;
    @FXML private Button btnPartido;
    @FXML private Button btnSalir;

    public void setMainApp(Principal principal) {
        this.principal = principal;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
        cargarDatosJugador();
    }

    private void cargarDatosJugador() {
        if (gameManager == null) return;

        var p = gameManager.getJugador();
        lblPlayerName.setText(p.getNombre());
        lblLevel.setText("Nivel: " + p.getNivel());
        lblDinero.setText("Dinero: " + p.getDinero());
    }

    @FXML
    private void initialize() {
        btnTorneo.setOnAction(e -> loadScreen("seleccionTorneo.fxml"));
        btnHabilidades.setOnAction(e -> loadScreen("habilidades.fxml"));
        btnPartido.setOnAction(e -> loadScreen("partidoRapido.fxml"));
        btnSalir.setOnAction(e -> System.exit(0));
    }

    private void loadScreen(String fxmlName) {
        principal.setScreen("/uiSinTabbedPane/" + fxmlName);
    }
}
