package cl.duoc.agronutri.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.Validacion
import cl.duoc.agronutri.ui.components.BannerEncabezado
import cl.duoc.agronutri.ui.components.CampoValidado
import cl.duoc.agronutri.ui.components.EstadisticaBanner
import cl.duoc.agronutri.ui.components.EstadoVacio
import cl.duoc.agronutri.ui.components.EtiquetaEstado
import cl.duoc.agronutri.ui.components.TarjetaApp
import cl.duoc.agronutri.ui.components.TipoEstado
import cl.duoc.agronutri.ui.components.entrada
import cl.duoc.agronutri.ui.theme.MentaInsignia
import cl.duoc.agronutri.ui.theme.TealOscuro
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.PrediosViewModel

@Composable
fun PrediosScreen(
    onRegistrar: (Int) -> Unit,
    vm: PrediosViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val predios by vm.predios.collectAsStateWithLifecycle()
    var mostrarDialogo by remember { mutableStateOf(false) }

    val totalHa = predios.sumOf { it.predio.superficieHa }.toFloat()
    val totalMediciones = predios.sumOf { it.total }

    // Adaptable: 1 columna en teléfono, 2 o 3 en tablet / teléfono girado.
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            BannerEncabezado(
                titulo = "Tus predios",
                subtitulo = "Registra una medición y recibe tu recomendación de fertirriego"
            ) {
                EstadisticaBanner(predios.size.toFloat(), "Predios", Modifier.weight(1f))
                EstadisticaBanner(totalHa, "Hectáreas", Modifier.weight(1f), decimales = 1)
                EstadisticaBanner(totalMediciones.toFloat(), "Mediciones", Modifier.weight(1f))
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Nuevo predio")
            }
        }

        if (predios.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EstadoVacio(
                    icono = Icons.Filled.Home,
                    titulo = "Aún no tienes predios",
                    mensaje = "Crea el primero con el botón \"Nuevo predio\"."
                )
            }
        }

        itemsIndexed(predios, key = { _, r -> r.predio.id }) { indice, resumen ->
            TarjetaApp(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
                    .entrada(indice)
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MentaInsignia),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Home, contentDescription = null, tint = TealOscuro)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(resumen.predio.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${resumen.predio.superficieHa} ha · ${resumen.total} medición(es)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                if (resumen.predio.latitud != null && resumen.predio.longitud != null) {
                                    "Ubicación: lat %.4f · lon %.4f".format(resumen.predio.latitud, resumen.predio.longitud)
                                } else {
                                    "Sin ubicación: el clima usa una zona de referencia"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    EtiquetaEstado(
                        texto = if (resumen.predio.pendienteSync) "Pendiente de sincronizar" else "Sincronizado",
                        tipo = if (resumen.predio.pendienteSync) TipoEstado.AVISO else TipoEstado.OK
                    )
                    Button(
                        onClick = { onRegistrar(resumen.predio.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar medición")
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(8.dp)) }
    }

    if (mostrarDialogo) {
        NuevoPredioDialogo(
            onCancelar = { mostrarDialogo = false },
            onGuardar = { nombre, superficie, latitud, longitud ->
                vm.crearPredio(nombre, superficie, latitud, longitud)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun NuevoPredioDialogo(
    onCancelar: () -> Unit,
    onGuardar: (String, Double, Double?, Double?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var superficie by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorSuperficie by remember { mutableStateOf<String?>(null) }
    var errorLatitud by remember { mutableStateOf<String?>(null) }
    var errorLongitud by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nuevo predio") },
        text = {
            // Con scroll: con 4 campos y el teclado abierto el diálogo no cabe en teléfonos pequeños.
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoValidado(
                    valor = nombre,
                    onValorChange = { nombre = it; errorNombre = null },
                    etiqueta = "Nombre del predio / sector",
                    error = errorNombre,
                    iconoInicial = Icons.Filled.Home
                )
                CampoValidado(
                    valor = superficie,
                    onValorChange = { superficie = Validacion.soloNumero(it); errorSuperficie = null },
                    etiqueta = "Superficie (ha)",
                    error = errorSuperficie,
                    iconoInicial = Icons.Filled.Info,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                // Ubicación opcional: con ella la API de clima muestra el tiempo del propio predio.
                CampoValidado(
                    valor = latitud,
                    onValorChange = { latitud = Validacion.soloCoordenada(it); errorLatitud = null },
                    etiqueta = "Latitud (opcional)",
                    error = errorLatitud,
                    ayuda = "Ej: -32.8790",
                    iconoInicial = Icons.Filled.Place,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                CampoValidado(
                    valor = longitud,
                    onValorChange = { longitud = Validacion.soloCoordenada(it); errorLongitud = null },
                    etiqueta = "Longitud (opcional)",
                    error = errorLongitud,
                    ayuda = "Ej: -71.2470",
                    iconoInicial = Icons.Filled.Place,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val errorPar = Validacion.validarParCoordenadas(latitud, longitud)
                errorNombre = Validacion.validarNombrePredio(nombre)
                errorSuperficie = Validacion.validarSuperficie(superficie)
                errorLatitud = Validacion.validarLatitud(latitud) ?: errorPar?.takeIf { latitud.isBlank() }
                errorLongitud = Validacion.validarLongitud(longitud) ?: errorPar?.takeIf { longitud.isBlank() }
                if (listOf(errorNombre, errorSuperficie, errorLatitud, errorLongitud).all { it == null }) {
                    onGuardar(
                        nombre.trim(),
                        Validacion.aNumero(superficie)!!,
                        Validacion.aNumero(latitud),
                        Validacion.aNumero(longitud)
                    )
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
