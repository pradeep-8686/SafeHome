package com.example.safehome.model

data class MyDuesMaintenanceDetails(
    /* var dueType: String = "",
     var invoicePeriod: String = "",
     var dueDate: String = "",
     var totalInvoiceAmount: Int = 0,
     var imageResource: Int = 0,
     var status: String = "",
     var paymentMode: String = "",*/

    var transactionId: String = "",
    var catageroyId: String = "",
    var catageroyName: String = "",
    var maintenanceType: String = "",
    var invoiceNumber: String = "",
    var invoiceFromDate: String = "",
    var invoiceToDate: String ="",
    var invoiceDate: String = "",
    var invoiceDueDate: String = "",
    var totalAmount: String = "",
    var paidAmount: String = "",
    var dueAmount: String = "",
    var invoiceAmount: Double ?= 0.00,
    var cgstPercentage: String = "",
    var sgstPercentage: String = "",
    var cgst: String = "",
    var sgst: String = "",
    var paymentStatus: String = "",
    var paymentStatusId: String = "",
    var invoicePath: String = ""

)
