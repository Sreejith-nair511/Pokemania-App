package com.example.pokemania

data class Pokemon(
    val id: Int,
    val name: String,
    val type: String,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val generation: Int,
    val description: String,
    val animeUrl: String = ""
) {
    // Official sprite from PokéAPI (works without API key)
    val spriteUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}

// ── Generation labels ────────────────────────────────────────────────────────
val generationLabels = mapOf(
    0 to "All",
    1 to "Gen I · Kanto",
    2 to "Gen II · Johto",
    3 to "Gen III · Hoenn",
    4 to "Gen IV · Sinnoh",
    5 to "Gen V · Unova",
    6 to "Gen VI · Kalos",
    7 to "Gen VII · Alola",
    8 to "Gen VIII · Galar",
    9 to "Gen IX · Paldea"
)

// ── Anime series links ───────────────────────────────────────────────────────
val animeSeriesLinks = mapOf(
    1 to "https://www.youtube.com/@PokemonAsiaHindiOfficial",  // Indigo League / Kanto
    2 to "https://www.youtube.com/@PokemonAsiaHindiOfficial",  // Johto
    3 to "https://www.youtube.com/watch?v=qvMitRMiczs",        // Advanced Generation
    4 to "https://www.youtube.com/watch?v=v5zPMrwrFjk",        // Diamond & Pearl
    5 to "https://www.youtube.com/@OfficialPokemonTV",         // Black & White
    6 to "https://www.youtube.com/@OfficialPokemonTV",         // XY
    7 to "https://www.hotstar.com/in/shows/pokemon/1971003167",// Sun & Moon
    8 to "https://www.youtube.com/@OfficialPokemonTV",         // Journeys / Galar
    9 to "https://www.youtube.com/@OfficialPokemonTV"          // Horizons / Paldea
)

val samplePokemonList = listOf(
    // ── GEN 1 · KANTO ────────────────────────────────────────────────────────
    Pokemon(1,   "Bulbasaur",   "Grass/Poison",  45,  49,  49,  45, 1, "A strange seed was planted on its back at birth. The plant sprouts and grows with this Pokémon."),
    Pokemon(2,   "Ivysaur",     "Grass/Poison",  60,  62,  63,  60, 1, "When the bulb on its back grows large, it appears to lose the ability to stand on its hind legs."),
    Pokemon(3,   "Venusaur",    "Grass/Poison",  80,  82,  83,  80, 1, "The plant blooms when it is absorbing solar energy. It stays on the move to seek sunlight."),
    Pokemon(4,   "Charmander",  "Fire",          39,  52,  43,  65, 1, "The flame on its tail indicates Charmander's life force. If it is healthy, the flame burns brightly."),
    Pokemon(5,   "Charmeleon",  "Fire",          58,  64,  58,  80, 1, "When it swings its burning tail, it elevates the temperature to unbearably high levels."),
    Pokemon(6,   "Charizard",   "Fire/Flying",   78,  84,  78, 100, 1, "It spits fire that is hot enough to melt boulders. It may cause forest fires by blowing flames."),
    Pokemon(7,   "Squirtle",    "Water",         44,  48,  65,  43, 1, "After birth, its back swells and hardens into a shell. It powerfully sprays foam from its mouth."),
    Pokemon(8,   "Wartortle",   "Water",         59,  63,  80,  58, 1, "It is recognized as a symbol of longevity. If its shell has algae on it, that Wartortle is very old."),
    Pokemon(9,   "Blastoise",   "Water",         79,  83, 100,  78, 1, "A brutal Pokémon with pressurized water jets on its shell. They are used for high-speed tackles."),
    Pokemon(25,  "Pikachu",     "Electric",      35,  55,  40,  90, 1, "When several of these Pokémon gather, their electricity can build and cause lightning storms."),
    Pokemon(26,  "Raichu",      "Electric",      60,  90,  55, 110, 1, "Its long tail serves as a ground to protect itself from its own high-voltage power."),
    Pokemon(39,  "Jigglypuff",  "Normal/Fairy", 115,  45,  20,  20, 1, "When its huge eyes light up, it sings a mysteriously soothing melody that lulls its enemies to sleep."),
    Pokemon(52,  "Meowth",      "Normal",        40,  45,  35,  90, 1, "Adores round objects. It wanders the streets on a nightly basis to look for dropped loose change."),
    Pokemon(54,  "Psyduck",     "Water",         50,  52,  48,  55, 1, "While lulling its enemies with its vacant look, this wily Pokémon will use psychokinetic powers."),
    Pokemon(63,  "Abra",        "Psychic",       25,  20,  15,  90, 1, "Sleeps 18 hours a day. If it senses danger, it teleports itself to safety even while asleep."),
    Pokemon(65,  "Alakazam",    "Psychic",       55,  50,  45, 120, 1, "Its brain can outperform a supercomputer. Its IQ (intelligence quotient) is said to be 5000."),
    Pokemon(94,  "Gengar",      "Ghost/Poison",  60,  65,  60, 110, 1, "On the night of a full moon, if shadows move on their own and laugh, it must be Gengar's doing."),
    Pokemon(129, "Magikarp",    "Water",         20,  10,  55,  80, 1, "In the distant past, it was somewhat stronger than the horribly weak Pokémon it is today."),
    Pokemon(130, "Gyarados",    "Water/Flying",  95, 125,  79,  81, 1, "Once it begins to rampage, a Gyarados will burn everything down, even in a harsh storm."),
    Pokemon(131, "Lapras",      "Water/Ice",    130,  85,  80,  60, 1, "A gentle soul that can read the minds of people. It can ferry people across the sea on its back."),
    Pokemon(143, "Snorlax",     "Normal",       160, 110,  65,  30, 1, "Very lazy. Just eats and sleeps. As its rotund bulk builds, it becomes steadily more slothful."),
    Pokemon(144, "Articuno",    "Ice/Flying",    90,  85, 100,  85, 1, "A legendary bird Pokémon that can control ice. The flapping of its wings chills the air."),
    Pokemon(145, "Zapdos",      "Electric/Flying",90, 90,  85, 100, 1, "A legendary bird Pokémon that has the ability to control electricity. It usually lives in thunderclouds."),
    Pokemon(146, "Moltres",     "Fire/Flying",   90, 100,  90,  90, 1, "Known as the legendary bird of fire. Every flap of its wings creates a dazzling flash of flames."),
    Pokemon(147, "Dratini",     "Dragon",        41,  64,  45,  50, 1, "Long considered a mythical Pokémon until recently when a small colony was found living underwater."),
    Pokemon(149, "Dragonite",   "Dragon/Flying",  91, 134,  95,  80, 1, "An extremely rarely seen marine Pokémon. Its intelligence is said to match that of humans."),
    Pokemon(150, "Mewtwo",      "Psychic",      106, 110,  90, 130, 1, "A Pokémon that was created by genetic manipulation. However, even science cannot explain this Pokémon's tremendous power."),
    Pokemon(151, "Mew",         "Psychic",      100, 100, 100, 100, 1, "So rare that it is still said to be a mirage by many experts. Only a few people have seen it worldwide."),

    // ── GEN 2 · JOHTO ────────────────────────────────────────────────────────
    Pokemon(152, "Chikorita",   "Grass",         45,  49,  65,  45, 2, "A sweet aroma gently wafts from the leaf on its head. It is docile and loves to soak up the sun's rays."),
    Pokemon(155, "Cyndaquil",   "Fire",          39,  52,  43,  65, 2, "It is timid, and always curls itself up in a ball. If attacked, it flares up its back for protection."),
    Pokemon(158, "Totodile",    "Water",         50,  65,  64,  43, 2, "Its well-developed jaws are powerful and capable of crushing anything. Even its trainer must be careful."),
    Pokemon(175, "Togepi",      "Fairy",         35,  20,  65,  20, 2, "The shell seems to be filled with joy. It is said that it will share good luck when treated kindly."),
    Pokemon(196, "Espeon",      "Psychic",       65,  65,  60, 110, 2, "By reading air currents, it can predict things such as the weather or its foe's next move."),
    Pokemon(197, "Umbreon",     "Dark",          95,  65, 110,  65, 2, "When exposed to the moon's aura, the rings on its body glow faintly and it gains a mysterious power."),
    Pokemon(245, "Suicune",     "Water",        100,  75, 115,  85, 2, "This Pokémon races across the land. It is said that north winds will somehow blow whenever it appears."),
    Pokemon(249, "Lugia",       "Psychic/Flying",106, 90, 130, 110, 2, "It is said to be the guardian of the seas. It is rumored to have been seen on the night of a storm."),
    Pokemon(250, "Ho-Oh",       "Fire/Flying",  106, 130,  90,  90, 2, "Its feathers glow in seven colors. It is said that those who see Ho-Oh are promised eternal happiness."),
    Pokemon(251, "Celebi",      "Psychic/Grass", 100, 100, 100, 100, 2, "This Pokémon came from the future by crossing over time. It is thought that so long as Celebi appears, a bright and shining future awaits."),

    // ── GEN 3 · HOENN ────────────────────────────────────────────────────────
    Pokemon(252, "Treecko",     "Grass",         40,  45,  35,  70, 3, "It quickly scales even vertical walls. It senses humidity with its tail to predict the next day's weather."),
    Pokemon(255, "Torchic",     "Fire",          45,  60,  40,  45, 3, "A fire burns inside, so it feels very warm to hug. It launches fireballs of 1,800 degrees F."),
    Pokemon(258, "Mudkip",      "Water",         50,  70,  50,  40, 3, "The fin on Mudkip's head acts as highly sensitive radar. Using this fin to sense movements of water and air, this Pokémon can determine what is taking place around it without using its eyes."),
    Pokemon(282, "Gardevoir",   "Psychic/Fairy", 68,  65, 65,  80, 3, "To protect its Trainer, it will expend all its psychic power to create a small black hole."),
    Pokemon(384, "Rayquaza",    "Dragon/Flying", 105, 150,  90,  95, 3, "It lives in the ozone layer far above the clouds and cannot be seen from the ground."),
    Pokemon(385, "Jirachi",     "Steel/Psychic", 100, 100, 100, 100, 3, "A legend states that Jirachi will make true any wish that is written on notes attached to its head when it awakens."),
    Pokemon(386, "Deoxys",      "Psychic",       50, 150,  50, 150, 3, "The DNA of a space virus underwent a sudden mutation upon exposure to a laser beam and resulted in Deoxys."),

    // ── GEN 4 · SINNOH ───────────────────────────────────────────────────────
    Pokemon(387, "Turtwig",     "Grass",         55,  68,  64,  31, 4, "Made from soil, the shell on its back hardens when it drinks water. It lives along lakes."),
    Pokemon(390, "Chimchar",    "Fire",          44,  58,  44,  61, 4, "The gas made in its belly burns from its rear end. The fire burns even when it rains."),
    Pokemon(393, "Piplup",      "Water",         53,  51,  53,  40, 4, "Because it is very proud, it hates accepting food from people. Its thick down guards it from cold."),
    Pokemon(448, "Lucario",     "Fighting/Steel", 70, 110,  70, 90, 4, "It has the ability to sense the Auras of all things. It understands human speech."),
    Pokemon(483, "Dialga",      "Steel/Dragon", 100, 120, 120,  90, 4, "It has the power to control time. It appears in Sinnoh-region myths as an ancient deity."),
    Pokemon(484, "Palkia",      "Water/Dragon", 90, 120, 100, 100, 4, "It has the ability to distort space. It is described as a deity in Sinnoh-region mythology."),
    Pokemon(487, "Giratina",    "Ghost/Dragon", 150, 100, 120,  90, 4, "This Pokémon is said to live in a world on the reverse side of ours, where common knowledge is distorted and strange."),
    Pokemon(493, "Arceus",      "Normal",       120, 120, 120, 120, 4, "It is described in mythology as the Pokémon that shaped the universe with its 1,000 arms."),

    // ── GEN 5 · UNOVA ────────────────────────────────────────────────────────
    Pokemon(495, "Snivy",       "Grass",         45,  45,  55,  63, 5, "They photosynthesize by bathing their tails in sunlight. When they are not feeling well, their tails droop."),
    Pokemon(498, "Tepig",       "Fire",          65,  63,  45,  45, 5, "It can deftly dodge its foe's attacks while shooting fireballs from its nose. It roasts berries before eating them."),
    Pokemon(501, "Oshawott",    "Water",         55,  55,  45,  45, 5, "It fights using the scalchop on its stomach. In response to an attack, it retaliates immediately by slashing."),
    Pokemon(571, "Zoroark",     "Dark",          60, 105,  60, 105, 5, "Bonds between these Pokémon are very strong. It protects the safety of its pack by tricking its opponents."),
    Pokemon(643, "Reshiram",    "Dragon/Fire",  100, 120, 100,  90, 5, "This Pokémon appears in legends. It sends flames into the air from its tail, burning up everything around it."),
    Pokemon(644, "Zekrom",      "Dragon/Electric",100,150,120,90, 5, "This Pokémon appears in legends. It can scorch the world with lightning. It assists those who want to build an ideal world."),
    Pokemon(646, "Kyurem",      "Dragon/Ice",   125, 130,  90,  95, 5, "It generates a powerful, freezing energy inside itself, but its body became frozen when the energy leaked out."),

    // ── GEN 6 · KALOS ────────────────────────────────────────────────────────
    Pokemon(650, "Chespin",     "Grass",         56,  61,  65,  38, 6, "The quills on its head are usually soft. When it flexes them, the points become so hard and sharp that they can pierce rock."),
    Pokemon(653, "Fennekin",    "Fire",          40,  45,  40,  60, 6, "Eating a twig fills it with energy, and its roomy ears give vent to air hotter than 390 degrees Fahrenheit."),
    Pokemon(656, "Froakie",     "Water",         41,  56,  40,  71, 6, "It secretes flexible bubbles from its chest and back. The bubbles reduce the damage it would otherwise take when attacked."),
    Pokemon(700, "Sylveon",     "Fairy",         95,  65,  65,  60, 6, "It wraps its ribbonlike feelers around the arm of its beloved Trainer and walks with him or her."),
    Pokemon(716, "Xerneas",     "Fairy",         126, 131,  95,  99, 6, "Legends say it can share eternal life. It slept for 1,000 years in the form of a tree before its revival."),
    Pokemon(717, "Yveltal",     "Dark/Flying",  126, 131,  95,  99, 6, "When this legendary Pokémon's wings and tail feathers spread wide and glow red, it absorbs the life force of living creatures."),
    Pokemon(718, "Zygarde",     "Dragon/Ground", 108, 100, 121,  95, 6, "It's hypothesized that it's monitoring those who destroy the ecosystem from deep in the cave where it lives."),

    // ── GEN 7 · ALOLA ────────────────────────────────────────────────────────
    Pokemon(722, "Rowlet",      "Grass/Flying",  68,  55,  55,  42, 7, "This wary Pokémon uses photosynthesis to store up energy during the day, while becoming active at night."),
    Pokemon(725, "Litten",      "Fire",          45,  65,  40,  70, 7, "While grooming itself, it builds up fur inside its stomach. It sets the fur alight and spits out fireballs."),
    Pokemon(728, "Popplio",     "Water",         50,  54,  54,  40, 7, "This Pokémon snorts body fluids from its nose, blowing balloons to smash into its foes."),
    Pokemon(745, "Lycanroc",    "Rock",          75, 115,  65,  112, 7, "It leaps at foes and then slashes them with the rocks on its mane. It has a wild temperament."),
    Pokemon(785, "Tapu Koko",   "Electric/Fairy", 70, 115,  85, 130, 7, "This guardian deity of Melemele is brimming with curiosity. It summons thunderclouds and stores their lightning inside its body."),
    Pokemon(800, "Necrozma",    "Psychic",       97, 107, 101, 101, 7, "Reminiscent of the Ultra Beasts, this life-form, apparently asleep underground, is thought to have come from another world in ancient times."),
    Pokemon(802, "Marshadow",   "Fighting/Ghost", 90, 125,  80, 125, 7, "Able to conceal itself in shadows, it never appears before humans, so its very existence was the stuff of myth."),

    // ── GEN 8 · GALAR ────────────────────────────────────────────────────────
    Pokemon(810, "Grookey",     "Grass",         50,  65,  50,  65, 8, "When it uses its special stick to strike up a beat, the sound waves produced carry revitalizing energy to the plants and flowers in the area."),
    Pokemon(813, "Scorbunny",   "Fire",          50,  71,  40,  69, 8, "A warm-up of running around gets fire energy coursing through this Pokémon's body. Once that happens, it's ready to fight at full power."),
    Pokemon(816, "Sobble",      "Water",         50,  40,  40,  70, 8, "When scared, this Pokémon cries. Its tears pack the chemical punch of 100 onions, and attackers won't be able to stop crying."),
    Pokemon(888, "Zacian",      "Fairy",         92, 130, 115, 138, 8, "Known as a legendary hero, this Pokémon absorbs metal particles, transforming them into a weapon it uses to battle."),
    Pokemon(889, "Zamazenta",   "Fighting",      92, 130, 145,  138, 8, "In times past, it worked together with a king of the people to save the Galar region. It absorbs metal that it then uses in battle."),
    Pokemon(890, "Eternatus",   "Poison/Dragon", 140, 85,  95, 130, 8, "The core on its chest absorbs energy emanating from the lands of the Galar region. This energy is what allows Eternatus to stay active."),
    Pokemon(898, "Calyrex",     "Psychic/Grass", 100, 80,  80,  80, 8, "Calyrex is a merciful Pokémon, capable of providing healing and blessings. It ruled all of Galar in ancient times."),

    // ── GEN 9 · PALDEA ───────────────────────────────────────────────────────
    Pokemon(906, "Sprigatito",  "Grass",         40,  61,  54,  65, 9, "Its fluffy fur is similar in composition to plants. This Pokémon frequently washes its face to keep it from drying out."),
    Pokemon(909, "Fuecoco",     "Fire",          67,  45,  59,  36, 9, "It lies on warm rocks and uses the heat absorbed by its square-shaped scales to create fire energy."),
    Pokemon(912, "Quaxly",      "Water",         55,  65,  45,  50, 9, "This Pokémon migrated to Paldea from distant lands long ago. The gel secreted by its feathers repels water and grime."),
    Pokemon(987, "Flutter Mane","Ghost/Fairy",   55,  55,  55, 105, 9, "There is something reminiscent of an old-fashioned doll about this Pokémon. It's said to be a Paradox Pokémon."),
    Pokemon(995, "Iron Hands",  "Fighting/Electric",154,140,108,50, 9, "This Pokémon resembles a creature described in a paranormal magazine as a mysterious life-form."),
    Pokemon(1007,"Koraidon",    "Fighting/Dragon",100,135,115,135, 9, "It's said to have fought against a human long ago. The wounds it suffered then still haven't healed."),
    Pokemon(1008,"Miraidon",    "Electric/Dragon",100,85, 100,135, 9, "It's said to have fought against a human long ago. The wounds it suffered then still haven't healed.")
)
