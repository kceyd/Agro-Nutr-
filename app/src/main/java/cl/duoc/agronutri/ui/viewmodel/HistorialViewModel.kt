package cl.duoc.agronutri.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.local.MedicionConPredio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistorialViewModel(repo: AgroRepository) : ViewModel() {
    val mediciones: StateFlow<List<MedicionConPredio>> = repo.historial()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class RecomendacionViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: AgroRepository
) : ViewModel() {

    private val medicionId: Int = checkNotNull(savedStateHandle["medicionId"])

    val detalle: StateFlow<MedicionConPredio?> = repo.detalle(medicionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun validar() {
        viewModelScope.launch { repo.validar(medicionId) }
    }
}
