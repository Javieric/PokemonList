package com.javier.presentation.ui

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.javier.domain.toMockPokemon
import com.javier.presentation.R
import com.javier.presentation.viewmodel.PokemonDetailsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonDetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeState = MutableStateFlow<PokemonDetailsViewModel.PokemonDetailsState>(
        PokemonDetailsViewModel.PokemonDetailsState.Loading
    )

    @Test
    fun whenStateIsLoadingThenItDisplaysCircularProgressIndicator() {
        composeTestRule.setContent {
            PokemonDetailsScreen(
                state = fakeState,
                onRetry = { },
                onBackClick = { }
            )
        }

        composeTestRule.onNodeWithTag("circularProgressIndicator").assertIsDisplayed()
    }

    @Test
    fun whenStateIsNetworkErrorThenItDisplaysNetworkError() {
        fakeState.value = PokemonDetailsViewModel.PokemonDetailsState.NetworkError

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorMessage = context.resources.getString(R.string.network_error_message)

        composeTestRule.setContent {
            PokemonDetailsScreen(
                state = fakeState,
                onRetry = { },
                onBackClick = { }
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retryButton").assertIsDisplayed()
    }

    @Test
    fun whenRetryIsClickedThenOnRetryIsCalled() {
        var onRetryCalled = false
        fakeState.value = PokemonDetailsViewModel.PokemonDetailsState.NetworkError

        composeTestRule.setContent {
            PokemonDetailsScreen(
                state = fakeState,
                onRetry = { onRetryCalled = true },
                onBackClick = { }
            )
        }

        composeTestRule.onNodeWithTag("retryButton").performClick()
        assert(onRetryCalled)
    }

    @Test
    fun whenStateIsNoInternetErrorThenItDisplaysNoInternetError() {
        fakeState.value = PokemonDetailsViewModel.PokemonDetailsState.NoInternetError

        composeTestRule.setContent {
            PokemonDetailsScreen(
                state = fakeState,
                onRetry = { },
                onBackClick = { }
            )
        }

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorMessage = context.resources.getString(R.string.no_internet_error_message)

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithTag("retryButton").assertIsDisplayed()
    }

    @Test
    fun whenStateIsSuccessThenItDisplaysData() {
        val pokemon = 1.toMockPokemon()
        fakeState.value = PokemonDetailsViewModel.PokemonDetailsState.Success(
            pokemonDetails = pokemon
        )

        composeTestRule.setContent {
            PokemonDetailsScreen(
                state = fakeState,
                onRetry = { },
                onBackClick = { }
            )
        }

        composeTestRule.onNodeWithTag("detailsImage").assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemon.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("#${pokemon.id}").assertIsDisplayed()

        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val height = context.resources.getString(R.string.height, pokemon.height)
        composeTestRule.onNodeWithText(height).assertIsDisplayed()
    }
}