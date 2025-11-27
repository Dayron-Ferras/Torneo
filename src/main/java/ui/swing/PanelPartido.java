package ui.swing;

import game.GameManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelPartido extends JPanel {
    private GameManager gameManager;
    private JLabel lblMarcador;
    private JLabel lblEstadoPartido;
    private JButton btnIzquierda, btnCentro, btnDerecha;
    private JButton btnIniciarPartido;
    private JTextArea areaLog;
    private JProgressBar progresoPartido;
    private JPanel panelCancha;
    private JLabel lblPortero;
    private int penaltisRealizados = 0;
    private int golesJugador = 0;
    private int golesRival = 0;
    private Timer animacionPortero;

    public PanelPartido(GameManager gameManager) {
        this.gameManager = gameManager;
        setupModernUI();
    }

    private void setupModernUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));

        // Header con gradiente
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Panel principal dividido
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(600);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainSplitPane.setLeftComponent(createCanchaPanel());
        mainSplitPane.setRightComponent(createControlPanel());

        add(mainSplitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 100, 0),
                        0, getHeight(), new Color(0, 150, 50));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(1000, 100));

        // Título principal
        JLabel lblTitulo = new JLabel("⚽ PARTIDO DE PENALTIS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        // Estado del partido
        lblEstadoPartido = new JLabel("Preparado para comenzar...", SwingConstants.CENTER);
        lblEstadoPartido.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEstadoPartido.setForeground(new Color(200, 255, 200));

        header.add(lblTitulo, BorderLayout.CENTER);
        header.add(lblEstadoPartido, BorderLayout.SOUTH);

        return header;
    }

    private JPanel createCanchaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 245, 250));
        panel.setBorder(createModernBorder());

        JLabel titulo = new JLabel("🏟️ CANCHA DE JUEGO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Panel de la cancha con gráficos
        panelCancha = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Césped
                GradientPaint grassGradient = new GradientPaint(0, 0, new Color(100, 200, 100),
                        0, getHeight(), new Color(60, 160, 60));
                g2d.setPaint(grassGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Líneas de la cancha
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(20, 20, getWidth()-40, getHeight()-40);

                // Círculo central
                g2d.drawOval(getWidth()/2-50, getHeight()/2-50, 100, 100);

                // Área pequeña
                g2d.drawRect(20, getHeight()/2-80, 100, 160);

                // Área grande
                g2d.drawRect(20, getHeight()/2-150, 220, 300);

                // Punto de penal
                g2d.fillOval(150, getHeight()/2-5, 10, 10);
            }
        };
        panelCancha.setPreferredSize(new Dimension(550, 400));

        // Portero (imagen representativa)
        lblPortero = new JLabel("🧤", SwingConstants.CENTER);
        lblPortero.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        lblPortero.setBounds(300, 150, 60, 60);
        panelCancha.setLayout(null);
        panelCancha.add(lblPortero);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panelCancha, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 248, 250));

        // Panel de marcador
        JPanel marcadorPanel = createMarcadorPanel();

        // Panel de controles de tiro
        JPanel controlesPanel = createControlesPanel();

        // Panel de log del partido
        JPanel logPanel = createLogPanel();

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, controlesPanel, logPanel);
        rightSplit.setDividerLocation(250);
        rightSplit.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        panel.add(marcadorPanel, BorderLayout.NORTH);
        panel.add(rightSplit, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMarcadorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createModernBorder());

        JLabel titulo = new JLabel("📊 MARCADOR EN TIEMPO REAL");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Marcador principal
        JPanel marcadorContainer = new JPanel(new FlowLayout());
        marcadorContainer.setBackground(Color.WHITE);

        JLabel lblJugador = new JLabel("JUGADOR");
        lblJugador.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblJugador.setForeground(new Color(0, 100, 200));

        lblMarcador = new JLabel("0 - 0");
        lblMarcador.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblMarcador.setForeground(new Color(200, 0, 0));

        JLabel lblRival = new JLabel("RIVAL");
        lblRival.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRival.setForeground(new Color(200, 0, 0));

        marcadorContainer.add(lblJugador);
        marcadorContainer.add(createMarcadorCircle());
        marcadorContainer.add(lblRival);

        // Botón iniciar partido
        btnIniciarPartido = createActionButton("🚀 INICIAR PARTIDO", new Color(46, 204, 113));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(marcadorContainer, BorderLayout.CENTER);
        panel.add(btnIniciarPartido, BorderLayout.SOUTH);

        // Evento del botón iniciar
        btnIniciarPartido.addActionListener(e -> iniciarPartido());

        return panel;
    }

    private JPanel createMarcadorCircle() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo del marcador
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);

                // Texto del marcador
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String texto = golesJugador + " - " + golesRival;
                int x = (getWidth() - fm.stringWidth(texto)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(texto, x, y);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 60);
            }
        };
    }

    private JPanel createControlesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createModernBorder());

        JLabel titulo = new JLabel("🎯 CONTROLES DE TIRO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Panel de botones de dirección
        JPanel botonesPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        botonesPanel.setBackground(Color.WHITE);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        btnIzquierda = createDirectionButton("⬅", "IZQUIERDA", new Color(231, 76, 60));
        btnCentro = createDirectionButton("⬆", "CENTRO", new Color(243, 156, 18));
        btnDerecha = createDirectionButton("➡", "DERECHA", new Color(46, 204, 113));

        // Deshabilitar botones hasta que empiece el partido
        btnIzquierda.setEnabled(false);
        btnCentro.setEnabled(false);
        btnDerecha.setEnabled(false);

        botonesPanel.add(btnIzquierda);
        botonesPanel.add(btnCentro);
        botonesPanel.add(btnDerecha);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(botonesPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createDirectionButton(String emoji, String texto, Color color) {
        JButton button = new JButton("<html><center>" + emoji + "<br>" + texto + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color colorBase = isEnabled() ? color : new Color(150, 150, 150);

                if (getModel().isPressed()) {
                    colorBase = colorBase.darker();
                } else if (getModel().isRollover() && isEnabled()) {
                    colorBase = colorBase.brighter();
                }

                // Fondo con gradiente
                GradientPaint gradient = new GradientPaint(0, 0, colorBase, 0, getHeight(), colorBase.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde
                g2d.setColor(colorBase.darker().darker());
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);

                // Sombra
                if (isEnabled()) {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(3, 3, getWidth()-4, getHeight()-4, 20, 20);
                }
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 80));

        // Eventos
        button.addActionListener(e -> ejecutarPenalti(texto.toUpperCase()));

        return button;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createModernBorder());

        JLabel titulo = new JLabel("📝 DESARROLLO DEL PARTIDO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(0, 100, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);
        areaLog.setBackground(new Color(250, 252, 255));
        areaLog.setForeground(new Color(60, 60, 60));
        areaLog.setText("🏟️ BIENVENIDO AL PARTIDO DE PENALTIS\n\n");
        areaLog.append("• Presiona 'INICIAR PARTIDO' para comenzar\n");
        areaLog.append("• Cada partido consta de 5 penaltis\n");
        areaLog.append("• Elige dirección: IZQUIERDA, CENTRO o DERECHA\n");
        areaLog.append("• ¡Demuestra tu habilidad!\n\n");

        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240)));
        scrollLog.getVerticalScrollBar().setUnitIncrement(16);

        // Barra de progreso
        progresoPartido = new JProgressBar(0, 5);
        progresoPartido.setValue(0);
        progresoPartido.setString("Penaltis: 0/5");
        progresoPartido.setStringPainted(true);
        progresoPartido.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progresoPartido.setForeground(new Color(46, 204, 113));
        progresoPartido.setBackground(new Color(240, 240, 240));
        progresoPartido.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scrollLog, BorderLayout.CENTER);
        panel.add(progresoPartido, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createActionButton(String texto, Color color) {
        JButton button = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color colorBase = isEnabled() ? color : new Color(150, 150, 150);

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

        button.setPreferredSize(new Dimension(200, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        return button;
    }

    private Border createModernBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }

    private void iniciarPartido() {
        // Reiniciar contadores
        penaltisRealizados = 0;
        golesJugador = 0;
        golesRival = 0;

        // Actualizar interfaz
        lblMarcador.setText("0 - 0");
        lblEstadoPartido.setText("Partido en curso - Penalti 1/5");
        progresoPartido.setValue(0);
        progresoPartido.setString("Penaltis: 0/5");

        // Habilitar controles
        btnIzquierda.setEnabled(true);
        btnCentro.setEnabled(true);
        btnDerecha.setEnabled(true);
        btnIniciarPartido.setEnabled(false);

        // Limpiar log y agregar mensaje inicial
        areaLog.setText("🚀 ¡PARTIDO INICIADO!\n\n");
        areaLog.append("🎯 RONDA 1 - PREPARADO PARA EL PRIMER PENALTI\n");
        areaLog.append("• Elige una dirección para ejecutar tu tiro\n\n");

        // Animación del portero
        iniciarAnimacionPortero();
    }

    private void ejecutarPenalti(String direccion) {
        if (penaltisRealizados >= 5) return;

        penaltisRealizados++;

        // Deshabilitar botones temporalmente
        btnIzquierda.setEnabled(false);
        btnCentro.setEnabled(false);
        btnDerecha.setEnabled(false);

        // Simular movimiento del portero
        simularMovimientoPortero(direccion);

        // Ejecutar lógica del penal (usando tu clase PenaltyShootout)
        boolean esGol = simularResultadoPenalti(direccion);

        if (esGol) {
            golesJugador++;
            areaLog.append("✅ PENALTI " + penaltisRealizados + ": ⚽ ¡GOL! \n");
            areaLog.append("   Tiro a " + direccion + " - ¡Perfecto!\n\n");
        } else {
            areaLog.append("❌ PENALTI " + penaltisRealizados + ": Fallado \n");
            areaLog.append("   Tiro a " + direccion + " - Portero atajó\n\n");
        }

        // Simular tiro del rival
        boolean golRival = Math.random() > 0.3; // 70% de probabilidad de gol del rival
        if (golRival) {
            golesRival++;
            areaLog.append("⚽ RIVAL: Gol - Marcador: " + golesJugador + " - " + golesRival + "\n\n");
        } else {
            areaLog.append("🧤 RIVAL: Falló - ¡Buen trabajo!\n\n");
        }

        // Actualizar interfaz
        actualizarMarcador();
        progresoPartido.setValue(penaltisRealizados);
        progresoPartido.setString("Penaltis: " + penaltisRealizados + "/5");
        lblEstadoPartido.setText("Partido en curso - Penalti " + (penaltisRealizados + 1) + "/5");

        // Verificar si el partido terminó
        if (penaltisRealizados >= 5) {
            finalizarPartido();
        } else {
            // Rehabilitar botones después de un delay
            Timer timer = new Timer(2000, e -> {
                btnIzquierda.setEnabled(true);
                btnCentro.setEnabled(true);
                btnDerecha.setEnabled(true);
                areaLog.append("🎯 RONDA " + (penaltisRealizados + 1) + " - PREPARADO PARA EL SIGUIENTE PENALTI\n\n");
            });
            timer.setRepeats(false);
            timer.start();
        }

        // Auto-scroll al final del log
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private boolean simularResultadoPenalti(String direccion) {
        // Usar las habilidades del jugador para determinar probabilidad
        int precision = gameManager.getJugador().getPrecision();
        double probabilidadBase = 0.6 + (precision * 0.004); // Base 60% + bonus por precisión

        // Factor aleatorio
        return Math.random() < probabilidadBase;
    }

    private void simularMovimientoPortero(String direccionJugador) {
        // Detener animación actual
        if (animacionPortero != null && animacionPortero.isRunning()) {
            animacionPortero.stop();
        }

        // Posiciones del portero para cada dirección
        int posX = 300; // Centro por defecto
        switch (direccionJugador) {
            case "IZQUIERDA": posX = 200; break;
            case "DERECHA": posX = 400; break;
            case "CENTRO": posX = 300; break;
        }

        // Animación suave del portero
        final int targetX = posX;
        final int startX = lblPortero.getX();

        animacionPortero = new Timer(20, new ActionListener() {
            int currentX = startX;
            int step = (targetX - startX) / 10;

            @Override
            public void actionPerformed(ActionEvent e) {
                currentX += step;
                lblPortero.setLocation(currentX, lblPortero.getY());
                panelCancha.repaint();

                if (Math.abs(currentX - targetX) <= Math.abs(step)) {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        animacionPortero.start();
    }

    private void iniciarAnimacionPortero() {
        // Animación de preparación del portero
        Timer preparacionTimer = new Timer(500, new ActionListener() {
            int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 4) {
                    int offset = (count % 2 == 0) ? 20 : -20;
                    lblPortero.setLocation(300 + offset, lblPortero.getY());
                    count++;
                } else {
                    ((Timer)e.getSource()).stop();
                    lblPortero.setLocation(300, lblPortero.getY());
                }
            }
        });
        preparacionTimer.start();
    }

    private void actualizarMarcador() {
        lblMarcador.setText(golesJugador + " - " + golesRival);
        panelCancha.repaint();
    }

    private void finalizarPartido() {
        lblEstadoPartido.setText("Partido Finalizado");
        btnIniciarPartido.setEnabled(true);

        // Determinar resultado
        String resultado;
        if (golesJugador > golesRival) {
            resultado = "🎉 ¡VICTORIA!";
            areaLog.append("\n🏆 ¡FELICIDADES! HAS GANADO EL PARTIDO\n");
            areaLog.append("💰 Recompensa: $500 | ⭐ Experiencia: 200 XP\n");

            // Dar recompensas
            gameManager.getJugador().setDinero(500);
            gameManager.getJugador().setExperiencia(200);
        } else if (golesJugador < golesRival) {
            resultado = "💔 Derrota";
            areaLog.append("\n😔 Has perdido el partido\n");
            areaLog.append("💪 Sigue practicando para mejorar\n");
        } else {
            resultado = "🤝 Empate";
            areaLog.append("\n⚖️ El partido terminó en empate\n");
            areaLog.append("💰 Recompensa: $200 | ⭐ Experiencia: 100 XP\n");

            gameManager.getJugador().setDinero(200);
            gameManager.getJugador().setExperiencia(100);
        }

        areaLog.append("\n" + resultado + " - Marcador Final: " + golesJugador + " - " + golesRival + "\n");
        areaLog.append("\nPresiona 'INICIAR PARTIDO' para jugar de nuevo\n");

        // Mostrar diálogo de resultado
        JOptionPane.showMessageDialog(this,
                "<html><center><b>" + resultado + "</b><br>" +
                        "Marcador Final: " + golesJugador + " - " + golesRival + "<br>" +
                        (golesJugador >= golesRival ? "¡Excelente trabajo!" : "¡Sigue practicando!") + "</center></html>",
                "🏁 Partido Finalizado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void refrescarPanel() {
        // Reiniciar estado del panel
        penaltisRealizados = 0;
        golesJugador = 0;
        golesRival = 0;
        lblMarcador.setText("0 - 0");
        lblEstadoPartido.setText("Preparado para comenzar...");
        progresoPartido.setValue(0);
        progresoPartido.setString("Penaltis: 0/5");
        areaLog.setText("🏟️ BIENVENIDO AL PARTIDO DE PENALTIS\n\n");
        btnIzquierda.setEnabled(false);
        btnCentro.setEnabled(false);
        btnDerecha.setEnabled(false);
        btnIniciarPartido.setEnabled(true);
        lblPortero.setLocation(300, 150);
    }
}