package com.javier.data.remote

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val height: Int
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?,
)