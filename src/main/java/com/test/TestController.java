package com.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/")
    public String home() {
        return "Test JDBC SSL - Endpoints disponibles:<br>" +
               "- GET /test-connection : Probar conexion basica<br>" +
               "- GET /test-query : Ejecutar query de prueba<br>" +
               "- GET /test-ssl-info : Ver info de SSL";
    }

    @GetMapping("/test-connection")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            String dbName = conn.getMetaData().getDatabaseProductName();
            String dbVersion = conn.getMetaData().getDatabaseProductVersion();
            String url = conn.getMetaData().getURL();
            
            return String.format(
                "CONEXION EXITOSA<br><br>" +
                "Database: %s<br>" +
                "Version: %s<br>" +
                "URL: %s<br>" +
                "SSL habilitado: %s",
                dbName, dbVersion, url, 
                url.contains("ssl=true") ? "SI" : "NO"
            );
        } catch (Exception e) {
            return "ERROR DE CONEXION:<br><br>" + 
                   "Mensaje: " + e.getMessage() + "<br><br>" +
                   "Tipo: " + e.getClass().getName() + "<br><br>" +
                   "Stack trace en consola";
        }
    }

    @GetMapping("/test-query")
    public String testQuery() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version(), current_database(), current_user")) {
            
            if (rs.next()) {
                return String.format(
                    "QUERY EXITOSA<br><br>" +
                    "PostgreSQL Version: %s<br>" +
                    "Database: %s<br>" +
                    "User: %s",
                    rs.getString(1), rs.getString(2), rs.getString(3)
                );
            }
            return "No se obtuvieron resultados";
        } catch (Exception e) {
            return "ERROR EN QUERY:<br><br>" + e.getMessage();
        }
    }

    @GetMapping("/test-ssl-info")
    public String testSslInfo() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ssl_is_used()")) {
            
            if (rs.next()) {
                boolean sslUsed = rs.getBoolean(1);
                return String.format(
                    "INFO SSL<br><br>" +
                    "SSL activo en conexion: %s<br>" +
                    "URL configurada: %s",
                    sslUsed ? "SI" : "NO",
                    dataSource.toString()
                );
            }
            return "No se pudo verificar SSL";
        } catch (Exception e) {
            return "ERROR AL VERIFICAR SSL:<br><br>" + e.getMessage();
        }
    }
}
