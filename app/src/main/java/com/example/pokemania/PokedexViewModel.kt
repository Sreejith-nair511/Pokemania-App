package com.example.pokemania

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class PokedexState {
    object Loading : PokedexState()
    data class Success(val list: List<Pokemon>) : PokedexState()
    data class Error(val message: String) : PokedexState()
}

class PokedexViewModel : ViewModel() {

    private val _state = MutableStateFlow<PokedexState>(PokedexState.Loading)
    val state: StateFlow<PokedexState> = _state

    // In-memory cache — survives rotation, cleared on process death
    private var cached: List<Pokemon>? = null

    init { loadAll() }

    fun loadAll() {
        cached?.let { _state.value = PokedexState.Success(it); return }
        _state.value = PokedexState.Loading

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    // 1. Fetch the full list of 1025 names/IDs in one call
                    val listResp = PokeApi.service.getPokemonList(limit = 1025, offset = 0)
                    val ids = listResp.results.map { res ->
                        res.url.trimEnd('/').substringAfterLast('/').toInt()
                    }

                    // 2. Process in sequential batches of 20 with a small delay between
                    //    batches to avoid PokéAPI rate-limiting (which caused the ~150 cap).
                    val allPokemon = mutableListOf<Pokemon>()
                    val batchSize  = 20

                    ids.chunked(batchSize).forEachIndexed { batchIndex, batch ->
                        // Small pause between batches — keeps us well under rate limits
                        if (batchIndex > 0) delay(200)

                        val batchResults = batch.map { id ->
                            async {
                                try {
                                    val detail = PokeApi.service.getPokemonDetail(id)
                                    val gen    = generationFromId(id)
                                    detail.toPokemon(description = "", generation = gen)
                                } catch (_: Exception) {
                                    null
                                }
                            }
                        }.awaitAll()

                        allPokemon.addAll(batchResults.filterNotNull())

                        // Emit partial results so the list populates progressively
                        if (allPokemon.isNotEmpty()) {
                            val snapshot = allPokemon.sortedBy { it.id }
                            _state.value = PokedexState.Success(snapshot)
                        }
                    }

                    allPokemon.sortedBy { it.id }
                }

                if (result.isEmpty()) throw Exception("Empty result")
                cached = result
                _state.value = PokedexState.Success(result)

            } catch (_: Exception) {
                // Offline fallback — show sample list so the app still works
                if (cached == null) {
                    cached = samplePokemonList
                    _state.value = PokedexState.Success(samplePokemonList)
                }
            }
        }
    }
}

/**
 * Derive generation from National Dex ID without an extra API call.
 */
fun generationFromId(id: Int): Int = when (id) {
    in 1..151    -> 1
    in 152..251  -> 2
    in 252..386  -> 3
    in 387..493  -> 4
    in 494..649  -> 5
    in 650..721  -> 6
    in 722..809  -> 7
    in 810..905  -> 8
    in 906..1025 -> 9
    else         -> 1
}
