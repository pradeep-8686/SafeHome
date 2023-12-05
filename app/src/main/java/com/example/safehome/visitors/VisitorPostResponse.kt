package com.example.safehome.visitors

data class VisitorPostResponse(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val inTime: String,
        val otp: Int,
        val startDate: String,
        val visitorId: Int
    )
}