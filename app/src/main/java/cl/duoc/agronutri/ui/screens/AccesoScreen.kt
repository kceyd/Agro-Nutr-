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
import cl.duoc.agronutri.ui.components.CampoValidado
import cl.duoc.agronutri.ui.theme.AzulLogo
import cl.duoc.agronutri.ui.theme.GrisAzulClaro
import cl.duoc.agronutri.ui.theme.MentaClaro
import cl.duoc.agronutri.ui.theme.TealPrimario
import cl.duoc.agronutri.ui.viewmodel.CampoAcceso
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
        TealPrimario
    } else {
        AzulLogo
    }

    val colorFondo = if (esAgricultor) {
        MentaClaro
    } else {
        GrisAzulClaro
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
    var esperandoInicioSesion by remember { mutableStateOf(false) }

    val estado by usuarioVm.estado.collectAsStateWithLifecycle()
    val errores by usuarioVm.errores.collectAsStateWithLifecycle()

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
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        CampoValidado(
                            valor = nombre,
                            onValorChange = {
                                nombre = it
                                usuarioVm.limpiarError(CampoAcceso.NOMBRE)
                            },
                            etiqueta = "Nombre completo",
                            error = errores.nombre,
                            iconoInicial = Icons.Filled.Person,
                            enabled = !estado.cargando,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    CampoValidado(
                        valor = correo,
                        onValorChange = {
                            correo = it
                            usuarioVm.limpiarError(CampoAcceso.CORREO)
                        },
                        etiqueta = "Correo electrónico",
                        error = errores.correo,
                        iconoInicial = Icons.Filled.Email,
                        enabled = !estado.cargando,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    CampoValidado(
                        valor = contrasena,
                        onValorChange = {
                            contrasena = it
                            usuarioVm.limpiarError(CampoAcceso.CLAVE)
                        },
                        etiqueta = "Contraseña",
                        error = errores.clave,
                        ayuda = if (esRegistro) {
                            "Mínimo 8 caracteres, una mayúscula y un número"
                        } else {
                            null
                        },
                        iconoInicial = Icons.Filled.Lock,
                        enabled = !estado.cargando,
                        visualTransformation = if (mostrarClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(onClick = { mostrarClave = !mostrarClave }) {
                                Text(if (mostrarClave) "Ocultar" else "Ver")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (esRegistro) {
                        CampoValidado(
                            valor = confirmar,
                            onValorChange = {
                                confirmar = it
                                usuarioVm.limpiarError(CampoAcceso.CONFIRMAR)
                            },
                            etiqueta = "Confirmar contraseña",
                            error = errores.confirmar,
                            iconoInicial = Icons.Filled.Lock,
                            enabled = !estado.cargando,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Mensajes que vienen del resultado de la operación (ej. "correo ya registrado").
                    if (estado.mensaje.isNotEmpty()) {
                        Text(
                            text = estado.mensaje,
                            color = if (estado.exito) {
                                colorPrincipal
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (esRegistro) {
                                esperandoInicioSesion = false
                                usuarioVm.registrar(
                                    nombre = nombre,
                                    correo = correo,
                                    contrasena = contrasena,
                                    confirmar = confirmar,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = {
                    esRegistro = !esRegistro
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
