package model;

public class Habilidad {
    private String id;
    private String nombre;
    private String descripcion;
    private int costoDinero;
    private int costoExperiencia;
    private boolean desbloqueada;
    private String tipo; // "precision", "potencia", "estrategia"
    private int valorMejora;

    public Habilidad(String id, String nombre, String descripcion, int costoDinero,
                     int costoExperiencia, String tipo, int valorMejora) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoDinero = costoDinero;
        this.costoExperiencia = costoExperiencia;
        this.tipo = tipo;
        this.valorMejora = valorMejora;
        this.desbloqueada = false;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCostoDinero() {
        return costoDinero;
    }

    public void setCostoDinero(int costoDinero) {
        this.costoDinero = costoDinero;
    }

    public int getCostoExperiencia() {
        return costoExperiencia;
    }

    public void setCostoExperiencia(int costoExperiencia) {
        this.costoExperiencia = costoExperiencia;
    }

    public boolean isDesbloqueada() {
        return desbloqueada;
    }

    public void setDesbloqueada(boolean desbloqueada) {
        this.desbloqueada = desbloqueada;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getValorMejora() {
        return valorMejora;
    }

    public void setValorMejora(int valorMejora) {
        this.valorMejora = valorMejora;
    }


    public boolean puedeComprar(Jugador jugador) {
        return jugador.getDinero() >= costoDinero &&
                jugador.getExperiencia() >= costoExperiencia;
    }

    public void comprar(Jugador jugador) {
        if (puedeComprar(jugador) && !desbloqueada) {
            boolean okDinero = jugador.gastarDinero(costoDinero);
            boolean okExp = jugador.gastarExperiencia(costoExperiencia);
            if (okDinero && okExp) {
                jugador.mejorarHabilidad(tipo, valorMejora);
                desbloqueada = true;
            } else {
                // Si por alguna razón no se pudo pagar, revertir (defensivo)
                if (okDinero && !okExp) jugador.agregarDinero(costoDinero);
                if (!okDinero && okExp) jugador.agregarExperiencia(costoExperiencia);
            }
        }
    }
}