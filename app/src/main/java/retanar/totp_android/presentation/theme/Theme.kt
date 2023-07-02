package retanar.totp_android.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorPalette = darkColors(
    primary = GrayGreen600,
    primaryVariant = GrayGreen700,
    secondary = GrayYellow600,
    secondaryVariant = GrayYellow700,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = GrayGreen700,
    primaryVariant = GrayGreen800,
    secondary = GrayYellow600,
    secondaryVariant = GrayYellow700,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

// TODO: add user setting for light/dark theme
@Composable
fun TOTPTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    // change status bar color with this simple trick
    val view = LocalView.current
    if ((!view.isInEditMode)) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) {
                colors.surface
            } else {
                colors.primaryVariant
            }.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}