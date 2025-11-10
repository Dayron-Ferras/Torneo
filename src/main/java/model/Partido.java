package model;

public class Partido {
    private String ronda;
    private String equipoLocal;
    private String equipoVisitante;
    private boolean jugado;
    private String ganador;
    private int golesLocal;
    private int golesVisitante;

    public Partido(String ronda, String equipoLocal, String equipoVisitante) {
        this.ronda = ronda;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.jugado = false;
        this.ganador = null;
        this.golesLocal = 0;
        this.golesVisitante = 0;
    }

    // Getters y Setters
    public String getRonda() { return ronda; }
    public String getEquipoLocal() { return equipoLocal; }
    public String getEquipoVisitante() { return equipoVisitante; }
    public boolean isJugado() { return jugado; }
    public String getGanador() { return ganador; }

    public void setResultado(String ganador, int golesLocal, int golesVisitante) {
        this.jugado = true;
        this.ganador = ganador;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
    }

    @Override
    public String toString() {
        if (!jugado) {
            return ronda + ": " + equipoLocal + " vs " + equipoVisitante;
        } else {
            return ronda + ": " + equipoLocal + " " + golesLocal + "-" +
                    golesVisitante + " " + equipoVisitante + " → Ganador: " + ganador;
        }
    }
}