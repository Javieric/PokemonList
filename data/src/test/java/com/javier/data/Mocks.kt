package com.javier.data

import com.javier.data.remote.PokemonListItemResponse
import com.javier.data.remote.PokemonListResponse
import com.javier.data.remote.PokemonResponse
import com.javier.data.remote.Sprites

fun Int.toMockPokemonResponse(): PokemonResponse {
    return PokemonResponse(
        id = this,
        name = "Name_$this",
        sprites = Sprites(
            frontDefault = "imageUrl$this"
        ),
        height = 10,
    )
}

fun List<Int>.toMockPokemonResponseList(): PokemonListResponse {
    return PokemonListResponse(results = this.map { it.toMockPokemonListItemResponse() })
}

fun Int.toMockPokemonListItemResponse(): PokemonListItemResponse {
    return PokemonListItemResponse(name = "Name_$this", url = "url/$this")
}
