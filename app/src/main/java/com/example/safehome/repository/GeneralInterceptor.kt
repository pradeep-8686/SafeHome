package com.example.safehome.repository

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class GeneralInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        val response = chain.proceed(requestBuilder.build())

        // Throw specific Exception on HTTP 204 response code
        if (response.code == 204) {
            throw NoContentException()
        }

        return response // Carry on with the response
    }

    private class NoContentException : IOException() {

        override val message: String
            get() = "There is no content"
    }
}