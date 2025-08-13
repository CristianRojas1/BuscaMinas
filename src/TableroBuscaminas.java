/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author User
 */
public class TableroBuscaminas {

    Casilla[][] casillas;

    int numFilas;
    int numColumnas;
    int numMinas;
    int numCasillasAbiertas;
    int numCasillasMarcadas;
    boolean generacionMinas;

    private Consumer<List<Casilla>> eventoPartidaPerdida;
    private Consumer<List<Casilla>> eventoPartidaGanada;
    private Consumer<Casilla> eventoCasillaAbierta;

    public TableroBuscaminas(int numFilas, int numColumnas, int numMinas) {
        this.numFilas = numFilas;
        this.numColumnas = numColumnas;
        this.numMinas = numMinas;
        this.numCasillasAbiertas = 0;
        this.numCasillasMarcadas = 0;
        this.inicializarCasillas();
        this.generacionMinas = false;
    }

    /**
     * Inicializa todas las casillas del tablero
     */
    private void inicializarCasillas() {
        casillas = new Casilla[this.numFilas][this.numColumnas];
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas[i].length; j++) {
                casillas[i][j] = new Casilla(i, j);
            }
        }
    }

    /**
     * Genera las minas aleatoriamente evitando la primera casilla clickeada
     */
    private void generarMinas(int posFilaIgnorar, int posColumnaIgnorar) {
        int minasGeneradas = 0;
        while (minasGeneradas != numMinas) {
            int posTmpFila;
            int posTmpColumna;
            do {
                posTmpFila = (int) (Math.random() * casillas.length);
                posTmpColumna = (int) (Math.random() * casillas[0].length);
            } while ((posTmpFila == posFilaIgnorar && posTmpColumna == posColumnaIgnorar)
                    || casillas[posTmpFila][posTmpColumna].isMina());
            
            casillas[posTmpFila][posTmpColumna].setMina(true);
            minasGeneradas++;
        }
        actualizarNumeroMinasAlrededor();
        this.generacionMinas = true;
        this.imprimirTablero(); // Para debugging
    }

    /**
     * Imprime el tablero en consola (para debugging)
     */
    private void imprimirTablero() {
        System.out.println("=== TABLERO DE MINAS ===");
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas[i].length; j++) {
                System.out.print(casillas[i][j].isMina() ? "*" : "0");
            }
            System.out.println("");
        }
    }

    /**
     * Actualiza el número de minas alrededor de cada casilla
     */
    private void actualizarNumeroMinasAlrededor() {
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas[i].length; j++) {
                if (casillas[i][j].isMina()) {
                    List<Casilla> casillasAlrededor = obtenerCasillasAlrededor(i, j);
                    casillasAlrededor.forEach((c) -> c.incrementarNumeroMinasAlrededor());
                }
            }
        }
    }

    /**
     * Obtiene las casillas que rodean una posición dada
     */
    private List<Casilla> obtenerCasillasAlrededor(int posFila, int posColumna) {
        List<Casilla> listaCasillas = new LinkedList<>();
        
        // Revisar las 8 direcciones alrededor
        for (int i = 0; i < 8; i++) {
            int tmpPosFila = posFila;
            int tmpPosColumna = posColumna;
            
            switch (i) {
                case 0: tmpPosFila--; break; // Arriba
                case 1: tmpPosFila--; tmpPosColumna++; break; // Arriba Derecha
                case 2: tmpPosColumna++; break; // Derecha
                case 3: tmpPosColumna++; tmpPosFila++; break; // Derecha Abajo
                case 4: tmpPosFila++; break; // Abajo
                case 5: tmpPosFila++; tmpPosColumna--; break; // Abajo Izquierda
                case 6: tmpPosColumna--; break; // Izquierda
                case 7: tmpPosFila--; tmpPosColumna--; break; // Izquierda Arriba
            }

            // Verificar que esté dentro de los límites
            if (tmpPosFila >= 0 && tmpPosFila < this.casillas.length
                    && tmpPosColumna >= 0 && tmpPosColumna < this.casillas[0].length) {
                listaCasillas.add(this.casillas[tmpPosFila][tmpPosColumna]);
            }
        }
        return listaCasillas;
    }

    /**
     * Obtiene todas las casillas que contienen minas
     */
    List<Casilla> obtenerCasillasConMinas() {
        List<Casilla> casillasConMinas = new LinkedList<>();
        for (int i = 0; i < casillas.length; i++) {
            for (int j = 0; j < casillas[i].length; j++) {
                if (casillas[i][j].isMina()) {
                    casillasConMinas.add(casillas[i][j]);
                }
            }
        }
        return casillasConMinas;
    }

    /**
     * Maneja la selección de una casilla (clic izquierdo)
     */
    public void seleccionarCasilla(int posFila, int posColumna) {
        Casilla casilla = casillas[posFila][posColumna];
        
        // No se puede seleccionar una casilla marcada o ya abierta
        if (!casilla.puedeAbrirse()) {
            return;
        }
        
        // Generar minas en el primer clic
        if (!this.generacionMinas) {
            this.generarMinas(posFila, posColumna);
        }

        // Si es una mina, se pierde
        if (casilla.isMina()) {
            eventoPartidaPerdida.accept(obtenerCasillasConMinas());
            return;
        }

        // Abrir la casilla
        marcarCasillaAbierta(posFila, posColumna);
        eventoCasillaAbierta.accept(casilla);

        // Si no tiene minas alrededor, abrir casillas adyacentes automáticamente
        if (casilla.getNumMinasAlrededor() == 0) {
            List<Casilla> casillasAlrededor = obtenerCasillasAlrededor(posFila, posColumna);
            for (Casilla casillaVecina : casillasAlrededor) {
                if (!casillaVecina.isAbierta() && !casillaVecina.isMarcada()) {
                    seleccionarCasilla(casillaVecina.getPosFila(), casillaVecina.getPosColumna());
                }
            }
        }

        // Verificar si se ganó la partida
        if (partidaGanada()) {
            eventoPartidaGanada.accept(obtenerCasillasConMinas());
        }
    }

    /**
     * Alterna el marcado de una casilla (clic derecho)
     */
    public void alternarMarcado(int posFila, int posColumna) {
        Casilla casilla = casillas[posFila][posColumna];
        
        // Solo se puede marcar/desmarcar casillas que no estén abiertas
        if (casilla.isAbierta()) {
            return;
        }
        
        // Verificar límite de marcas (no más marcas que minas)
        if (!casilla.isMarcada() && numCasillasMarcadas >= numMinas) {
            return; // No se pueden poner más marcas
        }
        
        // Alternar el estado de marcado
        casilla.alternarMarcado();
        
        // Actualizar contador de casillas marcadas
        if (casilla.isMarcada()) {
            numCasillasMarcadas++;
        } else {
            numCasillasMarcadas--;
        }
    }

    /**
     * Marca una casilla como abierta
     */
    void marcarCasillaAbierta(int posFila, int posColumna) {
        if (!this.casillas[posFila][posColumna].isAbierta()) {
            numCasillasAbiertas++;
            this.casillas[posFila][posColumna].setAbierta(true);
        }
    }

    /**
     * Verifica si la partida ha sido ganada
     */
    boolean partidaGanada() {
        return numCasillasAbiertas >= (numFilas * numColumnas) - numMinas;
    }

    /**
     * Obtiene una casilla específica (método helper)
     */
    public Casilla getCasilla(int posFila, int posColumna) {
        return casillas[posFila][posColumna];
    }

    /**
     * Obtiene el número de casillas marcadas
     */
    public int getNumCasillasMarcadas() {
        return numCasillasMarcadas;
    }

    /**
     * Obtiene el número de minas
     */
    public int getNumMinas() {
        return numMinas;
    }

    // Métodos para configurar eventos
    public void setEventoPartidaPerdida(Consumer<List<Casilla>> eventoPartidaPerdida) {
        this.eventoPartidaPerdida = eventoPartidaPerdida;
    }

    public void setEventoCasillaAbierta(Consumer<Casilla> eventoCasillaAbierta) {
        this.eventoCasillaAbierta = eventoCasillaAbierta;
    }

    public void setEventoPartidaGanada(Consumer<List<Casilla>> eventoPartidaGanada) {
        this.eventoPartidaGanada = eventoPartidaGanada;
    }

    /**
     * Método main para testing
     */
    public static void main(String[] args) {
        TableroBuscaminas tablero = new TableroBuscaminas(5, 5, 5);
        tablero.generarMinas(0, 0);
        tablero.imprimirTablero();
        System.out.println("--- PISTAS ---");
        for (int i = 0; i < tablero.casillas.length; i++) {
            for (int j = 0; j < tablero.casillas[i].length; j++) {
                System.out.print(tablero.casillas[i][j].getNumMinasAlrededor());
            }
            System.out.println("");
        }
    }
}