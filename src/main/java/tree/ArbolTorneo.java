package tree;

import cu.edu.cujae.ceis.tree.binary.BinaryTree;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import model.Partido;

public class ArbolTorneo {
    private BinaryTree<Partido> arbol;
    private String nombreTorneo;

    public ArbolTorneo(String nombre, String[] equipos) {
        this.nombreTorneo = nombre;
        this.arbol = new BinaryTree<>();
        construirLlave(equipos);
    }

    private void construirLlave(String[] equipos) {
        if (equipos.length < 2) return;

        BinaryTreeNode<Partido> raiz = construirRecursivo(equipos, 0, equipos.length - 1, 1);
        arbol.setRoot(raiz);
    }

    private BinaryTreeNode<Partido> construirRecursivo(String[] equipos, int inicio, int fin, int ronda) {
        if (inicio == fin) {
            // Este es un equipo individual (hoja del árbol)
            Partido partido = new Partido("Ronda " + ronda, equipos[inicio], "Por Definir");
            return new BinaryTreeNode<>(partido);
        }

        int medio = (inicio + fin) / 2;
        Partido partido = new Partido("Ronda " + ronda, "Ganador " + (inicio+1), "Ganador " + (medio+2));

        BinaryTreeNode<Partido> nodo = new BinaryTreeNode<>(partido);
        nodo.setLeft(construirRecursivo(equipos, inicio, medio, ronda + 1));
        nodo.setRight(construirRecursivo(equipos, medio + 1, fin, ronda + 1));

        return nodo;
    }

    public BinaryTree<Partido> getArbol() {
        return arbol;
    }

    public Partido getPartidoActual() {
        // Lógica para encontrar el partido actual del jugador
        return encontrarPartidoJugador((BinaryTreeNode<Partido>) arbol.getRoot());
    }

    private Partido encontrarPartidoJugador(BinaryTreeNode<Partido> nodo) {
        if (nodo == null) return null;

        Partido partido = nodo.getInfo();
        if (!partido.isJugado() && partido.getEquipoLocal().contains("Jugador")) {
            return partido;
        }

        Partido izquierda = encontrarPartidoJugador(nodo.getLeft());
        if (izquierda != null) return izquierda;

        return encontrarPartidoJugador(nodo.getRight());
    }
}