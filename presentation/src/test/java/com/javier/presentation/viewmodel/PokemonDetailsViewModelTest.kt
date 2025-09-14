package com.javier.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.javier.domain.model.ErrorCause
import com.javier.domain.model.ResponseResult
import com.javier.domain.toMockPokemon
import com.javier.domain.usecase.GetPokemonByIdUseCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailsViewModelTest {

    private lateinit var getPokemonByIdUseCase: GetPokemonByIdUseCase

    private lateinit var underTest: PokemonDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        getPokemonByIdUseCase = mock()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is created then initial state is Loading`() = runTest {
        val pokemon = 1.toMockPokemon()
        whenever(getPokemonByIdUseCase(1)).thenReturn(ResponseResult.Success(pokemon))

        underTest = PokemonDetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to 1)),
            getPokemonByIdUseCase = getPokemonByIdUseCase
        )

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(PokemonDetailsViewModel.PokemonDetailsState.Loading, loadingState)
        }
    }

    @Test
    fun `when getPokemonByIdUseCase returns success result then state is Success`() = runTest {
        val pokemon = 1.toMockPokemon()
        whenever(getPokemonByIdUseCase(1)).thenReturn(ResponseResult.Success(pokemon))

        underTest = PokemonDetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to 1)),
            getPokemonByIdUseCase = getPokemonByIdUseCase
        )

        underTest.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PokemonDetailsViewModel.PokemonDetailsState.Loading)

            val successState = awaitItem()
            assertEquals(PokemonDetailsViewModel.PokemonDetailsState.Success(pokemon), successState)
        }
    }

    @Test
    fun `when getPokemonByIdUseCase returns NO_INTERNET error then state is NoInternetError`() = runTest {
        whenever(getPokemonByIdUseCase(any())).thenReturn(ResponseResult.Error(ErrorCause.NO_INTERNET))

        underTest = PokemonDetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to 1)),
            getPokemonByIdUseCase = getPokemonByIdUseCase
        )
        underTest.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PokemonDetailsViewModel.PokemonDetailsState.Loading)

            val state = awaitItem()
            assertTrue(state is PokemonDetailsViewModel.PokemonDetailsState.NoInternetError)
        }
    }

    @Test
    fun `when getPokemonByIdUseCase returns NETWORK then state is NetworkError`() = runTest {
        whenever(getPokemonByIdUseCase(any())).thenReturn(ResponseResult.Error(ErrorCause.NETWORK))

        underTest = PokemonDetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to 1)),
            getPokemonByIdUseCase = getPokemonByIdUseCase
        )

        underTest.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PokemonDetailsViewModel.PokemonDetailsState.Loading)

            val state = awaitItem()
            assertTrue(state is PokemonDetailsViewModel.PokemonDetailsState.NetworkError)
        }
    }


    @Test
    fun `when retry is called then sets Loading state and calls getPokemonByIdUseCase again`() = runTest {
        whenever(getPokemonByIdUseCase(any())).thenReturn(ResponseResult.Error(ErrorCause.NETWORK))

        underTest = PokemonDetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to 1)),
            getPokemonByIdUseCase = getPokemonByIdUseCase
        )

        underTest.retry()

        underTest.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState is PokemonDetailsViewModel.PokemonDetailsState.Loading)
            val errorState = awaitItem()
            assertTrue(errorState is PokemonDetailsViewModel.PokemonDetailsState.NetworkError)

            verify(getPokemonByIdUseCase, times(2))(1)
        }
    }
}
