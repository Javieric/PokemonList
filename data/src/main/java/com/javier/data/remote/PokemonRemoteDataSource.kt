package com.javier.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonRemoteDataSource {

    @GET("pokemon")
    suspend fun requestPokemonList(@Query("limit") limit: Int, @Query("offset") offset: Int): Response<PokemonListResponse>

    @GET("pokemon/{id}")
    suspend fun requestPokemonById(@Path("id")id: Int): Response<PokemonResponse>
}