/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import javax.swing.JOptionPane;

public class Estadisticas {

    private int juegosJugados;
    private int juegosGanados;
    private int juegosPerdidos;

    // Constructor
    public Estadisticas() {
        this.juegosJugados = 0;
        this.juegosGanados = 0;
        this.juegosPerdidos = 0;
    }

    // Métodos para registrar resultados
    public void registrarVictoria() {
        juegosJugados++;
        juegosGanados++;
    }

    public void registrarDerrota() {
        juegosJugados++;
        juegosPerdidos++;
    }

    // Getters
    public int getJuegosJugados() {
        return juegosJugados;
    }

    public int getJuegosGanados() {
        return juegosGanados;
    }

    public int getJuegosPerdidos() {
        return juegosPerdidos;
    }

    // Mostrar estadísticas con JOptionPane
    public void mostrarEstadisticas() {
        String mensaje = "📊 Estadísticas\n"
                + "Juegos jugados: " + juegosJugados + "\n"
                + "Juegos ganados: " + juegosGanados + "\n"
                + "Juegos perdidos: " + juegosPerdidos;
        JOptionPane.showMessageDialog(null, mensaje, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }

    // Resetear estadísticas (opcional)
    public void reiniciarEstadisticas() {
        juegosJugados = 0;
        juegosGanados = 0;
        juegosPerdidos = 0;
    }
public void setJuegosJugados(int juegosJugados) {
    this.juegosJugados = juegosJugados;
}

public void setJuegosGanados(int juegosGanados) {
    this.juegosGanados = juegosGanados;
}

public void setJuegosPerdidos(int juegosPerdidos) {
    this.juegosPerdidos = juegosPerdidos;
}
}