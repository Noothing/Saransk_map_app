package com.example.mapsapplication.Objects

import org.osmdroid.util.GeoPoint

class Attraction(
    val id: Int,
    val image: String,
    val name: String,
    val description: String,
    val coordination: GeoPoint
)