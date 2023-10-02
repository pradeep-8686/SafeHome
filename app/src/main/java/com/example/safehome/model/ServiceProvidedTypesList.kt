package com.example.safehome.model

data class ServiceProvidedTypesList(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val serviceProvideId: Int,
        val serviceProvideName: String,
        val serviceTypeId: Int
    )
}