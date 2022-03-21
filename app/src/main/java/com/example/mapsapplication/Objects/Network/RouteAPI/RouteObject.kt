package com.example.mapsapplication.Objects.Network.RouteAPI

class RouteObject(
    val code: String,
    val routes: List<Route>,
    val waypoints: List<Waypoint>
) {
    class Route(
        val geometry: Geometry,
        val legs: List<Legs>,
        val distance: Double,
        val duration: Double,
        val weight_name: String,
        val weight: Double
    ) {
        class Geometry (
            val coordinates: List<List<Double>>,
            val type: String
                )
    }

    class Legs (
        val steps: List<Any>,
        val distance: Double,
        val duration: Double,
        val summary: String,
        val weight: Double
            )

    class Waypoint (
        val hint: String,
        val distance: Double,
        val name: String,
        val location: List<Double>
            )

}