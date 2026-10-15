package cl.duoc.agronutri.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PredioDao {
    @Query(
        "SELECT p.*, (SELECT COUNT(*) FROM mediciones WHERE predioId = p.id) AS total " +
            "FROM predios p ORDER BY p.nombre"
    )
    fun observarResumen(): Flow<List<PredioResumen>>

    @Query("SELECT * FROM predios WHERE id = :id")
    suspend fun obtener(id: Int): Predio?

    @Insert
    suspend fun insertar(predio: Predio): Long
}

@Dao
interface MedicionDao {
    @Insert
    suspend fun insertar(medicion: Medicion): Long

    @Transaction
    @Query("SELECT * FROM mediciones ORDER BY fechaMillis DESC")
    fun observarHistorial(): Flow<List<MedicionConPredio>>

    @Transaction
    @Query("SELECT * FROM mediciones WHERE id = :id")
    fun observarDetalle(id: Int): Flow<MedicionConPredio?>

    @Query("SELECT COUNT(*) FROM mediciones WHERE pendienteSync = 1")
    fun observarPendientes(): Flow<Int>

    @Query("SELECT * FROM mediciones WHERE pendienteSync = 1")
    suspend fun pendientes(): List<Medicion>

    @Query("UPDATE mediciones SET pendienteSync = 0 WHERE id IN (:ids)")
    suspend fun marcarSincronizadas(ids: List<Int>)

    // Al validar, el registro vuelve a quedar pendiente para sincronizar el cambio.
    @Query("UPDATE mediciones SET validada = 1, pendienteSync = 1 WHERE id = :id")
    suspend fun validar(id: Int)
}
