package com.example.pokemania

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Theme definitions ─────────────────────────────────────────────────────────

enum class AppThemeId(val label: String) {
    CRIMSON("Crimson Dark"),
    MIDNIGHT("Midnight Blue"),
    FOREST("Forest Green"),
    SLATE("Slate Light"),
    AMOLED("Pure AMOLED")
}

data class AppThemeColors(
    val id          : AppThemeId,
    val background  : Color,
    val surface     : Color,
    val surfaceAlt  : Color,   // slightly lighter card
    val primary     : Color,   // accent / brand colour
    val onPrimary   : Color,
    val onBackground: Color,
    val onSurface   : Color,
    val divider     : Color,
    val scheme      : ColorScheme
)

val CrimsonTheme = AppThemeColors(
    id           = AppThemeId.CRIMSON,
    background   = Color(0xFF0D0D0D),
    surface      = Color(0xFF1A1A1A),
    surfaceAlt   = Color(0xFF242424),
    primary      = Color(0xFFE53935),
    onPrimary    = Color(0xFFF5F5F5),
    onBackground = Color(0xFFF5F5F5),
    onSurface    = Color(0xFFF5F5F5),
    divider      = Color(0xFF2C2C2C),
    scheme       = darkColorScheme(
        primary      = Color(0xFFE53935),
        background   = Color(0xFF0D0D0D),
        surface      = Color(0xFF1A1A1A),
        onPrimary    = Color(0xFFF5F5F5),
        onBackground = Color(0xFFF5F5F5),
        onSurface    = Color(0xFFF5F5F5)
    )
)

val MidnightTheme = AppThemeColors(
    id           = AppThemeId.MIDNIGHT,
    background   = Color(0xFF0A0E1A),
    surface      = Color(0xFF141C2E),
    surfaceAlt   = Color(0xFF1E2840),
    primary      = Color(0xFF4A90D9),
    onPrimary    = Color(0xFFF0F4FF),
    onBackground = Color(0xFFD8E0F0),
    onSurface    = Color(0xFFD8E0F0),
    divider      = Color(0xFF1E2840),
    scheme       = darkColorScheme(
        primary      = Color(0xFF4A90D9),
        background   = Color(0xFF0A0E1A),
        surface      = Color(0xFF141C2E),
        onPrimary    = Color(0xFFF0F4FF),
        onBackground = Color(0xFFD8E0F0),
        onSurface    = Color(0xFFD8E0F0)
    )
)

val ForestTheme = AppThemeColors(
    id           = AppThemeId.FOREST,
    background   = Color(0xFF0A1A0E),
    surface      = Color(0xFF122018),
    surfaceAlt   = Color(0xFF1A2E20),
    primary      = Color(0xFF4CAF50),
    onPrimary    = Color(0xFFF0FFF2),
    onBackground = Color(0xFFCCE8D0),
    onSurface    = Color(0xFFCCE8D0),
    divider      = Color(0xFF1A2E20),
    scheme       = darkColorScheme(
        primary      = Color(0xFF4CAF50),
        background   = Color(0xFF0A1A0E),
        surface      = Color(0xFF122018),
        onPrimary    = Color(0xFFF0FFF2),
        onBackground = Color(0xFFCCE8D0),
        onSurface    = Color(0xFFCCE8D0)
    )
)

val SlateTheme = AppThemeColors(
    id           = AppThemeId.SLATE,
    background   = Color(0xFFF4F6F8),
    surface      = Color(0xFFFFFFFF),
    surfaceAlt   = Color(0xFFECEFF1),
    primary      = Color(0xFF1565C0),
    onPrimary    = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1A2E),
    onSurface    = Color(0xFF1A1A2E),
    divider      = Color(0xFFCFD8DC),
    scheme       = lightColorScheme(
        primary      = Color(0xFF1565C0),
        background   = Color(0xFFF4F6F8),
        surface      = Color(0xFFFFFFFF),
        onPrimary    = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1A1A2E),
        onSurface    = Color(0xFF1A1A2E)
    )
)

val AmoledTheme = AppThemeColors(
    id           = AppThemeId.AMOLED,
    background   = Color(0xFF000000),
    surface      = Color(0xFF0D0D0D),
    surfaceAlt   = Color(0xFF141414),
    primary      = Color(0xFFBB86FC),
    onPrimary    = Color(0xFF000000),
    onBackground = Color(0xFFE0E0E0),
    onSurface    = Color(0xFFE0E0E0),
    divider      = Color(0xFF1A1A1A),
    scheme       = darkColorScheme(
        primary      = Color(0xFFBB86FC),
        background   = Color(0xFF000000),
        surface      = Color(0xFF0D0D0D),
        onPrimary    = Color(0xFF000000),
        onBackground = Color(0xFFE0E0E0),
        onSurface    = Color(0xFFE0E0E0)
    )
)

val allThemes = listOf(CrimsonTheme, MidnightTheme, ForestTheme, SlateTheme, AmoledTheme)

// ── CompositionLocal so any composable can read the current theme ─────────────
val LocalAppTheme = compositionLocalOf<AppThemeColors> { CrimsonTheme }

// ── Shared preference key ─────────────────────────────────────────────────────
const val PREF_THEME = "selected_theme"
