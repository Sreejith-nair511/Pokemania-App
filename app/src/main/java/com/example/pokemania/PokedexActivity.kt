package com.example.pokemania

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

class PokedexActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = AppRed, background = AppDark,
                surface = AppCard, onBackground = AppWhite, onSurface = AppWhite
            )) {
                PokedexScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    onBack: () -> Unit,
    vm: PokedexViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedGen by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val allPokemon = when (val s = state) {
        is PokedexState.Success -> s.list
        else -> emptyList()
    }

    val filtered = remember(allPokemon, selectedGen, searchQuery) {
        allPokemon
            .filter { if (selectedGen == 0) true else it.generation == selectedGen }
            .filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val count = if (allPokemon.isEmpty()) "" else " (${allPokemon.size})"
                    Text("Pokédex$count", fontWeight = FontWeight.Bold, color = AppWhite)
                },
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
        ) {
            // ── Search bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppCard)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null,
                    tint = AppWhite.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    cursorBrush = SolidColor(AppRed),
                    textStyle = TextStyle(color = AppWhite, fontSize = 14.sp),
                    decorationBox = { inner ->
                        if (searchQuery.isEmpty())
                            Text("Search all 1025 Pokémon…", color = AppWhite.copy(alpha = 0.3f), fontSize = 14.sp)
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Generation tabs ──────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                items((0..9).toList()) { gen ->
                    val label = if (gen == 0) "All" else "Gen $gen"
                    val selected = selectedGen == gen
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) AppRed else AppCard)
                            .clickable { selectedGen = gen }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (selected) AppWhite else AppWhite.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ── Content ──────────────────────────────────────────────────────
            when (val s = state) {
                is PokedexState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AppRed)
                            Spacer(Modifier.height(16.dp))
                            Text("Loading all 1025 Pokémon…", color = AppWhite.copy(alpha = 0.6f), fontSize = 13.sp)
                        }
                    }
                }
                is PokedexState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${s.message}", color = AppRed, fontSize = 14.sp)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.loadAll() },
                                colors = ButtonDefaults.buttonColors(containerColor = AppRed)) {
                                Text("Retry", color = AppWhite)
                            }
                        }
                    }
                }
                is PokedexState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filtered, key = { it.id }) { pokemon ->
                            PokemonListItem(pokemon = pokemon) {
                                val intent = Intent(context, PokemonDetailActivity::class.java)
                                intent.putExtra("POKEMON_ID", pokemon.id)
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonListItem(pokemon: Pokemon, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppCard)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AppGrey),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemon.spriteUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = pokemon.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${pokemon.id.toString().padStart(3, '0')}",
                    color = AppRed, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(text = "Gen ${pokemon.generation}", color = AppWhite.copy(alpha = 0.35f), fontSize = 10.sp)
            }
            Text(pokemon.name, color = AppWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(3.dp))
            TypeBadgeRow(pokemon.type)
        }
        Text("HP ${pokemon.hp}", color = AppWhite.copy(alpha = 0.45f), fontSize = 12.sp)
    }
}

@Composable
fun TypeBadgeRow(type: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        type.split("/").forEach { t -> TypeBadgeSimple(t.trim()) }
    }
}

@Composable
fun TypeBadgeSimple(type: String) {
    val color = typeColor(type)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.25f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(type, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

fun typeColor(type: String): Color = when (type.trim()) {
    "Fire"     -> Color(0xFFFF6B35)
    "Water"    -> Color(0xFF4FC3F7)
    "Grass"    -> Color(0xFF66BB6A)
    "Electric" -> Color(0xFFFFD600)
    "Psychic"  -> Color(0xFFEC407A)
    "Ghost"    -> Color(0xFF7E57C2)
    "Ice"      -> Color(0xFF80DEEA)
    "Normal"   -> Color(0xFF9E9E9E)
    "Poison"   -> Color(0xFFAB47BC)
    "Flying"   -> Color(0xFF90CAF9)
    "Dragon"   -> Color(0xFF5C6BC0)
    "Dark"     -> Color(0xFF546E7A)
    "Steel"    -> Color(0xFF78909C)
    "Rock"     -> Color(0xFFBCAAA4)
    "Ground"   -> Color(0xFFD7CCC8)
    "Bug"      -> Color(0xFF9CCC65)
    "Fighting" -> Color(0xFFEF5350)
    "Fairy"    -> Color(0xFFF48FB1)
    else       -> Color(0xFF78909C)
}
