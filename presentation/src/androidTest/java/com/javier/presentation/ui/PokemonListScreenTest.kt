package com.javier.presentation.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.javier.domain.toMockPokemonList
import com.javier.presentation.R
import com.javier.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeState = MutableStateFlow<MainViewModel.PokemonListState>(MainViewModel.PokemonListState.Loading)

    @Test
    fun whenStateIsLoadingTheItDisplaysCircularProgressIndicator() {
        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { },
                onLoadMore = { }
            )
        }

        composeTestRule.onNodeWithTag("circularProgressIndicator").assertIsDisplayed()
    }

    @Test
    fun whenStateIsEmptyTheItDisplaysEmptyScreen() {
        fakeState.value = MainViewModel.PokemonListState.Empty

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { },
                onLoadMore = { }
            )
        }

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val emptyListMessage = context.resources.getString(R.string.empty_list)

        composeTestRule.onNodeWithText(emptyListMessage).assertIsDisplayed()
    }

    @Test
    fun whenStateIsNetworkErrorTheItDisplaysNetworkError() {
        fakeState.value = MainViewModel.PokemonListState.NetworkError

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { },
                onLoadMore = { }
            )
        }

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorMessage = context.resources.getString(R.string.network_error_message)

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retryButton").assertIsDisplayed()
    }

    @Test
    fun whenStateIsNoInternetErrorThenItDisplaysNoInternetError() {
        fakeState.value = MainViewModel.PokemonListState.NoInternetError

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { },
                onLoadMore = { }
            )
        }

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorMessage = context.resources.getString(R.string.no_internet_error_message)

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retryButton").assertIsDisplayed()
    }

    @Test
    fun whenRetryButtonIsClickedThenOnRetryIsCalled() {
        fakeState.value = MainViewModel.PokemonListState.NoInternetError

        var retryClicked = false

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { retryClicked = true },
                onLoadMore = { }
            )
        }
        composeTestRule.onNodeWithTag("retryButton").performClick()
        assert(retryClicked)
    }

    @Test
    fun whenStateIsSuccessTheItDisplaysData() {
        val pokemonList = listOf(1, 2).toMockPokemonList()
        fakeState.value = MainViewModel.PokemonListState.Success(
            pokemonList = pokemonList,
            isLoadingMore = false
        )

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { },
                onLoadMore = { }
            )
        }

        composeTestRule.onNodeWithText("#${pokemonList.results[0].id}").assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemonList.results[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText("#${pokemonList.results[1].id}").assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemonList.results[1].name).assertIsDisplayed()
    }

    @Test
    fun whenARowIsClickedThenonRowClickIsCalled() {
        var clickedId: Int? = null
        val pokemonList = listOf(1).toMockPokemonList()
        fakeState.value = MainViewModel.PokemonListState.Success(
            pokemonList = pokemonList,
            isLoadingMore = false
        )

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { clickedId = it },
                onRetry = { },
                onLoadMore = { }
            )
        }

        composeTestRule.onNodeWithText(pokemonList.results[0].name).performClick()
        assert(clickedId == pokemonList.results[0].id)
    }

    @Test
    fun whenIsLoadingMoreTrueThenItDisplaysloadingMoreCircularProgressIndicator() {
        val pokemonList = listOf(1).toMockPokemonList()
        fakeState.value = MainViewModel.PokemonListState.Success(
            pokemonList = pokemonList,
            isLoadingMore = true
        )

        composeTestRule.setContent {
            PokemonListScreen(
                state = fakeState,
                onRowClick = { },
                onRetry = { },
                onLoadMore = { }
            )
        }

        composeTestRule.onNodeWithTag("loadingMoreCircularProgressIndicator").assertExists()
    }
}
