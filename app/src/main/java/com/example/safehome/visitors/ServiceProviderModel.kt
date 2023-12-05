package com.example.safehome.visitors

data class ServiceProviderModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val id: Int,
        val name: String
    )
}