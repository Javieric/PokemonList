package com.javier.data.mapper

import com.javier.data.remote.PokemonListItemResponse
import com.javier.domain.model.PokemonListItem

fun PokemonListItemResponse.toDomain(): PokemonListItem = PokemonListItem(
    name = this.name,
    id = this.url.removeSuffix("/").substringAfterLast("/").toInt()
)