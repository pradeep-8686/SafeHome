package com.example.safehome.alert

data class AlertDeleteResponse(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val alertId: Int
    )
}