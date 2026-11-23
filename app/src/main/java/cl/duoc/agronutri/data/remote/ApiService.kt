package cl.duoc.agronutri.data.remote

import cl.duoc.agronutri.data.local.Medicion
import cl.duoc.agronutri.data.local.Predio
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// ---------- DTOs (la forma del JSON que viaja a cada microservicio) ----------

data class PredioDto(
    val idLocal: Int,
    val nombre: String,
    val superficieHa: Double
)

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

data class UsuarioRegistroDto(
    val nombre: String,
    val correo: String,
    val password: String,
    val rol: String
)

// ---------- Contratos con los 3 microservicios Spring Boot (carpeta backend/) ----------

/** predios-service (puerto 8081). Idempotente: reenviar el mismo predio no lo duplica. */
interface PrediosApi {
    @POST("api/predios/sync")
    suspend fun sincronizar(@Body predios: List<PredioDto>): Response<Unit>
}

/** mediciones-service (puerto 8083). Idempotente por idLocal. */
interface MedicionesApi {
    @POST("api/mediciones/sync")
    suspend fun sincronizar(@Body mediciones: List<MedicionDto>): Response<Unit>
}

/** usuarios-service (puerto 8082). */
interface UsuariosApi {
    @POST("api/usuarios/registro")
    suspend fun registrar(@Body usuario: UsuarioRegistroDto): Response<Unit>
}

object RetrofitProvider {
    // 10.0.2.2 = localhost del PC visto desde el emulador de Android Studio.
    // En un teléfono real cambia HOST por la IP de tu PC (ej: "http://192.168.1.50")
    // y agrega esa IP en res/xml/network_security_config.xml.
    private const val HOST = "http://10.0.2.2"

    private fun <T> crear(puerto: Int, servicio: Class<T>): T =
        Retrofit.Builder()
            .baseUrl("$HOST:$puerto/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(servicio)

    val predios: PrediosApi by lazy { crear(8081, PrediosApi::class.java) }
    val usuarios: UsuariosApi by lazy { crear(8082, UsuariosApi::class.java) }
    val mediciones: MedicionesApi by lazy { crear(8083, MedicionesApi::class.java) }
}

fun Predio.aDto() = PredioDto(idLocal = id, nombre = nombre, superficieHa = superficieHa)

fun Medicion.aDto() = MedicionDto(
    idLocal = id, predioId = predioId, cultivo = cultivo, etapa = etapa,
    ph = ph, ce = ce, n = n, p = p, k = k, ca = ca, mg = mg,
    fechaMillis = fechaMillis, validada = validada
)
