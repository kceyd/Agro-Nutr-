# AgroNutri - base de la app (Evaluación Parcial 2)

Kotlin + Jetpack Compose + Material 3 + MVVM + Room + Navigation Compose.

## Cómo abrirlo
1. Descomprime el ZIP y en Android Studio: File > Open > carpeta AgroNutri.
2. Si Android Studio pide un Gradle wrapper, acepta "usar Gradle por defecto" y sincroniza.
3. Si prefieres un proyecto nuevo (Empty Activity, paquete cl.duoc.agronutri), copia
   `app/src/main` y `app/build.gradle.kts`, y los plugins de `build.gradle.kts` raíz.
4. Ejecuta en un emulador con cámara o en un celular Android (minSdk 26).

## Qué cumple de la evaluación
- Material 3: tema, Scaffold, Cards, chips, dropdowns, botones segmentados.
- Formularios validados: predio (texto y listas) y análisis (rangos pH, CE, ppm).
- Navegación: Home > Predios > Registro / Análisis / Fertirriego.
- Estado: ViewModel + StateFlow + collectAsStateWithLifecycle.
- Almacenamiento local: Room (Predio y Analisis).
- Recurso nativo: cámara (TakePicture + FileProvider) y estado de red.
- Animaciones: transiciones entre pantallas, AnimatedVisibility, animateContentSize,
  animateColorAsState.

## Pendiente para el proyecto final
- Reemplazar Catalogo.kt (datos simulados) por el catálogo y la fórmula del cliente.
- Retrofit + Spring Boot y sincronización automática (los campos `sincronizado` ya existen).
- Perfil Asesor: validar y emitir recomendaciones.
- APK firmado en release, Git/GitHub y Trello.
