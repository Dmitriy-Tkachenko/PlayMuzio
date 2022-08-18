package com.tk4dmitriy.playmuzio.utils

import android.util.Log
import com.tk4dmitriy.playmuzio.utils.TAG
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object DoesNetworkHaveInternet {
    fun execute(): Boolean {
        return try {
            Log.d(TAG, "PINGING google")
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            true
        } catch (e: IOException) {
            Log.e(TAG, "No internet connection. $e")
            false
        }
    }
}