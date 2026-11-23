package cl.duoc.agronutri.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.formatoFecha
import cl.duoc.agronutri.ui.components.EstadoVacio
import cl.duoc.agronutri.ui.components.EtiquetaEstado
import cl.duoc.agronutri.ui.components.TarjetaApp
import cl.duoc.agronutri.ui.components.TipoEstado
import cl.duoc.agronutri.ui.components.entrada
import cl.duoc.agronutri.ui.components.presionable
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.HistorialViewModel
import cl.duoc.agronutri.ui.viewmodel.Rol

@Composable
fun HistorialScreen(
    rol: Rol?,
    onAbrir: (Int) -> Unit,
    vm: HistorialViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val todas by vm.mediciones.collectAsStateWithLifecycle()
    val esAsesor = rol == Rol.ASESOR
    var soloPorValidar by remember { mutableStateOf(esAsesor) }
    var consulta by remember { mutableStateOf("") }

    if (todas.isEmpty()) {
        EstadoVacio(
            icono = Icons.Filled.Search,
            titulo = "Aún no hay mediciones",
            mensaje = "Registra la primera desde la pestaña Predios.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val texto = consulta.trim()
    val mediciones = todas.filter { item ->
        val m = item.medicion
        (!soloPorValidar || !m.validada) &&
            (texto.isEmpty() ||
                item.predio.nombre.contains(texto, ignoreCase = true) ||
                m.cultivo.contains(texto, ignoreCase = true) ||
                m.etapa.contains(texto, ignoreCase = true))
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 340.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Buscador nuevo: filtra por predio, cultivo o etapa.
        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                label = { Text("Buscar por predio, cultivo o etapa") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !soloPorValidar,
                    onClick = { soloPorValidar = false },
                    label = { Text("Todas (${todas.size})") }
                )
                FilterChip(
                    selected = soloPorValidar,
                    onClick = { soloPorValidar = true },
                    label = { Text("Por validar (${todas.count { !it.medicion.validada }})") }
                )
            }
        }

        if (mediciones.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EstadoVacio(
                    icono = Icons.Filled.Check,
                    titulo = if (texto.isNotEmpty()) "Sin resultados" else "Todo al día",
                    mensaje = if (texto.isNotEmpty()) "Prueba con otra búsqueda." else "No hay mediciones pendientes de validar."
                )
            }
        }

        itemsIndexed(mediciones, key = { _, x -> x.medicion.id }) { indice, item ->
            val m = item.medicion
            TarjetaApp(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .entrada(indice)
                    .presionable { onAbrir(m.id) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("${m.cultivo} · ${m.etapa}", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${item.predio.nombre} · ${m.fechaMillis.formatoFecha()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EtiquetaEstado(
                            if (m.pendienteSync) "Pendiente" else "Sincronizado",
                            if (m.pendienteSync) TipoEstado.AVISO else TipoEstado.OK
                        )
                        EtiquetaEstado(
                            if (m.validada) "Validada" else "Sin validar",
                            if (m.validada) TipoEstado.OK else TipoEstado.NEUTRO
                        )
                    }
                    if (esAsesor && !m.validada) {
                        Button(
                            onClick = { vm.validar(m.id) },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Validar recomendación")
                        }
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(8.dp)) }
    }
}
