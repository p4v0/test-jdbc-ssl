import java.sql.*;

/**
 * Test básico de conexión JDBC a PostgreSQL sin Spring Boot
 * 
 * Compilar:
 *   javac -cp postgresql-42.7.4.jar TestJDBC.java
 * 
 * Ejecutar:
 *   java -cp .:postgresql-42.7.4.jar TestJDBC
 *   (En Windows: java -cp .;postgresql-42.7.4.jar TestJDBC)
 * 
 * Descargar driver PostgreSQL JDBC:
 *   https://jdbc.postgresql.org/download/
 *   O desde Maven Central: https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/
 */
public class TestJDBC {
    public static void main(String[] args) {
        // Configurar según tu entorno
        String url = "jdbc:postgresql://rds-cluster.us-east-2.rds.amazonaws.com:5432/database";
        String user = "<DB_USER>";
        String password = "<DB_PASSWORD>";
        
        // Para forzar SSL, agregar a la URL: ?ssl=true&sslmode=require
        // Ejemplo: jdbc:postgresql://rds-cluster.us-east-2.rds.amazonaws.com:5432/database?ssl=true&sslmode=require
        
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Conexión exitosa!");
            System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("URL: " + conn.getMetaData().getURL());
            conn.close();
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
