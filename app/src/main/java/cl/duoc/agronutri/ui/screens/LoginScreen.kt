package cl.duoc.agronutri.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.ui.viewmodel.Rol

@Composable
fun LoginScreen(onEntrar: (Rol) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AgroNutri", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        Text(
            "Registro y estimación nutricional agrícola en terreno",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            "Prototipo con datos simulados. Elige tu perfil para entrar.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = { onEntrar(Rol.AGRICULTOR) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como ${Rol.AGRICULTOR.etiqueta}")
        }
        OutlinedButton(onClick = { onEntrar(Rol.ASESOR) }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar como ${Rol.ASESOR.etiqueta}")
        }
    }
}
