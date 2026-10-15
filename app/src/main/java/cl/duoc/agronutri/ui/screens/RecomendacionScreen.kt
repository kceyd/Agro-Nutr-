package cl.duoc.agronutri.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.Fertirriego
import cl.duoc.agronutri.domain.formatoFecha
import cl.duoc.agronutri.ui.components.EtiquetaEstado
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.RecomendacionViewModel
import cl.duoc.agronutri.ui.viewmodel.Rol

@Composable
fun RecomendacionScreen(
    rol: Rol?,
    vm: RecomendacionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val detalle by vm.detalle.collectAsStateWithLifecycle()
    val item = detalle
    if (item == null) {
        Text("Cargando...", modifier = Modifier.padding(16.dp))
        return
    }
    val m = item.medicion
    val dosis = remember(m) { Fertirriego.calcular(m) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recomendación de fertirriego", style = MaterialTheme.typography.titleLarge)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("${m.cultivo} · ${m.etapa}", style = MaterialTheme.typography.titleMedium)
                Text("${item.predio.nombre} · ${m.fechaMillis.formatoFecha()}", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EtiquetaEstado(if (m.pendienteSync) "Pendiente de sincronizar" else "Sincronizado")
                    EtiquetaEstado(if (m.validada) "Validada" else "Sin validar")
                }
                Text(
                    "pH ${m.ph} · CE ${m.ce} dS/m",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Nutriente", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Déficit", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Fertilizante", Modifier.weight(2f), fontWeight = FontWeight.Bold)
                    Text("kg/ha", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(Modifier.padding(vertical = 6.dp))
                dosis.forEach { d ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(d.nutriente.simbolo, Modifier.weight(1f))
                        Text("%.0f ppm".format(d.deficitPpm), Modifier.weight(1f))
                        Text(d.nutriente.fertilizante, Modifier.weight(2f))
                        Text("%.1f".format(d.kgPorHa), Modifier.weight(1f))
                    }
                }
            }
        }

        m.fotoPath?.let { ruta ->
            val imagen = remember(ruta) { BitmapFactory.decodeFile(ruta)?.asImageBitmap() }
            if (imagen != null) {
                Image(
                    bitmap = imagen,
                    contentDescription = "Foto foliar",
                    modifier = Modifier
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        Text(
            "Estimación básica con datos ficticios. Debe ser validada por un asesor agrónomo.",
            style = MaterialTheme.typography.bodySmall
        )

        if (!m.validada) {
            if (rol == Rol.ASESOR) {
                Button(onClick = vm::validar, modifier = Modifier.fillMaxWidth()) {
                    Text("Validar recomendación")
                }
            } else {
                Text("Un asesor debe validar esta recomendación.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
