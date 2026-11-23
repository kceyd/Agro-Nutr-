# NutriExpert v2: documentación de las mejoras

## 1. Resumen
Se mantuvo la estructura de la app (mismas rutas, pantallas, ViewModels, Room offline-first, roles Agricultor/Asesor) y se agregó:

1. **Backend**: 3 microservicios Spring Boot simples, cada uno con su propia base de datos.
2. **Animaciones** en todas las pantallas.
3. **Diseño visual y adaptable** (teléfono, tablet, pantalla girada).
4. **Funcionalidad nueva**: sincronización de predios, buscador, compartir recomendación, total de fertilizante.

**Importante:** el proyecto subido no traía ningún microservicio (solo el contrato Retrofit de `POST mediciones` sin servidor). Por eso se crearon los tres desde cero.

## 2. Microservicios (carpeta `backend/`)

| Servicio | Puerto | Base de datos | Función |
|---|---|---|---|
| predios-service | 8081 | nutri_predios | Registro de predios (CRUD, búsqueda por nombre, resumen, sync) |
| usuarios-service | 8082 | nutri_usuarios | Registro, login y CRUD de usuarios, clave con BCrypt |
| mediciones-service | 8083 | nutri_mediciones | Recibe y consulta mediciones, validación, resumen por cultivo |

El tercero (mediciones) se eligió porque la app ya enviaba mediciones y no había dónde guardarlas.

**Independencia:** cada servicio tiene su base y su usuario de MySQL con permisos solo sobre su base (`init.sql`). No se llaman entre sí; `mediciones-service` guarda `predioId` solo como número.

**Convenciones (iguales a tus otros proyectos):** Java 21, Spring Boot 3.5, JPA, MySQL 8, DTOs Request/Response, `GlobalExceptionHandler`, `DataInitializer` con guarda `count() == 0`, tres consultas por repositorio, Swagger, Dockerfile multi-etapa y `docker-compose.yml`. No se usó Lombok.

### Endpoints
**predios-service:** `GET /api/predios?nombre=`, `GET /api/predios/{id}`, `GET /api/predios/resumen`, `POST /api/predios`, `PUT /api/predios/{id}`, `DELETE /api/predios/{id}`, `POST /api/predios/sync`.

**usuarios-service:** `POST /api/usuarios/registro` (201, o 409 si el correo existe), `POST /api/usuarios/login` (200, o 401), `GET /api/usuarios?rol=`, `GET /api/usuarios/{id}`, `PUT /api/usuarios/{id}`, `DELETE /api/usuarios/{id}`.

**mediciones-service:** `GET /api/mediciones?predioId=`, `GET /api/mediciones/{id}`, `GET /api/mediciones/resumen`, `POST /api/mediciones`, `POST /api/mediciones/sync`, `PATCH /api/mediciones/{id}/validar`, `DELETE /api/mediciones/{id}`.

### Decisiones de diseño
- **Sync idempotente por `idLocal`**: la app puede reenviar el mismo lote sin duplicar. Esto es necesario porque, al validar una medición, la app la marca otra vez como pendiente.
- **Lote todo o nada**: si una medición del lote es inválida, se rechaza el lote completo con 400.
- **Validación con los mismos rangos de la app** (`Validacion.kt`): pH 3-10, CE 0-10, N/P/Mg 0-500, K/Ca 0-1000, superficie 0-1000 ha, clave de 8+ caracteres con mayúscula y número.
- **Login sin pista**: el mensaje es el mismo si falla el correo, la clave o el perfil.
- **`ddl-auto=update`** en vez de `create-drop`, para que los datos sobrevivan a un reinicio.
- **Datos de ejemplo** de predios con `idLocal` 1, 2 y 3, iguales a los de la app, para que no se dupliquen al sincronizar.

## 3. Cambios en la app Android

### Capa de datos
| Archivo | Cambio |
|---|---|
| `Entities.kt` | `Predio` tiene `pendienteSync` (por defecto verdadero) |
| `Daos.kt` | `PredioDao`: contar pendientes, listar pendientes, marcar sincronizados |
| `AppDatabase.kt` | Versión 3 y `MIGRATION_2_3` (agrega la columna sin borrar datos) |
| `ApiService.kt` | Tres interfaces Retrofit (`PrediosApi`, `UsuariosApi`, `MedicionesApi`), DTOs y un `RetrofitProvider` con un puerto por servicio |
| `AgroRepository.kt` | Sincroniza predios y luego mediciones; el contador de pendientes suma ambos |
| `UsuarioViewModel.kt` | Si el backend está activo, copia la cuenta nueva a `usuarios-service` (sin red, se ignora) |
| `AndroidManifest.xml` + `network_security_config.xml` | Permite http solo hacia `10.0.2.2` y `localhost` |

**Interruptor:** `AgroRepository.USAR_BACKEND_REAL` queda en `false` (la sincronización se simula y la app funciona sin servidor). Ponlo en `true` para usar los microservicios.

### Animaciones
| Animación | Dónde |
|---|---|
| Entrada escalonada (aparecen subiendo, una tras otra) | Tarjetas de Predios, Historial, Registro y dosis |
| Contadores que cuentan desde 0 | Cifras del encabezado y kg/ha |
| Barras de nutrientes que se llenan | Recomendación |
| Efecto de presión con rebote | Tarjetas del Historial |
| Indicador de red con punto que late y texto en fundido | Barra superior |
| Círculo decorativo que "respira" | Encabezados |
| Logo que flota suave | Login |
| Ícono flotante en pantallas vacías | Predios, Historial |
| Barra inferior que entra y sale deslizando | Navegación |
| Foto foliar que aparece expandiéndose | Registro, Recomendación |
| Reordenado animado al filtrar | Listas |

### Diseño visual
- Encabezado con el degradado verde de la web (`TealBanner`), que antes no se usaba.
- Tipografía con más jerarquía, esquinas más redondeadas, tarjetas blancas con sombra suave.
- Superficies verdosas (antes M3 usaba grises lilas por defecto en algunas tarjetas).
- Etiquetas de estado con color: verde = listo, naranja = pendiente.
- Dosis por nutriente con símbolo, fertilizante, déficit, kg/ha y barra de color.

### Adaptabilidad
- **Grillas**: Predios e Historial usan `GridCells.Adaptive`, de 1 columna en teléfono a 2-3 en tablet.
- **Navegación**: con ancho de 600 dp o más, la barra inferior pasa a un riel lateral.
- **Formularios**: ancho máximo centrado; campos de medición de 2 por fila en teléfono y de 4 en pantallas anchas; cultivo y etapa lado a lado.
- **Login**: ancho máximo de 480 dp.
- No se agregaron dependencias nuevas.

### Funcionalidad nueva
1. **Predios se sincronizan** (antes solo las mediciones).
2. **Buscador** en el Historial por predio, cultivo o etapa.
3. **Compartir recomendación** como texto (WhatsApp, correo, etc.).
4. **Total de kg/ha** en la recomendación y mensajes de estado vacío con guía.

## 4. Cómo probar
1. Backend: `cd backend && docker compose up --build`, luego `bash probar.sh`.
2. App: abrir en Android Studio, esperar Gradle Sync y ejecutar.
3. Conectar: `USAR_BACKEND_REAL = true`, crear una medición y ver el contador de pendientes en la barra superior.

## 5. Qué NO se verificó
Esto se escribió sin poder compilar ni ejecutar nada en este entorno (no hay Android SDK ni acceso a Maven Central). Antes de entregar hay que:
- Hacer Gradle Sync y compilar la app, y corregir lo que Android Studio marque.
- Construir los microservicios (`docker compose up --build`).
- Probar la migración de Room v2 a v3 en un teléfono con la versión anterior instalada.
- Revisar las animaciones en un dispositivo real.

## 6. Limitaciones
- Sin JWT: el login no entrega token y los endpoints no exigen autenticación.
- http sin TLS (solo desarrollo).
- Las cuentas de demostración (`Demo1234`) deben borrarse antes de uso real.
- Los ids de Room podrían chocar si varios teléfonos usan el mismo servidor.
- La carpeta `app/build` del zip original (66 MB de archivos generados) no se incluyó.

## 7. v2.1: API externa de clima (Open-Meteo)
Se agregó una API externa, separada de los microservicios propios, con su propio Retrofit.

| Archivo | Cambio |
|---|---|
| `data/remote/ClimaApi.kt` (nuevo) | DTOs de la respuesta de Open-Meteo, interfaz `OpenMeteoApi` (`GET v1/forecast`), `ClimaRetrofit` con base `https://api.open-meteo.com/` y conversión DTO → dominio |
| `data/ClimaRepository.kt` (nuevo) | Llama a la API en `Dispatchers.IO` y traduce errores (sin red, HTTP, lectura) a mensajes |
| `domain/Clima.kt` (nuevo) | Modelos `ClimaActual`, `DiaPronostico`, `ClimaPredio`, `Ubicacion`; códigos WMO en español; aviso de riego; formato de día y hora |
| `ui/viewmodel/ClimaViewModel.kt` (nuevo) | Estados Cargando / SinRed / Error / Listo; reintenta solo al volver la red |
| `ui/components/TarjetaClima.kt` (nuevo) | Tarjeta "Clima del predio" |
| `RegistroScreen.kt`, `RecomendacionScreen.kt` | Muestran la tarjeta |
| `Entities.kt`, `AppDatabase.kt` | `Predio.latitud` y `Predio.longitud` (opcionales); base v4 con `MIGRATION_3_4`; coordenadas de ejemplo para los 3 predios iniciales |
| `PrediosScreen.kt`, `PrediosViewModel.kt`, `Validacion.kt` | Latitud/longitud opcionales en "Nuevo predio", con validación (−90..90, −180..180, ambas o ninguna) |
| `AgroNutriApp.kt`, `AppViewModelProvider.kt` | `ClimaRepository` en el contenedor y fábrica de `ClimaViewModel` |
| `test/ClimaTest.kt` (nuevo) | Lectura del JSON, códigos WMO, aviso de riego, coordenadas |

Decisiones:
- **Open-Meteo** porque no pide API key (funciona apenas se compila) e incluye ET0, útil para el riego.
- El clima **no se guarda** en Room ni se envía a los microservicios: es un dato en vivo.
- Las coordenadas **no se sincronizan** con `predios-service` (su DTO no cambió).
- `network_security_config.xml` no cambió: Open-Meteo usa HTTPS, que ya está permitido.

Pendiente de verificar (no se pudo compilar en este entorno): Gradle Sync, `./gradlew test`, la migración v3→v4 en un teléfono con la versión anterior y regenerar el APK firmado (`./gradlew assembleRelease`), porque `app/release/app-release.apk` es anterior a este cambio.
