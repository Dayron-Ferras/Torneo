package ui;



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
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel izquierdo - Lista de torneos
        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder("Torneos Disponibles"));

        listaTorneos = new JList<>(gameManager.getTorneosDisponibles().toArray(new Torneo[0]));
        listaTorneos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelLista.add(new JScrollPane(listaTorneos), BorderLayout.CENTER);

        btnIniciarTorneo = new JButton("Iniciar Torneo");
        panelLista.add(btnIniciarTorneo, BorderLayout.SOUTH);

        // Panel derecho - Información
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Torneo"));

        areaInfo = new JTextArea();
        areaInfo.setEditable(false);
        areaInfo.setText("Selecciona un torneo para ver su información...");
        panelInfo.add(new JScrollPane(areaInfo), BorderLayout.CENTER);

        // Dividir pantalla
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLista, panelInfo);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);

        // Listeners
        listaTorneos.addListSelectionListener(e -> mostrarInfoTorneo());
        btnIniciarTorneo.addActionListener(e -> iniciarTorneoSeleccionado());
    }

    private void mostrarInfoTorneo() {
        Torneo torneo = listaTorneos.getSelectedValue();
        if (torneo != null) {
            String info = "Nombre: " + torneo.getNombre() + "\n" +
                    "Nivel Requerido: " + torneo.getNivelRequerido() + "\n" +
                    "Recompensa: $" + torneo.getRecompensa() + "\n" +
                    "Estado: " + (torneo.isCompletado() ? "Completado" : "Disponible") + "\n\n" +
                    "Clubs Participantes:\n";

            for (int i = 0; i < torneo.getClubsParticipantes().size(); i++) {
                info += "- " + torneo.getClubsParticipantes().get(i).getNombre() + "\n";
            }

            areaInfo.setText(info);
        }
    }

    private void iniciarTorneoSeleccionado() {
        Torneo torneo = listaTorneos.getSelectedValue();
        if (torneo != null) {
            if (gameManager.iniciarTorneo(torneo.getNombre())) {
                JOptionPane.showMessageDialog(this,
                        "¡Torneo " + torneo.getNombre() + " iniciado!",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No cumples con los requisitos para este torneo.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}