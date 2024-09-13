package com.practicum.playlistmaker.main.data.network

import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory

class InternetConnection {

    fun isInternetAvailable(socketFactory: SocketFactory): Boolean {
        var socket: Socket? = null
        return try {
            socket = socketFactory.createSocket() ?: throw IOException()
            socket.connect(InetSocketAddress(HOST_NAME, PORT), CONNECTION_TIMEOUT)
            true
        } catch (e: IOException) {
            false
        } finally {
            socket?.close()
        }
    }

    companion object {
        private const val HOST_NAME = "8.8.8.8"
        private const val PORT = 53
        private const val CONNECTION_TIMEOUT = 1500
    }
}