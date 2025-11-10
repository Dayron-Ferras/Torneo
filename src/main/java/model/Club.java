package model;

public class Club {
    private String nombre;
    private int nivelRequerido;
    private int recompensaBase;
    private boolean desbloqueado;

    public Club(String nombre, int nivelRequerido, int recompensaBase) {
        this.nombre = nombre;
        this.nivelRequerido = nivelRequerido;
        this.recompensaBase = recompensaBase;
        this.desbloqueado = false;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNivelRequerido() {
        return nivelRequerido;
    }

    public void setNivelRequerido(int nivelRequerido) {
        this.nivelRequerido = nivelRequerido;
    }

    public void setRecompensaBase(int recompensaBase) {
        this.recompensaBase = recompensaBase;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }

    public void desbloquear(Jugador jugador) {
        if (jugador.getNivel() >= nivelRequerido) {
            desbloqueado = true;
        }
    }

    // Getters
    public boolean isDesbloqueado() { return desbloqueado; }
    public int getRecompensaBase() { return recompensaBase; }
}