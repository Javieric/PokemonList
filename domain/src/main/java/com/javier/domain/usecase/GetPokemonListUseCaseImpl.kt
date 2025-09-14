package com.javier.domain.usecase

import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult
import com.javier.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class GetPokemonListUseCaseImpl @Inject constructor(
    private val pokemonRepository: PokemonRepository
): GetPokemonListUseCase {

    override suspend operator fun invoke(limit: Int, offset: Int): ResponseResult<PokemonList> = withContext(Dispatchers.IO) {
        Timber.d("GetPokemonListUseCaseImpl invoked with limit: $limit, offset: $offset")
        val list = pokemonRepository.getPokemonList(limit, offset)
        Timber.d("GetPokemonListUseCaseImpl result: $list")
        list
    }
}