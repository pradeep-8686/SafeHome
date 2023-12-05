package com.example.safehome.alert

data class EmergencyTypeModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val emergencyTypeId: Int,
        val emergencyTypeName: String,
        val isActive: Boolean
    )
}