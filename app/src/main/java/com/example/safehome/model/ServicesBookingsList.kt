package com.example.safehome.model

import java.io.Serializable

data class ServicesBookingsList(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
): Serializable {
    data class Data(
        val amount: Any,
        val paidDate: Any,
        val payType: Any,
        val paymentMode: Any,
        val paymentStatusId: Int,
        val personName: String,
        val rating: Any,
        val review: Any,
        val serviceBookingId: Int,
        val serviceId: Int,
        val serviceTypeId: Int,
        val serviceTypeName: String
    ): Serializable
}