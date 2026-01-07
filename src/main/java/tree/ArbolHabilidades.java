package tree;

import cu.edu.cujae.ceis.tree.general.GeneralTree;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import model.Habilidad;

public class ArbolHabilidades {
    private GeneralTree<Habilidad> arbol;

    public ArbolHabilidades() {
        this.arbol = new GeneralTree<>();
        construirArbolHabilidades();
    }

    private void construirArbolHabilidades() {

        Habilidad raiz = new Habilidad("root", "Habilidades Principales",
                "Desbloquea todas las habilidades", 0, 0, "none", 0);
        BinaryTreeNode<Habilidad> nodoRaiz = new BinaryTreeNode<>(raiz);
        arbol.setRoot(nodoRaiz);

        // Rama Precisión
        Habilidad precision = new Habilidad("pre_base", "Precisión Base",
                "Mejora precisión +10", 500, 100, "precision", 10);
        Habilidad tiroColocado = new Habilidad("pre_colocado", "Tiro Colocado",
                "Precisión +15", 800, 200, "precision", 15);
        Habilidad tiroEfecto = new Habilidad("pre_efecto", "Tiro con Efecto",
                "Precisión +20", 1200, 300, "precision", 20);

        BinaryTreeNode<Habilidad> nodoPrecision = new BinaryTreeNode<>(precision);
        BinaryTreeNode<Habilidad> nodoColocado = new BinaryTreeNode<>(tiroColocado);
        BinaryTreeNode<Habilidad> nodoEfecto = new BinaryTreeNode<>(tiroEfecto);

        arbol.insertNode(nodoPrecision, nodoRaiz);
        arbol.insertNode(nodoColocado, nodoPrecision);
        arbol.insertNode(nodoEfecto, nodoPrecision);

        // Rama Potencia
        Habilidad potencia = new Habilidad("pot_base", "Potencia Base",
                "Potencia +10", 500, 100, "potencia", 10);
        Habilidad disparoLargo = new Habilidad("pot_largo", "Disparo Largo",
                "Potencia +15", 800, 200, "potencia", 15);
        Habilidad penalesPotentes = new Habilidad("pot_penales", "Penales Potentes",
                "Potencia +20", 1200, 300, "potencia", 20);

        BinaryTreeNode<Habilidad> nodoPotencia = new BinaryTreeNode<>(potencia);
        BinaryTreeNode<Habilidad> nodoLargo = new BinaryTreeNode<>(disparoLargo);
        BinaryTreeNode<Habilidad> nodoPenales = new BinaryTreeNode<>(penalesPotentes);

        arbol.insertNode(nodoPotencia, nodoRaiz);
        arbol.insertNode(nodoLargo, nodoPotencia);
        arbol.insertNode(nodoPenales, nodoPotencia);

        // Rama Estrategia
        Habilidad estrategia = new Habilidad("est_base", "Estrategia Base",
                "Estrategia +10", 500, 100, "estrategia", 10);
        Habilidad controlPresion = new Habilidad("est_presion", "Control de Presión",
                "Estrategia +15", 800, 200, "estrategia", 15);
        Habilidad lecturaPortero = new Habilidad("est_lectura", "Lectura del Portero",
                "Estrategia +20", 1200, 300, "estrategia", 20);

        BinaryTreeNode<Habilidad> nodoEstrategia = new BinaryTreeNode<>(estrategia);
        BinaryTreeNode<Habilidad> nodoPresion = new BinaryTreeNode<>(controlPresion);
        BinaryTreeNode<Habilidad> nodoLectura = new BinaryTreeNode<>(lecturaPortero);

        arbol.insertNode(nodoEstrategia, nodoRaiz);
        arbol.insertNode(nodoPresion, nodoEstrategia);
        arbol.insertNode(nodoLectura, nodoEstrategia);
    }

    public GeneralTree<Habilidad> getArbol() {
        return arbol;
    }
}