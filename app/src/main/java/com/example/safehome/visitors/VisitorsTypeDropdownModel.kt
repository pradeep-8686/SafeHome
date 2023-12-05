package com.example.safehome.visitors

data class VisitorsTypeDropdownModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val isActive: Boolean,
        val visitorTypeId: Int,
        val visitorTypeName: String
    )
}