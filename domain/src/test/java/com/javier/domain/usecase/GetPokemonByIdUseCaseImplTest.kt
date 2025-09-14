package com.javier.domain.usecase

import com.javier.domain.model.ErrorCause
import com.javier.domain.model.ResponseResult
import com.javier.domain.repository.PokemonRepository
import com.javier.domain.toMockPokemon
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetPokemonByIdUseCaseImplTest {

    private val repository: PokemonRepository = mock()

    private val underTest = GetPokemonByIdUseCaseImpl(repository)

    @Test
    fun `when repository returns pokemon then returns success`() = runTest {
        val pokemonId = 1
        val expectedPokemon = pokemonId.toMockPokemon()
        val expectedResult = ResponseResult.Success(expectedPokemon)

        whenever(repository.getPokemonById(pokemonId)).thenReturn(expectedResult)

        val result = underTest(pokemonId)

        assertEquals(expectedResult, result)
        verify(repository).getPokemonById(pokemonId)
    }

    @Test
    fun `when repository returns error then returns error`() = runTest {
        val pokemonId = 1
        val expectedError = ResponseResult.Error(cause = ErrorCause.NO_INTERNET)

        whenever(repository.getPokemonById(pokemonId)).thenReturn(expectedError)

        val result = underTest(pokemonId)

        assertEquals(expectedError, result)
        verify(repository).getPokemonById(pokemonId)
    }
}
