package cl.duoc.agronutri.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Predio::class, Medicion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun predioDao(): PredioDao
    abstract fun medicionDao(): MedicionDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Datos simulados y anonimizados (requisito del caso: Ley 21.719 y 19.628).
        private val datosIniciales = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO predios (nombre, superficieHa) VALUES ('Predio Los Aromos - Sector A', 4.5)")
                db.execSQL("INSERT INTO predios (nombre, superficieHa) VALUES ('Predio Quebrada Verde - Sector 2', 2.1)")
                db.execSQL("INSERT INTO predios (nombre, superficieHa) VALUES ('Predio El Canelo - Sector C', 6.8)")
            }
        }

        fun get(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agronutri.db"
                )
                    .addCallback(datosIniciales)
                    .build()
                    .also { instancia = it }
            }
    }
}
