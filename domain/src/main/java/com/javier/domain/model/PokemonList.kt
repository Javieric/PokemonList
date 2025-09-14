package com.javier.domain.model

data class PokemonList(
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val id: Int,
)