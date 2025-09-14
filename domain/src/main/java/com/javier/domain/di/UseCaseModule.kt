package com.javier.domain.di

import com.javier.domain.usecase.GetPokemonByIdUseCase
import com.javier.domain.usecase.GetPokemonByIdUseCaseImpl
import com.javier.domain.usecase.GetPokemonListUseCase
import com.javier.domain.usecase.GetPokemonListUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UseCaseModule {

    @Binds
    abstract fun bindGetPokemonListUseCase(
        impl: GetPokemonListUseCaseImpl
    ): GetPokemonListUseCase

    @Binds
    abstract fun bindGetPokemonByNameUseCase(
        impl: GetPokemonByIdUseCaseImpl
    ): GetPokemonByIdUseCase
}