package com.example.safehome.model

data class EmergencyContactCategory(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val contactTypeId: Int,
        val contactTypeName: String,
        val createdBy: Any,
        val createdOn: Any,
        val isActive: Boolean,
        val modifiedBy: Any,
        val modifiedOn: Any
    )
}