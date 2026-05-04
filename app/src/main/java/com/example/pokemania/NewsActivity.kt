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

data class NewsLink(
    val title   : String,
    val subtitle: String,
    val url     : String,
    val tag     : String,
    val tagColor: Color
)

val newsList = listOf(
    NewsLink("Pokémon Official Website",    "Latest news, games and announcements",
        "https://www.pokemon.com/us/pokemon-news",                    "OFFICIAL",    Color(0xFFE53935)),
    NewsLink("Pokémon Asia Hindi Official", "Hindi dubbed episodes on YouTube",
        "https://www.youtube.com/@PokemonAsiaHindiOfficial",          "YOUTUBE",     Color(0xFFCC0000)),
    NewsLink("Pokémon Indigo League",       "Classic Gen I anime playlist",
        "https://www.youtube.com/watch?v=qvMitRMiczs",                "YOUTUBE",     Color(0xFFCC0000)),
    NewsLink("Pokémon Diamond and Pearl",   "Gen IV anime series playlist",
        "https://www.youtube.com/watch?v=v5zPMrwrFjk",               "YOUTUBE",     Color(0xFFCC0000)),
    NewsLink("Official Pokémon TV Channel", "Full episodes, movies and clips",
        "https://www.youtube.com/@OfficialPokemonTV",                 "YOUTUBE",     Color(0xFFCC0000)),
    NewsLink("Pokémon on Hotstar India",    "Stream Pokémon series in India",
        "https://www.hotstar.com/in/shows/pokemon/1971003167",        "HOTSTAR",     Color(0xFF1565C0)),
    NewsLink("Pokémon Showdown",            "Online competitive battle simulator",
        "https://play.pokemonshowdown.com/",                          "BATTLE",      Color(0xFF6A1B9A)),
    NewsLink("Serebii.net",                 "Fastest Pokémon news and database updates",
        "https://www.serebii.net",                                    "NEWS",        Color(0xFF2E7D32)),
    NewsLink("Bulbapedia",                  "Community-driven Pokémon encyclopedia",
        "https://bulbapedia.bulbagarden.net",                         "WIKI",        Color(0xFF1B5E20)),
    NewsLink("Smogon University",           "Competitive Pokémon strategy and tier lists",
        "https://www.smogon.com",                                     "COMPETITIVE", Color(0xFF4A148C)),
    NewsLink("Pokémon HOME",                "Manage and transfer your Pokémon collection",
        "https://home.pokemon.com",                                   "APP",         Color(0xFF0277BD)),
    NewsLink("Pokémon GO Live",             "Latest Pokémon GO events and updates",
        "https://pokemongolive.com",                                  "GO",          Color(0xFF00695C)),
    NewsLink("Pokémon TCG Online",          "Play the Trading Card Game online",
        "https://www.pokemon.com/us/pokemon-tcg/play-online",         "TCG",         Color(0xFFE65100)),
    NewsLink("PokémonDB",                   "Complete stats, moves and learnsets",
        "https://pokemondb.net",                                      "DATABASE",    Color(0xFF37474F))
)

class NewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = AppRed, background = AppDark,
                surface = AppCard, onBackground = AppWhite, onSurface = AppWhite
            )) {
                NewsScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("News & Links", fontWeight = FontWeight.Bold, color = AppWhite)
                },
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                Text(
                    "OFFICIAL CHANNELS & LATEST NEWS",
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = AppRed,
                    letterSpacing = 1.5.sp,
                    modifier      = Modifier.padding(bottom = 4.dp)
                )
            }
            items(newsList) { link ->
                NewsCard(link = link) {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link.url)))
                }
            }
        }
    }
}

@Composable
fun NewsCard(link: NewsLink, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppCard)
            .border(1.dp, AppGrey, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colour accent bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(link.tagColor)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = link.title,
                color      = AppWhite,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(link.subtitle, color = AppWhite.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(link.tagColor.copy(alpha = 0.15f))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(link.tag, color = link.tagColor,
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
