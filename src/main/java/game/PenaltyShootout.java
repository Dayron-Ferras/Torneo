package game;

import model.Jugador;

import java.util.Random;

/**
 * Clase que maneja una tanda de penaltis paso a paso.
 * La UI debe instanciarla y llamar playerShoot(Direction) para procesar el tiro del jugador,
 * y luego obtener la respuesta para animar y actualizar marcadores.
 */
public class PenaltyShootout {
    private final Random random = new Random();
    private final String[] DIRECCIONES = {"IZQUIERDA", "CENTRO", "DERECHA"};
    private final Jugador jugador;
    private final int totalKicks;
    private int playerGoals;
    private int opponentGoals;
    private int kicksTaken;

    public PenaltyShootout(Jugador jugador) {
        this(jugador, 5);
    }

    public PenaltyShootout(Jugador jugador, int totalKicks) {
        this.jugador = jugador;
        this.totalKicks = totalKicks;
        reset();
    }

    public enum Direction { LEFT, CENTER, RIGHT }

    public static class KickResult {
        public final boolean playerScored;
        public final boolean opponentScored;
        public final int playerGoals;
        public final int opponentGoals;
        public final boolean seriesFinished;

        public KickResult(boolean playerScored, boolean opponentScored, int pGoals, int oGoals, boolean finished) {
            this.playerScored = playerScored;
            this.opponentScored = opponentScored;
            this.playerGoals = pGoals;
            this.opponentGoals = oGoals;
            this.seriesFinished = finished;
        }
    }

    public void reset() {
        this.playerGoals = 0;
        this.opponentGoals = 0;
        this.kicksTaken = 0;
    }

    public KickResult playerShoot(Direction dir) {
        if (kicksTaken >= totalKicks) {
            return new KickResult(false, false, playerGoals, opponentGoals, true);
        }

        boolean playerScored = computePlayerGoal(dir);
        if (playerScored) playerGoals++;

        // Oponente tira después
        boolean opponentScored = computeOpponentGoal();
        if (opponentScored) opponentGoals++;

        kicksTaken++;

        boolean finished = kicksTaken >= totalKicks && playerGoals != opponentGoals;
        // Nota: si empate al terminar, la UI decide si ir a muerte súbita (llamar otra tanda)

        return new KickResult(playerScored, opponentScored, playerGoals, opponentGoals, finished);
    }

    private boolean computePlayerGoal(Direction dir) {
        double base = 0.7;
        double bonusPrecision = jugador.getPrecision() * 0.002; // ejemplo
        double bonusEstrategia = jugador.getEstrategia() * 0.001;
        double prob = base + bonusPrecision + bonusEstrategia;

        // Portero (simulado) elige una dirección
        String portero = DIRECCIONES[random.nextInt(3)];
        String elegido = dir == Direction.LEFT ? "IZQUIERDA" : dir == Direction.CENTER ? "CENTRO" : "DERECHA";
        if (elegido.equals(portero)) prob -= 0.4;

        prob = Math.max(0.0, Math.min(1.0, prob)); // clamp
        return random.nextDouble() < prob;
    }

    private boolean computeOpponentGoal() {
        // Se puede mejorar usando atributos del equipo rival. Por ahora base 0.6
        double base = 0.6;
        double prob = Math.max(0.0, Math.min(1.0, base));
        return random.nextDouble() < prob;
    }

    // Getters de estado
    public int getPlayerGoals() { return playerGoals; }
    public int getOpponentGoals() { return opponentGoals; }
    public int getKicksTaken() { return kicksTaken; }
    public int getTotalKicks() { return totalKicks; }
}