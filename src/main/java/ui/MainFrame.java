package ui;
import game.GameManager;
import model.Jugador;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private GameManager gameManager;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        super("Penalty Tournament Evolution");
        initializeGame();
        setupUI();
    }

    private void initializeGame() {
        // Inicializar jugador y game manager
        String nombreJugador = JOptionPane.showInputDialog(this,
                "Ingresa tu nombre:", "Bienvenido", JOptionPane.QUESTION_MESSAGE);

        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            nombreJugador = "Jugador";
        }

        // Aquí deberías tener tu clase Jugador
        this.gameManager = new GameManager(new Jugador(nombreJugador));
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Configurar layout de cartas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear paneles
        mainPanel.add(new PanelTorneo(gameManager), "TORNEO");
         mainPanel.add(new PanelHabilidades(gameManager), "HABILIDADES");
         mainPanel.add(new PanelPartido(gameManager), "PARTIDO");

        add(mainPanel);

        // Barra de menú
        setupMenuBar();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuJuego = new JMenu("Juego");
        JMenuItem itemNuevo = new JMenuItem("Nuevo Juego");
        JMenuItem itemSalir = new JMenuItem("Salir");

        menuJuego.add(itemNuevo);
        menuJuego.addSeparator();
        menuJuego.add(itemSalir);

        JMenu menuVista = new JMenu("Vista");
        JMenuItem itemTorneo = new JMenuItem("Torneo");
        JMenuItem itemHabilidades = new JMenuItem("Habilidades");

        menuVista.add(itemTorneo);
        menuVista.add(itemHabilidades);

        menuBar.add(menuJuego);
        menuBar.add(menuVista);

        setJMenuBar(menuBar);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}