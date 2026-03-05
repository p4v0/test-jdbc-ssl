# Scripts de prueba

## TestJDBC.java

Script Java básico para probar conexión JDBC sin Spring Boot.

### Requisitos

- JDK 8 o superior
- Driver PostgreSQL JDBC (descargar de https://jdbc.postgresql.org/download/)

### Uso

1. Editar `TestJDBC.java` y configurar credenciales:
   ```java
   String url = "jdbc:postgresql://rds-cluster.us-east-2.rds.amazonaws.com:5432/database";
   String user = "tu_usuario";
   String password = "tu_password";
   ```

2. Compilar:
   ```bash
   javac -cp postgresql-42.7.4.jar TestJDBC.java
   ```

3. Ejecutar:
   ```bash
   # Linux/Mac
   java -cp .:postgresql-42.7.4.jar TestJDBC
   
   # Windows
   java -cp .;postgresql-42.7.4.jar TestJDBC
   ```

### Forzar SSL

Agregar parámetros a la URL:
```java
String url = "jdbc:postgresql://host:5432/db?ssl=true&sslmode=require";
```

### Ventajas vs Spring Boot

- Más rápido para pruebas simples
- Sin dependencias de Spring
- Útil para diagnóstico de problemas de driver JDBC
