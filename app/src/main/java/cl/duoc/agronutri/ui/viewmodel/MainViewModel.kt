package cl.duoc.agronutri.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.NetworkMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Rol(val etiqueta: String) {
    AGRICULTOR("Agricultor"),
    ASESOR("Asesor agrónomo")
}

/** Estado global: perfil activo, conexión y sincronización automática. */
class MainViewModel(
    private val repo: AgroRepository,
    monitor: NetworkMonitor
) : ViewModel() {

    private val _rol = MutableStateFlow<Rol?>(null)
    val rol: StateFlow<Rol?> = _rol.asStateFlow()

    val enLinea: StateFlow<Boolean> = monitor.enLinea
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val pendientes: StateFlow<Int> = repo.pendientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _mensajes = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val mensajes = _mensajes.asSharedFlow()

    init {
        // Cada vez que haya red y registros pendientes, se sincroniza solo.
        viewModelScope.launch {
            combine(monitor.enLinea, repo.pendientes()) { online, n -> online && n > 0 }
                .filter { it }
                .collect {
                    try {
                        val n = repo.sincronizarPendientes()
                        if (n > 0) _mensajes.emit("Sincronizados $n registro(s)")
                    } catch (e: Exception) {
                        _mensajes.emit("No se pudo sincronizar, se reintentará")
                    }
                }
        }
    }

    fun entrar(rol: Rol) {
        _rol.value = rol
    }

    fun salir() {
        _rol.value = null
    }
}
