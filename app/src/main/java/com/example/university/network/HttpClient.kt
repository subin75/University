package com.example.university.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class HttpClient {
    var client: OkHttpClient? = null

    constructor() {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.HEADERS
        client = OkHttpClient.Builder().addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Connection", "close")
                .addHeader("Content-Type", "application/xml;charset=UTF-8").build()
            chain.proceed(request)
        })
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .build()
    }
}