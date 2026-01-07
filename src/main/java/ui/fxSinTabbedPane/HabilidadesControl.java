package ui.fxSinTabbedPane;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Habilidad;
import game.GameManager;
import tree.ArbolHabilidades;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import javafx.scene.control.TreeItem;

public class HabilidadesControl {

    @FXML private TreeView<Object> treeHabs;
    @FXML private TextArea txtInfo;
    @FXML private Button btnBuy;
    @FXML private Button btnVolver;
    @FXML private GridPane statsGrid;

    private Principal principal;
    private GameManager gameManager;
    private Habilidad selected;


    public void setGameManager(GameManager gm) {
        this.gameManager = gm;
        buildTree();
        refreshStats();
    }

    public void setMainApp(Principal p){
        this.principal = p;
    }

    @FXML
    public void initialize() {
        btnBuy.setDisable(true);

        treeHabs.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) -> {
                    if (newV == null) return;

                    Object val = newV.getValue();
                    if (val instanceof Habilidad) {
                        selected = (Habilidad) val;
                        showInfo(selected);
                    } else {
                        selected = null;
                        txtInfo.setText("");
                        btnBuy.setDisable(true);
                    }
                });
    }

    private void buildTree() {
        if (gameManager == null) return;

        ArbolHabilidades arbol = gameManager.getArbolHabilidades();
        if (arbol == null || arbol.getArbol() == null) return;

        BinaryTreeNode<Habilidad> rootNode =
                (BinaryTreeNode<Habilidad>) arbol.getArbol().getRoot();

        // Crear solo UNA vez el root
        TreeItem<Object> rootItem = new TreeItem<>(rootNode.getInfo());

        // Construir hijos sin duplicar la raíz
        addChildren(rootNode, rootItem);

        treeHabs.setRoot(rootItem);
        rootItem.setExpanded(true);
    }

    /**
     * Agrega solo los hijos. NO recrea el nodo actual.
     */
    private void addChildren(BinaryTreeNode<Habilidad> node, TreeItem<Object> parentItem) {
        if (node == null) return;

        BinaryTreeNode<Habilidad> child = node.getLeft();

        while (child != null) {
            TreeItem<Object> childItem = new TreeItem<>(child.getInfo());
            parentItem.getChildren().add(childItem);

            addChildren(child, childItem);

            child = child.getRight();
        }
    }

    private void showInfo(Habilidad h) {
        StringBuilder sb = new StringBuilder();
        sb.append("🎯 ").append(h.getNombre()).append("\n");
        sb.append(h.getDescripcion()).append("\n\n");
        sb.append("Costo: $").append(h.getCostoDinero())
                .append(" | ").append(h.getCostoExperiencia()).append(" XP\n");
        sb.append("Mejora: +").append(h.getValorMejora())
                .append(" (").append(h.getTipo()).append(")\n");

        txtInfo.setText(sb.toString());

        boolean puede = h.puedeComprar(gameManager.getJugador()) && !h.isDesbloqueada();
        btnBuy.setDisable(!puede);
    }

    @FXML
    private void onBuy() {
        if (selected == null) return;

        selected.comprar(gameManager.getJugador());
        showInfo(selected);
        refreshStats();

        Alert a = new Alert(Alert.AlertType.INFORMATION, "¡Habilidad adquirida!", ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void refreshStats() {
        if (gameManager == null) return;

        Platform.runLater(() -> {
            statsGrid.getChildren().clear();
            int row = 0;

            addStat("Dinero:", "$" + gameManager.getJugador().getDinero(), row++);
            addStat("Experiencia:", gameManager.getJugador().getExperiencia() + " XP", row++);
            addStat("Nivel:", String.valueOf(gameManager.getJugador().getNivel()), row++);
            addStat("Precisión:", String.valueOf(gameManager.getJugador().getPrecision()), row++);
            addStat("Potencia:", String.valueOf(gameManager.getJugador().getPotencia()), row++);
            addStat("Estrategia:", String.valueOf(gameManager.getJugador().getEstrategia()), row++);
        });
    }

    private void addStat(String label, String value, int row) {
        Label l = new Label(label);
        Label v = new Label(value);

        l.getStyleClass().add("stat-label");
        v.getStyleClass().add("stat-value");

        statsGrid.add(l, 0, row);
        statsGrid.add(v, 1, row);
    }

    @FXML
    private void onVolver() {
        if (principal != null) {
            principal.setScreen("/uiSinTabbedPane/mainMenu.fxml");
        }
    }
}
