/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    // Cambia los valores según tu configuración
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BuscaminasDB;encrypt=false;";
    private static final String USUARIO = "CristianRA"; // o tu usuario de SQL Server
    private static final String CONTRASENA = "123456"; // tu contraseña

    public static Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("✅ Conexión exitosa a SQL Server.");
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
        }
        return conexion;
    }
}