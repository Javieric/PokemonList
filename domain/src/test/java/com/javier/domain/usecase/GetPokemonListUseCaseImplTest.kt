package com.javier.domain.usecase

import com.javier.domain.model.ErrorCause
import com.javier.domain.model.ResponseResult
import com.javier.domain.repository.PokemonRepository
import com.javier.domain.toMockPokemonList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class GetPokemonListUseCaseImplTest {

    private val repository: PokemonRepository = mock()

    private val underTest = GetPokemonListUseCaseImpl(repository)

    @Test
    fun `when repository returns PokemonList then returns Success`() = runTest {
        val limit = 20
        val offset = 0
        val pokemonList = listOf(1, 2).toMockPokemonList()
        val expectedResult = ResponseResult.Success(pokemonList)

        whenever(repository.getPokemonList(any(), any())).thenReturn(expectedResult)

        val result = underTest(limit, offset)

        assertEquals(expectedResult, result)
        verify(repository).getPokemonList(limit, offset)
    }

    @Test
    fun `when repository returns Error then returns Error`() = runTest {
        val limit = 20
        val offset = 0
        val expectedError = ResponseResult.Error(cause = ErrorCause.NOT_FOUND)

        whenever(repository.getPokemonList(any(), any())).thenReturn(expectedError)

        val result = underTest(limit, offset)

        assertEquals(expectedError, result)
        verify(repository).getPokemonList(limit, offset)
    }
}