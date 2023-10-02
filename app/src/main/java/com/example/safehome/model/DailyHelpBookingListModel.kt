package com.example.safehome.model

data class DailyHelpBookingListModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val paidDate: Any,
        val payType: Any,
        val paymentMode: Any,
        val rating: Any,
        val review: Any,
        val staffBookingId: Int,
        val staffId: Int,
        val staffName: String,
        val staffTypeId: Int,
        val staffTypeName: String
    )
}