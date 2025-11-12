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
        // Torneos básicos
        torneosDisponibles.add(new Torneo("Torneo Local", 1, 1000));
        torneosDisponibles.add(new Torneo("Copa Regional", 3, 2500));
        torneosDisponibles.add(new Torneo("Liga Nacional", 5, 5000));
        torneosDisponibles.add(new Torneo("Champions Cup", 8, 10000));

        // Configurar clubs para cada torneo
        for (Torneo torneo : torneosDisponibles) {
            torneo.agregarClub(new Club("Equipo Local", 1, 500));
            torneo.agregarClub(new Club("Club Deportivo", 2, 800));
            torneo.agregarClub(new Club("Asociación Deportiva", 3, 1200));
            torneo.agregarClub(new Club("Real Club", 4, 2000));
        }
    }
    public boolean iniciarTorneo(String nombreTorneo) {
        for (Torneo torneo : torneosDisponibles) {
            if (torneo.getNombre().equals(nombreTorneo) && torneo.puedeParticipar(jugador)) {
                this.torneoActual = torneo;

                // Crear equipos para el torneo
                String[] equipos = {
                        jugador.getNombre() + " FC",
                        "Real Madrid",
                        "Barcelona FC",
                        "Manchester United",
                        "Bayern Munich",
                        "Juventus FC",
                        "PSG",
                        "Liverpool FC"
                };

                this.arbolTorneoActual = new ArbolTorneo(nombreTorneo, equipos);
                return true;
            }
        }
        return false;
    }

    public void procesarVictoria(Partido partido) {
        // Recompensas por ganar partido (sumar, no sobrescribir)
        jugador.agregarDinero(200);
        jugador.agregarExperiencia(100);

        // Marcar partido como ganado (ejemplo)
        partido.setResultado(jugador.getNombre(), 3, 2);
    }

    public void completarTorneo() {
        if (torneoActual != null && !torneoActual.isCompletado()) {
            // Recompensa por ganar torneo completo
            jugador.agregarDinero(torneoActual.getRecompensa());
            jugador.agregarExperiencia(500);
            torneoActual.completarTorneo();

            System.out.println("¡Has ganado el " + torneoActual.getNombre() + "!");
            System.out.println("Recompensa: $" + torneoActual.getRecompensa());
        }
    }

    // Getters
    public Jugador getJugador() { return jugador; }
    public List<Torneo> getTorneosDisponibles() { return torneosDisponibles; }
    public Torneo getTorneoActual() { return torneoActual; }
    public ArbolTorneo getArbolTorneoActual() { return arbolTorneoActual; }

    public ArbolHabilidades getArbolHabilidades() {
        return arbolHabilidades;
    }
}