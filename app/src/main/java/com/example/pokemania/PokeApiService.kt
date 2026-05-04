package com.example.pokemania

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ── Leaf / shared models (no forward references) ─────────────────────────────

data class PokeListResponse(
    val count: Int,
    val results: List<NamedResource>
)

data class NamedResource(
    val name: String,
    val url: String
)

data class TypeSlot(
    val slot: Int,
    val type: NamedResource
)

data class StatSlot(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: NamedResource
)

data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_shiny")   val frontShiny: String?
)

data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork?
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_shiny")   val frontShiny: String?,
    val other: OtherSprites?
)

data class HeldItemSlot(
    val item: NamedResource
)

data class MoveSlot(
    val move: NamedResource
)

data class AbilitySlot(
    val ability: NamedResource,
    @SerializedName("is_hidden") val isHidden: Boolean = false
)

data class FlavorText(
    @SerializedName("flavor_text") val flavorText: String,
    val language: NamedResource,
    val version: NamedResource
)

// ── Composite models ──────────────────────────────────────────────────────────

data class PokeDetail(
    val id      : Int,
    val name    : String,
    val types   : List<TypeSlot>,
    val stats   : List<StatSlot>,
    val sprites : Sprites,
    val species : NamedResource,
    @SerializedName("held_items")      val heldItems      : List<HeldItemSlot> = emptyList(),
    val moves                          : List<MoveSlot>    = emptyList(),
    val abilities                      : List<AbilitySlot> = emptyList(),
    val weight                         : Int               = 0,
    val height                         : Int               = 0,
    @SerializedName("base_experience") val baseExperience : Int?               = null
)

data class SpeciesDetail(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorText>,
    val generation: NamedResource,
    val varieties: List<PokemonVariety> = emptyList()
)

data class PokemonVariety(
    @SerializedName("is_default") val isDefault: Boolean,
    val pokemon: NamedResource
)

// Form detail — gives us sprites for alternate forms
data class FormDetail(
    val id     : Int,
    val name   : String,
    val sprites: FormSprites
)

data class FormSprites(
    @SerializedName("front_default") val frontDefault: String?,
    @SerializedName("front_shiny")   val frontShiny  : String?
)

// ── Retrofit interface ────────────────────────────────────────────────────────

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit")  limit : Int = 1025,
        @Query("offset") offset: Int = 0
    ): PokeListResponse

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(@Path("id") id: Int): PokeDetail

    @GET("pokemon/{name}")
    suspend fun getPokemonDetailByName(@Path("name") name: String): PokeDetail

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(@Path("id") id: Int): SpeciesDetail

    @GET("pokemon-form/{name}")
    suspend fun getPokemonForm(@Path("name") name: String): FormDetail
}

// ── Singleton Retrofit client ─────────────────────────────────────────────────

object PokeApi {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    val service: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

fun PokeDetail.toPokemon(description: String = "", generation: Int = 1): Pokemon {
    val typeStr  = types.sortedBy { it.slot }
        .joinToString("/") { it.type.name.replaceFirstChar { c -> c.uppercase() } }
    val statsMap = stats.associate { it.stat.name to it.baseStat }
    return Pokemon(
        id          = id,
        name        = name.split("-").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
        type        = typeStr,
        hp          = statsMap["hp"]      ?: 0,
        attack      = statsMap["attack"]  ?: 0,
        defense     = statsMap["defense"] ?: 0,
        speed       = statsMap["speed"]   ?: 0,
        generation  = generation,
        description = description
    )
}

fun generationFromName(genName: String): Int = when (genName) {
    "generation-i"    -> 1
    "generation-ii"   -> 2
    "generation-iii"  -> 3
    "generation-iv"   -> 4
    "generation-v"    -> 5
    "generation-vi"   -> 6
    "generation-vii"  -> 7
    "generation-viii" -> 8
    "generation-ix"   -> 9
    else              -> 1
}
