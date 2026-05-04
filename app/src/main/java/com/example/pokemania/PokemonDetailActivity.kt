package com.example.pokemania

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import java.util.Locale

// ── GameBoy palette ───────────────────────────────────────────────────────────
var GbDark   = Color(0xFF0F380F)
var GbMid    = Color(0xFF306230)
var GbLight  = Color(0xFF8BAC0F)
var GbPale   = Color(0xFF9BBC0F)
var GbBorder = Color(0xFF8BAC0F)

data class GbColorPreset(val name: String, val dark: Color, val mid: Color, val light: Color, val pale: Color)

val gbPresets = listOf(
    GbColorPreset("Classic Green", Color(0xFF0F380F), Color(0xFF306230), Color(0xFF8BAC0F), Color(0xFF9BBC0F)),
    GbColorPreset("Crimson",       Color(0xFF1A0000), Color(0xFF3D0000), Color(0xFFCC2200), Color(0xFFFF4422)),
    GbColorPreset("Ocean Blue",    Color(0xFF001020), Color(0xFF002040), Color(0xFF0066AA), Color(0xFF0099DD)),
    GbColorPreset("Purple Haze",   Color(0xFF0D0020), Color(0xFF200040), Color(0xFF7700CC), Color(0xFFAA33FF)),
    GbColorPreset("Amber",         Color(0xFF1A0E00), Color(0xFF3D2200), Color(0xFFCC7700), Color(0xFFFFAA00)),
    GbColorPreset("Monochrome",    Color(0xFF111111), Color(0xFF333333), Color(0xFFAAAAAA), Color(0xFFEEEEEE)),
)

// ── Data models ───────────────────────────────────────────────────────────────
data class PokemonForm(val label: String, val spriteUrl: String, val tag: String)

data class PokemonRichDetail(
    val forms        : List<PokemonForm> = emptyList(),
    val abilities    : List<String>      = emptyList(),
    val hiddenAbility: String            = "",
    val heldItems    : List<String>      = emptyList(),
    val height       : Int               = 0,
    val weight       : Int               = 0,
    val baseExp      : Int               = 0,
    val spAtk        : Int               = 0,
    val spDef        : Int               = 0,
    val allMoves     : List<String>      = emptyList()
)

// ── Activity ──────────────────────────────────────────────────────────────────
class PokemonDetailActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val pokemonId = intent.getIntExtra("POKEMON_ID", 1)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(0.8f)
                tts?.setSpeechRate(0.9f)
            }
        }

        setContent {
            var gbDark   by remember { mutableStateOf(GbDark) }
            var gbMid    by remember { mutableStateOf(GbMid) }
            var gbLight  by remember { mutableStateOf(GbLight) }
            var gbPale   by remember { mutableStateOf(GbPale) }
            var gbBorder by remember { mutableStateOf(GbBorder) }

            MaterialTheme(colorScheme = darkColorScheme(
                primary = gbPale, background = gbDark, surface = gbMid,
                onPrimary = gbDark, onBackground = gbPale, onSurface = gbPale
            )) {
                PokemonDetailScreen(
                    pokemonId = pokemonId,
                    onBack    = { finish() },
                    onSpeak   = { text -> speakWithBeep(text) },
                    gbDark = gbDark, gbMid = gbMid, gbLight = gbLight,
                    gbPale = gbPale, gbBorder = gbBorder,
                    onPresetSelected = { p ->
                        gbDark = p.dark;  GbDark = p.dark
                        gbMid  = p.mid;   GbMid  = p.mid
                        gbLight = p.light; GbLight = p.light
                        gbPale = p.pale;  GbPale = p.pale
                        gbBorder = p.light; GbBorder = p.light
                    }
                )
            }
        }
    }

    private fun speakWithBeep(text: String) {
        try {
            mediaPlayer?.release(); mediaPlayer = null
            val mp = MediaPlayer.create(this, R.raw.pokedex_beep)
            if (mp != null) {
                mediaPlayer = mp
                mp.setOnCompletionListener { f -> f.release(); mediaPlayer = null
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pokedex_entry") }
                mp.start()
            } else tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pokedex_entry")
        } catch (_: Exception) { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "pokedex_entry") }
    }

    override fun onDestroy() {
        tts?.stop(); tts?.shutdown(); tts = null
        mediaPlayer?.release(); mediaPlayer = null
        super.onDestroy()
    }
}

// ── TypingText ────────────────────────────────────────────────────────────────
@Composable
fun TypingText(
    text: String, modifier: Modifier = Modifier, charDelay: Long = 38L,
    style: TextStyle = TextStyle(color = GbPale, fontSize = 14.sp,
        fontFamily = FontFamily.Monospace, lineHeight = 22.sp),
    onComplete: () -> Unit = {}
) {
    var displayed  by remember(text) { mutableStateOf("") }
    var showCursor by remember { mutableStateOf(true) }
    var done       by remember(text) { mutableStateOf(false) }
    LaunchedEffect(text) {
        displayed = ""; done = false
        for (i in text.indices) { delay(charDelay); displayed = text.substring(0, i + 1) }
        done = true; onComplete()
    }
    LaunchedEffect(Unit) { while (true) { delay(500); showCursor = !showCursor } }
    Text(text = if (done) displayed else "$displayed${if (showCursor) "|" else " "}",
        style = style, modifier = modifier)
}

// ── YouTube player ────────────────────────────────────────────────────────────
fun extractYouTubeId(url: String): String? {
    for (p in listOf(Regex("(?:v=|youtu\\.be/)([A-Za-z0-9_-]{11})"), Regex("embed/([A-Za-z0-9_-]{11})"))) {
        p.find(url)?.let { return it.groupValues[1] }
    }
    return null
}

@Composable
fun YoutubePlayer(url: String, bgColor: Color, modifier: Modifier = Modifier) {
    val videoId = extractYouTubeId(url)
    if (videoId == null) {
        Box(modifier.background(bgColor), contentAlignment = Alignment.Center) {
            Text("Video unavailable", color = GbLight, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        }
        return
    }
    val hex  = String.format("%06X", 0xFFFFFF and bgColor.toArgb())
    val html = """<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1">
<style>body{margin:0;background:#$hex;}.yt{position:relative;padding-bottom:56.25%;height:0;overflow:hidden;}
.yt iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:0;}</style></head>
<body><div class="yt"><iframe src="https://www.youtube.com/embed/$videoId?autoplay=0&rel=0"
allow="accelerometer;autoplay;clipboard-write;encrypted-media;gyroscope;picture-in-picture"
allowfullscreen></iframe></div></body></html>"""
    AndroidView(factory = { ctx ->
        WebView(ctx).apply {
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webChromeClient = WebChromeClient()
            webViewClient   = WebViewClient()
            loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
        }
    }, modifier = modifier)
}

// ── Color preset dialog ───────────────────────────────────────────────────────
@Composable
fun ColorPresetDialog(current: GbColorPreset?, onSelect: (GbColorPreset) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF1A1A1A), tonalElevation = 8.dp) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Screen Color Theme", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    color = Color.White, fontFamily = FontFamily.Monospace)
                Spacer(Modifier.height(14.dp))
                gbPresets.forEach { preset ->
                    val sel = preset.name == current?.name
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (sel) preset.mid else preset.dark)
                        .border(if (sel) 2.dp else 1.dp, if (sel) preset.pale else preset.mid, RoundedCornerShape(8.dp))
                        .clickable { onSelect(preset) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(preset.name, color = preset.pale, fontFamily = FontFamily.Monospace,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(preset.dark, preset.mid, preset.light, preset.pale).forEach { c ->
                                Box(Modifier.size(16.dp).clip(CircleShape).background(c))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}

// ── Detail screen ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId       : Int,
    onBack          : () -> Unit,
    onSpeak         : (String) -> Unit = {},
    gbDark          : Color = GbDark,
    gbMid           : Color = GbMid,
    gbLight         : Color = GbLight,
    gbPale          : Color = GbPale,
    gbBorder        : Color = GbBorder,
    onPresetSelected: (GbColorPreset) -> Unit = {},
    vm              : PokedexViewModel = viewModel()
) {
    val context = LocalContext.current
    val state   by vm.state.collectAsStateWithLifecycle()

    // Base pokemon — resolved immediately from cache or sample list
    var pokemon by remember(pokemonId) {
        mutableStateOf(
            samplePokemonList.find { it.id == pokemonId }
                ?: Pokemon(pokemonId, "#$pokemonId", "Unknown", 0, 0, 0, 0, 1, "")
        )
    }
    LaunchedEffect(state, pokemonId) {
        if (state is PokedexState.Success) {
            (state as PokedexState.Success).list.find { it.id == pokemonId }?.let { pokemon = it }
        }
    }

    // Rich detail — fetched directly from PokéAPI
    var rich        by remember(pokemonId) { mutableStateOf<PokemonRichDetail?>(null) }
    var loadingRich by remember(pokemonId) { mutableStateOf(true) }

    LaunchedEffect(pokemonId) {
        loadingRich = true
        try {
            val detail  = PokeApi.service.getPokemonDetail(pokemonId)
            val species = PokeApi.service.getPokemonSpecies(pokemonId)
            val gen     = generationFromName(species.generation.name)
            val desc    = species.flavorTextEntries
                .firstOrNull { it.language.name == "en" }
                ?.flavorText?.replace("\n", " ")?.replace("\u000c", " ") ?: ""

            val statsMap = detail.stats.associate { it.stat.name to it.baseStat }
            val typeStr  = detail.types.sortedBy { it.slot }
                .joinToString("/") { it.type.name.replaceFirstChar { c -> c.uppercase() } }

            pokemon = Pokemon(
                id = pokemonId,
                name = detail.name.split("-").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
                type = typeStr, hp = statsMap["hp"] ?: 0, attack = statsMap["attack"] ?: 0,
                defense = statsMap["defense"] ?: 0, speed = statsMap["speed"] ?: 0,
                generation = gen, description = desc
            )

            // Build forms list
            val forms = mutableListOf<PokemonForm>()
            val baseArt = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork"

            // Normal
            val normalUrl = detail.sprites.other?.officialArtwork?.frontDefault ?: "$baseArt/$pokemonId.png"
            forms.add(PokemonForm("Normal", normalUrl, "NORMAL"))

            // Shiny
            val shinyUrl = detail.sprites.other?.officialArtwork?.frontShiny ?: "$baseArt/shiny/$pokemonId.png"
            forms.add(PokemonForm("Shiny", shinyUrl, "SHINY"))

            // Alternate forms from species varieties
            val baseName = detail.name
            for (variety in species.varieties.filter { !it.isDefault }) {
                val vName = variety.pokemon.name
                try {
                    val vDetail = PokeApi.service.getPokemonDetailByName(vName)
                    val vUrl = vDetail.sprites.other?.officialArtwork?.frontDefault
                        ?: vDetail.sprites.frontDefault ?: continue
                    val suffix = vName.removePrefix("$baseName-").uppercase()
                    val (label, tag) = when {
                        suffix.startsWith("MEGA-X")  -> "Mega X"   to "MEGA"
                        suffix.startsWith("MEGA-Y")  -> "Mega Y"   to "MEGA"
                        suffix.startsWith("MEGA")    -> "Mega"     to "MEGA"
                        suffix.startsWith("GMAX")    -> "G-Max"    to "GMAX"
                        suffix.startsWith("ALOLA")   -> "Alolan"   to "FORM"
                        suffix.startsWith("GALAR")   -> "Galarian" to "FORM"
                        suffix.startsWith("HISUI")   -> "Hisuian"  to "FORM"
                        suffix.startsWith("PALDEA")  -> "Paldean"  to "FORM"
                        suffix.startsWith("ORIGIN")  -> "Origin"   to "FORM"
                        suffix.startsWith("PRIMAL")  -> "Primal"   to "FORM"
                        suffix.startsWith("ULTRA")   -> "Ultra"    to "FORM"
                        else -> suffix.split("-").joinToString(" ") { w ->
                            w.replaceFirstChar { c -> c.uppercase() }
                        } to "FORM"
                    }
                    forms.add(PokemonForm(label, vUrl, tag))
                } catch (_: Exception) { }
            }

            val abilities = detail.abilities.filter { !it.isHidden }.map {
                it.ability.name.split("-").joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } }
            }
            val hiddenAbility = detail.abilities.firstOrNull { it.isHidden }?.ability?.name
                ?.split("-")?.joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } } ?: ""
            val heldItems = detail.heldItems.take(6).map {
                it.item.name.split("-").joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } }
            }
            val allMoves = detail.moves.take(20).map {
                it.move.name.split("-").joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } }
            }

            rich = PokemonRichDetail(
                forms = forms, abilities = abilities, hiddenAbility = hiddenAbility,
                heldItems = heldItems, height = detail.height, weight = detail.weight,
                baseExp = detail.baseExperience ?: 0,
                spAtk = statsMap["special-attack"] ?: 0, spDef = statsMap["special-defense"] ?: 0,
                allMoves = allMoves
            )
        } catch (_: Exception) { }
        loadingRich = false
    }

    // UI state
    val animeLink       = animeSeriesLinks[pokemon.generation] ?: ""
    var selectedFormIdx by remember(pokemonId) { mutableStateOf(0) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showAnimePlayer by remember { mutableStateOf(false) }
    var currentPreset   by remember { mutableStateOf(gbPresets[0]) }
    var spokenId        by remember { mutableStateOf(-1) }

    LaunchedEffect(pokemon.id, pokemon.description) {
        if (pokemon.description.isNotEmpty() && spokenId != pokemon.id) {
            spokenId = pokemon.id; onSpeak(pokemon.description)
        }
    }

    val availableForms = rich?.forms ?: listOf(PokemonForm("Normal", pokemon.spriteUrl, "NORMAL"))
    val safeIdx        = selectedFormIdx.coerceIn(0, availableForms.lastIndex)
    val activeForm     = availableForms[safeIdx]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(pokemon.name.uppercase(), fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold, color = gbPale)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = gbPale)
                    }
                },
                actions = {
                    IconButton(onClick = { showColorPicker = true }) {
                        Box(Modifier.size(22.dp).clip(CircleShape).background(gbPale).border(2.dp, gbBorder, CircleShape))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = gbMid)
            )
        },
        containerColor = gbDark
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Header ───────────────────────────────────────────────────────
            GbBoxColored(gbMid, gbBorder) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("No.${pokemon.id.toString().padStart(3,'0')}", color = gbLight,
                        fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text(generationLabels[pokemon.generation] ?: "", color = gbLight,
                        fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Spacer(Modifier.height(4.dp))
                Text(pokemon.name.uppercase(), color = gbPale, fontSize = 26.sp,
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    pokemon.type.split("/").forEach { t ->
                        val tc = typeColor(t.trim())
                        Box(Modifier.border(1.dp, tc, RoundedCornerShape(4.dp))
                            .background(tc.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)) {
                            Text(t.trim().uppercase(), color = tc, fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(6.dp))
                    }
                }
                if (rich != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                        InfoChip("Height", "${rich!!.height / 10.0}m", gbLight, gbDark, gbBorder)
                        InfoChip("Weight", "${rich!!.weight / 10.0}kg", gbLight, gbDark, gbBorder)
                        InfoChip("Base EXP", "${rich!!.baseExp}", gbLight, gbDark, gbBorder)
                    }
                }
            }

            // ── Form tabs (only shown when more than 1 form exists) ───────────
            if (availableForms.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableForms.forEachIndexed { idx, form ->
                        item(key = form.label) {
                            val sel = safeIdx == idx
                            val tagColor = when (form.tag) {
                                "SHINY" -> Color(0xFFFFD700)
                                "MEGA"  -> Color(0xFF00BFFF)
                                "GMAX"  -> Color(0xFFFF6B35)
                                "FORM"  -> Color(0xFFAA88FF)
                                else    -> gbPale
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (sel) tagColor.copy(alpha = 0.25f) else gbMid)
                                    .border(if (sel) 2.dp else 1.dp, if (sel) tagColor else gbBorder, RoundedCornerShape(6.dp))
                                    .clickable { selectedFormIdx = idx }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(form.label, color = if (sel) tagColor else gbLight,
                                    fontSize = 10.sp, fontFamily = FontFamily.Monospace,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }

            // ── Sprite ───────────────────────────────────────────────────────
            GbBoxColored(gbMid, gbBorder) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                        .clip(RoundedCornerShape(4.dp)).background(gbDark)
                        .border(2.dp, gbBorder, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (loadingRich && rich == null) {
                        CircularProgressIndicator(color = gbPale, modifier = Modifier.size(32.dp))
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(activeForm.spriteUrl).crossfade(true).build(),
                            contentDescription = "${pokemon.name} ${activeForm.label}",
                            contentScale = ContentScale.Fit, modifier = Modifier.size(180.dp)
                        )
                    }
                }
                val badgeColor = when (activeForm.tag) {
                    "SHINY" -> Color(0xFFFFD700); "MEGA" -> Color(0xFF00BFFF)
                    "GMAX"  -> Color(0xFFFF6B35); "FORM" -> Color(0xFFAA88FF)
                    else    -> null
                }
                if (badgeColor != null) {
                    Spacer(Modifier.height(6.dp))
                    Text("${activeForm.tag} — ${activeForm.label.uppercase()}", color = badgeColor,
                        fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            // ── Pokédex entry ─────────────────────────────────────────────────
            GbBoxColored(gbMid, gbBorder) {
                Text("POKEDEX ENTRY", color = gbLight, fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(8.dp))
                TypingText(
                    text = pokemon.description.ifEmpty { "Loading data..." }, charDelay = 38L,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = gbPale, fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace, lineHeight = 22.sp)
                )
            }

            // ── Stats ─────────────────────────────────────────────────────────
            GbBoxColored(gbMid, gbBorder) {
                Text("BASE STATS", color = gbLight, fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(10.dp))
                GbStatRowColored("HP ",    pokemon.hp,      gbLight, gbPale, gbDark, gbBorder)
                GbStatRowColored("ATK",    pokemon.attack,  gbLight, gbPale, gbDark, gbBorder)
                GbStatRowColored("DEF",    pokemon.defense, gbLight, gbPale, gbDark, gbBorder)
                GbStatRowColored("SPD",    pokemon.speed,   gbLight, gbPale, gbDark, gbBorder)
                if (rich != null) {
                    GbStatRowColored("SP.ATK", rich!!.spAtk, gbLight, gbPale, gbDark, gbBorder)
                    GbStatRowColored("SP.DEF", rich!!.spDef, gbLight, gbPale, gbDark, gbBorder)
                }
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth().height(2.dp).background(gbBorder))
                Spacer(Modifier.height(6.dp))
                val total = pokemon.hp + pokemon.attack + pokemon.defense + pokemon.speed +
                    (rich?.spAtk ?: 0) + (rich?.spDef ?: 0)
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("TOTAL", color = gbLight, fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text(total.toString(), color = gbPale, fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }

            // ── Abilities ─────────────────────────────────────────────────────
            val r = rich
            if (r != null && (r.abilities.isNotEmpty() || r.hiddenAbility.isNotEmpty())) {
                GbBoxColored(gbMid, gbBorder) {
                    Text("ABILITIES", color = gbLight, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    r.abilities.forEach { ab ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(gbPale))
                            Spacer(Modifier.width(8.dp))
                            Text(ab, color = gbPale, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    if (r.hiddenAbility.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFFD700)))
                            Spacer(Modifier.width(8.dp))
                            Text("${r.hiddenAbility} (Hidden)", color = Color(0xFFFFD700),
                                fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // ── Held items ────────────────────────────────────────────────────
            if (r != null && r.heldItems.isNotEmpty()) {
                GbBoxColored(gbMid, gbBorder) {
                    Text("HELD ITEMS", color = gbLight, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    r.heldItems.forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(gbLight))
                            Spacer(Modifier.width(8.dp))
                            Text(item, color = gbPale, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            // ── Moves ─────────────────────────────────────────────────────────
            if (r != null && r.allMoves.isNotEmpty()) {
                GbBoxColored(gbMid, gbBorder) {
                    Text("LEARNABLE MOVES (SAMPLE)", color = gbLight, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    r.allMoves.chunked(2).forEach { pair ->
                        Row(Modifier.fillMaxWidth()) {
                            pair.forEach { move ->
                                Text(move, color = gbPale, fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                            }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(3.dp))
                    }
                }
            }

            // ── Anime section ─────────────────────────────────────────────────
            if (animeLink.isNotEmpty()) {
                GbBoxColored(gbMid, gbBorder) {
                    Text("ANIME", color = gbLight, fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Generation ${pokemon.generation} Series", color = gbPale,
                        fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    Spacer(Modifier.height(10.dp))
                    if (showAnimePlayer) {
                        YoutubePlayer(url = animeLink, bgColor = gbDark,
                            modifier = Modifier.fillMaxWidth().height(220.dp)
                                .clip(RoundedCornerShape(4.dp)).border(2.dp, gbBorder, RoundedCornerShape(4.dp)))
                        Spacer(Modifier.height(8.dp))
                        GbButtonColored("CLOSE PLAYER", gbMid, gbBorder, gbPale) { showAnimePlayer = false }
                    } else {
                        GbButtonColored("PLAY ANIME IN APP", gbMid, gbBorder, gbPale) { showAnimePlayer = true }
                        Spacer(Modifier.height(6.dp))
                        GbButtonColored("OPEN IN YOUTUBE / BROWSER", gbDark, gbBorder, gbLight) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(animeLink)))
                        }
                    }
                }
            }

            // ── Showdown ──────────────────────────────────────────────────────
            GbButtonColored("POKEMON SHOWDOWN — BATTLE ONLINE", gbMid, gbBorder, gbPale) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.pokemonshowdown.com/")))
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showColorPicker) {
        ColorPresetDialog(
            current   = currentPreset,
            onSelect  = { p -> currentPreset = p; onPresetSelected(p); showColorPicker = false },
            onDismiss = { showColorPicker = false }
        )
    }
}

// ── Reusable GB components ────────────────────────────────────────────────────
@Composable
fun GbBoxColored(mid: Color, border: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()
        .border(2.dp, border, RoundedCornerShape(4.dp))
        .background(mid, RoundedCornerShape(4.dp))
        .padding(14.dp), content = content)
}

@Composable
fun GbButtonColored(label: String, bg: Color, border: Color, text: Color, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp))
        .border(2.dp, border, RoundedCornerShape(4.dp)).background(bg)
        .clickable { onClick() }.padding(vertical = 13.dp),
        contentAlignment = Alignment.Center) {
        Text(label, color = text, fontSize = 12.sp, fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun GbStatRowColored(label: String, value: Int, light: Color, pale: Color, dark: Color, border: Color) {
    val fraction = (value / 255f).coerceIn(0f, 1f)
    val barColor = when { fraction > 0.66f -> pale; fraction > 0.33f -> light; else -> light.copy(alpha = 0.5f) }
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, color = light, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Text(value.toString().padStart(3), color = pale, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        }
        Spacer(Modifier.height(3.dp))
        Box(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(2.dp))
            .background(dark).border(1.dp, border, RoundedCornerShape(2.dp))) {
            Box(Modifier.fillMaxWidth(fraction).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(barColor))
        }
    }
}

@Composable
fun InfoChip(label: String, value: String, light: Color, dark: Color, border: Color) {
    Column(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(dark)
        .border(1.dp, border, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = light, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(value, color = light.copy(alpha = 0.9f), fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

// ── Legacy aliases for other screens ─────────────────────────────────────────
@Composable fun GbBox(minHeight: Dp = 0.dp, content: @Composable ColumnScope.() -> Unit) = GbBoxColored(GbMid, GbBorder, content)
@Composable fun GbStatRow(label: String, value: Int) = GbStatRowColored(label, value, GbLight, GbPale, GbDark, GbBorder)
@Composable fun GbTypeBadge(type: String) {
    Box(Modifier.border(1.dp, GbBorder, RoundedCornerShape(3.dp)).background(GbDark, RoundedCornerShape(3.dp)).padding(horizontal = 10.dp, vertical = 3.dp)) {
        Text(type.uppercase(), color = GbPale, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
@Composable fun GbButton(label: String, onClick: () -> Unit) = GbButtonColored(label, GbMid, GbBorder, GbPale, onClick)

@Composable
fun StatRow(label: String, value: Int, barColor: Color) {
    Column {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text(label, color = AppWhite.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.width(70.dp))
            Text(value.toString(), color = AppWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(AppGrey)) {
            Box(Modifier.fillMaxWidth((value / 255f).coerceIn(0f, 1f)).height(8.dp).clip(RoundedCornerShape(4.dp)).background(barColor))
        }
    }
}
