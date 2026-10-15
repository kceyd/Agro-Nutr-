package cl.duoc.agronutri.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.local.Medicion
import cl.duoc.agronutri.data.local.Predio
import cl.duoc.agronutri.domain.Campo
import cl.duoc.agronutri.domain.Fertirriego
import cl.duoc.agronutri.domain.Validacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class RegistroUiState(
    val cultivo: String = Fertirriego.cultivos.first(),
    val etapa: String = Fertirriego.etapas.first(),
    val valores: Map<Campo, String> = Campo.entries.associateWith { "" },
    val errores: Map<Campo, String> = emptyMap(),
    val fotoPath: String? = null
)

class RegistroViewModel(
    savedStateHandle: SavedStateHandle,
    private val context: Context,
    private val repo: AgroRepository
) : ViewModel() {

    private val predioId: Int = checkNotNull(savedStateHandle["predioId"])

    private val _ui = MutableStateFlow(RegistroUiState())
    val ui: StateFlow<RegistroUiState> = _ui.asStateFlow()

    private val _predio = MutableStateFlow<Predio?>(null)
    val predio: StateFlow<Predio?> = _predio.asStateFlow()

    init {
        viewModelScope.launch { _predio.value = repo.predio(predioId) }
    }

    fun onCultivo(cultivo: String) = _ui.update { it.copy(cultivo = cultivo) }
    fun onEtapa(etapa: String) = _ui.update { it.copy(etapa = etapa) }

    fun onValor(campo: Campo, texto: String) = _ui.update {
        it.copy(valores = it.valores + (campo to texto), errores = it.errores - campo)
    }

    /** Guarda la foto de la cámara en el almacenamiento interno de la app. */
    fun onFoto(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val archivo = File(context.filesDir, "foto_${System.currentTimeMillis()}.jpg")
            FileOutputStream(archivo).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
            _ui.update { it.copy(fotoPath = archivo.absolutePath) }
        }
    }

    fun guardar(onGuardado: (Int) -> Unit) {
        val estado = _ui.value
        val errores = Campo.entries.mapNotNull { campo ->
            Validacion.validarCampo(campo, estado.valores[campo].orEmpty())?.let { campo to it }
        }.toMap()

        if (errores.isNotEmpty()) {
            _ui.update { it.copy(errores = errores) }
            return
        }

        val num = { campo: Campo -> Validacion.aNumero(estado.valores.getValue(campo)) ?: 0.0 }
        viewModelScope.launch {
            val id = repo.guardarMedicion(
                Medicion(
                    predioId = predioId,
                    cultivo = estado.cultivo,
                    etapa = estado.etapa,
                    ph = num(Campo.PH), ce = num(Campo.CE),
                    n = num(Campo.N), p = num(Campo.P), k = num(Campo.K),
                    ca = num(Campo.CA), mg = num(Campo.MG),
                    fotoPath = estado.fotoPath
                )
            )
            onGuardado(id.toInt())
        }
    }
}
