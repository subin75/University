package com.example.university.network

import com.example.university.network.ApiService
import com.example.university.network.ServerApi

object ServerRepository {
    val serverApi: ServerApi = ApiService.create()
}