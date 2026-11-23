package cl.duoc.agronutri.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.ui.theme.AzulLogo
import cl.duoc.agronutri.ui.theme.MentaInsignia
import cl.duoc.agronutri.ui.theme.Naranja
import cl.duoc.agronutri.ui.theme.Rojo
import cl.duoc.agronutri.ui.theme.TealBanner
import cl.duoc.agronutri.ui.theme.TealBannerFin
import cl.duoc.agronutri.ui.theme.TealOscuro
import cl.duoc.agronutri.ui.theme.VerdeHoja
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

// ------------------------------------------------------------------------------------------
// ADAPTABLE: la app decide su distribución según el ancho disponible (teléfono, tablet, giro)
// ------------------------------------------------------------------------------------------

/** true en tablets o teléfonos girados (ancho >= 600 dp). */
@Composable
fun esPantallaAncha(): Boolean = LocalConfiguration.current.screenWidthDp >= 600

/**
 * Centra el contenido y limita su ancho: en un teléfono ocupa todo, en una tablet
 * los formularios no se estiran de borde a borde.
 */
@Composable
fun ContenidoAdaptable(
    modifier: Modifier = Modifier,
    anchoMaximo: Dp = 720.dp,
    contenido: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.widthIn(max = anchoMaximo).fillMaxSize(), content = contenido)
    }
}

// ------------------------------------------------------------------------------------------
// ANIMACIONES reutilizables
// ------------------------------------------------------------------------------------------

/** Efecto "botón que se hunde" al tocar: la tarjeta se encoge un poco y rebota al soltar. */
fun Modifier.presionable(onClick: () -> Unit): Modifier = composed {
    val fuente = remember { MutableInteractionSource() }
    val presionado by fuente.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "presion"
    )
    this
        .graphicsLayer {
            scaleX = escala
            scaleY = escala
        }
        .clickable(interactionSource = fuente, indication = LocalIndication.current, onClick = onClick)
}

/** Entrada escalonada: cada elemento aparece subiendo y con fade, uno tras otro. */
fun Modifier.entrada(indice: Int): Modifier = composed {
    val progreso = remember { Animatable(if (indice >= 8) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (progreso.value < 1f) {
            delay(indice * 70L)
            progreso.animateTo(1f, tween(450, easing = EaseOutCubic))
        }
    }
    this.graphicsLayer {
        alpha = progreso.value
        translationY = (1f - progreso.value) * 48f
    }
}

/** Número que "cuenta" desde 0 hasta su valor. */
@Composable
fun ContadorAnimado(
    valor: Float,
    modifier: Modifier = Modifier,
    decimales: Int = 0,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified
) {
    var objetivo by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(valor) { objetivo = valor }
    val animado by animateFloatAsState(objetivo, tween(1000, easing = FastOutSlowInEasing), label = "contador")
    Text(
        text = if (decimales == 0) animado.roundToInt().toString() else "%.${decimales}f".format(animado),
        style = style,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

/** Barra de progreso que se llena con animación al aparecer. */
@Composable
fun BarraNutriente(progreso: Float, color: Color, modifier: Modifier = Modifier) {
    var objetivo by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progreso) { objetivo = progreso.coerceIn(0f, 1f) }
    val animado by animateFloatAsState(objetivo, tween(900, easing = EaseOutCubic), label = "barra")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animado)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(color)
        )
    }
}

// ------------------------------------------------------------------------------------------
// COMPONENTES visuales
// ------------------------------------------------------------------------------------------

/** Tarjeta estándar de la app: esquinas grandes, fondo blanco y sombra suave. */
@Composable
fun TarjetaApp(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        content = contenido
    )
}

/** Encabezado con el degradado verde de la web (TealBanner) y un círculo que "respira". */
@Composable
fun BannerEncabezado(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    contenido: @Composable RowScope.() -> Unit = {}
) {
    val respiracion = rememberInfiniteTransition(label = "respiracion")
    val escala by respiracion.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "escalaCirculo"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(TealBanner, TealBannerFin)))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 36.dp, y = (-36).dp)
                .size(150.dp)
                .graphicsLayer {
                    scaleX = escala
                    scaleY = escala
                }
                .clip(CircleShape)
                .background(VerdeHoja.copy(alpha = 0.20f))
        )
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MentaInsignia.copy(alpha = 0.9f))
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), content = contenido)
        }
    }
}

/** Cifra grande + etiqueta, para usar dentro de [BannerEncabezado]. */
@Composable
fun EstadisticaBanner(
    valor: Float,
    etiqueta: String,
    modifier: Modifier = Modifier,
    decimales: Int = 0
) {
    Column(modifier = modifier) {
        ContadorAnimado(
            valor = valor,
            decimales = decimales,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MentaInsignia.copy(alpha = 0.85f))
    }
}

/** Pantalla vacía amistosa, con el ícono flotando suavemente. */
@Composable
fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier
) {
    val flotar = rememberInfiniteTransition(label = "flotar")
    val dy by flotar.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flotarDy"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { translationY = dy * density }
                .size(88.dp)
                .clip(CircleShape)
                .background(MentaInsignia),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = TealOscuro, modifier = Modifier.size(42.dp))
        }
        Text(titulo, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Indicador de conexión: punto que late cuando hay red, con el texto cambiando en fundido. */
@Composable
fun IndicadorRed(enLinea: Boolean, pendientes: Int, modifier: Modifier = Modifier) {
    val color by animateColorAsState(if (enLinea) VerdeHoja else Naranja, tween(400), label = "colorRed")
    val transicion = rememberInfiniteTransition(label = "pulso")
    val pulso by transicion.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulsoAlpha"
    )
    val texto = (if (enLinea) "En línea" else "Sin red") +
        (if (pendientes > 0) " · $pendientes pend." else "")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .graphicsLayer { alpha = if (enLinea) pulso else 1f }
                .clip(CircleShape)
                .background(color)
        )
        Crossfade(targetState = texto, label = "estadoRed") {
            Text(it, color = Color.White, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

enum class TipoEstado { OK, AVISO, NEUTRO }

/** Etiqueta de estado con color según el tipo (verde = listo, naranja = pendiente). */
@Composable
fun EtiquetaEstado(texto: String, tipo: TipoEstado = TipoEstado.NEUTRO) {
    val (fondo, contenido) = when (tipo) {
        TipoEstado.OK -> MentaInsignia to TealOscuro
        TipoEstado.AVISO -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        TipoEstado.NEUTRO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    val fondoAnimado by animateColorAsState(fondo, tween(300), label = "fondoEtiqueta")
    Row(
        modifier = Modifier
            .animateContentSize()
            .clip(RoundedCornerShape(50))
            .background(fondoAnimado)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(contenido))
        Text(texto, style = MaterialTheme.typography.labelMedium, color = contenido)
    }
}

/** Color de una barra de déficit según qué parte del requerimiento falta cubrir. */
fun colorDeficit(fraccionFaltante: Float): Color = when {
    fraccionFaltante <= 0f -> VerdeHoja
    fraccionFaltante < 0.4f -> AzulLogo
    fraccionFaltante < 0.7f -> Naranja
    else -> Rojo
}

// ------------------------------------------------------------------------------------------
// FORMULARIOS (sin cambios de comportamiento)
// ------------------------------------------------------------------------------------------

/** Lista desplegable de solo lectura (cultivo, etapa fenológica). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Selector(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

/**
 * Campo de texto estándar de la app: mismo aspecto de error en todos los formularios
 * (borde rojo, ícono de advertencia y mensaje debajo). No valida nada por sí mismo:
 * recibe el [error] que calculó [cl.duoc.agronutri.domain.Validacion].
 */
@Composable
fun CampoValidado(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    iconoInicial: ImageVector? = null,
    ayuda: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    val textoApoyo = error ?: ayuda

    val inicial: (@Composable () -> Unit)? =
        if (iconoInicial != null) ({ Icon(iconoInicial, contentDescription = null) }) else null

    val iconoFinal: (@Composable () -> Unit)? = when {
        trailingIcon != null -> trailingIcon
        error != null -> ({
            Icon(
                Icons.Filled.Warning,
                contentDescription = "Campo con error",
                tint = MaterialTheme.colorScheme.error
            )
        })
        else -> null
    }

    val apoyo: (@Composable () -> Unit)? =
        if (textoApoyo != null) ({ Text(textoApoyo) }) else null

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        leadingIcon = inicial,
        trailingIcon = iconoFinal,
        supportingText = apoyo,
        isError = error != null,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        modifier = modifier.animateContentSize()
    )
}
