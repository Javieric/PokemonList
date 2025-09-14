package com.javier.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkConnectivityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun checkInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        connectivityManager?.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                return hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }
        return false
    }
}