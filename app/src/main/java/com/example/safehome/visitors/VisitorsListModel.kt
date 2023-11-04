package com.example.safehome.visitors

data class VisitorsListModel(
    val entryTitle: String,
    val entryType: String,
    val entryCabNum: String,
    val entryDate: String,
    val allowedBy: String,
    val inTime: String,
    val allowFor: String,
    val entryPersonName: String,
    val visitorImage: Int
)
