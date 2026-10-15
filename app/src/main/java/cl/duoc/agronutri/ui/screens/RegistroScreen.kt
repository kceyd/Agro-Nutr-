package cl.duoc.agronutri.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.Campo
import cl.duoc.agronutri.domain.Fertirriego
import cl.duoc.agronutri.ui.components.Selector
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onGuardado: (Int) -> Unit,
    vm: RegistroViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val predio by vm.predio.collectAsStateWithLifecycle()

    // Cámara nativa: devuelve una miniatura (Bitmap) sin necesitar permisos extra.
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) vm.onFoto(bitmap)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nueva medición", style = MaterialTheme.typography.titleLarge)
        Text(predio?.nombre ?: "Cargando predio...", style = MaterialTheme.typography.bodyMedium)

        Selector("Cultivo", ui.cultivo, Fertirriego.cultivos, vm::onCultivo, Modifier.fillMaxWidth())
        Selector("Estado fenológico", ui.etapa, Fertirriego.etapas, vm::onEtapa, Modifier.fillMaxWidth())

        // Los 7 campos de medición, de a dos por fila.
        Campo.entries.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                fila.forEach { campo ->
                    OutlinedTextField(
                        value = ui.valores[campo].orEmpty(),
                        onValueChange = { vm.onValor(campo, it) },
                        label = { Text(campo.etiqueta) },
                        singleLine = true,
                        isError = campo in ui.errores,
                        supportingText = {
                            AnimatedVisibility(visible = campo in ui.errores) {
                                Text(ui.errores[campo].orEmpty())
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        OutlinedButton(onClick = { camara.launch(null) }) {
            Text(if (ui.fotoPath == null) "Tomar foto foliar" else "Repetir foto foliar")
        }
        ui.fotoPath?.let { ruta ->
            val imagen = remember(ruta) { BitmapFactory.decodeFile(ruta)?.asImageBitmap() }
            if (imagen != null) {
                Image(
                    bitmap = imagen,
                    contentDescription = "Foto foliar",
                    modifier = Modifier
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        Button(
            onClick = { vm.guardar(onGuardado) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar y estimar")
        }
    }
}
