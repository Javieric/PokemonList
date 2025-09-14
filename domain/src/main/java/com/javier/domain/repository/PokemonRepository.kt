package com.javier.domain.repository

import com.javier.domain.model.Pokemon
import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult

interface PokemonRepository {

    suspend fun getPokemonList(limit: Int, offset: Int): ResponseResult<PokemonList>

    suspend fun getPokemonById(id: Int): ResponseResult<Pokemon>
}