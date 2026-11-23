package cl.duoc.agronutri

import cl.duoc.agronutri.domain.Validacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionTest {

    @Test
    fun correoValidoEInvalido() {
        assertNull(Validacion.validarCorreo("sergio@duocuc.cl"))
        assertNotNull(Validacion.validarCorreo("sergio@"))
        assertNotNull(Validacion.validarCorreo("   "))
    }

    @Test
    fun claveDeRegistroExigeLargoMayusculaYNumero() {
        assertEquals("Mínimo 8 caracteres", Validacion.validarClaveRegistro("Ab1"))
        assertEquals("Falta una letra mayúscula", Validacion.validarClaveRegistro("abcdefg1"))
        assertEquals("Falta un número", Validacion.validarClaveRegistro("Abcdefgh"))
        assertNull(Validacion.validarClaveRegistro("Abcdefg1"))
    }

    @Test
    fun claveDeLoginSoloNoPuedeEstarVacia() {
        assertNotNull(Validacion.validarClaveLogin(""))
        assertNull(Validacion.validarClaveLogin("x"))
    }

    @Test
    fun confirmacionDebeCoincidir() {
        assertNotNull(Validacion.validarConfirmacion("Abcdefg1", ""))
        assertNotNull(Validacion.validarConfirmacion("Abcdefg1", "Abcdefg2"))
        assertNull(Validacion.validarConfirmacion("Abcdefg1", "Abcdefg1"))
    }

    @Test
    fun nombreDePersonaYPredio() {
        assertNotNull(Validacion.validarNombrePersona("  "))
        assertNotNull(Validacion.validarNombrePersona("Al"))
        assertNull(Validacion.validarNombrePersona("Ana"))
        assertNotNull(Validacion.validarSuperficie("0"))
        assertNull(Validacion.validarSuperficie("2,5"))
    }
}

class FiltroNumericoTest {
    @org.junit.Test
    fun quitaLetrasYSimbolos() {
        org.junit.Assert.assertEquals("6.8", Validacion.soloNumero("6a.8x"))
        org.junit.Assert.assertEquals("12,5", Validacion.soloNumero("1-2,5"))
        org.junit.Assert.assertEquals("", Validacion.soloNumero("abc"))
    }

    @org.junit.Test
    fun aceptaUnSoloSeparadorYLimitaDecimales() {
        org.junit.Assert.assertEquals("1.23", Validacion.soloNumero("1.2.3"))
        org.junit.Assert.assertEquals("0.123", Validacion.soloNumero("0.12345"))
    }
}
