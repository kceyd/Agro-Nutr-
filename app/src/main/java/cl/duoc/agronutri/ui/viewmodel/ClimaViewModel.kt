package cl.duoc.agronutri.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.ClimaRepository
import cl.duoc.agronutri.data.NetworkMonitor
import cl.duoc.agronutri.domain.Clima
import cl.duoc.agronutri.domain.ClimaPredio
import cl.duoc.agronutri.domain.Ubicacion
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Lo que la tarjeta de clima puede mostrar. */
sealed interface EstadoClima {
    data object Cargando : EstadoClima
    data object SinRed : EstadoClima
    data class Error(val mensaje: String) : EstadoClima
    data class Listo(val clima: ClimaPredio, val ubicacion: Ubicacion) : EstadoClima
}

/**
 * Estado de la API externa de clima para un predio. Si no hay red espera y, apenas vuelve,
 * consulta sola. El clima no se guarda en Room: es un dato en vivo.
 */
class ClimaViewModel(
    private val repo: ClimaRepository,
    monitor: NetworkMonitor
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoClima>(EstadoClima.Cargando)
    val estado: StateFlow<EstadoClima> = _estado.asStateFlow()

    private var ubicacion: Ubicacion? = null
    private var consulta: Job? = null
    private var enLinea = true

    init {
        viewModelScope.launch {
            monitor.enLinea.collect { online ->
                enLinea = online
                when {
                    !online && _estado.value !is EstadoClima.Listo -> _estado.value = EstadoClima.SinRed
                    // Volvió la red: reintenta solo si la consulta anterior no pudo completarse.
                    online && (_estado.value is EstadoClima.SinRed || _estado.value is EstadoClima.Error) -> recargar()
                }
            }
        }
    }

    /** Indica de qué predio mostrar el clima. Llamarla de nuevo con la misma ubicación no repite la consulta. */
    fun cargar(latitud: Double?, longitud: Double?) {
        val nueva = Clima.ubicacionDe(latitud, longitud)
        if (nueva == ubicacion && _estado.value is EstadoClima.Listo) return
        ubicacion = nueva
        recargar()
    }

    fun recargar() {
        val destino = ubicacion ?: return
        if (!enLinea) {
            _estado.value = EstadoClima.SinRed
            return
        }
        consulta?.cancel()
        consulta = viewModelScope.launch {
            _estado.value = EstadoClima.Cargando
            _estado.value = repo.clima(destino).fold(
                onSuccess = { EstadoClima.Listo(it, destino) },
                onFailure = { EstadoClima.Error(it.message ?: "No se pudo obtener el clima.") }
            )
        }
    }
}
