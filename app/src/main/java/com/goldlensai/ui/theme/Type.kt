package com.goldlensai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GoldLensDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD166),
    secondary = Color(0xFF9CC9FF),
    tertiary = Color(0xFF70E3C7),
    background = Color(0xFF050B13),
    surface = Color(0xFF0B1B2C),
    onPrimary = Color(0xFF0A1624),
    onSecondary = Color(0xFF08131E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun GoldLensTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GoldLensDarkColorScheme,
        typography = Typography,
        content = content
    )
}
