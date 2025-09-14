package com.javier.domain

import com.javier.domain.model.Pokemon
import com.javier.domain.model.PokemonList
import com.javier.domain.model.PokemonListItem

fun Int.toMockPokemon(): Pokemon {
    return Pokemon(
        name = "Name_$this",
        id = 0,
        imageUrl = "umageUrl$this",
        height = 10
    )
}

fun Int.toMockPokemonListItem(): PokemonListItem {
    return PokemonListItem(
        name = "Name_$this",
        id = this
    )
}

fun List<Int>.toMockPokemonList(): PokemonList {
    return PokemonList(
        results = this.map { it.toMockPokemonListItem() }
    )
}