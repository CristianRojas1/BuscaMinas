/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstadisticasDAO {

    // Insertar estadísticas en la BD
    public void guardarEstadistica(int jugados, int ganados, int perdidos) {
        String sql = "INSERT INTO Estadisticas (JuegosJugados, JuegosGanados, JuegosPerdidos) VALUES (?, ?, ?)";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jugados);
            stmt.setInt(2, ganados);
            stmt.setInt(3, perdidos);
            stmt.executeUpdate();
            System.out.println("📌 Estadística guardada en la base de datos.");

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar estadística: " + e.getMessage());
        }
    }

    // Obtener última estadística registrada
    public void mostrarUltimaEstadistica() {
        String sql = "SELECT TOP 1 * FROM Estadisticas ORDER BY IdEstadistica DESC";
        try (Connection conn = ConexionSQL.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("📊 Última estadística registrada:");
                System.out.println("ID: " + rs.getInt("IdEstadistica"));
                System.out.println("Fecha: " + rs.getTimestamp("FechaRegistro"));
                System.out.println("Jugados: " + rs.getInt("JuegosJugados"));
                System.out.println("Ganados: " + rs.getInt("JuegosGanados"));
                System.out.println("Perdidos: " + rs.getInt("JuegosPerdidos"));
            } else {
                System.out.println("No hay registros.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al leer estadística: " + e.getMessage());
        }
    }
public modelo.Estadisticas cargarEstadisticasTotales() {
    String sql = "SELECT SUM(JuegosJugados) as TotalJugados, " +
                "SUM(JuegosGanados) as TotalGanados, " +
                "SUM(JuegosPerdidos) as TotalPerdidos " +
                "FROM Estadisticas";
    
    modelo.Estadisticas stats = new modelo.Estadisticas();
    
    try (Connection conn = ConexionSQL.conectar();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            // Usar setters para cargar los valores
            stats.setJuegosJugados(rs.getInt("TotalJugados"));
            stats.setJuegosGanados(rs.getInt("TotalGanados"));
            stats.setJuegosPerdidos(rs.getInt("TotalPerdidos"));
            
            System.out.println("📊 Estadísticas cargadas desde BD:");
            System.out.println("Jugados: " + stats.getJuegosJugados());
            System.out.println("Ganados: " + stats.getJuegosGanados());
            System.out.println("Perdidos: " + stats.getJuegosPerdidos());
        }

    } catch (SQLException e) {
        System.err.println("❌ Error al cargar estadísticas: " + e.getMessage());
        // Si hay error, devolver estadísticas en 0 (valores por defecto)
    }
    
    return stats;
}
}