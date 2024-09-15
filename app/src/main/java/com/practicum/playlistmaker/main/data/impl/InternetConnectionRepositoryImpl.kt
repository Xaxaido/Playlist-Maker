package com.practicum.playlistmaker.main.data.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.main.domain.api.InternetConnectionCallback
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternetConnectionRepositoryImpl(
    context: Context,
    private val internetConnection: InternetConnection,
) : InternetConnectionRepository {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()
    private val connectionCallbacks: MutableSet<InternetConnectionCallback> = mutableSetOf()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            checkInternetConnection(network)
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    private fun checkInternetConnection(network: Network?) {
        network?.also {
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = internetConnection.isInternetAvailable(it.socketFactory)
                if (hasInternet) {
                    validNetworks.add(it)
                } else {
                    validNetworks.remove(it)
                }
                checkValidNetworks()
            }
        } ?: checkValidNetworks()
    }

    private fun checkValidNetworks() {
        val status = validNetworks.size > 0
        if (status) {
            connectionCallbacks.forEach { it.onConnected() }
        } else{
            connectionCallbacks.forEach { it.onDisconnected() }
        }
    }

    override fun register() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(networkRequest, networkCallback)
        checkInternetConnection(cm.activeNetwork)
    }

    override fun unregister() {
        cm.unregisterNetworkCallback(networkCallback)
    }

    override fun setCallback(callback: InternetConnectionCallback) {
        connectionCallbacks.add(callback)
    }
}