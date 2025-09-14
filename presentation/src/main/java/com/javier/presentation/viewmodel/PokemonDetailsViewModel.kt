package com.javier.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javier.domain.model.ErrorCause
import com.javier.domain.model.Pokemon
import com.javier.domain.model.ResponseResult
import com.javier.domain.usecase.GetPokemonByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPokemonByIdUseCase: GetPokemonByIdUseCase,
) : ViewModel() {

    private val id: Int = savedStateHandle["id"] ?: 0

    private val _state = MutableStateFlow<PokemonDetailsState>(PokemonDetailsState.Loading)
    val state: StateFlow<PokemonDetailsState> = _state.asStateFlow()

    init {
        getPokemonDetails()
    }

    private fun getPokemonDetails() {

        viewModelScope.launch {
            val pokemonDetailsResponse = getPokemonByIdUseCase(id)

            if (pokemonDetailsResponse is ResponseResult.Error) {
                when (pokemonDetailsResponse.cause) {
                    ErrorCause.NETWORK -> _state.update { PokemonDetailsState.NetworkError }
                    ErrorCause.SERVER -> _state.update { PokemonDetailsState.NetworkError }
                    ErrorCause.NOT_FOUND -> _state.update { PokemonDetailsState.NetworkError }
                    ErrorCause.NO_INTERNET -> _state.update { PokemonDetailsState.NoInternetError }
                    ErrorCause.UNKNOWN -> _state.update { PokemonDetailsState.NetworkError }
                }
            } else {
                val pokemonDetails = (pokemonDetailsResponse as ResponseResult.Success).data
                _state.update { PokemonDetailsState.Success(pokemonDetails = pokemonDetails) }
            }
        }
    }

    fun retry() {
        _state.update { PokemonDetailsState.Loading }
        getPokemonDetails()
    }

    sealed class PokemonDetailsState {
        data object Loading: PokemonDetailsState()
        data object NetworkError: PokemonDetailsState()
        data object NoInternetError: PokemonDetailsState()
        data class Success(
            val pokemonDetails: Pokemon,
        ) : PokemonDetailsState()
    }
}