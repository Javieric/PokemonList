package com.javier.pokemonlist

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.javier.domain.toMockPokemon
import com.javier.domain.toMockPokemonList
import com.javier.presentation.ui.PokemonDetailsScreen
import com.javier.presentation.ui.PokemonListScreen
import com.javier.presentation.viewmodel.MainViewModel
import com.javier.presentation.viewmodel.PokemonDetailsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigateFromListToDetails() {

        val pokemonList = listOf(1, 2).toMockPokemonList()
        val pokemon = 1.toMockPokemon()
        val fakeListState = MutableStateFlow(
            MainViewModel.PokemonListState.Success(
                pokemonList = pokemonList,
                isLoadingMore = false
            )
        )
        val fakeDetailsState = MutableStateFlow<PokemonDetailsViewModel.PokemonDetailsState>(
            PokemonDetailsViewModel.PokemonDetailsState.Success(pokemon)
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Routes.LIST) {
                composable(Routes.LIST) {
                    PokemonListScreen(
                        state = fakeListState,
                        onRowClick = { id -> navController.navigate(Routes.detailsRoute(id)) },
                        onRetry = { },
                        onLoadMore = { },
                        modifier = Modifier.testTag("listScreen")
                    )
                }
                composable(
                    route = "${Routes.DETAILS}/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) {
                    PokemonDetailsScreen(
                        state = fakeDetailsState,
                        onRetry = { },
                        onBackClick = { navController.popBackStack() },
                        modifier = Modifier.testTag("detailsScreen")
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("listScreen").assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemonList.results[0].name).assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemonList.results[0].name).performClick()
        composeTestRule.onNodeWithTag("detailsScreen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Height: ${pokemon.height}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("backButton").performClick()
        composeTestRule.onNodeWithTag("listScreen").assertIsDisplayed()
        composeTestRule.onNodeWithText(pokemonList.results[0].name).assertIsDisplayed()
    }
}
