package com.javier.data.remote

data class PokemonListResponse(
    val results: List<PokemonListItemResponse>
)

data class PokemonListItemResponse(
    val name: String,
    val url: String,
)