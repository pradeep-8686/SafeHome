package com.example.safehome.repository

import android.content.Context
import com.example.safehome.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor(private var mContext: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!Utils.isNetworkAvailable(mContext)){
            throw NoConnectivityException()
        }

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private class NoConnectivityException : IOException() {

        override val message: String
            get() = "Please check network connectivity."
    }
}