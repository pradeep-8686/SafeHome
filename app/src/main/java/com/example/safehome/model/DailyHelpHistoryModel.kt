package com.example.safehome.model

data class DailyHelpHistoryModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val paidDate: String,
        val payType: String,
        val paymentMode: String,
        val rating: String,
        val review: String,
        val staffBookingId: Int,
        val staffId: Int,
        val staffName: String,
        val staffTypeId: Int,
        val staffTypeName: String
    )
}