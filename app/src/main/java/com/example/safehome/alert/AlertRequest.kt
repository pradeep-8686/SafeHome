package com.example.safehome.alert

data class AlertRequest(
    val AlertId: Int? = null,
    val Comments: String,
    val Description: String,
    val EmergencyTypeId: Int,
    val NotifyToIds: List<Int>
)