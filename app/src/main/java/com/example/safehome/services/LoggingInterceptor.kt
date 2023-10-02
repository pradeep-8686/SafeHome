package com.example.safehome.services

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Log request details
        println("Request URL: ${request.url}")
        println("Request Method: ${request.method}")
        println("Request Headers: ${request.headers}")
        println("Request Body: ${request.body}")

        val response = chain.proceed(request)

        // Log response details
        println("Response Code: ${response.code}")
        println("Response Headers: ${response.headers}")
        println("Response Body: ${response.body?.string()}")

        return response
    }
}