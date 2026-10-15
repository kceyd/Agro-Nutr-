package cl.duoc.agronutri.data.remote

import cl.duoc.agronutri.data.local.Medicion
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class MedicionDto(
    val idLocal: Int,
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
    val fechaMillis: Long,
    val validada: Boolean
)

/** Contrato con el backend Spring Boot (API REST). */
interface ApiService {
    @POST("mediciones")
    suspend fun enviarMediciones(@Body mediciones: List<MedicionDto>): Response<Unit>
}

object RetrofitProvider {
    // 10.0.2.2 = localhost del PC visto desde el emulador de Android Studio.
    // Si usas http (sin TLS) agrega android:usesCleartextTraffic="true" en el manifest.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

fun Medicion.aDto() = MedicionDto(
    idLocal = id, predioId = predioId, cultivo = cultivo, etapa = etapa,
    ph = ph, ce = ce, n = n, p = p, k = k, ca = ca, mg = mg,
    fechaMillis = fechaMillis, validada = validada
)
