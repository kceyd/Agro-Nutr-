package cl.duoc.agronutri.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.local.AppDatabase
import cl.duoc.agronutri.data.local.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.get(application).usuarioDao()

    private val _estado = MutableStateFlow(EstadoUsuario())
    val estado = _estado.asStateFlow()

    fun limpiarEstado() {
        _estado.value = EstadoUsuario()
    }

    fun registrar(
        nombre: String,
        correo: String,
        contrasena: String,
        rol: Rol
    ) {
        if (_estado.value.cargando) return

        viewModelScope.launch {
            _estado.value = EstadoUsuario(cargando = true)

            try {
                val nombreLimpio = nombre.trim()
                val correoLimpio = correo.trim().lowercase()

                if (nombreLimpio.isBlank()) {
                    _estado.value = EstadoUsuario(
                        mensaje = "Debes ingresar tu nombre."
                    )
                    return@launch
                }

                if (!android.util.Patterns.EMAIL_ADDRESS
                        .matcher(correoLimpio).matches()
                ) {
                    _estado.value = EstadoUsuario(
                        mensaje = "Ingresa un correo válido."
                    )
                    return@launch
                }

                if (!claveValida(contrasena)) {
                    _estado.value = EstadoUsuario(
                        mensaje = "La contraseña necesita 8 caracteres, una mayúscula y un número."
                    )
                    return@launch
                }

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

    private fun claveValida(clave: String): Boolean {
        return clave.length >= 8 &&
                clave.any { it.isUpperCase() } &&
                clave.any { it.isDigit() }
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
