package model;

public class Partido {
    private String ronda;
    private Club equipoLocal;
    private Club equipoVisitante;
    private boolean jugado;
    private Club ganador;
    private int golesLocal;
    private int golesVisitante;

    public Partido(String ronda, Club equipoLocal, Club equipoVisitante) {
        this.ronda = ronda;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.jugado = false;
        this.ganador = null;
        this.golesLocal = 0;
        this.golesVisitante = 0;
    }

    public Partido(String tandaDePenaltis, String s, String rival) {
    }

    // Getters
    public String getRonda() { return ronda; }
    public Club getEquipoLocal() { return equipoLocal; }
    public Club getEquipoVisitante() { return equipoVisitante; }
    public boolean isJugado() { return jugado; }
    public Club getGanador() { return ganador; }
    public int getGolesLocal() { return golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }

    // Método para simular el partido
    public void jugarPartido(Jugador jugador) {
        if (jugado) return; // Si ya se jugó, no hacer nada

        this.jugado = true;

        // Verificar si el jugador está en alguno de los equipos
        boolean jugadorEnLocal = equipoLocal != null && equipoLocal.equals(jugador.getClubActual());
        boolean jugadorEnVisitante = equipoVisitante != null && equipoVisitante.equals(jugador.getClubActual());

        if (jugadorEnLocal || jugadorEnVisitante) {
            // El jugador participa en este partido - simular con sus habilidades
            simularPartidoConJugador(jugador, jugadorEnLocal);
        } else {
            // Simular partido entre IA
            simularPartidoIA();
        }
    }

    private void simularPartidoConJugador(Jugador jugador, boolean esLocal) {
        // Calcular probabilidad de ganar basado en habilidades del jugador
        double probabilidadGanar = calcularProbabilidadGanar(jugador);

        // Generar resultado basado en probabilidad
        if (Math.random() < probabilidadGanar) {
            // Jugador gana
            this.ganador = jugador.getClubActual();
            if (esLocal) {
                this.golesLocal = 2 + (int)(Math.random() * 3); // 2-4 goles
                this.golesVisitante = (int)(Math.random() * 2); // 0-1 goles
            } else {
                this.golesVisitante = 2 + (int)(Math.random() * 3);
                this.golesLocal = (int)(Math.random() * 2);
            }

            // Recompensa por ganar
            int recompensa = (equipoLocal != null ? equipoLocal.getRecompensaBase() : 0) +
                    (equipoVisitante != null ? equipoVisitante.getRecompensaBase() : 0);
            jugador.agregarDinero(recompensa);
            jugador.agregarExperiencia(50); // XP por ganar

        } else {
            // Jugador pierde
            Club oponente = esLocal ? equipoVisitante : equipoLocal;
            this.ganador = oponente;

            if (esLocal) {
                this.golesLocal = (int)(Math.random() * 2); // 0-1 goles
                this.golesVisitante = 2 + (int)(Math.random() * 3); // 2-4 goles
            } else {
                this.golesVisitante = (int)(Math.random() * 2);
                this.golesLocal = 2 + (int)(Math.random() * 3);
            }

            // Pequeña recompensa por participar
            jugador.agregarExperiencia(20);
        }
    }

    private double calcularProbabilidadGanar(Jugador jugador) {
        // Base 50% + bonificaciones por habilidades
        double probabilidad = 0.5;

        // Bonificación por precisión (0-15%)
        probabilidad += (jugador.getPrecision() - 50) * 0.003;

        // Bonificación por potencia (0-15%)
        probabilidad += (jugador.getPotencia() - 50) * 0.003;

        // Bonificación por estrategia (0-15%)
        probabilidad += (jugador.getEstrategia() - 50) * 0.003;

        // Bonificación por nivel (0-10%)
        probabilidad += (jugador.getNivel() - 1) * 0.02;

        return Math.max(0.1, Math.min(0.9, probabilidad)); // Entre 10% y 90%
    }

    private void simularPartidoIA() {
        // Simulación simple entre dos equipos IA
        this.golesLocal = (int)(Math.random() * 4); // 0-3 goles
        this.golesVisitante = (int)(Math.random() * 4); // 0-3 goles

        if (golesLocal > golesVisitante) {
            this.ganador = equipoLocal;
        } else if (golesVisitante > golesLocal) {
            this.ganador = equipoVisitante;
        } else {
            // Empate - ganador aleatorio
            this.ganador = Math.random() < 0.5 ? equipoLocal : equipoVisitante;
            // Ajustar goles para que haya un ganador
            if (this.ganador == equipoLocal) {
                this.golesLocal++;
            } else {
                this.golesVisitante++;
            }
        }
    }

    @Override
    public String toString() {
        String localNombre = equipoLocal != null ? equipoLocal.getNombre() : "Por Definir";
        String visitanteNombre = equipoVisitante != null ? equipoVisitante.getNombre() : "Por Definir";

        if (!jugado) {
            return ronda + ": " + localNombre + " vs " + visitanteNombre;
        } else {
            String ganadorNombre = ganador != null ? ganador.getNombre() : "Empate";
            return ronda + ": " + localNombre + " " + golesLocal + "-" +
                    golesVisitante + " " + visitanteNombre + " → Ganador: " + ganadorNombre;
        }
    }
}