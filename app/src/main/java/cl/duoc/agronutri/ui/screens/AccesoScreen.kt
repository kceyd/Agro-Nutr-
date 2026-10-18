package cl.duoc.agronutri.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.ui.viewmodel.Rol
import cl.duoc.agronutri.ui.viewmodel.UsuarioViewModel

@Composable
fun AccesoScreen(
    rol: Rol,
    onVolver: () -> Unit,
    onInicioSesionCorrecto: (Rol) -> Unit = {},
    usuarioVm: UsuarioViewModel = viewModel()
) {
    val esAgricultor = rol == Rol.AGRICULTOR

    val colorPrincipal = if (esAgricultor) {
        Color(0xFF2E7D32)
    } else {
        Color(0xFF176B53)
    }

    val colorFondo = if (esAgricultor) {
        Color(0xFFF1F8EE)
    } else {
        Color(0xFFEDF5F1)
    }

    val descripcionPerfil = if (esAgricultor) {
        "Gestiona tus cultivos y registros agrícolas"
    } else {
        "Consulta y analiza información nutricional"
    }

    var esRegistro by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }
    var mensajeLocal by remember { mutableStateOf("") }
    var esperandoInicioSesion by remember { mutableStateOf(false) }

    val estado by usuarioVm.estado.collectAsStateWithLifecycle()

    LaunchedEffect(rol) {
        usuarioVm.limpiarEstado()
        esperandoInicioSesion = false
    }

    LaunchedEffect(estado.exito, estado.cargando, esperandoInicioSesion) {
        if (estado.exito && !estado.cargando && esperandoInicioSesion) {
            esperandoInicioSesion = false
            onInicioSesionCorrecto(rol)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 64.dp,
                    bottom = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (esAgricultor) {
                    Icons.Filled.Person
                } else {
                    Icons.Filled.CheckCircle
                },
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.size(56.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = rol.etiqueta,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = colorPrincipal
            )

            Text(
                text = descripcionPerfil,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFF52645A)
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (esRegistro) {
                            "Crear cuenta"
                        } else {
                            "Iniciar sesión"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorPrincipal
                    )

                    if (esRegistro) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = {
                                nombre = it
                                mensajeLocal = ""
                            },
                            label = { Text("Nombre completo") },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !estado.cargando
                        )
                    }

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            mensajeLocal = ""
                        },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !estado.cargando
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            mensajeLocal = ""
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    mostrarClave = !mostrarClave
                                }
                            ) {
                                Text(
                                    if (mostrarClave) "Ocultar" else "Ver"
                                )
                            }
                        },
                        visualTransformation = if (mostrarClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !estado.cargando
                    )

                    if (esRegistro) {
                        OutlinedTextField(
                            value = confirmar,
                            onValueChange = {
                                confirmar = it
                                mensajeLocal = ""
                            },
                            label = {
                                Text("Confirmar contraseña")
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !estado.cargando
                        )
                    }

                    val mensajeMostrar = mensajeLocal.ifEmpty {
                        estado.mensaje
                    }

                    if (mensajeMostrar.isNotEmpty()) {
                        Text(
                            text = mensajeMostrar,
                            color = if (
                                mensajeLocal.isEmpty() && estado.exito
                            ) {
                                colorPrincipal
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            mensajeLocal = ""

                            val error = when {
                                esRegistro && nombre.isBlank() ->
                                    "Debes ingresar tu nombre."

                                !android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(correo.trim())
                                    .matches() ->
                                    "Ingresa un correo válido."

                                esRegistro && contrasena.length < 8 ->
                                    "La contraseña necesita al menos 8 caracteres."

                                esRegistro && !contrasena.any {
                                    it.isUpperCase()
                                } ->
                                    "La contraseña necesita una mayúscula."

                                esRegistro && !contrasena.any {
                                    it.isDigit()
                                } ->
                                    "La contraseña necesita un número."

                                esRegistro && contrasena != confirmar ->
                                    "Las contraseñas no coinciden."

                                contrasena.isEmpty() ->
                                    "Debes ingresar tu contraseña."

                                else -> null
                            }

                            if (error != null) {
                                mensajeLocal = error
                            } else if (esRegistro) {
                                esperandoInicioSesion = false
                                usuarioVm.registrar(
                                    nombre = nombre,
                                    correo = correo,
                                    contrasena = contrasena,
                                    rol = rol
                                )
                            } else {
                                esperandoInicioSesion = true
                                usuarioVm.iniciarSesion(
                                    correo = correo,
                                    contrasena = contrasena,
                                    rol = rol
                                )
                            }
                        },
                        enabled = !estado.cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorPrincipal
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (estado.cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                if (esRegistro) {
                                    "Crear cuenta"
                                } else {
                                    "Iniciar sesión"
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = if (esRegistro) {
                    "¿Ya tienes una cuenta?"
                } else {
                    "¿No tienes una cuenta?"
                },
                color = Color(0xFF52645A)
            )

            TextButton(
                onClick = {
                    esRegistro = !esRegistro
                    mensajeLocal = ""
                    esperandoInicioSesion = false
                    contrasena = ""
                    confirmar = ""
                    usuarioVm.limpiarEstado()
                },
                enabled = !estado.cargando
            ) {
                Text(
                    text = if (esRegistro) {
                        "Iniciar sesión"
                    } else {
                        "Registrarse"
                    },
                    color = colorPrincipal,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        TextButton(
            onClick = onVolver,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = colorPrincipal
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "Volver",
                color = colorPrincipal
            )
        }
    }
}
