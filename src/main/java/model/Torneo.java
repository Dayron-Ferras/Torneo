package model;



import java.util.ArrayList;
import java.util.List;

public class Torneo {
    private String nombre;
    private int nivelRequerido;
    private int recompensa;
    private boolean completado;
    private List<Club> clubsParticipantes;

    public Torneo(String nombre, int nivelRequerido, int recompensa) {
        this.nombre = nombre;
        this.nivelRequerido = nivelRequerido;
        this.recompensa = recompensa;
        this.completado = false;
        this.clubsParticipantes = new ArrayList<>();
    }

    public void agregarClub(Club club) {
        clubsParticipantes.add(club);
    }

    public boolean puedeParticipar(Jugador jugador) {
        return jugador.getNivel() >= nivelRequerido;
    }

    public void completarTorneo() {
        this.completado = true;
    }

    public String getNombre() { return nombre; }
    public int getNivelRequerido() { return nivelRequerido; }
    public int getRecompensa() { return recompensa; }
    public boolean isCompletado() { return completado; }
    public List<Club> getClubsParticipantes() { return clubsParticipantes; }

    @Override
    public String toString() {
        return nombre;
    }
}