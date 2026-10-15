package cl.duoc.agronutri.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.Validacion
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.PrediosViewModel

@Composable
fun PrediosScreen(
    onRegistrar: (Int) -> Unit,
    vm: PrediosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val predios by vm.predios.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("+ Nuevo predio") }
        }
        items(predios, key = { it.predio.id }) { resumen ->
            Card(modifier = Modifier.fillMaxWidth().animateItem().animateContentSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(resumen.predio.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${resumen.predio.superficieHa} ha · ${resumen.total} medición(es)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onRegistrar(resumen.predio.id) }) {
                        Text("+ Registrar medición")
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        NuevoPredioDialogo(
            onCancelar = { mostrarDialogo = false },
            onGuardar = { nombre, superficie ->
                vm.crearPredio(nombre, superficie)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun NuevoPredioDialogo(
    onCancelar: () -> Unit,
    onGuardar: (String, Double) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var superficie by remember { mutableStateOf("") }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorSuperficie by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nuevo predio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; errorNombre = null },
                    label = { Text("Nombre del predio / sector") },
                    singleLine = true,
                    isError = errorNombre != null,
                    supportingText = {
                        AnimatedVisibility(visible = errorNombre != null) {
                            Text(errorNombre.orEmpty())
                        }
                    }
                )
                OutlinedTextField(
                    value = superficie,
                    onValueChange = { superficie = it; errorSuperficie = null },
                    label = { Text("Superficie (ha)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = errorSuperficie != null,
                    supportingText = {
                        AnimatedVisibility(visible = errorSuperficie != null) {
                            Text(errorSuperficie.orEmpty())
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                errorNombre = Validacion.validarNombrePredio(nombre)
                errorSuperficie = Validacion.validarSuperficie(superficie)
                if (errorNombre == null && errorSuperficie == null) {
                    onGuardar(nombre.trim(), Validacion.aNumero(superficie)!!)
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
