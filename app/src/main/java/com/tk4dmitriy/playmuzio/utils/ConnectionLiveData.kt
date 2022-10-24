package com.tk4dmitriy.playmuzio.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.*
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionLiveData(context: Context) : LiveData<Boolean>() {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    init {
        isNetworkAvailable()
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "onAvailable")
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = DoesNetworkHaveInternet.execute()
                if (hasInternet) {
                    withContext(Dispatchers.Main){
                        Log.d(TAG, "onAvailable: adding network. $network")
                        validNetworks.add(network)
                        checkValidNetworks()
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: $network")
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    private fun isNetworkAvailable() {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)

        if (hasInternetCapability == true) {
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = DoesNetworkHaveInternet.execute()
                if (!hasInternet) postValue(false)
            }
        } else {
            postValue(false)
        }
    }
}