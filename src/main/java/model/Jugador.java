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

    // Getters y Setters básicos (mantener por compatibilidad)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Dinero / Experiencia: usar métodos aditivos para evitar sobrescrituras accidentales
    public int getDinero() { return dinero; }
    public void setDinero(int dinero) { this.dinero = dinero; } // conservar por compatibilidad
    public void agregarDinero(int cantidad) { this.dinero += cantidad; }
    public boolean gastarDinero(int cantidad) {
        if (dinero >= cantidad) {
            dinero -= cantidad;
            return true;
        }
        return false;
    }

    public int getExperiencia() { return experiencia; }
    public void setExperiencia(int experiencia) { this.experiencia = experiencia; } // conservar por compat
    public void agregarExperiencia(int cantidad) { this.experiencia += cantidad; }
    public boolean gastarExperiencia(int cantidad) {
        if (experiencia >= cantidad) {
            experiencia -= cantidad;
            return true;
        }
        return false;
    }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    public Club getClubActual() { return clubActual; }
    public void setClubActual(Club clubActual) { this.clubActual = clubActual; }

    public int getPrecision() { return precision; }
    public void setPrecision(int precision) { this.precision = precision; }

    public int getPotencia() { return potencia; }
    public void setPotencia(int potencia) { this.potencia = potencia; }

    public int getEstrategia() { return estrategia; }
    public void setEstrategia(int estrategia) { this.estrategia = estrategia; }

    public void mejorarHabilidad(String tipo, int incremento) {
        switch (tipo) {
            case "precision": precision += incremento; break;
            case "potencia": potencia += incremento; break;
            case "estrategia": estrategia += incremento; break;
        }
    }
}