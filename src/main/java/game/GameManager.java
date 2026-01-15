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
    private int llamadasJugarPartido = 0;
    private static final int MAX_LLAMADAS = 5;

    public GameManager(Jugador jugador) {
        this.jugador = jugador;
        this.arbolHabilidades = new ArbolHabilidades();
        this.torneosDisponibles = new ArrayList<>();
        this.llamadasJugarPartido = 0;
        inicializarTorneos();
    }

    private void inicializarTorneos() {
        // Crear torneos
        Torneo torneoLocal = new Torneo("Torneo Local", 1, 1000);
        Torneo copaRegional = new Torneo("Copa Regional", 3, 2500);
        Torneo ligaNacional = new Torneo("Liga Nacional", 5, 5000);
        Torneo championsCup = new Torneo("Champions Cup", 8, 10000);

        // Añadir torneos a la lista
        torneosDisponibles.add(torneoLocal);
        torneosDisponibles.add(copaRegional);
        torneosDisponibles.add(ligaNacional);
        torneosDisponibles.add(championsCup);

        // Inicializar equipos para cada torneo
        inicializarEquiposTorneo(torneoLocal, 1);
        inicializarEquiposTorneo(copaRegional, 2);
        inicializarEquiposTorneo(ligaNacional, 4);
        inicializarEquiposTorneo(championsCup, 7);
    }

    private void inicializarEquiposTorneo(Torneo torneo, int nivelBase) {
        if (torneo.getNombre().equals("Torneo Local")) {
            torneo.agregarClub(new Club("Equipo Local", nivelBase, 500));
            torneo.agregarClub(new Club("Club Deportivo", nivelBase + 1, 800));
            torneo.agregarClub(new Club("Asociación Deportiva", nivelBase + 2, 1200));
            torneo.agregarClub(new Club("Real Club", nivelBase + 3, 2000));
        } else if (torneo.getNombre().equals("Copa Regional")) {
            torneo.agregarClub(new Club("Regional FC", nivelBase, 700));
            torneo.agregarClub(new Club("Ciudad Deportiva", nivelBase + 1, 1000));
            torneo.agregarClub(new Club("Metro Club", nivelBase + 2, 1500));
            torneo.agregarClub(new Club("Estrella Roja", nivelBase + 3, 2200));
        } else if (torneo.getNombre().equals("Liga Nacional")) {
            torneo.agregarClub(new Club("Nacional FC", nivelBase, 1200));
            torneo.agregarClub(new Club("Capital United", nivelBase + 1, 1800));
            torneo.agregarClub(new Club("Provincia FC", nivelBase + 2, 2500));
            torneo.agregarClub(new Club("Globetrotters", nivelBase + 3, 3200));
        } else if (torneo.getNombre().equals("Champions Cup")) {
            torneo.agregarClub(new Club("Champions FC", nivelBase, 2000));
            torneo.agregarClub(new Club("Euro Giants", nivelBase + 1, 3000));
            torneo.agregarClub(new Club("World Stars", nivelBase + 2, 4000));
            torneo.agregarClub(new Club("Galácticos", nivelBase + 3, 5000));
        }
    }

    public boolean iniciarTorneo(String nombreTorneo) {
        for (Torneo torneo : torneosDisponibles) {
            if (torneo.getNombre().equals(nombreTorneo) && torneo.puedeParticipar(jugador)) {
                this.torneoActual = torneo;

                // Crear copia de los clubes para evitar problemas de referencia
                List<Club> clubsList = new ArrayList<>(torneo.getClubsParticipantes());
                Club[] clubsArray = clubsList.toArray(new Club[0]);

                // Crear árbol del torneo
                this.arbolTorneoActual = new ArbolTorneo(torneo.getNombre(), clubsArray);

                // Asignar el primer club al jugador si no tiene uno
                if (jugador.getClubActual() == null && !clubsList.isEmpty()) {
                    jugador.setClubActual(clubsList.get(0));
                    System.out.println("Jugador asignado al club: " + clubsList.get(0).getNombre());
                } else if (jugador.getClubActual() != null) {
                    // Verificar que el club del jugador esté en el torneo
                    boolean clubEnTorneo = false;
                    for (Club club : clubsList) {
                        if (club.getNombre().equals(jugador.getClubActual().getNombre())) {
                            clubEnTorneo = true;
                            break;
                        }
                    }
                    if (!clubEnTorneo && !clubsList.isEmpty()) {
                        jugador.setClubActual(clubsList.get(0));
                        System.out.println("Club actualizado: " + clubsList.get(0).getNombre());
                    }
                }

                // Mostrar estado inicial
                System.out.println("Torneo iniciado: " + torneo.getNombre());
                System.out.println("Equipos participantes: " + clubsList.size());

                return true;
            }
        }
        return false;
    }

    public void jugarPartidoActual() {
        // Contador de seguridad
        llamadasJugarPartido++;

        if (llamadasJugarPartido > MAX_LLAMADAS) {
            System.err.println("¡BUCLE DETECTADO! Deteniendo ejecución de jugarPartidoActual()");
            llamadasJugarPartido = 0;
            return;
        }

        if (arbolTorneoActual == null) {
            System.out.println("No hay torneo activo");
            llamadasJugarPartido = 0;
            return;
        }

        // Verificar si el torneo ya terminó
        if (arbolTorneoActual.isTorneoTerminado()) {
            System.out.println("El torneo ya terminó.");
            completarTorneo();
            llamadasJugarPartido = 0;
            return;
        }

        // Verificar si el jugador ya está eliminado ANTES de buscar partidos
        if (isJugadorEliminadoVerificado()) {
            System.out.println("El jugador ya fue eliminado. No hay más partidos.");
            llamadasJugarPartido = 0;
            return; // ¡SALIR INMEDIATAMENTE!
        }

        Partido partidoActual = arbolTorneoActual.getPartidoActual(jugador);

        if (partidoActual == null) {
            System.out.println("No hay partido disponible para el jugador en este momento");
            llamadasJugarPartido = 0;
            return;
        }

        if (partidoActual.isJugado()) {
            System.out.println("El partido ya fue jugado");
            llamadasJugarPartido = 0;
            return;
        }

        System.out.println("Jugando partido: " + partidoActual);
        partidoActual.jugarPartido(jugador);

        // Actualizar el árbol
        arbolTorneoActual.actualizarLlave();

        // Verificar si el jugador perdió
        if (partidoActual.getGanador() != null &&
                !partidoActual.getGanador().equals(jugador.getClubActual())) {
            System.out.println("¡El jugador ha sido eliminado del torneo!");
            // NO marcar aquí, dejar que isJugadorEliminadoVerificado lo detecte
        } else if (partidoActual.getGanador() != null &&
                partidoActual.getGanador().equals(jugador.getClubActual())) {
            System.out.println("¡Partido ganado! Avanzando a siguiente ronda...");
        }

        // Verificar si el torneo terminó
        if (arbolTorneoActual.isTorneoTerminado()) {
            completarTorneo();
        }

        llamadasJugarPartido = 0;
    }

    // Método mejorado para verificar eliminación
    private boolean isJugadorEliminadoVerificado() {
        if (arbolTorneoActual == null || jugador.getClubActual() == null) {
            return true;
        }

        // Si el torneo terminó, verificar si el jugador es campeón
        if (arbolTorneoActual.isTorneoTerminado()) {
            Club campeon = arbolTorneoActual.getCampeon();
            return campeon == null || !campeon.equals(jugador.getClubActual());
        }

        // Buscar si el jugador perdió en algún partido
        for (Partido partido : arbolTorneoActual.getTodosPartidos()) {
            if (partido.isJugado()) {
                Club local = partido.getEquipoLocal();
                Club visitante = partido.getEquipoVisitante();
                Club ganador = partido.getGanador();

                // Si el jugador estaba en este partido y NO fue el ganador
                if ((local != null && local.equals(jugador.getClubActual()) ||
                        visitante != null && visitante.equals(jugador.getClubActual())) &&
                        (ganador != null && !ganador.equals(jugador.getClubActual()))) {
                    return true;
                }
            }
        }

        return false;
    }

    // Nuevos métodos auxiliares en GameManager
    private boolean verificarSiJugadorFueEliminado() {
        if (jugador.getClubActual() == null) return true;

        // Recorrer todos los partidos jugados para ver si el jugador perdió
        for (Partido partido : arbolTorneoActual.getTodosPartidos()) {
            if (partido.isJugado()) {
                Club local = partido.getEquipoLocal();
                Club visitante = partido.getEquipoVisitante();
                Club ganador = partido.getGanador();

                // Si el jugador estaba en este partido y no fue el ganador
                if ((local != null && local.equals(jugador.getClubActual()) ||
                        visitante != null && visitante.equals(jugador.getClubActual())) &&
                        (ganador != null && !ganador.equals(jugador.getClubActual()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void marcarJugadorEliminado() {
        // Puedes agregar lógica adicional aquí
        System.out.println("Jugador marcado como eliminado.");
        // Opcional: puedes setear el club actual a null o agregar un flag
        // jugador.setClubActual(null);
    }

    public boolean isJugadorEliminado() {
        if (arbolTorneoActual == null || jugador.getClubActual() == null) {
            return true;
        }

        // Si el torneo terminó
        if (arbolTorneoActual.isTorneoTerminado()) {
            Club campeon = arbolTorneoActual.getCampeon();
            return campeon == null || !campeon.equals(jugador.getClubActual());
        }

        // Buscar partidos donde el jugador participó y perdió
        for (Partido partido : arbolTorneoActual.getTodosPartidos()) {
            if (partido.isJugado()) {
                boolean jugadorEnPartido =
                        (partido.getEquipoLocal() != null && partido.getEquipoLocal().equals(jugador.getClubActual())) ||
                                (partido.getEquipoVisitante() != null && partido.getEquipoVisitante().equals(jugador.getClubActual()));

                if (jugadorEnPartido &&
                        partido.getGanador() != null &&
                        !partido.getGanador().equals(jugador.getClubActual())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void simularRestoTorneoDesdeInterfaz() {
        if (arbolTorneoActual != null && !arbolTorneoActual.isTorneoTerminado()) {
            System.out.println("Simulando partidos restantes del torneo...");
            arbolTorneoActual.simularPartidosPendientes();

            if (arbolTorneoActual.isTorneoTerminado()) {
                completarTorneo();
            }
        }
    }

    public void procesarVictoria(Partido partido) {
        if (partido != null && partido.getGanador() != null &&
                partido.getGanador().equals(jugador.getClubActual())) {

            int recompensaPartido = 200;
            jugador.agregarDinero(recompensaPartido);
            jugador.agregarExperiencia(50);

            System.out.println("¡Partido ganado! Recompensa: $" + recompensaPartido + " + 50 XP");
        } else {
            System.out.println("Partido perdido o no hay ganador definido");
        }
    }

    public void completarTorneo() {
        if (torneoActual != null && arbolTorneoActual != null &&
                arbolTorneoActual.isTorneoTerminado()) {

            Club campeon = arbolTorneoActual.getCampeon();
            System.out.println("\n=== TORNEO TERMINADO ===");
            System.out.println("Torneo: " + torneoActual.getNombre());
            System.out.println("Campeón: " + (campeon != null ? campeon.getNombre() : "No definido"));

            if (campeon != null && campeon.equals(jugador.getClubActual())) {
                int recompensaTorneo = torneoActual.getRecompensa();
                int experienciaTorneo = 500;

                jugador.agregarDinero(recompensaTorneo);
                jugador.agregarExperiencia(experienciaTorneo);
                torneoActual.completarTorneo();

                System.out.println("¡HAS GANADO EL TORNEO!");
                System.out.println("Recompensa total: $" + recompensaTorneo + " + " + experienciaTorneo + " XP");
            } else {
                System.out.println("No ganaste el torneo. ¡Sigue intentando!");
            }
        }
    }

    public void avanzarTorneo() {
        if (arbolTorneoActual != null) {
            System.out.println("Forzando avance del torneo...");
            int partidosJugados = 0;
            int maxPartidos = 10;

            while (!arbolTorneoActual.isTorneoTerminado() && partidosJugados < maxPartidos) {
                Partido partido = arbolTorneoActual.getProximoPartidoJugable();

                if (partido != null && !partido.isJugado()) {
                    boolean jugadorEnPartido = (partido.getEquipoLocal() != null &&
                            partido.getEquipoLocal().equals(jugador.getClubActual())) ||
                            (partido.getEquipoVisitante() != null &&
                                    partido.getEquipoVisitante().equals(jugador.getClubActual()));

                    if (jugadorEnPartido) {
                        // Solo jugar si el jugador está en el partido
                        jugarPartidoActual();
                    } else {
                        // Simular partidos de otros equipos
                        System.out.println("Simulando partido de otros equipos: " + partido);
                        partido.simularPartido();
                        arbolTorneoActual.actualizarLlave();
                    }
                    partidosJugados++;

                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                } else {
                    break;
                }
            }

            System.out.println("Avance completado. " + partidosJugados + " partidos jugados/simulados.");

            if (arbolTorneoActual.isTorneoTerminado()) {
                completarTorneo();
            }
        }
    }

    public void simularPartidosOtrosEquipos() {
        if (arbolTorneoActual != null && !arbolTorneoActual.isTorneoTerminado()) {
            System.out.println("Simulando partidos de otros equipos...");

            Partido partidoSimular = arbolTorneoActual.getProximoPartidoJugable();
            int simulados = 0;

            while (partidoSimular != null && simulados < 5) {
                boolean involucraJugador =
                        (partidoSimular.getEquipoLocal() != null &&
                                partidoSimular.getEquipoLocal().equals(jugador.getClubActual())) ||
                                (partidoSimular.getEquipoVisitante() != null &&
                                        partidoSimular.getEquipoVisitante().equals(jugador.getClubActual()));

                if (!involucraJugador && !partidoSimular.isJugado()) {
                    partidoSimular.simularPartido();
                    simulados++;
                    System.out.println("  Simulado: " + partidoSimular);
                    arbolTorneoActual.actualizarLlave();
                }

                partidoSimular = arbolTorneoActual.getProximoPartidoJugable();
            }

            System.out.println(simulados + " partidos de otros equipos simulados.");
        }
    }

    public void mostrarEstadoTorneo() {
        if (arbolTorneoActual != null) {
            System.out.println("\n=== ESTADO DEL TORNEO ===");
            arbolTorneoActual.imprimirArbol();

            Partido proximoPartido = arbolTorneoActual.getPartidoActual(jugador);
            if (proximoPartido != null) {
                System.out.println("\nPróximo partido del jugador:");
                System.out.println("  " + proximoPartido);
            } else if (arbolTorneoActual.isTorneoTerminado()) {
                Club campeon = arbolTorneoActual.getCampeon();
                System.out.println("\n¡Torneo terminado! Campeón: " +
                        (campeon != null ? campeon.getNombre() : "No definido"));
            } else {
                System.out.println("\nNo hay partido próximo para el jugador");
            }
        } else {
            System.out.println("No hay torneo activo");
        }
    }

    // Getters
    public Jugador getJugador() {
        return jugador;
    }

    public List<Torneo> getTorneosDisponibles() {
        return new ArrayList<>(torneosDisponibles);
    }

    public Torneo getTorneoActual() {
        return torneoActual;
    }

    public ArbolTorneo getArbolTorneoActual() {
        return arbolTorneoActual;
    }

    public ArbolHabilidades getArbolHabilidades() {
        return arbolHabilidades;
    }

    public void simularRestoTorneo() {
        if (arbolTorneoActual == null) {
            System.out.println("No hay torneo activo para simular");
            return;
        }

        if (arbolTorneoActual.isTorneoTerminado()) {
            System.out.println("El torneo ya terminó");
            completarTorneo();
            return;
        }

        System.out.println("=== SIMULANDO RESTO DEL TORNEO ===");
        System.out.println("Torneo: " + (torneoActual != null ? torneoActual.getNombre() : "Desconocido"));

        int partidosSimulados = 0;
        int maxSimulaciones = 20; // Límite de seguridad

        // Primero, verificar si el jugador aún está en el torneo
        Partido partidoJugador = arbolTorneoActual.getPartidoActual(jugador);
        boolean jugadorEliminado = (partidoJugador == null);

        if (jugadorEliminado) {
            System.out.println("El jugador ha sido eliminado. Simulando el resto del torneo...");
        } else {
            System.out.println("El jugador aún está en el torneo. Jugando su partido primero...");
        }

        // Simular todos los partidos pendientes
        while (!arbolTorneoActual.isTorneoTerminado() && partidosSimulados < maxSimulaciones) {
            // Obtener el próximo partido jugable
            Partido proximoPartido = arbolTorneoActual.getProximoPartidoJugable();

            if (proximoPartido == null) {
                System.out.println("No hay más partidos jugables en este momento");
                break;
            }

            // Verificar si este es el partido del jugador
            boolean esPartidoJugador = (proximoPartido.getEquipoLocal() != null &&
                    proximoPartido.getEquipoLocal().equals(jugador.getClubActual())) ||
                    (proximoPartido.getEquipoVisitante() != null &&
                            proximoPartido.getEquipoVisitante().equals(jugador.getClubActual()));

            if (esPartidoJugador) {
                // Es el partido del jugador, jugarlo normalmente
                System.out.println("Jugando partido del jugador: " + proximoPartido);
                proximoPartido.jugarPartido(jugador);
            } else {
                // Es un partido de otros equipos, simularlo
                System.out.println("Simulando partido: " + proximoPartido);
                proximoPartido.simularPartido();
            }

            partidosSimulados++;

            // Actualizar el árbol después de cada partido
            arbolTorneoActual.actualizarLlave();

            // Mostrar progreso
            System.out.println("  Partido " + partidosSimulados + " completado");

            // Pequeña pausa para efecto visual (opcional)
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("=====================================");
        System.out.println("Simulación completada.");
        System.out.println("Total partidos simulados: " + partidosSimulados);
        System.out.println("Torneo terminado: " + arbolTorneoActual.isTorneoTerminado());

        if (arbolTorneoActual.isTorneoTerminado()) {
            completarTorneo();
        } else {
            System.out.println("El torneo no pudo completarse completamente");
            mostrarEstadoTorneo();
        }
    }

    public boolean isJugadorEnTorneo() {
        if (arbolTorneoActual == null || jugador.getClubActual() == null) {
            return false;
        }

        // Buscar si el club del jugador está en algún partido pendiente
        for (Partido partido : arbolTorneoActual.getTodosPartidos()) {
            if (!partido.isJugado()) {
                Club local = partido.getEquipoLocal();
                Club visitante = partido.getEquipoVisitante();

                if ((local != null && local.equals(jugador.getClubActual())) ||
                        (visitante != null && visitante.equals(jugador.getClubActual()))) {
                    return true;
                }
            }
        }

        return false;
    }



    public void diagnosticarProblema() {
        System.out.println("\n=== DIAGNÓSTICO DEL TORNEO ===");
        System.out.println("Torneo activo: " + (torneoActual != null ? torneoActual.getNombre() : "null"));
        System.out.println("Ábol activo: " + (arbolTorneoActual != null ? "Sí" : "No"));
        System.out.println("Jugador: " + jugador.getNombre());
        System.out.println("Club actual: " + (jugador.getClubActual() != null ? jugador.getClubActual().getNombre() : "null"));
        System.out.println("¿Torneo terminado?: " + (arbolTorneoActual != null ? arbolTorneoActual.isTorneoTerminado() : "N/A"));
        System.out.println("¿Jugador eliminado?: " + isJugadorEliminado());
        System.out.println("Llamadas a jugarPartidoActual: " + llamadasJugarPartido);

        if (arbolTorneoActual != null) {
            System.out.println("\nPartidos del torneo:");
            for (Partido p : arbolTorneoActual.getTodosPartidos()) {
                System.out.println("  " + p + " - Jugado: " + p.isJugado());
            }
        }
        System.out.println("=================================\n");
    }

}