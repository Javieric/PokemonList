package com.javier.pokemonlist

object Routes {
    const val LIST = "list"
    const val DETAILS = "details"

    fun detailsRoute(id: Int) = "$DETAILS/$id"
}