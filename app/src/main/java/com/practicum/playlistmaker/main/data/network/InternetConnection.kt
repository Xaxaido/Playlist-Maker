package com.practicum.playlistmaker.main.data.network

import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

class InternetConnection {

    fun check(socketFactory: SocketFactory): Boolean {
        return try {
            val socket = socketFactory.createSocket() ?: throw IOException()
            socket.connect(InetSocketAddress(HOST_NAME, PORT), CONNECTION_TIMEOUT)
            socket.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    companion object {
        private const val HOST_NAME = "8.8.8.8"
        private const val PORT = 53
        private const val CONNECTION_TIMEOUT = 1500
    }
}