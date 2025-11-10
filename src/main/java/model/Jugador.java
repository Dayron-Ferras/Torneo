package model;

public class Jugador {
    private String nombre;
    private int dinero;
    private int experiencia;
    private int nivel;
    private Club clubActual;

    // Habilidades base
    private int precision;
    private int potencia;
    private int estrategia;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.dinero = 1000;
        this.experiencia = 0;
        this.nivel = 1;
        this.precision = 50;
        this.potencia = 50;
        this.estrategia = 50;
    }

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public Club getClubActual() {
        return clubActual;
    }

    public void setClubActual(Club clubActual) {
        this.clubActual = clubActual;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getPotencia() {
        return potencia;
    }

    public void setPotencia(int potencia) {
        this.potencia = potencia;
    }

    public int getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(int estrategia) {
        this.estrategia = estrategia;
    }

    public boolean gastarDinero(int cantidad) {
        if (dinero >= cantidad) {
            dinero -= cantidad;
            return true;
        }
        return false;
    }

    public void mejorarHabilidad(String tipo, int incremento) {
        switch (tipo) {
            case "precision": precision += incremento; break;
            case "potencia": potencia += incremento; break;
            case "estrategia": estrategia += incremento; break;
        }
    }

         }

