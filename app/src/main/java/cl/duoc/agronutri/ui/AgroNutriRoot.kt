package cl.duoc.agronutri.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import cl.duoc.agronutri.ui.components.IndicadorRed
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import cl.duoc.agronutri.ui.screens.AccesoScreen
import cl.duoc.agronutri.ui.screens.HistorialScreen
import cl.duoc.agronutri.ui.screens.LoginScreen
import cl.duoc.agronutri.ui.screens.PrediosScreen
import cl.duoc.agronutri.ui.screens.RecomendacionScreen
import cl.duoc.agronutri.ui.screens.RegistroScreen

import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.MainViewModel
import cl.duoc.agronutri.ui.viewmodel.Rol

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgroNutriRoot(
    mainVm: MainViewModel = viewModel(
        factory = AppViewModelProvider.Factory
    )
) {

    val nav = rememberNavController()

    val rol by mainVm.rol.collectAsStateWithLifecycle()
    val enLinea by mainVm.enLinea.collectAsStateWithLifecycle()
    val pendientes by mainVm.pendientes.collectAsStateWithLifecycle()

    val snackbar = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        mainVm.mensajes.collect {
            snackbar.showSnackbar(it)
        }
    }

    val rutaActual = nav
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val esPantallaAcceso =
        rutaActual == null ||
                rutaActual == Rutas.LOGIN ||
                rutaActual == Rutas.ACCESO

    val esRaiz =
        rutaActual == Rutas.PREDIOS ||
                rutaActual == Rutas.HISTORIAL

    // Adaptable: en pantallas anchas (tablet / teléfono girado) la barra inferior pasa a un riel lateral.
    val usarRiel = LocalConfiguration.current.screenWidthDp >= 600
    val mostrarBarraInferior = esRaiz && !usarRiel
    val mostrarRiel = esRaiz && usarRiel

    var confirmarSalida by remember { mutableStateOf(false) }

    // Atrás en una pantalla raíz ya no cierra la app de golpe: pide confirmación.
    BackHandler(enabled = esRaiz) { confirmarSalida = true }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Volverás a la pantalla de inicio. Tus registros guardados se mantienen.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    mainVm.salir()
                    nav.navigate(Rutas.LOGIN) { popUpTo(0) }
                }) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Quedarme") }
            }
        )
    }

    Scaffold(


        topBar = {

            if (!esPantallaAcceso) {

                TopAppBar(
                    title = {
                        Text(
                            "NutriExpert · ${rol?.etiqueta.orEmpty()}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },

                    navigationIcon = {
                        if (!esRaiz) {
                            IconButton(onClick = { nav.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary,

                        titleContentColor =
                            MaterialTheme.colorScheme.onPrimary,

                        actionIconContentColor =
                            MaterialTheme.colorScheme.onPrimary
                    ),

                    actions = {

                        IndicadorRed(enLinea = enLinea, pendientes = pendientes)

                        TextButton(
                            onClick = {

                                mainVm.salir()

                                nav.navigate(Rutas.LOGIN) {
                                    popUpTo(0)
                                }
                            },

                            colors = ButtonDefaults.textButtonColors(
                                contentColor =
                                    MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Salir")
                        }
                    }
                )
            }
        },


        bottomBar = {

            AnimatedVisibility(
                visible = mostrarBarraInferior,
                enter = slideInVertically(tween(300, easing = EaseOutCubic)) { it } + fadeIn(tween(300)),
                exit = slideOutVertically(tween(200)) { it } + fadeOut(tween(200))
            ) {

                NavigationBar {

                    NavigationBarItem(
                        selected = rutaActual == Rutas.PREDIOS,
                        onClick = { irA(nav, Rutas.PREDIOS) },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text("Predios") }
                    )

                    NavigationBarItem(
                        selected = rutaActual == Rutas.HISTORIAL,
                        onClick = { irA(nav, Rutas.HISTORIAL) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Historial") }
                    )
                }
            }
        },

        snackbarHost = {
            SnackbarHost(snackbar)
        }

    ) { padding ->


        val modificadorNavegacion = if (esPantallaAcceso) {
            Modifier
        } else {
            Modifier.padding(padding)
        }

        Row(modifier = modificadorNavegacion.fillMaxSize()) {

        if (mostrarRiel) {
            NavigationRail {
                NavigationRailItem(
                    selected = rutaActual == Rutas.PREDIOS,
                    onClick = { irA(nav, Rutas.PREDIOS) },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text("Predios") }
                )
                NavigationRailItem(
                    selected = rutaActual == Rutas.HISTORIAL,
                    onClick = { irA(nav, Rutas.HISTORIAL) },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Historial") }
                )
            }
        }

        NavHost(
            navController = nav,

            startDestination = Rutas.LOGIN,

            enterTransition = {
                slideInHorizontally(
                    tween(300)
                ) {
                    it / 2
                } + fadeIn(tween(300))
            },

            exitTransition = {
                fadeOut(tween(200))
            },

            popEnterTransition = {
                fadeIn(tween(300))
            },

            popExitTransition = {
                slideOutHorizontally(
                    tween(300)
                ) {
                    it / 2
                } + fadeOut(tween(300))
            },

            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()

        ) {


            composable(Rutas.LOGIN) {

                LoginScreen(
                    onEntrar = { elegido ->


                        nav.navigate(
                            Rutas.acceso(elegido.name)
                        )
                    }
                )
            }


            composable(
                route = Rutas.ACCESO,

                arguments = listOf(
                    navArgument("rol") {
                        type = NavType.StringType
                    }
                )

            ) { entrada ->

                val nombreRol = entrada.arguments
                    ?.getString("rol")

                val rolSeleccionado =
                    Rol.entries.firstOrNull {
                        it.name == nombreRol
                    }

                if (rolSeleccionado != null) {


                    AccesoScreen(
                        rol = rolSeleccionado,

                        onVolver = {
                            nav.popBackStack()
                        },

                        onInicioSesionCorrecto = { rolAutenticado ->
                            mainVm.entrar(rolAutenticado)

                            val inicio = if (rolAutenticado == Rol.ASESOR) {
                                Rutas.HISTORIAL
                            } else {
                                Rutas.PREDIOS
                            }

                            nav.navigate(inicio) {
                                popUpTo(Rutas.LOGIN) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )

                }
            }


            composable(Rutas.PREDIOS) {

                PrediosScreen(
                    onRegistrar = { id ->
                        nav.navigate(
                            Rutas.registro(id)
                        )
                    }
                )
            }


            composable(Rutas.HISTORIAL) {

                HistorialScreen(
                    rol = rol,
                    onAbrir = { id ->
                        nav.navigate(
                            Rutas.recomendacion(id)
                        )
                    }
                )
            }


            composable(
                route = Rutas.REGISTRO,

                arguments = listOf(
                    navArgument("predioId") {
                        type = NavType.IntType
                    }
                )

            ) {

                RegistroScreen(
                    onGuardado = { medicionId ->

                        nav.navigate(
                            Rutas.recomendacion(medicionId)
                        ) {
                            popUpTo(Rutas.PREDIOS)
                        }
                    }
                )
            }


            composable(
                route = Rutas.RECOMENDACION,

                arguments = listOf(
                    navArgument("medicionId") {
                        type = NavType.IntType
                    }
                )

            ) {

                RecomendacionScreen(
                    rol = rol
                )
            }
        }

        }
    }
}


private fun irA(
    nav: NavController,
    ruta: String
) {

    nav.navigate(ruta) {

        popUpTo(Rutas.PREDIOS) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}
