package ui.fxSinTabbedPane;

import game.GameManager;
import model.Torneo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;

public class SeleccionTorneoControl {

    @FXML private ListView<Torneo> listTorneos;
    @FXML private TextArea txtTournamentInfo;
    @FXML private Button btnStartTournament;
    @FXML private TreeView<String> treeBracket;
    @FXML private Button btnVolverMenu;

    private GameManager gameManager;
    private Principal principal;

    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
        loadTournaments();
    }

    public void setMainApp(Principal p) {
        this.principal = p;
    }

    @FXML
    public void initialize() {
        btnStartTournament.setDisable(true);

        listTorneos.getSelectionModel().selectedItemProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                showTorneoInfo(newT);
                btnStartTournament.setDisable(false);
            } else {
                txtTournamentInfo.clear();
                treeBracket.setRoot(null);
                btnStartTournament.setDisable(true);
            }
        });
    }

    private void loadTournaments() {
        if (gameManager == null) return;

        Platform.runLater(() -> {
            listTorneos.setItems(
                    FXCollections.observableArrayList(
                            gameManager.getTorneosDisponibles()
                    )
            );
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

        TreeItem<String> root = new TreeItem<>("Torneo: " + t.getNombre());
        TreeItem<String> r1 = new TreeItem<>("Ronda 1");
        TreeItem<String> r2 = new TreeItem<>("Ronda 2");
        TreeItem<String> r3 = new TreeItem<>("Final");

        root.getChildren().addAll(r1, r2, r3);
        root.setExpanded(true);

        treeBracket.setRoot(root);
    }

    @FXML
    private void onStartTournament() {
        Torneo sel = listTorneos.getSelectionModel().getSelectedItem();
        if (sel == null || gameManager == null) return;

        boolean ok = gameManager.iniciarTorneo(sel.getNombre());
        Alert a;

        if (ok) {
            a = new Alert(Alert.AlertType.INFORMATION,
                    "¡Torneo iniciado: " + sel.getNombre() + "!",
                    ButtonType.OK);
        } else {
            a = new Alert(Alert.AlertType.WARNING,
                    "No cumples los requisitos de nivel para este torneo.",
                    ButtonType.OK);
        }

        a.setHeaderText(null);
        a.showAndWait();
    }

    @FXML
    private void onVolverMenu() {
        if (principal != null) {
            principal.loadMainMenu();
        }
    }
}
