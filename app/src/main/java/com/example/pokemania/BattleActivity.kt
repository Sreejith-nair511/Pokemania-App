package com.example.pokemania

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

class BattleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary      = AppRed,
                background   = AppDark,
                surface      = AppCard,
                onBackground = AppWhite,
                onSurface    = AppWhite
            )) {
                BattleScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(onBack: () -> Unit, vm: PokedexViewModel = viewModel()) {
    var fighter1    by remember { mutableStateOf<Pokemon?>(null) }
    var fighter2    by remember { mutableStateOf<Pokemon?>(null) }
    var winner      by remember { mutableStateOf<Pokemon?>(null) }
    var picking     by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val state by vm.state.collectAsStateWithLifecycle()
    val allPokemon = when (val s = state) {
        is PokedexState.Success -> s.list
        else -> samplePokemonList
    }
    val filteredForPick = remember(allPokemon, searchQuery) {
        allPokemon.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Battle Simulator", fontWeight = FontWeight.Bold, color = AppWhite)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppCard)
            )
        },
        containerColor = AppDark
    ) { padding ->

        if (picking > 0) {
            // ── Pokémon picker ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text("Select Fighter $picking",
                    color    = AppRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppCard)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine    = true,
                        cursorBrush   = SolidColor(AppRed),
                        textStyle     = TextStyle(color = AppWhite, fontSize = 14.sp),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty())
                                Text("Search…", color = AppWhite.copy(alpha = 0.3f), fontSize = 14.sp)
                            inner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier        = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding  = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredForPick, key = { it.id }) { pokemon ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppCard)
                                .clickable {
                                    if (picking == 1) fighter1 = pokemon else fighter2 = pokemon
                                    winner      = null
                                    picking     = 0
                                    searchQuery = ""
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
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
                                        contentScale       = ContentScale.Fit,
                                        modifier           = Modifier.size(38.dp)
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text       = "#${pokemon.id.toString().padStart(3, '0')}  ${pokemon.name}",
                                        color      = AppWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 14.sp
                                    )
                                    Text(
                                        text     = pokemon.type,
                                        color    = AppWhite.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text     = "HP ${pokemon.hp}",
                                color    = AppWhite.copy(alpha = 0.4f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

        } else {
            // ── Battle arena ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fighter slots
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FighterSlot(
                        label    = "Fighter 1",
                        pokemon  = fighter1,
                        isWinner = winner != null && winner == fighter1,
                        modifier = Modifier.weight(1f),
                        onClick  = { picking = 1 }
                    )
                    FighterSlot(
                        label    = "Fighter 2",
                        pokemon  = fighter2,
                        isWinner = winner != null && winner == fighter2,
                        modifier = Modifier.weight(1f),
                        onClick  = { picking = 2 }
                    )
                }

                Text("VS", fontSize = 28.sp, fontWeight = FontWeight.Black, color = AppRed)

                // Battle button
                Button(
                    onClick = {
                        val p1 = fighter1
                        val p2 = fighter2
                        if (p1 != null && p2 != null) {
                            val s1 = p1.hp + p1.attack + p1.defense + p1.speed
                            val s2 = p2.hp + p2.attack + p2.defense + p2.speed
                            winner = if (s1 >= s2) p1 else p2
                        }
                    },
                    enabled  = fighter1 != null && fighter2 != null,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = AppRed,
                        disabledContainerColor = AppGrey
                    )
                ) {
                    Text(
                        "START BATTLE",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = AppWhite
                    )
                }

                // Result card
                winner?.let { w ->
                    val s1 = fighter1!!.hp + fighter1!!.attack + fighter1!!.defense + fighter1!!.speed
                    val s2 = fighter2!!.hp + fighter2!!.attack + fighter2!!.defense + fighter2!!.speed
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1B2A1B))
                            .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = "${w.name} Wins",
                                fontSize   = 24.sp,
                                fontWeight = FontWeight.Black,
                                color      = Color(0xFF4CAF50),
                                textAlign  = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text      = "${fighter1!!.name}: $s1  vs  ${fighter2!!.name}: $s2",
                                fontSize  = 12.sp,
                                color     = AppWhite.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FighterSlot(
    label    : String,
    pokemon  : Pokemon?,
    isWinner : Boolean,
    modifier : Modifier = Modifier,
    onClick  : () -> Unit
) {
    val borderColor = when {
        isWinner        -> Color(0xFF4CAF50)
        pokemon != null -> AppRed.copy(alpha = 0.6f)
        else            -> AppGrey
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppCard)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (pokemon == null) {
                Text("+", fontSize = 28.sp, color = AppGrey, fontWeight = FontWeight.Light)
                Spacer(Modifier.height(4.dp))
                Text(label, color = AppWhite.copy(alpha = 0.4f), fontSize = 12.sp)
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(pokemon.spriteUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = pokemon.name,
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = pokemon.name,
                    color      = if (isWinner) Color(0xFF4CAF50) else AppWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp,
                    textAlign  = TextAlign.Center
                )
                Text(
                    text      = pokemon.type,
                    color     = AppWhite.copy(alpha = 0.5f),
                    fontSize  = 10.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = "HP ${pokemon.hp}",
                    color      = Color(0xFF4CAF50),
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
