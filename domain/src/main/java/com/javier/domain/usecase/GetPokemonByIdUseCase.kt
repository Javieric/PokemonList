package com.javier.domain.usecase

import com.javier.domain.model.Pokemon
import com.javier.domain.model.ResponseResult

interface GetPokemonByIdUseCase {
    suspend operator fun invoke(id: Int): ResponseResult<Pokemon>
}