package com.javier.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.javier.presentation.R
import com.javier.presentation.components.Error
import com.javier.presentation.components.Loading
import com.javier.presentation.components.PokemonScaffold
import com.javier.presentation.components.ShimmerBox
import com.javier.presentation.viewmodel.PokemonDetailsViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PokemonDetailsScreen(
    state: StateFlow<PokemonDetailsViewModel.PokemonDetailsState>,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    PokemonScaffold(
        title = stringResource(R.string.pokemon_details),
        onBackClick = onBackClick,
        modifier = modifier
    ) { innerPadding ->
        when (val uiState = state.collectAsStateWithLifecycle().value) {
            PokemonDetailsViewModel.PokemonDetailsState.Loading -> {
                Loading(modifier = Modifier.padding(innerPadding))
            }
            PokemonDetailsViewModel.PokemonDetailsState.NetworkError -> {
                Error(
                    message = stringResource(R.string.network_error_message),
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            PokemonDetailsViewModel.PokemonDetailsState.NoInternetError -> {
                Error(
                    message = stringResource(R.string.no_internet_error_message),
                    onRetry = onRetry,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is PokemonDetailsViewModel.PokemonDetailsState.Success -> {
                val pokemonDetails = uiState.pokemonDetails
                Success(
                    id = pokemonDetails.id,
                    name = pokemonDetails.name,
                    height = pokemonDetails.height,
                    imageUrl = pokemonDetails.imageUrl,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
fun Success(
    id: Int,
    name: String,
    height: Int,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .testTag("detailsImage")
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.large),
                loading = { ShimmerBox() },
                error = {
                    Image(
                        painter = painterResource(R.drawable.pokemon_not_found),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "#$id",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.height, height),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
