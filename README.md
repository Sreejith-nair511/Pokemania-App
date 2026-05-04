# PokeMania

A comprehensive Android Pokédex application built with Jetpack Compose, covering all 1025 Pokémon across Generations I through IX. The app fetches live data from PokéAPI, supports alternate forms including Mega Evolutions and Gigantamax, and includes a battle simulator, type effectiveness chart, anime links, and competitive resources.

---

## Features

### Pokédex
- All 1025 Pokémon loaded live from PokéAPI
- Progressive loading — list populates in real time as batches complete
- Filter by generation (Gen I through Gen IX)
- Search by name across the full roster
- Official artwork sprites for every entry

### Pokémon Detail Screen
- GameBoy-style retro UI with six selectable color themes
- Typing text animation for Pokédex entries (character-by-character reveal)
- Text-to-Speech narration with robotic pitch, triggered after a beep sound
- Sprite form switcher: Normal, Shiny, Mega Evolution, Gigantamax, regional variants
  - Alternate forms only appear for Pokémon that actually have them (fetched from PokéAPI species varieties)
- Full base stats including Special Attack and Special Defense
- Abilities including hidden ability
- Held items found in the wild
- Sample learnable moves
- Physical data: height, weight, base experience
- Embedded YouTube player for the anime series of that generation
- Direct link to Pokémon Showdown for competitive battles

### Battle Simulator
- Pick any two Pokémon from the full 1025 roster
- Search by name during selection
- Sprite display for each fighter
- Score-based winner calculation using combined base stats
- Result card showing both scores

### Type Chart
- Full 18-type effectiveness table
- Tap any attacking type to see super effective, not very effective, no effect, and normal matchups
- Scrollable full grid view showing all 18x18 combinations

### News and Anime Links
- Official Pokémon website
- Pokémon Asia Hindi Official YouTube channel
- Indigo League and Diamond and Pearl playlists
- Official Pokémon TV channel
- Pokémon on Hotstar India
- Pokémon Showdown battle simulator
- Serebii, Bulbapedia, Smogon, PokémonDB
- Pokémon HOME, Pokémon GO Live, TCG Online

### Resources
- Emulators: Delta, RetroArch, mGBA, DeSmuME, Citra, Yuzu/Ryujinx
- Competitive tools: Showdown, Smogon Dex, Damage Calculator, VGC
- Databases: Bulbapedia, PokémonDB, Serebii, PokéAPI
- ROM hacks and fan games: PokéCommunity, Pokémon Reborn, Insurgence, Universal Randomizer
- Community: Pokémon HOME, r/pokemon, r/pokemontrades, TCGPlayer

### Theme System
- Five app-wide themes: Crimson Dark, Midnight Blue, Forest Green, Slate Light, Pure AMOLED
- Persistent theme selection saved to SharedPreferences
- Six GameBoy screen color presets on the detail screen: Classic Green, Crimson, Ocean Blue, Purple Haze, Amber, Monochrome

---

## Technical Stack

| Component | Library / Version |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | ViewModel + StateFlow |
| Networking | Retrofit 2 + Gson |
| Image loading | Coil 2 |
| Async | Kotlin Coroutines |
| Text-to-Speech | Android TextToSpeech API |
| Video | WebView with YouTube embed |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 |
| Build tools | Gradle 9.1, AGP 8.5.2, Kotlin 2.0.21 |

---

## Data Source

All Pokémon data is fetched from [PokéAPI](https://pokeapi.co) — a free, open REST API with no authentication required. Sprites are served from the [PokeAPI sprites GitHub repository](https://github.com/PokeAPI/sprites).

The app loads all 1025 Pokémon in sequential batches of 20 with a 200ms delay between batches to respect PokéAPI rate limits. The list populates progressively as each batch completes. A bundled sample list of ~80 Pokémon serves as an offline fallback.

---

## Project Structure

```
app/src/main/java/com/example/pokemania/
├── MainActivity.kt           Home screen, theme switcher, navigation
├── AppTheme.kt               Theme definitions and CompositionLocal
├── Pokemon.kt                Data class, sample list, generation labels, anime links
├── PokeApiService.kt         Retrofit interface and all API data models
├── PokedexViewModel.kt       Coroutine-based loader for all 1025 Pokémon
├── PokedexActivity.kt        Pokédex list with search and generation filter
├── PokemonDetailActivity.kt  Detail screen with forms, TTS, stats, anime player
├── BattleActivity.kt         Battle simulator
├── TypeChartActivity.kt      Type effectiveness chart
├── NewsActivity.kt           News and anime links
└── ResourcesActivity.kt      Emulators, tools, community links
```

---

## Building

Requirements:
- Android Studio Hedgehog or later
- JDK 11
- Internet connection (for PokéAPI data at runtime)

```bash
# Clone the repository
git clone https://github.com/Sreejith-nair511/Pokemania-App.git
cd Pokemania-App

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

---

## Pokémon Coverage

| Generation | Region | Count |
|---|---|---|
| Gen I | Kanto | 151 |
| Gen II | Johto | 100 |
| Gen III | Hoenn | 135 |
| Gen IV | Sinnoh | 107 |
| Gen V | Unova | 156 |
| Gen VI | Kalos | 72 |
| Gen VII | Alola | 88 |
| Gen VIII | Galar | 96 |
| Gen IX | Paldea | 120 |
| **Total** | | **1025** |

---

## Alternate Forms Supported

The detail screen fetches all species varieties from PokéAPI and displays only the forms that actually exist for each Pokémon. Supported form types include:

- Mega Evolutions (e.g. Mega Charizard X, Mega Charizard Y)
- Gigantamax forms (e.g. Gigantamax Charizard, Gigantamax Pikachu)
- Regional variants: Alolan, Galarian, Hisuian, Paldean
- Origin, Primal, Ultra forms
- Other species-specific alternate forms

---

## Anime Links

| Generation | Series | Source |
|---|---|---|
| Gen I & II | Indigo League / Johto | Pokémon Asia Hindi Official |
| Gen III | Advanced Generation | YouTube playlist |
| Gen IV | Diamond and Pearl | YouTube playlist |
| Gen V–IX | Black/White through Horizons | Official Pokémon TV |
| All | Streaming | Hotstar India |

---

## License

This project is for educational and personal use. Pokémon and all related names are trademarks of Nintendo, Game Freak, and The Pokémon Company. This app is not affiliated with or endorsed by any of those entities. All data is sourced from the open PokéAPI project.
