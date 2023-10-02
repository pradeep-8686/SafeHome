package com.example.safehome.model

import java.io.Serializable

data class DailyHelpStaffModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) : Serializable {
    data class Data(
        val address: Any,
        val availableOn: String,
        val companyName: Any,
        val documentPath: Any,
        val mobileNo: String,
        val serviceTo: String,
        val staffAvilableTimes: List<StaffAvilableTime>,
        val staffId: Int,
        val staffMode: String,
        val staffName: String,
        val staffworkingDetails: List<StaffworkingDetail>
    ):Serializable {
        data class StaffAvilableTime(
            val availableFrom: String,
            val availableTimeId: Int,
            val availableTo: String
        ):Serializable

        data class StaffworkingDetail(
            val bookFor: String,
            val fromDate: String,
            val fromTime: String,
            val rating: Any,
            val residentdetais: Residentdetais? = null,
            val review: Any,
            val staffBookingId: Int,
            val toDate: String,
            val toTime: String
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