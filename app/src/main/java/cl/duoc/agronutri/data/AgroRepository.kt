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
import kotlinx.coroutines.flow.combine

/**
 * Única fuente de verdad: Room (offline). La sincronización con los microservicios
 * se hace aparte, cuando hay red.
 */
class AgroRepository(
    private val predioDao: PredioDao,
    private val medicionDao: MedicionDao
) {
    companion object {
        // false = la sincronización se simula (la app funciona sin backend).
        // true  = envía a predios-service (8081) y mediciones-service (8083). Ver backend/README.md
        const val USAR_BACKEND_REAL = false
    }

    fun predios(): Flow<List<PredioResumen>> = predioDao.observarResumen()
    suspend fun predio(id: Int): Predio? = predioDao.obtener(id)
    suspend fun crearPredio(predio: Predio): Long = predioDao.insertar(predio)

    suspend fun guardarMedicion(medicion: Medicion): Long = medicionDao.insertar(medicion)
    fun historial(): Flow<List<MedicionConPredio>> = medicionDao.observarHistorial()
    fun detalle(id: Int): Flow<MedicionConPredio?> = medicionDao.observarDetalle(id)
    suspend fun validar(id: Int) = medicionDao.validar(id)

    /** Registros sin enviar: predios nuevos + mediciones nuevas o validadas. */
    fun pendientes(): Flow<Int> =
        combine(predioDao.observarPendientes(), medicionDao.observarPendientes()) { a, b -> a + b }

    /** Envía lo pendiente (primero predios, luego mediciones). Devuelve cuántos registros se sincronizaron. */
    suspend fun sincronizarPendientes(): Int {
        val predios = predioDao.pendientes()
        val mediciones = medicionDao.pendientes()
        if (predios.isEmpty() && mediciones.isEmpty()) return 0

        if (USAR_BACKEND_REAL) {
            if (predios.isNotEmpty()) {
                val r = RetrofitProvider.predios.sincronizar(predios.map { it.aDto() })
                if (!r.isSuccessful) error("predios-service respondió ${r.code()}")
            }
            if (mediciones.isNotEmpty()) {
                val r = RetrofitProvider.mediciones.sincronizar(mediciones.map { it.aDto() })
                if (!r.isSuccessful) error("mediciones-service respondió ${r.code()}")
            }
        } else {
            delay(1000) // simula la llamada al servidor
        }

        if (predios.isNotEmpty()) predioDao.marcarSincronizados(predios.map { it.id })
        if (mediciones.isNotEmpty()) medicionDao.marcarSincronizadas(mediciones.map { it.id })
        return predios.size + mediciones.size
    }
}
