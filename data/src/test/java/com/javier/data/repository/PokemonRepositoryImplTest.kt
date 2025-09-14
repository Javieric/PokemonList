package com.javier.data.repository

import com.javier.core.NetworkConnectivityChecker
import com.javier.data.mapper.toDomain
import com.javier.data.remote.PokemonListResponse
import com.javier.data.remote.PokemonRemoteDataSource
import com.javier.data.toMockPokemonResponse
import com.javier.data.toMockPokemonResponseList
import com.javier.domain.model.ErrorCause
import com.javier.domain.model.PokemonList
import com.javier.domain.model.ResponseResult
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class PokemonRepositoryImplTest {

    @Mock
    private lateinit var api: PokemonRemoteDataSource

    @Mock
    private lateinit var networkConnectivityChecker: NetworkConnectivityChecker

    private lateinit var underTest: PokemonRepositoryImpl

    @Before
    fun setUp() {
        underTest = PokemonRepositoryImpl(api, networkConnectivityChecker)
    }

    // getPokemonList tests
    @Test
    fun `given there is no internet connection when getPokemonList is called then returns NO_INTERNET`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(false)

        val result = underTest.getPokemonList(20, 0)

        assertEquals(ResponseResult.Error(ErrorCause.NO_INTERNET), result)
        verify(networkConnectivityChecker).checkInternetConnection()
        verifyNoInteractions(api)
    }

    @Test
    fun `given api returns success with body when getPokemonList is called then returns success`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        val pokemonListResponse = listOf(1).toMockPokemonResponseList()
        whenever(api.requestPokemonList(any(), any())).thenReturn(Response.success(pokemonListResponse))

        val result = underTest.getPokemonList(10, 0)

        assertEquals(ResponseResult.Success(pokemonListResponse.toDomain()), result)
    }

    @Test
    fun `given api returns null body when getPokemonList is called then reutns empty list`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        val response = Response.success<PokemonListResponse>(null)
        whenever(api.requestPokemonList(any(), any())).thenReturn(response)

        val result = underTest.getPokemonList(10, 0)

        assertEquals(ResponseResult.Success(PokemonList(emptyList())), result)
    }

    @Test
    fun `given api returns 404 whengetPokemonList is called then returns NOT_FOUND error cause`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Not found")
        whenever(api.requestPokemonList(any(), any())).thenReturn(Response.error(404, errorBody))

        val result = underTest.getPokemonList(10, 0)

        assertEquals(ResponseResult.Error(ErrorCause.NOT_FOUND), result)
    }

    @Test
    fun `given api returns error 500 when getPokemonList is called then returns SERVER error cause`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Server error")
        whenever(api.requestPokemonList(any(), any())).thenReturn(Response.error(500, errorBody))

        val result = underTest.getPokemonList(10, 0)

        assertEquals(ResponseResult.Error(ErrorCause.SERVER), result)
    }

    @Test
    fun `given api returns returns non-404 non-5xx error when getPokemonList is called then returns UNKNOWN error cause`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        val errorBody = ResponseBody.create("application/json".toMediaTypeOrNull(), "Bad request")
        whenever(api.requestPokemonList(any(), any())).thenReturn(Response.error(400, errorBody))

        val result = underTest.getPokemonList(10, 0)

        assertEquals(ResponseResult.Error(ErrorCause.UNKNOWN), result)
    }

    @Test
    fun `given api returns IOException when getPokemonList is called then returns NETWORK error cause`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonList(any(), any())).thenAnswer { throw IOException("network down") }

        val result = underTest.getPokemonList(10, 0)

        assert(result is ResponseResult.Error && result.cause == ErrorCause.NETWORK)
    }

    @Test
    fun `given api returns Exception when getPokemonList is called then returns UNKNOWN error cause`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonList(any(), any())).thenThrow(RuntimeException("Unexpected"))

        val result = underTest.getPokemonList(10, 0)

        assert(result is ResponseResult.Error && result.cause == ErrorCause.UNKNOWN)
    }

    // getPokemonById tests

    @Test
    fun `given there is no internet connection when getPokemonById is called then returns NO_INTERNET`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(false)

        val result = underTest.getPokemonById(1)

        assertEquals(ResponseResult.Error(ErrorCause.NO_INTERNET), result)
        verifyNoInteractions(api)
    }

    @Test
    fun `given api returns success with body when getPokemonById is called then returns success`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)

        val pokemon = 1.toMockPokemonResponse()

        whenever(api.requestPokemonById(any())).thenReturn(Response.success(pokemon))

        val result = underTest.getPokemonById(1)

        assertEquals(ResponseResult.Success(pokemon.toDomain()), result)
    }

    @Test
    fun `given api returns success with null body when getPokemonById is called then return UNKNOWN error cause`() = runTest {
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonById(any())).thenReturn(Response.success(null))

        val result = underTest.getPokemonById(1)

        assertEquals(ResponseResult.Error(ErrorCause.UNKNOWN), result)
    }

    @Test
    fun `given api returns 404 when getPokemonById is called then returns NOT_FOUND error cause`() = runTest {
        val pokemonId = 1
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonById(any())).thenReturn(
            Response.error(404, ResponseBody.create("application/json".toMediaTypeOrNull(), "Not found"))
        )

        val result = underTest.getPokemonById(pokemonId)

        assertEquals(ResponseResult.Error(ErrorCause.NOT_FOUND), result)
        verify(api).requestPokemonById(pokemonId)
    }

    @Test
    fun `given api returns 500 when getPokemonById is called then returns SERVER error cause`() = runTest {
        val pokemonId = 1
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonById(any())).thenReturn(
            Response.error(500, ResponseBody.create("application/json".toMediaTypeOrNull(), "Server error"))
        )

        val result = underTest.getPokemonById(pokemonId)

        assertEquals(ResponseResult.Error(ErrorCause.SERVER), result)
        verify(api).requestPokemonById(pokemonId)
    }

    @Test
    fun `given api returns IOException when getPokemonById is called then returns NETWORK error cause`() = runTest {
        val pokemonId = 1
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonById(any())).thenAnswer { throw IOException("network down") }

        val result = underTest.getPokemonById(pokemonId)

        assertEquals(ErrorCause.NETWORK, (result as ResponseResult.Error).cause)
        verify(api).requestPokemonById(pokemonId)
    }

    @Test
    fun `given api returns Exception when getPokemonById is called then returns UNKNOWN error cause`() = runTest {
        val pokemonId = 1
        whenever(networkConnectivityChecker.checkInternetConnection()).thenReturn(true)
        whenever(api.requestPokemonById(any())).thenThrow(RuntimeException("Unexpected"))

        val result = underTest.getPokemonById(pokemonId)

        assertEquals(ErrorCause.UNKNOWN, (result as ResponseResult.Error).cause)
        verify(api).requestPokemonById(pokemonId)
    }
}
