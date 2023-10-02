package com.example.safehome.model

import java.io.Serializable

data class ServiceDataList(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
):Serializable {
    data class Data(
        val availableOn: String,
        val companyName: String,
        val documentPath: String,
        val mobileNo: String,
        val personName: String,
        val serviceId: Int,
        val serviceMode: String,
        val serviceProvides: List<ServiceProvide>,
        val serviceworkingDetails: List<ServiceworkingDetail>
    ): Serializable {
        data class ServiceProvide(
            val serviceProvideId: Int,
            val serviceProvideName: String
        ):Serializable

        data class ServiceworkingDetail(
            val bookFor: String,
            val date: String,
            val rating: Any,
            val residentdetais: Residentdetais,
            val review: Any,
            val serviceBookingId: Int,
            val time: String
        ):Serializable {
            data class Residentdetais(
                val block: String,
                val blockId: Int,
                val flatId: Int,
                val flatNo: String,
                val residentId: Int
            ):Serializable
        }
    }
}