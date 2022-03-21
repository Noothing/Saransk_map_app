package com.example.mapsapplication.Objects.Network.LocationAPI

import retrofit2.Call
import retrofit2.http.*

interface LocationAPIClient {

    @GET("search")
    fun searchLocationByQuery(
        @Query("q") query: String,
        @Query("extratags") extratags: Int = 1,
        @Query("polygon_geojson") polygon_geojson: Int = 1
    ) : Call<List<LocationObject>>

    @GET("reverse")
    fun searchLocationByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        @Query("extratags") extratags: Int = 1,
        @Query("polygon_geojson") polygon_geojson: Int = 1,
        @Query("accept-language") accept_language: String = "ru"
    ) : Call<LocationObject>
}