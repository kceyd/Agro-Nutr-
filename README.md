# NutriExpert (AgroNutri) - Registro y Estimación Nutricional Agrícola en Terreno (DSY1105)

App Android (Kotlin + Jetpack Compose + Material 3, MVVM) con backend de microservicios Spring Boot, del caso de Consultora Gestión y Proyectos Limitada.

## Integrantes
Equipo08 - Desarrollo de Aplicaciones Móviles (DSY1105)

| Integrante | Rol / aportes principales |
|---|---|
| Aaron Nicodemo Barrios Jimenez | COMPLETAR (por ejemplo: app móvil, microservicios, pruebas) |
| Diego Alejandro Torres Magne | COMPLETAR |
| Sergio Eduardo Pizarro Berrios | COMPLETAR |

Planificación en Trello: COMPLETAR enlace del tablero. Ver también `CONTRIBUTING.md`.

## Cómo abrirlo
1. Android Studio > File > Open > carpeta `NutriExpert`.
2. Esperar el Gradle Sync (necesita internet la primera vez).
3. Ejecutar en emulador o teléfono Android 8.0+ (API 26).

## Funcionalidades (v1.1)
- Login por perfil (Agricultor / Asesor).
- Predios, registro de mediciones (pH, CE, N, P, K, Ca, Mg) con validación de rangos y foto foliar completa (cámara nativa + FileProvider).
- Estimación básica de fertirriego (datos ficticios en `domain/Fertirriego.kt`), compartir recomendación y validación del asesor.
- Historial, catálogo de fertilizantes (SQLite) y bitácora de eventos (SQLiteOpenHelper).
- Clima del predio con la API externa Open-Meteo (Retrofit propio): condición actual, pronóstico de 3 días y aviso para el fertirriego.
- Offline con Room/SQLite; sincroniza solo al detectar red. Retrofit configurado en `data/remote`;
  activa `USAR_BACKEND_REAL` en `AgroRepository` para usar los microservicios.
- Pruebas unitarias e instrumentadas y firma release por `keystore.properties`.

## Novedades v2 (NutriExpert mejorado)
- **Backend** (`backend/`): 3 microservicios Spring Boot, cada uno con su base de datos MySQL: predios, usuarios y mediciones. Documentación completa en [`backend/DOCUMENTACION_MICROSERVICIOS.md`](backend/DOCUMENTACION_MICROSERVICIOS.md) (entidades, endpoints, persistencia y ejemplos) y resumen en `backend/README.md`.
- **Animaciones**: entrada escalonada de tarjetas, contadores que cuentan, barras de nutrientes que se llenan, efecto de presión al tocar, indicador de red que late, logo flotante, estados vacíos animados.
- **Adaptable**: grillas de 1 a 3 columnas, riel lateral en pantallas anchas, formularios con ancho máximo y campos de 2 o 4 por fila.
- **Funcionalidad**: sincronización de predios (migración Room v2→v3), buscador en el historial, compartir recomendación, total kg/ha.
- **APK firmado desde Gradle**: `signingConfigs` en `app/build.gradle.kts` lee `keystore.properties`; el APK y la llave quedan en el repositorio (ver más abajo).
- Detalle de las mejoras en `docs/MEJORAS_NUTRIEXPERT.md`.

## Validación centralizada
Todas las reglas (predios, mediciones, correo, contraseña) viven en `domain/Validacion.kt` y se prueban en
`ValidacionTest`/`FertirriegoTest`. Los ViewModels llaman a esas reglas y exponen el error de cada campo; las
pantallas solo lo dibujan con el componente `CampoValidado` (`ui/components`), así todos los formularios
muestran el error igual (borde rojo, ícono de advertencia y mensaje bajo el campo).

## Endpoints utilizados

### Microservicios propios (Spring Boot)
Cada servicio tiene su propia base MySQL y Swagger en `/swagger-ui.html`. Detalle completo en `backend/DOCUMENTACION_MICROSERVICIOS.md`.

| Servicio (puerto) | Método y ruta | Descripción |
|---|---|---|
| predios (8081) | `GET /api/predios?nombre=` | Lista y busca predios |
| predios (8081) | `GET /api/predios/{id}` | Obtiene un predio |
| predios (8081) | `GET /api/predios/resumen` | Total de predios y superficie |
| predios (8081) | `POST /api/predios` | Crea un predio |
| predios (8081) | `PUT /api/predios/{id}` | Edita un predio |
| predios (8081) | `DELETE /api/predios/{id}` | Elimina un predio |
| predios (8081) | `POST /api/predios/sync` | Sincroniza predios desde la app (usado por la app) |
| usuarios (8082) | `POST /api/usuarios/registro` | Registra un usuario (usado por la app) |
| usuarios (8082) | `POST /api/usuarios/login` | Inicia sesión |
| usuarios (8082) | `GET /api/usuarios?rol=` | Lista usuarios |
| usuarios (8082) | `GET /api/usuarios/{id}` | Obtiene un usuario |
| usuarios (8082) | `PUT /api/usuarios/{id}` | Edita un usuario |
| usuarios (8082) | `DELETE /api/usuarios/{id}` | Elimina un usuario |
| mediciones (8083) | `GET /api/mediciones?predioId=` | Lista mediciones |
| mediciones (8083) | `GET /api/mediciones/{id}` | Obtiene una medición |
| mediciones (8083) | `GET /api/mediciones/resumen` | Resumen por cultivo y estado |
| mediciones (8083) | `POST /api/mediciones` | Crea una medición |
| mediciones (8083) | `POST /api/mediciones/sync` | Sincroniza mediciones desde la app (usado por la app) |
| mediciones (8083) | `PATCH /api/mediciones/{id}/validar` | Valida una medición |
| mediciones (8083) | `DELETE /api/mediciones/{id}` | Elimina una medición |

### API externa (Retrofit propio)
| Dato | Valor |
|---|---|
| API | [Open-Meteo](https://open-meteo.com) – pronóstico del tiempo, gratuita y sin API key |
| URL base | `https://api.open-meteo.com/` (HTTPS) |
| Endpoint | `GET /v1/forecast?latitude=&longitude=&current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration&timezone=America/Santiago&forecast_days=3` |
| Código | `data/remote/ClimaApi.kt` (DTOs, interfaz `OpenMeteoApi`, `ClimaRetrofit` con su propio `Retrofit.Builder`), `data/ClimaRepository.kt`, `domain/Clima.kt`, `ui/viewmodel/ClimaViewModel.kt` |
| Pantallas | Tarjeta **"Clima del predio"** (`ui/components/TarjetaClima.kt`) en **Registro de medición** y en **Recomendación** |
| Qué muestra | Temperatura, condición, humedad, viento y lluvia actuales; máx./mín., lluvia y ET0 de 3 días; aviso si se espera lluvia (≥ 5 mm en 48 h) o demanda de agua alta (ET0 ≥ 5 mm/día) |
| Ubicación | Latitud/longitud del predio (nuevas columnas Room v4, opcionales en "Nuevo predio"); sin coordenadas se usa Quillota como referencia |
| Sin red | La tarjeta lo indica y consulta sola al volver la conexión; el registro de mediciones sigue funcionando offline |

## Pasos para ejecutar

### 1. Microservicios (requiere Docker Desktop)
```bash
cd backend
docker compose up --build
```
- Levanta MySQL 8 (crea las 3 bases con `init.sql`) y los servicios en los puertos 8081, 8082 y 8083.
- Swagger: http://localhost:8081/swagger-ui.html, http://localhost:8082/swagger-ui.html y http://localhost:8083/swagger-ui.html
- Prueba rápida: `bash probar.sh`
- Apagar: `docker compose down` (con `-v` borra los datos).

### 2. App
1. Abrir la carpeta `NutriExpert` en Android Studio y esperar el Gradle Sync.
2. Para usar los microservicios, poner `USAR_BACKEND_REAL = true` en `AgroRepository`.
3. En el emulador, el `HOST` de `RetrofitProvider` es `10.0.2.2` (el `localhost` del PC). En un teléfono real o un servidor, cambiarlo por la IP correspondiente y agregarla en `network_security_config.xml`.
4. Ejecutar en emulador o teléfono Android 8.0+ (API 26).

Cuentas de demostración (clave `Demo1234`): `agricultor@demo.cl` y `asesor@demo.cl`.

## APK release firmado
El APK se firma desde Gradle con la llave del repositorio:

1. La llave (keystore PKCS12) está en `jks/firma` y sus datos en `keystore.properties`:
   ```properties
   storeFile=jks/firma
   storePassword=<clave del almacén>
   keyAlias=key0
   keyPassword=<clave de la llave>
   ```
2. En `app/build.gradle.kts`, `signingConfigs { create("release") { ... } }` lee esos valores y el build `release` usa `signingConfig = signingConfigs.getByName("release")`.
3. Generar el APK:
   ```bash
   ./gradlew assembleRelease
   ```
   Queda en `app/build/outputs/apk/release/app-release.apk`; la copia entregada está en `app/release/app-release.apk`.

Nota: la llave y sus claves están en el repositorio solo porque es un trabajo académico y lo pide la pauta; no se debe reutilizar para publicar una app real.

### Capturas
COMPLETAR: insertar aquí la captura del APK firmado (`app/release/app-release.apk`) y la captura del archivo `jks/firma`.

## Trabajo en equipo
Ver `CONTRIBUTING.md` (ramas por tarea, issues, Pull Requests con revisión cruzada). La planificación de tareas está en Trello (COMPLETAR enlace) y los commits por integrante en el historial de GitHub.

## Estructura
`data/` (Room, SQLite, Retrofit, repositorio) · `domain/` (reglas) · `ui/` (pantallas, ViewModels, tema) · `backend/` (microservicios) · `jks/` (llave de firma) · `docs/`

## Documentación
- `backend/DOCUMENTACION_MICROSERVICIOS.md`: microservicios (entidades, endpoints, persistencia, ejemplos).
- `docs/MEJORAS_NUTRIEXPERT.md`: mejoras de la v2.
- COMPLETAR o quitar de esta lista los archivos que no estén en el repo: `AgroNutri_Explicacion_del_Codigo.pdf`, `AgroNutri_Mockup.pdf`, `AgroNutri_Backlog_Priorizado.xlsx`, `ARQUITECTURA.md` y `PLAN_DE_PRUEBAS.md` (hoy no vienen en el zip).
