package com.example.safehome.visitors.staff

data class DeleteVisitorDetailsModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val visitorId: Int
    )
}