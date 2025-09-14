package com.javier.presentation.viewmodel

import app.cash.turbine.test
import com.javier.domain.model.ErrorCause
import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult
import com.javier.domain.toMockPokemonList
import com.javier.domain.usecase.GetPokemonListUseCase
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
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var getPokemonListUseCase: GetPokemonListUseCase
    private lateinit var underTest: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        getPokemonListUseCase = mock()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is created then initial state is Loading`() = runTest {
        whenever(getPokemonListUseCase(any(), any())).thenReturn(ResponseResult.Success(PokemonList(emptyList())))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)
        }
    }

    @Test
    fun `when getPokemonListUseCase returns empty list on first page then state is Empty`() = runTest {
        whenever(getPokemonListUseCase(any(), any())).thenReturn(ResponseResult.Success(PokemonList(emptyList())))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)

            val emptyState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Empty, emptyState)
        }
    }

    @Test
    fun `when getPokemonListUseCase returns list then state is Success`() = runTest {
        val list = listOf(1).toMockPokemonList()
        whenever(getPokemonListUseCase(any(), any())).thenReturn(ResponseResult.Success(list))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)

            val successState = awaitItem()
            assertEquals(list, (successState as MainViewModel.PokemonListState.Success).pokemonList)
        }
    }

    @Test
    fun `when getPokemonListUseCase returns NO_INTERNET then state is NoInternetError`() = runTest {
        whenever(getPokemonListUseCase(any(), any())).thenReturn(ResponseResult.Error(ErrorCause.NO_INTERNET))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)

            val errorState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.NoInternetError, errorState)
        }
    }

    @Test
    fun `when getPokemonListUseCase returns NETWORK error then state is NetworkError`() = runTest {
        whenever(getPokemonListUseCase(any(), any())).thenReturn(ResponseResult.Error(ErrorCause.NETWORK))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)

            val errorState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.NetworkError, errorState)
        }
    }

    @Test
    fun `when second page is fetched then appends results correctly`() = runTest {
        val firstPage = listOf(1).toMockPokemonList()
        val secondPage = listOf(2).toMockPokemonList()

        whenever(getPokemonListUseCase(20, 0)).thenReturn(ResponseResult.Success(firstPage))
        whenever(getPokemonListUseCase(20, 20)).thenReturn(ResponseResult.Success(secondPage))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)

            val firstState = awaitItem() as MainViewModel.PokemonListState.Success
            assertEquals(1, firstState.pokemonList.results.size)
            assert(!firstState.isLoadingMore)

            underTest.loadNextPage()
            testScheduler.advanceUntilIdle()

            val loadingSecondPageState = awaitItem() as MainViewModel.PokemonListState.Success
            assertEquals(1, loadingSecondPageState.pokemonList.results.size)
            assert(loadingSecondPageState.isLoadingMore)

            val secondState = awaitItem() as MainViewModel.PokemonListState.Success
            assertEquals(2, secondState.pokemonList.results.size)
            assert(!secondState.isLoadingMore)
        }
    }

    @Test
    fun `when retry is called then sets Loading state`() = runTest {
        whenever(getPokemonListUseCase(any(), any())).thenReturn(ResponseResult.Error(ErrorCause.NETWORK))

        underTest = MainViewModel(getPokemonListUseCase)

        underTest.state.test {
            val loadingState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingState)

            val errorState = awaitItem()
            assertEquals(MainViewModel.PokemonListState.NetworkError, errorState)

            underTest.retry()

            val loadingStateAfterRetry = awaitItem()
            assertEquals(MainViewModel.PokemonListState.Loading, loadingStateAfterRetry)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
