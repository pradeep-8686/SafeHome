package com.example.safehome.model

data class DeleteFacilityModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val approvalStatusId: Int,
        val bookFacilityId: Int,
        val cgst: Double,
        val cgstPercentage: Double,
        val charge: Double,
        val chargeFor: String,
        val comments: String,
        val contactDetails: Any,
        val createdBy: Int,
        val createdOn: String,
        val dateOfBooking: Any,
        val endDate: String,
        val endTime: String,
        val facility: Any,
        val facilityId: Int,
        val fullName: String,
        val generateReceipt: Any,
        val isDeleted: Boolean,
        val modifiedBy: Int,
        val modifiedOn: String,
        val noOfDays: Int,
        val noOfHours: Int,
        val paidDate: Any,
        val payType: Any,
        val paymentMode: Any,
        val paymentReceipt: Any,
        val paymentStatusId: Int,
        val purpose: String,
        val residentId: Int,
        val sgst: Double,
        val sgstPercentage: Double,
        val startDate: String,
        val startTime: String,
        val statusId: Int,
        val totalAmount: Double,
        val transactionComments: Any,
        val transactionId: Any,
        val transactionStatus: Any
    )
}