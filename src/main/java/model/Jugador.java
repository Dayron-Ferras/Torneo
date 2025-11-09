package model;



    public class Jugador {
        private String nombre;
        private int precision;
        private int potencia;
        private int estrategia;
        private int dinero;
        private int experiencia;
        private int nivel;

        public Jugador(String nombre) {
            this.nombre = nombre;
            this.precision = 50;
            this.potencia = 50;
            this.estrategia = 50;
            this.dinero = 1000;
            this.experiencia = 0;
            this.nivel = 0;
        }

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

        public void ganarExperiencia(int exp) {
            this.experiencia += exp;
            if (this.experiencia >= 100) {
                this.nivel++;
                this.experiencia = 0;
                System.out.println("¡Subiste al nivel " + this.nivel + "!");
            }
        }


         }

