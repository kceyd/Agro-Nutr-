package cl.duoc.agronutri.data

import cl.duoc.agronutri.data.local.Medicion
import cl.duoc.agronutri.data.local.MedicionConPredio
import cl.duoc.agronutri.data.local.MedicionDao
import cl.duoc.agronutri.data.local.Predio
import cl.duoc.agronutri.data.local.PredioDao
import cl.duoc.agronutri.data.local.PredioResumen
import cl.duoc.agronutri.data.remote.RetrofitProvider
import cl.duoc.agronutri.data.remote.aDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

/**
 * Única fuente de verdad: Room (offline). La sincronización con el backend
 * se hace aparte, cuando hay red.
 */
class AgroRepository(
    private val predioDao: PredioDao,
    private val medicionDao: MedicionDao
) {
    companion object {
        // Déjalo en false mientras no tengas el backend Spring Boot corriendo.
        const val USAR_BACKEND_REAL = false
    }

    fun predios(): Flow<List<PredioResumen>> = predioDao.observarResumen()
    suspend fun predio(id: Int): Predio? = predioDao.obtener(id)
    suspend fun crearPredio(predio: Predio): Long = predioDao.insertar(predio)

    suspend fun guardarMedicion(medicion: Medicion): Long = medicionDao.insertar(medicion)
    fun historial(): Flow<List<MedicionConPredio>> = medicionDao.observarHistorial()
    fun detalle(id: Int): Flow<MedicionConPredio?> = medicionDao.observarDetalle(id)
    fun pendientes(): Flow<Int> = medicionDao.observarPendientes()
    suspend fun validar(id: Int) = medicionDao.validar(id)

    /** Envía los registros pendientes. Devuelve cuántos se sincronizaron. */
    suspend fun sincronizarPendientes(): Int {
        val lista = medicionDao.pendientes()
        if (lista.isEmpty()) return 0

        if (USAR_BACKEND_REAL) {
            val respuesta = RetrofitProvider.api.enviarMediciones(lista.map { it.aDto() })
            if (!respuesta.isSuccessful) error("Error del servidor: ${respuesta.code()}")
        } else {
            delay(1000) // simula la llamada al servidor
        }

        medicionDao.marcarSincronizadas(lista.map { it.id })
        return lista.size
    }
}
