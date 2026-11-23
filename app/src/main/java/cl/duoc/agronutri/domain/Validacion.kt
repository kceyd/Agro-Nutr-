package cl.duoc.agronutri.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Campos que se miden en terreno, con su rango válido. */
enum class Campo(val etiqueta: String, val min: Double, val max: Double) {
    PH("pH", 3.0, 10.0),
    CE("CE (dS/m)", 0.0, 10.0),
    N("N (ppm)", 0.0, 500.0),
    P("P (ppm)", 0.0, 500.0),
    K("K (ppm)", 0.0, 1000.0),
    CA("Ca (ppm)", 0.0, 1000.0),
    MG("Mg (ppm)", 0.0, 500.0)
}

object Validacion {
    fun aNumero(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

    fun validarNombrePredio(texto: String): String? = when {
        texto.isBlank() -> "Ingresa un nombre"
        texto.trim().length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }

    fun validarSuperficie(texto: String): String? {
        val valor = aNumero(texto) ?: return "Ingresa un número"
        return if (valor <= 0.0 || valor > 1000.0) "Debe estar entre 0 y 1000 ha" else null
    }

    /**
     * Deja solo dígitos y un separador decimal (. o ,). Se usa al escribir, así los campos
     * numéricos no aceptan letras ni símbolos aunque se pegue texto o se use otro teclado.
     */
    fun soloNumero(texto: String, maxDecimales: Int = 3): String {
        val salida = StringBuilder()
        var separador = false
        var decimales = 0
        for (c in texto) {
            when {
                c.isDigit() && c in '0'..'9' -> {
                    if (separador) {
                        if (decimales < maxDecimales) { salida.append(c); decimales++ }
                    } else salida.append(c)
                }
                (c == '.' || c == ',') && !separador -> { separador = true; salida.append(c) }
            }
        }
        return salida.toString()
    }

    // ---- Coordenadas del predio (opcionales; se usan para el clima de la API externa) ----

    /** Igual que [soloNumero], pero acepta un signo "-" al inicio (en Chile latitud y longitud son negativas). */
    fun soloCoordenada(texto: String, maxDecimales: Int = 6): String {
        val limpio = texto.trim()
        val negativo = limpio.startsWith("-")
        val numero = soloNumero(if (negativo) limpio.drop(1) else limpio, maxDecimales)
        return if (negativo) "-$numero" else numero
    }

    /** Vacía = válida (es opcional). Si se escribe, debe ser un número entre -90 y 90. */
    fun validarLatitud(texto: String): String? = validarRango(texto, -90.0, 90.0)

    /** Vacía = válida (es opcional). Si se escribe, debe ser un número entre -180 y 180. */
    fun validarLongitud(texto: String): String? = validarRango(texto, -180.0, 180.0)

    /** Se completan las dos o ninguna. Devuelve el error para el campo vacío, o null. */
    fun validarParCoordenadas(latitud: String, longitud: String): String? =
        if (latitud.isBlank() != longitud.isBlank()) "Completa latitud y longitud, o deja ambas vacías" else null

    private fun validarRango(texto: String, min: Double, max: Double): String? {
        if (texto.isBlank()) return null
        val valor = aNumero(texto) ?: return "Ingresa un número"
        return if (valor < min || valor > max) "Debe estar entre ${min.toInt()} y ${max.toInt()}" else null
    }

    // ---- Validaciones de cuenta (acceso). Una sola fuente de verdad para UI y ViewModel ----

    private val CORREO_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validarNombrePersona(texto: String): String? = when {
        texto.isBlank() -> "Ingresa tu nombre"
        texto.trim().length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }

    fun validarCorreo(texto: String): String? {
        val correo = texto.trim()
        return when {
            correo.isEmpty() -> "Ingresa tu correo"
            !CORREO_REGEX.matches(correo) -> "Ingresa un correo válido"
            else -> null
        }
    }

    /** Reglas para crear una cuenta: 8+ caracteres, una mayúscula y un número. */
    fun validarClaveRegistro(clave: String): String? = when {
        clave.isEmpty() -> "Ingresa una contraseña"
        clave.length < 8 -> "Mínimo 8 caracteres"
        clave.none { it.isUpperCase() } -> "Falta una letra mayúscula"
        clave.none { it.isDigit() } -> "Falta un número"
        else -> null
    }

    /** Al iniciar sesión solo se exige que no esté vacía. */
    fun validarClaveLogin(clave: String): String? =
        if (clave.isEmpty()) "Ingresa tu contraseña" else null

    fun validarConfirmacion(clave: String, confirmar: String): String? = when {
        confirmar.isEmpty() -> "Confirma tu contraseña"
        clave != confirmar -> "Las contraseñas no coinciden"
        else -> null
    }

    /** Devuelve el mensaje de error o null si el valor es válido. */
    fun validarCampo(campo: Campo, texto: String): String? {
        val valor = aNumero(texto) ?: return "Ingresa un número"
        return if (valor < campo.min || valor > campo.max) {
            "Debe estar entre ${campo.min.toInt()} y ${campo.max.toInt()}"
        } else {
            null
        }
    }
}

fun Long.formatoFecha(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(this))
