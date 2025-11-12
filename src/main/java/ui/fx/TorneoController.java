package ui.fx;

import game.GameManager;
import model.Torneo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.TreeItem;
import javafx.collections.FXCollections;

public class TorneoController {

    @FXML private ListView<Torneo> listTorneos;
    @FXML private TextArea txtTournamentInfo;
    @FXML private Button btnStartTournament;
    @FXML private TreeView<String> treeBracket;

    private GameManager gameManager;

    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
        loadTournaments();
    }

    @FXML
    public void initialize() {
        btnStartTournament.setDisable(true);
        listTorneos.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                showTorneoInfo(newV);
                btnStartTournament.setDisable(false);
            } else {
                txtTournamentInfo.clear();
                btnStartTournament.setDisable(true);
            }
        });
    }

    private void loadTournaments() {
        if (gameManager == null) return;
        Platform.runLater(() -> {
            listTorneos.setItems(FXCollections.observableArrayList(gameManager.getTorneosDisponibles()));
        });
    }

    private void showTorneoInfo(Torneo t) {
        StringBuilder sb = new StringBuilder();
        sb.append("🏆 ").append(t.getNombre()).append("\n\n");
        sb.append("Nivel Requerido: ").append(t.getNivelRequerido()).append("\n");
        sb.append("Recompensa: $").append(t.getRecompensa()).append("\n");
        sb.append("Estado: ").append(t.isCompletado() ? "✅ Completado" : "🟡 Disponible").append("\n\n");
        sb.append("Clubs participantes:\n");
        t.getClubsParticipantes().forEach(c -> sb.append("• ").append(c.getNombre()).append("\n"));
        txtTournamentInfo.setText(sb.toString());

        // simple bracket demo
        TreeItem<String> root = new TreeItem<>(t.getNombre());
        root.getChildren().add(new TreeItem<>("Ronda 1"));
        root.getChildren().add(new TreeItem<>("Ronda 2"));
        treeBracket.setRoot(root);
        root.setExpanded(true);
    }

    @FXML
    private void onStartTournament() {
        Torneo sel = listTorneos.getSelectionModel().getSelectedItem();
        if (sel == null || gameManager == null) return;

        boolean ok = gameManager.iniciarTorneo(sel.getNombre());
        Alert a;
        if (ok) {
            a = new Alert(Alert.AlertType.INFORMATION, "¡Torneo iniciado: " + sel.getNombre(), ButtonType.OK);
            a.setHeaderText(null);
            a.showAndWait();
            // Optionally navigate to Partido pane in your app
        } else {
            a = new Alert(Alert.AlertType.WARNING, "No cumples requisitos para participar (nivel insuficiente).", ButtonType.OK);
            a.setHeaderText(null);
            a.showAndWait();
        }
    }
}