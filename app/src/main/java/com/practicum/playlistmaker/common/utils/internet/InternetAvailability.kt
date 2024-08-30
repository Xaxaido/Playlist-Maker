package com.practicum.playlistmaker.common.utils.internet

import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

object InternetAvailability {

    private const val HOST_NAME = "8.8.8.8"
    private const val PORT = 53
    private const val CONNECTION_TIMEOUT = 1500

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
}