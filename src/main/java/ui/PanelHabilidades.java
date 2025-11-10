package ui;

import game.GameManager;
import model.Habilidad;
import tree.ArbolHabilidades;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class PanelHabilidades extends JPanel {
    private GameManager gameManager;
    private JTree arbolHabilidades;
    private JTextArea areaDescripcion;
    private JButton btnComprar;
    private DefaultTreeModel treeModel;
    private Habilidad habilidadSeleccionada;
    private JPanel panelStats;

    public PanelHabilidades(GameManager gameManager) {
        this.gameManager = gameManager;
        setupModernUI();
        configurarEventos(); // MOVER AQUÍ - después de crear todos los componentes
    }

    private void setupModernUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 250));

        // Header con gradiente
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Contenido principal dividido
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(500);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainSplitPane.setLeftComponent(createTreePanel());
        mainSplitPane.setRightComponent(createInfoPanel());

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 60, 90),
                        0, getHeight(), new Color(60, 100, 150));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 100));

        JLabel lblTitulo = new JLabel("⭐ SISTEMA DE HABILIDADES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JLabel lblSubtitulo = new JLabel("Mejora tu jugador y domina el campo", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(200, 230, 255));

        header.add(lblTitulo, BorderLayout.CENTER);
        header.add(lblSubtitulo, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createTreePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createModernBorder());

        JLabel titulo = new JLabel("🌳 ÁRBOL DE HABILIDADES");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(30, 60, 90));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Configurar árbol de habilidades
        configurarArbolHabilidades();

        JScrollPane scrollPane = new JScrollPane(arbolHabilidades);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 248, 250));

        // Panel de información de habilidad
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(createModernBorder());

        JLabel infoTitulo = new JLabel("📊 INFORMACIÓN DE HABILIDAD");
        infoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoTitulo.setForeground(new Color(30, 60, 90));
        infoTitulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        areaDescripcion = new JTextArea();
        areaDescripcion.setEditable(false);
        areaDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaDescripcion.setLineWrap(true);
        areaDescripcion.setWrapStyleWord(true);
        areaDescripcion.setText("Selecciona una habilidad para ver información detallada...");
        areaDescripcion.setBackground(new Color(250, 252, 255));
        areaDescripcion.setForeground(new Color(60, 60, 60));
        areaDescripcion.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollInfo = new JScrollPane(areaDescripcion);
        scrollInfo.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));

        // Botón de compra moderno - CREAR EL BOTÓN AQUÍ
        btnComprar = createBuyButton();
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        buttonPanel.add(btnComprar);

        infoPanel.add(infoTitulo, BorderLayout.NORTH);
        infoPanel.add(scrollInfo, BorderLayout.CENTER);
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel de estadísticas
        panelStats = createStatsPanel();

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, panelStats);
        rightSplit.setDividerLocation(350);
        rightSplit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel.add(rightSplit, BorderLayout.CENTER);

        return panel;
    }

    private JButton createBuyButton() {
        JButton button = new JButton("🛒 COMPRAR HABILIDAD") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color colorBase = isEnabled() ? new Color(46, 204, 113) : new Color(150, 150, 150);

                if (getModel().isPressed()) {
                    colorBase = colorBase.darker();
                } else if (getModel().isRollover() && isEnabled()) {
                    colorBase = colorBase.brighter();
                }

                GradientPaint gradient = new GradientPaint(0, 0, colorBase, 0, getHeight(), colorBase.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2d.setColor(colorBase.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setEnabled(false);

        return button;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createModernBorder());

        JLabel titulo = new JLabel("👤 ESTADÍSTICAS DEL JUGADOR");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(30, 60, 90));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel statsGrid = new JPanel(new GridLayout(0, 2, 10, 8));
        statsGrid.setBackground(Color.WHITE);
        statsGrid.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        actualizarEstadisticas(statsGrid);

        JScrollPane scrollStats = new JScrollPane(statsGrid);
        scrollStats.setBorder(null);
        scrollStats.getViewport().setBackground(Color.WHITE);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scrollStats, BorderLayout.CENTER);

        return panel;
    }

    private void configurarArbolHabilidades() {
        // Crear modelo del árbol
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Habilidades Principales");
        treeModel = new DefaultTreeModel(raiz);

        // Construir árbol desde GameManager
        ArbolHabilidades arbolHabs = gameManager.getArbolHabilidades();
        if (arbolHabs != null) {
            construirArbolSwing(raiz, (BinaryTreeNode<Habilidad>) arbolHabs.getArbol().getRoot());
        }

        // Crear JTree con estilo personalizado
        arbolHabilidades = new JTree(treeModel);
        arbolHabilidades.setCellRenderer(new HabilidadTreeCellRenderer());
        arbolHabilidades.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbolHabilidades.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        arbolHabilidades.setBackground(new Color(250, 252, 255));

        // Expandir todos los nodos
        for (int i = 0; i < arbolHabilidades.getRowCount(); i++) {
            arbolHabilidades.expandRow(i);
        }

        // QUITAR configurarEventos() de aquí - se mueve al constructor
    }

    private void construirArbolSwing(DefaultMutableTreeNode padreSwing, BinaryTreeNode<Habilidad> nodoArbol) {
        if (nodoArbol == null) return;

        Habilidad habilidad = nodoArbol.getInfo();
        DefaultMutableTreeNode nodoSwing = new DefaultMutableTreeNode(habilidad);
        padreSwing.add(nodoSwing);

        // Recorrer hijos
        if (nodoArbol.getLeft() != null) {
            BinaryTreeNode<Habilidad> hijoActual = nodoArbol.getLeft();
            while (hijoActual != null) {
                construirArbolSwing(nodoSwing, hijoActual);
                hijoActual = hijoActual.getRight();
            }
        }
    }

    private void configurarEventos() {
        // Evento de selección en el árbol
        arbolHabilidades.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)
                    arbolHabilidades.getLastSelectedPathComponent();

            if (selectedNode == null) return;

            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof Habilidad) {
                habilidadSeleccionada = (Habilidad) userObject;
                mostrarInformacionHabilidad(habilidadSeleccionada);
            }
        });

        // Evento del botón comprar - AHORA btnComprar NO ES NULL
        btnComprar.addActionListener(e -> {
            if (habilidadSeleccionada != null) {
                comprarHabilidad();
            }
        });
    }

    private void actualizarEstadisticas(JPanel statsGrid) {
        statsGrid.removeAll();

        Color colorLabel = new Color(80, 80, 80);
        Color colorValue = new Color(30, 60, 90);
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 14);
        Font fontValue = new Font("Segoe UI", Font.BOLD, 14);

        // Dinero
        JLabel lblDinero = new JLabel("💰 Dinero:");
        lblDinero.setFont(fontLabel);
        lblDinero.setForeground(colorLabel);
        JLabel valDinero = new JLabel("$" + gameManager.getJugador().getDinero());
        valDinero.setFont(fontValue);
        valDinero.setForeground(new Color(46, 204, 113));

        // Experiencia
        JLabel lblExp = new JLabel("⭐ Experiencia:");
        lblExp.setFont(fontLabel);
        lblExp.setForeground(colorLabel);
        JLabel valExp = new JLabel(gameManager.getJugador().getExperiencia() + " XP");
        valExp.setFont(fontValue);
        valExp.setForeground(new Color(155, 89, 182));

        // Nivel
        JLabel lblNivel = new JLabel("🎯 Nivel:");
        lblNivel.setFont(fontLabel);
        lblNivel.setForeground(colorLabel);
        JLabel valNivel = new JLabel(String.valueOf(gameManager.getJugador().getNivel()));
        valNivel.setFont(fontValue);
        valNivel.setForeground(new Color(52, 152, 219));

        // Precisión
        JLabel lblPrecision = new JLabel("🎯 Precisión:");
        lblPrecision.setFont(fontLabel);
        lblPrecision.setForeground(colorLabel);
        JLabel valPrecision = createStatBar(gameManager.getJugador().getPrecision(), 100);

        // Potencia
        JLabel lblPotencia = new JLabel("💪 Potencia:");
        lblPotencia.setFont(fontLabel);
        lblPotencia.setForeground(colorLabel);
        JLabel valPotencia = createStatBar(gameManager.getJugador().getPotencia(), 100);

        // Estrategia
        JLabel lblEstrategia = new JLabel("🧠 Estrategia:");
        lblEstrategia.setFont(fontLabel);
        lblEstrategia.setForeground(colorLabel);
        JLabel valEstrategia = createStatBar(gameManager.getJugador().getEstrategia(), 100);

        // Agregar componentes
        statsGrid.add(lblDinero); statsGrid.add(valDinero);
        statsGrid.add(lblExp); statsGrid.add(valExp);
        statsGrid.add(lblNivel); statsGrid.add(valNivel);
        statsGrid.add(lblPrecision); statsGrid.add(valPrecision);
        statsGrid.add(lblPotencia); statsGrid.add(valPotencia);
        statsGrid.add(lblEstrategia); statsGrid.add(valEstrategia);

        statsGrid.revalidate();
        statsGrid.repaint();
    }

    private JLabel createStatBar(int valor, int max) {
        return new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = 120;
                int height = 20;
                int arc = 10;

                // Fondo de la barra
                g2d.setColor(new Color(230, 230, 230));
                g2d.fillRoundRect(0, 0, width, height, arc, arc);

                // Barra de progreso
                int progressWidth = (int) ((double) valor / max * width);
                Color progressColor = getColorForValue(valor, max);
                g2d.setColor(progressColor);
                g2d.fillRoundRect(0, 0, progressWidth, height, arc, arc);

                // Borde
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, width, height, arc, arc);

                // Texto
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String text = valor + "/" + max;
                FontMetrics fm = g2d.getFontMetrics();
                int x = (width - fm.stringWidth(text)) / 2;
                int y = (height - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, x, y);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 20);
            }
        };
    }

    private Color getColorForValue(int valor, int max) {
        double percentage = (double) valor / max;
        if (percentage < 0.3) return new Color(231, 76, 60);
        if (percentage < 0.6) return new Color(243, 156, 18);
        if (percentage < 0.8) return new Color(46, 204, 113);
        return new Color(39, 174, 96);
    }

    private void mostrarInformacionHabilidad(Habilidad habilidad) {
        StringBuilder info = new StringBuilder();

        String estado = habilidad.isDesbloqueada() ? "✅ DESBLOQUEADA" : "🔒 BLOQUEADA";
        info.append("🎯 ").append(habilidad.getNombre()).append("\n");
        info.append(estado).append("\n\n");

        info.append("📝 ").append(habilidad.getDescripcion()).append("\n\n");

        info.append("📊 ESTADÍSTICAS:\n");
        info.append("• Tipo: ").append(getTipoEmoji(habilidad.getTipo())).append(" ").append(habilidad.getTipo().toUpperCase()).append("\n");
        info.append("• Mejora: +").append(habilidad.getValorMejora()).append(" puntos\n\n");

        info.append("💳 COSTOS:\n");
        info.append("• 💰 Dinero: $").append(habilidad.getCostoDinero()).append("\n");
        info.append("• ⭐ Experiencia: ").append(habilidad.getCostoExperiencia()).append(" XP\n\n");

        if (habilidad.isDesbloqueada()) {
            info.append("🎉 ¡Ya posees esta habilidad!");
            btnComprar.setEnabled(false);
            btnComprar.setText("✅ ADQUIRIDA");
        } else {
            boolean puedeComprar = habilidad.puedeComprar(gameManager.getJugador());
            if (puedeComprar) {
                info.append("✨ ¡Puedes adquirir esta habilidad!");
                btnComprar.setEnabled(true);
                btnComprar.setText("🛒 COMPRAR HABILIDAD");
            } else {
                info.append("❌ No cumples con los requisitos necesarios");
                btnComprar.setEnabled(false);
                btnComprar.setText("FONDOS INSUFICIENTES");
            }
        }

        areaDescripcion.setText(info.toString());
    }

    private String getTipoEmoji(String tipo) {
        switch (tipo) {
            case "precision": return "🎯";
            case "potencia": return "💪";
            case "estrategia": return "🧠";
            default: return "⭐";
        }
    }

    private void comprarHabilidad() {
        if (habilidadSeleccionada != null && !habilidadSeleccionada.isDesbloqueada()) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "<html><b>¿Confirmar compra?</b><br>" +
                            "Habilidad: " + habilidadSeleccionada.getNombre() + "<br>" +
                            "Costo: $" + habilidadSeleccionada.getCostoDinero() + " + " +
                            habilidadSeleccionada.getCostoExperiencia() + " XP</html>",
                    "🛒 Confirmar Compra",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                habilidadSeleccionada.comprar(gameManager.getJugador());

                actualizarArbolHabilidades();
                actualizarEstadisticas((JPanel) panelStats.getComponent(0));
                mostrarInformacionHabilidad(habilidadSeleccionada);

                JOptionPane.showMessageDialog(this,
                        "<html><b>¡Habilidad Adquirida!</b><br>" +
                                "Has aprendido: " + habilidadSeleccionada.getNombre() + "</html>",
                        "🎉 ¡Éxito!",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void actualizarArbolHabilidades() {
        DefaultMutableTreeNode raiz = (DefaultMutableTreeNode) treeModel.getRoot();
        raiz.removeAllChildren();

        ArbolHabilidades arbolHabs = gameManager.getArbolHabilidades();
        if (arbolHabs != null) {
            construirArbolSwing(raiz, (BinaryTreeNode<Habilidad>) arbolHabs.getArbol().getRoot());
        }

        treeModel.reload();

        for (int i = 0; i < arbolHabilidades.getRowCount(); i++) {
            arbolHabilidades.expandRow(i);
        }
    }

    private Border createModernBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    private class HabilidadTreeCellRenderer extends DefaultTreeCellRenderer {
        private Color backgroundSelection = new Color(220, 240, 255);
        private Color backgroundNonSelection = Color.WHITE;
        private Color borderColor = new Color(200, 220, 240);

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();

            if (userObject instanceof Habilidad) {
                Habilidad habilidad = (Habilidad) userObject;
                String texto = habilidad.getNombre();
                String emoji = getTipoEmoji(habilidad.getTipo());

                if (habilidad.isDesbloqueada()) {
                    setText("✅ " + emoji + " " + texto);
                    setForeground(new Color(39, 174, 96));
                } else {
                    setText("🔒 " + emoji + " " + texto);
                    setForeground(new Color(120, 120, 120));
                }

                setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                setText("🌳 " + value.toString());
                setForeground(new Color(30, 60, 90));
                setFont(new Font("Segoe UI", Font.BOLD, 14));
            }

            if (selected) {
                setBackground(backgroundSelection);
                setBorder(BorderFactory.createLineBorder(borderColor, 1));
            } else {
                setBackground(backgroundNonSelection);
                setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }

            setOpaque(true);
            return this;
        }
    }

    public void refrescarPanel() {
        actualizarArbolHabilidades();
        if (panelStats != null) {
            actualizarEstadisticas((JPanel) panelStats.getComponent(0));
        }
    }
}