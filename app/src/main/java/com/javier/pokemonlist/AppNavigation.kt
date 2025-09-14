package com.javier.pokemonlist

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.javier.presentation.ui.PokemonDetailsScreen
import com.javier.presentation.ui.PokemonListScreen
import com.javier.presentation.viewmodel.MainViewModel
import com.javier.presentation.viewmodel.PokemonDetailsViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(Routes.LIST) { backstackEntry ->

            val viewModel: MainViewModel = hiltViewModel(backstackEntry)
            PokemonListScreen(
                state = viewModel.state,
                onRowClick = { id ->
                    navController.navigate(Routes.detailsRoute(id))
                },
                onRetry = { viewModel.retry() },
                onLoadMore = { viewModel.loadNextPage() }
            )
        }

        composable(
            route = "${Routes.DETAILS}/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backstackEntry ->
            val viewModel: PokemonDetailsViewModel = hiltViewModel(backstackEntry)

            PokemonDetailsScreen(
                state = viewModel.state,
                onRetry = { viewModel.retry() },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
