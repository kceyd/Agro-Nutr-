package cl.duoc.agronutri.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.local.AppDatabase
import cl.duoc.agronutri.data.local.Usuario
import cl.duoc.agronutri.data.remote.RetrofitProvider
import cl.duoc.agronutri.data.remote.UsuarioRegistroDto
import cl.duoc.agronutri.domain.Validacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class EstadoUsuario(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val exito: Boolean = false
)

/** Campos del formulario de acceso que pueden tener un error visible. */
enum class CampoAcceso { NOMBRE, CORREO, CLAVE, CONFIRMAR }

/** Error (o null) de cada campo; la pantalla solo lo dibuja. */
data class ErroresAcceso(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmar: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(nombre, correo, clave, confirmar).any { it != null }
}

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.get(application).usuarioDao()

    private val _estado = MutableStateFlow(EstadoUsuario())
    val estado = _estado.asStateFlow()

    private val _errores = MutableStateFlow(ErroresAcceso())
    val errores: StateFlow<ErroresAcceso> = _errores.asStateFlow()

    fun limpiarEstado() {
        _estado.value = EstadoUsuario()
        _errores.value = ErroresAcceso()
    }

    /** Quita el error de un campo apenas el usuario vuelve a escribir en él. */
    fun limpiarError(campo: CampoAcceso) {
        _errores.value = when (campo) {
            CampoAcceso.NOMBRE -> _errores.value.copy(nombre = null)
            CampoAcceso.CORREO -> _errores.value.copy(correo = null)
            CampoAcceso.CLAVE -> _errores.value.copy(clave = null)
            CampoAcceso.CONFIRMAR -> _errores.value.copy(confirmar = null)
        }
    }

    /** Valida todo el formulario con las reglas de [Validacion]; true si no hay errores. */
    private fun validarFormulario(
        esRegistro: Boolean,
        nombre: String,
        correo: String,
        contrasena: String,
        confirmar: String
    ): Boolean {
        val errores = if (esRegistro) {
            ErroresAcceso(
                nombre = Validacion.validarNombrePersona(nombre),
                correo = Validacion.validarCorreo(correo),
                clave = Validacion.validarClaveRegistro(contrasena),
                confirmar = Validacion.validarConfirmacion(contrasena, confirmar)
            )
        } else {
            ErroresAcceso(
                correo = Validacion.validarCorreo(correo),
                clave = Validacion.validarClaveLogin(contrasena)
            )
        }
        _errores.value = errores
        return !errores.hayErrores
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        confirmar: String,
        rol: Rol
    ) {
        if (_estado.value.cargando) return
        if (!validarFormulario(true, nombre, correo, contrasena, confirmar)) return

        viewModelScope.launch {
            _estado.value = EstadoUsuario(cargando = true)

            try {
                val nombreLimpio = nombre.trim()
                val correoLimpio = correo.trim().lowercase()

                val existe = withContext(Dispatchers.IO) {
                    usuarioDao.buscarPorCorreo(correoLimpio)
                }

                if (existe != null) {
                    _estado.value = EstadoUsuario(
                        mensaje = "Este correo ya está registrado."
                    )
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    val salt = ByteArray(16)
                    SecureRandom().nextBytes(salt)

                    val hash = generarHash(contrasena, salt)

                    usuarioDao.insertar(
                        Usuario(
                            nombre = nombreLimpio,
                            correo = correoLimpio,
                            rol = rol.name,
                            passwordHash = hash,
                            passwordSalt = Base64.getEncoder().encodeToString(salt)
                        )
                    )

                    // Copia de la cuenta en usuarios-service. Es opcional: si no hay red o el
                    // servidor no responde, la cuenta local igual funciona (la app es offline-first).
                    if (AgroRepository.USAR_BACKEND_REAL) {
                        try {
                            RetrofitProvider.usuarios.registrar(
                                UsuarioRegistroDto(nombreLimpio, correoLimpio, contrasena, rol.name)
                            )
                        } catch (e: Exception) {
                            // sin red: se ignora
                        }
                    }
                }

                _estado.value = EstadoUsuario(
                    mensaje = "Cuenta creada correctamente. Ahora puedes iniciar sesión.",
                    exito = true
                )

            } catch (e: Exception) {
                _estado.value = EstadoUsuario(
                    mensaje = "No se pudo registrar la cuenta. Inténtalo nuevamente."
                )
            }
        }
    }

    fun iniciarSesion(
        correo: String,
        contrasena: String,
        rol: Rol
    ) {
        if (_estado.value.cargando) return
        if (!validarFormulario(false, "", correo, contrasena, "")) return

        viewModelScope.launch {
            _estado.value = EstadoUsuario(cargando = true)

            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioDao.buscarPorCorreo(correo.trim().lowercase())
                }

                if (usuario == null || usuario.rol != rol.name) {
                    _estado.value = EstadoUsuario(
                        mensaje = "Correo, contraseña o perfil incorrecto."
                    )
                    return@launch
                }

                val claveCorrecta = withContext(Dispatchers.Default) {
                    verificarClave(
                        contrasena,
                        usuario.passwordSalt,
                        usuario.passwordHash
                    )
                }

                if (!claveCorrecta) {
                    _estado.value = EstadoUsuario(
                        mensaje = "Correo, contraseña o perfil incorrecto."
                    )
                    return@launch
                }

                _estado.value = EstadoUsuario(
                    mensaje = "Inicio de sesión correcto.",
                    exito = true
                )

            } catch (e: Exception) {
                _estado.value = EstadoUsuario(
                    mensaje = "No se pudo iniciar sesión."
                )
            }
        }
    }

    private fun generarHash(
        clave: String,
        salt: ByteArray
    ): String {
        val spec = PBEKeySpec(clave.toCharArray(), salt, 210_000, 256)

        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val bytes = factory.generateSecret(spec).encoded
            Base64.getEncoder().encodeToString(bytes)
        } finally {
            spec.clearPassword()
        }
    }

    private fun verificarClave(
        clave: String,
        saltGuardado: String,
        hashGuardado: String
    ): Boolean {
        val salt = Base64.getDecoder().decode(saltGuardado)
        val hashCalculado = Base64.getDecoder().decode(
            generarHash(clave, salt)
        )
        val hashOriginal = Base64.getDecoder().decode(hashGuardado)

        return java.security.MessageDigest.isEqual(
            hashCalculado,
            hashOriginal
        )
    }
}
