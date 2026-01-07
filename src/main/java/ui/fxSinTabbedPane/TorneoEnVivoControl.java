package ui.fxSinTabbedPane;

import game.GameManager;
import model.Torneo;
import model.Club;
import model.Partido;
import tree.ArbolTorneo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TorneoEnVivoControl {

    @FXML private Label lblTorneoNombre;
    @FXML private Label lblRondaActual;
    @FXML private TreeView<String> treeBracket;
    @FXML private Button btnJugarPartido;
    @FXML private Button btnSimularTodo;
    @FXML private Button btnForzarAvance;
    @FXML private Button btnVolverMenu;
    @FXML private VBox panelPartidoContainer;
    @FXML private TextArea logTorneo;
    @FXML private ProgressBar progressTorneo;
    @FXML private Label lblEstadoTorneo;
    @FXML private Separator separator1;
    @FXML private Separator separator2;

    private GameManager gameManager;
    private Principal principal;
    private Torneo torneoActual;
    private ArbolTorneo arbolTorneoActual;
    private boolean torneoTerminado = false;

    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
        cargarTorneoActual();
    }

    public void setMainApp(Principal p) {
        this.principal = p;
    }

    @FXML
    public void initialize() {
        btnJugarPartido.setDisable(true);
        btnSimularTodo.setDisable(true);
        btnForzarAvance.setDisable(true);

        if (logTorneo != null) {
            logTorneo.setEditable(false);
            logTorneo.setText("⚽ TORNEO EN VIVO\n");
            logTorneo.appendText("══════════════════\n\n");
        }

        // Configurar estilo del árbol
        if (treeBracket != null) {
            treeBracket.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        }

        // Configurar estilo de las etiquetas
        if (lblTorneoNombre != null) {
            lblTorneoNombre.setFont(Font.font("System", FontWeight.BOLD, 18));
            lblTorneoNombre.setTextFill(Color.DARKBLUE);
        }

        if (lblEstadoTorneo != null) {
            lblEstadoTorneo.setFont(Font.font("System", FontWeight.BOLD, 14));
        }
    }

    private void cargarTorneoActual() {
        if (gameManager == null) return;

        torneoActual = gameManager.getTorneoActual();
        arbolTorneoActual = gameManager.getArbolTorneoActual();

        Platform.runLater(() -> {
            if (torneoActual != null && arbolTorneoActual != null) {
                inicializarTorneo();
                actualizarUI();
            } else {
                mostrarSinTorneoActivo();
            }
        });
    }

    private void inicializarTorneo() {
        agregarLog("🏆 TORNEO INICIADO: " + torneoActual.getNombre());
        agregarLog("💰 Recompensa: $" + torneoActual.getRecompensa());
        agregarLog("📊 Nivel requerido: " + torneoActual.getNivelRequerido());
        agregarLog("──────────────────────────────");

        // Mostrar equipos participantes
        agregarLog("Equipos participantes:");
        for (Club club : torneoActual.getClubsParticipantes()) {
            agregarLog("  • " + club.getNombre() +
                    // Si Club no tiene método getNivel(), comentamos esta línea
                    // " (Nivel " + club.getNivel() + ")"
                    "");
        }
        agregarLog("");
    }

    private void actualizarUI() {
        if (torneoActual == null || arbolTorneoActual == null) return;

        lblTorneoNombre.setText(torneoActual.getNombre());

        // Actualizar estado del torneo
        actualizarEstadoTorneo();

        // Construir árbol visual
        construirArbolVisual();

        // Actualizar progreso
        actualizarProgreso();

        // Actualizar botones
        actualizarBotones();
    }

    private void actualizarEstadoTorneo() {
        if (arbolTorneoActual.isTorneoTerminado()) {
            torneoTerminado = true;
            Club campeon = arbolTorneoActual.getCampeon();
            if (campeon != null) {
                lblEstadoTorneo.setText("🏆 Campeón: " + campeon.getNombre());
                lblEstadoTorneo.setTextFill(Color.GOLDENROD);
            } else {
                lblEstadoTorneo.setText("🏆 Torneo terminado");
                lblEstadoTorneo.setTextFill(Color.DARKGREEN);
            }
            lblRondaActual.setText("Finalizado");
        } else {
            torneoTerminado = false;
            Partido partidoJugador = arbolTorneoActual.getPartidoActual(gameManager.getJugador());
            if (partidoJugador != null) {
                String ronda = formatearNombreRonda(partidoJugador.getRonda());
                lblRondaActual.setText(ronda);
                lblEstadoTorneo.setText("▶️ " + ronda + " pendiente");
                lblEstadoTorneo.setTextFill(Color.DARKORANGE);
            } else {
                lblRondaActual.setText("En progreso...");
                lblEstadoTorneo.setText("⏳ Esperando siguiente ronda");
                lblEstadoTorneo.setTextFill(Color.DARKGRAY);
            }
        }
    }

    private String formatearNombreRonda(String ronda) {
        if (ronda.contains("3") || ronda.contains("Final")) return "🏆 FINAL";
        if (ronda.contains("2") || ronda.contains("Semi")) return "🥈 SEMIFINAL";
        if (ronda.contains("1") || ronda.contains("Cuarto")) return "🥉 CUARTOS";
        return ronda;
    }

    private void actualizarProgreso() {
        if (progressTorneo == null) return;

        double progreso = 0.0;
        int totalPartidos = 0;
        int partidosJugados = 0;

        // Simular cálculo de progreso (esto depende de tu implementación de ArbolTorneo)
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldOut = System.out;

            System.setOut(ps);
            arbolTorneoActual.imprimirArbol();
            System.setOut(oldOut);

            String output = baos.toString();
            String[] lines = output.split("\n");

            for (String line : lines) {
                if (line.contains("vs")) {
                    totalPartidos++;
                    if (line.contains("Ganador:") || line.contains("→")) {
                        partidosJugados++;
                    }
                }
            }

            if (totalPartidos > 0) {
                progreso = (double) partidosJugados / totalPartidos;
            }

        } catch (Exception e) {
            progreso = 0.0;
        }

        progressTorneo.setProgress(progreso);

        // Cambiar color según progreso
        if (progreso >= 1.0) {
            progressTorneo.setStyle("-fx-accent: green;");
        } else if (progreso >= 0.5) {
            progressTorneo.setStyle("-fx-accent: orange;");
        } else {
            progressTorneo.setStyle("-fx-accent: red;");
        }
    }

    private void actualizarBotones() {
        if (torneoTerminado) {
            btnJugarPartido.setText("Torneo Completado");
            btnJugarPartido.setDisable(true);
            btnJugarPartido.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
            btnSimularTodo.setDisable(true);
            btnForzarAvance.setDisable(true);
            return;
        }

        Partido partidoActual = arbolTorneoActual.getPartidoActual(gameManager.getJugador());
        boolean puedeJugar = (partidoActual != null && !partidoActual.isJugado());

        // Verificar si los equipos están definidos
        if (puedeJugar) {
            boolean equiposDefinidos = verificarEquiposPartido(partidoActual);
            if (!equiposDefinidos) {
                btnJugarPartido.setText("Simular Partidos Previos");
                btnJugarPartido.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
                agregarLog("⚠️ Partido pendiente pero equipos no definidos completamente");
            } else {
                btnJugarPartido.setText("🎮 Jugar Partido");
                btnJugarPartido.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
            btnJugarPartido.setDisable(false);
        } else {
            btnJugarPartido.setText("No hay partido pendiente");
            btnJugarPartido.setDisable(true);
            btnJugarPartido.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
        }

        // Siempre habilitar botones de simulación
        btnSimularTodo.setDisable(false);
        btnForzarAvance.setDisable(false);
    }

    private boolean verificarEquiposPartido(Partido partido) {
        if (partido == null) return false;

        Club local = partido.getEquipoLocal();
        Club visitante = partido.getEquipoVisitante();

        if (local == null || visitante == null) {
            agregarLog("   Local: " + (local != null ? local.getNombre() : "null"));
            agregarLog("   Visitante: " + (visitante != null ? visitante.getNombre() : "null"));
            return false;
        }

        // Verificar que no sean equipos placeholder
        if (local.getNombre().contains("Por Definir") || visitante.getNombre().contains("Por Definir")) {
            return false;
        }

        return true;
    }

    private void construirArbolVisual() {
        if (treeBracket == null) return;

        try {
            // Capturar la salida de imprimirArbol()
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldOut = System.out;

            System.setOut(ps);
            arbolTorneoActual.imprimirArbol();
            System.setOut(oldOut);

            String treeOutput = baos.toString();
            TreeItem<String> root = construirArbolDesdeOutput(treeOutput);
            treeBracket.setRoot(root);

            // Expandir todos los nodos
            expandirNodos(root);

        } catch (Exception e) {
            e.printStackTrace();
            TreeItem<String> root = new TreeItem<>("Error al cargar el árbol");
            TreeItem<String> error = new TreeItem<>("Detalles: " + e.getMessage());
            root.getChildren().add(error);
            treeBracket.setRoot(root);
        }
    }

    private TreeItem<String> construirArbolDesdeOutput(String output) {
        TreeItem<String> root = new TreeItem<>("🏆 " + torneoActual.getNombre());

        String[] lines = output.split("\n");
        java.util.Stack<TreeItem<String>> stack = new java.util.Stack<>();
        stack.push(root);

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            // Calcular nivel de indentación
            int indent = 0;
            while (indent < line.length() && (line.charAt(indent) == ' ' || line.charAt(indent) == '\t')) {
                indent++;
            }

            // Ajustar stack según indentación
            while (stack.size() > indent + 1) {
                stack.pop();
            }

            String texto = line.trim();

            // Formatear texto
            if (texto.contains("===")) {
                // Título del torneo
                texto = texto.replace("===", "").trim();
                if (!texto.isEmpty()) {
                    TreeItem<String> titulo = new TreeItem<>("📋 " + texto);
                    root.getChildren().add(titulo);
                    stack.push(titulo);
                }
            } else if (texto.startsWith("Ronda")) {
                // Nueva ronda
                String nombreRonda = formatearNombreRonda(texto);
                TreeItem<String> rondaItem = new TreeItem<>(nombreRonda);
                stack.peek().getChildren().add(rondaItem);
                stack.push(rondaItem);
            } else if (texto.contains("vs")) {
                // Partido
                TreeItem<String> partidoItem = new TreeItem<>(formatearPartido(texto));
                stack.peek().getChildren().add(partidoItem);

                // Resaltar si es partido del jugador
                Club clubJugador = gameManager.getJugador().getClubActual();
                if (clubJugador != null && texto.contains(clubJugador.getNombre())) {
                    partidoItem.setValue("⭐ " + partidoItem.getValue());
                }
            }
        }

        return root;
    }

    private String formatearPartido(String partidoStr) {
        // Formatear para mejor visualización
        String formateado = partidoStr
                .replace("→ Ganador:", "→ 🏆")
                .replace("Ganador:", "🏆")
                .replace("Empate", "🤝 Empate")
                .replace("Por Definir", "❓ Por Definir");

        return formateado;
    }

    private void expandirNodos(TreeItem<String> item) {
        item.setExpanded(true);
        for (TreeItem<String> child : item.getChildren()) {
            expandirNodos(child);
        }
    }

    @FXML
    private void onJugarPartido() {
        if (gameManager == null || arbolTorneoActual == null) {
            mostrarAlerta("No hay torneo activo", Alert.AlertType.WARNING);
            return;
        }

        Partido partidoActual = arbolTorneoActual.getPartidoActual(gameManager.getJugador());

        if (partidoActual == null) {
            mostrarAlerta("No hay partidos pendientes", Alert.AlertType.INFORMATION);
            return;
        }

        if (partidoActual.isJugado()) {
            mostrarAlerta("Este partido ya fue jugado", Alert.AlertType.INFORMATION);
            return;
        }

        // Verificar equipos
        if (!verificarEquiposPartido(partidoActual)) {
            // Si los equipos no están definidos, simular partidos previos
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Equipos no definidos");
            alert.setHeaderText("Los equipos para este partido no están completamente definidos");
            alert.setContentText("¿Quieres simular automáticamente los partidos previos necesarios?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    simularHastaPartidoActual();
                    actualizarUI();
                }
            });
            return;
        }

        // Preparar partido
        prepararPartido(partidoActual);
    }

    private void prepararPartido(Partido partido) {
        Club local = partido.getEquipoLocal();
        Club visitante = partido.getEquipoVisitante();

        agregarLog("🎮 PREPARANDO PARTIDO");
        agregarLog("   " + formatearNombreRonda(partido.getRonda()));
        agregarLog("   " + local.getNombre() + " 🆚 " + visitante.getNombre());
        agregarLog("");

        // Cargar interfaz de partido
        cargarInterfazPartido(partido);
    }

    private void cargarInterfazPartido(Partido partido) {
        try {
            // IMPORTANTE: Usar FXMLLoader con importación correcta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uiSinTabbedPane/partidoRapido.fxml"));
            BorderPane partidoView = loader.load();

            PartidoRapidoControl partidoController = loader.getController();
            partidoController.setGameManager(gameManager);
            partidoController.setMainApp(new PrincipalWrapper());
            partidoController.refreshPanel();

            panelPartidoContainer.getChildren().clear();
            panelPartidoContainer.getChildren().add(partidoView);

            // Botón de control
            HBox controlBox = new HBox(10);
            controlBox.setAlignment(Pos.CENTER);
            controlBox.setStyle("-fx-padding: 15px; -fx-background-color: #f5f5f5;");

            Button btnSimularYVolver = new Button("🎲 Simular este partido");
            btnSimularYVolver.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            btnSimularYVolver.setOnAction(e -> {
                simularPartidoActual();
                panelPartidoContainer.getChildren().clear();
                actualizarUI();
            });

            Button btnJugarReal = new Button("🎮 Jugar con penalitis");
            btnJugarReal.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            btnJugarReal.setOnAction(e -> {
                // El partido se jugará a través de la interfaz de penalitis
                agregarLog("⚽ Iniciando tanda de penalitis...");
            });

            Button btnCancelar = new Button("❌ Cancelar");
            btnCancelar.setOnAction(e -> {
                panelPartidoContainer.getChildren().clear();
                agregarLog("Partido cancelado");
            });

            controlBox.getChildren().addAll(btnSimularYVolver, btnJugarReal, btnCancelar);
            panelPartidoContainer.getChildren().add(controlBox);

        } catch (Exception e) {
            e.printStackTrace();
            agregarLog("❌ Error al cargar la interfaz del partido: " + e.getMessage());
        }
    }

    private void simularPartidoActual() {
        Partido partido = arbolTorneoActual.getPartidoActual(gameManager.getJugador());
        if (partido == null || partido.isJugado()) return;

        Club local = partido.getEquipoLocal();
        Club visitante = partido.getEquipoVisitante();

        agregarLog("🎲 Simulando partido:");
        agregarLog("   " + local.getNombre() + " vs " + visitante.getNombre());

        // Jugar el partido
        gameManager.jugarPartidoActual();

        // Mostrar resultado
        Club ganador = partido.getGanador();
        if (ganador != null) {
            if (ganador.equals(gameManager.getJugador().getClubActual())) {
                agregarLog("   ✅ ¡VICTORIA! " + partido.getGolesLocal() + "-" + partido.getGolesVisitante());
                agregarLog("   💰 Recompensa obtenida");
            } else {
                agregarLog("   ❌ Derrota " + partido.getGolesLocal() + "-" + partido.getGolesVisitante());
            }
        }

        agregarLog("");
    }

    @FXML
    private void onSimularTodo() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Simular torneo completo");
        confirmacion.setHeaderText("¿Simular todo el torneo automáticamente?");
        confirmacion.setContentText("Se simularán todos los partidos pendientes hasta el final.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                simularTorneoCompleto();
            }
        });
    }

    private void simularTorneoCompleto() {
        agregarLog("⏩ SIMULANDO TORNEO COMPLETO");
        agregarLog("──────────────────────────────");

        int partidosSimulados = 0;
        int maxPartidos = 20;

        while (!arbolTorneoActual.isTorneoTerminado() && partidosSimulados < maxPartidos) {
            try {
                gameManager.jugarPartidoActual();
                partidosSimulados++;

                if (partidosSimulados % 3 == 0) {
                    agregarLog("📊 " + partidosSimulados + " partidos simulados...");
                }

                try { Thread.sleep(50); } catch (InterruptedException e) {}

            } catch (Exception e) {
                agregarLog("❌ Error en simulación: " + e.getMessage());
                break;
            }
        }

        agregarLog("──────────────────────────────");
        agregarLog("✅ " + partidosSimulados + " partidos simulados");

        // Mostrar resultado final
        if (arbolTorneoActual.isTorneoTerminado()) {
            Club campeon = arbolTorneoActual.getCampeon();
            Club clubJugador = gameManager.getJugador().getClubActual();

            if (campeon != null && campeon.equals(clubJugador)) {
                agregarLog("\n🏆 ¡FELICIDADES! ¡HAS GANADO EL TORNEO!");
                agregarLog("💰 Recompensa: $" + torneoActual.getRecompensa() + " + 500 XP");
                mostrarAlerta("¡Campeón!", Alert.AlertType.INFORMATION);
            } else if (campeon != null) {
                agregarLog("\n🏆 Torneo terminado");
                agregarLog("   Campeón: " + campeon.getNombre());
            }
        } else {
            agregarLog("\n⚠️ El torneo no pudo completarse automáticamente");
        }

        actualizarUI();
    }

    private void simularHastaPartidoActual() {
        agregarLog("🔧 SIMULANDO PARTIDOS PREVIOS");

        Partido partidoObjetivo = arbolTorneoActual.getPartidoActual(gameManager.getJugador());
        if (partidoObjetivo == null) return;

        int simulados = 0;
        int maxSimulaciones = 10;

        while (!verificarEquiposPartido(partidoObjetivo) && simulados < maxSimulaciones) {
            try {
                gameManager.jugarPartidoActual();
                simulados++;
                agregarLog("   📋 Partido " + simulados + " simulado");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            } catch (Exception e) {
                agregarLog("   ❌ Error: " + e.getMessage());
                break;
            }
        }

        agregarLog("✅ " + simulados + " partidos simulados");
        if (verificarEquiposPartido(partidoObjetivo)) {
            agregarLog("   🎯 Partido principal listo para jugar");
        }
    }

    @FXML
    private void onForzarAvance() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Forzar avance");
        confirmacion.setHeaderText("¿Forzar avance del torneo?");
        confirmacion.setContentText("Se intentará resolver partidos con equipos no definidos.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                forzarAvanceTorneo();
            }
        });
    }

    private void forzarAvanceTorneo() {
        agregarLog("🔧 FORZANDO AVANCE DEL TORNEO");

        int cambios = 0;

        try {
            // Intentar simular varias veces para forzar avance
            for (int i = 0; i < 15; i++) {
                gameManager.jugarPartidoActual();
                cambios++;
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }

            agregarLog("✅ " + cambios + " cambios aplicados");
            actualizarUI();

        } catch (Exception e) {
            agregarLog("❌ Error: " + e.getMessage());
        }
    }

    @FXML
    private void onVolverMenu() {
        if (principal != null) {
            // Preguntar si está seguro
            if (!torneoTerminado) {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Salir del torneo");
                confirmacion.setHeaderText("¿Salir del torneo en curso?");
                confirmacion.setContentText("El torneo se pausará y podrás continuar más tarde.");

                confirmacion.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
                    }
                });
            } else {
                principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
            }
        }
    }

    private void mostrarSinTorneoActivo() {
        lblTorneoNombre.setText("Sin torneo activo");
        lblRondaActual.setText("-");
        if (lblEstadoTorneo != null) {
            lblEstadoTorneo.setText("Selecciona un torneo");
            lblEstadoTorneo.setTextFill(Color.GRAY);
        }

        if (progressTorneo != null) {
            progressTorneo.setProgress(0);
        }

        TreeItem<String> root = new TreeItem<>("🏆 Selecciona un torneo");
        TreeItem<String> mensaje = new TreeItem<>("Ve al menú principal → Torneos");
        root.getChildren().add(mensaje);
        if (treeBracket != null) {
            treeBracket.setRoot(root);
        }

        btnJugarPartido.setDisable(true);
        btnSimularTodo.setDisable(true);
        btnForzarAvance.setDisable(true);

        agregarLog("ℹ️ No hay torneo activo");
        agregarLog("   Ve a 'Seleccionar Torneo' para comenzar uno");
    }

    private void agregarLog(String mensaje) {
        if (logTorneo != null) {
            Platform.runLater(() -> {
                logTorneo.appendText(mensaje + "\n");
                logTorneo.setScrollTop(Double.MAX_VALUE);
            });
        }
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    // Clase wrapper para PartidoRapidoControl
    private class PrincipalWrapper extends Principal {
        @Override
        public void setScreen(String fxml) {
            // Cuando termina un partido, actualizar
            agregarLog("⚽ Partido finalizado");
            actualizarUI();
            panelPartidoContainer.getChildren().clear();

            // Verificar si el torneo terminó
            if (arbolTorneoActual.isTorneoTerminado()) {
                Club campeon = arbolTorneoActual.getCampeon();
                if (campeon != null && campeon.equals(gameManager.getJugador().getClubActual())) {
                    Alert felicitacion = new Alert(Alert.AlertType.INFORMATION);
                    felicitacion.setTitle("¡Felicidades!");
                    felicitacion.setHeaderText("🏆 ¡HAS GANADO EL TORNEO!");
                    felicitacion.setContentText("Recompensa: $" + torneoActual.getRecompensa() + " + 500 XP");
                    felicitacion.showAndWait();
                }
            }
        }

        @Override
        public GameManager getGameManager() {
            return gameManager;
        }
    }
}