package com.example.safehome.model

data class ServiceTypesList(
    val `data`: List<ServicesListItem>,
    val message: String,
    val statusCode: Int,

){
    data class ServicesListItem(
        val serviceTypeId: Int,
        val serviceTypeName: String,
        val staffCount: Int
    )
}
