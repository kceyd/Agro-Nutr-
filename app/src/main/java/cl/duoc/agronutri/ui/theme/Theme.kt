package cl.duoc.agronutri.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = TealPrimario,
    onPrimary = Color.White,
    primaryContainer = MentaInsignia,
    onPrimaryContainer = TealOscuro,
    secondary = Naranja,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF6E3CF),
    onSecondaryContainer = Color(0xFF5A3410),
    tertiary = VerdeHoja,
    onTertiary = TealOscuro,
    tertiaryContainer = MentaClaro,
    onTertiaryContainer = TealOscuro,
    error = Rojo,
    onError = Color.White,
    errorContainer = RojoClaro,
    onErrorContainer = Rojo,
    background = FondoPagina,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = GrisAzulClaro,
    onSurfaceVariant = Color(0xFF52606A),
    outline = Color(0xFF9AA6AD),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1F5F3),
    surfaceContainer = Color(0xFFEBF0EE),
    surfaceContainerHigh = Color(0xFFE5EBE8),
    surfaceContainerHighest = Color(0xFFDFE6E3)
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// La web no tiene modo oscuro: se deja una versión oscura con los mismos tonos por si se activa.
private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FB3B4),
    onPrimary = TealBanner,
    primaryContainer = TealOscuro,
    onPrimaryContainer = MentaInsignia,
    secondary = Color(0xFFE5A464),
    tertiary = VerdeHoja,
    error = Color(0xFFFFB4AB)
)

/** Siempre claro por defecto, igual que la página web (no depende del modo del teléfono). */
@Composable
fun AgroNutriTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}
