package cl.duoc.agronutri

import android.app.Application
import android.content.Context
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.ClimaRepository
import cl.duoc.agronutri.data.NetworkMonitor
import cl.duoc.agronutri.data.local.AppDatabase
import cl.duoc.agronutri.data.remote.ClimaRetrofit

/** Contenedor simple de dependencias (inyección manual, sin Hilt para mantenerlo básico). */
class AppContainer(context: Context) {
    private val db = AppDatabase.get(context)
    val repository = AgroRepository(db.predioDao(), db.medicionDao())
    val networkMonitor = NetworkMonitor(context)

    // API externa (Open-Meteo) con su propio Retrofit, independiente de los microservicios.
    val climaRepository = ClimaRepository(ClimaRetrofit.api)
}

class AgroNutriApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
