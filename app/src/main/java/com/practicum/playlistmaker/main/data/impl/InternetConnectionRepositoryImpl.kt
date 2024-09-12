package com.practicum.playlistmaker.main.data.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.common.utils.InternetAvailability
import com.practicum.playlistmaker.main.domain.api.InternetConnectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InternetConnectionRepositoryImpl(
    context: Context,
) : InternetConnectionRepository {

    private val _internetStatus = MutableLiveData<Boolean>()
    override val internetStatus: LiveData<Boolean> = _internetStatus
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            val networkCapabilities = cm.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            if (hasInternetCapability == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = InternetAvailability.check(network.socketFactory)
                    if (hasInternet) {
                        validNetworks.add(network)
                    } else {
                        validNetworks.remove(network)
                    }
                    checkValidNetworks()
                }
            }
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    private fun checkNetworkAtStart() {
        cm.activeNetwork?.let{
            val hasInternetCapability = cm.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            _internetStatus.postValue(hasInternetCapability == true)
        } ?: _internetStatus.postValue(false)
    }

    private fun checkValidNetworks() {
        _internetStatus.postValue(validNetworks.size > 0)
    }

    override fun register() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = createNetworkCallback()
        cm.registerNetworkCallback(networkRequest, networkCallback)
        checkNetworkAtStart()
    }

    override fun unregister() {
        cm.unregisterNetworkCallback(networkCallback)
    }
}