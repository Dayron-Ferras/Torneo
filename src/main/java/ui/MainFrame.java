package ui;

import game.GameManager;
import model.Jugador;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {
    private GameManager gameManager;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        super("⚽ Penalty Tournament Evolution ⚽");
        initializeGame();
        setupUI();
        applyModernStyle();
    }

    private void initializeGame() {
        // Pantalla de bienvenida con estilo moderno
        ImageIcon iconoFutbol = new ImageIcon("futbol_icon.png"); // Puedes agregar un icono después
        String nombreJugador = (String) JOptionPane.showInputDialog(this,
                "<html><div style='text-align: center;'><b>¡Bienvenido al Penalty Tournament Evolution!</b><br>"
                        + "Ingresa tu nombre para comenzar tu carrera:</div></html>",
                "Inicio de Carrera",
                JOptionPane.PLAIN_MESSAGE,
                iconoFutbol,
                null,
                "Futbolista Pro");

        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            nombreJugador = "Futbolista Pro";
        }

        this.gameManager = new GameManager(new Jugador(nombreJugador));
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setIconImage(createFootballIcon());

        // Configurar layout de cartas
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(240, 240, 240));

        // Crear paneles con estilo moderno
        mainPanel.add(createMainMenu(), "MENU");
        mainPanel.add(new PanelTorneo(gameManager), "TORNEO");
        mainPanel.add(new PanelHabilidades(gameManager), "HABILIDADES");
        mainPanel.add(new PanelPartido(gameManager), "PARTIDO");

        add(mainPanel);

        // Barra de menú moderna
        setupMenuBar();
    }

    private JPanel createMainMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBackground(new Color(30, 30, 60));

        // Header con gradiente
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 100, 200),
                        0, getHeight(), new Color(0, 50, 120));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 150));
        headerPanel.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("⚽ PENALTY TOURNAMENT EVOLUTION", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo, BorderLayout.CENTER);

        JLabel subtitulo = new JLabel("Conviértete en la leyenda del fútbol", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitulo.setForeground(new Color(200, 200, 255));
        headerPanel.add(subtitulo, BorderLayout.SOUTH);

        // Panel de botones central
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
        buttonPanel.setOpaque(false);

        String[] opciones = {"🏆 INICIAR TORNEO", "⭐ HABILIDADES", "⚽ PARTIDO RÁPIDO", "🚪 SALIR"};
        Color[] colores = {new Color(0, 150, 0), new Color(255, 165, 0), new Color(220, 0, 0), new Color(100, 100, 100)};

        for (int i = 0; i < opciones.length; i++) {
            JButton button = createModernButton(opciones[i], colores[i]);
            final String panelName = i == 0 ? "TORNEO" : i == 1 ? "HABILIDADES" : i == 2 ? "PARTIDO" : "EXIT";

            button.addActionListener(e -> {
                if (panelName.equals("EXIT")) {
                    System.exit(0);
                } else {
                    cardLayout.show(mainPanel, panelName);
                }
            });
            buttonPanel.add(button);
        }

        // Panel de información del jugador
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.setOpaque(false);
        JLabel infoJugador = new JLabel("Jugador: " + gameManager.getJugador().getNombre() +
                " | Nivel: " + gameManager.getJugador().getNivel() +
                " | Dinero: $" + gameManager.getJugador().getDinero());
        infoJugador.setForeground(Color.WHITE);
        infoJugador.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(infoJugador);

        menuPanel.add(headerPanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        menuPanel.add(infoPanel, BorderLayout.SOUTH);

        return menuPanel;
    }

    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con gradiente
                GradientPaint gradient = new GradientPaint(0, 0, color, 0, getHeight(), color.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Borde
                g2d.setColor(color.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);

                // Texto
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(300, 60));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        return button;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(50, 50, 80));
        menuBar.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));

        // Menú Juego
        JMenu menuJuego = createStyledMenu("Juego");
        JMenuItem itemNuevo = createStyledMenuItem("Nuevo Juego");
        JMenuItem itemMenu = createStyledMenuItem("Menú Principal");
        JMenuItem itemSalir = createStyledMenuItem("Salir");

        menuJuego.add(itemNuevo);
        menuJuego.add(itemMenu);
        menuJuego.addSeparator();
        menuJuego.add(itemSalir);

        // Menú Vista
        JMenu menuVista = createStyledMenu("Vista");
        JMenuItem itemTorneo = createStyledMenuItem("Torneos");
        JMenuItem itemHabilidades = createStyledMenuItem("Habilidades");
        JMenuItem itemPartido = createStyledMenuItem("Partido");

        menuVista.add(itemTorneo);
        menuVista.add(itemHabilidades);
        menuVista.add(itemPartido);

        // Eventos
        itemMenu.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        itemTorneo.addActionListener(e -> cardLayout.show(mainPanel, "TORNEO"));
        itemHabilidades.addActionListener(e -> cardLayout.show(mainPanel, "HABILIDADES"));
        itemPartido.addActionListener(e -> cardLayout.show(mainPanel, "PARTIDO"));
        itemSalir.addActionListener(e -> System.exit(0));

        menuBar.add(menuJuego);
        menuBar.add(menuVista);

        setJMenuBar(menuBar);
    }

    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Color.WHITE);
        menu.setFont(new Font("Arial", Font.BOLD, 14));
        return menu;
    }

    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Arial", Font.PLAIN, 12));
        return item;
    }

    private void applyModernStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image createFootballIcon() {
        // Icono simple de fútbol (puedes reemplazar con una imagen real)
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 255, 255));
        g2d.fillOval(2, 2, size-4, size-4);
        g2d.setColor(new Color(0, 0, 0));
        g2d.drawOval(2, 2, size-4, size-4);

        // Patrón de balón de fútbol simple
        g2d.drawLine(size/2, 4, size/2, size-4);
        g2d.drawLine(4, size/2, size-4, size/2);

        g2d.dispose();
        return image;
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}