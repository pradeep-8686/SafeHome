package com.example.safehome.model

data class YearModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val year: Int
    )
}