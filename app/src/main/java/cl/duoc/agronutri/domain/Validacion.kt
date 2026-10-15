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
