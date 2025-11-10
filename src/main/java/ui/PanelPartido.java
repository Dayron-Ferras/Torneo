package ui;



import game.GameManager;
import game.PenaltyShootout;

import javax.swing.*;
import java.awt.*;

public class PanelPartido extends JPanel {
    private GameManager gameManager;
    private JLabel lblMarcador;
    private JButton btnIzquierda, btnCentro, btnDerecha;
    private JTextArea areaLog;

    public PanelPartido(GameManager gameManager) {
        this.gameManager = gameManager;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel superior - Información del partido
        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Partido en Curso"));

        lblMarcador = new JLabel("Preparado para comenzar...");
        panelSuperior.add(lblMarcador);

        // Panel central - Controles de tiro
        JPanel panelControles = new JPanel(new GridLayout(1, 3, 10, 10));
        panelControles.setBorder(BorderFactory.createTitledBorder("Elige dirección del tiro"));

        btnIzquierda = new JButton("⬅ IZQUIERDA");
        btnCentro = new JButton("⬆ CENTRO");
        btnDerecha = new JButton("➡ DERECHA");

        // Personalizar botones
        btnIzquierda.setBackground(Color.RED);
        btnCentro.setBackground(Color.YELLOW);
        btnDerecha.setBackground(Color.GREEN);

        btnIzquierda.setForeground(Color.WHITE);
        btnDerecha.setForeground(Color.WHITE);

        panelControles.add(btnIzquierda);
        panelControles.add(btnCentro);
        panelControles.add(btnDerecha);

        // Panel inferior - Log del partido
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Desarrollo del Partido"));

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setText("El partido comenzará cuando ejecutes un penal...\n");
        panelInferior.add(new JScrollPane(areaLog), BorderLayout.CENTER);

        // Agregar todos los paneles
        add(panelSuperior, BorderLayout.NORTH);
        add(panelControles, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Configurar listeners
        setupListeners();
    }

    private void setupListeners() {
        btnIzquierda.addActionListener(e -> ejecutarPenalti("IZQUIERDA"));
        btnCentro.addActionListener(e -> ejecutarPenalti("CENTRO"));
        btnDerecha.addActionListener(e -> ejecutarPenalti("DERECHA"));
    }

    private void ejecutarPenalti(String direccion) {
        areaLog.append("Ejecutando penal hacia: " + direccion + "\n");
        // TODO: Integrar con la lógica de PenaltyShootout
    }
}