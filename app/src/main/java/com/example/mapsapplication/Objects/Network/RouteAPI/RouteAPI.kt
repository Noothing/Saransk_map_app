package com.example.mapsapplication.Objects.Network.RouteAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RouteAPI {
    companion object {
        const val BASE_URL = "https://route.madskill.ru/"
    }

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun init() : RouteAPIClient {
        return retrofit.create(RouteAPIClient::class.java)
    }
}