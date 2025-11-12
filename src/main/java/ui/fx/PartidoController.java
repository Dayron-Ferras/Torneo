package ui.fx;

import game.GameManager;
import game.PenaltyShootout;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import model.Partido;

public class PartidoController {

    @FXML private Label lblEstado;
    @FXML private Pane pitchPane;
    @FXML private Pane ballLayer;
    @FXML private Label lblGoalkeeper;
    @FXML private Label lblPlayerScore;
    @FXML private Label lblOpponentScore;
    @FXML private ProgressBar progressKicks;
    @FXML private Button btnLeft, btnCenter, btnRight, btnStart;
    @FXML private TextArea logArea;

    private GameManager gameManager;
    private PenaltyShootout shootout;
    private Timeline keeperIdle;
    private boolean running = false;

    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
    }

    @FXML
    public void initialize() {
        disableShootButtons(true);
        // small keeper idle animation
        keeperIdle = new Timeline(new KeyFrame(Duration.millis(600), e -> {
            if (lblGoalkeeper != null) {
                double shift = (Math.random() - 0.5) * 40;
                TranslateTransition t = new TranslateTransition(Duration.millis(300), lblGoalkeeper);
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
        lblPlayerScore.setText("0");
        lblOpponentScore.setText("0");
        progressKicks.setProgress(0);
        lblEstado.setText("Preparado para comenzar...");
        logArea.setText("🏟️ BIENVENIDO AL PARTIDO DE PENALTIS\n\n");
        disableShootButtons(true);
        btnStart.setDisable(false);
        lblGoalkeeper.setTranslateX(0);
        ballLayer.getChildren().clear();
        if (keeperIdle != null) keeperIdle.stop();
    }

    @FXML
    private void onStart() {
        if (gameManager == null) {
            log("GameManager no inicializado.");
            return;
        }
        shootout = new PenaltyShootout(gameManager.getJugador());
        running = true;
        lblPlayerScore.setText("0");
        lblOpponentScore.setText("0");
        progressKicks.setProgress(0);
        lblEstado.setText("Partido iniciado - Penalti 1/" + shootout.getTotalKicks());
        logArea.appendText("🚀 PARTIDO INICIADO\n");
        disableShootButtons(false);
        btnStart.setDisable(true);
        keeperIdle.play();
    }

    @FXML private void onLeft() { playerShoot(PenaltyShootout.Direction.LEFT); }
    @FXML private void onCenter() { playerShoot(PenaltyShootout.Direction.CENTER); }
    @FXML private void onRight() { playerShoot(PenaltyShootout.Direction.RIGHT); }

    private void playerShoot(PenaltyShootout.Direction dir) {
        if (!running || shootout == null) return;

        disableShootButtons(true);
        // animate ball from penalty spot to goalkeeper direction
        animatePlayerShot(dir, shotFinished -> {
            // compute logic via shootout
            PenaltyShootout.KickResult res = shootout.playerShoot(dir);
            Platform.runLater(() -> {
                lblPlayerScore.setText(String.valueOf(res.playerGoals));
                lblOpponentScore.setText(String.valueOf(res.opponentGoals));
                progressKicks.setProgress((double) shootout.getKicksTaken() / shootout.getTotalKicks());
                logArea.appendText(formatKickLog(res));
                if (res.seriesFinished) {
                    concludeMatch(res);
                } else {
                    // small delay then re-enable
                    PauseTransition pause = new PauseTransition(Duration.millis(900));
                    pause.setOnFinished(ev -> {
                        disableShootButtons(false);
                        lblEstado.setText("Penalti " + (shootout.getKicksTaken() + 1) + "/" + shootout.getTotalKicks());
                    });
                    pause.play();
                }
            });
        });
    }

    private void animatePlayerShot(PenaltyShootout.Direction dir, java.util.function.Consumer<Void> onDone) {
        // create ball
        javafx.scene.shape.Circle ball = new javafx.scene.shape.Circle(8);
        ball.getStyleClass().add("ball");
        Bounds bounds = pitchPane.getLayoutBounds();

        double startX = bounds.getMinX() + bounds.getWidth() * 0.2;
        double startY = bounds.getMinY() + bounds.getHeight() * 0.85;
        ball.setTranslateX(startX);
        ball.setTranslateY(startY);
        ballLayer.getChildren().add(ball);

        double targetX;
        switch (dir) {
            case LEFT: targetX = bounds.getMinX() + bounds.getWidth() * 0.35; break;
            case CENTER: targetX = bounds.getMinX() + bounds.getWidth() * 0.5; break;
            default: targetX = bounds.getMinX() + bounds.getWidth() * 0.7; break;
        }
        double targetY = bounds.getMinY() + bounds.getHeight() * 0.15;

        TranslateTransition tt = new TranslateTransition(Duration.millis(650), ball);
        tt.setToX(targetX);
        tt.setToY(targetY);
        tt.setInterpolator(javafx.animation.Interpolator.EASE_IN);
        tt.setOnFinished(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(180), ball);
            ft.setToValue(0);
            ft.setOnFinished(ev -> {
                ballLayer.getChildren().remove(ball);
                onDone.accept(null);
            });
            ft.play();
        });
        tt.play();
    }

    private String formatKickLog(PenaltyShootout.KickResult res) {
        StringBuilder sb = new StringBuilder();
        sb.append("Penal ").append(shootout.getKicksTaken()).append(": ");
        sb.append(res.playerScored ? "✅ GOL" : "❌ Fallado");
        sb.append(" | Rival: ").append(res.opponentScored ? "⚽ Gol" : "🧤 Falló");
        sb.append("\nMarcador: ").append(res.playerGoals).append(" - ").append(res.opponentGoals).append("\n\n");
        return sb.toString();
    }

    private void concludeMatch(PenaltyShootout.KickResult res) {
        running = false;
        disableShootButtons(true);
        btnStart.setDisable(false);
        keeperIdle.stop();

        String resultMsg = res.playerGoals > res.opponentGoals ? "🎉 ¡VICTORIA!" : res.playerGoals < res.opponentGoals ? "💔 Derrota" : "🤝 Empate";
        lblEstado.setText("Partido finalizado: " + resultMsg);
        logArea.appendText("\n" + resultMsg + " - Marcador final: " + res.playerGoals + " - " + res.opponentGoals + "\n");

        // notify GameManager for rewards if win (use procesarVictoria/completarTorneo accordingly)
        if (res.playerGoals > res.opponentGoals && gameManager != null) {
            gameManager.procesarVictoria(new Partido("Tanda", gameManager.getJugador().getNombre(), "Rival"));
        }
        // show small notification (non-blocking)
    }

    private void disableShootButtons(boolean disable) {
        btnLeft.setDisable(disable);
        btnCenter.setDisable(disable);
        btnRight.setDisable(disable);
    }

    private void log(String s) {
        Platform.runLater(() -> logArea.appendText(s + "\n"));
    }
}