import java.sql.*;
import java.io.Console;

/**
 * Test básico de conexión JDBC a PostgreSQL sin Spring Boot
 * 
 * Ejecutar sin compilar (Java 11+):
 *   java -cp postgresql-42.7.4.jar TestJDBC.java <URL>
 *   (Pedirá usuario y password de forma segura)
 * 
 * Compilar y ejecutar (método tradicional):
 *   javac -cp postgresql-42.7.4.jar TestJDBC.java
 *   java -cp .:postgresql-42.7.4.jar TestJDBC <URL>
 *   (En Windows: java -cp .;postgresql-42.7.4.jar TestJDBC <URL>)
 * 
 * Descargar driver PostgreSQL JDBC:
 *   https://jdbc.postgresql.org/download/
 *   O desde Maven Central: https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/
 */
public class TestJDBC {
    public static void main(String[] args) {
        String url, user, password;
        
        // Validar que se proporcione la URL
        if (args.length < 1) {
            System.out.println("Uso: java TestJDBC <URL>");
            System.out.println("Ejemplo: java TestJDBC \"jdbc:postgresql://host:5432/db\"");
            System.out.println("Para SSL: java TestJDBC \"jdbc:postgresql://host:5432/db?ssl=true&sslmode=require\"");
            System.exit(1);
            return;
        }
        
        url = args[0];
        
        // Pedir credenciales de forma segura
        Console console = System.console();
        if (console == null) {
            System.out.println("Error: No se puede leer credenciales de forma segura (no hay consola)");
            System.exit(1);
            return;
        }
        
        user = console.readLine("Usuario: ");
        char[] passwordArray = console.readPassword("Password: ");
        password = new String(passwordArray);
        
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
