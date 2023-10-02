package com.example.safehome.model

data class CommunityCities(
    val `data`: List<CitiesData> ,
    val message: String,
    val statusCode: Int
)