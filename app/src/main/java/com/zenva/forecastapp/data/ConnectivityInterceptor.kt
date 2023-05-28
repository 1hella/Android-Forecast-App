package com.zenva.forecastapp.data

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.zenva.forecastapp.ForecastApplication
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

// Similar to how an Interceptor can be created as a Kotlin's object (anonymous class),
// it can also be created as a real class.
class ConnectivityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Throwing an exception when not online (and then catching it elsewhere)
        // will prevent the app from crashing.
        if (!isOnline())
            throw NoConnectivityException()
        return chain.proceed(chain.request())
    }

    private fun isOnline(): Boolean {
        // ConnectivityManager is a system service which can be gotten from a context
        val connectivityManager = ForecastApplication.context
            .getSystemService<ConnectivityManager>()
        val network = connectivityManager?.activeNetwork
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(network)
        // Return whether or not the device is connected to the Internet
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    // Retrofit / OkHttp exceptions need to subclass IOException.
    // Otherwise, they won't be propagated.
    class NoConnectivityException: IOException() {
        override val message: String?
            get() = "No Internet connection"
    }
}