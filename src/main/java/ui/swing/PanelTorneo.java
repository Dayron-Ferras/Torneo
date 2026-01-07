package ui.swing;

import game.GameManager;
import model.Torneo;

import javax.swing.*;
import java.awt.*;

public class PanelTorneo extends JPanel {
    private GameManager gameManager;
    private JList<Torneo> listaTorneos;
    private JButton btnIniciarTorneo;
    private JTextArea areaInfo;

    public PanelTorneo(GameManager gameManager) {
        this.gameManager = gameManager;
        setupModernUI();
    }

    private void setupModernUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));

        // Header moderno
        JPanel headerPanel = createHeaderPanel("🏆 SELECCIÓN DE TORNEOS",
                "Elige tu competencia y demuestra tu habilidad");

        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        splitPane.setLeftComponent(createTorneosPanel());
        splitPane.setRightComponent(createInfoPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel(String titulo, String subtitulo) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 80, 160),
                        0, getHeight(), new Color(0, 120, 215));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 80));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblSubtitulo = new JLabel(subtitulo, SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(200, 230, 255));

        header.add(lblTitulo, BorderLayout.CENTER);
        header.add(lblSubtitulo, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createTorneosPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("TORNEOS DISPONIBLES");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 80, 160));

        // Lista moderna de torneos
        listaTorneos = new JList<>(gameManager.getTorneosDisponibles().toArray(new Torneo[0]));
        listaTorneos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTorneos.setFont(new Font("Arial", Font.PLAIN, 14));
        listaTorneos.setCellRenderer(new TorneoCellRenderer());

        JScrollPane scrollPane = new JScrollPane(listaTorneos);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));

        // Botón moderno
        btnIniciarTorneo = createModernButton("🚀 INICIAR TORNEO SELECCIONADO",
                new Color(0, 150, 0), new Dimension(300, 45));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnIniciarTorneo, BorderLayout.SOUTH);

        // Listeners
        listaTorneos.addListSelectionListener(e -> mostrarInfoTorneo());
        btnIniciarTorneo.addActionListener(e -> iniciarTorneoSeleccionado());

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("INFORMACIÓN DEL TORNEO");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 80, 160));

        areaInfo = new JTextArea();
        areaInfo.setEditable(false);
        areaInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        areaInfo.setLineWrap(true);
        areaInfo.setWrapStyleWord(true);
        areaInfo.setText("Selecciona un torneo para ver información detallada...");
        areaInfo.setBackground(new Color(250, 250, 255));
        areaInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(areaInfo);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createModernButton(String text, Color color, Dimension size) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(color.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(size);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        return button;
    }

    // Resto de los métodos se mantienen igual...
    private void mostrarInfoTorneo() {
        Torneo torneo = listaTorneos.getSelectedValue();
        if (torneo != null) {
            StringBuilder info = new StringBuilder();
            info.append("🏆 ").append(torneo.getNombre()).append("\n\n");
            info.append("📊 INFORMACIÓN:\n");
            info.append("• Nivel Requerido: ").append(torneo.getNivelRequerido()).append("\n");
            info.append("• Recompensa: $").append(torneo.getRecompensa()).append("\n");
            info.append("• Estado: ").append(torneo.isCompletado() ? "✅ Completado" : "🟡 Disponible").append("\n\n");

            info.append("👥 CLUBS PARTICIPANTES:\n");
            for (int i = 0; i < torneo.getClubsParticipantes().size(); i++) {
                info.append("• ").append(torneo.getClubsParticipantes().get(i).getNombre()).append("\n");
            }

            areaInfo.setText(info.toString());
        }
    }

    private void iniciarTorneoSeleccionado() {
        Torneo torneo = listaTorneos.getSelectedValue();
        if (torneo != null) {
            if (gameManager.iniciarTorneo(torneo.getNombre())) {
                JOptionPane.showMessageDialog(this,
                        "<html><b>¡Torneo Iniciado!</b><br>Has comenzado el " + torneo.getNombre() + "</html>",
                        "🚀 Comienza la Competencia",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "<html><b>Requisitos Insuficientes</b><br>"
                                + "Necesitas nivel " + torneo.getNivelRequerido() + " para este torneo</html>",
                        "❌ Acceso Denegado",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Renderer personalizado para la lista de torneos
    private class TorneoCellRenderer extends JLabel implements ListCellRenderer<Torneo> {
        public TorneoCellRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Torneo> list, Torneo torneo,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            setText("🏆 " + torneo.getNombre());
            setFont(new Font("Arial", Font.BOLD, 14));

            if (isSelected) {
                setBackground(new Color(220, 240, 255));
                setForeground(new Color(0, 80, 160));
            } else {
                setBackground(index % 2 == 0 ? new Color(250, 250, 255) : new Color(245, 245, 250));
                setForeground(torneo.isCompletado() ? new Color(100, 100, 100) : new Color(50, 50, 50));
            }

            return this;
        }
    }
}