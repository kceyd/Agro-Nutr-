# Backend NutriExpert: 3 microservicios simples

| Servicio | Puerto | Base de datos | Qué hace |
|---|---|---|---|
| `predios-service` | 8081 | `nutri_predios` | CRUD de predios + `POST /api/predios/sync` (lo usa la app) |
| `usuarios-service` | 8082 | `nutri_usuarios` | Registro, login y CRUD de usuarios (clave con BCrypt) |
| `mediciones-service` | 8083 | `nutri_mediciones` | Recibe y consulta mediciones + `POST /api/mediciones/sync` (lo usa la app) |

Cada servicio tiene **su propia base de datos y su propio usuario de MySQL** (ver `init.sql`) y no llama a los otros:
`mediciones-service` guarda `predioId` solo como número, no consulta a `predios-service`.

Stack (igual que tus otros proyectos): Java 21, Spring Boot 3.5, JPA/Hibernate, MySQL 8, Bean Validation,
Swagger (springdoc), DTOs Request/Response, `GlobalExceptionHandler`, `DataInitializer` con guarda `count() == 0`
y tres consultas por repositorio.

## Levantar todo con Docker
```bash
cd backend
docker compose up --build
```
Swagger: http://localhost:8081/swagger-ui.html · http://localhost:8082/swagger-ui.html · http://localhost:8083/swagger-ui.html

Prueba rápida (con los servicios corriendo): `bash probar.sh`

## Levantar sin Docker
1. `docker compose up mysql` (solo la base) o un MySQL propio con `init.sql` ejecutado.
2. En cada carpeta: `mvn spring-boot:run`

## Conectar la app Android
1. En `AgroRepository.kt` cambia `USAR_BACKEND_REAL` a `true`.
2. Emulador: ya apunta a `10.0.2.2`. Teléfono real: cambia `HOST` en `data/remote/ApiService.kt` por la IP de tu PC
   y agrégala en `res/xml/network_security_config.xml`.

## Datos de demostración (se cargan solo si la tabla está vacía)
- predios: los mismos 3 de la app (con `idLocal` 1, 2 y 3, para que al sincronizar no se dupliquen)
- usuarios: `agricultor@demo.cl` y `asesor@demo.cl`, clave `Demo1234` (**bórralas antes de usar de verdad**)
- mediciones: 2 de ejemplo del predio 1

## Limitaciones conocidas (a propósito, para mantenerlo simple)
- Sin JWT: el login devuelve los datos del usuario pero no un token. Los endpoints no exigen autenticación.
- Se usa http sin TLS (solo desarrollo). En producción: https y token.
- La app guarda y valida claves localmente; `usuarios-service` recibe una copia del registro (si `USAR_BACKEND_REAL = true`).
- La sincronización identifica registros por `idLocal`; si dos teléfonos distintos usaran el mismo servidor, los ids de Room podrían chocar.
