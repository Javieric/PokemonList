package com.javier.data.mapper

import com.javier.data.remote.PokemonResponse
import com.javier.domain.model.Pokemon

fun PokemonResponse.toDomain() = Pokemon(
    name = this.name,
    id = this.id,
    imageUrl = this.sprites.frontDefault.orEmpty(),
    height = this.height
)