package ui.fxSinTabbedPane;

import game.GameManager;
import game.PenaltyShootout;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import model.Partido;

public class PartidoRapidoControl {

    @FXML private Label lblEstado;
    @FXML private Pane pitchPane;
    @FXML private Pane ballLayer;
    @FXML private Label lblGoalkeeper;
    @FXML private Label lblPlayerScore;
    @FXML private Label lblOpponentScore;
    @FXML private ProgressBar progressKicks;
    @FXML private Button btnLeft, btnCenter, btnRight, btnStart, btnVolver;
    @FXML private TextArea logArea;

    private GameManager gameManager;
    private PenaltyShootout shootout;
    private Timeline keeperIdle;
    private boolean running = false;
    private Principal principal;

    // -------------------------
    // setters públicos
    // -------------------------
    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
    }

    public void setMainApp(Principal p){
        this.principal = p; // CORRECCIÓN: asignar correctamente
    }

    // -------------------------
    // inicialización
    // -------------------------
    @FXML
    public void initialize() {
        disableShootButtons(true);
        setupKeeperAnimation();
        // Aseguramos log inicial si no fue seteado
        if (logArea != null && logArea.getText().isEmpty()) {
            logArea.setText("🏟️ BIENVENIDO AL PARTIDO DE PENALTIS\n\n");
        }
        // Garantizar que ballLayer esté vacío
        if (ballLayer != null) ballLayer.getChildren().clear();
    }

    private void setupKeeperAnimation() {
        keeperIdle = new Timeline(new KeyFrame(Duration.millis(800), e -> {
            if (lblGoalkeeper != null) {
                double shift = (Math.random() - 0.5) * 30;
                TranslateTransition t = new TranslateTransition(Duration.millis(400), lblGoalkeeper);
                t.setByX(shift);
                t.setAutoReverse(true);
                t.setCycleCount(2);
                t.play();
            }
        }));
        keeperIdle.setCycleCount(Animation.INDEFINITE);
    }

    public void refreshPanel() {
        resetState();
    }

    private void resetState() {
        running = false;
        shootout = null;
        if (lblPlayerScore != null) lblPlayerScore.setText("0");
        if (lblOpponentScore != null) lblOpponentScore.setText("0");
        if (progressKicks != null) progressKicks.setProgress(0);
        if (lblEstado != null) lblEstado.setText("Preparado para comenzar...");
        if (logArea != null) logArea.setText("🏟️ BIENVENIDO AL PARTIDO DE PENALTIS\n\n");
        disableShootButtons(true);
        if (btnStart != null) btnStart.setDisable(false);
        if (lblGoalkeeper != null) lblGoalkeeper.setTranslateX(0);
        if (ballLayer != null) ballLayer.getChildren().clear();
        if (keeperIdle != null) keeperIdle.stop();
    }

    // -------------------------
    // acciones UI
    // -------------------------
    @FXML
    private void onStart() {
        if (gameManager == null) {
            log("GameManager no inicializado.");
            return;
        }

        // inicializar tanda de penaltis
        shootout = new PenaltyShootout(gameManager.getJugador());
        running = true;
        if (lblPlayerScore != null) lblPlayerScore.setText("0");
        if (lblOpponentScore != null) lblOpponentScore.setText("0");
        if (progressKicks != null) progressKicks.setProgress(0);
        if (lblEstado != null) lblEstado.setText("Partido iniciado - Penalti 1/" + shootout.getTotalKicks());
        if (logArea != null) logArea.appendText("🚀 PARTIDO INICIADO\n");
        disableShootButtons(false);
        if (btnStart != null) btnStart.setDisable(true);

        if (keeperIdle != null) keeperIdle.play();
    }

    @FXML
    private void onLeft() { if (running) playerShoot(PenaltyShootout.Direction.LEFT); }

    @FXML
    private void onCenter() { if (running) playerShoot(PenaltyShootout.Direction.CENTER); }

    @FXML
    private void onRight() { if (running) playerShoot(PenaltyShootout.Direction.RIGHT); }

    // -------------------------
    // lógica del disparo + animación
    // -------------------------
    private void playerShoot(PenaltyShootout.Direction dir) {
        if (!running || shootout == null) return;

        disableShootButtons(true);

        // animación: calcular tamaños en runLater (asegura que la escena ya esté renderizada)
        Platform.runLater(() -> animatePlayerShot(dir, shotFinished -> {
            PenaltyShootout.KickResult res = shootout.playerShoot(dir);

            Platform.runLater(() -> {
                if (lblPlayerScore != null) lblPlayerScore.setText(String.valueOf(res.playerGoals));
                if (lblOpponentScore != null) lblOpponentScore.setText(String.valueOf(res.opponentGoals));
                if (progressKicks != null) progressKicks.setProgress((double) shootout.getKicksTaken() / shootout.getTotalKicks());
                if (logArea != null) logArea.appendText(formatKickLog(res));

                if (res.seriesFinished) {
                    concludeMatch(res);
                } else {
                    PauseTransition pause = new PauseTransition(Duration.millis(800));
                    pause.setOnFinished(ev -> {
                        disableShootButtons(false);
                        if (lblEstado != null) lblEstado.setText("Penalti " + (shootout.getKicksTaken() + 1) + "/" + shootout.getTotalKicks());
                    });
                    pause.play();
                }
            });
        }));
    }

    private void animatePlayerShot(PenaltyShootout.Direction dir, java.util.function.Consumer<Void> onDone) {
        if (pitchPane == null || ballLayer == null) {
            onDone.accept(null);
            return;
        }

        // obtener bounds con runLater -> ya hecho por el llamador
        Bounds bounds = pitchPane.getLayoutBounds();
        double width = pitchPane.getWidth() > 0 ? pitchPane.getWidth() : bounds.getWidth();
        double height = pitchPane.getHeight() > 0 ? pitchPane.getHeight() : bounds.getHeight();

        // fallback si aún no hay medidas
        if (width <= 0 || height <= 0) {
            // esperar un frame
            Platform.runLater(() -> animatePlayerShot(dir, onDone));
            return;
        }

        double startX = width * 0.2;
        double startY = height * 0.85;
        double targetX;
        switch (dir) {
            case LEFT:   targetX = width * 0.35; break;
            case CENTER: targetX = width * 0.5;  break;
            case RIGHT:  targetX = width * 0.65; break;
            default:     targetX = width * 0.5;  break;
        }
        double targetY = height * 0.15;

        Circle ball = new Circle(8);
        ball.getStyleClass().add("ball");

        // colocar relativo al ballLayer (use translate)
        ball.setTranslateX(startX);
        ball.setTranslateY(startY);
        ballLayer.getChildren().add(ball);

        TranslateTransition moveBall = new TranslateTransition(Duration.millis(600), ball);
        moveBall.setToX(targetX - startX);
        moveBall.setToY(targetY - startY);
        moveBall.setInterpolator(Interpolator.EASE_IN);

        moveBall.setOnFinished(e -> {
            FadeTransition fadeBall = new FadeTransition(Duration.millis(200), ball);
            fadeBall.setToValue(0);
            fadeBall.setOnFinished(ev -> {
                ballLayer.getChildren().remove(ball);
                onDone.accept(null);
            });
            fadeBall.play();
        });

        moveBall.play();
    }

    private String formatKickLog(PenaltyShootout.KickResult res) {
        StringBuilder sb = new StringBuilder();
        sb.append("Penalti ").append(shootout.getKicksTaken()).append(": ");
        sb.append(res.playerScored ? "✅ GOL" : "❌ Fallado");
        sb.append(" | Rival: ").append(res.opponentScored ? "⚽ Gol" : "🧤 Falló");
        sb.append("\nMarcador: ").append(res.playerGoals).append(" - ").append(res.opponentGoals).append("\n\n");
        return sb.toString();
    }

    private void concludeMatch(PenaltyShootout.KickResult res) {
        running = false;
        disableShootButtons(true);
        if (btnStart != null) btnStart.setDisable(false);
        if (keeperIdle != null) keeperIdle.stop();

        String resultMsg;
        if (res.playerGoals > res.opponentGoals) resultMsg = "🎉 ¡VICTORIA!";
        else if (res.playerGoals < res.opponentGoals) resultMsg = "💔 Derrota";
        else resultMsg = "🤝 Empate";

        if (lblEstado != null) lblEstado.setText("Partido finalizado: " + resultMsg);
        if (logArea != null) logArea.appendText("\n" + resultMsg + " - Marcador final: " +
                res.playerGoals + " - " + res.opponentGoals + "\n");

        if (res.playerGoals > res.opponentGoals && gameManager != null) {
            Partido partidoSimulado = new Partido("Tanda de Penaltis",
                    gameManager.getJugador().getNombre() + " FC",
                    "Rival");
            gameManager.procesarVictoria(partidoSimulado);
        }
    }

    private void disableShootButtons(boolean disable) {
        if (btnLeft != null) btnLeft.setDisable(disable);
        if (btnCenter != null) btnCenter.setDisable(disable);
        if (btnRight != null) btnRight.setDisable(disable);
    }

    private void log(String message) {
        if (logArea != null) Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    // -------------------------
    // volver al menú principal
    // -------------------------
    @FXML
    private void onVolver() {
        // detener animaciones
        if (keeperIdle != null) keeperIdle.stop();
        running = false;
        if (principal != null) {
            principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
        } else {
            log("principal null en onVolver()");
        }
    }
}
