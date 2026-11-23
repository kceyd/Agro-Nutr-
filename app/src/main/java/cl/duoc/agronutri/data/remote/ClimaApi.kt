package cl.duoc.agronutri.data.remote

import cl.duoc.agronutri.domain.ClimaActual
import cl.duoc.agronutri.domain.ClimaPredio
import cl.duoc.agronutri.domain.Clima
import cl.duoc.agronutri.domain.DiaPronostico
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// =====================================================================================
// API EXTERNA: Open-Meteo (https://open-meteo.com). Pronóstico del tiempo gratuito y sin
// API key. Está separada de ApiService.kt (microservicios propios) y tiene su propio Retrofit.
//
// Endpoint consumido:
//   GET https://api.open-meteo.com/v1/forecast
//       ?latitude=-32.88&longitude=-71.25
//       &current=temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m
//       &daily=temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration
//       &timezone=America/Santiago&forecast_days=3
// =====================================================================================

// ---------- DTOs: la forma exacta del JSON que responde Open-Meteo ----------
// Todos los campos son anulables: si la API omite uno, Gson deja null y no se cae la app.

data class OpenMeteoRespuesta(
    val latitude: Double?,
    val longitude: Double?,
    val timezone: String?,
    @SerializedName("utc_offset_seconds") val utcOffsetSegundos: Int?,
    val current: ActualDto?,
    val daily: DiarioDto?
)

data class ActualDto(
    val time: String?,
    @SerializedName("temperature_2m") val temperatura: Double?,
    @SerializedName("relative_humidity_2m") val humedad: Double?,
    val precipitation: Double?,
    @SerializedName("weather_code") val codigo: Int?,
    @SerializedName("wind_speed_10m") val viento: Double?
)

data class DiarioDto(
    val time: List<String>?,
    @SerializedName("temperature_2m_max") val maxima: List<Double?>?,
    @SerializedName("temperature_2m_min") val minima: List<Double?>?,
    @SerializedName("precipitation_sum") val lluvia: List<Double?>?,
    @SerializedName("et0_fao_evapotranspiration") val et0: List<Double?>?
)

// ---------- Contrato Retrofit ----------

interface OpenMeteoApi {
    /** Los parámetros con listas separadas por coma van sin codificar (encoded = true). */
    @GET("v1/forecast")
    suspend fun pronostico(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query(value = "current", encoded = true) actual: String,
        @Query(value = "daily", encoded = true) diario: String,
        @Query(value = "timezone", encoded = true) zonaHoraria: String,
        @Query("forecast_days") dias: Int
    ): OpenMeteoRespuesta
}

/** Retrofit propio de la API externa (HTTPS, distinto host que los microservicios). */
object ClimaRetrofit {
    private const val BASE_URL = "https://api.open-meteo.com/"

    const val VARIABLES_ACTUALES =
        "temperature_2m,relative_humidity_2m,precipitation,weather_code,wind_speed_10m"
    const val VARIABLES_DIARIAS =
        "temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration"
    const val DIAS_PRONOSTICO = 3

    val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}

// ---------- DTO -> modelo de dominio ----------

/** Convierte la respuesta en el modelo que usa la UI. Lanza IllegalStateException si falta "current". */
fun OpenMeteoRespuesta.aDominio(): ClimaPredio {
    val c = current ?: error("La respuesta de Open-Meteo no trae el bloque \"current\"")
    val actual = ClimaActual(
        hora = Clima.formatoHora(c.time.orEmpty()),
        temperaturaC = c.temperatura ?: 0.0,
        humedadPct = (c.humedad ?: 0.0).toInt(),
        precipitacionMm = c.precipitation ?: 0.0,
        vientoKmH = c.viento ?: 0.0,
        codigo = c.codigo ?: -1
    )
    val d = daily
    val fechas = d?.time.orEmpty()
    val pronostico = fechas.mapIndexed { i, fecha ->
        DiaPronostico(
            fecha = fecha,
            maxC = d?.maxima?.getOrNull(i) ?: 0.0,
            minC = d?.minima?.getOrNull(i) ?: 0.0,
            lluviaMm = d?.lluvia?.getOrNull(i) ?: 0.0,
            et0Mm = d?.et0?.getOrNull(i) ?: 0.0
        )
    }
    return ClimaPredio(actual, pronostico, timezone ?: Clima.ZONA_HORARIA)
}
