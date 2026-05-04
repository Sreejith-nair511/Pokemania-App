package com.example.pokemania

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ResourceItem(
    val title        : String,
    val subtitle     : String,
    val url          : String,
    val category     : String,
    val categoryColor: Color
)

val resourceCategories = listOf(
    "EMULATORS & ROM TOOLS" to listOf(
        ResourceItem("Delta Emulator",              "Play GBA, DS and N64 Pokémon games on iPhone",
            "https://deltaemulator.com",                                                              "EMULATOR",  Color(0xFF6A1B9A)),
        ResourceItem("RetroArch",                   "Multi-system emulator for Android and PC",
            "https://www.retroarch.com",                                                              "EMULATOR",  Color(0xFF6A1B9A)),
        ResourceItem("mGBA",                        "Best Game Boy Advance emulator for PC and Android",
            "https://mgba.io",                                                                        "EMULATOR",  Color(0xFF6A1B9A)),
        ResourceItem("DeSmuME",                     "Nintendo DS emulator for PC",
            "https://desmume.org",                                                                    "EMULATOR",  Color(0xFF6A1B9A)),
        ResourceItem("Citra",                       "Nintendo 3DS emulator — X/Y, ORAS, Sun/Moon",
            "https://citra-emu.org",                                                                  "EMULATOR",  Color(0xFF6A1B9A)),
        ResourceItem("Yuzu / Ryujinx",              "Nintendo Switch emulator — Sword/Shield, Scarlet/Violet",
            "https://yuzu-emu.org",                                                                   "EMULATOR",  Color(0xFF6A1B9A)),
    ),
    "COMPETITIVE BATTLE TOOLS" to listOf(
        ResourceItem("Pokémon Showdown",            "Free online battle simulator — all generations",
            "https://play.pokemonshowdown.com",                                                       "BATTLE",    Color(0xFFE53935)),
        ResourceItem("Smogon Dex",                  "Tier lists, movesets and strategy guides",
            "https://www.smogon.com/dex",                                                             "STRATEGY",  Color(0xFF4A148C)),
        ResourceItem("Damage Calculator",           "Calculate exact damage for any matchup",
            "https://calc.pokemonshowdown.com",                                                       "CALC",      Color(0xFF1565C0)),
        ResourceItem("Pokémon VGC",                 "Official Video Game Championship information",
            "https://www.pokemon.com/us/play-pokemon/pokemon-events/pokemon-tournaments",             "VGC",       Color(0xFFE53935)),
    ),
    "DATABASES & WIKIS" to listOf(
        ResourceItem("Bulbapedia",                  "The most complete Pokémon encyclopedia",
            "https://bulbapedia.bulbagarden.net",                                                     "WIKI",      Color(0xFF2E7D32)),
        ResourceItem("PokémonDB",                   "Stats, moves and learnsets for all Pokémon",
            "https://pokemondb.net",                                                                  "DATABASE",  Color(0xFF37474F)),
        ResourceItem("Serebii.net",                 "Fastest news and event database",
            "https://www.serebii.net",                                                                "NEWS",      Color(0xFF2E7D32)),
        ResourceItem("PokéAPI",                     "Free REST API — powers this app's live data",
            "https://pokeapi.co",                                                                     "API",       Color(0xFF00695C)),
    ),
    "ROM HACKS & FAN GAMES" to listOf(
        ResourceItem("PokéCommunity",               "Largest Pokémon ROM hack community",
            "https://www.pokecommunity.com",                                                          "COMMUNITY", Color(0xFF0277BD)),
        ResourceItem("Pokémon Reborn",              "Fan-made game with all 18 gyms",
            "https://www.rebornevo.com",                                                              "FAN GAME",  Color(0xFF880E4F)),
        ResourceItem("Pokémon Insurgence",          "Fan game with mega evolutions and delta species",
            "https://pokemon-insurgence.com",                                                         "FAN GAME",  Color(0xFF880E4F)),
        ResourceItem("Universal Pokémon Randomizer","Randomize any Pokémon ROM",
            "https://github.com/Ajarmar/universal-pokemon-randomizer-zx",                             "TOOL",      Color(0xFF4E342E)),
    ),
    "TRADING & COMMUNITY" to listOf(
        ResourceItem("Pokémon HOME",                "Transfer and manage your collection",
            "https://home.pokemon.com",                                                               "APP",       Color(0xFF0277BD)),
        ResourceItem("r/pokemon",                   "Reddit's main Pokémon community",
            "https://www.reddit.com/r/pokemon",                                                       "REDDIT",    Color(0xFFFF6D00)),
        ResourceItem("r/pokemontrades",             "Trade Pokémon with other players",
            "https://www.reddit.com/r/pokemontrades",                                                 "REDDIT",    Color(0xFFFF6D00)),
        ResourceItem("TCGPlayer",                   "Buy and sell Pokémon trading cards",
            "https://www.tcgplayer.com/categories/trading-and-collectible-card-games/pokemon",        "TCG",       Color(0xFFE65100)),
    )
)

class ResourcesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = AppRed, background = AppDark,
                surface = AppCard, onBackground = AppWhite, onSurface = AppWhite
            )) {
                ResourcesScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resources", fontWeight = FontWeight.Bold, color = AppWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back", tint = AppWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppCard)
            )
        },
        containerColor = AppDark
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            resourceCategories.forEach { (category, items) ->
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        category,
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = AppRed,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(6.dp))
                }
                items(items) { res ->
                    ResourceCard(res) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(res.url)))
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceCard(res: ResourceItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppCard)
            .border(1.dp, AppGrey, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(res.categoryColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(res.title, color = AppWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(res.subtitle, color = AppWhite.copy(alpha = 0.5f), fontSize = 11.sp)
            Spacer(Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(res.categoryColor.copy(alpha = 0.15f))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(res.category, color = res.categoryColor,
                    fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(8.dp))
        Icon(
            Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "Open",
            tint     = AppWhite.copy(alpha = 0.25f),
            modifier = Modifier.size(16.dp)
        )
    }
}
