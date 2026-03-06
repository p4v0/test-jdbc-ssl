# Test JDBC SSL

Herramientas para probar conexiones con JDBC a PostgreSQL/Aurora.

## Opción 1: Script Java simple (Recomendado para pruebas rápidas)

**Ventajas:** Sin dependencias, ejecución inmediata, credenciales seguras.

```bash
# Ejecutar sin compilar (Java 11+)
cd Scripts
java -cp postgresql-42.7.4.jar TestJDBC.java "jdbc:postgresql://host:5432/db"
```

El script pedirá usuario y password de forma segura. Ver `Scripts/README.md` para más detalles.

## Opción 2: Aplicación Spring Boot (Para pruebas con framework)

Usar cuando necesites probar con Spring Boot, JPA, o HikariCP.

### Configuración

**IMPORTANTE:** No versionar credenciales reales.

1. Copiar el archivo de ejemplo:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

2. Editar `src/main/resources/application.properties` con tus credenciales:

```properties
spring.datasource.url=jdbc:postgresql://<DB_HOST>:<DB_PORT>/<DB_NAME>
spring.datasource.username=<DB_USER>
spring.datasource.password=<DB_PASSWORD>
```

**Alternativa más segura:** Usar variables de entorno o parámetros en runtime (ver sección "Override de configuración en runtime").

---

## Información sobre SSL

### Comportamiento de SSL por defecto

**URL sin parámetros SSL (por defecto):**
```
jdbc:postgresql://host:5432/database
```
- El driver JDBC negocia SSL según la configuración del servidor
- Si el servidor tiene `force_ssl=1`, la conexión será SSL automáticamente
- Si el servidor tiene `force_ssl=0`, la conexión será sin SSL
- **Recomendado para producción**: Dejar que el servidor controle SSL

**Forzar SSL desde el cliente:**
```
jdbc:postgresql://host:5432/database?ssl=true&sslmode=require
```

### Modos SSL (parámetro `sslmode`)

| Modo | Descripción | Uso |
|------|-------------|-----|
| `disable` | Sin SSL | Solo para desarrollo local |
| `allow` | Intenta SSL, si falla usa conexión normal | No recomendado |
| `prefer` | Prefiere SSL pero acepta sin SSL | No recomendado |
| `require` | Requiere SSL pero no valida certificado | Producción básica |
| `verify-ca` | Requiere SSL y valida CA del certificado | Producción segura |
| `verify-full` | Requiere SSL, valida CA y hostname | Máxima seguridad |

**Nota importante sobre `sslrootcert`:** 
- El parámetro `sslrootcert` en la URL **NO es soportado** por el driver JDBC de PostgreSQL
- Es un parámetro de `libpq` (usado por psql/pgAdmin)
- Para JDBC, el certificado debe estar en el Java truststore del sistema si se usa un modo SSL que haga validación de él

### Ejemplos de configuración

**Desarrollo local sin SSL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb?ssl=false&sslmode=disable
```

**Producción con SSL (servidor controla):**
```properties
spring.datasource.url=jdbc:postgresql://rds-host.amazonaws.com:5432/mydb
```

**Forzar SSL desde cliente:**
```properties
spring.datasource.url=jdbc:postgresql://rds-host.amazonaws.com:5432/mydb?ssl=true&sslmode=require
```

## Ejecutar

### Desde línea de comandos (Maven):
```bash
mvn spring-boot:run
```

### Compilar JAR ejecutable independiente:
```bash
mvn clean package -DskipTests
```

Esto genera `target/test-jdbc-ssl-1.0.jar` que incluye todas las dependencias y puede ejecutarse en cualquier máquina con Java 17+.

### Ejecutar con JAR compilado:
```bash
java -jar target/test-jdbc-ssl-1.0.jar
```

### Override de configuración en runtime

**Cambiar URL sin recompilar:**
```bash
# Con Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.datasource.url='jdbc:postgresql://host:5432/db?ssl=true&sslmode=require'"

# Con JAR
java -Dspring.datasource.url='jdbc:postgresql://host:5432/db?ssl=true&sslmode=require' -jar target/test-jdbc-ssl-1.0.jar
```

## Probar

Abrir navegador o usar curl:
- http://localhost:8080/ - Ver endpoints disponibles
- http://localhost:8080/test-connection - Probar conexión y verificar SSL
- http://localhost:8080/test-query - Ejecutar query de prueba
- http://localhost:8080/test-ssl-info - Verificar SSL (solo PostgreSQL estándar, no Aurora)

**Ejemplo con curl:**
```bash
curl http://localhost:8080/test-connection
```

## Logs

Los logs de PostgreSQL JDBC aparecen en consola con nivel DEBUG para diagnóstico:
- `org.postgresql=DEBUG` - Logs del driver JDBC
- `org.springframework.jdbc=DEBUG` - Logs de Spring JDBC

Buscar en logs:
- `converting regular socket connection to ssl` - SSL está siendo negociado
- `SSL error: Connection reset` - Problema con handshake SSL

## Troubleshooting

### Verificar conectividad de red

Antes de probar JDBC, verifica que puedes alcanzar el servidor de base de datos:

**Linux/WSL:**
```bash
# Con netcat
nc -zv <DB_HOST> 5432

# Con curl
curl -v telnet://<DB_HOST>:5432 --connect-timeout 5

# Con bash
timeout 5 bash -c 'cat < /dev/null > /dev/tcp/<DB_HOST>/5432' && echo "Conectado" || echo "Fallo"
```

**PowerShell (Windows):**
```powershell
tnc <DB_HOST> -p 5432
```

Si estos comandos fallan con timeout, el problema es de red/firewall, no de JDBC o SSL.

### Error "Connection reset" con SSL

**Causa común:** Conexión desde fuera de la VPC de AWS (via VPN) o a través de un firewall, puede interferir con handshake SSL.

**Soluciones:**
1. Probar primero con `ssl=false` para confirmar conectividad básica sin cifrar
2. Si funciona sin SSL pero falla con SSL:
   - Desde **dentro de la VPC** (ECS, EC2, bastion): SSL funciona correctamente
   - Desde **fuera de la VPC** (local via VPN): Puede fallar por firewall/VPN
3. Para desarrollo local, usar port forwarding via SSM:
   ```bash
   aws ssm start-session --target <bastion-id> \
     --document-name AWS-StartPortForwardingSessionToRemoteHost \
     --parameters '{"host":["rds-host.amazonaws.com"],"portNumber":["5432"],"localPortNumber":["5433"]}'
   ```
   Luego conectar a `localhost:5433`

### Importar certificado RDS al Java truststore (si es necesario)

```bash
# Descargar certificado (la URL varía para cada región de AWS)
curl -o rds-ca.pem https://truststore.pki.rds.amazonaws.com/us-east-2/us-east-2-bundle.pem

# Importar a Java truststore
sudo keytool -importcert -alias rds-ca \
  -file rds-ca.pem \
  -keystore $JAVA_HOME/lib/security/cacerts \
  -storepass changeit \
  -noprompt
```

### Verificar versión del driver

El proyecto usa PostgreSQL JDBC Driver 42.7.4. Verificar en `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
</dependency>
```

## Notas de seguridad

- **Nunca** commitear credenciales reales en `application.properties`, gitignore viene configurado para evitarlo
- Usar variables de entorno o AWS Secrets Manager en producción
- Preferir `sslmode=verify-full` en producción para máxima seguridad
- El modo `require` es aceptable si el certificado está en el truststore del sistema

---
