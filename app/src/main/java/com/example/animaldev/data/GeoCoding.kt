package com.example.animalDev.data


data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val geometry: Geometry
)

data class Geometry(
    val location: GeoCoding
)

data class GeoCoding(
    val lat: Double,
    val lng: Double
)
