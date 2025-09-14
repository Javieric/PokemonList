package com.javier.data.repository

import com.javier.core.NetworkConnectivityChecker
import com.javier.data.mapper.toDomain
import com.javier.data.remote.PokemonRemoteDataSource
import com.javier.domain.model.ErrorCause
import com.javier.domain.model.Pokemon
import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult
import com.javier.domain.repository.PokemonRepository
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

internal class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonRemoteDataSource,
    private val networkConnectivityChecker: NetworkConnectivityChecker
) : PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): ResponseResult<PokemonList> =
        safeApiCall(
            request = { api.requestPokemonList(limit, offset) },
            mapBody = { it?.toDomain() ?: PokemonList(emptyList()) }
        )

    override suspend fun getPokemonById(id: Int): ResponseResult<Pokemon> =
        safeApiCall(
            request = { api.requestPokemonById(id) },
            mapBody = { it?.toDomain() }
        )

    private suspend fun <T, R> safeApiCall(
        request: suspend () -> Response<T>,
        mapBody: (T?) -> R?
    ): ResponseResult<R> {
        if (!networkConnectivityChecker.checkInternetConnection()) {
            return ResponseResult.Error(ErrorCause.NO_INTERNET)
        }

        return try {
            val response = request()
            if (response.isSuccessful) {
                val mapped = mapBody(response.body())
                if (mapped != null) {
                    ResponseResult.Success(mapped)
                } else {
                    ResponseResult.Error(ErrorCause.UNKNOWN)
                }
            } else {
                ResponseResult.Error(response.toErrorCause())
            }
        } catch (e: IOException) {
            ResponseResult.Error(ErrorCause.NETWORK, e)
        } catch (e: Exception) {
            ResponseResult.Error(ErrorCause.UNKNOWN, e)
        }
    }

    private fun <T> Response<T>.toErrorCause(): ErrorCause =
        when (code()) {
            404 -> ErrorCause.NOT_FOUND
            in 500..599 -> ErrorCause.SERVER
            else -> ErrorCause.UNKNOWN
        }
}