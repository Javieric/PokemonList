package com.javier.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.javier.domain.model.PokemonListItem
import com.javier.presentation.R
import com.javier.presentation.components.Empty
import com.javier.presentation.components.Error
import com.javier.presentation.components.Loading
import com.javier.presentation.components.PokemonScaffold
import com.javier.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PokemonListScreen(
    state: StateFlow<MainViewModel.PokemonListState>,
    onRowClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by state.collectAsStateWithLifecycle()

    PokemonScaffold(
        title = stringResource(R.string.app_name),
        modifier = modifier
    ) { innerPadding ->
        when (uiState) {
            is MainViewModel.PokemonListState.Success -> PokemonListComposable(
                results = (uiState as MainViewModel.PokemonListState.Success).pokemonList.results,
                onRowClick = onRowClick,
                onLoadMore = onLoadMore,
                isLoadingMore = (uiState as MainViewModel.PokemonListState.Success).isLoadingMore,
                modifier = Modifier.padding(innerPadding)
            )

            MainViewModel.PokemonListState.NoInternetError -> Error(
                message = stringResource(R.string.no_internet_error_message),
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            MainViewModel.PokemonListState.NetworkError -> Error(
                message = stringResource(R.string.network_error_message),
                onRetry = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            MainViewModel.PokemonListState.Loading -> Loading(modifier = Modifier.padding(innerPadding))
            MainViewModel.PokemonListState.Empty -> Empty(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun PokemonListComposable(
    results: List<PokemonListItem>,
    isLoadingMore: Boolean,
    modifier: Modifier = Modifier,
    onRowClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        items(results) { item ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = { onRowClick(item.id) }
                    ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${item.id}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = item.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (isLoadingMore) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.testTag("loadingMoreCircularProgressIndicator"),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(listState, results) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItem ->
                if (lastVisibleItem != null && lastVisibleItem >= results.size - 5) {
                    onLoadMore()
                }
            }
    }
}
