package cl.duoc.agronutri.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.agronutri.data.local.Predio
import cl.duoc.agronutri.domain.Clima
import cl.duoc.agronutri.domain.ClimaPredio
import cl.duoc.agronutri.domain.Ubicacion
import cl.duoc.agronutri.ui.viewmodel.AppViewModelProvider
import cl.duoc.agronutri.ui.viewmodel.ClimaViewModel
import cl.duoc.agronutri.ui.viewmodel.EstadoClima

/**
 * Tarjeta con el clima actual y el pronóstico de 3 días del predio, obtenidos de la
 * API externa Open-Meteo (data/remote/ClimaApi.kt). Se usa en Registro y Recomendación.
 */
@Composable
fun TarjetaClima(
    predio: Predio,
    modifier: Modifier = Modifier,
    vm: ClimaViewModel = viewModel(key = "clima-${predio.id}", factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(predio.latitud, predio.longitud) { vm.cargar(predio.latitud, predio.longitud) }
    val estado by vm.estado.collectAsStateWithLifecycle()

    TarjetaApp(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Clima del predio", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = vm::recargar, enabled = estado !is EstadoClima.Cargando) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar clima")
                }
            }

            Crossfade(targetState = estado, label = "estadoClima") { actual ->
                when (actual) {
                    EstadoClima.Cargando -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text("Consultando el clima...", style = MaterialTheme.typography.bodyMedium)
                    }

                    EstadoClima.SinRed -> Text(
                        "Sin conexión. El clima se cargará solo cuando vuelva la red; la medición se puede registrar igual.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    is EstadoClima.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            actual.mensaje,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        OutlinedButton(onClick = vm::recargar) { Text("Reintentar") }
                    }

                    is EstadoClima.Listo -> ContenidoClima(actual.clima, actual.ubicacion)
                }
            }

            Text(
                "Datos meteorológicos: Open-Meteo.com",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContenidoClima(clima: ClimaPredio, ubicacion: Ubicacion) {
    val a = clima.actual
    val cl = Clima.LOCALE_CL

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Condición actual
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.Bottom) {
                ContadorAnimado(
                    valor = a.temperaturaC.toFloat(),
                    decimales = 1,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(" °C", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(a.descripcion, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Actualizado a las ${a.hora} (hora de Chile)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            DatoClima("Humedad", "${a.humedadPct} %", Modifier.weight(1f))
            DatoClima("Viento", "%.0f km/h".format(cl, a.vientoKmH), Modifier.weight(1f))
            DatoClima("Lluvia", "%.1f mm".format(cl, a.precipitacionMm), Modifier.weight(1f))
        }

        // Pronóstico: máx/mín, lluvia y ET0 (agua que pierde el cultivo) por día
        if (clima.pronostico.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Próximos días", style = MaterialTheme.typography.labelLarge)
                clima.pronostico.forEach { d ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(Clima.formatoDia(d.fecha), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text("%.0f° / %.0f°".format(cl, d.maxC, d.minC), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text("%.1f mm".format(cl, d.lluviaMm), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text("ET0 %.1f".format(cl, d.et0Mm), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Clima.avisoRiego(clima.pronostico)?.let { aviso ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(10.dp)
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    aviso,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Text(
            if (ubicacion.esReferencia) {
                "Ubicación de referencia (${Clima.NOMBRE_REFERENCIA}): el predio no tiene coordenadas."
            } else {
                "Coordenadas del predio: lat %.4f · lon %.4f".format(cl, ubicacion.latitud, ubicacion.longitud)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DatoClima(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.titleSmall)
    }
}
