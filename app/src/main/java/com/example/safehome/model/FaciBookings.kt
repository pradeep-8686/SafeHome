package com.example.safehome.model

import java.io.Serializable

data class FaciBookings(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) : Serializable{
    data class Data(
        val facililities: List<Facilility>,
        val totalRecords: Int
    ): Serializable {
        data class Facilility(
            val approvalStatusId: Int,
            val approvalStatusName: String,
            val block: String,
            val blockId: Int,
            val bookFacilityId: Int,
            val cgst: Double,
            val cgstPercentage: Double,
            val cgstpercentageForOutSidersChargeByDay: Double,
            val cgstpercentageForOutSidersChargeByHour: Double,
            val cgstpercentageForResidentsChargeByDay: Double,
            val cgstpercentageForResidentsChargeByHour: Double,
            val charge: Double,
            val chargeFor: String,
            val chargeable: String,
            val comments: String,
            val contactDetails: Any,
            val createdBy: Int,
            val createdByName: String,
            val createdByRole: String,
            val createdOn: String,
            var dateOfBooking: String,
            var endDate: String,
            val endTime: String,
            val facilityId: Int,
            val flatId: Int,
            val flatNo: String,
            val fullName: String,
            val generateReceipt: Any,
            val modifiedBy: Any,
            val modifiedOn: Any,
            val name: String,
            val noOfDays: Int,
            val noOfHours: Int,
            val outSidersChargeByDay: Double,
            val outSidersChargeByHour: Double,
            val paymentStatusId: Int,
            val paymentStatusName: String,
            val purpose: String,
            val residentId: Int,
            val residentsChargeByDay: Double,
            val residentsChargeByHour: Double,
            val sgst: Double,
            val sgstPercentage: Double,
            val sgstpercentageForOutSidersChargeByDay: Double,
            val sgstpercentageForOutSidersChargeByHour: Double,
            val sgstpercentageForResidentsChargeByDay: Double,
            val sgstpercentageForResidentsChargeByHour: Double,
            var startDate: String,
            val startTime: String,
            val statusId: Int,
            val statusName: String,
            val totalAmount: Double,
            val transactionID: Any,
            val transactionStatus: Any
        ): Serializable
    }
}