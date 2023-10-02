package com.example.safehome.model

data class GetAllHistoryServiceTypes(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val serviceTypeId: Int,
        val serviceTypeName: String
    )
}