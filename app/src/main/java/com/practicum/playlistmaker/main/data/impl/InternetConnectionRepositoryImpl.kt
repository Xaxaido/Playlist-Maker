package com.practicum.playlistmaker.main.data.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.main.data.network.InternetConnection
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternetConnectionRepositoryImpl(
    context: Context,
    private val internetConnection: InternetConnection,
) : InternetConnectionRepository {

    private val _internetStatus = MutableLiveData<Boolean>()
    override val internetStatus: LiveData<Boolean> = _internetStatus
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            if (isNetworkCapable()) checkInternetConnection(network)
            else checkValidNetworks()
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    private fun isNetworkCapable() = cm.activeNetwork?.let {
        cm.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    } ?: false

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
        _internetStatus.postValue(validNetworks.size > 0)
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
}