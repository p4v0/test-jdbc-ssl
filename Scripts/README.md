# Scripts de prueba

## TestJDBC.java

Script Java básico para probar conexión JDBC sin Spring Boot.

### Requisitos

- JDK 11 o superior (para ejecutar sin compilar)
- **Driver PostgreSQL JDBC** (NO incluido en Java, debe descargarse):
  ```bash
  cd Scripts
  
  # Descargar con curl
  curl -o postgresql-42.7.4.jar https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
  
  # O con wget
  wget https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
  ```

### Uso rápido (sin compilar - Java 11+)

**REQUERIDO:** Debes proporcionar la URL JDBC como argumento.

```bash
# Linux/Mac
java -cp postgresql-42.7.4.jar TestJDBC.java "jdbc:postgresql://host:5432/db"

# Windows
java -cp postgresql-42.7.4.jar TestJDBC.java "jdbc:postgresql://host:5432/db"
```

El script pedirá usuario y password de forma segura (no quedarán en el historial de comandos).

### Uso tradicional (compilar primero)

1. Compilar:
   ```bash
   javac -cp postgresql-42.7.4.jar TestJDBC.java
   ```

2. Ejecutar con URL como argumento:
   ```bash
   # Linux/Mac
   java -cp .:postgresql-42.7.4.jar TestJDBC "jdbc:postgresql://host:5432/db"
   
   # Windows
   java -cp .;postgresql-42.7.4.jar TestJDBC "jdbc:postgresql://host:5432/db"
   ```

### Ejemplos de configuración

**Forzar sin SSL desde el cliente:**
```properties
java -cp postgresql-42.7.4.jar TestJDBC.java "jdbc:postgresql://host:5432/db?ssl=false&sslmode=disable"
```

**Producción con SSL (servidor controla):**
```properties
java -cp postgresql-42.7.4.jar TestJDBC.java "jdbc:postgresql://host:5432/db"
```

**Forzar SSL desde cliente:**
```properties
java -cp postgresql-42.7.4.jar TestJDBC.java "jdbc:postgresql://host:5432/db?ssl=true&sslmode=require"
```

### Ventajas vs Spring Boot

- Más rápido para pruebas simples
- Sin dependencias de Spring
- Útil para diagnóstico de problemas de driver JDBC
- Ejecución directa sin compilar (Java 11+)
- Credenciales seguras (no quedan en historial CLI)
