package com.example.mapsapplication.Objects.Network.LocationAPI

class LocationObject(
    val place_id: Int,
    val licence: String,
    val osm_type: String,
    val osm_id: String,
    val boundingbox: List<Double>,
    val lat: Double,
    val lon: Double,
    val display_name: String,
    val place_rank: Int?,
    val category: String?,
    val type: String?,
    val importance: Double?,
    val geojson: GeoJson?,
    val extratags: Extratags?,
    val address: Address

) {
    class Address(
        val amenity: String,
        val house_number: String?,
        val road: String,
        val city: String,
        val county: String,
        val state: String,
        val region: String,
        val postcode: String,
        val country: String,
        val country_code: String
    ) {
    }

    class GeoJson(
        val type: String?,
        val coordinates: List<List<List<Double>>>
    )

    class Extratags(
        val religion: String?,
        val wikidata: String?,
        val wikipedia: String?,
        val denomination: String?
    )
}