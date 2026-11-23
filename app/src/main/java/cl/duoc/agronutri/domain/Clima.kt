package cl.duoc.agronutri.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Punto geográfico desde el que se consulta el clima. */
data class Ubicacion(
    val latitud: Double,
    val longitud: Double,
    /** true = el predio no tiene coordenadas y se usa la ubicación de referencia. */
    val esReferencia: Boolean = false
)

/** Condiciones actuales en la ubicación del predio. */
data class ClimaActual(
    val hora: String,            // "HH:mm" en la zona horaria pedida
    val temperaturaC: Double,
    val humedadPct: Int,
    val precipitacionMm: Double,
    val vientoKmH: Double,
    val codigo: Int
) {
    val descripcion: String get() = Clima.descripcion(codigo)
}

/** Resumen de un día del pronóstico. */
data class DiaPronostico(
    val fecha: String,           // ISO "yyyy-MM-dd"
    val maxC: Double,
    val minC: Double,
    val lluviaMm: Double,
    /** Evapotranspiración de referencia FAO-56 (mm/día): cuánta agua pierde el cultivo. */
    val et0Mm: Double
)

data class ClimaPredio(
    val actual: ClimaActual,
    val pronostico: List<DiaPronostico>,
    val zonaHoraria: String
)

/**
 * Reglas del clima, sin dependencias de Android (se prueban en ClimaTest).
 * Los datos vienen de la API externa Open-Meteo (ver data/remote/ClimaApi.kt).
 */
object Clima {

    /** Números y fechas en formato chileno (coma decimal, días en español). */
    val LOCALE_CL: Locale = Locale("es", "CL")

    /** Zona horaria con la que se piden horas y días (la de los usuarios en Chile). */
    const val ZONA_HORARIA = "America/Santiago"

    /** Ubicación usada cuando el predio no tiene coordenadas: Quillota, zona de paltos y cítricos. */
    val REFERENCIA = Ubicacion(latitud = -32.8833, longitud = -71.2489, esReferencia = true)
    const val NOMBRE_REFERENCIA = "Quillota"

    /** Lluvia acumulada (mm en 48 h) desde la que conviene esperar antes de fertirregar. */
    const val UMBRAL_LLUVIA_MM = 5.0

    /** ET0 diaria (mm) desde la que la demanda de agua se considera alta. */
    const val UMBRAL_ET0_ALTA_MM = 5.0

    fun ubicacionDe(latitud: Double?, longitud: Double?): Ubicacion =
        if (latitud != null && longitud != null) Ubicacion(latitud, longitud) else REFERENCIA

    /** Texto en español para los códigos meteorológicos WMO que entrega Open-Meteo. */
    fun descripcion(codigo: Int): String = when (codigo) {
        0 -> "Despejado"
        1 -> "Mayormente despejado"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Niebla"
        51, 53, 55 -> "Llovizna"
        56, 57 -> "Llovizna helada"
        61 -> "Lluvia débil"
        63 -> "Lluvia moderada"
        65 -> "Lluvia intensa"
        66, 67 -> "Lluvia helada"
        71, 73, 75, 77 -> "Nieve"
        80, 81, 82 -> "Chubascos"
        85, 86 -> "Chubascos de nieve"
        95, 97 -> "Tormenta"
        96, 99 -> "Tormenta con granizo"
        else -> "Sin datos"
    }

    /**
     * Aviso orientativo para el fertirriego según el pronóstico:
     * con lluvia en las próximas 48 h el fertilizante puede lavarse; con ET0 alta el cultivo pide más agua.
     * Devuelve null si no hay nada que advertir.
     */
    fun avisoRiego(pronostico: List<DiaPronostico>): String? {
        val lluvia48h = pronostico.take(2).sumOf { it.lluviaMm }
        val et0Hoy = pronostico.firstOrNull()?.et0Mm ?: 0.0
        return when {
            lluvia48h >= UMBRAL_LLUVIA_MM ->
                "Lluvia prevista (%.1f mm en 48 h): considera posponer el fertirriego.".format(LOCALE_CL, lluvia48h)
            et0Hoy >= UMBRAL_ET0_ALTA_MM ->
                "Demanda de agua alta (ET0 %.1f mm/día): revisa la frecuencia de riego.".format(LOCALE_CL, et0Hoy)
            else -> null
        }
    }

    private val FORMATO_DIA = DateTimeFormatter.ofPattern("EEE d", LOCALE_CL)

    /** "2026-11-23" -> "lun 23". Si el texto no es una fecha, lo devuelve tal cual. */
    fun formatoDia(fechaIso: String): String =
        try {
            LocalDate.parse(fechaIso).format(FORMATO_DIA).replace(".", "")
        } catch (e: Exception) {
            fechaIso
        }

    /** "2026-11-22T21:45" -> "21:45". */
    fun formatoHora(fechaHoraIso: String): String =
        fechaHoraIso.substringAfter('T', "").take(5).ifEmpty { fechaHoraIso }
}
