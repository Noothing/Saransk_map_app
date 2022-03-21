package com.example.mapsapplication.Objects.Network.LocationAPI

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class LocationAPI {
    companion object {
        const val BASE_URL = "https://geo.madskill.ru/"
    }

    private val retrofit = Retrofit
        .Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    fun init() : LocationAPIClient {
        return retrofit.create(LocationAPIClient::class.java)
    }
}