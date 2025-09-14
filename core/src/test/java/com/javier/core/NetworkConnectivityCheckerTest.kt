package com.javier.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config

@Config(sdk = [33])
class NetworkConnectivityCheckerTest {

    private val context: Context = mock()

    private val connectivityManager: ConnectivityManager = mock()

    private val network: Network = mock()

    private val networkCapabilities: NetworkCapabilities = mock()

    private lateinit var underTest: NetworkConnectivityChecker

    @Before
    fun setUp() {
        whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        underTest = NetworkConnectivityChecker(context)
    }

    @Test
    fun `given wifi is connected then returns true`() {
        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(true)

        assertTrue(underTest.checkInternetConnection())
    }

    @Test
    fun `given cellular is connected then returns true`() {
        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)).thenReturn(false)
        whenever(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)).thenReturn(true)

        assertTrue(underTest.checkInternetConnection())
    }

    @Test
    fun `given no network is active then returns false`() {
        whenever(connectivityManager.activeNetwork).thenReturn(null)

        assertFalse(underTest.checkInternetConnection())
    }

    @Test
    fun `given networkCapabilities is null then returns false`() {
        whenever(connectivityManager.activeNetwork).thenReturn(network)
        whenever(connectivityManager.getNetworkCapabilities(network)).thenReturn(null)

        assertFalse(underTest.checkInternetConnection())
    }
}
