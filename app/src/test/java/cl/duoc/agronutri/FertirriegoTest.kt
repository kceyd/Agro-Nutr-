package cl.duoc.agronutri

import cl.duoc.agronutri.data.local.Medicion
import cl.duoc.agronutri.domain.Campo
import cl.duoc.agronutri.domain.Fertirriego
import cl.duoc.agronutri.domain.Nutriente
import cl.duoc.agronutri.domain.Validacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FertirriegoTest {

    private fun medicion(n: Double) = Medicion(
        predioId = 1, cultivo = "Palto", etapa = "Floración",
        ph = 6.5, ce = 1.2, n = n, p = 30.0, k = 200.0, ca = 160.0, mg = 55.0
    )

    @Test
    fun deficitSeCalculaContraElRequerimiento() {
        // Palto en floración requiere N = 40 ppm; con 20 ppm el déficit es 20 ppm.
        val dosis = Fertirriego.calcular(medicion(n = 20.0)).first { it.nutriente == Nutriente.N }
        assertEquals(20.0, dosis.deficitPpm, 0.001)
        assertEquals(7.0, dosis.kgPorHa, 0.001)
    }

    @Test
    fun sinDeficitNoHayDosis() {
        val dosis = Fertirriego.calcular(medicion(n = 80.0)).first { it.nutriente == Nutriente.N }
        assertEquals(0.0, dosis.kgPorHa, 0.001)
    }

    @Test
    fun validaRangosDeCampos() {
        assertNull(Validacion.validarCampo(Campo.PH, "6,5"))
        assertNotNull(Validacion.validarCampo(Campo.PH, "15"))
        assertNotNull(Validacion.validarCampo(Campo.N, ""))
    }
}
