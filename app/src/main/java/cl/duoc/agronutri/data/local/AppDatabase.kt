
package cl.duoc.agronutri.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Predio::class,
        Medicion::class,
        Usuario::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun predioDao(): PredioDao
    abstract fun medicionDao(): MedicionDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var instancia: AppDatabase? = null


        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `usuarios` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `nombre` TEXT NOT NULL,
                `correo` TEXT NOT NULL,
                `rol` TEXT NOT NULL,
                `passwordHash` TEXT NOT NULL,
                `passwordSalt` TEXT NOT NULL
            )
            """.trimIndent()
                )

                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)"
                )
            }
        }


        private val datosIniciales = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL(
                    "INSERT INTO predios (nombre, superficieHa) VALUES ('Predio Los Aromos - Sector A', 4.5)"
                )
                db.execSQL(
                    "INSERT INTO predios (nombre, superficieHa) VALUES ('Predio Quebrada Verde - Sector 2', 2.1)"
                )
                db.execSQL(
                    "INSERT INTO predios (nombre, superficieHa) VALUES ('Predio El Canelo - Sector C', 6.8)"
                )
            }
        }

        fun get(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agronutri.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(datosIniciales)
                    .build()
                    .also { instancia = it }
            }
    }
}
