package com.example.safehome.model

data class DailyHelpRoles(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val serviceTo: String,
        val staffCount: Int,
        val staffTypeId: Int,
        val staffTypeName: String
    )
}