package model;



public class Torneo {
    private String nombre;
    private int nivelRequerido;
    private int recompensa;


    public Torneo(String nombre, int nivelRequerido, int recompensa) {
        this.nombre = nombre;
        this.nivelRequerido = nivelRequerido;
        this.recompensa = recompensa;
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

    public int getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(int recompensa) {
        this.recompensa = recompensa;
    }
}