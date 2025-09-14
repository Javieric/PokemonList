package com.javier.data.mapper

import com.javier.data.remote.PokemonListResponse
import com.javier.domain.model.PokemonList

fun PokemonListResponse.toDomain(): PokemonList = PokemonList(
    results = this.results.map { it.toDomain() }
)