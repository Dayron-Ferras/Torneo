package game;

import model.Jugador;
import java.util.Random;

public class PenaltyShootout {
    private static final Random random = new Random();
    private static final String[] DIRECCIONES = {"IZQUIERDA", "CENTRO", "DERECHA"};

    public static ResultadoPenaltis jugarPartido(Jugador jugador, String equipoRival) {
        int golesJugador = 0;
        int golesRival = 0;

        System.out.println("⚽ COMIENZA LA TANDA DE PENALTIS ⚽");
        System.out.println("Jugador vs " + equipoRival);

        // 5 penaltis para cada uno
        for (int i = 1; i <= 5; i++) {
            System.out.println("\n--- Penalti " + i + " ---");

            // Turno del jugador
            boolean golJugador = ejecutarPenaltiJugador(jugador);
            if (golJugador) golesJugador++;

            // Turno del rival (simulado)
            boolean golRival = ejecutarPenaltiRival();
            if (golRival) golesRival++;

            System.out.println("Marcador: " + golesJugador + " - " + golesRival);
        }

        // Desempate si hay empate
        while (golesJugador == golesRival) {
            System.out.println("\n--- PENALTI DE DESEMPATE ---");

            boolean golJugador = ejecutarPenaltiJugador(jugador);
            if (golJugador) golesJugador++;

            boolean golRival = ejecutarPenaltiRival();
            if (golRival) golesRival++;

            System.out.println("Marcador: " + golesJugador + " - " + golesRival);
        }

        boolean jugadorGana = golesJugador > golesRival;
        System.out.println("\n" + (jugadorGana ? "🎉 ¡VICTORIA!" : "💔 Derrota"));

        return new ResultadoPenaltis(jugadorGana, golesJugador, golesRival);
    }

    private static boolean ejecutarPenaltiJugador(Jugador jugador) {
        // Simular elección del jugador (en UI sería con botones)
        String direccionElegida = DIRECCIONES[random.nextInt(3)];

        // El portero elige una dirección aleatoria
        String direccionPortero = DIRECCIONES[random.nextInt(3)];

        // Calcular probabilidad de gol basada en habilidades
        double probabilidadBase = 0.7; // 70% base
        double bonusPrecision = jugador.getPrecision() * 0.002; // +0.2% por punto
        double bonusEstrategia = jugador.getEstrategia() * 0.001; // +0.1% por punto

        double probabilidadTotal = probabilidadBase + bonusPrecision + bonusEstrategia;

        // Si el jugador y portero eligen misma dirección, menos probabilidad
        if (direccionElegida.equals(direccionPortero)) {
            probabilidadTotal -= 0.4; // -40% si ataja
        }

        boolean esGol = random.nextDouble() < probabilidadTotal;

        System.out.println("Tu tiro: " + direccionElegida +
                " | Portero: " + direccionPortero +
                " | " + (esGol ? "⚽ GOL!" : "❌ Fallado"));

        return esGol;
    }

    private static boolean ejecutarPenaltiRival() {
        // Simular penalti del rival
        String direccionRival = DIRECCIONES[random.nextInt(3)];
        String direccionPorteroJugador = DIRECCIONES[random.nextInt(3)];

        boolean esGol = !direccionRival.equals(direccionPorteroJugador);

        System.out.println("Rival tira: " + direccionRival +
                " | Tu portero: " + direccionPorteroJugador +
                " | " + (esGol ? "⚽ Gol rival" : "🧤 ¡Atajado!"));

        return esGol;
    }

    public static class ResultadoPenaltis {
        private final boolean victoria;
        private final int golesJugador;
        private final int golesRival;

        public ResultadoPenaltis(boolean victoria, int golesJugador, int golesRival) {
            this.victoria = victoria;
            this.golesJugador = golesJugador;
            this.golesRival = golesRival;
        }

        // Getters
        public boolean isVictoria() { return victoria; }
        public int getGolesJugador() { return golesJugador; }
        public int getGolesRival() { return golesRival; }
    }
}