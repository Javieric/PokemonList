package com.javier.domain.usecase

import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult

interface GetPokemonListUseCase {
    suspend operator fun invoke(limit: Int, offset: Int): ResponseResult<PokemonList>
}