/**
 *
 * @author BELSOFT
 */
public class Casilla {
    private int posFila;
    private int posColumna;
    private boolean mina;
    private int numMinasAlrededor;
    private boolean abierta;
    private boolean marcada; // NUEVO: para las banderitas

    public Casilla(int posFila, int posColumna) {
        this.posFila = posFila;
        this.posColumna = posColumna;
        this.mina = false;
        this.numMinasAlrededor = 0;
        this.abierta = false;
        this.marcada = false; // Inicializar como no marcada
    }

    // Getters y Setters existentes
    public int getPosFila() {
        return posFila;
    }

    public void setPosFila(int posFila) {
        this.posFila = posFila;
    }

    public int getPosColumna() {
        return posColumna;
    }

    public void setPosColumna(int posColumna) {
        this.posColumna = posColumna;
    }

    public boolean isMina() {
        return mina;
    }

    public void setMina(boolean mina) {
        this.mina = mina;
    }

    public int getNumMinasAlrededor() {
        return numMinasAlrededor;
    }

    public void setNumMinasAlrededor(int numMinasAlrededor) {
        this.numMinasAlrededor = numMinasAlrededor;
    }
    
    public void incrementarNumeroMinasAlrededor(){
        this.numMinasAlrededor++;
    }

    public boolean isAbierta() {
        return abierta;
    }

    public void setAbierta(boolean abierta) {
        this.abierta = abierta;
    }

    // NUEVOS: Métodos para el marcado
    public boolean isMarcada() {
        return marcada;
    }

    public void setMarcada(boolean marcada) {
        this.marcada = marcada;
    }
    
    /**
     * Alterna el estado de marcado de la casilla
     */
    public void alternarMarcado() {
        this.marcada = !this.marcada;
    }
    
    /**
     * Verifica si la casilla se puede seleccionar (abrir)
     * Una casilla marcada no se puede abrir
     */
    public boolean puedeAbrirse() {
        return !abierta && !marcada;
    }
}