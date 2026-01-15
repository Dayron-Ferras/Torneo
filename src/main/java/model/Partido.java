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

        // Verificar que no se cree un partido inválido
        if (equipoLocal == null && equipoVisitante == null) {
            System.err.println("Advertencia: Partido creado con ambos equipos null");
        }
    }

    public Partido(String tandaDePenaltis, String s, String rival) {
        // Constructor alternativo para partidos de penalitis
        this.ronda = tandaDePenaltis;
        this.equipoLocal = new Club(s, 1, 100); // Club temporal
        this.equipoVisitante = new Club(rival, 1, 100); // Club temporal
        this.jugado = false;
        this.ganador = null;
        this.golesLocal = 0;
        this.golesVisitante = 0;
    }

    // Getters
    public String getRonda() { return ronda; }
    public Club getEquipoLocal() { return equipoLocal; }
    public Club getEquipoVisitante() { return equipoVisitante; }
    public boolean isJugado() { return jugado; }
    public Club getGanador() { return ganador; }
    public int getGolesLocal() { return golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }

    // Setters (añadidos para permitir actualización)
    public void setEquipoLocal(Club equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public void setEquipoVisitante(Club equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public void setRonda(String ronda) {
        this.ronda = ronda;
    }

    // Método para simular el partido
    public void jugarPartido(Jugador jugador) {
        if (jugado) {
            System.out.println("Partido ya fue jugado: " + this);
            return;
        }

        this.jugado = true;

        // Verificar que ambos equipos estén definidos
        if (equipoLocal == null || equipoVisitante == null) {
            System.err.println("Error: Partido con equipos no definidos");
            this.ganador = (equipoLocal != null) ? equipoLocal : equipoVisitante;
            this.golesLocal = 1;
            this.golesVisitante = 0;
            return;
        }

        // Verificar si el jugador está en alguno de los equipos
        boolean jugadorEnLocal = equipoLocal.equals(jugador.getClubActual());
        boolean jugadorEnVisitante = equipoVisitante.equals(jugador.getClubActual());

        if (jugadorEnLocal || jugadorEnVisitante) {
            // El jugador participa en este partido - simular con sus habilidades
            simularPartidoConJugador(jugador, jugadorEnLocal);
        } else {
            // Simular partido entre IA
            simularPartidoIA();
        }

        System.out.println("Resultado: " + this);
    }

    // Método para simular partido sin jugador (para simulación automática)
    public void simularPartido() {
        if (jugado) return;

        this.jugado = true;

        if (equipoLocal == null || equipoVisitante == null) {
            System.err.println("Error: No se puede simular partido con equipos null");
            return;
        }

        simularPartidoIA();
        System.out.println("Partido simulado: " + this);
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

        // Considerar diferencia de niveles entre equipos
        if (equipoLocal != null && equipoVisitante != null) {
            int diferenciaNivel = Math.abs(equipoLocal.getNivel() - equipoVisitante.getNivel());
            if (equipoLocal.equals(jugador.getClubActual())) {
                // El jugador es local
                if (equipoLocal.getNivel() > equipoVisitante.getNivel()) {
                    probabilidad += diferenciaNivel * 0.05;
                } else {
                    probabilidad -= diferenciaNivel * 0.05;
                }
            } else {
                // El jugador es visitante
                if (equipoVisitante.getNivel() > equipoLocal.getNivel()) {
                    probabilidad += diferenciaNivel * 0.05;
                } else {
                    probabilidad -= diferenciaNivel * 0.05;
                }
            }
        }

        return Math.max(0.1, Math.min(0.9, probabilidad)); // Entre 10% y 90%
    }

    private void simularPartidoIA() {
        // Simulación más realista considerando niveles de los equipos
        double probabilidadLocal = 0.5;

        if (equipoLocal != null && equipoVisitante != null) {
            // Basar probabilidad en diferencia de niveles
            int diferencia = equipoLocal.getNivel() - equipoVisitante.getNivel();
            probabilidadLocal = 0.5 + (diferencia * 0.1);
            probabilidadLocal = Math.max(0.2, Math.min(0.8, probabilidadLocal));
        }

        // Generar goles basados en probabilidad
        if (Math.random() < probabilidadLocal) {
            // Gana el local
            this.golesLocal = 1 + (int)(Math.random() * 3); // 1-3 goles
            this.golesVisitante = (int)(Math.random() * 2); // 0-1 goles
            this.ganador = equipoLocal;
        } else {
            // Gana el visitante
            this.golesLocal = (int)(Math.random() * 2); // 0-1 goles
            this.golesVisitante = 1 + (int)(Math.random() * 3); // 1-3 goles
            this.ganador = equipoVisitante;
        }

        // Posibilidad de empate (10%)
        if (Math.random() < 0.1) {
            this.golesLocal = 1 + (int)(Math.random() * 2); // 1-2 goles
            this.golesVisitante = this.golesLocal;
            // En empate, ganador aleatorio (para torneo de eliminación debe haber ganador)
            this.ganador = Math.random() < 0.5 ? equipoLocal : equipoVisitante;
            if (this.ganador == equipoLocal) {
                this.golesLocal++;
            } else {
                this.golesVisitante++;
            }
        }
    }

    // Método para comparar partidos (útil para debugging)
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Partido partido = (Partido) obj;

        // Comparar por ronda y equipos
        boolean mismaRonda = this.ronda.equals(partido.ronda);
        boolean mismosEquipos =
                ((this.equipoLocal == null && partido.equipoLocal == null) ||
                        (this.equipoLocal != null && this.equipoLocal.equals(partido.equipoLocal))) &&
                        ((this.equipoVisitante == null && partido.equipoVisitante == null) ||
                                (this.equipoVisitante != null && this.equipoVisitante.equals(partido.equipoVisitante)));

        return mismaRonda && mismosEquipos;
    }

    public int hashCode() {
        int result = ronda != null ? ronda.hashCode() : 0;
        result = 31 * result + (equipoLocal != null ? equipoLocal.hashCode() : 0);
        result = 31 * result + (equipoVisitante != null ? equipoVisitante.hashCode() : 0);
        return result;
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

    // Método para obtener información detallada del partido
    public String getInfoDetallada() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PARTIDO ===\n");
        sb.append("Ronda: ").append(ronda).append("\n");
        sb.append("Local: ").append(equipoLocal != null ? equipoLocal.getNombre() : "Por Definir");
        if (equipoLocal != null) {
            sb.append(" (Nivel ").append(equipoLocal.getNivel()).append(")");
        }
        sb.append("\n");

        sb.append("Visitante: ").append(equipoVisitante != null ? equipoVisitante.getNombre() : "Por Definir");
        if (equipoVisitante != null) {
            sb.append(" (Nivel ").append(equipoVisitante.getNivel()).append(")");
        }
        sb.append("\n");

        sb.append("Estado: ").append(jugado ? "Jugado" : "Pendiente").append("\n");

        if (jugado) {
            sb.append("Resultado: ").append(golesLocal).append("-").append(golesVisitante).append("\n");
            sb.append("Ganador: ").append(ganador != null ? ganador.getNombre() : "Empate").append("\n");
        }

        return sb.toString();
    }
}