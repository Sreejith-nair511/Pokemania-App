package com.example.pokemania

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TypeChartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = AppRed, background = AppDark,
                surface = AppCard, onBackground = AppWhite, onSurface = AppWhite
            )) {
                TypeChartScreen(onBack = { finish() })
            }
        }
    }
}

val ALL_TYPES = listOf(
    "Normal","Fire","Water","Electric","Grass","Ice",
    "Fighting","Poison","Ground","Flying","Psychic","Bug",
    "Rock","Ghost","Dragon","Dark","Steel","Fairy"
)

// chart[attacker][defender] = multiplier (missing = 1x)
val typeChart: Map<String, Map<String, Float>> = mapOf(
    "Normal"   to mapOf("Rock" to 0.5f, "Ghost" to 0f, "Steel" to 0.5f),
    "Fire"     to mapOf("Fire" to 0.5f, "Water" to 0.5f, "Rock" to 0.5f, "Dragon" to 0.5f, "Grass" to 2f, "Ice" to 2f, "Bug" to 2f, "Steel" to 2f),
    "Water"    to mapOf("Water" to 0.5f, "Grass" to 0.5f, "Dragon" to 0.5f, "Fire" to 2f, "Ground" to 2f, "Rock" to 2f),
    "Electric" to mapOf("Electric" to 0.5f, "Grass" to 0.5f, "Dragon" to 0.5f, "Ground" to 0f, "Water" to 2f, "Flying" to 2f),
    "Grass"    to mapOf("Fire" to 0.5f, "Grass" to 0.5f, "Poison" to 0.5f, "Flying" to 0.5f, "Bug" to 0.5f, "Dragon" to 0.5f, "Steel" to 0.5f, "Water" to 2f, "Ground" to 2f, "Rock" to 2f),
    "Ice"      to mapOf("Water" to 0.5f, "Ice" to 0.5f, "Steel" to 0.5f, "Fire" to 0.5f, "Grass" to 2f, "Ground" to 2f, "Flying" to 2f, "Dragon" to 2f),
    "Fighting" to mapOf("Poison" to 0.5f, "Flying" to 0.5f, "Psychic" to 0.5f, "Bug" to 0.5f, "Ghost" to 0f, "Fairy" to 0.5f, "Normal" to 2f, "Ice" to 2f, "Rock" to 2f, "Dark" to 2f, "Steel" to 2f),
    "Poison"   to mapOf("Poison" to 0.5f, "Ground" to 0.5f, "Rock" to 0.5f, "Ghost" to 0.5f, "Steel" to 0f, "Grass" to 2f, "Fairy" to 2f),
    "Ground"   to mapOf("Grass" to 0.5f, "Bug" to 0.5f, "Flying" to 0f, "Fire" to 2f, "Electric" to 2f, "Poison" to 2f, "Rock" to 2f, "Steel" to 2f),
    "Flying"   to mapOf("Electric" to 0.5f, "Rock" to 0.5f, "Steel" to 0.5f, "Grass" to 2f, "Fighting" to 2f, "Bug" to 2f),
    "Psychic"  to mapOf("Psychic" to 0.5f, "Steel" to 0.5f, "Dark" to 0f, "Fighting" to 2f, "Poison" to 2f),
    "Bug"      to mapOf("Fire" to 0.5f, "Fighting" to 0.5f, "Flying" to 0.5f, "Ghost" to 0.5f, "Steel" to 0.5f, "Fairy" to 0.5f, "Grass" to 2f, "Psychic" to 2f, "Dark" to 2f),
    "Rock"     to mapOf("Fighting" to 0.5f, "Ground" to 0.5f, "Steel" to 0.5f, "Fire" to 2f, "Ice" to 2f, "Flying" to 2f, "Bug" to 2f),
    "Ghost"    to mapOf("Normal" to 0f, "Dark" to 0.5f, "Ghost" to 2f, "Psychic" to 2f),
    "Dragon"   to mapOf("Steel" to 0.5f, "Fairy" to 0f, "Dragon" to 2f),
    "Dark"     to mapOf("Fighting" to 0.5f, "Dark" to 0.5f, "Fairy" to 0.5f, "Ghost" to 2f, "Psychic" to 2f),
    "Steel"    to mapOf("Fire" to 0.5f, "Water" to 0.5f, "Electric" to 0.5f, "Steel" to 0.5f, "Ice" to 2f, "Rock" to 2f, "Fairy" to 2f),
    "Fairy"    to mapOf("Fire" to 0.5f, "Poison" to 0.5f, "Steel" to 0.5f, "Fighting" to 2f, "Dragon" to 2f, "Dark" to 2f)
)

fun getEffectiveness(attacker: String, defender: String): Float =
    typeChart[attacker]?.get(defender) ?: 1f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeChartScreen(onBack: () -> Unit) {
    var selectedAttacker by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Type Chart", fontWeight = FontWeight.Bold, color = AppWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppCard)
            )
        },
        containerColor = AppDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("TAP AN ATTACKING TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = AppRed, letterSpacing = 1.5.sp)

            // Type picker grid
            ALL_TYPES.chunked(6).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    row.forEach { type ->
                        val selected = selectedAttacker == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected) typeColor(type)
                                    else typeColor(type).copy(alpha = 0.22f)
                                )
                                .clickable { selectedAttacker = if (selected) null else type },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                color = if (selected) Color.White else typeColor(type),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                    repeat(6 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }

            HorizontalDivider(color = AppGrey)

            if (selectedAttacker != null) {
                val atk = selectedAttacker!!
                val groups = ALL_TYPES.groupBy { getEffectiveness(atk, it) }

                Text("${atk.uppercase()} ATTACKING →",
                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = typeColor(atk), letterSpacing = 1.sp)

                listOf(
                    2f   to ("Super Effective (2x)"    to Color(0xFF4CAF50)),
                    0.5f to ("Not Very Effective (0.5x)" to Color(0xFFEF5350)),
                    0f   to ("No Effect (0x)"           to AppGrey),
                    1f   to ("Normal Damage (1x)"       to AppWhite.copy(alpha = 0.5f))
                ).forEach { (mult, pair) ->
                    val (label, labelColor) = pair
                    val types = groups[mult]
                    if (!types.isNullOrEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = labelColor)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            types.forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(typeColor(type).copy(alpha = 0.25f))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(type, color = typeColor(type),
                                        fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                // Full scrollable grid
                Text("FULL TABLE (ATK → DEF)", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = AppRed, letterSpacing = 1.5.sp)
                Text("Tap a type above for quick view, or scroll the full grid:",
                    fontSize = 12.sp, color = AppWhite.copy(alpha = 0.45f))

                Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    Column {
                        // Header
                        Row {
                            Spacer(Modifier.width(52.dp))
                            ALL_TYPES.forEach { def ->
                                Box(Modifier.width(30.dp).height(22.dp), contentAlignment = Alignment.Center) {
                                    Text(def.take(3), color = typeColor(def),
                                        fontSize = 7.sp, fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center)
                                }
                            }
                        }
                        ALL_TYPES.forEach { atk ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.width(52.dp).height(26.dp).padding(end = 4.dp),
                                    contentAlignment = Alignment.CenterEnd) {
                                    Text(atk.take(7), color = typeColor(atk),
                                        fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                }
                                ALL_TYPES.forEach { def ->
                                    val eff = getEffectiveness(atk, def)
                                    val bg = when (eff) {
                                        2f   -> Color(0xFF1B4A1B)
                                        0.5f -> Color(0xFF4A1B1B)
                                        0f   -> Color(0xFF111111)
                                        else -> Color(0xFF1A1A1A)
                                    }
                                    val fg = when (eff) {
                                        2f   -> Color(0xFF4CAF50)
                                        0.5f -> Color(0xFFEF5350)
                                        0f   -> Color(0xFF444444)
                                        else -> AppWhite.copy(alpha = 0.25f)
                                    }
                                    val lbl = when (eff) {
                                        2f -> "2"; 0.5f -> "½"; 0f -> "0"; else -> "·"
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(width = 30.dp, height = 26.dp)
                                            .padding(1.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(bg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(lbl, color = fg, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
