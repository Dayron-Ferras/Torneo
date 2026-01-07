package game;

import model.*;
import tree.ArbolHabilidades;
import tree.ArbolTorneo;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private Jugador jugador;
    private List<Torneo> torneosDisponibles;
    private Torneo torneoActual;
    private ArbolTorneo arbolTorneoActual;
    private ArbolHabilidades arbolHabilidades;

    public GameManager(Jugador jugador) {
        this.jugador = jugador;
        this.arbolHabilidades = new ArbolHabilidades();
        this.torneosDisponibles = new ArrayList<>();
        inicializarTorneos();
    }

    private void inicializarTorneos() {
        torneosDisponibles.add(new Torneo("Torneo Local", 1, 1000));
        torneosDisponibles.add(new Torneo("Copa Regional", 3, 2500));
        torneosDisponibles.add(new Torneo("Liga Nacional", 5, 5000));
        torneosDisponibles.add(new Torneo("Champions Cup", 8, 10000));

        for (Torneo torneo : torneosDisponibles) {
            if (torneo.getNombre().equals("Torneo Local")) {
                torneo.agregarClub(new Club("Equipo Local", 1, 500));
                torneo.agregarClub(new Club("Club Deportivo", 2, 800));
                torneo.agregarClub(new Club("Asociación Deportiva", 3, 1200));
                torneo.agregarClub(new Club("Real Club", 4, 2000));
            } else if (torneo.getNombre().equals("Copa Regional")) {
                torneo.agregarClub(new Club("Regional FC", 2, 700));
                torneo.agregarClub(new Club("Ciudad Deportiva", 3, 1000));
                torneo.agregarClub(new Club("Metro Club", 4, 1500));
                torneo.agregarClub(new Club("Estrella Roja", 5, 2200));
            } else if (torneo.getNombre().equals("Liga Nacional")) {
                torneo.agregarClub(new Club("Nacional FC", 4, 1200));
                torneo.agregarClub(new Club("Capital United", 5, 1800));
                torneo.agregarClub(new Club("Provincia FC", 6, 2500));
                torneo.agregarClub(new Club("Globetrotters", 7, 3200));
            } else if (torneo.getNombre().equals("Champions Cup")) {
                torneo.agregarClub(new Club("Champions FC", 7, 2000));
                torneo.agregarClub(new Club("Euro Giants", 8, 3000));
                torneo.agregarClub(new Club("World Stars", 9, 4000));
                torneo.agregarClub(new Club("Galácticos", 10, 5000));
            }
        }
    }

    public boolean iniciarTorneo(String nombreTorneo) {
        for (Torneo torneo : torneosDisponibles) {
            if (torneo.getNombre().equals(nombreTorneo) && torneo.puedeParticipar(jugador)) {
                this.torneoActual = torneo;

                List<Club> clubsList = torneo.getClubsParticipantes();
                Club[] clubsArray = clubsList.toArray(new Club[0]);

                this.arbolTorneoActual = new ArbolTorneo(nombreTorneo, clubsArray);

                if (!clubsList.isEmpty()) {
                    jugador.setClubActual(clubsList.get(0));
                }

                return true;
            }
        }
        return false;
    }

    public void jugarPartidoActual() {
        if (arbolTorneoActual != null) {
            Partido partidoActual = arbolTorneoActual.getPartidoActual(jugador);
            if (partidoActual != null && !partidoActual.isJugado()) {
                partidoActual.jugarPartido(jugador);
                arbolTorneoActual.actualizarLlave();

                if (arbolTorneoActual.isTorneoTerminado()) {
                    completarTorneo();
                }
            }
        }
    }

    public void procesarVictoria(Partido partido) {
        System.out.println("¡Partido ganado! Recompensas aplicadas automáticamente.");
    }

    public void completarTorneo() {
        if (torneoActual != null && arbolTorneoActual != null &&
                arbolTorneoActual.isTorneoTerminado()) {

            Club campeon = arbolTorneoActual.getCampeon();
            if (campeon != null && campeon.equals(jugador.getClubActual())) {
                jugador.agregarDinero(torneoActual.getRecompensa());
                jugador.agregarExperiencia(500);

                System.out.println("¡Has ganado el " + torneoActual.getNombre() + "!");
                System.out.println("Recompensa: $" + torneoActual.getRecompensa());

                torneoActual.completarTorneo();
            }
        }
    }
    // En GameManager.java
    public void avanzarTorneo() {
        if (arbolTorneoActual != null) {
            // Forzar actualización de todos los partidos
            for (int i = 0; i < 10; i++) { // Intentar varias veces
                jugarPartidoActual();
                if (arbolTorneoActual.isTorneoTerminado()) {
                    break;
                }
            }
        }
    }
    public void mostrarEstadoTorneo() {
        if (arbolTorneoActual != null) {
            arbolTorneoActual.imprimirArbol();

            Partido proximoPartido = arbolTorneoActual.getPartidoActual(jugador);
            if (proximoPartido != null) {
                System.out.println("\nPróximo partido: " + proximoPartido);
            } else if (arbolTorneoActual.isTorneoTerminado()) {
                Club campeon = arbolTorneoActual.getCampeon();
                System.out.println("\n¡Torneo terminado! Campeón: " +
                        (campeon != null ? campeon.getNombre() : "No definido"));
            }
        }
    }

    public Jugador getJugador() { return jugador; }
    public List<Torneo> getTorneosDisponibles() { return torneosDisponibles; }
    public Torneo getTorneoActual() { return torneoActual; }
    public ArbolTorneo getArbolTorneoActual() { return arbolTorneoActual; }
    public ArbolHabilidades getArbolHabilidades() { return arbolHabilidades; }
}