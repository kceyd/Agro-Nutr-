package cl.duoc.agronutri.ui.screens

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.Campo
import cl.duoc.agronutri.domain.Fertirriego
import cl.duoc.agronutri.ui.components.CampoValidado
import cl.duoc.agronutri.ui.components.ContenidoAdaptable
import cl.duoc.agronutri.ui.components.Selector
import cl.duoc.agronutri.ui.components.TarjetaApp
import cl.duoc.agronutri.ui.components.TarjetaClima
import cl.duoc.agronutri.ui.components.entrada
import cl.duoc.agronutri.ui.components.esPantallaAncha
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onGuardado: (Int) -> Unit,
    vm: RegistroViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val predio by vm.predio.collectAsStateWithLifecycle()
    val ancha = esPantallaAncha()

    // Cámara nativa: devuelve una miniatura (Bitmap) sin necesitar permisos extra.
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) vm.onFoto(bitmap)
    }

    ContenidoAdaptable(anchoMaximo = 760.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(modifier = Modifier.entrada(0)) {
                Text("Nueva medición", style = MaterialTheme.typography.headlineSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        predio?.nombre ?: "Cargando predio...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 0) Clima actual del predio (API externa Open-Meteo): ayuda a decidir si fertirregar hoy.
            predio?.let { p ->
                TarjetaClima(predio = p, modifier = Modifier.fillMaxWidth().entrada(1))
            }

            // 1) Cultivo y etapa (lado a lado en pantallas anchas)
            TarjetaApp(modifier = Modifier.fillMaxWidth().entrada(2)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Cultivo", style = MaterialTheme.typography.titleMedium)
                    if (ancha) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Selector("Cultivo", ui.cultivo, Fertirriego.cultivos, vm::onCultivo, Modifier.weight(1f))
                            Selector("Estado fenológico", ui.etapa, Fertirriego.etapas, vm::onEtapa, Modifier.weight(1f))
                        }
                    } else {
                        Selector("Cultivo", ui.cultivo, Fertirriego.cultivos, vm::onCultivo, Modifier.fillMaxWidth())
                        Selector("Estado fenológico", ui.etapa, Fertirriego.etapas, vm::onEtapa, Modifier.fillMaxWidth())
                    }
                }
            }

            // 2) Los 7 campos de medición: de a 2 por fila en teléfono, de a 4 en pantallas anchas.
            TarjetaApp(modifier = Modifier.fillMaxWidth().entrada(3)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Mediciones de terreno", style = MaterialTheme.typography.titleMedium)
                    val columnas = if (ancha) 4 else 2
                    Campo.entries.chunked(columnas).forEach { fila ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            fila.forEach { campo ->
                                CampoValidado(
                                    valor = ui.valores[campo].orEmpty(),
                                    onValorChange = { vm.onValor(campo, it) },
                                    etiqueta = campo.etiqueta,
                                    error = ui.errores[campo],
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columnas - fila.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }

            // 3) Foto foliar: la vista previa aparece con animación al tomarla.
            TarjetaApp(modifier = Modifier.fillMaxWidth().entrada(4)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Foto foliar", style = MaterialTheme.typography.titleMedium)
                    OutlinedButton(onClick = { camara.launch(null) }) {
                        Text(if (ui.fotoPath == null) "Tomar foto foliar" else "Repetir foto foliar")
                    }
                    AnimatedVisibility(
                        visible = ui.fotoPath != null,
                        enter = fadeIn() + expandVertically()
                    ) {
                        val ruta = ui.fotoPath
                        val imagen = remember(ruta) { ruta?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() } }
                        if (imagen != null) {
                            Image(
                                bitmap = imagen,
                                contentDescription = "Foto foliar",
                                modifier = Modifier
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { vm.guardar(onGuardado) },
                modifier = Modifier.fillMaxWidth().entrada(5)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Guardar y estimar")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
