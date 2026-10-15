package cl.duoc.agronutri.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "predios")
data class Predio(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val superficieHa: Double
)

@Entity(
    tableName = "mediciones",
    foreignKeys = [
        ForeignKey(
            entity = Predio::class,
            parentColumns = ["id"],
            childColumns = ["predioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("predioId")]
)
data class Medicion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val predioId: Int,
    val cultivo: String,
    val etapa: String,
    val ph: Double,
    val ce: Double,
    val n: Double,
    val p: Double,
    val k: Double,
    val ca: Double,
    val mg: Double,
    val fotoPath: String? = null,
    val fechaMillis: Long = System.currentTimeMillis(),
    val validada: Boolean = false,
    val pendienteSync: Boolean = true
)

/** Predio con su número de mediciones (para la lista de predios). */
data class PredioResumen(
    @Embedded val predio: Predio,
    val total: Int
)

/** Medición junto a su predio (para historial y detalle). */
data class MedicionConPredio(
    @Embedded val medicion: Medicion,
    @Relation(parentColumn = "predioId", entityColumn = "id")
    val predio: Predio
)
