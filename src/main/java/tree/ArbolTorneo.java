package tree;

import cu.edu.cujae.ceis.tree.TreeNode;
import cu.edu.cujae.ceis.tree.binary.BinaryTree;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import model.Partido;
import model.Club;
import model.Jugador;

public class ArbolTorneo {
    private BinaryTree<Partido> arbol;
    private String nombreTorneo;

    public ArbolTorneo(String nombre, Club[] equipos) {
        this.nombreTorneo = nombre;
        this.arbol = new BinaryTree<>();
        construirLlave(equipos);
    }

    private void construirLlave(Club[] equipos) {
        if (equipos.length < 2) return;

        BinaryTreeNode<Partido> raiz = construirRecursivo(equipos, 0, equipos.length - 1, 1);
        arbol.setRoot(raiz);
    }

    private BinaryTreeNode<Partido> construirRecursivo(Club[] equipos, int inicio, int fin, int ronda) {
        if (inicio == fin) {
            // Este es un equipo individual (hoja del árbol)
            Partido partido = new Partido("Ronda " + ronda, equipos[inicio], null);
            return new BinaryTreeNode<>(partido);
        }

        int medio = (inicio + fin) / 2;
        Partido partido = new Partido("Ronda " + ronda, (Club) null, (Club)null);

        BinaryTreeNode<Partido> nodo = new BinaryTreeNode<>(partido);

        nodo.setLeft(construirRecursivo(equipos, inicio, medio, ronda + 1));
        nodo.setRight(construirRecursivo(equipos, medio + 1, fin, ronda + 1));

        return nodo;
    }

    public void actualizarLlave() {
        actualizarRecursivo((BinaryTreeNode<Partido>) arbol.getRoot());
    }

    private void actualizarRecursivo(BinaryTreeNode<Partido> nodo) {
        if (nodo == null) return;

        Partido partido = nodo.getInfo();

        // Si es un partido de ronda final (hoja), ya tiene su equipo local definido
        if (nodo.getLeft() == null && nodo.getRight() == null) {
            return; // Ya está definido desde la construcción
        }

        // Para partidos que no son hojas, determinar equipos basado en ganadores de rondas anteriores
        if (nodo.getLeft() != null && nodo.getRight() != null) {
            Partido leftPartido = nodo.getLeft().getInfo();
            Partido rightPartido = nodo.getRight().getInfo();

            // Solo actualizar si los partidos hijos ya se jugaron
            if (leftPartido.isJugado() && rightPartido.isJugado()) {
                partido = new Partido(partido.getRonda(), leftPartido.getGanador(), rightPartido.getGanador());
                nodo.setInfo(partido);
            }
        }

        // Continuar recursivamente
        actualizarRecursivo(nodo.getLeft());
        actualizarRecursivo(nodo.getRight());
    }

    public BinaryTree<Partido> getArbol() {
        return arbol;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    // Encuentra el próximo partido del jugador
    public Partido getPartidoActual(Jugador jugador) {
        return encontrarPartidoJugador((BinaryTreeNode<Partido>) arbol.getRoot(), jugador);
    }

    private Partido encontrarPartidoJugador(BinaryTreeNode<Partido> nodo, Jugador jugador) {
        if (nodo == null || jugador == null || jugador.getClubActual() == null)
            return null;

        Partido partido = nodo.getInfo();

        // Verificar si el jugador participa en este partido y no se ha jugado
        if (!partido.isJugado() &&
                ((partido.getEquipoLocal() != null && partido.getEquipoLocal().equals(jugador.getClubActual())) ||
                        (partido.getEquipoVisitante() != null && partido.getEquipoVisitante().equals(jugador.getClubActual())))) {
            return partido;
        }

        // Buscar recursivamente
        Partido izquierda = encontrarPartidoJugador(nodo.getLeft(), jugador);
        if (izquierda != null) return izquierda;

        return encontrarPartidoJugador(nodo.getRight(), jugador);
    }

    // Verificar si el torneo ha terminado
    public boolean isTorneoTerminado() {
        // Ahora hacemos el casting correctamente
        TreeNode<Partido> rootNode = arbol.getRoot();
        Partido finalPartido = null;
        if (rootNode != null) {
            BinaryTreeNode<Partido> binaryRoot = (BinaryTreeNode<Partido>) rootNode;
            finalPartido = binaryRoot.getInfo();
            // ... resto del código
        }
        return finalPartido.isJugado();
    }

    // Obtener el campeón del torneo
    public Club getCampeon() {
        if (isTorneoTerminado()) {
            // Ahora hacemos el casting correctamente
            TreeNode<Partido> rootNode = arbol.getRoot();
            Partido finalPartido = null;
            if (rootNode != null) {
                BinaryTreeNode<Partido> binaryRoot = (BinaryTreeNode<Partido>) rootNode;
                finalPartido = binaryRoot.getInfo();
                // ... resto del código
            }
            return finalPartido.getGanador();
        }
        return null;
    }

    public Partido getPartidoFinal() {
        TreeNode<Partido> rootNode = arbol.getRoot();
        if (rootNode != null) {
            BinaryTreeNode<Partido> binaryRoot = (BinaryTreeNode<Partido>) rootNode;
            return binaryRoot.getInfo();
        }
        return null;
    }

    // Método para imprimir todo el árbol (útil para ver el progreso)
    public void imprimirArbol() {
        System.out.println("=== " + nombreTorneo + " ===");
        imprimirRecursivo((BinaryTreeNode<Partido>) arbol.getRoot(), 0);
    }

    private void imprimirRecursivo(BinaryTreeNode<Partido> nodo, int nivel) {
        if (nodo == null) return;

        // Espacios para indentación según el nivel
        String espacios = "  ".repeat(nivel);

        Partido partido = nodo.getInfo();
        System.out.println(espacios + partido.toString());

        // Imprimir subárbol izquierdo y derecho
        imprimirRecursivo(nodo.getLeft(), nivel + 1);
        imprimirRecursivo(nodo.getRight(), nivel + 1);
    }

}