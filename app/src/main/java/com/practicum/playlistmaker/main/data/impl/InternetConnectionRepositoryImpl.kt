package com.practicum.playlistmaker.main.data.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.main.domain.api.InternetConnectListener
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class InternetConnectionRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val internetConnection: InternetConnection,
) : InternetConnectionRepository {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()
    private val internetConnectListeners: MutableSet<InternetConnectListener> = mutableSetOf()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            checkInternetConnection(network)
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    private fun checkInternetConnection(network: Network?, listener: InternetConnectListener? = null) {
        network?.also {
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = internetConnection.isInternetAvailable(it.socketFactory)
                if (hasInternet) {
                    validNetworks.add(it)
                } else {
                    validNetworks.remove(it)
                }
                checkValidNetworks(listener)
            }
        } ?: checkValidNetworks(listener)
    }

    private fun checkValidNetworks(listener: InternetConnectListener? = null) {
        val status = validNetworks.size > 0
        listener?.also { it.onConnectionStatusUpdate(status) }
            ?: internetConnectListeners.forEach { it.onConnectionStatusUpdate(status) }
    }

    override fun register() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun unregister() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    override fun addOnInternetConnectListener(listener: InternetConnectListener) {
        internetConnectListeners.add(listener)
        checkInternetConnection(cm.activeNetwork, listener)
    }

    override fun removeOnInternetConnectListener(listener: InternetConnectListener) {
        internetConnectListeners.remove(listener)
    }
}