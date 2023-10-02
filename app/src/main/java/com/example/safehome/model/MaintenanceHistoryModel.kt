package com.example.safehome.model

data class MaintenanceHistoryModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val catageroyId: Int,
        val catageroyName: String,
        val cgst: Any,
        val cgstPercentage: Double,
        val dueAmount: Double,
        val invoiceAmount: Double,
        val invoiceDate: String,
        val invoiceDueDate: String,
        val invoiceFromDate: String,
        val invoiceNumber: Int,
        val invoicePath: Any,
        val invoiceToDate: String,
        val maintenanceType: String,
        val paidAmount: Double,
        val paymentStatus: String,
        val paymentStatusId: Int,
        val sgst: Any,
        val sgstPercentage: Double,
        val totalAmount: Any,
        val transactionId: Int
    )
}