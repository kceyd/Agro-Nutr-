package cl.duoc.agronutri.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.domain.Fertirriego
import cl.duoc.agronutri.domain.formatoFecha
import cl.duoc.agronutri.ui.components.BannerEncabezado
import cl.duoc.agronutri.ui.components.BarraNutriente
import cl.duoc.agronutri.ui.components.ContadorAnimado
import cl.duoc.agronutri.ui.components.ContenidoAdaptable
import cl.duoc.agronutri.ui.components.EstadisticaBanner
import cl.duoc.agronutri.ui.components.EtiquetaEstado
import cl.duoc.agronutri.ui.components.TarjetaApp
import cl.duoc.agronutri.ui.components.TarjetaClima
import cl.duoc.agronutri.ui.components.TipoEstado
import cl.duoc.agronutri.ui.components.colorDeficit
import cl.duoc.agronutri.ui.components.entrada
import cl.duoc.agronutri.ui.theme.MentaInsignia
import cl.duoc.agronutri.ui.theme.TealOscuro
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
    val objetivo = remember(m) { Fertirriego.requerimiento(m.cultivo, m.etapa) }
    val totalKg = dosis.sumOf { it.kgPorHa }.toFloat()
    val contexto = LocalContext.current

    ContenidoAdaptable(anchoMaximo = 760.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BannerEncabezado(
                titulo = "${m.cultivo} · ${m.etapa}",
                subtitulo = "${item.predio.nombre} · ${m.fechaMillis.formatoFecha()}"
            ) {
                EstadisticaBanner(totalKg, "kg/ha en total", Modifier.weight(1f), decimales = 1)
                EstadisticaBanner(m.ph.toFloat(), "pH", Modifier.weight(1f), decimales = 1)
                EstadisticaBanner(m.ce.toFloat(), "CE dS/m", Modifier.weight(1f), decimales = 1)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EtiquetaEstado(
                    if (m.pendienteSync) "Pendiente de sincronizar" else "Sincronizado",
                    if (m.pendienteSync) TipoEstado.AVISO else TipoEstado.OK
                )
                EtiquetaEstado(
                    if (m.validada) "Validada" else "Sin validar",
                    if (m.validada) TipoEstado.OK else TipoEstado.NEUTRO
                )
            }

            TarjetaApp(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Dosis por nutriente", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "La barra muestra qué parte del requerimiento falta cubrir.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    dosis.forEachIndexed { indice, d ->
                        val meta = objetivo[d.nutriente] ?: 0.0
                        val faltante = if (meta > 0.0) (d.deficitPpm / meta).toFloat() else 0f

                        Column(
                            modifier = Modifier.entrada(indice),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(MentaInsignia),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        d.nutriente.simbolo,
                                        color = TealOscuro,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(d.nutriente.fertilizante, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        if (d.deficitPpm <= 0.0) "Cubierto"
                                        else "Déficit %.0f ppm".format(d.deficitPpm),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    ContadorAnimado(
                                        valor = d.kgPorHa.toFloat(),
                                        decimales = 1,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        "kg/ha",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            BarraNutriente(progreso = faltante, color = colorDeficit(faltante))
                        }
                    }
                }
            }

            // Clima actual y pronóstico del predio (API externa Open-Meteo), con aviso si conviene esperar.
            TarjetaClima(predio = item.predio, modifier = Modifier.fillMaxWidth())

            m.fotoPath?.let { ruta ->
                val imagen = remember(ruta) { BitmapFactory.decodeFile(ruta)?.asImageBitmap() }
                if (imagen != null) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 4 }
                    ) {
                        Image(
                            bitmap = imagen,
                            contentDescription = "Foto foliar",
                            modifier = Modifier
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }

            Text(
                "Estimación básica con datos ficticios. Debe ser validada por un asesor agrónomo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Nuevo: compartir la recomendación como texto (WhatsApp, correo, etc.)
            OutlinedButton(
                onClick = {
                    val texto = buildString {
                        appendLine("NutriExpert · Recomendación de fertirriego")
                        appendLine("${m.cultivo} · ${m.etapa}")
                        appendLine("${item.predio.nombre} · ${m.fechaMillis.formatoFecha()}")
                        appendLine("pH ${m.ph} · CE ${m.ce} dS/m")
                        appendLine()
                        dosis.forEach {
                            appendLine("${it.nutriente.simbolo}: ${"%.1f".format(it.kgPorHa)} kg/ha de ${it.nutriente.fertilizante}")
                        }
                        appendLine()
                        append(if (m.validada) "Validada por asesor." else "Pendiente de validación por un asesor.")
                    }
                    val envio = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Recomendación NutriExpert")
                        putExtra(Intent.EXTRA_TEXT, texto)
                    }
                    contexto.startActivity(Intent.createChooser(envio, "Compartir recomendación"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Compartir recomendación")
            }

            if (!m.validada) {
                if (rol == Rol.ASESOR) {
                    Button(onClick = vm::validar, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Validar recomendación")
                    }
                } else {
                    Text(
                        "Un asesor debe validar esta recomendación.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
