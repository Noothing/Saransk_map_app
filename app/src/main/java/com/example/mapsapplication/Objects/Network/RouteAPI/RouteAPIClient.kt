package com.example.mapsapplication.Objects.Network.RouteAPI

import retrofit2.Call
import retrofit2.http.*

interface RouteAPIClient {
    @Headers(
        "accept: application/json",
        "connection: keep-alive"
    )
    @GET("/route/v1/{profile}/{startPoint};{endPoint}?steps=false&geometries=geojson&overview=full")
    fun getRoute(
        @Path(value = "profile", encoded=true) profile: String,
        @Path(value = "startPoint", encoded=true) startPoint: String,
        @Path(value = "endPoint", encoded=true) endPoint: String,
    ) : Call<String>
}