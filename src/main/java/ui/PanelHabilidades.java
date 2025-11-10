package ui;

import game.GameManager;
import model.Habilidad;
import tree.ArbolHabilidades;
import cu.edu.cujae.ceis.tree.general.GeneralTree;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import cu.edu.cujae.ceis.tree.iterators.general.InDepthIterator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
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

    public PanelHabilidades(GameManager gameManager) {
        this.gameManager = gameManager;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Panel del árbol de habilidades
        JPanel panelArbol = new JPanel(new BorderLayout());
        panelArbol.setBorder(BorderFactory.createTitledBorder("Árbol de Habilidades"));

        // Configurar JTree con las habilidades
        configurarArbolHabilidades();
        panelArbol.add(new JScrollPane(arbolHabilidades), BorderLayout.CENTER);

        // Panel de información y compra
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información de Habilidad"));

        areaDescripcion = new JTextArea();
        areaDescripcion.setEditable(false);
        areaDescripcion.setText("Selecciona una habilidad para ver su información...");
        areaDescripcion.setLineWrap(true);
        areaDescripcion.setWrapStyleWord(true);
        panelInfo.add(new JScrollPane(areaDescripcion), BorderLayout.CENTER);

        btnComprar = new JButton("Comprar Habilidad");
        btnComprar.setEnabled(false);
        panelInfo.add(btnComprar, BorderLayout.SOUTH);

        // Panel de estadísticas del jugador
        JPanel panelStats = crearPanelEstadisticas();

        // Layout principal
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(panelInfo, BorderLayout.CENTER);
        panelDerecho.add(panelStats, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelArbol, panelDerecho);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);

        // Configurar eventos
        configurarEventos();
    }

    private void configurarArbolHabilidades() {
        // Crear el modelo del árbol
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Habilidades Principales");
        treeModel = new DefaultTreeModel(raiz);

        // Obtener el árbol de habilidades del gameManager
        ArbolHabilidades arbolHabs = gameManager.getArbolHabilidades();
        if (arbolHabs != null) {
            construirArbolSwing(raiz, (BinaryTreeNode<Habilidad>) arbolHabs.getArbol().getRoot());
        }

        // Crear el JTree
        arbolHabilidades = new JTree(treeModel);
        arbolHabilidades.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Expandir todos los nodos inicialmente
        for (int i = 0; i < arbolHabilidades.getRowCount(); i++) {
            arbolHabilidades.expandRow(i);
        }
    }

    private void construirArbolSwing(DefaultMutableTreeNode padreSwing, BinaryTreeNode<Habilidad> nodoArbol) {
        if (nodoArbol == null) return;

        Habilidad habilidad = nodoArbol.getInfo();
        String textoNodo = habilidad.getNombre();

        // Si la habilidad está desbloqueada, agregar un checkmark
        if (habilidad.isDesbloqueada()) {
            textoNodo = "✅ " + textoNodo;
        } else {
            textoNodo = "🔒 " + textoNodo;
        }

        DefaultMutableTreeNode nodoSwing = new DefaultMutableTreeNode(textoNodo);
        nodoSwing.setUserObject(habilidad); // Guardar la habilidad como userObject
        padreSwing.add(nodoSwing);

        // Recorrer hijos (en árbol general, los hijos están en left y right como lista)
        if (nodoArbol.getLeft() != null) {
            BinaryTreeNode<Habilidad> hijoActual = nodoArbol.getLeft();
            while (hijoActual != null) {
                construirArbolSwing(nodoSwing, hijoActual);
                hijoActual = hijoActual.getRight();
            }
        }
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panelStats = new JPanel(new GridLayout(0, 2, 5, 5));
        panelStats.setBorder(BorderFactory.createTitledBorder("Estadísticas del Jugador"));

        // Actualizar estadísticas
        actualizarEstadisticas(panelStats);

        return panelStats;
    }

    private void actualizarEstadisticas(JPanel panelStats) {
        panelStats.removeAll();

        panelStats.add(new JLabel("Dinero:"));
        panelStats.add(new JLabel("$" + gameManager.getJugador().getDinero()));

        panelStats.add(new JLabel("Experiencia:"));
        panelStats.add(new JLabel(gameManager.getJugador().getExperiencia() + " XP"));

        panelStats.add(new JLabel("Nivel:"));
        panelStats.add(new JLabel(String.valueOf(gameManager.getJugador().getNivel())));

        panelStats.add(new JLabel("Precisión:"));
        panelStats.add(new JLabel(String.valueOf(gameManager.getJugador().getPrecision())));

        panelStats.add(new JLabel("Potencia:"));
        panelStats.add(new JLabel(String.valueOf(gameManager.getJugador().getPotencia())));

        panelStats.add(new JLabel("Estrategia:"));
        panelStats.add(new JLabel(String.valueOf(gameManager.getJugador().getEstrategia())));

        panelStats.revalidate();
        panelStats.repaint();
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

        // Evento del botón comprar
        btnComprar.addActionListener(e -> {
            if (habilidadSeleccionada != null) {
                comprarHabilidad();
            }
        });
    }

    private void mostrarInformacionHabilidad(Habilidad habilidad) {
        StringBuilder info = new StringBuilder();
        info.append("Nombre: ").append(habilidad.getNombre()).append("\n\n");
        info.append("Descripción: ").append(habilidad.getDescripcion()).append("\n\n");
        info.append("Tipo: ").append(habilidad.getTipo().toUpperCase()).append("\n");
        info.append("Mejora: +").append(habilidad.getValorMejora()).append(" puntos\n\n");
        info.append("Costo:\n");
        info.append("  • Dinero: $").append(habilidad.getCostoDinero()).append("\n");
        info.append("  • Experiencia: ").append(habilidad.getCostoExperiencia()).append(" XP\n\n");

        if (habilidad.isDesbloqueada()) {
            info.append("✅ HABILIDAD DESBLOQUEADA");
            btnComprar.setEnabled(false);
            btnComprar.setText("Ya adquirida");
        } else {
            boolean puedeComprar = habilidad.puedeComprar(gameManager.getJugador());
            if (puedeComprar) {
                info.append("🎯 PUEDES COMPRAR ESTA HABILIDAD");
                btnComprar.setEnabled(true);
                btnComprar.setText("Comprar Habilidad");
            } else {
                info.append("❌ NO CUMPLES CON LOS REQUISITOS");
                btnComprar.setEnabled(false);
                btnComprar.setText("Fondos insuficientes");
            }
        }

        areaDescripcion.setText(info.toString());
    }

    private void comprarHabilidad() {
        if (habilidadSeleccionada != null && !habilidadSeleccionada.isDesbloqueada()) {
            habilidadSeleccionada.comprar(gameManager.getJugador());

            // Actualizar la interfaz
            actualizarArbolHabilidades();
            actualizarEstadisticas((JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH));
            mostrarInformacionHabilidad(habilidadSeleccionada);

            JOptionPane.showMessageDialog(this,
                    "¡Habilidad '" + habilidadSeleccionada.getNombre() + "' adquirida!",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
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

        // Expandir todos los nodos nuevamente
        for (int i = 0; i < arbolHabilidades.getRowCount(); i++) {
            arbolHabilidades.expandRow(i);
        }
    }

    // Método para refrescar el panel completo
    public void refrescarPanel() {
        actualizarArbolHabilidades();
        JPanel panelStats = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        if (panelStats != null) {
            actualizarEstadisticas(panelStats);
        }
    }
}