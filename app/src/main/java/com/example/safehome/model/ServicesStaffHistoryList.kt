package com.example.safehome.model

data class ServicesStaffHistoryList(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        var paidDate: String,
        val payType: String,
        val paymentMode: String,
        val rating: Any,
        val review: Any,
        val staffBookingId: Int,
        val staffId: Int,
        val staffName: String,
        val staffTypeId: Int,
        val amount: String,
        val staffTypeName: String
    )
}