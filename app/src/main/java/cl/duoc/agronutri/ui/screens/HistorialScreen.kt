package cl.duoc.agronutri.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.formatoFecha
import cl.duoc.agronutri.ui.components.EtiquetaEstado
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.HistorialViewModel

@Composable
fun HistorialScreen(
    onAbrir: (Int) -> Unit,
    vm: HistorialViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val mediciones by vm.mediciones.collectAsStateWithLifecycle()

    if (mediciones.isEmpty()) {
        Text(
            "Aún no hay mediciones. Registra la primera desde la pestaña Predios.",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(mediciones, key = { it.medicion.id }) { item ->
            val m = item.medicion
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAbrir(m.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${m.cultivo} · ${m.etapa}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${item.predio.nombre} · ${m.fechaMillis.formatoFecha()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EtiquetaEstado(if (m.pendienteSync) "Pendiente" else "Sincronizado")
                        EtiquetaEstado(if (m.validada) "Validada" else "Sin validar")
                    }
                }
            }
        }
    }
}
