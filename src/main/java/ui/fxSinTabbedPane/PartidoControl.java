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

public class PartidoControl {

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
    private Principal principal;

    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
    }

    @FXML
    public void initialize() {
        disableShootButtons(true);
        setupKeeperAnimation();
    }

    private void setupKeeperAnimation() {
        keeperIdle = new Timeline(
                new KeyFrame(Duration.millis(800), e -> {
                    if (lblGoalkeeper != null) {
                        double shift = (Math.random() - 0.5) * 30;
                        TranslateTransition t = new TranslateTransition(Duration.millis(400), lblGoalkeeper);
                        t.setByX(shift);
                        t.setAutoReverse(true);
                        t.setCycleCount(2);
                        t.play();
                    }
                })
        );
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
        if (lblGoalkeeper != null) {
            lblGoalkeeper.setTranslateX(0);
        }
        if (ballLayer != null) {
            ballLayer.getChildren().clear();
        }
        if (keeperIdle != null) {
            keeperIdle.stop();
        }
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

        if (keeperIdle != null) {
            keeperIdle.play();
        }
    }

    @FXML
    private void onLeft() {
        if (running) playerShoot(PenaltyShootout.Direction.LEFT);
    }

    @FXML
    private void onCenter() {
        if (running) playerShoot(PenaltyShootout.Direction.CENTER);
    }

    @FXML
    private void onRight() {
        if (running) playerShoot(PenaltyShootout.Direction.RIGHT);
    }

    private void playerShoot(PenaltyShootout.Direction dir) {
        if (!running || shootout == null) return;

        disableShootButtons(true);

        animatePlayerShot(dir, shotFinished -> {
            PenaltyShootout.KickResult res = shootout.playerShoot(dir);

            Platform.runLater(() -> {
                lblPlayerScore.setText(String.valueOf(res.playerGoals));
                lblOpponentScore.setText(String.valueOf(res.opponentGoals));
                progressKicks.setProgress((double) shootout.getKicksTaken() / shootout.getTotalKicks());
                logArea.appendText(formatKickLog(res));

                if (res.seriesFinished) {
                    concludeMatch(res);
                } else {
                    PauseTransition pause = new PauseTransition(Duration.millis(1000));
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
        if (pitchPane == null || ballLayer == null) {
            onDone.accept(null);
            return;
        }

        Circle ball = new Circle(8);
        ball.getStyleClass().add("ball");
        Bounds bounds = pitchPane.getLayoutBounds();

        double startX = bounds.getMinX() + bounds.getWidth() * 0.2;
        double startY = bounds.getMinY() + bounds.getHeight() * 0.85;
        ball.setTranslateX(startX);
        ball.setTranslateY(startY);
        ballLayer.getChildren().add(ball);

        double targetX;
        switch (dir) {
            case LEFT:
                targetX = bounds.getMinX() + bounds.getWidth() * 0.35;
                break;
            case CENTER:
                targetX = bounds.getMinX() + bounds.getWidth() * 0.5;
                break;
            case RIGHT:
                targetX = bounds.getMinX() + bounds.getWidth() * 0.65;
                break;
            default:
                targetX = bounds.getMinX() + bounds.getWidth() * 0.5;
        }
        double targetY = bounds.getMinY() + bounds.getHeight() * 0.15;

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
        btnStart.setDisable(false);

        if (keeperIdle != null) {
            keeperIdle.stop();
        }

        String resultMsg;
        if (res.playerGoals > res.opponentGoals) {
            resultMsg = "🎉 ¡VICTORIA!";
        } else if (res.playerGoals < res.opponentGoals) {
            resultMsg = "💔 Derrota";
        } else {
            resultMsg = "🤝 Empate";
        }

        lblEstado.setText("Partido finalizado: " + resultMsg);
        logArea.appendText("\n" + resultMsg + " - Marcador final: " +
                res.playerGoals + " - " + res.opponentGoals + "\n");

        if (res.playerGoals > res.opponentGoals && gameManager != null) {
            Partido partidoSimulado = new Partido("Tanda de Penaltis",
                    gameManager.getJugador().getNombre() + " FC",
                    "Rival");
            gameManager.procesarVictoria(partidoSimulado);
        }
    }

    private void disableShootButtons(boolean disable) {
        btnLeft.setDisable(disable);
        btnCenter.setDisable(disable);
        btnRight.setDisable(disable);
    }

    private void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public void setMainApp(Principal p){
        this.principal=principal;
    }
}