package cl.duoc.agronutri.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.local.Predio
import cl.duoc.agronutri.data.local.PredioResumen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PrediosViewModel(private val repo: AgroRepository) : ViewModel() {
    val predios: StateFlow<List<PredioResumen>> = repo.predios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun crearPredio(nombre: String, superficieHa: Double) {
        viewModelScope.launch {
            repo.crearPredio(Predio(nombre = nombre, superficieHa = superficieHa))
        }
    }
}
