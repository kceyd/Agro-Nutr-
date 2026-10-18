package cl.duoc.agronutri.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.R
import cl.duoc.agronutri.ui.viewmodel.Rol
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onEntrar: (Rol) -> Unit) {

    val zoomFondo = remember { Animatable(1.10f) }
    val titulo = remember { Animatable(0f) }
    val descripcion = remember { Animatable(0f) }
    val botones = remember { Animatable(0f) }

    var animacionTerminada by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        launch {
            zoomFondo.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 2500,
                    easing = EaseOutCubic
                )
            )
        }

        delay(250)

        launch {
            titulo.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 850,
                    easing = EaseOutCubic
                )
            )
        }

        delay(400)

        launch {
            descripcion.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 850,
                    easing = EaseOutCubic
                )
            )
        }

        delay(450)

        botones.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 900,
                easing = EaseOutCubic
            )
        )

        animacionTerminada = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        Image(
            painter = painterResource(
                id = R.drawable.fondo_agronutri
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomFondo.value
                    scaleY = zoomFondo.value
                }
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.35f)
                )
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(
                16.dp,
                Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "AgroNutri",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.graphicsLayer {
                    alpha = titulo.value
                    translationY = (1f - titulo.value) * 35f
                }
            )


            Text(
                text = "Registro y estimación nutricional agrícola en terreno",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = descripcion.value
                    translationY = (1f - descripcion.value) * 25f
                }
            )


            Text(
                text = "Prototipo con datos simulados. Elige tu perfil para entrar.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = descripcion.value
                    translationY = (1f - descripcion.value) * 25f
                }
            )


            Button(
                onClick = {
                    onEntrar(Rol.AGRICULTOR)
                },
                enabled = animacionTerminada,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = botones.value
                        translationY = (1f - botones.value) * 45f
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF2E7D32),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    "Entrar como ${Rol.AGRICULTOR.etiqueta}"
                )
            }


            OutlinedButton(
                onClick = {
                    onEntrar(Rol.ASESOR)
                },
                enabled = animacionTerminada,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = botones.value
                        translationY = (1f - botones.value) * 45f
                    },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White.copy(
                        alpha = 0.95f
                    ),
                    contentColor = Color(0xFF1B5E20),
                    disabledContainerColor = Color.White.copy(
                        alpha = 0.95f
                    ),
                    disabledContentColor = Color(0xFF1B5E20)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF2E7D32)
                )
            ) {
                Text(
                    "Entrar como ${Rol.ASESOR.etiqueta}"
                )
            }
        }
    }
}
