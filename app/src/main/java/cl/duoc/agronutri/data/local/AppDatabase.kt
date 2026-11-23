
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
    version = 4,
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


        // v3: los predios también se sincronizan con el microservicio de predios.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `predios` ADD COLUMN `pendienteSync` INTEGER NOT NULL DEFAULT 1")
            }
        }


        // v4: latitud y longitud del predio (opcionales) para la API externa de clima.
        // Los 3 predios de ejemplo reciben las mismas coordenadas que en una instalación nueva.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `predios` ADD COLUMN `latitud` REAL")
                db.execSQL("ALTER TABLE `predios` ADD COLUMN `longitud` REAL")
                db.execSQL(
                    "UPDATE `predios` SET `latitud` = -32.8790, `longitud` = -71.2470 " +
                        "WHERE `nombre` = 'Predio Los Aromos - Sector A'"
                )
                db.execSQL(
                    "UPDATE `predios` SET `latitud` = -32.8260, `longitud` = -71.2290 " +
                        "WHERE `nombre` = 'Predio Quebrada Verde - Sector 2'"
                )
                db.execSQL(
                    "UPDATE `predios` SET `latitud` = -32.7990, `longitud` = -71.1440 " +
                        "WHERE `nombre` = 'Predio El Canelo - Sector C'"
                )
            }
        }


        private val datosIniciales = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                // Coordenadas de ejemplo (valle de Aconcagua) para que el clima funcione desde el inicio.
                db.execSQL(
                    "INSERT INTO predios (nombre, superficieHa, latitud, longitud) " +
                        "VALUES ('Predio Los Aromos - Sector A', 4.5, -32.8790, -71.2470)"
                )
                db.execSQL(
                    "INSERT INTO predios (nombre, superficieHa, latitud, longitud) " +
                        "VALUES ('Predio Quebrada Verde - Sector 2', 2.1, -32.8260, -71.2290)"
                )
                db.execSQL(
                    "INSERT INTO predios (nombre, superficieHa, latitud, longitud) " +
                        "VALUES ('Predio El Canelo - Sector C', 6.8, -32.7990, -71.1440)"
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .addCallback(datosIniciales)
                    .build()
                    .also { instancia = it }
            }
    }
}
