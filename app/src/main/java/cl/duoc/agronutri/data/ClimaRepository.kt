package cl.duoc.agronutri.data

import cl.duoc.agronutri.data.remote.ClimaRetrofit
import cl.duoc.agronutri.data.remote.OpenMeteoApi
import cl.duoc.agronutri.data.remote.aDominio
import cl.duoc.agronutri.domain.Clima
import cl.duoc.agronutri.domain.ClimaPredio
import cl.duoc.agronutri.domain.Ubicacion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Acceso a la API externa de clima (Open-Meteo). No pasa por Room: el clima es un dato
 * en vivo que solo se muestra, no se guarda ni se sincroniza con los microservicios.
 */
class ClimaRepository(private val api: OpenMeteoApi) {

    /** Devuelve el clima de la ubicación o un error con un mensaje listo para mostrar. */
    suspend fun clima(ubicacion: Ubicacion): Result<ClimaPredio> = withContext(Dispatchers.IO) {
        try {
            val respuesta = api.pronostico(
                latitud = ubicacion.latitud,
                longitud = ubicacion.longitud,
                actual = ClimaRetrofit.VARIABLES_ACTUALES,
                diario = ClimaRetrofit.VARIABLES_DIARIAS,
                zonaHoraria = Clima.ZONA_HORARIA,
                dias = ClimaRetrofit.DIAS_PRONOSTICO
            )
            Result.success(respuesta.aDominio())
        } catch (e: CancellationException) {
            throw e // la pantalla se cerró: no es un error
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet.", e))
        } catch (e: HttpException) {
            Result.failure(Exception("El servicio de clima respondió ${e.code()}.", e))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo leer el clima.", e))
        }
    }
}
