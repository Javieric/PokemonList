package com.javier.domain.usecase

import com.javier.domain.model.Pokemon
import com.javier.domain.model.ResponseResult
import com.javier.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GetPokemonByIdUseCaseImpl @Inject constructor(
    private val pokemonRepository: PokemonRepository
): GetPokemonByIdUseCase {

    override suspend operator fun invoke(id: Int): ResponseResult<Pokemon> = withContext(Dispatchers.IO) {
        pokemonRepository.getPokemonById(id)
    }
}