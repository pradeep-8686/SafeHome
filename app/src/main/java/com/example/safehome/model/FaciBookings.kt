package com.example.safehome.model

data class FaciBookings(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val facililities: List<Facilility>,
        val totalRecords: Int
    ) {
        data class Facilility(
            val approvalStatusId: Int,
            val approvalStatusName: String,
            val block: String,
            val blockId: Int,
            val bookFacilityId: Int,
            val cgst: Double,
            val cgstPercentage: Double,
            val chargeFor: String,
            val chargeable: String,
            val chargedAmount: Double,
            val comments: String,
            val contactDetails: String,
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
            val generateReceipt: String,
            val modifiedBy: Int,
            val modifiedOn: String,
            val name: String,
            val noOfDays: Int,
            val noOfHours: Int,
            val paymentStatusId: Int,
            val paymentStatusName: String,
            val purpose: String,
            val residentId: Int,
            val sgst: Double,
            val sgstPercentage: Double,
            var startDate: String,
            val startTime: String,
            val statusId: Int,
            val statusName: String,
            val totalAmount: Double,
            val transactionID: String,
            val transactionStatus: String
        )
    }
}
