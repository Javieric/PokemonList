package com.javier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javier.domain.model.ErrorCause
import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult
import com.javier.domain.usecase.GetPokemonListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPokemonListUseCase: GetPokemonListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<PokemonListState>(PokemonListState.Loading)
    val state: StateFlow<PokemonListState> = _state.asStateFlow()

    private var currentOffset = 0
    private val limit = 20
    private var isLoading = false
    private var endReached = false

    sealed class PokemonListState {
        data object Loading: PokemonListState()
        data object Empty: PokemonListState()
        data object NetworkError: PokemonListState()
        data object NoInternetError: PokemonListState()
        data class Success(
            val pokemonList: PokemonList,
            val isLoadingMore: Boolean = false,
        ) : PokemonListState()
    }

    init {
        loadNextPage()
    }

    fun loadNextPage() {

        if (isLoading || endReached) return
        isLoading = true

        _state.update { previous ->
            if (previous is PokemonListState.Success) {
                previous.copy(isLoadingMore = true)
            } else {
                previous
            }
        }

        viewModelScope.launch {
            val pokemonListResult = getPokemonListUseCase(limit, currentOffset)

            isLoading = false

            if (pokemonListResult is ResponseResult.Error) {

                when (pokemonListResult.cause) {
                    ErrorCause.NETWORK -> _state.update { PokemonListState.NetworkError }
                    ErrorCause.SERVER -> _state.update { PokemonListState.NetworkError }
                    ErrorCause.NOT_FOUND -> _state.update { PokemonListState.NetworkError }
                    ErrorCause.NO_INTERNET -> _state.update { PokemonListState.NoInternetError }
                    ErrorCause.UNKNOWN -> _state.update { PokemonListState.NetworkError }
                }
            } else {
                val pokemonList = (pokemonListResult as ResponseResult.Success).data

                if (pokemonList.results.isEmpty()) {
                    endReached = true
                    if (currentOffset == 0) {
                        _state.update { PokemonListState.Empty }
                    } else {
                        _state.update { previous ->
                            if (previous is PokemonListState.Success) previous.copy(isLoadingMore = false) else previous
                        }
                    }
                } else {
                    currentOffset += limit
                    _state.update { previous ->
                        val currentResults = (previous as? PokemonListState.Success)?.pokemonList?.results ?: emptyList()
                        PokemonListState.Success(
                            pokemonList = PokemonList(results = currentResults + pokemonList.results),
                            isLoadingMore = false
                        )
                    }
                }
            }
        }
    }

    fun retry() {
        _state.update { PokemonListState.Loading }
        currentOffset = 0
        loadNextPage()
    }
}