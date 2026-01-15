package tree;

import cu.edu.cujae.ceis.tree.TreeNode;
import cu.edu.cujae.ceis.tree.binary.BinaryTree;
import cu.edu.cujae.ceis.tree.binary.BinaryTreeNode;
import model.Partido;
import model.Club;
import model.Jugador;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArbolTorneo {
    private BinaryTree<Partido> arbol;
    private String nombreTorneo;
    private List<Partido> todosPartidos;
    private Random random;

    public ArbolTorneo(String nombre, Club[] equipos) {
        this.nombreTorneo = nombre;
        this.arbol = new BinaryTree<>();
        this.todosPartidos = new ArrayList<>();
        this.random = new Random();
        construirLlave(equipos);
    }

    private void construirLlave(Club[] equipos) {
        if (equipos.length < 2) return;

        // Mezclar los equipos para que los enfrentamientos sean aleatorios
        List<Club> equiposList = new ArrayList<>();
        for (Club club : equipos) {
            equiposList.add(club);
        }
        // Mezclar
        for (int i = equiposList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Club temp = equiposList.get(i);
            equiposList.set(i, equiposList.get(j));
            equiposList.set(j, temp);
        }

        // Crear partidos de primera ronda
        List<BinaryTreeNode<Partido>> partidosRonda1 = new ArrayList<>();

        for (int i = 0; i < equiposList.size(); i += 2) {
            Club equipo1 = equiposList.get(i);
            Club equipo2 = (i + 1 < equiposList.size()) ? equiposList.get(i + 1) : null;

            if (equipo2 != null) {
                Partido partido = new Partido("Ronda 1", equipo1, equipo2);
                todosPartidos.add(partido);
                partidosRonda1.add(new BinaryTreeNode<>(partido));
            } else {
                // Si hay número impar, el último equipo pasa directamente
                System.out.println(equipo1.getNombre() + " pasa directamente a la siguiente ronda");
            }
        }

        // Construir el árbol completo
        int numRondas = (int) Math.ceil(Math.log(equiposList.size()) / Math.log(2));

        List<BinaryTreeNode<Partido>> partidosActual = partidosRonda1;
        int rondaActual = 2;

        while (partidosActual.size() > 1) {
            List<BinaryTreeNode<Partido>> partidosSiguiente = new ArrayList<>();

            for (int i = 0; i < partidosActual.size(); i += 2) {
                BinaryTreeNode<Partido> nodoIzq = partidosActual.get(i);
                BinaryTreeNode<Partido> nodoDer = (i + 1 < partidosActual.size()) ?
                        partidosActual.get(i + 1) : null;

                // Crear partido de siguiente ronda - inicialmente sin equipos

                Club clubPlaceholder = new Club("Por Definir", 0, 0);
                Partido partido = new Partido("Ronda " + rondaActual, clubPlaceholder, clubPlaceholder);


               todosPartidos.add(partido);
                BinaryTreeNode<Partido> nuevoNodo = new BinaryTreeNode<>(partido);

                nuevoNodo.setLeft(nodoIzq);
                if (nodoDer != null) {
                    nuevoNodo.setRight(nodoDer);
                }

                partidosSiguiente.add(nuevoNodo);
            }

            partidosActual = partidosSiguiente;
            rondaActual++;
        }

        // Establecer la raíz del árbol
        if (!partidosActual.isEmpty()) {
            arbol.setRoot(partidosActual.get(0));
        }

        System.out.println("Árbol del torneo '" + nombreTorneo + "' construido:");
        System.out.println("  Total equipos: " + equiposList.size());
        System.out.println("  Total partidos: " + todosPartidos.size());
        System.out.println("  Rondas: " + (rondaActual - 1));
    }

    public void actualizarLlave() {
        System.out.println("Actualizando llave del torneo...");
        actualizarRecursivo((BinaryTreeNode<Partido>) arbol.getRoot());
    }

    private void actualizarRecursivo(BinaryTreeNode<Partido> nodo) {
        if (nodo == null) return;

        // Si es un partido de primera ronda, no necesita actualización de equipos
        if (nodo.getLeft() == null && nodo.getRight() == null) {
            return;
        }

        // Obtener ganadores de los partidos hijos
        Club ganadorIzq = null;
        Club ganadorDer = null;

        if (nodo.getLeft() != null) {
            Partido partidoIzq = nodo.getLeft().getInfo();
            if (partidoIzq.isJugado() && partidoIzq.getGanador() != null) {
                ganadorIzq = partidoIzq.getGanador();
                System.out.println("  Ganador izquierdo para " + nodo.getInfo().getRonda() +
                        ": " + ganadorIzq.getNombre());
            }
        }

        if (nodo.getRight() != null) {
            Partido partidoDer = nodo.getRight().getInfo();
            if (partidoDer.isJugado() && partidoDer.getGanador() != null) {
                ganadorDer = partidoDer.getGanador();
                System.out.println("  Ganador derecho para " + nodo.getInfo().getRonda() +
                        ": " + ganadorDer.getNombre());
            }
        }

        // Actualizar el partido actual si tenemos ganadores
        Partido partidoActual = nodo.getInfo();

        if (ganadorIzq != null && ganadorDer != null) {
            // Ambos ganadores definidos
            if (partidoActual.getEquipoLocal() != ganadorIzq ||
                    partidoActual.getEquipoVisitante() != ganadorDer) {

                // Crear nuevo partido con los equipos actualizados
                Partido nuevoPartido = new Partido(partidoActual.getRonda(), ganadorIzq, ganadorDer);
                nodo.setInfo(nuevoPartido);

                // Actualizar en la lista
                actualizarEnListaPartidos(partidoActual, nuevoPartido);

                System.out.println("  " + partidoActual.getRonda() + " actualizado: " +
                        ganadorIzq.getNombre() + " vs " + ganadorDer.getNombre());
            }
        } else if (ganadorIzq != null && partidoActual.getEquipoLocal() == null) {
            // Solo el ganador izquierdo definido
            Partido nuevoPartido = new Partido(partidoActual.getRonda(), ganadorIzq, null);
            nodo.setInfo(nuevoPartido);
            actualizarEnListaPartidos(partidoActual, nuevoPartido);
        } else if (ganadorDer != null && partidoActual.getEquipoVisitante() == null) {
            // Solo el ganador derecho definido
            Partido nuevoPartido = new Partido(partidoActual.getRonda(), null, ganadorDer);
            nodo.setInfo(nuevoPartido);
            actualizarEnListaPartidos(partidoActual, nuevoPartido);
        }

        // Continuar recursivamente
        actualizarRecursivo(nodo.getLeft());
        actualizarRecursivo(nodo.getRight());
    }

    private void actualizarEnListaPartidos(Partido viejo, Partido nuevo) {
        int index = todosPartidos.indexOf(viejo);
        if (index != -1) {
            todosPartidos.set(index, nuevo);
        }
    }

    public BinaryTree<Partido> getArbol() {
        return arbol;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    public Partido getPartidoActual(Jugador jugador) {
        if (jugador == null || jugador.getClubActual() == null) {
            return null;
        }

        Club clubJugador = jugador.getClubActual();

        // Buscar en todos los partidos no jugados
        for (Partido partido : todosPartidos) {
            if (!partido.isJugado()) {
                Club local = partido.getEquipoLocal();
                Club visitante = partido.getEquipoVisitante();

                // Verificar si el club del jugador está en este partido
                if ((local != null && local.equals(clubJugador)) ||
                        (visitante != null && visitante.equals(clubJugador))) {
                    return partido;
                }
            }
        }

        return null;
    }

    private Partido buscarPartidoJugadorRecursivo(BinaryTreeNode<Partido> nodo, Club club) {
        if (nodo == null) return null;

        Partido partido = nodo.getInfo();

        // Verificar si este partido involucra al club
        Club local = partido.getEquipoLocal();
        Club visitante = partido.getEquipoVisitante();

        boolean clubEnPartido = (local != null && local.equals(club)) ||
                (visitante != null && visitante.equals(club));

        if (clubEnPartido) {
            // Si el club está en este partido
            if (!partido.isJugado()) {
                return partido; // Partido pendiente
            } else {
                // Si ya se jugó y el club ganó, buscar en el partido padre
                if (partido.getGanador() != null && partido.getGanador().equals(club)) {
                    // El club ganó, verificar si hay siguiente ronda
                    BinaryTreeNode<Partido> padre = obtenerPadre(nodo);
                    if (padre != null && !padre.getInfo().isJugado()) {
                        return padre.getInfo();
                    }
                }
                // El club perdió o no hay siguiente ronda
                return null;
            }
        }

        // Si el club no está en este partido, buscar en hijos
        Partido encontradoIzq = buscarPartidoJugadorRecursivo(nodo.getLeft(), club);
        if (encontradoIzq != null) return encontradoIzq;

        return buscarPartidoJugadorRecursivo(nodo.getRight(), club);
    }

    // Método auxiliar para obtener el nodo padre (necesita una implementación)
    private BinaryTreeNode<Partido> obtenerPadre(BinaryTreeNode<Partido> nodoActual) {
        // Necesitas implementar una búsqueda del padre en el árbol
        // Esto es una implementación simplificada
        return buscarPadreRecursivo((BinaryTreeNode<Partido>) arbol.getRoot(), nodoActual, null);
    }

    private BinaryTreeNode<Partido> buscarPadreRecursivo(BinaryTreeNode<Partido> actual,
                                                         BinaryTreeNode<Partido> objetivo,
                                                         BinaryTreeNode<Partido> padre) {
        if (actual == null) return null;
        if (actual == objetivo) return padre;

        BinaryTreeNode<Partido> encontrado = buscarPadreRecursivo(actual.getLeft(), objetivo, actual);
        if (encontrado == null) {
            encontrado = buscarPadreRecursivo(actual.getRight(), objetivo, actual);
        }
        return encontrado;
    }


    // Método para simular automáticamente todos los partidos pendientes
    public void simularPartidosPendientes() {
        System.out.println("\nSimulando partidos pendientes del torneo '" + nombreTorneo + "'...");

        boolean huboCambios;
        int intentos = 0;
        int maxIntentos = 10;

        do {
            huboCambios = false;
            intentos++;

            for (Partido partido : todosPartidos) {
                if (!partido.isJugado()) {
                    // Verificar que el partido tenga ambos equipos definidos
                    if (partido.getEquipoLocal() != null && partido.getEquipoVisitante() != null) {
                        // Simular el partido
                        partido.simularPartido();
                        huboCambios = true;
                        System.out.println("  Simulado: " + partido);
                    } else if (partido.getEquipoLocal() != null || partido.getEquipoVisitante() != null) {
                        // Solo un equipo definido - esperar
                        System.out.println("  Esperando: " + partido.getRonda() + " - Equipo incompleto");
                    }
                }
            }

            // Actualizar llave después de cada ronda de simulaciones
            if (huboCambios) {
                actualizarLlave();
            }

        } while (huboCambios && intentos < maxIntentos);

        System.out.println("Simulación completada después de " + intentos + " intentos");
    }

    public boolean isTorneoTerminado() {
        BinaryTreeNode<Partido> raiz = (BinaryTreeNode<Partido>) arbol.getRoot();
        if (raiz == null) return false;

        Partido partidoFinal = raiz.getInfo();
        return partidoFinal.isJugado();
    }

    public Club getCampeon() {
        if (isTorneoTerminado()) {
            BinaryTreeNode<Partido> raiz = (BinaryTreeNode<Partido>) arbol.getRoot();
            Partido partidoFinal = raiz.getInfo();
            Club campeon = partidoFinal.getGanador();
            if (campeon != null) {
                System.out.println("¡Campeón del torneo '" + nombreTorneo + "': " + campeon.getNombre() + "!");
            }
            return campeon;
        }
        return null;
    }

    public void imprimirArbol() {
        System.out.println("=== " + nombreTorneo + " ===");
        imprimirRecursivo((BinaryTreeNode<Partido>) arbol.getRoot(), 0);
    }

    private void imprimirRecursivo(BinaryTreeNode<Partido> nodo, int nivel) {
        if (nodo == null) return;

        String espacios = "  ".repeat(nivel);
        Partido partido = nodo.getInfo();

        String local = (partido.getEquipoLocal() != null) ?
                partido.getEquipoLocal().getNombre() : "Por Definir";
        String visitante = (partido.getEquipoVisitante() != null) ?
                partido.getEquipoVisitante().getNombre() : "Por Definir";

        if (partido.isJugado()) {
            String ganador = (partido.getGanador() != null) ?
                    partido.getGanador().getNombre() : "Empate";
            System.out.println(espacios + partido.getRonda() + ": " +
                    local + " " + partido.getGolesLocal() + "-" +
                    partido.getGolesVisitante() + " " + visitante +
                    " → Ganador: " + ganador);
        } else {
            System.out.println(espacios + partido.getRonda() + ": " + local + " vs " + visitante);
        }

        imprimirRecursivo(nodo.getLeft(), nivel + 1);
        imprimirRecursivo(nodo.getRight(), nivel + 1);
    }

    public void mostrarEstadoDetallado() {
        System.out.println("\n=== ESTADO DETALLADO DEL TORNEO ===");
        System.out.println("Nombre: " + nombreTorneo);
        System.out.println("Total partidos: " + todosPartidos.size());

        int jugados = 0;
        for (Partido p : todosPartidos) {
            if (p.isJugado()) jugados++;
        }
        System.out.println("Partidos jugados: " + jugados + "/" + todosPartidos.size());
        System.out.println("Torneo terminado: " + isTorneoTerminado());

        if (isTorneoTerminado()) {
            Club campeon = getCampeon();
            System.out.println("Campeón: " + (campeon != null ? campeon.getNombre() : "No definido"));
        }

        System.out.println("\nLista de partidos:");
        for (Partido p : todosPartidos) {
            String estado = p.isJugado() ? "✅" : "🔄";
            String equipos = (p.getEquipoLocal() != null ? p.getEquipoLocal().getNombre() : "?") +
                    " vs " +
                    (p.getEquipoVisitante() != null ? p.getEquipoVisitante().getNombre() : "?");
            System.out.println("  " + estado + " " + p.getRonda() + ": " + equipos);
        }
    }

    public List<Partido> getTodosPartidos() {
        return new ArrayList<>(todosPartidos);
    }

    // Método para obtener el siguiente partido que se puede jugar (no necesariamente del jugador)
    public Partido getProximoPartidoJugable() {
        for (Partido partido : todosPartidos) {
            if (!partido.isJugado() &&
                    partido.getEquipoLocal() != null &&
                    partido.getEquipoVisitante() != null) {
                return partido;
            }
        }
        return null;
    }
}