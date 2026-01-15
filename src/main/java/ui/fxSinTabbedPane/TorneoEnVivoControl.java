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

    private int eliminadoCount = 0;
    private String ultimoMensaje = "";


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
                    " (Nivel " + club.getNivel() + ")"); // ¡Ahora funciona!
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
        Platform.runLater(() -> {
            if (torneoTerminado) {
                btnJugarPartido.setText("🏆 Torneo Completado");
                btnJugarPartido.setDisable(true);
                btnJugarPartido.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
                btnSimularTodo.setDisable(true);
                btnForzarAvance.setDisable(true);
                return;
            }

            // Verificar si el jugador fue eliminado
            boolean jugadorEliminado = false;
            try {
                jugadorEliminado = gameManager.isJugadorEliminado();
            } catch (Exception e) {
                jugadorEliminado = true;
            }

            if (jugadorEliminado) {
                btnJugarPartido.setText("❌ ELIMINADO");
                btnJugarPartido.setDisable(true);
                btnJugarPartido.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
                // ¡IMPORTANTE! Remover cualquier evento que pueda estar causando el bucle
                btnJugarPartido.setOnAction(null);
            } else {
                Partido partidoActual = arbolTorneoActual.getPartidoActual(gameManager.getJugador());

                if (partidoActual != null && !partidoActual.isJugado()) {
                    boolean equiposDefinidos = verificarEquiposPartido(partidoActual);

                    if (equiposDefinidos) {
                        btnJugarPartido.setText("🎮 Jugar Partido");
                        btnJugarPartido.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        btnJugarPartido.setDisable(false);
                        // Restaurar el evento solo si no está eliminado
                        btnJugarPartido.setOnAction(e -> onJugarPartido());
                    } else {
                        btnJugarPartido.setText("⚙️ Simular Previos");
                        btnJugarPartido.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
                        btnJugarPartido.setDisable(false);
                        btnJugarPartido.setOnAction(e -> {
                            simularHastaPartidoActual();
                            actualizarUI();
                        });
                    }
                } else {
                    btnJugarPartido.setText("⏳ Esperando...");
                    btnJugarPartido.setDisable(true);
                    btnJugarPartido.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
                    btnJugarPartido.setOnAction(null);
                }
            }

            // Siempre habilitar botones de simulación
            btnSimularTodo.setDisable(false);
            btnForzarAvance.setDisable(false);
        });
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
        return !local.getNombre().contains("Por Definir") && !visitante.getNombre().contains("Por Definir");
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
        // Protección contra múltiples clics rápidos
        btnJugarPartido.setDisable(true);

        try {
            if (gameManager == null || arbolTorneoActual == null) {
                mostrarAlerta("No hay torneo activo", Alert.AlertType.WARNING);
                return;
            }

            // Verificar ANTES de cualquier acción
            if (gameManager.isJugadorEliminado()) {
                mostrarAlerta("Ya has sido eliminado", Alert.AlertType.INFORMATION);
                return;
            }

            Partido partidoActual = arbolTorneoActual.getPartidoActual(gameManager.getJugador());

            if (partidoActual == null) {
                mostrarAlerta("No hay partido disponible", Alert.AlertType.INFORMATION);
                return;
            }

            if (partidoActual.isJugado()) {
                mostrarAlerta("Este partido ya fue jugado", Alert.AlertType.INFORMATION);
                return;
            }

            // Verificar equipos
            if (!verificarEquiposPartido(partidoActual)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Equipos no definidos");
                alert.setHeaderText("Los equipos no están completamente definidos");
                alert.setContentText("¿Simular partidos previos?");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // SIMULAR SIN LLAMAR A jugarPartidoActual directamente
                        simularPartidosPendientesNoJugador();
                        // Después de simular, intentar cargar el partido nuevamente
                        Partido nuevoPartido = arbolTorneoActual.getPartidoActual(gameManager.getJugador());
                        if (nuevoPartido != null && verificarEquiposPartido(nuevoPartido)) {
                            cargarInterfazPartido(nuevoPartido);
                        }
                    }
                });
                return;
            }

            // Cargar la interfaz del partido de penaltis
            agregarLog("🎮 Preparando partido de penaltis...");
            cargarInterfazPartido(partidoActual);

        } finally {
            // Re-habilitar el botón después de un tiempo
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Evitar clics rápidos
                    Platform.runLater(() -> {
                        if (!torneoTerminado && !gameManager.isJugadorEliminado()) {
                            btnJugarPartido.setDisable(false);
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    // Método para simular solo partidos NO del jugador
    private void simularPartidosPendientesNoJugador() {
        agregarLog("🔧 Simulando partidos previos...");

        Club clubJugador = gameManager.getJugador().getClubActual();
        int simulados = 0;

        while (simulados < 5) {
            Partido proximo = arbolTorneoActual.getProximoPartidoJugable();
            if (proximo == null) break;

            // Solo simular si el jugador NO está en este partido
            boolean jugadorEnPartido =
                    (proximo.getEquipoLocal() != null && proximo.getEquipoLocal().equals(clubJugador)) ||
                            (proximo.getEquipoVisitante() != null && proximo.getEquipoVisitante().equals(clubJugador));

            if (!jugadorEnPartido && !proximo.isJugado()) {
                proximo.simularPartido();
                simulados++;
                agregarLog("   📋 Partido " + simulados + " simulado");
                arbolTorneoActual.actualizarLlave();
            } else {
                break;
            }
        }

        agregarLog("✅ " + simulados + " partidos simulados");
        actualizarUI();
    }

    // Método auxiliar para simular solo los partidos necesarios
    private void simularPartidosPendientes() {
        agregarLog("🔧 SIMULANDO PARTIDOS PENDIENTES");

        int simulados = 0;
        int maxSimulaciones = 5;

        while (simulados < maxSimulaciones) {
            Partido proximoPartido = arbolTorneoActual.getProximoPartidoJugable();
            if (proximoPartido == null) break;

            proximoPartido.simularPartido();
            simulados++;
            agregarLog("   📋 Partido " + simulados + " simulado: " + proximoPartido);

            // Actualizar árbol
            arbolTorneoActual.actualizarLlave();

            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }

        agregarLog("✅ " + simulados + " partidos simulados");
        actualizarUI();
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

        System.out.println("Intentando cargar FXML desde: " + getClass().getResource("/uiSinTabbedPane/partidoRapido.fxml"));
        System.out.println("Partido: " + partido);
        System.out.println("GameManager: " + gameManager);

        try {
            // Obtener el partido actual del jugador
            Partido partidoActual = arbolTorneoActual.getPartidoActual(gameManager.getJugador());

            if (partidoActual == null) {
                agregarLog("❌ No se pudo encontrar el partido del jugador");
                return;
            }

            // IMPORTANTE: Usar FXMLLoader con importación correcta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uiSinTabbedPane/partidoRapido.fxml"));
            BorderPane partidoView = loader.load();

            PartidoRapidoControl partidoController = loader.getController();
            partidoController.setGameManager(gameManager);
            partidoController.setMainApp(principal); // Pasar el principal real, no un wrapper
            partidoController.refreshPanel();

            // Limpiar el contenedor y agregar la vista del partido
            panelPartidoContainer.getChildren().clear();
            panelPartidoContainer.getChildren().add(partidoView);

            // Mostrar información del partido
            agregarLog("🎮 Partido cargado: " + partidoActual.getEquipoLocal().getNombre() +
                    " vs " + partidoActual.getEquipoVisitante().getNombre());
            agregarLog("   Haz clic en 'Iniciar' para comenzar la tanda de penaltis");

            // Opcional: Agregar botones de control simplificados
            HBox controlBox = new HBox(10);
            controlBox.setAlignment(Pos.CENTER);
            controlBox.setStyle("-fx-padding: 10px; -fx-background-color: #f0f0f0;");

            Button btnVolver = new Button("← Volver al torneo");
            btnVolver.setOnAction(e -> {
                panelPartidoContainer.getChildren().clear();
                agregarLog("← Regresando al torneo");
                actualizarUI();
            });

            controlBox.getChildren().add(btnVolver);
            panelPartidoContainer.getChildren().add(controlBox);

        } catch (Exception e) {
            e.printStackTrace();
            agregarLog("❌ Error al cargar la interfaz del partido: " + e.getMessage());

            // Mostrar mensaje de error detallado
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar el partido");
            alert.setContentText("Detalles: " + e.getMessage());
            alert.showAndWait();
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

    // En TorneoEnVivoControl, modifica el método onForzarAvance():
    @FXML
    private void onForzarAvance() {
        if (gameManager == null || arbolTorneoActual == null) {
            mostrarAlerta("No hay torneo activo", Alert.AlertType.WARNING);
            return;
        }

        // Verificar estado del jugador
        boolean jugadorEliminado = false;
        Partido partidoJugador = arbolTorneoActual.getPartidoActual(gameManager.getJugador());
        jugadorEliminado = (partidoJugador == null && !arbolTorneoActual.isTorneoTerminado());

        if (jugadorEliminado) {
            // Jugador eliminado - simular solo partidos de otros equipos
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Simular resto del torneo");
            confirmacion.setHeaderText("¿Simular los partidos restantes?");
            confirmacion.setContentText("Has sido eliminado. ¿Quieres ver cómo termina el torneo?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    agregarLog("⏩ Simulando partidos restantes...");

                    try {
                        // Intentar usar el nuevo método
                        java.lang.reflect.Method method = gameManager.getClass()
                                .getMethod("simularPartidosOtrosEquipos");
                        method.invoke(gameManager);
                    } catch (Exception e) {
                        // Si no existe, usar avanzarTorneo
                        gameManager.avanzarTorneo();
                    }

                    actualizarUI();

                    // Verificar si el torneo terminó
                    if (arbolTorneoActual.isTorneoTerminado()) {
                        Club campeon = arbolTorneoActual.getCampeon();
                        if (campeon != null) {
                            agregarLog("\n🏆 CAMPEÓN: " + campeon.getNombre());
                        }
                    }
                }
            });
        } else {
            // Jugador aún en el torneo
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Forzar avance");
            confirmacion.setHeaderText("¿Forzar avance del torneo?");
            confirmacion.setContentText("Se intentará avanzar en el torneo.");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    forzarAvanceTorneo();
                }
            });
        }
    }

    // Modifica el método forzarAvanceTorneo():
    private void forzarAvanceTorneo() {
        agregarLog("🔧 FORZANDO AVANCE DEL TORNEO");

        try {
            // Usar el método que tiene límites para evitar bucles
            gameManager.avanzarTorneo();
            agregarLog("✅ Avance completado");
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
                // Agregar el mensaje al log
                logTorneo.appendText(mensaje + "\n");
                logTorneo.setScrollTop(Double.MAX_VALUE);

                // Detectar mensajes repetidos de "eliminado"
                if (mensaje.toLowerCase().contains("eliminado")) {
                    // Si es el mismo mensaje que el anterior, incrementar contador
                    if (mensaje.equals(ultimoMensaje)) {
                        eliminadoCount++;
                    } else {
                        eliminadoCount = 1;
                        ultimoMensaje = mensaje;
                    }

                    // Si hay más de 3 mensajes idénticos seguidos, hay un bucle
                    if (eliminadoCount > 3) {
                        logTorneo.appendText("⚠️ ⚠️ ⚠️ POSIBLE BUCLE DETECTADO!\n");
                        logTorneo.appendText("Mensaje repetido " + eliminadoCount + " veces: '" + mensaje + "'\n");
                        logTorneo.appendText("Ejecutando diagnóstico...\n");

                        // Ejecutar diagnóstico
                        if (gameManager != null) {
                            try {
                                // Llamar al método de diagnóstico si existe
                                java.lang.reflect.Method method = gameManager.getClass()
                                        .getMethod("diagnosticarProblema");
                                method.invoke(gameManager);
                            } catch (Exception e) {
                                logTorneo.appendText("No se pudo ejecutar diagnóstico: " + e.getMessage() + "\n");

                                // Mostrar stack trace en consola
                                e.printStackTrace();

                                // Alternativa: hacer un dump de threads
                                logTorneo.appendText("Realizando dump de threads...\n");
                                Thread.dumpStack();
                            }
                        }

                        logTorneo.appendText("Intenta:\n");
                        logTorneo.appendText("1. Salir y volver al menú principal\n");
                        logTorneo.appendText("2. Seleccionar otro torneo\n");
                        logTorneo.appendText("3. Revisar consola para más detalles\n");
                    }
                } else {
                    // Resetear contador si no es mensaje de eliminación
                    eliminadoCount = 0;
                    ultimoMensaje = "";
                }
            });
        } else {
            // Si logTorneo es null, imprimir en consola
            System.out.println("[LOG UI] " + mensaje);
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