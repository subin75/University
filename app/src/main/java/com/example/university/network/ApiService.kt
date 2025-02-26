package com.example.university.network

import com.example.university.BuildConfig
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Retrofit

object ApiService {
    fun create(): ServerApi {
        val tikXml = TikXml.Builder().exceptionOnUnreadXml(false).build()
        return Retrofit.Builder()
            .addConverterFactory(TikXmlConverterFactory.create(tikXml))
            .baseUrl(BuildConfig.HOST)
            .client(HttpClient().client)
            .build()
            .create(ServerApi::class.java)
    }
}