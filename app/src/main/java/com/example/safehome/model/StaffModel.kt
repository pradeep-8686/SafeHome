package com.example.safehome.model

data class StaffModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val staffTypeId: Int,
        val staffTypeName: String
    )
}