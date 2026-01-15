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
import model.Club;
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

    // Variables para conectar con el torneo
    private Partido partidoTorneoActual; // El partido del torneo que estamos jugando
    private boolean esPartidoDeTorneo = false; // Indica si es un partido de torneo

    // -------------------------
    // setters públicos
    // -------------------------
    public void setGameManager(GameManager gm) {
        this.gameManager = gm;

        // Verificar si hay un partido de torneo activo
        if (gameManager != null && gameManager.getArbolTorneoActual() != null) {
            this.partidoTorneoActual = gameManager.getArbolTorneoActual()
                    .getPartidoActual(gameManager.getJugador());

            if (partidoTorneoActual != null && !partidoTorneoActual.isJugado()) {
                esPartidoDeTorneo = true;
                logArea.setText("🏆 PARTIDO DE TORNEO - TANDA DE PENALTIS\n");
                logArea.appendText("═══════════════════════════════\n");
                logArea.appendText("Ronda: " + partidoTorneoActual.getRonda() + "\n");
                logArea.appendText("Local: " + partidoTorneoActual.getEquipoLocal().getNombre() + "\n");
                logArea.appendText("Visitante: " + partidoTorneoActual.getEquipoVisitante().getNombre() + "\n\n");
                logArea.appendText("¡Preparado para comenzar!\n\n");
            }
        }
    }

    public void setMainApp(Principal p){
        this.principal = p;
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

        // No resetear log si es partido de torneo
        if (logArea != null && !esPartidoDeTorneo) {
            logArea.setText("🏟️ BIENVENIDO AL PARTIDO DE PENALTIS\n\n");
        }

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

        if (esPartidoDeTorneo) {
            logArea.appendText("🚀 TANDA DE PENALTIS INICIADA\n");
            logArea.appendText("Se jugarán " + shootout.getTotalKicks() + " penaltis\n\n");
        } else {
            if (logArea != null) logArea.appendText("🚀 PARTIDO INICIADO\n");
        }

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
    // lógica del disparo + animación (SIN MODIFICAR)
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
        boolean jugadorGano = res.playerGoals > res.opponentGoals;
        boolean empate = res.playerGoals == res.opponentGoals;

        if (jugadorGano) {
            resultMsg = "🎉 ¡VICTORIA!";
        } else if (empate) {
            resultMsg = "🤝 Empate";
        } else {
            resultMsg = "💔 Derrota";
        }

        if (lblEstado != null) lblEstado.setText("Partido finalizado: " + resultMsg);
        if (logArea != null) logArea.appendText("\n" + resultMsg + " - Marcador final: " +
                res.playerGoals + " - " + res.opponentGoals + "\n");

        // ============ CÓDIGO MODIFICADO ============
        if (esPartidoDeTorneo && partidoTorneoActual != null && !partidoTorneoActual.isJugado()) {
            // 1. MARCAR COMO JUGADO INMEDIATAMENTE
            boolean resultadoFinal;
            if (empate) {
                // En empate, decidir por sorteo
                resultadoFinal = Math.random() > 0.5;
                marcarPartidoComoJugado(resultadoFinal,
                        resultadoFinal ? res.playerGoals + 1 : res.playerGoals,
                        resultadoFinal ? res.opponentGoals : res.opponentGoals + 1);
            } else {
                resultadoFinal = jugadorGano;
                marcarPartidoComoJugado(jugadorGano, res.playerGoals, res.opponentGoals);
            }

            // 2. LOG DEL RESULTADO
            logArea.appendText("\n═══════════════════════════════\n");
            logArea.appendText(resultadoFinal ? "✅ ¡HAS GANADO EL PARTIDO!\n" : "❌ HAS PERDIDO EL PARTIDO\n");
            logArea.appendText(resultadoFinal ? "Avanzas a la siguiente ronda\n" : "Has sido eliminado\n");

            // 3. LUEGO mostrar opciones
            mostrarOpcionesVolver();

        } else if (jugadorGano && gameManager != null) {
            // Para partidos amistosos (no de torneo)
            Partido partidoSimulado = new Partido("Tanda de Penaltis",
                    gameManager.getJugador().getNombre() + " FC",
                    "Rival");
            gameManager.procesarVictoria(partidoSimulado);
        }
    }
    // ============ NUEVO MÉTODO PARA PROCESAR RESULTADO DEL TORNEO ============
    private void procesarResultadoParaTorneo(PenaltyShootout.KickResult res, boolean jugadorGano, boolean empate) {
        try {
            logArea.appendText("\n═══════════════════════════════\n");

            if (jugadorGano) {
                logArea.appendText("✅ ¡HAS GANADO EL PARTIDO DEL TORNEO!\n");
                logArea.appendText("Avanzas a la siguiente ronda\n");

                // Marcar el partido como jugado y con victoria
                marcarPartidoComoJugado(true, res.playerGoals, res.opponentGoals);

            } else if (!empate) {
                logArea.appendText("❌ HAS PERDIDO EL PARTIDO DEL TORNEO\n");
                logArea.appendText("Has sido eliminado del torneo\n");

                // Marcar el partido como jugado y con derrota
                marcarPartidoComoJugado(false, res.playerGoals, res.opponentGoals);

            } else {
                logArea.appendText("🎯 EMPATE - Se decide por sorteo\n");
                // En torneo de eliminación, se necesita un ganador
                boolean ganaPorSorteo = Math.random() > 0.5;

                if (ganaPorSorteo) {
                    logArea.appendText("✅ ¡Ganas por sorteo! Avanzas\n");
                    marcarPartidoComoJugado(true, res.playerGoals + 1, res.opponentGoals);
                } else {
                    logArea.appendText("❌ Pierdes por sorteo. Eliminado\n");
                    marcarPartidoComoJugado(false, res.playerGoals, res.opponentGoals + 1);
                }
            }

            // Actualizar el árbol del torneo
            if (gameManager.getArbolTorneoActual() != null) {
                gameManager.getArbolTorneoActual().actualizarLlave();
            }

        } catch (Exception e) {
            log("Error al procesar resultado del torneo: " + e.getMessage());
        }
    }

    // ============ NUEVO MÉTODO PARA MARCAR PARTIDO COMO JUGADO ============
    private void marcarPartidoComoJugado(boolean jugadorGano, int golesJugador, int golesRival) {
        try {
            // Usar reflexión para modificar los campos privados del partido
            java.lang.reflect.Field jugadoField = Partido.class.getDeclaredField("jugado");
            jugadoField.setAccessible(true);
            jugadoField.set(partidoTorneoActual, true);

            java.lang.reflect.Field golesLocalField = Partido.class.getDeclaredField("golesLocal");
            golesLocalField.setAccessible(true);

            java.lang.reflect.Field golesVisitanteField = Partido.class.getDeclaredField("golesVisitante");
            golesVisitanteField.setAccessible(true);

            java.lang.reflect.Field ganadorField = Partido.class.getDeclaredField("ganador");
            ganadorField.setAccessible(true);

            // Determinar quién es local y quién visitante
            Club clubJugador = gameManager.getJugador().getClubActual();
            Club equipoLocal = partidoTorneoActual.getEquipoLocal();
            Club equipoVisitante = partidoTorneoActual.getEquipoVisitante();

            if (clubJugador != null && clubJugador.equals(equipoLocal)) {
                // Jugador es local
                golesLocalField.set(partidoTorneoActual, golesJugador);
                golesVisitanteField.set(partidoTorneoActual, golesRival);
                ganadorField.set(partidoTorneoActual, jugadorGano ? equipoLocal : equipoVisitante);
            } else {
                // Jugador es visitante
                golesLocalField.set(partidoTorneoActual, golesRival);
                golesVisitanteField.set(partidoTorneoActual, golesJugador);
                ganadorField.set(partidoTorneoActual, jugadorGano ? equipoVisitante : equipoLocal);
            }

            log("✅ Partido del torneo actualizado: " + partidoTorneoActual.toString());

        } catch (Exception e) {
            log("Error al actualizar partido: " + e.getMessage());

            // Método alternativo: usar el GameManager
            try {
                if (jugadorGano) {
                    gameManager.procesarVictoria(partidoTorneoActual);
                } else {
                    // Si el jugador pierde, marcar como eliminado
                    gameManager.jugarPartidoActual(); // Esto debería procesar la derrota
                }
            } catch (Exception ex) {
                log("Error alternativo: " + ex.getMessage());
            }
        }
    }

    // ============ NUEVO MÉTODO PARA MOSTRAR OPCIONES DE VOLVER ============
    private void mostrarOpcionesVolver() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Partido finalizado");
            alert.setHeaderText("¿Qué quieres hacer ahora?");
            alert.setContentText("El partido del torneo ha terminado.");

            ButtonType btnVolverTorneo = new ButtonType("Volver al torneo", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnQuedarse = new ButtonType("Quedarse aquí", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType btnMenuPrincipal = new ButtonType("Menú principal", ButtonBar.ButtonData.OTHER);

            alert.getButtonTypes().setAll(btnVolverTorneo, btnQuedarse, btnMenuPrincipal);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnVolverTorneo) {
                    volverAlTorneo();
                } else if (response == btnMenuPrincipal) {
                    if (principal != null) {
                        principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
                    }
                }
                // Si elige "Quedarse aquí", no hace nada
            });
        });
    }

    private void volverAlTorneo() {
        if (keeperIdle != null) keeperIdle.stop();
        running = false;

        if (principal != null) {
            try {
                // 1. Forzar que el GameManager procese el resultado
                if (gameManager != null && partidoTorneoActual != null) {
                    // IMPORTANTE: Primero asegurarnos de que el partido esté marcado como jugado
                    if (!partidoTorneoActual.isJugado()) {
                        // Si por alguna razón no se marcó en concludeMatch, marcarlo ahora
                        boolean jugadorGano = marcarPartidoComoJugadoSiNoEstaMarcado();
                    }

                    // Simular que se jugó el partido en el GameManager
                    // Esto actualiza el estado del jugador y el árbol
                    gameManager.jugarPartidoActual();

                    // Esperar un momento para que se procese completamente
                    Thread.sleep(500); // Aumenté a 500ms para más seguridad

                    // Forzar actualización del árbol del torneo
                    if (gameManager.getArbolTorneoActual() != null) {
                        gameManager.getArbolTorneoActual().actualizarLlave();
                    }
                }
            } catch (Exception e) {
                log("⚠️ Error al procesar resultado: " + e.getMessage());
                e.printStackTrace(); // Para debug
            }

            // 2. Volver al torneo
            principal.setScreen("/uiSinTabbedPane/torneoEnVivo.fxml");

            // 3. Asegurar que el torneo se actualice después de cargar
            Platform.runLater(() -> {
                try {
                    Thread.sleep(300); // Pequeña pausa para que cargue la pantalla
                    if (gameManager != null && gameManager.getArbolTorneoActual() != null) {
                        // Última actualización por si acaso
                        gameManager.getArbolTorneoActual().actualizarLlave();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    // Método auxiliar para asegurar que el partido esté marcado
    private boolean marcarPartidoComoJugadoSiNoEstaMarcado() {
        try {
            if (partidoTorneoActual != null && !partidoTorneoActual.isJugado()) {
                // Usar reflexión para verificar si hay goles en el marcador
                java.lang.reflect.Field golesLocalField = Partido.class.getDeclaredField("golesLocal");
                golesLocalField.setAccessible(true);
                Integer golesLocal = (Integer) golesLocalField.get(partidoTorneoActual);

                java.lang.reflect.Field golesVisitanteField = Partido.class.getDeclaredField("golesVisitante");
                golesVisitanteField.setAccessible(true);
                Integer golesVisitante = (Integer) golesVisitanteField.get(partidoTorneoActual);

                // Si no hay goles, establecer unos por defecto (el jugador pierde)
                if (golesLocal == 0 && golesVisitante == 0) {
                    golesLocalField.set(partidoTorneoActual, 0);
                    golesVisitanteField.set(partidoTorneoActual, 1); // El rival gana 1-0

                    // Marcar ganador
                    java.lang.reflect.Field ganadorField = Partido.class.getDeclaredField("ganador");
                    ganadorField.setAccessible(true);

                    Club clubJugador = gameManager.getJugador().getClubActual();
                    if (clubJugador != null && clubJugador.equals(partidoTorneoActual.getEquipoLocal())) {
                        ganadorField.set(partidoTorneoActual, partidoTorneoActual.getEquipoVisitante());
                    } else {
                        ganadorField.set(partidoTorneoActual, partidoTorneoActual.getEquipoLocal());
                    }

                    // Marcar como jugado
                    java.lang.reflect.Field jugadoField = Partido.class.getDeclaredField("jugado");
                    jugadoField.setAccessible(true);
                    jugadoField.set(partidoTorneoActual, true);

                    log("⚠️ Partido marcado como jugado por defecto (derrota)");
                    return false; // Jugador perdió
                }
                return true; // Ya tenía goles, asumimos que está correcto
            }
        } catch (Exception e) {
            log("Error al verificar partido: " + e.getMessage());
        }
        return false;
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

        if (esPartidoDeTorneo) {
            // Si es partido de torneo, preguntar confirmación
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Abandonar partido de torneo");
            alert.setHeaderText("¿Estás seguro de abandonar el partido?");
            alert.setContentText("Si abandonas, se considerará como derrota.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Marcar como derrota si aún no se jugó
                    if (partidoTorneoActual != null && !partidoTorneoActual.isJugado()) {
                        marcarPartidoComoJugado(false, 0, 1);
                    }

                    if (principal != null) {
                        principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
                    }
                }
            });
        } else {
            // Partido amistoso, volver normalmente
            if (principal != null) {
                principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
            } else {
                log("principal null en onVolver()");
            }
        }
    }
}