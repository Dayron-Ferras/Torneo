package model;

public class Partido {
    private String equipoLocal;
    private String equipoVisitante;
    private boolean jugado;
    private String ganador;

    public Partido(String equipoLocal, String equipoVisitante, boolean jugado, String ganador) {
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.jugado = jugado;
        this.ganador = ganador;
    }


    public String getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(String equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(String equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public boolean isJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }
}