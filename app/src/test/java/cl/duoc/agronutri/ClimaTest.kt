package cl.duoc.agronutri

import cl.duoc.agronutri.data.remote.OpenMeteoRespuesta
import cl.duoc.agronutri.data.remote.aDominio
import cl.duoc.agronutri.domain.Clima
import cl.duoc.agronutri.domain.DiaPronostico
import cl.duoc.agronutri.domain.Validacion
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la API externa de clima: lectura del JSON de Open-Meteo y reglas de dominio. */
class ClimaTest {

    // Respuesta con la misma forma que entrega GET https://api.open-meteo.com/v1/forecast
    private val json = """
        {
          "latitude": -32.875, "longitude": -71.25,
          "utc_offset_seconds": -10800, "timezone": "America/Santiago",
          "timezone_abbreviation": "GMT-3", "elevation": 128.0,
          "current_units": {"time": "iso8601", "temperature_2m": "°C"},
          "current": {
            "time": "2026-11-22T21:45", "interval": 900,
            "temperature_2m": 18.4, "relative_humidity_2m": 71,
            "precipitation": 0.0, "weather_code": 2, "wind_speed_10m": 9.7
          },
          "daily_units": {"time": "iso8601"},
          "daily": {
            "time": ["2026-11-22", "2026-11-23", "2026-11-24"],
            "temperature_2m_max": [24.1, 25.3, 23.0],
            "temperature_2m_min": [10.2, 11.0, 12.4],
            "precipitation_sum": [0.0, 1.2, 6.5],
            "et0_fao_evapotranspiration": [4.8, 5.1, 3.9]
          }
        }
    """.trimIndent()

    @Test
    fun leeElJsonDeOpenMeteo() {
        val clima = Gson().fromJson(json, OpenMeteoRespuesta::class.java).aDominio()
        assertEquals(18.4, clima.actual.temperaturaC, 0.001)
        assertEquals(71, clima.actual.humedadPct)
        assertEquals(9.7, clima.actual.vientoKmH, 0.001)
        assertEquals("21:45", clima.actual.hora)
        assertEquals("Parcialmente nublado", clima.actual.descripcion)
        assertEquals("America/Santiago", clima.zonaHoraria)
        assertEquals(3, clima.pronostico.size)
        assertEquals(6.5, clima.pronostico[2].lluviaMm, 0.001)
        assertEquals(4.8, clima.pronostico[0].et0Mm, 0.001)
    }

    @Test
    fun valoresFaltantesNoRompenLaLectura() {
        val parcial = """{"timezone":"America/Santiago","current":{"time":"2026-11-22T21:45"}}"""
        val clima = Gson().fromJson(parcial, OpenMeteoRespuesta::class.java).aDominio()
        assertEquals(0.0, clima.actual.temperaturaC, 0.001)
        assertEquals("Sin datos", clima.actual.descripcion)
        assertTrue(clima.pronostico.isEmpty())
    }

    @Test
    fun codigosWmoEnEspanol() {
        assertEquals("Despejado", Clima.descripcion(0))
        assertEquals("Niebla", Clima.descripcion(45))
        assertEquals("Lluvia moderada", Clima.descripcion(63))
        assertEquals("Tormenta con granizo", Clima.descripcion(99))
        assertEquals("Sin datos", Clima.descripcion(1234))
    }

    private fun dia(lluvia: Double, et0: Double) = DiaPronostico("2026-11-23", 25.0, 10.0, lluvia, et0)

    @Test
    fun avisoDeLluviaEn48Horas() {
        // 2 + 3,5 = 5,5 mm en los dos primeros días: supera el umbral de 5 mm.
        val aviso = Clima.avisoRiego(listOf(dia(2.0, 3.0), dia(3.5, 3.0), dia(20.0, 3.0)))
        assertNotNull(aviso)
        assertTrue(aviso!!.startsWith("Lluvia prevista"))
    }

    @Test
    fun avisoDeDemandaAltaYSinAviso() {
        assertTrue(Clima.avisoRiego(listOf(dia(0.0, 5.6)))!!.startsWith("Demanda de agua alta"))
        assertNull(Clima.avisoRiego(listOf(dia(1.0, 4.0), dia(1.0, 4.0))))
        assertNull(Clima.avisoRiego(emptyList()))
    }

    @Test
    fun sinCoordenadasSeUsaLaReferencia() {
        assertTrue(Clima.ubicacionDe(null, -71.0).esReferencia)
        val propia = Clima.ubicacionDe(-32.8, -71.2)
        assertEquals(-32.8, propia.latitud, 0.0001)
        assertTrue(!propia.esReferencia)
    }

    @Test
    fun formatoDeHora() {
        assertEquals("21:45", Clima.formatoHora("2026-11-22T21:45"))
        assertEquals("sin-hora", Clima.formatoHora("sin-hora"))
        assertEquals("texto", Clima.formatoDia("texto"))
    }
}

class CoordenadasTest {

    @Test
    fun aceptaSignoNegativoSoloAlInicio() {
        assertEquals("-32.879", Validacion.soloCoordenada("-32.879"))
        assertEquals("-71.24", Validacion.soloCoordenada("-71-.24"))
        assertEquals("32.5", Validacion.soloCoordenada("3-2.5"))
    }

    @Test
    fun rangosDeLatitudYLongitud() {
        assertNull(Validacion.validarLatitud(""))
        assertNull(Validacion.validarLatitud("-32,879"))
        assertEquals("Debe estar entre -90 y 90", Validacion.validarLatitud("-95"))
        assertEquals("Ingresa un número", Validacion.validarLatitud("-"))
        assertNull(Validacion.validarLongitud("-71.247"))
        assertEquals("Debe estar entre -180 y 180", Validacion.validarLongitud("200"))
    }

    @Test
    fun coordenadasVanEnPar() {
        assertNull(Validacion.validarParCoordenadas("", ""))
        assertNull(Validacion.validarParCoordenadas("-32.8", "-71.2"))
        assertNotNull(Validacion.validarParCoordenadas("-32.8", ""))
    }
}
