package com.example.safehome.visitors

data class ScheduleModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val name: String,
        val repeatId: Int
    )
}