package com.example.safehome.model

data class MyDues(
    var dueType: String = "",
    var invoiceNumber: String = "",
    var invoicePeriod: String = "",
    var dueDate: String = "",
    var totalInvoiceAmount: Int = 0,
    var imageResource: Int = 0,
    var status: String = "",
    var paymentMode: String = ""
)
