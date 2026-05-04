package com.example.pokemania

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ── Shared colour tokens (used across all activities) ─────────────────────────
val AppRed   = Color(0xFFE53935)
val AppDark  = Color(0xFF0D0D0D)
val AppCard  = Color(0xFF1A1A1A)
val AppGrey  = Color(0xFF2C2C2C)
val AppWhite = Color(0xFFF5F5F5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = getSharedPreferences("pokemania", Context.MODE_PRIVATE)
            var themeId by remember {
                mutableStateOf(
                    AppThemeId.valueOf(
                        prefs.getString(PREF_THEME, AppThemeId.CRIMSON.name) ?: AppThemeId.CRIMSON.name
                    )
                )
            }
            val theme = allThemes.first { it.id == themeId }

            CompositionLocalProvider(LocalAppTheme provides theme) {
                MaterialTheme(colorScheme = theme.scheme) {
                    HomeScreen(
                        currentThemeId = themeId,
                        onThemeChange  = { newId ->
                            themeId = newId
                            prefs.edit().putString(PREF_THEME, newId.name).apply()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentThemeId: AppThemeId,
    onThemeChange : (AppThemeId) -> Unit
) {
    val context = LocalContext.current
    val theme   = LocalAppTheme.current
    var showThemePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "PokeMania",
                        fontWeight = FontWeight.Black,
                        fontSize   = 20.sp,
                        color      = theme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { showThemePicker = true }) {
                        Icon(Icons.Default.Palette, contentDescription = "Change Theme",
                            tint = theme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = theme.surface)
            )
        },
        containerColor = theme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Hero banner ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(theme.primary.copy(alpha = 0.15f), theme.surface)
                        )
                    )
                    .border(1.dp, theme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "POKÉMANIA",
                        fontSize   = 32.sp,
                        fontWeight = FontWeight.Black,
                        color      = theme.primary,
                        letterSpacing = 4.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text      = "Complete Pokédex · Gen I – IX · 1025 Pokémon",
                        fontSize  = 12.sp,
                        color     = theme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Navigation buttons ───────────────────────────────────────────
            NavButton(label = "Pokédex", subtitle = "Browse all 1025 Pokémon",
                theme = theme) {
                context.startActivity(Intent(context, PokedexActivity::class.java))
            }
            NavButton(label = "Battle Simulator", subtitle = "Compare any two Pokémon",
                theme = theme, accent = theme.primary.copy(alpha = 0.7f)) {
                context.startActivity(Intent(context, BattleActivity::class.java))
            }
            NavButton(label = "Type Chart", subtitle = "Full 18-type effectiveness table",
                theme = theme, accent = Color(0xFF7B1FA2)) {
                context.startActivity(Intent(context, TypeChartActivity::class.java))
            }
            NavButton(label = "News & Anime", subtitle = "Official channels and latest updates",
                theme = theme, accent = Color(0xFFB71C1C)) {
                context.startActivity(Intent(context, NewsActivity::class.java))
            }
            NavButton(label = "Resources", subtitle = "Emulators, tools and community links",
                theme = theme, accent = Color(0xFF0D47A1)) {
                context.startActivity(Intent(context, ResourcesActivity::class.java))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color    = theme.divider
            )

            NavButton(
                label    = "Pokémon Showdown",
                subtitle = "Online competitive battle simulator",
                theme    = theme,
                accent   = Color(0xFF4A148C)
            ) {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://play.pokemonshowdown.com/"))
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text     = "Theme: ${currentThemeId.label}",
                fontSize = 11.sp,
                color    = theme.onBackground.copy(alpha = 0.3f)
            )
        }
    }

    // ── Theme picker dialog ──────────────────────────────────────────────────
    if (showThemePicker) {
        ThemePickerDialog(
            current  = currentThemeId,
            onSelect = { onThemeChange(it); showThemePicker = false },
            onDismiss = { showThemePicker = false }
        )
    }
}

@Composable
fun NavButton(
    label   : String,
    subtitle: String,
    theme   : AppThemeColors,
    accent  : Color = theme.primary,
    onClick : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(theme.surface)
            .border(1.dp, theme.divider, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = theme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = theme.onSurface.copy(alpha = 0.45f), fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(accent)
        )
    }
}

@Composable
fun ThemePickerDialog(
    current  : AppThemeId,
    onSelect : (AppThemeId) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AppCard,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Select Theme",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = AppWhite
                )
                Spacer(Modifier.height(16.dp))
                allThemes.forEach { t ->
                    val selected = t.id == current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selected) t.primary.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) t.primary else AppGrey,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelect(t.id) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            t.id.label,
                            color      = if (selected) t.primary else AppWhite,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize   = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(t.primary)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick  = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel", color = AppWhite.copy(alpha = 0.5f))
                }
            }
        }
    }
}

// ── AppButton kept for backward compat in other screens ───────────────────────
@Composable
fun AppButton(
    label         : String,
    containerColor: Color = AppRed,
    onClick       : () -> Unit
) {
    Button(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text       = label,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = AppWhite,
            textAlign  = TextAlign.Center
        )
    }
}
